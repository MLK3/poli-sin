package br.usp.language.syntax;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.usp.language.automata.Action;
import br.usp.language.automata.MachineCall;
import br.usp.language.automata.StackMachine;
import br.usp.language.automata.State;
import br.usp.language.automata.StateMachine;
import br.usp.language.automata.Transition;
import br.usp.language.morph.MorphologicAnalyser;
import br.usp.language.morph.TokenMorph;
import br.usp.language.syntax.grammar.ContextFreeGrammar;
import br.usp.language.syntax.grammar.NonTerminal;
import br.usp.language.syntax.grammar.ProductionElement;
import br.usp.language.syntax.grammar.ProductionRule;
import br.usp.language.syntax.tree.SyntaxTree;
import br.usp.language.syntax.tree.SyntaxTreeNode;

public class SyntacticAnalyser {

    private StackMachine analyser;

    private MorphologicAnalyser ma;

    private SyntaxTree tree;

    private SyntaxTreeNode currentTreeNode;

    private TokenMorph currentToken;

    /**
     * This constructor receives a ContextFreeGrammar that will be used to generate a StackMachine
     * @param ma
     * @param grammar
     */
    public SyntacticAnalyser(MorphologicAnalyser ma, ContextFreeGrammar grammar) {
        this.ma = ma;
        this.analyser = new StackMachine(this.createSyntaxStateMachines(grammar));

        SyntaxTreeNode root = new SyntaxTreeNode(grammar.getStartSymbol().getName());
        this.tree = new SyntaxTree(root);
        this.currentTreeNode = root;
    }

    /**
     * This constructor receives a StackMachine; proper actions will be set
     * @param ma
     * @param stackMachine
     */
    public SyntacticAnalyser(MorphologicAnalyser ma, StackMachine stackMachine) {
        this.ma = ma;
        stackMachine.restart();
        this.setActions(stackMachine); // prepare semantic actions to build the tree
        this.analyser = stackMachine;

        SyntaxTreeNode root = new SyntaxTreeNode(stackMachine.getCurrentStateMachine().getName());
        this.tree = new SyntaxTree(root);
        this.currentTreeNode = root;
    }

    public void setReader(Reader reader) {
    	this.ma.setInput(reader);
    }
    
    public SyntaxTree getTree() {
        return this.tree;
    }

    public StackMachine getStackMachine() {
        return this.analyser;
    }

    /**
     * Analyses the input, but only considers the first gramatical category given by the morphological analyser.
     * 
     * @return
     */
    public boolean analyse() {

        while (ma.hasMoreTokens()) {
            this.currentToken = ma.getNextToken();
            this.analyser.input(currentToken.getType());
        }

        this.analyser.finish();

        return analyser.getCurrentStateMachine().isAtAcceptState();
    }

    /**
     * Considers all possibilities
     * 
     * @return true if it is in an acceptState
     */
    public boolean analyse2() {

        List<TokenMorph[]> allTokens = new ArrayList<TokenMorph[]>();

        // Retrieves all tokens
        while (ma.hasMoreTokens()) {
            allTokens.add(ma.getNextTokens());
        }

        int numWords = allTokens.size();
        // This array saves the previous decisions
        int possibIndex[] = new int[numWords];
        int backtrackPoint = -1;

        // Loop words
        int i = 0;
        while (i < numWords) {

            TokenMorph[] wordTokens = allTokens.get(i);

            boolean restart = false;
            int aux = 0;
            if (i == backtrackPoint) { // Backtrack point - change decision
                aux = possibIndex[i] + 1; // Next option
                if (aux >= wordTokens.length) { // If there arent options
                    backtrackPoint = i - 1;
                    if (backtrackPoint < 0) {
                        break; // No more possiblities
                    }
                    this.reset();
                    i = 0;
                    restart = true;
                }
            }

            if (!restart) {
                // Loop each word possibilities
                boolean consumed = false;
                for (int j = aux; !consumed && j < wordTokens.length; j++) {
                    this.currentToken = wordTokens[j];
                    consumed = this.analyser.input(currentToken.getType());
                    if (consumed) {
                        // Decision made ==> save
                        possibIndex[i] = j;
                    }
                }
                if (!consumed) { // Backtracking time! Not consumed            
                    backtrackPoint = i - 1;
                    if (backtrackPoint < 0)
                        break; // No more possiblities
                    this.reset();
                    i = 0;
                } else if (this.analyser.getCurrentStateMachine().isAtErrorState()) {
                    // Backtracking time! Consumed, but led to an error state
                    backtrackPoint = i;
                    this.reset();
                    i = 0;
                } else {
                    i++;
                }
            }
        }
        
        this.analyser.finish();

        return this.analyser.getCurrentStateMachine().isAtAcceptState();
    }

    /**
     * Restarts machine and deletes tree
     */
    public void reset() {
        this.analyser.restart();
        this.tree.getRoot().pruneAllChildren();
        this.currentTreeNode = this.tree.getRoot();
    }

