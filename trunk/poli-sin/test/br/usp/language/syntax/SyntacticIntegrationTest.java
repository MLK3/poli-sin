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

public class SyntacticIntegrationTest {

    MorphologicAnalyser amor;
    SyntacticAnalyser sa;

    @Before
    public void setUp() throws Exception {
        ContextFreeGrammar grammar = GrammarLoader.load("resources/gramatica_simples.txt");
        amor = new AnalisadorMorfologico("./resources/port");
        sa = new SyntacticAnalyser(amor, grammar);
    }

    // O rato roeu o casaco.
    // O jovem envelheceu.
    // O professor está longe.

    @Test
    public void testAnalyse1() throws IOException {
        amor.setInput(new StringReader("O jovem envelheceu"));
        sa.analyse();
        assertEquals("[F [PS [OA [SS [art O][NOME [a_nc jovem]]][SV [v envelheceu]]]]]", sa.getTree().polishNotation());
        //System.out.println(sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse2() throws IOException {
        amor.setInput(new StringReader("O rato roeu o casaco"));
        sa.analyse();
        //System.out.println(sa.getTree().polishNotation());
        assertEquals("[F [PS [OA [SS [art O][NOME [a_nc rato]]][SV [v roeu][OBJ [SS [art o][NOME [nc casaco]]]]]]]]",
                sa.getTree().polishNotation());
    }

    @Test
    public void testAnalyse3() throws IOException {
        amor.setInput(new StringReader("O professor esteve longe"));
        sa.analyse();
        //System.out.println(sa.getTree().polishNotation());
        assertEquals("[F [PS [OA [SS [art O][NOME [nc professor]]][SV [v esteve][OBJ [ADJ [adj longe]]]]]]]", 
                sa.getTree().polishNotation());
    }
}
