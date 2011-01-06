package br.usp.language.automata;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StateMachineTest {
    
    StateMachine fsm;
    State stateOne;
    State stateZero;
    State stateOther;
    State initialState;
    State initialState2;

    @Before
    public void setUp() throws Exception {
        // Maquina de teste:
        // 5 estados (Initial, Initial2, One, Zero, Other) - alfabeto: inteiros
        // Initial vai pra Initial2 com epsilon
        // Se recebe "1" vai para "One". Se recebe "0", vai para "Zero".
        // Se recebe outra coisa, vai para "Other";
        
        fsm = new StateMachine("Teste");
        stateOne = fsm.createState("Par");
        stateZero = fsm.createState("Impar");
        stateOther = fsm.createState("Lixo");
        initialState2 = fsm.createState("Initial2");
        initialState = fsm.getInitialState();
        
        initialState.createEpsilonTransitionTo(initialState2, null);
        initialState2.createTransitionTo(stateOne, "1", null);
        initialState2.createTransitionTo(stateZero, "0", null);
        initialState2.setAlternateTransition(stateOther, null);
        stateOne.createTransitionTo(stateOne, "1", null);
        stateOne.createTransitionTo(stateZero, "0", null);
        stateOne.setAlternateTransition(stateOther, null);
        stateZero.createTransitionTo(stateOne, "1", null);
        stateZero.createTransitionTo(stateZero, "0", null);
        stateZero.setAlternateTransition(stateOther, null);
        stateOther.setAlternateTransition(stateOther, null);
                
    }
    
    @Test
    public void inputTest() {
        assertEquals(initialState, fsm.getCurrentState());
        fsm.input("1");
        assertEquals(stateOne, fsm.getCurrentState());
        fsm.input("0");
        assertEquals(stateZero, fsm.getCurrentState());
        fsm.input("0");
        assertEquals(stateZero, fsm.getCurrentState());
        fsm.input("1");
        assertEquals(stateOne, fsm.getCurrentState());
        fsm.input("2");
        assertEquals(stateOther, fsm.getCurrentState());
    }
    
    @Test
    public void restartTest() {
        fsm.input("1");
        fsm.input("1");
        fsm.input("0");
        fsm.restart();
        assertEquals(initialState, fsm.getCurrentState());
    }
    
    @Test
    public void isAtAcceptStateTest() {
        stateOne.setAcceptState();
        assertFalse(fsm.isAtAcceptState());
        fsm.input("1");
        assertTrue(fsm.isAtAcceptState());
    }
    

}
