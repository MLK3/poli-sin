package br.usp.language.syntax.grammar;

public abstract class ProductionElement {

    public static final int EPSILON = 0;
    public static final int TERMINAL = 1;
    public static final int NONTERMINAL = 2;

    protected String name;
    protected int type;

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProductionElement other = (ProductionElement) obj;
        if (type != other.type) {
            return false;
        } else if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
