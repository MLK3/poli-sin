package br.usp.language.syntax.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class GrammarLoader {
    
    private static final String REGEX = "['*'\\-'-'><='+']+";

    public static ContextFreeGrammar load(String filename) throws IOException {

        File grammarFile = new File(filename);
        if (!grammarFile.canRead())
            throw new IOException("File cannot be read.");
        
        ContextFreeGrammar grammar = new ContextFreeGrammar();

        BufferedReader reader = new BufferedReader(new FileReader(grammarFile));
                
        // Primeiro: adicionar todos os não-terminais
        
        int i = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            if (!line.isEmpty()) {
                //line = line.replaceAll("\\p{Punct}", " ");
                line = line.replaceAll(REGEX, " ");
                NonTerminal nt = new NonTerminal(line.split("\\s+")[0]);                
                grammar.addNonTerminal(nt);
                // Tratamento especial para o primeiro, que é o simbolo inicial da gramatica
                if (i == 0)
                    grammar.setStartSymbol(nt);
            }
            i++;
        }

        reader.close();

        // Agora adiciona as regras
        reader = new BufferedReader(new FileReader(grammarFile));
        int x = 1;
        while (reader.ready()) {
            String line = reader.readLine();
            line = line.replaceAll(REGEX, " "); // Substitui pontuações por espaço            
            String[] symbols = line.split("\\s+"); // Divide em tokens separados por espaço
            
            x++;
            
            // Cria nova regra
            ProductionRule rule = new ProductionRule(new NonTerminal(symbols[0]));
            
            // Passa de novo pela gramática, agora acrescentando os produtos da regra
            for (String symbol : Arrays.copyOfRange(symbols, 1, symbols.length)) {
                if (grammar.hasNonTerminal(symbol)) { // Verifica se é nao-Terminal
                    rule.add(grammar.getNonTerminal(symbol));
                } else if (symbol.equalsIgnoreCase(ContextFreeGrammar.EPSILON)) { // Transicao vazia
                    rule.add(new Epsilon());
                } else { // Terminal
                    Terminal terminal;
                    if (!grammar.hasTerminal(symbol)) {
                        terminal = new Terminal(symbol);
                        grammar.addTerminal(terminal);
                    } else {
                        terminal = grammar.getTerminal(symbol);
                    }
                    rule.add(terminal);
                }
            }
            // Adiciona regra
            grammar.addRule(rule);
        }

        return grammar;
    }

}
