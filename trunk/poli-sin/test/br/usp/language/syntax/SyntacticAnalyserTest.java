package br.usp.language.syntax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import br.usp.language.automata.StateMachine;
import br.usp.language.morph.MorphologicAnalyser;
import br.usp.language.morph.TokenMorph;
import br.usp.language.syntax.grammar.ContextFreeGrammar;
import br.usp.language.syntax.grammar.NonTerminal;
import br.usp.language.syntax.grammar.ProductionRule;
import br.usp.language.syntax.grammar.Terminal;
import br.usp.language.syntax.tree.SyntaxTree;

public class SyntacticAnalyserTest {
    
    SyntacticAnalyser sa;
    
    MorphologicAnalyser mockMa;
    
    ContextFreeGrammar grammar;
    
    @Before
    public void setUp() throws Exception {
        mockMa = EasyMock.createMock(MorphologicAnalyser.class);
        // A -> s + B
        // B -> s
        // B -> v
        grammar = new ContextFreeGrammar();
        NonTerminal a = new NonTerminal("A");
        NonTerminal b = new NonTerminal("B");
        Terminal subst = new Terminal("s");
        Terminal verb = new Terminal("v");
        grammar.addNonTerminal(a);
        grammar.addNonTerminal(b);
        grammar.addTerminal(subst);
        grammar.addTerminal(verb);
        ProductionRule rule1 = new ProductionRule(a);
        rule1.add(subst);
        rule1.add(b);
        ProductionRule rule2 = new ProductionRule(b);
        rule2.add(subst);
        ProductionRule rule3 = new ProductionRule(b);
        rule3.add(verb);
        grammar.addRule(rule1);
        grammar.addRule(rule2);
        grammar.addRule(rule3);
        grammar.setStartSymbol(a);
        
        sa = new SyntacticAnalyser(mockMa, grammar);
    }
    
    @Test
    public void testCreateSyntaxStateMachines() {
        Collection<StateMachine> col = sa.getStackMachine().getSubMachines();
        assertEquals(2, col.size());
        StateMachine[] list = col.toArray(new StateMachine[0]);
        StateMachine m1 = list[0];
        StateMachine m2 = list[1];
        if (m2.getName().compareTo(m1.getName()) < 0) {
            m1 = m2;
            m2 = list[0];
        }
        // m1 = maquina A e m2 = maquina B
        assertEquals("A", m1.getName());
        assertEquals("B", m2.getName());
        
        assertEquals(5, m1.getAllStates().size());
        m1.goToState("State1_s");
        assertTrue(m1.hasSubMachineCallNow());
        assertEquals(5, m2.getAllStates().size());
    }

    @Test
    public void testAnalyse1() {
        // Criando Tokens da entrada
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("cat","s");
        TokenMorph token1 = new TokenMorph("Eu", "", map1,false);
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("cat","v");
        TokenMorph token2 = new TokenMorph("fui", "", map2,false);
        
        // Adicionando comportamento Mock
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token1);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token2);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(false);
        EasyMock.replay(mockMa);
        
        assertTrue(sa.analyse());
        
        EasyMock.verify(mockMa);
    }
    
    /**
     * Teste de Falha
     */
    @Test
    public void testAnalyse2() {
        // Criando Tokens da entrada
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("cat","v");
        TokenMorph token1 = new TokenMorph("fui", "", map1,false);
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("cat","s");
        TokenMorph token2 = new TokenMorph("eu","",map2,false);
        
        // Adicionando comportamento Mock
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token1);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token2);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(false);
        EasyMock.replay(mockMa);
        
        assertFalse(sa.analyse());
        
        EasyMock.verify(mockMa);
    }
    
    @Test
    public void testTree() {
     // Criando Tokens da entrada
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("cat","s");
        TokenMorph token1 = new TokenMorph("Eu", "",map1,false);
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("cat","v");
        TokenMorph token2 = new TokenMorph("fui","",map2,false);
        
        // Adicionando comportamento Mock
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token1);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(true);
        EasyMock.expect(mockMa.getNextToken()).andReturn(token2);
        EasyMock.expect(mockMa.hasMoreTokens()).andReturn(false);
        EasyMock.replay(mockMa);
        
        sa.analyse();
        
        SyntaxTree tree = sa.getTree();
        
        assertEquals("[A [s Eu][B [v fui]]]", tree.polishNotation());
        
        EasyMock.verify(mockMa);
    }
}
