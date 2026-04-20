/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.disciplinaryactionms;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    private int cornerRadius = 20;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //SHADOW
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, cornerRadius, cornerRadius);

        //MAIN PANEL
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, cornerRadius, cornerRadius);
    }
}
