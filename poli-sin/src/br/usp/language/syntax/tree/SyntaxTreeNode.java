package br.usp.language.syntax.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.usp.language.morph.TokenMorph;

/**
 * Representa um nó da árvore sintática.
 * 
 * @author Marcelo Li Koga
 * 
 */
public class SyntaxTreeNode {

    /** Label  */
    private String label;

    private SyntaxTreeNode parent;
    private List<SyntaxTreeNode> childNodes;

    /** SyntaTreeNode may have a token assigned */
    private TokenMorph token;


    public SyntaxTreeNode(String label) {
        this.label = label;
        this.childNodes = new ArrayList<SyntaxTreeNode>(2);
    }

    /**
     * Creates a new Node, short label will be the same as the label And assigns to it an object
     * 
     * @param shortLabel/label
     */
    public SyntaxTreeNode(String label, TokenMorph token) {
        this(label);
        this.token = token;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the childNodes
     */
    public List<SyntaxTreeNode> getChildNodes() {
        // Isso para retornar uma cópia do array e preservar a lista original (mas os objetos são os mesmos). 
        return new ArrayList<SyntaxTreeNode>(this.childNodes);
    }

    public void setParent(SyntaxTreeNode parent) {
        this.parent = parent;
    }

    public SyntaxTreeNode getParent() {
        return this.parent;
    }

    /**
     * Returns the siblings of the node, i.e., the other child nodes of the same parent.
     * @return List of siblings
     */
    public List<SyntaxTreeNode> getSiblings() {
        if (this.parent != null) {
            List<SyntaxTreeNode> siblings = this.parent.getChildNodes();
            siblings.remove(this);
            // A lista é outra, mas os objetos são os mesmos.
            return siblings;
        }
        return Collections.emptyList();
    }

    /**
     * @param childNodes the childNodes to set
     */
    public void setChildNodes(List<SyntaxTreeNode> childNodes) {
        this.childNodes = childNodes;
    }

    public TokenMorph getToken() {
        return token;
    }

    public void setToken(TokenMorph token) {
        this.token = token;
    }

    /* === END OF GETTERS AND SETTERS === */

    /**
     * @param childNode the childNode
     */
    public void addChild(SyntaxTreeNode childNode) {
        childNode.setParent(this);
        this.childNodes.add(childNode);
    }

    /**
     * Prunes child
     * 
     * @param childNode the childNode
     */
    public void pruneChild(SyntaxTreeNode childNode) {
        this.childNodes.remove(childNode);
        childNode.setParent(null);
    }
    
    public void pruneAllChildren() {
        this.childNodes.clear();
    }

    /**
     * The node prunes itself from parent node.
     */
    public void pruneItselfFromParent() {
        this.getParent().pruneChild(this);
    }

    /**
     * Retorna o número de filhos.
     * 
     * @return
     */
    public int getNumberOfChildren() {
        return childNodes.size();
    }

    public boolean isLeaf() {
        return (childNodes.size() == 0);
    }

    /**
     * Retorna o filho de número index, sendo 0 para o primeiro, 1 para o segundo, etc.
     * 
     * @param index
     * @return filho
     */
    public SyntaxTreeNode getChild(int index) {
        return this.childNodes.get(index);
    }

    public String polishNotation() {
        if (this.isLeaf()) {
            if (this.token != null)
                return "[" + this.label + " " + this.token.getLexeme() + "]";
        }
        int n = this.getNumberOfChildren();
        String str = "";
        for (int i = 0; i < n; i++) {
            str = str + this.getChild(i).polishNotation();
        }
        return "[" + this.label + " " + str + "]";
    }

    /**
     * Searches inside this tree for a node labeled label using DFS (depth-first search).
     * 
     * @param label
     * @return The first node found with the given label. Null, if not found. 
     */
    public SyntaxTreeNode search(String label) {
        if (this.label.equals(label)) {
            return this;
        }
        for (SyntaxTreeNode node : this.childNodes) {
            SyntaxTreeNode resp = node.search(label);
            if (resp != null)
                return resp;
        }
        return null;
    }

    /**
     * Retuans a list with all Leafs
     * @return
     */
    public List<SyntaxTreeNode> getAllLeafs() {
        List<SyntaxTreeNode> leafs = new ArrayList<SyntaxTreeNode>();
        if (this.isLeaf()) {
            leafs.add(this);
        } else {
            for (SyntaxTreeNode child : this.childNodes) {
                leafs.addAll(child.getAllLeafs());
            }
        }
        return leafs;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
