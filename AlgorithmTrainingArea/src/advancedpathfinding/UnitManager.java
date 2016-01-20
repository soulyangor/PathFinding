/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advancedpathfinding;

import td.unit.Unit;

/**
 *
 * @author Sokolov@ivc.org
 */
public class UnitManager {

    public final Unit unit;
    public final long id;

    private static final double MIN_VALUE = 10e-6;

    private Grid grid;
    private long priority;

    private Leaf leaf;
    private Cell cell;

    private Cell aim;

    public UnitManager(Unit unit, Grid grid) {
        this.unit = unit;
        this.grid = grid;
        this.id = unit.id;
        this.priority = id;
        grid.setUnit(unit);
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
        return leaf;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void addAim(Cell cell) {
        if (!MovingManager.isReady()) {
            throw new IllegalStateException("MovingManager не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }

        this.aim = cell;
        this.leaf = null;
        defPath();
        //reDef();
    }

    public void execute() {
        if (cell == null) {
            priority = id;
        }
        if (!MovingManager.isReady()) {
            throw new IllegalStateException("MovingManager не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        if ((leaf == null) && (cell == null)) {
            aim = null;
            return;
        }
        if (cell != null) {
            double cx = cell.x * grid.cSize + grid.cSize / 2;
            double cy = cell.y * grid.cSize + grid.cSize / 2;
            double r = Math.sqrt((cx - unit.getX()) * (cx - unit.getX())
                    + (cy - unit.getY()) * (cy - unit.getY()));
            if (r < 1.2) {
                this.cell = cell.getCell();
                defineAngle(this.cell);
                if (id != 0) {
                    System.out.println("df1: " + unit.getAngle());
                }
            }
        } else {
            this.leaf = leaf.getLeaf();
            if ((leaf != null) && (leaf.getLeaf() != null)) {
                int x0 = leaf.node.x;
                int y0 = leaf.node.y;
                int x1 = leaf.getLeaf().node.x;
                int y1 = leaf.getLeaf().node.y;
                this.cell = Algorithm.searchPath(x1, y1, x0, y0);
                if (this.cell != null) {
                    defineAngle(this.cell.getCell());
                    if (id != 0) {
                        System.out.println("df2: " + unit.getAngle());
                    }
                }
            }
            return;
        }
        grid.setNullUnit(unit);
        if (grid.isAbsWalkable(unit)) {
            defineAngle(cell);
            unit.update();
            if (id != 0) {
                System.out.println("f1: " + unit.getAngle());
            }
        } else {
            if (grid.isIgnoreWalkable(unit)) {
                manageLets();
                if (id != 0) {
                    System.out.println("f2: " + unit.getAngle());
                }
            } else {
                reDef();
                if (id != 0) {
                    System.out.println("f3: " + unit.getAngle());
                }
            }
        }
        grid.setUnit(unit);
    }

    private double dist(int x, int y) {
        int x0 = (int) (unit.getX() / grid.cSize);
        int y0 = (int) (unit.getY() / grid.cSize);
        return Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
    }

    private void manageLets() {
        for (Long l : grid.getImpedingUnitIds(unit)) {
            UnitManager manager = MovingManager.getManager(l);
            Unit mu = grid.getUnit(manager.getCell());
            if (id != 1) {
                if (manager.getCell() != null) {
                    System.out.println("cell: " + manager.getCell().key);
                } else {
                    System.out.println("cell: null");
                }
            }
            if ((manager.getCell() == null) || (mu == unit)) {
                if (id != 1) {
                    System.out.println("ml: " + manager.unit.getAngle());
                    System.out.println("unit: " + mu);
                }
                manager.setPriority(priority);
                int x = (int) (manager.unit.getX() / grid.cSize);
                int y = (int) (manager.unit.getY() / grid.cSize);
                double max = 0;
                Cell c = null;
                for (int i = x - 1; i < x + 2; i++) {
                    for (int j = y - 1; j < y + 2; j++) {
                        double d = dist(i, j);
                        if ((d > max) && (grid.isIgnoreWalkable(i, j, manager.unit))
                                && (grid.isRelWalkable(manager.unit.getX(),
                                        manager.unit.getY(),
                                        manager.unit.getSize()))) {
                            max = d;
                            c = new Cell(i, j);
                        }
                    }
                }
                manager.addAim(c);
            }
        }
    }

    private void defineAngle(Cell c) {
        if (c == null) {
            return;
        }
        double ex = c.x * grid.cSize + grid.cSize / 2;
        double ey = c.y * grid.cSize + grid.cSize / 2;
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

    private void defPath() {
        Graph.setUnitSize(unit.getSize());

        int size = unit.getSize();
        int ux = (int) (unit.getX() / grid.cSize);
        int uy = (int) (unit.getY() / grid.cSize);

        int r = (int) Math.sqrt((aim.x - ux) * (aim.x - ux)
                + (aim.y - uy) * (aim.x - uy));
        if (r < grid.fieldSize) {
            this.cell = Algorithm.searchPath(aim.x, aim.y, ux, uy);
            if (this.cell != null) {
                defineAngle(this.cell.getCell());
            }
            return;
        }
        Field fs = grid.getField(ux, uy);
        Field fe = grid.getField(aim.x, aim.y);
        Node n1 = fs.addTempNode(ux, uy, size);
        Node n2 = fe.addTempNode(aim.x, aim.y, size);
        fs.formEdges();
        fe.formEdges();
        this.leaf = Graph.searchPath(n2, n1);
        fs.removeTemp();
        fe.removeTemp();
        Algorithm.setUnitSize(size);
        if (leaf != null) {
            int x0 = leaf.node.x;
            int y0 = leaf.node.y;
            int x = leaf.getLeaf().node.x;
            int y = leaf.getLeaf().node.y;
            this.cell = Algorithm.searchPath(x, y, x0, y0, fs);
            if (this.cell != null) {
                defineAngle(this.cell.getCell());
                if (id != 0) {
                    System.out.println("dp: " + unit.getAngle());
                }
            }
        }
    }

    private void reDef() {
        Graph.setUnitSize(unit.getSize());

        int size = unit.getSize();
        int ux = (int) (unit.getX() / grid.cSize);
        int uy = (int) (unit.getY() / grid.cSize);

        int r = (int) Math.sqrt((aim.x - ux) * (aim.x - ux)
                + (aim.y - uy) * (aim.x - uy));
        if (r < grid.fieldSize) {
            this.cell = Algorithm.searchPath(aim.x, aim.y, ux, uy, unit);
            if (this.cell != null) {
                defineAngle(this.cell.getCell());
            }
            return;
        }

        Field fs = grid.getField(ux, uy);
        Field fe = grid.getField(aim.x, aim.y);
        Node n1 = fs.addTempNode(ux, uy, size);
        Node n2 = fe.addTempNode(aim.x, aim.y, size);
        fs.formEdges();
        fe.formEdges();
        this.leaf = Graph.searchPath(n2, n1);
        fs.removeTemp();
        fe.removeTemp();
        Algorithm.setUnitSize(size);
        if (leaf != null) {
            int x0 = leaf.node.x;
            int y0 = leaf.node.y;
            int x = leaf.getLeaf().node.x;
            int y = leaf.getLeaf().node.y;
            this.cell = Algorithm.searchPath(x, y, x0, y0, fs, unit);
            if (this.cell != null) {
                defineAngle(this.cell.getCell());
            }
        }
    }

}
