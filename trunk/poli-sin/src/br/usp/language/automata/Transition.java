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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(from.getName() + " -> " + to.getName() + "; Conds: ");
        for (String cond : this.conditions) {
            sb.append(cond);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
