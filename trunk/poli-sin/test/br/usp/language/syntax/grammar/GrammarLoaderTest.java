package br.usp.language.syntax.grammar;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;

import br.usp.language.syntax.grammar.ContextFreeGrammar;
import br.usp.language.syntax.grammar.GrammarLoader;

public class GrammarLoaderTest {

    @Test
    public void testLoad() throws IOException {
        ContextFreeGrammar grammar = GrammarLoader.load("resources/gramatica_teste.txt");

        assertEquals(5, grammar.getNonTerminals().size());
        assertEquals(6, grammar.getTerminals().size());
        assertEquals(9, grammar.getRules().size());
    }
    
    @Test 
    public void testRegex() {
        String line = "SS -> *a_nc* + NOME";
        line = line.replaceAll("['*'\\-'-'><='+']+", " ");
        String[] lines = line.split("\\s+");
        assertEquals("SS", lines[0]);
        assertEquals("a_nc", lines[1]);
        assertEquals("NOME", lines[2]);
    }
}
