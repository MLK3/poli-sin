package br.usp.language.syntax.grammar;

public class NonTerminal extends ProductionElement {
    
    private boolean auxiliary;

    public NonTerminal(String name) {
        this.name = name;
        this.type = NONTERMINAL;
        this.auxiliary = false;
    }
    
    /**
     * 
     * @param name
     * @param aux tells if it is an auxiliary (without semantic relevance) non-terminal or not
     */
    public NonTerminal(String name, boolean aux) {
        this.name = name;
        this.type = NONTERMINAL;
        this.auxiliary = aux;
    }
    
    /**
     * Tells if it is an auxiliary (without semantic relevance) non-terminal or not
     * @return
     */
    public boolean isAuxiliary() {
        return this.auxiliary;
    }
}
