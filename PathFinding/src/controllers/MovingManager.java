package controllers;

import grid.Grid;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import units.Unit;

/**
 *
 * @author Хозяин
 */
public class MovingManager {

    private static boolean ready = false;

    private static final Map<UnitManager, Unit> units = new HashMap<>();
    private static final Map<Long, UnitManager> managers = new HashMap<>();

    private static Grid grid = null;

    private MovingManager() {
    }

    public static boolean isReady() {
        return ready;
    }

    public static void setGrid(Grid grid) {
        MovingManager.grid = grid;
        MovingManager.ready = true;
    }

    public static UnitManager addUnit(Unit u) {
        if (grid == null) {
            throw new IllegalStateException("Поле grid не задано");
        }
        UnitManager manager = new UnitManager(u, grid);
        units.put(manager, u);
        managers.put(manager.id, manager);
        return manager;
    }

    public static void execute() {
        for (UnitManager manager : units.keySet()) {
            manager.execute();
        }
    }

    public static void drawUnits(Graphics2D g2d) {
        for (UnitManager manager : units.keySet()) {
            units.get(manager).draw(g2d, grid);
        }
    }

    /**
     * Может ли u2 игнорировать u1. Сравнивает исполняемые приоритеты для 2-х
     * юнитов. Возвращает true, если значение поля priority ключа первого
     * параметра больше чем у второго. В случае равенства сравниваются id
     * параметров, если у u1 поле id больше, то возвращается true. Также
     * возвращается true, если один из параметров null
     *
     * @param u1 Unit
     * @param u2 Unit
     * @return boolean
     */
    public static boolean isIgnore(Unit u1, Unit u2) {
        if ((u1 == null) || (u2 == null)) {
            return true;
        }
        UnitManager m1 = managers.get(u1.id);
        UnitManager m2 = managers.get(u2.id);
        if (m1.getPriority() == m2.getPriority()) {
            return u1.id >= u2.id;
        }
        return m1.getPriority() > m2.getPriority();
    }

    public static UnitManager getManager(Long id) {
        return managers.get(id);
    }

}
