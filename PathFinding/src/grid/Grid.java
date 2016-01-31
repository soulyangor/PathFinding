/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid;

import advancedpathfinding.Cell;
import advancedpathfinding.Node;
import controllers.MovingManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import td.gamefield.GroundType;
import td.gamefield.Mesh;
import td.unit.Unit;

/**
 *
 * @author Sokolov@ivc.org
 */
public class Grid {

    public static int progress = 0;
    public static int max = 1;

    public final AdvancedMesh map[][];
    public final int cellSize;
    public final Field fields[][];
    public final int fieldSize;

    private final int fCount;

    public Grid(int mapSize, int fieldSize, int displaySize) {
        this.map = new AdvancedMesh[mapSize][mapSize];
        this.fieldSize = fieldSize;
        this.fCount = mapSize / fieldSize;
        this.fields = new Field[fCount][fCount];
        this.cellSize = displaySize / mapSize;
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                this.map[i][j] = new AdvancedMesh(new Mesh(GroundType.GROUND, 0, 0));
            }
        }
        for (int fi = 0; fi < fCount; fi++) {
            for (int fj = 0; fj < fCount; fj++) {
                this.fields[fi][fj] = new Field(fi * fieldSize, fj * fieldSize,
                        (fi + 1) * fieldSize - 1, (fj + 1) * fieldSize - 1);
            }
        }
        Node.setGrid(this);
    }

    public Unit getUnit(Cell cell) {
        return cell == null ? null : map[cell.x][cell.y].getUnit();
    }

    public Field getField(int i, int j) {
        return fields[i / fieldSize][j / fieldSize];
    }

    public void setMap(Mesh m[][]) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (((m[i][j].getType() == GroundType.FOREST)
                        || (m[i][j].getType() == GroundType.ROCKS)
                        || (m[i][j].getType() == GroundType.WATER)) && (i < m.length) && (j < m[i].length)) {
                    map[i][j] = new AdvancedMesh(m[i][j]);
                }
            }
        }
    }

    public void generating(int level) {
        for (int l = 2; l <= level; l++) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map.length; j++) {
                    if (isWalkability(i, j, l)) {
                        map[i][j].setGroundValue(l);
                    }
                }
            }
        }
        for (int l = 1; l <= level; l++) {
            for (int fi = 0; fi < fCount; fi++) {
                for (int fj = 0; fj < fCount; fj++) {
                    if (fj % 2 == 0) {
                        horizontalNodes(l, fi, fj);
                    }
                    if (fi % 2 == 0) {
                        verticalNodes(l, fi, fj);
                    }
                }
            }
        }

        Grid.max = fCount * fCount;

        for (int fi = 0; fi < fCount; fi++) {
            for (int fj = 0; fj < fCount; fj++) {
                fields[fi][fj].formEdges();
                progress++;
            }
        }
    }

    public void setUnit(Unit u) {
        int i = (int) (u.getX() / cellSize);
        int j = (int) (u.getY() / cellSize);
        for (int ii = i; ii < i + u.getSize(); ii++) {
            for (int ij = j; ij < j + u.getSize(); ij++) {
                map[ii][ij].setUnit(u);
            }
        }
    }

    public void setNullUnit(Unit u) {
        int i = (int) (u.getX() / cellSize);
        int j = (int) (u.getY() / cellSize);
        for (int ii = i; ii < i + u.getSize(); ii++) {
            for (int ij = j; ij < j + u.getSize(); ij++) {
                if (map[ii][ij].getUnit() == u) {
                    map[ii][ij].setUnit(null);
                }
            }
        }
    }

    public boolean isRelWalkable(Cell cell, int size) {
        return isRelWalkable(cell.x, cell.y, size);
    }

    public boolean isRelWalkable(int i, int j, int size) {
        return map[i][j].getGroundValue() >= size;
    }

    public boolean isRelWalkable(double x, double y, int size) {
        int i = (int) (x / cellSize);
        int j = (int) (y / cellSize);
        return map[i][j].getGroundValue() >= size;
    }

    public boolean isIgnoreWalkable(Cell cell, Unit u) {
        return isIgnoreWalkable(cell.x, cell.y, u);
    }

    public boolean isIgnoreWalkable(int x, int y, Unit u) {
        for (int i = x; i < x + u.getSize(); i++) {
            for (int j = y; j < y + u.getSize(); j++) {
                if ((!MovingManager.isIgnore(map[i][j].getUnit(), u))
                        || (!isRelWalkable(x, y, u.getSize()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isIgnoreWalkable(Unit u) {
        double ux = u.getX() + cellSize * Math.cos(u.getAngle()) / 2;
        double uy = u.getY() + cellSize * Math.sin(u.getAngle()) / 2;
        int x = (int) (ux / cellSize);
        int y = (int) (uy / cellSize);
        for (int i = x; i < x + u.getSize(); i++) {
            for (int j = y; j < y + u.getSize(); j++) {
                if (!MovingManager.isIgnore(map[i][j].getUnit(), u)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Возвращает список id объектов, мешающих движению объекта u
     *
     * @param u Unit
     * @return List<Long>
     */
    public List<Long> getImpedingUnitIds(Unit u) {
        List<Long> unitIds = new ArrayList<>();
        double ux = u.getX() + cellSize * Math.cos(u.getAngle()) / 2;
        double uy = u.getY() + cellSize * Math.sin(u.getAngle()) / 2;
        int x = (int) (ux / cellSize);
        int y = (int) (uy / cellSize);
        for (int i = x; i < x + u.getSize(); i++) {
            for (int j = y; j < y + u.getSize(); j++) {
                if ((map[i][j].getUnit() != null) && (map[i][j].getUnit() != u)) {
                    unitIds.add(map[i][j].getUnit().id);
                }
            }
        }
        return unitIds;
    }

    public boolean isAbsWalkable(Unit unit) {
        double x = unit.getX() + cellSize * Math.cos(unit.getAngle()) / 2;
        double y = unit.getY() + cellSize * Math.sin(unit.getAngle()) / 2;
        if (!isRelWalkable(x, y, unit.getSize())) {
            return false;
        }
        int i = (int) (x / cellSize);
        int j = (int) (y / cellSize);
        for (int ii = i; ii < i + unit.getSize(); ii++) {
            for (int ij = j; ij < j + unit.getSize(); ij++) {
                if (map[ii][ij].getUnit() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void draw(Graphics2D g2d, int level) {
        for (int i = 0; i < map.length; i++) {
            if ((i + 1) * cellSize > 600) {
                break;
            }
            for (int j = 0; j < map.length; j++) {
                if ((j + 1) * cellSize > 600) {
                    break;
                }
                if (map[i][j].getGroundValue() < level) {
                    if (map[i][j].getGroundValue() == 0) {
                        switch (map[i][j].getType()) {
                            case ROCKS:
                                g2d.setColor(Color.DARK_GRAY);
                                break;
                            case WATER:
                                g2d.setColor(Color.BLUE);
                                break;
                            case FOREST:
                                g2d.setColor(new Color(0, 105, 0));
                                break;
                        }
                    } else {
                        g2d.setColor(Color.PINK);
                    }
                    g2d.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    private void horizontalNodes(int level, int fi, int fj) {
        int flag1 = -1;
        int flag2 = -1;
        Field f = fields[fi][fj];
        int left = f.left;
        int right = f.right;
        int top = f.top;
        int bottom = f.bottom;
        for (int i = left; i <= right; i++) {
            if ((top > 0) && (map[i][top].getGroundValue() >= level)
                    && (map[i][top - 1].getGroundValue()) >= level) {
                if (i == right) {
                    if (flag1 >= 0) {
                        Node n1 = f.addNode((i + flag1) / 2, top, level);
                        Node n2 = fields[fi][fj - 1].addNode((i + flag1) / 2, top - 1, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    } else {
                        Node n1 = f.addNode(i, top, level);
                        Node n2 = fields[fi][fj - 1].addNode(i, top - 1, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    }
                }
                if (flag1 < 0) {
                    flag1 = i;
                }
            } else if ((top > 0) && (flag1 >= 0)) {
                Node n1 = f.addNode((i + flag1) / 2, top, level);
                Node n2 = fields[fi][fj - 1].addNode((i + flag1) / 2, top - 1, level);
                flag1 = -1;
                n1.addNode(n2);
                n2.addNode(n1);
            }
            if ((bottom + 1 < map.length) && (map[i][bottom].getGroundValue() >= level)
                    && (map[i][bottom + 1].getGroundValue() >= level)) {
                if (i == right) {
                    if (flag2 >= 0) {
                        Node n1 = f.addNode((i + flag2) / 2, bottom, level);
                        Node n2 = fields[fi][fj + 1].addNode((i + flag2) / 2, bottom + 1, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    } else {
                        Node n1 = f.addNode(i, bottom, level);
                        Node n2 = fields[fi][fj + 1].addNode(i, bottom + 1, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    }
                }
                if (flag2 < 0) {
                    flag2 = i;
                }
            } else if ((bottom + 1 < map.length) && (flag2 >= 0)) {
                Node n1 = f.addNode((i + flag2) / 2, bottom, level);
                Node n2 = fields[fi][fj + 1].addNode((i + flag2) / 2, bottom + 1, level);
                flag2 = -1;
                n1.addNode(n2);
                n2.addNode(n1);
            }
        }
    }

    private void verticalNodes(int level, int fi, int fj) {
        int flag1 = -1;
        int flag2 = -1;
        Field f = fields[fi][fj];
        int left = f.left;
        int right = f.right;
        int top = f.top;
        int bottom = f.bottom;
        for (int j = top; j <= bottom; j++) {
            if ((left > 0) && (map[left][j].getGroundValue() >= level)
                    && (map[left - 1][j].getGroundValue()) >= level) {
                if (j == bottom) {
                    if (flag1 >= 0) {
                        Node n1 = f.addNode(left, (j + flag1) / 2, level);
                        Node n2 = fields[fi - 1][fj].addNode(left - 1, (j + flag1) / 2, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    } else {
                        Node n1 = f.addNode(left, j, level);
                        Node n2 = fields[fi - 1][fj].addNode(left - 1, j, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    }
                }
                if (flag1 < 0) {
                    flag1 = j;
                }
            } else if ((left > 0) && (flag1 >= 0)) {
                Node n1 = f.addNode(left, (j + flag1) / 2, level);
                Node n2 = fields[fi - 1][fj].addNode(left - 1, (j + flag1) / 2, level);
                flag1 = -1;
                n1.addNode(n2);
                n2.addNode(n1);
            }
            if ((right + 1 < map.length) && (map[right][j].getGroundValue() >= level)
                    && (map[right + 1][j].getGroundValue()) >= level) {
                if (j == bottom) {
                    if (flag2 >= 0) {
                        Node n1 = f.addNode(right, (j + flag2) / 2, level);
                        Node n2 = fields[fi + 1][fj].addNode(right + 1, (j + flag2) / 2, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    } else {
                        Node n1 = f.addNode(right, j, level);
                        Node n2 = fields[fi + 1][fj].addNode(right + 1, j, level);
                        n1.addNode(n2);
                        n2.addNode(n1);
                    }
                }
                if (flag2 < 0) {
                    flag2 = j;
                }
            } else if ((right + 1 < map.length) && (flag2 >= 0)) {
                Node n1 = f.addNode(right, (j + flag2) / 2, level);
                Node n2 = fields[fi + 1][fj].addNode(right + 1, (j + flag2) / 2, level);
                flag2 = -1;
                n1.addNode(n2);
                n2.addNode(n1);
            }
        }
    }

    private boolean isWalkability(int i, int j, int size) {
        if (map[i][j].getGroundValue() == 0) {
            return false;
        }
        if ((map.length < i + size) || (map[0].length < j + size)) {
            return false;
        }
        for (int ii = i; ii < i + size; ii++) {
            for (int ij = j; ij < j + size; ij++) {
                if (map[ii][ij].getGroundValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
