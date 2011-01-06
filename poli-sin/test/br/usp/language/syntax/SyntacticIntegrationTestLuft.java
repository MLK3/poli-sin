package br.usp.language.syntax;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import br.usp.language.morph.AnalisadorMorfologico;
import br.usp.language.morph.MorphologicAnalyser;
import br.usp.language.syntax.grammar.ContextFreeGrammar;
import br.usp.language.syntax.grammar.GrammarLoader;

public class SyntacticIntegrationTestLuft {

    MorphologicAnalyser amor;
    SyntacticAnalyser sa;

    @Before
    public void setUp() throws Exception {
        ContextFreeGrammar grammar = GrammarLoader.load("resources/gramatica_luft.txt");
        amor = new AnalisadorMorfologico("./resources/port");
        sa = new SyntacticAnalyser(amor, grammar);
    }

    // O rato roeu o casaco.
    // O jovem envelheceu.
    // O professor está longe.

    @Test
    public void testAnalyse1() throws IOException {
        amor.setInput(new StringReader("Nenhum aluno conhece o livro"));
        sa.analyse();
        assertEquals(
                "[F [PS [OA [SS [PrAdj [pind Nenhum]][NOME [nc aluno]]][SV [v conhece][SS [PrAdj [art o]][NOME [nc livro]]]]]]]",
                sa.getTree().polishNotation());
        //System.out.println(sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse2() throws IOException {
        amor.setInput(new StringReader("Eu quero chocolate"));
        sa.analyse();
        assertEquals("[F [PS [OA [SS [ppes Eu]][SV [v quero][SS [NOME [nc chocolate]]]]]]]", sa.getTree()
                .polishNotation());
        //System.out.println(sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse3() throws IOException {
        amor.setInput(new StringReader("Eu quero um chocolate rapidamente"));
        sa.analyse();
        assertEquals(
                "[F [PS [OA [SS [ppes Eu]][SV [v quero][SS [PrAdj [art um]][NOME [nc chocolate]]]][SAdv [adv rapidamente]]]]]",
                sa.getTree().polishNotation());
        //System.out.println(sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse4() throws IOException {
        amor.setInput(new StringReader("O rato roeu a roupa de o rei"));
        sa.analyse2();
        assertEquals(
                "[F [PS [OA [SS [PrAdj [art O]][NOME [a_nc rato]]][SV [v roeu][SS [PrAdj [art a]][NOME [nc roupa]]][SP [prep de][SS [PrAdj [art o]][NOME [nc rei]]]]]]]]",
                sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse5() throws IOException {
        amor.setInput(new StringReader("A bola caiu"));
        sa.analyse2();
        assertEquals("[F [PS [OA [SS [PrAdj [art A]][NOME [nc bola]]][SV [v caiu]]]]]", sa.getTree().polishNotation());
    }

}
