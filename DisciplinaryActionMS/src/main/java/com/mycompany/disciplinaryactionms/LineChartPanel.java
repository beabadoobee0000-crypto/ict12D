/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.disciplinaryactionms;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class LineChartPanel extends JPanel {

    private Map<String, Integer> data;

    public LineChartPanel(Map<String, Integer> data) {
        this.data = data;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int padding = 60;
        int graphWidth = width - (padding * 2);
        int graphHeight = height - (padding * 2);

        int max = Collections.max(data.values());

        int total = data.size();
        int xSpacing = graphWidth / (total - 1);

       
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(0, 102, 255));

        int prevX = -1, prevY = -1;

        int i = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {

            int value = entry.getValue();

            int x = padding + (i * xSpacing);
            int y = padding + (int)((1 - (value / (double) max)) * graphHeight);

            // draw line
            if (prevX != -1) {
                g2.drawLine(prevX, prevY, x, y);
            }

            // draw point
            g2.fillOval(x-4, y-4, 8, 8);

            // draw value
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf(value), x - 5, y - 10);

            
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(entry.getKey());
            g2.drawString(entry.getKey(), x - textWidth / 2, height - 20);
            g2.setStroke(new BasicStroke(3f));
            prevX = x;
            prevY = y;

            i++;
        }
    }
}