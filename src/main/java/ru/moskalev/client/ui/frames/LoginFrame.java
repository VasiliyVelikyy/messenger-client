package ru.moskalev.client.ui.frames;

import com.formdev.flatlaf.FlatClientProperties;
import ru.moskalev.client.network.MessengerWebSocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton continueButton;
    private JLabel errorLabel;
    private MessengerWebSocketClient client;

    public LoginFrame() {
        setTitle("Вход в Telegram");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(25, 35, 48));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Заголовок
        JLabel titleLabel = new JLabel("Ваш номер телефона");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Подзаголовок
        JLabel subtitleLabel = new JLabel("Проверьте код страны и введите");
        subtitleLabel.setForeground(new Color(150, 160, 170));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel2 = new JLabel("свой номер телефона.");
        subtitleLabel2.setForeground(new Color(150, 160, 170));
        subtitleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(subtitleLabel2);
        mainPanel.add(Box.createVerticalStrut(40));

        // Поле выбора страны (заглушка)
        JPanel countryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countryPanel.setOpaque(false);
        countryPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel countryLabel = new JLabel("Russian Federation");
        countryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countryLabel.setForeground(new Color(180, 190, 200));
        countryPanel.add(countryLabel);

        JLabel arrowLabel = new JLabel("▼");
        arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        arrowLabel.setForeground(new Color(100, 110, 120));
        countryPanel.add(arrowLabel);

        mainPanel.add(countryPanel);

        // Поле ввода кода страны (+7)
        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codePanel.setOpaque(false);
        codePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel codeLabel = new JLabel("+7");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        codeLabel.setForeground(Color.WHITE);
        codePanel.add(codeLabel);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(200, 2));
        codePanel.add(separator);

        mainPanel.add(codePanel);

        // Поле ввода логина
        loginField = new JTextField();
        loginField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginField.setPreferredSize(new Dimension(300, 40));
        loginField.setMaximumSize(new Dimension(300, 40));
        loginField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 85), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        loginField.setBackground(new Color(35, 45, 60));
        loginField.setForeground(Color.WHITE);
        loginField.setCaretColor(Color.WHITE);
        loginField.putClientProperty("FlatLaf.style", "arc: 8");
        loginField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "введите логин");
        loginField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Поле ввода пароля
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 85), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        passwordField.setBackground(new Color(35, 45, 60));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.putClientProperty("FlatLaf.style", "arc: 8");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "введите пароль");

        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Обработка Enter
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    continueButton.doClick();
                }
            }
        };
        loginField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        mainPanel.add(loginField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));

        // Метка ошибки
        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(237, 108, 95));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(errorLabel);

        // Кнопка "Продолжить"
        continueButton = new JButton("Продолжить");
        continueButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(300, 44));
        continueButton.setMaximumSize(new Dimension(300, 44));
        continueButton.setBackground(new Color(41, 103, 154));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueButton.putClientProperty("FlatLaf.style", "arc: 8");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton.addActionListener(e -> attemptLogin());

        mainPanel.add(continueButton);
        mainPanel.add(Box.createVerticalStrut(20));

        // Кнопка смены языка
        JButton langButton = new JButton("Continue in English");
        langButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        langButton.setForeground(new Color(41, 103, 154));
        langButton.setBorderPainted(false);
        langButton.setContentAreaFilled(false);
        langButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        langButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(langButton);
        mainPanel.add(Box.createVerticalStrut(20));

        // QR вход
        JButton qrButton = new JButton("Быстрый вход по QR-коду");
        qrButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        qrButton.setForeground(new Color(41, 103, 154));
        qrButton.setBorderPainted(false);
        qrButton.setContentAreaFilled(false);
        qrButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        qrButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(qrButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private void attemptLogin() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Валидация
        if (login.isEmpty()) {
            showError("Введите логин");
            loginField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Введите пароль");
            passwordField.requestFocus();
            return;
        }

        // Блокируем кнопку
        continueButton.setEnabled(false);
        continueButton.setText("Подключение...");
        clearError();

        // Создаем WebSocket клиент и подключаемся
        try {
            if (client != null && client.isOpen()) {
                client.close();
            }
            client = new MessengerWebSocketClient("ws://localhost:8080/ws", this);
            client.connect();

            // Отправляем данные для авторизации
            client.sendAuth(login, password);

        } catch (Exception ex) {
            System.err.println("❌ [CLIENT] Ошибка: " + ex.getMessage());
            ex.printStackTrace();
            showError("Ошибка подключения: " + ex.getMessage());
            continueButton.setEnabled(true);
            continueButton.setText("Продолжить");
        }
    }

    public void onAuthSuccess(String displayName) {
        SwingUtilities.invokeLater(() -> {
            dispose();
            ChatFrame chatFrame = new ChatFrame(displayName, client);
            chatFrame.setVisible(true);
            client.setChatFrame(chatFrame); // Связываем клиент с окном чата
        });
    }

    public void onAuthError(String errorCode) {
        SwingUtilities.invokeLater(() -> {
            continueButton.setEnabled(true);
            continueButton.setText("Продолжить");

            String message = switch (errorCode) {
                case "USER_NOT_FOUND" -> "Пользователь не найден";
                case "INVALID_PASSWORD" -> "Неверный пароль";
                case "LOGIN_EMPTY" -> "Введите логин";
                case "PASSWORD_EMPTY" -> "Введите пароль";
                default -> "Ошибка авторизации";
            };
            showError(message);
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    @Override
    public void dispose() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                // ignore
            }
        }
        super.dispose();
    }
}