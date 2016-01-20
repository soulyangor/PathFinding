/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedpathfinding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Вячеслав
 */
public class Graph {

    private static final double INFINITY = 10e6;

    private static int unitSize = 1;

    public static void setUnitSize(int unitSize) {
        Graph.unitSize = unitSize;
    }

    private Graph() {
    }

    public static Leaf searchPath(Node end, Node start) {
        Map<String, Leaf> openPoints = new LinkedHashMap<>();
        Map<String, Leaf> closePoints = new LinkedHashMap<>();
        Leaf newLeaf = new Leaf(end);
        Leaf curLeaf;
        newLeaf.calcH(start);
        newLeaf.calcF();
        String minKey = newLeaf.key;
        Double minValue = newLeaf.getF();
        openPoints.put(newLeaf.key, newLeaf);
        boolean complete = false;
        while ((openPoints.size() > 0) && (!complete)) {
            curLeaf = Graph.findPointByFmin(openPoints, minKey, minValue);
            String key = curLeaf.key;
            closePoints.put(key, curLeaf);
            openPoints.remove(key);
            for (Leaf iter : getNeighbors(curLeaf)) {
                Node node = iter.node;
                if (complete) {
                    break;
                }
                complete = (node.x == start.x) && (node.y == start.y);
                Graph.manageLoopByPoints(node, start, curLeaf, openPoints,
                        closePoints, minKey, minValue);
            }
        }

        String key = start.key;
        curLeaf = openPoints.get(key);
        openPoints.clear();
        closePoints.clear();
        return curLeaf;
    }

    private static Leaf findPointByFmin(Map<String, Leaf> list, String minKey,
            Double minValue) {
        if (list.containsKey(minKey)) {
            return list.get(minKey);
        }
        Leaf current = null;
        double fMin = INFINITY;
        for (Map.Entry<String, Leaf> entry : list.entrySet()) {
            if (entry.getValue().getF() < fMin) {
                fMin = entry.getValue().getF();
                current = entry.getValue();
            }
        }
        minValue = current.getF();
        return current;
    }

    private static void manageLoopByPoints(Node node, Node start, Leaf curLeaf,
            Map<String, Leaf> openList, Map<String, Leaf> closeList,
            String minKey, Double minValue) {
        String key = node.key;
        double step;
        if (closeList.containsKey(key)) {
            return;
        }
        if (openList.containsKey(key)) {
            Leaf tmp = openList.get(key);
//            step = tmp.node.dist(curLeaf.node);
            step = tmp.node.getWeight(curLeaf.node.key);
            if (tmp.getG() > curLeaf.getG() + step) {
                tmp.setLeaf(curLeaf);
                tmp.setG(curLeaf.getG() + step);
                tmp.calcF();
                minKey = null;
            }
        } else {
//            step = node.dist(curLeaf.node);
            //           System.out.println("2fl " + node.key);
            step = node.getWeight(curLeaf.node.key);
            Leaf newPoint = new Leaf(node);
            newPoint.setLeaf(curLeaf);
            newPoint.setG(curLeaf.getG() + step);
            newPoint.calcH(start);
            newPoint.calcF();
            openList.put(key, newPoint);
            if(minValue > newPoint.getF()){
                minValue = newPoint.getF();
                minKey = newPoint.key;
            }
        }
    }

    private static List<Leaf> getNeighbors(Leaf leaf) {
        List<Leaf> leafs = new ArrayList<>();
        for (Node n : leaf.node.getNodes()) {
            if (n.level == unitSize) {
                leafs.add(new Leaf(n));
            }
        }
        return leafs;
    }

}
