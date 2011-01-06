package br.usp.language.automata;

import java.util.ArrayList;
import java.util.List;

public class StateMachine {

    /* Attributes */

    private String name;

    private List<State> states;

    private State currentState;

    private State initialState;

    /* Constructor */

    /**
     * Creates a machine with an initial state called "Initial"
     */
    public StateMachine(String name) {
        this.name = name;
        this.states = new ArrayList<State>();
        this.initialState = new State("Initial");
        this.states.add(initialState);
        this.currentState = this.initialState;
    }

    /**
     * Creates a machine with given initial State
     * 
     * @param initialState
     * @throws Exception if initialState is null
     */
    public StateMachine(String name, State initialState) throws Exception {
        this.name = name;
        this.states = new ArrayList<State>();
        if (initialState != null) {
            this.initialState = initialState;
            this.states.add(initialState);
            this.currentState = this.initialState;
        } else
            throw new Exception("Null initial state");
    }

    /* Methods */

    public String getName() {
        return this.name;
    }
    
    public List<State> getAllStates() {
        return this.states;
    }

    public State getCurrentState() {
        return currentState;
    }

    public State getInitialState() {
        return initialState;
    }

    public boolean isAtAcceptState() {
        return this.currentState.isAcceptState();
    }

    public boolean isAtErrorState() {
        return this.currentState.isErrorState();
    }

    /**
     * Creates and inserts a new state in the automaton.
     * 
     * @param name Name of the state to be created
     * @return created state
     */
    public State createState(String name) {
        // Check if it already exists
        for (State st : this.states) {
            if (st.getName().equals(name)) {
                return st;
            }
        }
        // If not, create it
        State state = new State(name);
        this.states.add(state);
        return state;
    }

    /**
     * Check if an given input will lead to a state change. Note that this depends on the current state.
     * 
     * @param input
     * @return
     */
    public boolean isValidInput(String input) {
        return this.currentState.containsTransitionForInput(input);
    }

    /**
     * Check if the current state has an alternate transition (can be epslion)
     * 
     * @return true, if it has;
     */
    public boolean hasAlternateTransitionNow() {
        return this.currentState.hasAlternateTransition();
    }

    /**
     * Check if the current state has an submachine call
     * 
     * @return true, if it has;
     */
    public boolean hasSubMachineCallNow() {
        if (this.currentState.getMachineCalls().size() > 0) {
            return true;
        } else
            return false;
    }
    
    public boolean hasEpsilonTransitionNow() {
        return this.currentState.hasEpsilonTransition();
    }

    /**
     * Apply the input in the machine, which makes a transition activate
     * 
     * @param input
     * @return true if the input was consumed.
     */
    public boolean input(String input) {
        // 1) Entrada normal
        for (Transition trans : this.currentState.getTransitions()) {
            if (trans.getConditions().contains(input)) {
                this.currentState = trans.execute();
                return true;
            }
        }
        // Input not found in conditions:
        // 2) Epsilon
        if (this.currentState.hasEpsilonTransition()) {
            // Tries an epsilon transition and calls input again
            this.currentState = this.currentState.getEpsilonTransition().execute();
            return this.input(input);
        }
        // 3) If none of the conditions is met, execute alternate Transition
        else if (this.currentState.hasAlternateTransition()) {
            this.currentState = this.currentState.getAlternateTransition().execute();
            return true;
        }
        return false;
    }

    /**
     * Makes an epsilon transition if there is one.
     */
    public boolean inputEpsilon() {
        if (this.currentState.hasEpsilonTransition()) {
            // Tries an epsilon transition and calls input again
            this.currentState = this.currentState.getEpsilonTransition().execute();
        }
        return this.currentState.isAcceptState();
    }

    /**
     * Executa a transição provocada pela chamada de submaquina propriamente dita.
     * Quando retorna da submaquina chamada, retorna-se no mesmo estado que partiu.
     * Então essa transição é executada, dado o input que seria o nome da submaquina.
     * 
     * @param input Nome da submaquina que terminou a execução
     * @return true, if it is on an accept state after transition
     */
    public boolean machineTransition(String name) {
        for(MachineCall call : this.currentState.getMachineCalls()) {
            if (call.getCalledMachineName().equals(name)) {
                this.currentState = call.execute();
                break;
            }
        }
        return this.currentState.isAcceptState();
    }

    /**
     * Apply the inputs in the machine, which makes transitions activate
     * 
     * @param input list of inputs
     * @return true if the machine is at an accept state after the input
     */
    public boolean execute(String[] inputs) {
        // Consumes all inputs, if possible
        for (String input : inputs) {
            this.input(input);
        }
        // If the machine is not on a final state, checks if there are still epsilon transitions to make
        this.finish();
        // Return
        return this.currentState.isAcceptState();
    }
    
    /**
     * If the machine is not on a final state, checks if there are still epsilon transitions to make
     */
    public void finish() {
        while (this.hasEpsilonTransitionNow()) {            
            this.inputEpsilon();
            if (this.isAtAcceptState()) {
                break;
            }
        }
    }

    /**
     * Restarts the machine (Current state goes back to the initial one).
     */
    public void restart() {
        this.currentState = this.initialState;
    }

    /**
     * Forces the state machine to go to a specied state.
     * 
     * @param name the name of the state
     */
    public void goToState(String name) {
        for (State state : this.states) {
            // Searches for the right state
            if (state.getName().equals(name)) {
                this.currentState = state; // When found, stop;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "StateMachine:" + this.name + "; cs:" + this.currentState.getName();
    }
    
    
}
