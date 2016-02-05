/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedpathfinding;

/**
 *
 * @author Вячеслав
 */
public class Leaf {

    public final Node node;
    public final String key;

    private Leaf leaf;
    private double g;
    private double h;
    private double f;

    public Leaf(Node node) {
        this.node = node;
        this.key = node.key;
    }

    public Leaf getLeaf() {
        return leaf;
    }

    public void setLeaf(Leaf leaf) {
        this.leaf = leaf;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getF() {
        return f;
    }

    public void calcH(Node node) {
        this.h = Math.sqrt(Math.pow(node.x - this.node.x, 2)
                + Math.pow(node.y - this.node.y, 2));
    }

    public void calcF() {
        this.f = this.h + this.g;
    }

    /*public Cell getCell() {
        if (leaf != null) {
            return node.getCell(leaf.node);
        }
        return null;
    }*/

}
