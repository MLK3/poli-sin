package br.usp.language.automata;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Testa a escolha entre diversas chamadas de submaquina.
 * @author mlk
 *
 */
public class StackMachineTest2 {
    
    StackMachine sm;
    StateMachine fsmA, fsmB, fsmC, fsmD;
    State fsmAs1, fsmAs2, fsmAs3, fsmAs4;
    State fsmBs1, fsmBs2;
    State fsmCs1, fsmCs2, fsmCs3;
    State fsmDs1, fsmDs2; 

    @Before
    public void setUp() throws Exception {
        // Maquina de teste:
        // Demonstra as 3 opções de A: 2 chamadas de submaquina e um terminal
        // A -> B + 0
        // A -> C
        // A -> D
        // A -> n
        // B -> 1
        // C -> 3
        // C -> D
        // D -> 4
        fsmA = new StateMachine("A");
        fsmAs1 = fsmA.createState("A: A->B");        
        fsmAs2 = fsmA.createState("A: A->C");
        fsmAs3 = fsmA.createState("A: A->n");
        fsmAs4 = fsmA.createState("A: B->0");
        fsmAs2.setAcceptState();
        fsmAs3.setAcceptState();
        fsmAs4.setAcceptState();
        fsmB = new StateMachine("B");
        fsmBs1 = fsmB.createState("B: B->1");
        fsmBs2 = fsmB.createState("B: 1->F");
        fsmBs2.setAcceptState();
        fsmC = new StateMachine("C");
        fsmCs1 = fsmC.createState("C: C->3");        
        fsmCs2 = fsmC.createState("C: 3->F");
        fsmCs2.setAcceptState();
        fsmCs3 = fsmC.createState("C: C->D");
        fsmD = new StateMachine("D");
        fsmDs1 = fsmD.createState("D: D->4");
        fsmDs2 = fsmD.createState("D: 4->F");
        fsmDs2.setAcceptState();
        
        fsmA.getInitialState().createMachineCallTo(fsmAs1, "B", null, null);
        fsmA.getInitialState().createMachineCallTo(fsmAs2, "C", null, null);
        fsmA.getInitialState().createTransitionTo(fsmAs3, "n", null);
        fsmAs1.createTransitionTo(fsmAs4, "0", null);
        fsmB.getInitialState().createTransitionTo(fsmBs1, "1", null);
        fsmBs1.createEpsilonTransitionTo(fsmBs2, null);
        fsmC.getInitialState().createTransitionTo(fsmCs1, "3", null);
        fsmC.getInitialState().createMachineCallTo(fsmCs3, "D", null, null);
        fsmCs1.createEpsilonTransitionTo(fsmCs2, null);
        fsmCs3.createEpsilonTransitionTo(fsmCs2, null);
        fsmD.getInitialState().createTransitionTo(fsmDs1, "4", null);
        fsmDs1.createEpsilonTransitionTo(fsmDs2, null);
        
        sm = new StackMachine(new StateMachine[]{fsmA, fsmB, fsmC, fsmD});
    }
    
    // Teste 1: Chama B. Entrada: 10
    @Test
    public void testInputCallB() {
        sm.restart();
        sm.input("1");
        assertEquals(fsmBs1, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(1, sm.getStackMachines().size());
        sm.input("0");
        assertEquals(fsmAs4, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(0, sm.getStackMachines().size());
    }
    
    // Teste 2: Chama C. Entrada: 3
    @Test
    public void testInputCallC() {
        sm.restart();
        sm.input("3");
        assertEquals(fsmCs1, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(1, sm.getStackMachines().size());
    }
    
    // Teste 3: Chama C. Entrada: 4
    // TODO Ainda nao implementado
    @Test
    public void testInputCallCD() {
        sm.restart();
        sm.input("4");
        assertEquals(fsmDs1, sm.getCurrentStateMachine().getCurrentState());
        assertEquals(2, sm.getStackMachines().size());
    }
}
