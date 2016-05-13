package com.drexelsp.blunote.network;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by omnia on 5/2/16.
 */
public class NetworkNode {
    private String value;
    private ArrayList<NetworkNode> children;

    public NetworkNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public boolean addToNode(String existingNode, String newNode) {
        NetworkNode node = getNode(existingNode);
        return node != null && node.addChild(newNode);
    }


    public ArrayList<String> removeNodeSubTree(String value) {
        NetworkNode node = getNode(value);
        if (node == null) {
            return null;
        }
        ArrayList<String> subTree = node.getSubTree();
        NetworkNode parent = getParentOf(value);
        parent.removeChild(node.getValue());
        return subTree;
    }

    private String getValue() {
        return this.value;
    }

    private boolean addChild(String value) {
        return this.children.add(new NetworkNode(value));
    }

    private boolean removeChild(String value) {
        for (NetworkNode node : this.children) {
            if (node.getValue().equals(value)){
                return this.children.remove(node);
            }
        }
        return false;
    }

    private NetworkNode getNode(String value) {
        if (this.getValue().equals(value)) {
            return this;
        } else {
            for (NetworkNode node : this.children) {
                NetworkNode returnValue = node.getNode(value);
                if (returnValue != null) {
                    return returnValue;
                }
            }
            return null;
        }
    }

    private NetworkNode getParentOf(String value) {
        for (NetworkNode node : this.children) {
            if (node.getValue().equals(value)) {
                return this;
            }
        }
        for (NetworkNode node : this.children) {
            if (node.getParentOf(value) != null) {
                return node;
            }
        }
        return null;
    }

    private ArrayList<String> getSubTree() {
        ArrayList<String> values = new ArrayList<>(Collections.singletonList(this.value));
        for (NetworkNode child : this.children) {
            values.addAll(child.getSubTree());
        }
        return values;
    }
}
