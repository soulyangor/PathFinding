/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import advancedpathfinding.Algorithm;
import advancedpathfinding.Cell;
import advancedpathfinding.Graph;
import advancedpathfinding.Leaf;
import advancedpathfinding.Node;
import grid.Grid;
import grid.elements.Field;
import java.util.List;
import units.Unit;

/**
 *
 * @author Хозяин
 */
public class PathController {

    private final Grid grid;
    private final Unit unit;
    private final double eps;

    private Cell aim;
    private Cell cell;
    private Leaf leaf;

    private boolean redef;

    public PathController(Grid grid, Unit unit) {
        this.grid = grid;
        this.unit = unit;
        this.eps = 1.2;
    }

    public boolean isArrivedToAim() {
        if (aim == null) {
            return true;
        } else {
            double ax = toDoubleValue(aim.x);
            double ay = toDoubleValue(aim.y);
            return Math.sqrt((ax - unit.getX()) * (ax - unit.getX())
                    + (ay - unit.getY()) * (ay - unit.getY())) < eps;
        }
    }

    public boolean isRedef() {
        return redef;
    }

    public void setRedef(boolean redef) {
        this.redef = redef;
    }

    public Cell findNearPosition(List<Cell> cells) {
        if (cells == null) {
            return null;
        }
        int ux = (int) (unit.getX() / grid.cellSize);
        int uy = (int) (unit.getY() / grid.cellSize);
        Cell c = Algorithm.searchNearPosition(ux, uy, unit, cells);
        if (c != null) {
            System.out.println("NEAR_CELL - " + c.key);
        } else {
            System.out.println("NEAR_CELL - NULL");
        }
        return c;
    }

    public Cell getAim() {
        return aim;
    }

    public Cell availableCell(int distance) {
        int ux = (int) (unit.getX() / grid.cellSize);
        int uy = (int) (unit.getY() / grid.cellSize);
        return Algorithm.searchAvailableDistance(ux, uy, unit.getSize(), distance);
    }

    public void distPath(Cell aim) {
        if (aim == null) {
            return;
        }
        this.aim = aim;

        int ux = (int) (unit.getX() / grid.cellSize);
        int uy = (int) (unit.getY() / grid.cellSize);

        this.cell = Algorithm.searchPath(aim.x, aim.y, ux, uy, unit);
        this.leaf = null;
    }

    /**
     * Определяет путь от объекта до целевой клетки, обозначенной параметром
     * aim. После выполнения метода, можно получить текущую клетку пути
     *
     * @param aim
     */
    public void definePath(Cell aim) {
        if (aim == null) {
            return;
        }
        this.aim = aim;

        int size = unit.getSize();
        int ux = toIntValue(unit.getX());
        int uy = toIntValue(unit.getY());

        if (dist(aim.x, aim.y, ux, uy) < grid.fieldSize) {
            this.cell = Algorithm.searchPath(aim.x, aim.y, ux, uy, unit.getSize());
            this.leaf = null;
            return;
        }
        Field fs = grid.getField(ux, uy);
        Field fe = grid.getField(aim.x, aim.y);
        Node n1 = fs.addTempNode(ux, uy, size);
        Node n2 = fe.addTempNode(aim.x, aim.y, size);
        fs.formEdges();
        fe.formEdges();
        this.leaf = Graph.searchPath(n2, n1, size);
        fs.removeTemp();
        fe.removeTemp();
        if (leaf != null) {
            int x0 = leaf.node.x;
            int y0 = leaf.node.y;
            int x = leaf.getLeaf().node.x;
            int y = leaf.getLeaf().node.y;
            this.cell = Algorithm.searchPath(x, y, x0, y0, size, fs);
        }
    }

    public void redef() {
        System.out.println("REDEFINIG AIM:" + aim.key);
        distPath(aim);
    }

    public void definePathWithUnit(Cell aim) {
        this.aim = aim;

        int size = unit.getSize();
        int ux = (int) (unit.getX() / grid.cellSize);
        int uy = (int) (unit.getY() / grid.cellSize);

        int r = (int) Math.sqrt((aim.x - ux) * (aim.x - ux)
                + (aim.y - uy) * (aim.x - uy));
        if (r < grid.fieldSize) {
            this.cell = Algorithm.searchPath(aim.x, aim.y, ux, uy, unit);
            this.leaf = null;
            return;
        }
        Field fs = grid.getField(ux, uy);
        Field fe = grid.getField(aim.x, aim.y);
        Node n1 = fs.addTempNode(ux, uy, size);
        Node n2 = fe.addTempNode(aim.x, aim.y, size);
        fs.formEdges();
        fe.formEdges();
        this.leaf = Graph.searchPath(n2, n1, size);
        fs.removeTemp();
        fe.removeTemp();
        if (leaf != null) {
            int x0 = leaf.node.x;
            int y0 = leaf.node.y;
            int x = leaf.getLeaf().node.x;
            int y = leaf.getLeaf().node.y;
            this.cell = Algorithm.searchPath(x, y, x0, y0, fs, unit);
        }
    }

    /**
     * Возвращает длину пути от текущей точки до конечной
     *
     * @return double
     */
    public double getLength() {
        return cell == null ? -1 : cell.getG();
    }

    /**
     * Возвращает текущую клетку пути
     *
     * @return Cell
     */
    public Cell getCell() {
        if (cell == null) {
            next();
        }
        return this.cell;
    }

    /**
     * Возвращает текущую вершину графа
     *
     * @return Leaf
     */
    public Leaf getLeaf() {
        return this.leaf;
    }

    /**
     * Заставляет перейти к следующей клетке пути. В результате выполнения
     * данного метода изменится текущая клетка пути. Текущая вершина графа также
     * изменится, если была достигнута предыдущая.
     */
    public void next() {
        if ((cell == null) && (leaf != null)) {
            this.leaf = leaf.getLeaf();
            if ((leaf != null) && (leaf.getLeaf() != null)) {
                int x0 = leaf.node.x;
                int y0 = leaf.node.y;
                int x = leaf.getLeaf().node.x;
                int y = leaf.getLeaf().node.y;
                this.cell = Algorithm.searchPath(x, y, x0, y0, unit.getSize());
            }
            return;
        }
        if ((cell == null) && (leaf == null)) {
            return;
        }
        cell = cell.getCell();
    }

    /**
     * Проверяет достиг ли объект центра текущей клетки, возвращает true, если
     * достиг и false, если не достигнута середина текущей клетки или текущая
     * клетка равна null
     *
     * @return boolean
     */
    public boolean isArrivedIntoCentrCell() {
        if (cell == null) {
            return false;
        }
        double cellX = toDoubleValue(cell.x);
        double cellY = toDoubleValue(cell.y);
        return dist(cellX, cellY, unit.getX(), unit.getY()) < eps;
    }

    private double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private double toDoubleValue(int x) {
        return x * grid.cellSize + grid.cellSize / 2;
    }

    private int toIntValue(double x) {
        return (int) (x / grid.cellSize);
    }

}
