package com.drexelsp.blunote.blunote;

import com.drexelsp.blunote.network.NetworkNode;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void NetworkTree_isCorrect() throws Exception {
        NetworkNode tree = new NetworkNode("L0N0");
        assertTrue(tree.addToNode("L0N0", "L1N0"));
        assertTrue(tree.addToNode("L0N0", "L1N1"));
        assertTrue(tree.addToNode("L0N0", "L1N2"));

        assertTrue(tree.addToNode("L1N0", "L2N0"));
        assertTrue(tree.addToNode("L1N0", "L2N1"));

        assertTrue(tree.addToNode("L2N0", "L3N0"));
        assertTrue(tree.addToNode("L2N0", "L3N1"));
        assertTrue(tree.addToNode("L2N1", "L3N2"));

        assertTrue(tree.addToNode("L3N1", "L4N0"));
        assertTrue(tree.addToNode("L3N1", "L4N1"));

        assertNull(tree.removeNodeSubTree("L5N0"));

        ArrayList<String> subTree = tree.removeNodeSubTree("L3N1");
        assertNotNull(subTree);

        assertTrue(subTree.contains("L3N1"));
        assertTrue(subTree.contains("L4N0"));
        assertTrue(subTree.contains("L4N1"));

        ArrayList<String> subTreeTwo = tree.removeNodeSubTree("L2N0");
        assertNotNull(subTreeTwo);

        assertTrue(subTreeTwo.contains("L2N0"));
        assertTrue(subTreeTwo.contains("L3N0"));
        assertFalse(subTreeTwo.contains("L3N1"));
        assertFalse(subTreeTwo.contains("L4N0"));
        assertFalse(subTreeTwo.contains("L4N1"));

        assertFalse(tree.addToNode("L9N9", "L10N10"));
        assertNull(tree.removeNodeSubTree("L10N10"));

    }
}