/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import advancedpathfinding.Cell;
import grid.Grid;
import advancedpathfinding.Leaf;
import java.util.ArrayList;
import java.util.List;
import units.Unit;

/**
 *
 * @author Sokolov@ivc.org
 */
public class UnitManager {

    public final Unit unit;
    public final long id;

    private static final double MIN_VALUE = 10e-6;

    private final PathController pathController;

    private boolean redef = false;
    private Grid grid;
    private long priority;
    private List<Cell> cells;

    public UnitManager(Unit unit, Grid grid) {
        this.unit = unit;
        this.grid = grid;
        this.id = unit.id;
        this.priority = id;
        grid.setUnit(unit);
        this.pathController = new PathController(grid, unit);
    }

    public void setRedef() {
        this.redef = true;
    }

    /**
     * Устанавливает набор точек положения приоритетного юнита
     *
     * @param cells
     */
    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public long getPriority() {
        return priority;
    }

    public boolean setPriority(long priority) {
        if (priority <= id) {
            this.priority = priority;
            return true;
        } else {
            return false;
        }
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Leaf getLeaf() {
        return pathController.getLeaf();
    }

    public Cell getCell() {
        return pathController.getCell();
    }
    
    public boolean isArrivedToAim(){
        return pathController.isArrivedToAim();
    }

    //Временный
    public Cell getAvailableCell() {
        return pathController.availableCell(12);
    }

    public void addAim(Cell cell) {
        if (!MovingManager.isReady()) {
            throw new IllegalStateException("MovingManager не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        pathController.definePath(cell);
    }

    public void execute() {
        if (!MovingManager.isReady()) {
            throw new IllegalStateException("MovingManager не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        Cell cell = pathController.getCell();
        if (redef) {
            if (cell == null) {
                Cell c = pathController.findNearPosition(cells);
                pathController.distPath(c);
                redef = false;
                cells = null;
                System.out.println("==================================");
            } else if (!grid.isIgnoreWalkable(cell, unit)) {
                System.out.println("CELL - " + cell.key);
                System.out.println("REDEFINING EXECUTED");
                pathController.redef();
                if (pathController.getCell() == null) {
                    System.out.println("-----------------------------");
                    Cell c = pathController.findNearPosition(cells);
                    pathController.distPath(c);
                    redef = false;
                    cells = null;
                } else {
                    System.out.println("REDEF_CELL - " + pathController.getCell().key);
                }
            }
        }

        if (pathController.isArrivedIntoCentrCell()) {
            pathController.next();
        }

        cell = pathController.getCell();

        if (cell == null) {
            priority = id;
            redef = false;
            cells = null;
            return;
        }
        defineAngle(cell);
        grid.setNullUnit(unit);
        if (grid.isAbsWalkable(unit)) {
            defineAngle(cell);
            unit.update();
        } else if (grid.isIgnoreWalkable(unit)) {
            manageLets();
        } else {
            System.out.println("REDEFINING ITSELF");
            pathController.redef();
        }
        grid.setUnit(unit);
    }

    private void manageLets() {
        for (Long l : grid.getImpedingUnitIds(unit)) {
            UnitManager manager = MovingManager.getManager(l);
            Unit u = manager.unit;
            if (MovingManager.isIgnore(u, unit)) {
                manager.setRedef();
                manager.setCells(getArea());
            }
        }
    }

    private List<Cell> getArea() {
        int size = unit.getSize();
        int x = getCell().x;
        int y = getCell().y;
        List<Cell> result = new ArrayList<>();
        for (int i = x; i < x + size; i++) {
            for (int j = y; j < y + size; j++) {
                result.add(new Cell(i, j));
            }
        }
        return result;
    }

    private List<Cell> unitBorder() {
        List<Cell> border = new ArrayList<>();
        int size = unit.getSize();
        size = 1;
        int x = (int) (unit.getX() / grid.cellSize);
        int y = (int) (unit.getY() / grid.cellSize);
        for (int i = x - 1; i <= x + size; i++) {
            border.add(new Cell(i, y - 1));
            border.add(new Cell(i, y + size));
        }
        for (int j = y; j < y + size; j++) {
            border.add(new Cell(x - 1, j));
            border.add(new Cell(x + size, j));
        }
        return border;
    }

    private void defineAngle(Cell c) {
        if (c == null) {
            return;
        }
        double ex = c.x * grid.cellSize + grid.cellSize / 2;
        double ey = c.y * grid.cellSize + grid.cellSize / 2;
        double ux = unit.getX();
        double uy = unit.getY();
        double angle;
        if (Math.abs(ex - ux) < MIN_VALUE) {
            double ch = Math.signum(ex - ux);
            double z = Math.signum(ey - uy);
            angle = Math.atan(z / (ch * MIN_VALUE));
        } else {
            angle = Math.atan((ey - uy) / (ex - ux));
        }
        if ((ex - ux) < 0) {
            angle += Math.PI;
        }
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        unit.setAngle(angle);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        UnitManager other = (UnitManager) otherObject;

        return (this.id == other.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

}
