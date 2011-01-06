package br.usp.language.syntax.grammar;

public class Terminal extends ProductionElement {

    public Terminal(String name) {
        this.name = name;
        this.type = TERMINAL;
    }
}
