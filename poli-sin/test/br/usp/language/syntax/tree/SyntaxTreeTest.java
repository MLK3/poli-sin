package br.usp.language.syntax.tree;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.language.morph.TokenMorph;
import br.usp.language.syntax.tree.SyntaxTree;
import br.usp.language.syntax.tree.SyntaxTreeNode;


public class SyntaxTreeTest {
    
    SyntaxTree tree;
    
    @Before
    public void setUp() throws Exception {
        SyntaxTreeNode root = new SyntaxTreeNode("OA");
        SyntaxTreeNode ss1 = new SyntaxTreeNode("SS");
        SyntaxTreeNode sv = new SyntaxTreeNode("SV");
        SyntaxTreeNode vtd = new SyntaxTreeNode("VTD");
        SyntaxTreeNode ss2 = new SyntaxTreeNode("SS");
        SyntaxTreeNode leaf1 = new SyntaxTreeNode("ppes", new TokenMorph("Eu", "", null,false));
        SyntaxTreeNode leaf2 = new SyntaxTreeNode("v", new TokenMorph("jogo", "", null,false));
        SyntaxTreeNode leaf3 = new SyntaxTreeNode("nc", new TokenMorph("futebol", "", null,false));       
        root.addChild(ss1);
        root.addChild(sv);
        ss1.addChild(leaf1);
        sv.addChild(vtd);
        sv.addChild(ss2);
        vtd.addChild(leaf2);
        ss2.addChild(leaf3);       
        tree = new SyntaxTree(root);
    }

    @Test
    public void testPolishNotation() {        
        assertEquals("[OA [SS [ppes Eu]][SV [VTD [v jogo]][SS [nc futebol]]]]", tree.polishNotation());        
    }
    
    @Test
    public void testGetSiblings() {
        assertEquals(2, tree.getRoot().getChildNodes().size());
        SyntaxTreeNode node = tree.getRoot().getChildNodes().get(0);
        List<SyntaxTreeNode> siblings = node.getSiblings();
        assertEquals(1, siblings.size());
        assertEquals(2, tree.getRoot().getChildNodes().size());
        
        SyntaxTreeNode n = siblings.get(0);
        n.setLabel("Bla");
        // Mudou o objeto
        assertEquals("Bla", tree.getRoot().getChildNodes().get(1).getLabel());
    }
    
    @Test
    public void testGetAllLeafs() {
        List<SyntaxTreeNode> leafs = tree.getRoot().getAllLeafs();
        assertEquals(3, leafs.size());
        assertEquals("ppes", leafs.get(0).getLabel());
        assertEquals("v", leafs.get(1).getLabel());
        assertEquals("nc", leafs.get(2).getLabel());
    }
    
    @Test
    public void testPruneAllChildren() {
        tree.getRoot().pruneAllChildren();
        assertEquals(0, tree.getRoot().getNumberOfChildren());
    }

}
