/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.elements;

import advancedpathfinding.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Sokolov@ivc.org
 */
public class Field {

    public final int left;
    public final int top;
    public final int right;
    public final int bottom;
    public final Map<String, Node> nodes = new HashMap<>();

    private Node temp;

    public Field(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.temp = null;
    }

    public boolean isEntry(Node node) {
        return (node.x >= left) && (node.x <= right)
                && (node.y <= bottom) && (node.y >= top);
    }

    public Node addNode(int x, int y, int l) {
        Node node = new Node(x, y, l);
        if (!nodes.containsKey(node.key)) {
            for (Entry<String, Node> e : nodes.entrySet()) {
                e.getValue().addNode(node);
                node.addNode(e.getValue());
            }
            nodes.put(node.key, node);
        } else {
            node = nodes.get(node.key);
            for (Entry<String, Node> e : nodes.entrySet()) {
                e.getValue().addNode(node);
                node.addNode(e.getValue());
            }
        }
        return node;
    }

    public Node addTempNode(int x, int y, int l) {
        Node node = new Node(x, y, l);
        if (!nodes.containsKey(node.key)) {
            for (Entry<String, Node> e : nodes.entrySet()) {
                e.getValue().addNode(node);
                node.addNode(e.getValue());
            }
            this.temp = node;
            nodes.put(node.key, node);
        } else {
            node = nodes.get(node.key);
            for (Entry<String, Node> e : nodes.entrySet()) {
                e.getValue().addNode(node);
                node.addNode(e.getValue());
            }
            this.temp = null;
        }
        return node;
    }

    public void formEdges() {
        for (Entry<String, Node> e : nodes.entrySet()) {
            e.getValue().smartGraf(this);
            e.getValue().normalize();
        }
    }

    public void removeTemp() {
        if (temp != null) {
            for (Node n : temp.getNodes()) {
                n.removeNode(temp);
            }
            nodes.remove(temp.key);
        }
    }

}
