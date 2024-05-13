package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JLabelFontResizeExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("放大字体");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("很好");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 12)); // 设置初始字体（使用微软雅黑字体）

        Font currentFont = label.getFont();
        Font newFont = currentFont.deriveFont(400f);
        label.setFont(newFont);


        frame.add(label);

        frame.pack();
        frame.setVisible(true);
    }
}
