/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedpathfinding;

import grid.Grid;
import grid.elements.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sokolov@ivc.org
 */
public class Node {

    public final int x;
    public final int y;
    public final String key;
    public final int level;

    private static Grid grid;

    private final List<Node> nodes = new ArrayList<>();
    private final Map<String, Double> weights = new HashMap<>();

    public Node(int x, int y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.key = Integer.toString(x) + "|" + Integer.toString(y) + "|" + Integer.toString(level);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node n) {
        if (level == n.level) {
            nodes.add(n);
        }
    }

    public Double getWeight(String key) {
        return weights.get(key);
    }

    public double dist(int x, int y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
    }

    public double dist(Node node) {
        return Math.sqrt(Math.pow(this.x - node.x, 2) + Math.pow(this.y - node.y, 2));
    }

    public static void setGrid(Grid grid) {
        Node.grid = grid;
    }

    public void addWeight(String key, Cell cell) {
        if ((cell != null) && (!weights.containsKey(key))) {
            weights.put(key, cell.getG());
        }
    }

    public void addWeight(String key, double d) {
        if (!weights.containsKey(key)) {
            weights.put(key, d);
        }
    }

    public void smartGraf(Field f) {
        if (grid == null) {
            throw new IllegalStateException("Node не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        for (Node n : nodes) {
            if ((n == this) || (n.level != level) || (weights.containsKey(n.key))) {
                continue;
            }
            if (!f.isEntry(n)) {
                Double d = dist(n);
                addWeight(n.key, d);
                n.addWeight(key, d);
                continue;
            }
            Algorithm.setGrid(grid);
            Cell cell = Algorithm.searchPath(n.x, n.y, x, y, level, f);
            addWeight(n.key, cell);
            n.addWeight(key, cell);
        }
    }

    public void normalize() {
        List<Node> removeList = new ArrayList<>();
        for (Node n : nodes) {
            if (!weights.containsKey(n.key)) {
                removeList.add(n);
            }
        }
        for (Node n : removeList) {
            nodes.remove(n);
        }
    }

    public void removeNode(Node node) {
        if (node != null) {
            nodes.remove(node);
            weights.remove(node.key);
        }
    }

}
