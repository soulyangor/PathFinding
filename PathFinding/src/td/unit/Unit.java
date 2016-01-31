/*
 Информационно-вычислительный центр
 космодрома Байконур
 */
package td.unit;

import grid.Grid;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author rmo
 */
public class Unit {

    public final long id;

    private static long counter = 0;

    private double x;
    private double y;
    private double angle;
    private int size;

    public Unit(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.angle = 0;
        this.id = nextId();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void update() {
        x += Math.cos(angle);
        y += Math.sin(angle);
    }

    public void draw(Graphics2D g2d, Grid grid) {
        double displayX = x - grid.cellSize / 2;
        double displayY = y - grid.cellSize / 2;
        BasicStroke pen1 = new BasicStroke(2);
        g2d.setStroke(pen1);
        g2d.setColor(Color.MAGENTA);
        g2d.fillOval((int) displayX, (int) displayY, size * grid.cellSize, size * grid.cellSize);
        g2d.setColor(Color.BLACK);
        g2d.drawOval((int) displayX, (int) displayY, size * grid.cellSize, size * grid.cellSize);
        drawArrow(g2d, grid);
    }

    private long nextId() {
        return counter++;
    }

    private void drawArrow(Graphics2D g2d, Grid grid) {
        double cx;
        double cy;
        if (size % 2 == 0) {
            int inc = size / 2 - 1;
            cx = x + inc * grid.cellSize + grid.cellSize / 2;
            cy = y + inc * grid.cellSize + grid.cellSize / 2;
        } else {
            int inc = size / 2;
            cx = x + inc * grid.cellSize;
            cy = y + inc * grid.cellSize;
        }
        int x1 = (int) (cx - (size * grid.cellSize / 2 - 1) * Math.cos(angle));
        int x2 = (int) (cx + (size * grid.cellSize / 2 - 1) * Math.cos(angle));
        int y1 = (int) (cy - (size * grid.cellSize / 2 - 1) * Math.sin(angle));
        int y2 = (int) (cy + (size * grid.cellSize / 2 - 1) * Math.sin(angle));

        int lx = (int) (cx + (size * grid.cellSize / 2 - 5) * Math.cos(angle + Math.PI / 12));
        int rx = (int) (cx + (size * grid.cellSize / 2 - 5) * Math.cos(angle - Math.PI / 12));

        int ly = (int) (cy + (size * grid.cellSize / 2 - 5) * Math.sin(angle + Math.PI / 12));
        int ry = (int) (cy + (size * grid.cellSize / 2 - 5) * Math.sin(angle - Math.PI / 12));

        BasicStroke pen1 = new BasicStroke(1);
        g2d.setStroke(pen1);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x1, y1, x2, y2);
        g2d.drawLine(lx, ly, x2, y2);
        g2d.drawLine(rx, ry, x2, y2);
    }

}
