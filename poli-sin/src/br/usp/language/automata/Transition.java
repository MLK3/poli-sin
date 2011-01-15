package br.usp.language.automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Representação de uma transição entre estados de um autômato.
 * 
 * @author Marcelo Li Koga
 * 
 */
public class Transition {

    public static final String EPSILON = "&epsilon";

    /* Atributos */
    /** Estado origem da transição */
    private State from;

    /** Estado destino da transição */
    private State to;

    /**
     * Lista contendo todas as entradas que acionam a transição. Essas entradas são consideradas as condições da
     * transição e são sempre String.
     */
    private List<String> conditions;

    /**
     * A ocorrência da transição implica na realização de uma ação que será responsável pelo processamento das
     * informações pelo autômato.
     */
    private Action action;

    /* Construtores */

    public Transition(State from, State to, String condition, Action action) {
        this.from = from;
        this.to = to;
        this.conditions = new ArrayList<String>();
        this.conditions.add(condition);
        this.action = action;
    }

    public Transition(State from, State to, List<String> conditions, Action action) {
        this.from = from;
        this.to = to;
        this.conditions = conditions;
        this.action = action;
    }

    public Transition(State from, State to, Action action) {
        this.from = from;
        this.to = to;
        this.conditions = new ArrayList<String>();
        this.action = action;
    }

    /* Methods */

    /**
     * Insere uma nova condição na lista de condições da transição
     * 
     * @param condition nova condição
     */
    public void addCondition(String condition) {
        this.conditions.add(condition);
    }

    /**
     * Método no qual a transição é realizada, ocorrendo a chamada da ação
     * 
     * @return estado destino da transição
     */
    public State execute() {
        if (this.action != null) {
            this.action.doAction();
        }
        return this.to;
    }

    /**
     * @return Estado origem da transição
     */
    public State getStateFrom() {
        return from;
    }

    /**
     * @return Estado destino da transição
     */
    public State getStateTo() {
        return to;
    }

    /**
     * @return Lista contendo todas as entradas que acionam a transição. Essas entradas são consideradas as condições da
     *         transição.
     */
    public List<String> getConditions() {
        return conditions;
    }

    /**
     * Retorna a ação que essa transição realiza
     * 
     * @return action
     */
    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * 
     * @return true, if there's an epsilon condition for transitioning.
     */
    public boolean isEpsilon() {
        if (this.conditions.contains(EPSILON)) {
            return true;
        }
        return false;
    }

    public boolean isMachineCall() {
        return false;
    }

    // methods that change the automata behavior
    // intended to allow adaptatvive actions

    public void setFrom(State from) {
        this.from = from;
    }

    public void setTo(State to) {
        this.to = to;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }
    
    /**
     * Inverts this transition, i.e., swaps states from and to
     */
    public void invert() {
        this.from.removeTransition(this);
        this.to.addTransition(this);
        State aux = this.from;
        this.from = this.to;
        this.to = aux;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
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
        Transition other = (Transition) obj;
        if (conditions == null) {
            if (other.conditions != null)
                return false;
        } else if (!conditions.equals(other.conditions))
            return false;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(from.getName() + " -> " + to.getName() + "; Conds: ");
        for (String cond : this.conditions) {
            sb.append(cond);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
