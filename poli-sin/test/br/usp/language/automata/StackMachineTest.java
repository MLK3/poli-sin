package br.usp.language.automata;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StackMachineTest {
    
    StackMachine sm;
    StateMachine fsm1;
    State fsm1s1;
    State fsm1s2;
    State fsm2s1;
    State fsm2s2;
    StateMachine fsm2;

    @Before
    public void setUp() throws Exception {
        // Maquina de teste:
        // A -> B + 0
        // B -> 1
        fsm1 = new StateMachine("A");
        fsm1s1 = fsm1.createState("A: A->B");
        fsm1s2 = fsm1.createState("A: B->0");
        fsm1s2.setAcceptState();
        fsm2 = new StateMachine("B");
        fsm2s1 = fsm2.createState("B: B->1");
        fsm2s2 = fsm2.createState("B: 1->F");
        fsm2s2.setAcceptState();
        
        fsm1.getInitialState().createMachineCallTo(fsm1s1, "B", null, null);
        fsm1s1.createTransitionTo(fsm1s2, "0", null);
        fsm2.getInitialState().createTransitionTo(fsm2s1, "1", null);
        fsm2s1.createEpsilonTransitionTo(fsm2s2, null);
        
        sm = new StackMachine(new StateMachine[]{fsm1, fsm2});
    }

    @Test
    public void testInput() {
        sm.restart();
        sm.input("1");
        assertEquals(fsm2s1, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(1, sm.getStackMachines().size());
        sm.input("0");
        assertEquals(fsm1s2, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(0, sm.getStackMachines().size());
    }
    
    @Test
    public void testInputError() {
        sm.restart();
        assertFalse(sm.execute(new String[]{"0","0"}));
    }
}