    protected List<StateMachine> createSyntaxStateMachines(ContextFreeGrammar grammar) {
        HashMap<String, StateMachine> stateMachines = new HashMap<String, StateMachine>();

        // First, create one machine for each non-terminal
        for (NonTerminal nt : grammar.getNonTerminals()) {
            stateMachines.put(nt.getName(), new StateMachine(nt.getName()));
        }

        // Now extend each machine
        for (NonTerminal nt : grammar.getNonTerminals()) {
            StateMachine stateMachine = stateMachines.get(nt.getName());
            State initialState = stateMachine.getInitialState();
            State lastState = initialState;
            State finalState = stateMachine.createState("Final");
            finalState.setAcceptState();
            State errorState = stateMachine.createState("Error");
            errorState.setErrorState();

            errorState.setAlternateTransition(errorState, null);

            List<ProductionRule> productions = new ArrayList<ProductionRule>();
            for (ProductionRule rule : grammar.getRules()) {
                if (rule.getGenerator().equals(nt))
                    productions.add(rule);
            }

            int numStates = 1;

            // Instanciates the actions
            // As they are always the same, we'll use the same instance always.
            Action actionTerminal = new AddLeaf();
            Action actionCallSubMachine = new AddNonTerminalChild();
            Action actionReturnSubMachine = new ReturnParent();

            for (ProductionRule prod : productions) {
                lastState = initialState;

                List<ProductionElement> products = prod.getProducts();

                for (ProductionElement product : products) {
                    String prodToken = product.getName();

                    // If the product is a terminal, we must create a State which input is the terminal name
                    if (product.getType() == ProductionElement.TERMINAL) {

                        // Verifies if such transition doesn't exist
                        State nextState = lastState.getNextState(prodToken);
                        if (nextState == null) {
                            State newState = stateMachine.createState("State" + numStates + "_" + prodToken);
                            numStates++;
                            lastState.createTransitionTo(newState, prodToken, actionTerminal);
                            lastState.setAlternateTransition(errorState, null);
                            lastState = newState;
                        } else { // This transition already exists
                            lastState = nextState;
                        }
                    } else if (product.getType() == ProductionElement.EPSILON) {
                        // Case it is epsilon
                        initialState.createEpsilonTransitionTo(finalState, null);
                    } else if (product.getType() == ProductionElement.NONTERMINAL) {
                        // Por fim, quando há um não-terminal na produção, é preciso realizar a chamada de outra máquina 
                        // que continuára a análise sintática. Para isso, emprega-se um tipo de transição especial.

                        State nextState = lastState.getNextState(prodToken);
                        if (nextState == null) {

                            // Estado de retorno da submaquina
                            State newState = stateMachine.createState("State" + numStates + "_" + prodToken);
                            numStates++;
                            // Condição especial que indica transicao dado um nao-terminal
                            lastState.createMachineCallTo(newState, prodToken, actionCallSubMachine,
                                    actionReturnSubMachine);
                            lastState = newState;
                        } else {
                            lastState = nextState;
                        }
                    }
                }
                // Ao final de uma produção, deve-se ir do último estado sequencial criado para o estado final da máquina
                // a fim de indicar que a sequencia determinada na produção é válida, pois levou ao estado final.
                if (lastState != initialState)
                    lastState.createEpsilonTransitionTo(finalState, null);
            } // END LOOP RULES
        } // END LOOP NON-TERMINALS

        // Create a list which first element is the start symbol
        List<StateMachine> list = new ArrayList<StateMachine>(stateMachines.values());
        StateMachine firstMachine = stateMachines.remove(grammar.getStartSymbol().getName());
        list.add(0, firstMachine);
        list.addAll(stateMachines.values());
        return list;
    }
    
    /**
     * Set properly the stack machine transitions actions, 
     * so the semantic actions will build the syntactic tree
     * @param stackMachine
     */
    protected void setActions(StackMachine stackMachine) {
    	
        // Instanciates the actions
        // As they are always the same, we'll use the same instance always.
        Action actionTerminal = new AddLeaf();
        Action actionCallSubMachine = new AddNonTerminalChild();
        Action actionReturnSubMachine = new ReturnParent();

        for (StateMachine sm: stackMachine.getSubMachines()) {
        	
        	for (State s: sm.getAllStates()) {

        		for (Transition t: s.getTransitions()) {
        			t.setAction(actionTerminal);
        		}
        		for (MachineCall mc: s.getMachineCalls()) {
        			
        				mc.setAction(actionReturnSubMachine);
        				mc.setActionBefore(actionCallSubMachine);
        		}
        	}
        }
        
    }

    private class AddNonTerminalChild implements Action {

        public void doAction() {
            SyntaxTreeNode newNode = new SyntaxTreeNode(analyser.getCurrentStateMachine().getName());
            currentTreeNode.addChild(newNode);
            currentTreeNode = newNode;
        }
    }

    private class ReturnParent implements Action {

        public void doAction() {
            // Guarda o pai
            SyntaxTreeNode parent = currentTreeNode.getParent();

            // Se nao gerou filhos, corta.
            if (currentTreeNode.getNumberOfChildren() == 0) {
                currentTreeNode.pruneItselfFromParent();
            }
            // Atual é o pai
            currentTreeNode = parent;
        }
    }

    private class AddLeaf implements Action {

        public void doAction() {
            SyntaxTreeNode newNode = new SyntaxTreeNode(currentToken.getType(), currentToken);
            currentTreeNode.addChild(newNode);
            // Current node stays the same
        }
    }
}
