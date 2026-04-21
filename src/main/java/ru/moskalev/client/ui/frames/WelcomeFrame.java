package ru.moskalev.client.ui.frames;

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {
        setTitle("Telegram Desktop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(25, 35, 48)); // Темно-синий фон


        JPanel logoPanel = new JPanel(new GridBagLayout()); // Логотип (бумажный самолетик)
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(600, 200));

        JLabel logoLabel = new JLabel(createPaperPlaneIcon());
        logoPanel.add(logoLabel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Telegram Desktop");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        descPanel.setOpaque(false);
        JLabel descLabel1 = new JLabel("Добро пожаловать в Telegram для ПК.");
        descLabel1.setForeground(new Color(150, 160, 170));
        descPanel.add(descLabel1);

        JPanel descPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        descPanel2.setOpaque(false);
        JLabel descLabel2 = new JLabel("Быстрый и безопасный официальный клиент.");
        descLabel2.setForeground(new Color(150, 160, 170));
        descPanel2.add(descLabel2);

        // Кнопка "Начать общение"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        buttonPanel.setOpaque(false);
        JButton startButton = new JButton("Начать общение");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startButton.setPreferredSize(new Dimension(280, 44));
        startButton.setBackground(new Color(41, 103, 154)); // Синий цвет Telegram
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Действие кнопки
        startButton.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        buttonPanel.add(startButton);

        // Кнопка смены языка
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        langPanel.setOpaque(false);
        JButton langButton = new JButton("Continue in English");
        langButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        langButton.setForeground(new Color(41, 103, 154));
        langButton.setBorderPainted(false);
        langButton.setContentAreaFilled(false);
        langButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        langPanel.add(langButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(logoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(descPanel);
        mainPanel.add(descPanel2);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(buttonPanel);
        mainPanel.add(langPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private Icon createPaperPlaneIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.WHITE);
                int[] xPoints = {x + 100, x + 30, x + 100, x + 70};
                int[] yPoints = {y + 50, y + 20, y + 90, y + 70};
                g2d.fillPolygon(xPoints, yPoints, 4);

                g2d.setColor(new Color(200, 220, 255));
                g2d.fillPolygon(
                        new int[]{x + 100, x + 55, x + 70},
                        new int[]{y + 50, y + 45, y + 70},
                        3
                );

                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return 200;
            }

            @Override
            public int getIconHeight() {
                return 120;
            }
        };
    }
}