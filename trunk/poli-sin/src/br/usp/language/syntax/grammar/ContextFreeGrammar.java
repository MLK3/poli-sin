package br.usp.language.syntax.grammar;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContextFreeGrammar {
    
    public static final String EPSILON = "epsilon";
    
    private Map<String, Terminal> terminals;
    private Map<String, NonTerminal> nonTerminals;
    private Set<ProductionRule> rules;
    private NonTerminal startSymbol;
    
    public ContextFreeGrammar() {
        terminals = new HashMap<String, Terminal>();
        nonTerminals = new HashMap<String, NonTerminal>();
        rules = new HashSet<ProductionRule>();        
    }
    
    /**
     * @return the startSymbol
     */
    public NonTerminal getStartSymbol() {
        return startSymbol;
    }
    /**
     * @param startSymbol the startSymbol to set
     */
    public void setStartSymbol(NonTerminal startSymbol) {
        this.startSymbol = startSymbol;
    }
    /**
     * @return the terminals
     */
    public Collection<Terminal> getTerminals() {
        return  terminals.values();
    }
    /**
     * Add one terminal to the Set of terminals of the grammar
     * @param t
     */
    public void addTerminal(Terminal t) {
        terminals.put(t.getName(), t);        
    }
    /**
     * @return the nonTerminals
     */
    public Collection<NonTerminal> getNonTerminals() {
        return nonTerminals.values();
    }
    /**
     * Add one non-terminal to the Set of non-terminals of the grammar
     * @param nt
     */
    public void addNonTerminal(NonTerminal nt) {
        nonTerminals.put(nt.getName(), nt);        
    }
    /**
     * @return the rules
     */
    public Set<ProductionRule> getRules() {
        return rules;
    }
    
    public void addRule(ProductionRule rule) {
        rules.add(rule);        
    }
    
    public boolean hasTerminal(String terminalName) {
        if (this.terminals.containsKey(terminalName))
            return true;
        return false;
    }
    
    public boolean hasNonTerminal(String nonTerminalName) {
        if (this.nonTerminals.containsKey(nonTerminalName))
            return true;
        return false;
    }
    
    public Terminal getTerminal(String terminalName) {
        return this.terminals.get(terminalName);
    }
    
    public NonTerminal getNonTerminal(String nonTerminalName) {
        return this.nonTerminals.get(nonTerminalName);
    }
}
