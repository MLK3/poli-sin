package br.usp.language.automata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StackMachine {

    private String initialStateMachineName;
    private StateMachine currentStateMachine;
    /** Map with submachines. Key: name */
    private Map<String, StateMachine> subMachines;

    // The machines stack is not enough because the same machine can be stacked more than once
    // Thus, we need to know which is the state, because we only use 1 instance of each machine
    private Stack<StateMachine> stackMachines;
    private Stack<String> stackReturnStates;

    /**
     * Creates a Stack Machine
     * 
     * @param machines submachines (automata) of the stack machine; The first one is the initial.
     */
    public StackMachine(StateMachine[] machines) {
        if (machines.length > 0) {
            this.initialStateMachineName = machines[0].getName();
            this.currentStateMachine = machines[0];
        }
        this.subMachines = new HashMap<String, StateMachine>(machines.length);
        for (StateMachine stateMachine : machines) {
            this.subMachines.put(stateMachine.getName(), stateMachine);
        }

        this.stackMachines = new Stack<StateMachine>();
        this.stackReturnStates = new Stack<String>();
    }

    /**
     * Creates a Stack Machine
     * 
     * @param machines submachines (automata) of the stack machine; The first one is the initial.
     */
    public StackMachine(List<StateMachine> machines) {
        this(machines.toArray(new StateMachine[0]));
    }

    /**
     * Creates a Stack Machine
     * 
     * @param machines submachines (automata) of the stack machine;
     * @param first The initial machine
     */
    public StackMachine(List<StateMachine> machines, String first) {
        this.initialStateMachineName = first;
        this.subMachines = new HashMap<String, StateMachine>(machines.size());
        for (StateMachine stateMachine : machines) {
            this.subMachines.put(stateMachine.getName(), stateMachine);
        }
        currentStateMachine = this.subMachines.get(first);
        this.stackMachines = new Stack<StateMachine>();
        this.stackReturnStates = new Stack<String>();
    }

    public StateMachine getCurrentStateMachine() {
        return currentStateMachine;
    }

    public Collection<StateMachine> getSubMachines() {
        return subMachines.values();
    }

    public Stack<StateMachine> getStackMachines() {
        return stackMachines;
    }

    /**
     * Consumes one input.
     * Preference order: SubMachineCall > local input > epsilon > alternative
     * 
     * @param input
     * @return true, if the input is consumed.
     */
    public boolean input(String input) {
        boolean consumed = false;
        int max = 10;
        int i = 0;
        
        // Saves current state
        StateMachine initialSM = this.currentStateMachine;
        State initialState = this.currentStateMachine.getCurrentState();

        while (!consumed && i <= max && !this.currentStateMachine.isAtErrorState()) {
            
            // Check if there is an eligible submachine call
            boolean subCalled = checkSubMachineCall(input);
            
            if (!subCalled) { // Necessary to maintain preference order
                // Tries to consume locally
                consumed = this.currentStateMachine.input(input);
                i++;
            }
            
            checkReturnSubMachine();
        }
        
        // If not successful return to initial state
        if (!consumed) {
            this.currentStateMachine = initialSM;
            this.currentStateMachine.goToState(initialState.getName());
        }
        
        return consumed;
    }

    /**
     * Check if there is an eligible submachine call. If there is, call it.
     * @param input
     * @return true if a submachine was called.
     */
    private boolean checkSubMachineCall(String input) {
        // Verifies if there's a submachine call

        MachineCall call = this.chooseSubMachineCall(this.currentStateMachine.getCurrentState(), input);
        
        if (call != null) {
            
            // Stack current machine
            this.stackMachines.push(this.currentStateMachine);
            this.stackReturnStates.push(this.currentStateMachine.getCurrentState().getName());

            // Called machine takes control
            // MachineCall call = this.currentStateMachine.getCurrentState().getMachineCall();
            this.currentStateMachine = this.subMachines.get(call.getCalledMachineName());
            // Deve-se executar a ação relacionada a chamada da submaquina
            call.executeActionBefore();
            this.currentStateMachine.restart();
            // Calls input function again
            //this.input(input);
            return true;
        }
        return false;
    }

    /**
     * Chooses which Machine Call should be called, given the input.
     * 
     * @param state
     * @param input
     * @return
     */
    private MachineCall chooseSubMachineCall(State state, String input) {
        for (MachineCall m : state.getMachineCalls()) {
            // Gets initial state of called Machine
            State s = this.subMachines.get(m.getCalledMachineName()).getInitialState();
            // Verifies inputs for the initial state
            if (s.containsTransitionForInput(input)) {
                return m;
            }
            // Verifies machineCalls of the initial state
            if (s.hasMachineCalls()) {
                // Recusiverly searches for the right call
                MachineCall call = this.chooseSubMachineCall(s, input);
                if (call != null) {
                    return m;
                }
            }
            if (s.hasEpsilonTransition()) {
                MachineCall call = this.chooseSubMachineCall(s.getNextEpsilonState(), input);
                if (s.getNextEpsilonState().isAcceptState() || call != null) {
                    return m;
                }
            }
            if (s.isAcceptState()) {
                return m;
            }
        }
        return null;
    }

    private void checkReturnSubMachine() {
        // If it's in an accept state, pop the stack
        if (this.currentStateMachine.isAtAcceptState() && !this.stackMachines.empty()) {
            String name = this.currentStateMachine.getName();
            this.currentStateMachine = this.stackMachines.pop();
            // Returns to the calling state
            this.currentStateMachine.goToState(this.stackReturnStates.pop());
            this.currentStateMachine.machineTransition(name);
            // this.input(input);
        }
    }

    public boolean execute(String[] inputs) {
        // Consumes all inputs, if possible
        for (String input : inputs) {
            this.input(input);
        }
        this.finish();
        // Return
        return this.currentStateMachine.isAtAcceptState();
    }

    /**
     * If the machine is not on a final state, checks if there are still epsilon transitions to make Or SubMachines
     * calls and returns.
     */
    public void finish() {
        // If the machine is not on a final state, checks if there are still epsilon transitions to make
        while (this.currentStateMachine.hasEpsilonTransitionNow() 
                || this.currentStateMachine.hasSubMachineCallNow() && this.chooseSubMachineCall(this.currentStateMachine.getCurrentState(), null) != null  
                || (!this.stackMachines.empty() && this.currentStateMachine.isAtAcceptState())) {

            this.currentStateMachine.inputEpsilon();
            this.checkReturnSubMachine();
            this.checkSubMachineCall(null);
        }
    }

    /**
     * Returns to initial state
     */
    public void restart() {
        this.currentStateMachine = this.subMachines.get(initialStateMachineName);
        this.currentStateMachine.restart();
    }
}
