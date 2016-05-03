package com.drexelsp.blunote.network;

import java.util.ArrayList;
import java.util.Arrays;
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

    public String getValue() {
        return this.value;
    }

    public boolean addChild(NetworkNode node) {
        return this.children.add(node);
    }

    public boolean removeChild(NetworkNode node) {
        return this.children.remove(node);
    }

    public boolean addToNode(String value, NetworkNode newNode) {
        NetworkNode node = getNode(value);
        return node != null && node.addChild(newNode);
    }

    public NetworkNode getNode(String value) {
        if (this.value.equals(value)) {
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

    public ArrayList<String> getSubTree() {
        ArrayList<String> values = new ArrayList<>(Collections.singletonList(this.value));
        for (NetworkNode child : this.children) {
            values.addAll(child.getSubTree());
        }
        return values;
    }

    public ArrayList<String> getNodeSubTree(String value) {
        NetworkNode node = getNode(value);
        return node != null ? node.getSubTree() : null;
    }
}
