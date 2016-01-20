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
import td.unit.Unit;

/**
 *
 * @author Sokolov@ivc.org
 */
public class Algorithm {

    private static final double INFINITY = 10e6;

    private static Grid grid;

    private static int unitSize = 1;

    public static void setGrid(Grid grid) {
        Algorithm.grid = grid;
    }

    public static void setUnitSize(int unitSize) {
        Algorithm.unitSize = unitSize;
    }

    private Algorithm() {
    }

    /**
     * Ищет путь между точками (x0, y0) и (x, y)и возвращает связанные точки
     * начиная от ячейки с координатами(x, y). Алгоритм поиска - А*
     *
     * @param x0 int
     * @param y0 int
     * @param x int
     * @param y int
     * @return Cell
     */
    public static Cell searchPath(int x0, int y0, int x, int y) {
        if (grid == null) {
            throw new IllegalStateException("Algorithm не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        Map<String, Cell> openPoints = new LinkedHashMap<>();
        Map<String, Cell> closePoints = new LinkedHashMap<>();
        Cell newCell = new Cell(x0, y0);
        Cell curCell;
        newCell.calcH(x, y);
        newCell.calcF();
        openPoints.put(newCell.key, newCell);
        boolean complete = false;
        while ((openPoints.size() > 0) && (!complete)) {
            curCell = Algorithm.findPointByFmin(openPoints);
            String key = curCell.key;
            closePoints.put(key, curCell);
            openPoints.remove(key);
            for (Cell iter : getBorder(curCell)) {
                int i = iter.x;
                int j = iter.y;
                int manageValue = Algorithm.manageLoopByMap(i, j, curCell,
                        complete);
                if (manageValue == 1) {
                    continue;
                }
                if (manageValue == -1) {
                    break;
                }
                complete = (i == x) && (j == y);
                Algorithm.manageLoopByPoints(i, j, x, y, curCell,
                        openPoints, closePoints);
            }
        }

        String key = Integer.toString(x) + "|" + Integer.toString(y);
        curCell = openPoints.get(key);
        openPoints.clear();
        closePoints.clear();
        return curCell;
    }

    /**
     * Ищет путь между точками (x0, y0) и (x, y)и возвращает связанные точки
     * начиная от ячейки с координатами(x, y). Алгоритм поиска - А*. Параметры
     * left, top, right, bottom - ограничения на зону поиска
     *
     * @param x0 int
     * @param y0 int
     * @param x int
     * @param y int
     * @param f Field
     * @return Cell
     */
    public static Cell searchPath(int x0, int y0, int x, int y, Field f) {
        if (grid == null) {
            throw new IllegalStateException("Algorithm не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        Map<String, Cell> openPoints = new LinkedHashMap<>();
        Map<String, Cell> closePoints = new LinkedHashMap<>();
        Cell newCell = new Cell(x0, y0);
        Cell curCell;
        newCell.calcH(x, y);
        newCell.calcF();
        openPoints.put(newCell.key, newCell);
        boolean complete = false;
        while ((openPoints.size() > 0) && (!complete)) {
            curCell = Algorithm.findPointByFmin(openPoints);
            String key = curCell.key;
            closePoints.put(key, curCell);
            openPoints.remove(key);
            for (Cell iter : getBorder(curCell)) {
                int i = iter.x;
                int j = iter.y;
                int manageValue = Algorithm.manageLoopByMap(i, j, curCell,
                        complete, f);
                if (manageValue == 1) {
                    continue;
                }
                if (manageValue == -1) {
                    break;
                }
                complete = (i == x) && (j == y);
                Algorithm.manageLoopByPoints(i, j, x, y, curCell,
                        openPoints, closePoints);
            }
        }

        String key = Integer.toString(x) + "|" + Integer.toString(y);
        curCell = openPoints.get(key);
        openPoints.clear();
        closePoints.clear();
        return curCell;
    }

    /**
     * Ищет путь между точками (x0, y0) и (x, y) с учётом юнитов и возвращает
     * связанные точки начиная от ячейки с координатами(x, y). Алгоритм поиска -
     * А*. Параметр Unit - какой юнит осуществляет поиск. Во время поиска
     * игнорируются юниты со значением исполняемого приоритета выше чем у
     * заданного юнита
     *
     * @param x0 int
     * @param y0 int
     * @param x int
     * @param y int
     * @param u Unit
     * @return Cell
     */
    public static Cell searchPath(int x0, int y0, int x, int y, Unit u) {
        if (grid == null) {
            throw new IllegalStateException("Algorithm не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        Map<String, Cell> openPoints = new LinkedHashMap<>();
        Map<String, Cell> closePoints = new LinkedHashMap<>();
        Cell newCell = new Cell(x0, y0);
        Cell curCell;
        newCell.calcH(x, y);
        newCell.calcF();
        openPoints.put(newCell.key, newCell);
        boolean complete = false;
        while ((openPoints.size() > 0) && (!complete)) {
            curCell = Algorithm.findPointByFmin(openPoints);
            String key = curCell.key;
            closePoints.put(key, curCell);
            openPoints.remove(key);
            for (Cell iter : getBorder(curCell)) {
                int i = iter.x;
                int j = iter.y;
                int manageValue = Algorithm.manageLoopByMap(i, j, curCell,
                        complete, u);
                if (manageValue == 1) {
                    continue;
                }
                if (manageValue == -1) {
                    break;
                }
                complete = (i == x) && (j == y);
                Algorithm.manageLoopByPoints(i, j, x, y, curCell,
                        openPoints, closePoints);
            }
        }

        String key = Integer.toString(x) + "|" + Integer.toString(y);
        curCell = openPoints.get(key);
        openPoints.clear();
        closePoints.clear();
        return curCell;
    }

    /**
     * Ищет путь между точками (x0, y0) и (x, y)и возвращает связанные точки
     * начиная от ячейки с координатами(x, y). Алгоритм поиска - А*. Параметры
     * left, top, right, bottom - ограничения на зону поиска. Параметр Unit -
     * какой юнит осуществляет поиск. Во время поиска игнорируются юниты со
     * значением исполняемого приоритета выше чем у заданного юнита
     *
     * @param x0 int
     * @param y0 int
     * @param x int
     * @param y int
     * @param f Field
     * @param u Unit
     * @return Cell
     */
    public static Cell searchPath(int x0, int y0, int x, int y, Field f, Unit u) {
        if (grid == null) {
            throw new IllegalStateException("Algorithm не готов для работы, "
                    + "задайте ему поле grid при помощи метода setGrid(Grid grid)");
        }
        Map<String, Cell> openPoints = new LinkedHashMap<>();
        Map<String, Cell> closePoints = new LinkedHashMap<>();
        Cell newCell = new Cell(x0, y0);
        Cell curCell;
        newCell.calcH(x, y);
        newCell.calcF();
        openPoints.put(newCell.key, newCell);
        boolean complete = false;
        while ((openPoints.size() > 0) && (!complete)) {
            curCell = Algorithm.findPointByFmin(openPoints);
            String key = curCell.key;
            closePoints.put(key, curCell);
            openPoints.remove(key);
            for (Cell iter : getBorder(curCell)) {
                int i = iter.x;
                int j = iter.y;
                int manageValue = Algorithm.manageLoopByMap(i, j, curCell,
                        complete, f, u);
                if (manageValue == 1) {
                    continue;
                }
                if (manageValue == -1) {
                    break;
                }
                complete = (i == x) && (j == y);
                Algorithm.manageLoopByPoints(i, j, x, y, curCell,
                        openPoints, closePoints);
            }
        }

        String key = Integer.toString(x) + "|" + Integer.toString(y);
        curCell = openPoints.get(key);
        openPoints.clear();
        closePoints.clear();
        return curCell;
    }

    private static Cell findPointByFmin(Map<String, Cell> list) {
        Cell current = null;
        double fMin = INFINITY;
        for (Map.Entry<String, Cell> entry : list.entrySet()) {
            if (entry.getValue().getF() < fMin) {
                fMin = entry.getValue().getF();
                current = entry.getValue();
            }
        }
        return current;
    }

    private static boolean canWalk(int i, int j, Cell cell) {
        if (Math.abs(cell.x - i) + Math.abs(cell.y - j) > 1) {
            return (grid.map[i][j].getGroundValue() >= unitSize)
                    && (grid.map[cell.x][j].getGroundValue() >= unitSize)
                    && (grid.map[i][cell.y].getGroundValue() >= unitSize);
        } else {
            return grid.map[i][j].getGroundValue() >= unitSize;
        }
    }

    private static boolean canWalk(int i, int j, Cell cell, Unit u) {
        if (Math.abs(cell.x - i) + Math.abs(cell.y - j) > 1) {
            return (grid.map[i][j].getGroundValue() >= unitSize)
                    && (grid.map[cell.x][j].getGroundValue() >= unitSize)
                    && (grid.map[i][cell.y].getGroundValue() >= unitSize)
                    && grid.isIgnoreWalkable(i, j, u)
                    && grid.isIgnoreWalkable(cell.x, j, u)
                    && grid.isIgnoreWalkable(i, cell.y, u);
        } else {
            return (grid.map[i][j].getGroundValue() >= unitSize)
                    && grid.isIgnoreWalkable(i, j, u);
        }
    }

    private static int manageLoopByMap(int i, int j, Cell cell,
            boolean complete, Unit u) {
        if ((!canWalk(i, j, cell, u)) || ((cell.x == i) && (cell.y == j))) {
            return 1;
        } else if (!complete) {
            return 0;
        } else {
            return -1;
        }
    }

    private static int manageLoopByMap(int i, int j, Cell cell,
            boolean complete) {
        if (!canWalk(i, j, cell)) {
            return 1;
        }
        if ((cell.x == i) && (cell.y == j)) {
            return 1;
        } else if (!complete) {
            return 0;
        } else {
            return -1;
        }
    }

    private static int manageLoopByMap(int i, int j, Cell cell,
            boolean complete, Field f) {
        if (!((i <= f.right) && (i >= f.left)
                && (j >= f.top) && (j <= f.bottom))) {
            return 1;
        }
        if ((!canWalk(i, j, cell)) || ((cell.x == i) && (cell.y == j))) {
            return 1;
        } else if (!complete) {
            return 0;
        } else {
            return -1;
        }
    }

    private static int manageLoopByMap(int i, int j, Cell cell,
            boolean complete, Field f, Unit u) {
        if (!((i <= f.right) && (i >= f.left)
                && (j >= f.top) && (j <= f.bottom))) {
            return 1;
        }
        if ((!canWalk(i, j, cell, u)) || ((cell.x == i) && (cell.y == j))) {
            return 1;
        } else if (!complete) {
            return 0;
        } else {
            return -1;
        }
    }

    private static void manageLoopByPoints(int i, int j, int x, int y,
            Cell curCell, Map<String, Cell> openList,
            Map<String, Cell> closeList) {
        String key = Integer.toString(i) + "|" + Integer.toString(j);
        double step;
        if (closeList.containsKey(key)) {
            return;
        }
        if (openList.containsKey(key)) {
            Cell tmp = openList.get(key);
            step = ((curCell.x == tmp.x)
                    || (curCell.y == tmp.y)) ? 1 : 1.41;
            if (tmp.getG() > curCell.getG() + step) {
                tmp.setCell(curCell);
                tmp.setG(curCell.getG() + step);
                tmp.calcF();
            }
        } else {
            step = ((curCell.x == i) || (curCell.y == j)) ? 1 : 1.41;
            Cell newPoint = new Cell(i, j);
            newPoint.setCell(curCell);
            newPoint.setG(curCell.getG() + step);
            newPoint.calcH(x, y);
            newPoint.calcF();
            openList.put(key, newPoint);
        }
    }

    private static List<Cell> getBorder(Cell cell) {
        int minI = (cell.x - 1) < 0 ? 0 : cell.x - 1;
        int minJ = (cell.y - 1) < 0 ? 0 : cell.y - 1;
        int maxI = (cell.x + 2) > grid.map.length ? grid.map.length
                : cell.x + 2;
        int maxJ = (cell.y + 2) > grid.map.length ? grid.map.length
                : cell.y + 2;
        List<Cell> cells = new ArrayList<>();
        for (int i = minI; i < maxI; i++) {
            cells.add(new Cell(i, minJ));

        }
        for (int j = minJ + 1; j < maxJ; j++) {
            cells.add(new Cell(maxI - 1, j));
        }
        for (int i = maxI - 2; i >= minI; i--) {
            cells.add(new Cell(i, maxJ - 1));

        }
        for (int j = maxJ - 1; j > minJ; j--) {
            cells.add(new Cell(minI, j));
        }
        return cells;
    }

}
