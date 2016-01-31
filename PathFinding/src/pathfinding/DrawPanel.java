/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import advancedpathfinding.Algorithm;
import advancedpathfinding.Cell;
import advancedpathfinding.Graph;
import grid.Grid;
import advancedpathfinding.Leaf;
import controllers.MovingManager;
import controllers.UnitManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import td.gamefield.GroundType;
import td.gamefield.Mesh;
import td.unit.Unit;

/**
 *
 * @author Sokolov@ivc.org
 */
public class DrawPanel extends JComponent implements Runnable {

    private static final int SIZE = 32;
    private static final int FIELD_SIZE = 8;
    private static final int LEVEL = 5;

    public final Grid grid = new Grid(SIZE, FIELD_SIZE, 600);
    public int size = 1;
    public volatile Cell aim;

    private long calcTime;
    private long loadTime;

    private Unit gu;
    private Unit u;
    private UnitManager manager;

    private volatile boolean isLoad = false;

    public DrawPanel() {
        super();
        u = new Unit(12 * grid.cellSize + grid.cellSize / 2, 12 * grid.cellSize + grid.cellSize / 2, 1);
        gu = new Unit(10 * grid.cellSize + grid.cellSize / 2, 12 * grid.cellSize + grid.cellSize / 2, 2);
        //u = new Unit(12 * grid.cellSize + grid.cellSize / 2, 12 * grid.cellSize + grid.cellSize / 2, 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isLoad) {
                    repaint();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        Mesh map[][] = new Mesh[SIZE][SIZE];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if ((new Random()).nextInt(10) < 2) {
                    int r = (new Random()).nextInt(3);
                    switch (r) {
                        case 0:
                            map[i][j] = new Mesh(GroundType.FOREST, 0, 0);
                            break;
                        case 1:
                            map[i][j] = new Mesh(GroundType.ROCKS, 0, 0);
                            break;
                        case 2:
                            map[i][j] = new Mesh(GroundType.WATER, 0, 0);
                            break;
                    }
                } else {
                    map[i][j] = new Mesh(GroundType.GROUND, 0, 0);
                }
            }
        }

        map[10][12] = new Mesh(GroundType.GROUND, 0, 0);
        map[11][12] = new Mesh(GroundType.GROUND, 0, 0);
        map[10][13] = new Mesh(GroundType.GROUND, 0, 0);
        map[11][13] = new Mesh(GroundType.GROUND, 0, 0);

        map[12][12] = new Mesh(GroundType.GROUND, 0, 0);

        grid.setMap(map);
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lt = System.currentTimeMillis();
        grid.generating(LEVEL);
        Algorithm.setGrid(grid);
        this.loadTime = System.currentTimeMillis() - lt;
        isLoad = true;
        MovingManager.setGrid(grid);
        manager = MovingManager.addUnit(u);
        MovingManager.addUnit(gu);
        while (true) {
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(DrawPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            long t = System.currentTimeMillis();
            u.setSize(size);
            grid.setNullUnit(u);
            if (aim != null) {
                manager.addAim(aim);
                aim = null;
            }
            // manager.execute();
            MovingManager.execute();
            this.calcTime = System.currentTimeMillis() - t;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (!isLoad) {
            g2d.drawString("Загрузка: " + 100 * Grid.progress / Grid.max + " %", 220, 620);
            return;
        }
        if (aim != null) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(aim.x * grid.cellSize - 1, aim.y * grid.cellSize - 1, grid.cellSize + 2, grid.cellSize + 2);
        }

        BasicStroke pen1;
        pen1 = new BasicStroke(1);
        g2d.setStroke(pen1);
        grid.draw(g2d, size);

        Cell curCell = manager.getCell();
        while (curCell != null) {
            pen1 = new BasicStroke(1);
            g2d.setStroke(pen1);
            g2d.setColor(Color.RED);
            g2d.drawRect(curCell.x * grid.cellSize + 2, curCell.y * grid.cellSize + 2, grid.cellSize - 4, grid.cellSize - 4);
            curCell = curCell.getCell();
        }

        Leaf curLeaf = manager.getLeaf();
        Leaf tmp = null;

        while (curLeaf != null) {
            tmp = curLeaf;
            if (curLeaf.getLeaf() != null) {
                Cell c;
                if (manager.getLeaf() != null) {
                    int x0 = curLeaf.node.x;
                    int y0 = curLeaf.node.y;
                    int x = curLeaf.getLeaf().node.x;
                    int y = curLeaf.getLeaf().node.y;
                    c = Algorithm.searchPath(x, y, x0, y0, size);
                    while (c != null) {
                        pen1 = new BasicStroke(1);
                        g2d.setStroke(pen1);
                        g2d.setColor(Color.RED);
                        g2d.drawRect(c.x * grid.cellSize + 2, c.y * grid.cellSize + 2, grid.cellSize - 4, grid.cellSize - 4);
                        c = c.getCell();
                    }
                }
                pen1 = new BasicStroke(2);
                g2d.setStroke(pen1);
                g2d.setColor(Color.ORANGE);
                g2d.drawLine(curLeaf.getLeaf().node.x * grid.cellSize + grid.cellSize / 2,
                        curLeaf.getLeaf().node.y * grid.cellSize + grid.cellSize / 2,
                        tmp.node.x * grid.cellSize + grid.cellSize / 2,
                        tmp.node.y * grid.cellSize + grid.cellSize / 2);
            }
            curLeaf = curLeaf.getLeaf();
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("Время расчёта: " + (float) calcTime / 1000 + "сек.", 620, 55);
        g2d.drawString("Время предрасчёта путей: " + (float) loadTime / 1000 + " сек.", 220, 620);
        MovingManager.drawUnits(g2d);
    }

}
