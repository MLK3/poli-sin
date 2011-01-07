package br.usp.language.syntax;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import br.usp.language.morph.MorphologicAnalyser;
import br.usp.language.morph.PortugueseAnalyser;
import br.usp.language.syntax.grammar.ContextFreeGrammar;
import br.usp.language.syntax.grammar.GrammarLoader;

public class SyntacticIntegrationTestLuft2 {

    MorphologicAnalyser amor;
    SyntacticAnalyser sa;

    @Before
    public void setUp() throws Exception {
        ContextFreeGrammar grammar = GrammarLoader.load("resources/gramatica_luft2.txt");
        amor = new PortugueseAnalyser("resources/port", "resources/port_amor_config.list");
        sa = new SyntacticAnalyser(amor, grammar);
    }

    // O rato roeu o casaco.
    // O jovem envelheceu.
    // O professor está longe.

    @Test
    public void testAnalyse1() throws IOException {
        amor.setInput(new StringReader("Nenhum aluno conhece o livro"));
        sa.analyse2();
        assertEquals(
                "[F [PS [OA [SS [PrAdj [pind Nenhum]][NOME [nc aluno]]][SV [v conhece][SS [PrAdj [art o]][NOME [nc livro]]]]]]]",
                sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse2() throws IOException {
        amor.setInput(new StringReader("Eu quero chocolate"));
        sa.analyse2();
        assertEquals("[F [PS [OA [SS [ppes Eu]][SV [v quero][SS [NOME [nc chocolate]]]]]]]", sa.getTree()
                .polishNotation());
    }

    @Test
    public void testAnalyse3() throws IOException {
        amor.setInput(new StringReader("Eu quero um chocolate rapidamente"));
        sa.analyse2();
        assertEquals(
                "[F [PS [OA [SS [ppes Eu]][SV [v quero][SS [PrAdj [art um]][NOME [nc chocolate]]]][SAdv [adv rapidamente]]]]]",
                sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse4() throws IOException {
        amor.setInput(new StringReader("A rata roeu a roupa de Roma"));
        sa.analyse2();
        String expected = "[F [PS [OA [SS [PrAdj [art A]][NOME [nc rata]]][SV [v roeu][SS [PrAdj [art a]][NOME [nc roupa]]][SP [prep de][SS [NOME [np Roma]]]]]]]]";
        assertEquals(expected, sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse5() throws IOException {
        amor.setInput(new StringReader("Eu sou legal"));
        sa.analyse2();
        String expected = "[F [PS [OA [SS [ppes Eu]][SN [vlig sou][SAdj [adj legal]]]]]]";
        assertEquals(expected, sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse6() throws IOException {
        amor.setInput(new StringReader("Ele estava muito doente"));
        sa.analyse2();
        String expected = "[F [PS [OA [SS [ppes Ele]][SN [vlig estava][SAdj [adv muito][a_nc doente]]]]]]";
        assertEquals(expected, sa.getTree().polishNotation());
    }
    
    @Test
    public void testFalse() throws IOException {
        amor.setInput(new StringReader("O rato esperto é"));
        assertFalse(sa.analyse2());
    }

    @Test
    public void tester() throws IOException {
        amor.setInput(new StringReader("O rato esperto é"));
        System.out.println(sa.analyse2());
        System.out.println(sa.getTree().polishNotation());
    }
}
