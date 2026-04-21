package ru.moskalev.client.ui.frames;

import com.formdev.flatlaf.FlatClientProperties;
import ru.moskalev.client.network.MessengerWebSocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ChatFrame extends JFrame {

    private final String currentUser;
    private final MessengerWebSocketClient wsClient;

    // UI-компоненты
    private JPanel contactsPanel;      // Левая панель: список контактов
    private JPanel chatHistoryPanel;   // Правая панель: история сообщений
    private JTextArea messageInput;    // Поле ввода
    private JButton sendButton;        // Кнопка отправки
    private JLabel chatHeaderLabel;    // Заголовок текущего чата


    private String selectedContactLogin = null;


    public ChatFrame(String currentUser, MessengerWebSocketClient wsClient) {
        this.currentUser = currentUser;
        this.wsClient = wsClient;
        initUI();
        initContactsRequest();
    }

    public void initContactsRequest() {
        wsClient.requestContacts();
    }

    public void updateContactsList(List<MessengerWebSocketClient.ContactUI> contacts) {
        contactsPanel.removeAll();

        for (var contact : contacts) {
            addContactItem(contact);
        }
        contactsPanel.revalidate();
        contactsPanel.repaint();
    }

    private void initUI() {
        setTitle("Telegram — " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Основной контейнер с отступами
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 35, 48));
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // === ВЕРХНЯЯ ПАНЕЛЬ ===
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // === ЦЕНТРАЛЬНАЯ ЧАСТЬ: контакты + чат ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(25, 35, 48));

        // Левая панель: контакты
        contactsPanel = new JPanel();
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        contactsPanel.setBackground(new Color(32, 44, 59));
        contactsPanel.setPreferredSize(new Dimension(280, 0));

        JScrollPane contactsScroll = new JScrollPane(contactsPanel);
        contactsScroll.setBorder(null);
        contactsScroll.setBackground(new Color(32, 44, 59));
        splitPane.setLeftComponent(contactsScroll);

        // Правая панель: чат
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(25, 35, 48));

        // Заголовок чата
        chatHeaderLabel = new JLabel("Выберите контакт");
        chatHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setBorder(new EmptyBorder(15, 20, 10, 20));
        chatPanel.add(chatHeaderLabel, BorderLayout.NORTH);

        // История сообщений
        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(new Color(25, 35, 48));

        JScrollPane historyScroll = new JScrollPane(chatHistoryPanel);
        historyScroll.setBorder(null);
        historyScroll.setBackground(new Color(25, 35, 48));
        historyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(historyScroll, BorderLayout.CENTER);

        // Поле ввода сообщения
        JPanel inputPanel = createInputPanel();
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(chatPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(32, 44, 59));
        topPanel.setPreferredSize(new Dimension(0, 50));
        topPanel.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Поиск
        JTextField searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Поиск");
        searchField.putClientProperty("FlatLaf.style", "arc: 20");
        searchField.setPreferredSize(new Dimension(200, 30));
        topPanel.add(searchField, BorderLayout.WEST);

        // Кнопка выхода
        JButton logoutButton = new JButton("Выйти");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> {
            wsClient.close();
            dispose();
            new LoginFrame().setVisible(true);
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(32, 44, 59));
        inputPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        messageInput = new JTextArea();
        messageInput.setRows(3);
        messageInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageInput.setBackground(new Color(40, 52, 68));
        messageInput.setForeground(Color.WHITE);
        messageInput.setCaretColor(Color.WHITE);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        messageInput.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Введите сообщение...");
        messageInput.putClientProperty("FlatLaf.style", "arc: 20");

        JScrollPane inputScroll = new JScrollPane(messageInput);
        inputScroll.setBorder(null);
        inputScroll.setBackground(new Color(40, 52, 68));

        sendButton = new JButton("➤");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setPreferredSize(new Dimension(50, 40));
        sendButton.setBackground(new Color(41, 103, 154));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.putClientProperty("FlatLaf.style", "arc: 20");
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    private void sendMessage() {
        if (selectedContactLogin == null) {
            JOptionPane.showMessageDialog(this, "Выберите контакт для отправки сообщения", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        wsClient.sendMessage(selectedContactLogin, text);

        // Отображаем своё сообщение сразу
        addMessageToChat(text, true);
        messageInput.setText("");
    }

    public void receiveMessage(String fromLogin, String text) {
        // Если чат с этим пользователем открыт → показываем
        if (fromLogin.equals(selectedContactLogin)) {
            addMessageToChat(text, false);
        } else {
            // TODO: можно добавить уведомление или счётчик непрочитанных
            System.out.println("💬 Новое сообщение от " + fromLogin + ": " + text);
        }
    }


    private void addContactItem(MessengerWebSocketClient.ContactUI contact) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        item.setBackground(new Color(32, 44, 59));
        item.setPreferredSize(new Dimension(260, 60));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setOpaque(true);

        // Аватар
        JLabel avatar = new JLabel(contact.displayName().substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBackground(new Color(41, 103, 154));
        avatar.setOpaque(true);
        avatar.putClientProperty("FlatLaf.style", "arc: 20");

        // Текст
        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        JLabel name = new JLabel(contact.displayName());
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        name.setForeground(Color.WHITE);
        JLabel status = new JLabel(contact.online() ? "в сети" : "был(а) недавно");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(contact.online() ? new Color(76, 201, 120) : new Color(100, 110, 120));
        info.add(name, BorderLayout.NORTH);
        info.add(status, BorderLayout.SOUTH);

        item.add(avatar);
        item.add(info);

        // Клик по контакту
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedContactLogin = contact.login();
                chatHeaderLabel.setText("💬 " + contact.displayName());
                chatHistoryPanel.removeAll();
                chatHistoryPanel.revalidate();
                chatHistoryPanel.repaint();
                messageInput.requestFocus();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                item.setBackground(new Color(40, 54, 71));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!contact.login().equals(selectedContactLogin)) {
                    item.setBackground(new Color(32, 44, 59));
                }
            }
        });

        // Подсветка выбранного
        if (contact.login().equals(selectedContactLogin)) {
            item.setBackground(new Color(40, 54, 71));
        }

        contactsPanel.add(item);
    }

    // Метод для добавления сообщения в историю (будет использоваться при приёме)
    public void addMessageToChat(String text, boolean isOwn) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.X_AXIS));
        bubble.setBackground(new Color(25, 35, 48));
        bubble.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel messageLabel = new JLabel("<html><div style='width: 300px; white-space: normal;'>" +
                text.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBackground(isOwn ? new Color(41, 103, 154) : new Color(40, 52, 68));
        messageLabel.setOpaque(true);
        messageLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        messageLabel.putClientProperty("FlatLaf.style", "arc: 18");

        if (isOwn) {
            bubble.add(Box.createHorizontalGlue());
            bubble.add(messageLabel);
        } else {
            bubble.add(messageLabel);
            bubble.add(Box.createHorizontalGlue());
        }

        chatHistoryPanel.add(bubble);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();

        // Auto-scroll вниз
        Container parent = chatHistoryPanel.getParent();
        if (parent instanceof JViewport) {
            JViewport viewport = (JViewport) parent;
            Rectangle rect = chatHistoryPanel.getBounds();
            viewport.setViewPosition(new Point(0, Math.max(0, rect.height - viewport.getHeight())));
        }
    }
}