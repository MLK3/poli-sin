package br.usp.language.syntax.tree;

public class SyntaxTree {

    private SyntaxTreeNode root;

    public SyntaxTree(SyntaxTreeNode root) {
        super();
        this.root = root;
    }

    public SyntaxTreeNode getRoot() {
        return root;
    }

    public SyntaxTreeNode getNextDFSNode() {
        return null;
    }
    
    public String polishNotation() {
        return root.polishNotation();    
    }
    
    public SyntaxTreeNode search(String label) {
        return root.search(label);
    }

    @Override
    public String toString() {
        
        
        return super.toString();
    }

}
