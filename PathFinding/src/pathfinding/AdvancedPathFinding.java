/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import advancedpathfinding.Cell;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;

/**
 *
 * @author Sokolov@ivc.org
 */
public class AdvancedPathFinding {

    private static JFrame frame;

    public static void show() {
        frame = new JFrame("Advanced pathfinding");
        frame.setSize(800, 720);

        DrawPanel panel = new DrawPanel();
        panel.setSize(700, 700);
        frame.add(panel);

        Integer items[] = new Integer[5];
        for (int i = 1; i < 6; i++) {
            items[i - 1] = i;
        }
        JComboBox comboBox = new JComboBox(items);
        JLabel label = new JLabel("Размер объекта:");

        panel.add(comboBox);
        panel.add(label);

        comboBox.setSize(40, 20);
        label.setSize(120, 20);

        comboBox.setLocation(730, 20);
        label.setLocation(620, 20);

        comboBox.setVisible(true);
        label.setVisible(true);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                Integer item = (Integer) box.getSelectedItem();
                panel.size = item;
                panel.aim = null;
            }
        };

        MouseListener mouseListener = new MouseListener() {
            int tick = 0;
            Cell aim;

            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getX() > 600) || (e.getY() > 600)) {
                    return;
                }
                aim = new Cell(e.getX() / panel.grid.cellSize,
                        e.getY() / panel.grid.cellSize);
                panel.aim = aim;

            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel.addMouseListener(mouseListener);
        comboBox.addActionListener(actionListener);

    }

}
