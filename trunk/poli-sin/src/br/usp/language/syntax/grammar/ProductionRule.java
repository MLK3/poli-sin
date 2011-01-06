package br.usp.language.syntax.grammar;

import java.util.ArrayList;
import java.util.List;

public class ProductionRule {
    
    private NonTerminal generator;
    private List<ProductionElement> products;
    
    /**
     * Cria nova regra gramatical, começando com o não-terminal generator.
     * @param generator
     */    
    public ProductionRule(NonTerminal generator) {
        this.generator = generator;
        products = new ArrayList<ProductionElement>();
    } 
    
    public NonTerminal getGenerator() {
        return generator;
    }
    
    public List<ProductionElement> getProducts() {
        return products;
    }
    
    /**
     * Adiciona produto e à regra.
     * @param e
     */
    public void add(ProductionElement e) {
        products.add(e);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(generator.getName() + " -> ");
        for (ProductionElement pe : products) {
            sb.append(pe.getName());
            sb.append(" + ");
        }                
        return sb.substring(0, sb.length()-3); // Remove o ultimo " + "
    }
}
