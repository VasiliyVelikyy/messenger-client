package ru.moskalev.client.ui.frames;

import com.formdev.flatlaf.FlatClientProperties;
import ru.moskalev.client.model.ContactUI;
import ru.moskalev.client.network.MessengerWebSocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ru.moskalev.client.util.IconCreator.createSendIcon;
import static ru.moskalev.client.util.TimeUtil.formatTimestamp;

/**
 * Главное окно чата после успешной авторизации.
 * <p>
 * Содержит две панели: список контактов слева и область переписки справа.
 * Обрабатывает отправку, получение, редактирование и удаление сообщений.
 */
public class ChatFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(ChatFrame.class.getName());

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final int CONTACTS_PANEL_WIDTH = 280;
    private static final int CONTACT_ITEM_HEIGHT = 56;
    private static final int INPUT_PANEL_HEIGHT = 80;
    private static final Color BG_MAIN = new Color(25, 35, 48);
    private static final Color BG_CONTACTS = new Color(32, 44, 59);
    private static final Color BG_INPUT = new Color(40, 52, 68);
    private static final Color MSG_OWN = new Color(41, 103, 154);
    private static final Color MSG_OTHER = new Color(40, 52, 68);
    private static final Color STATUS_ONLINE = new Color(76, 201, 120);
    private static final Color STATUS_OFFLINE = new Color(100, 110, 120);
    private static final Color TIME_LABEL_COLOR = new Color(200, 200, 200, 180);

    private final String currentUser;
    private final MessengerWebSocketClient wsClient;
    private String selectedContactLogin = null;

    private JPanel contactsPanel;
    private JPanel chatHistoryPanel;
    private JTextArea messageInput;
    private JButton sendButton;
    private JLabel chatHeaderLabel;
    private JScrollPane historyScroll;
    private final Map<JLabel, JPanel> messageBubbles = new HashMap<>();

    /**
     * Создаёт окно чата для авторизованного пользователя.
     *
     * @param currentUser логин текущего пользователя
     * @param wsClient    клиент WebSocket для обмена сообщениями
     */
    public ChatFrame(String currentUser, MessengerWebSocketClient wsClient) {
        this.currentUser = currentUser;
        this.wsClient = wsClient;
        initWindow();
        initUI();
        loadContacts();
        logger.info("Chat frame initialized for user: " + currentUser);
    }

    private void initWindow() {
        setTitle("Telegram — " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_MAIN);

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createSplitPane(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JSplitPane createSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(CONTACTS_PANEL_WIDTH);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(BG_MAIN);
        split.setOneTouchExpandable(false);

        split.setLeftComponent(createContactsScrollPane());
        split.setRightComponent(createChatPanel());

        return split;
    }

    private JScrollPane createContactsScrollPane() {
        contactsPanel = new JPanel();
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        contactsPanel.setBackground(BG_CONTACTS);
        contactsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(contactsPanel);
        scroll.setBorder(null);
        scroll.setBackground(BG_CONTACTS);
        scroll.setViewportBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ((JComponent) scroll.getViewport()).setBorder(null);

        return scroll;
    }

    /**
     * Обновляет список контактов в левой панели.
     *
     * @param contacts список объектов ContactUI для отображения
     */
    public void updateContactsList(List<ContactUI> contacts) {
        contactsPanel.removeAll();
        for (var contact : contacts) {
            contactsPanel.add(createContactItem(contact));
            contactsPanel.add(Box.createVerticalStrut(1));
        }
        refreshPanel(contactsPanel);
        logger.fine("Contacts list updated: " + contacts.size() + " items");
    }

    private JPanel createContactItem(ContactUI contact) {
        JPanel item = new JPanel(new BorderLayout(10, 4));
        item.setBackground(BG_CONTACTS);
        item.setPreferredSize(new Dimension(CONTACTS_PANEL_WIDTH - 20, CONTACT_ITEM_HEIGHT));
        item.setMaximumSize(new Dimension(CONTACTS_PANEL_WIDTH - 20, CONTACT_ITEM_HEIGHT));
        item.setBorder(new EmptyBorder(4, 12, 4, 12));
        item.setOpaque(true);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.add(createContactLeftPart(contact), BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectContact(contact.login(), contact.displayName());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!contact.login().equals(selectedContactLogin)) {
                    item.setBackground(new Color(40, 54, 71));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!contact.login().equals(selectedContactLogin)) {
                    item.setBackground(BG_CONTACTS);
                }
            }
        });

        if (contact.login().equals(selectedContactLogin)) {
            item.setBackground(new Color(40, 54, 71));
        }

        return item;
    }

    private JPanel createContactLeftPart(ContactUI contact) {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel avatar = new JLabel(contact.displayName().substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setBackground(new Color(41, 103, 154));
        avatar.setOpaque(true);
        avatar.putClientProperty("FlatLaf.style", "arc: 20");

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(contact.displayName());
        name.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = new JLabel(contact.online() ? "в сети" : "был(а) недавно");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        status.setForeground(contact.online() ? STATUS_ONLINE : STATUS_OFFLINE);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(name);
        text.add(Box.createVerticalStrut(2));
        text.add(status);

        left.add(avatar);
        left.add(text);
        return left;
    }

    private void selectContact(String login, String displayName) {
        selectedContactLogin = login;
        chatHeaderLabel.setText(displayName);
        showLoadingIndicator();
        if (wsClient != null) {
            wsClient.requestHistory(login);
        }
        if (messageInput != null) {
            messageInput.requestFocus();
        }
        logger.fine("Contact selected: " + login);
    }

    private void loadContacts() {
        if (wsClient != null) {
            SwingUtilities.invokeLater(wsClient::requestContacts);
        }
    }

    private JPanel createChatPanel() {
        JPanel chat = new JPanel(new BorderLayout());
        chat.setBackground(BG_MAIN);

        chatHeaderLabel = new JLabel("Выберите контакт");
        chatHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chatHeaderLabel.setForeground(Color.WHITE);
        chatHeaderLabel.setBorder(new EmptyBorder(15, 20, 10, 20));
        chat.add(chatHeaderLabel, BorderLayout.NORTH);

        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(BG_MAIN);
        chatHistoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        historyScroll = new JScrollPane(chatHistoryPanel);
        historyScroll.setBorder(null);
        historyScroll.setBackground(BG_MAIN);
        historyScroll.setViewportBorder(null);
        historyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        historyScroll.getVerticalScrollBar().setUnitIncrement(16);
        chat.add(historyScroll, BorderLayout.CENTER);

        chat.add(createInputPanel(), BorderLayout.SOUTH);
        return chat;
    }

    private JPanel createInputPanel() {
        JPanel input = new JPanel(new BorderLayout(10, 0));
        input.setBackground(BG_CONTACTS);
        input.setBorder(new EmptyBorder(10, 15, 15, 15));
        input.setPreferredSize(new Dimension(0, INPUT_PANEL_HEIGHT));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_PANEL_HEIGHT));

        messageInput = new JTextArea();
        messageInput.setRows(2);
        messageInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageInput.setBackground(BG_INPUT);
        messageInput.setForeground(Color.WHITE);
        messageInput.setCaretColor(Color.WHITE);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        messageInput.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Введите сообщение...");
        messageInput.putClientProperty("FlatLaf.style", "arc: 20");

        JScrollPane scroll = new JScrollPane(messageInput);
        scroll.setBorder(null);
        scroll.setBackground(BG_INPUT);
        scroll.setViewportBorder(null);

        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        sendButton = new JButton();
        sendButton.setIcon(createSendIcon());
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setPreferredSize(new Dimension(50, 40));
        sendButton.setBackground(MSG_OWN);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.putClientProperty("FlatLaf.style", "arc: 20");
        sendButton.addActionListener(e -> sendMessage());

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(51, 113, 164));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(MSG_OWN);
            }
        });

        input.add(scroll, BorderLayout.CENTER);
        input.add(sendButton, BorderLayout.EAST);
        return input;
    }

    private void sendMessage() {
        if (selectedContactLogin == null) {
            JOptionPane.showMessageDialog(this, "Выберите контакт", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        wsClient.sendMessage(selectedContactLogin, text);
        addMessageToChat(text, true);
        messageInput.setText("");
        logger.fine("Message sent to: " + selectedContactLogin);
    }

    /**
     * Добавляет сообщение в историю чата с текущим временем.
     *
     * @param text  текст сообщения
     * @param isOwn признак того, что сообщение отправлено текущим пользователем
     */
    public void addMessageToChat(String text, boolean isOwn) {
        addMessageToChat(text, isOwn, System.currentTimeMillis());
    }

    /**
     * Добавляет сообщение в историю чата с указанным временем.
     *
     * @param text      текст сообщения
     * @param isOwn     признак того, что сообщение отправлено текущим пользователем
     * @param timestamp временная метка сообщения в миллисекундах
     */
    public void addMessageToChat(String text, boolean isOwn, long timestamp) {
        JPanel bubble = createMessageBubble(text, isOwn, timestamp);
        chatHistoryPanel.add(bubble);
        refreshPanel(chatHistoryPanel);
        if (isOwn) scrollToBottom();
    }

    private JPanel createMessageBubble(String text, boolean isOwn, long timestamp) {
        JPanel container = new JPanel(new FlowLayout(
                isOwn ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));
        container.setBackground(BG_MAIN);
        container.setOpaque(true);

        String timeText = formatTimestamp(timestamp);

        JLabel label = new JLabel(
                "<html><body style='width: " + (isOwn ? 180 : 280) + "px'>" +
                        text.replace("\n", "<br>") + "</body></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, isOwn ? 13 : 14));
        label.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(TIME_LABEL_COLOR);
        timeLabel.setHorizontalAlignment(
                isOwn ? SwingConstants.RIGHT : SwingConstants.LEFT);

        JPanel bubble = new JPanel(new BorderLayout(0, 4));
        bubble.setBackground(isOwn ? MSG_OWN : MSG_OTHER);
        bubble.setOpaque(true);
        bubble.putClientProperty("FlatLaf.style", "arc: " + (isOwn ? 15 : 18));
        bubble.setBorder(new EmptyBorder(10, 12, 4, 12));

        bubble.add(label, BorderLayout.CENTER);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        container.add(bubble);

        if (isOwn) {
            messageBubbles.put(label, container);
        }

        if (isOwn) {
            setupMessageContextMenu(bubble, label, text);
        }

        return container;
    }

    private void setupMessageContextMenu(JPanel bubble, JLabel label, String originalText) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Изменить");
        editItem.addActionListener(e -> showEditDialog(label, originalText));
        popup.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Удалить");
        deleteItem.addActionListener(e -> deleteMessage(label));
        popup.add(deleteItem);

        bubble.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popup.show(bubble, e.getX(), e.getY());
                }
            }
        });

        bubble.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bubble.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bubble.setBackground(bubble.getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bubble.setBackground(bubble.getBackground().brighter());
            }
        });
    }

    private String formatMessageText(String text, int widthPx) {
        return "<html><div style='width: " + widthPx + "px; white-space: normal;'>" +
                text.replace("\n", "<br>") + "</div></html>";
    }

    /**
     * Прокручивает историю чата вниз.
     */
    public void scrollToBottom() {
        if (historyScroll != null) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = historyScroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });
        }
    }

    /**
     * Очищает историю чата.
     */
    public void clearChat() {
        chatHistoryPanel.removeAll();
        refreshPanel(chatHistoryPanel);
    }

    private void showLoadingIndicator() {
        clearChat();
        JLabel loading = new JLabel("Загрузка сообщений...", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        loading.setForeground(new Color(120, 130, 140));
        loading.setBorder(new EmptyBorder(20, 0, 20, 0));
        chatHistoryPanel.add(loading);
        refreshPanel(chatHistoryPanel);
    }

    private void refreshPanel(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_CONTACTS);
        top.setPreferredSize(new Dimension(0, 50));
        top.setBorder(new EmptyBorder(0, 15, 0, 15));

        JTextField search = new JTextField();
        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Поиск");
        search.putClientProperty("FlatLaf.style", "arc: 20");
        search.setPreferredSize(new Dimension(200, 30));
        top.add(search, BorderLayout.WEST);

        JButton logout = new JButton("Выйти");
        logout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logout.addActionListener(e -> {
            if (wsClient != null) wsClient.close();
            dispose();
            new LoginFrame().setVisible(true);
            logger.info("User logged out: " + currentUser);
        });
        top.add(logout, BorderLayout.EAST);

        return top;
    }


    private void showEditDialog(JLabel label, String originalText) {
        JTextField textField = new JTextField(originalText);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Редактировать сообщение:"), BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Изменение сообщения",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String newText = textField.getText().trim();
            if (!newText.isEmpty() && !newText.equals(originalText)) {
                wsClient.editMessage(selectedContactLogin, originalText, newText);
                label.setText(formatMessageText(newText, 170));
                label.revalidate();
                label.repaint();
                logger.fine("Message edit requested");
            }
        }
    }

    private void deleteMessage(JLabel label) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить это сообщение?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String cleanText = label.getText()
                .replaceAll("<[^>]*>", "")
                .replace("\n", " ")
                .trim();

        JPanel container = messageBubbles.get(label);
        if (container != null) {
            chatHistoryPanel.remove(container);
            messageBubbles.remove(label);
            chatHistoryPanel.revalidate();
            chatHistoryPanel.repaint();
        }

        if (selectedContactLogin != null) {
            wsClient.deleteMessage(selectedContactLogin, cleanText);
            logger.fine("Message delete requested");
        }
    }

    /**
     * Обновляет текст существующего сообщения в интерфейсе.
     *
     * @param originalText исходный текст сообщения
     * @param newText      новый текст сообщения
     */
    public void updateMessage(String originalText, String newText) {
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<JLabel, JPanel> entry : messageBubbles.entrySet()) {
                String bubbleText = entry.getKey().getText()
                        .replaceAll("<[^>]*>", "")
                        .replace("\n", " ")
                        .trim();

                if (bubbleText.equals(originalText)) {
                    entry.getKey().setText(formatMessageText(newText, 170));
                    entry.getKey().revalidate();
                    entry.getKey().repaint();
                    break;
                }
            }
        });
    }

    /**
     * Удаляет сообщение из интерфейса по его тексту.
     *
     * @param text текст сообщения для удаления
     */
    public void deleteMessageByContent(String text) {
        SwingUtilities.invokeLater(() -> {
            for (Map.Entry<JLabel, JPanel> entry : messageBubbles.entrySet()) {
                String bubbleText = entry.getKey().getText()
                        .replaceAll("<[^>]*>", "")
                        .replace("\n", " ")
                        .trim();

                if (bubbleText.equals(text)) {
                    chatHistoryPanel.remove(entry.getValue());
                    messageBubbles.remove(entry.getKey());
                    refreshPanel(chatHistoryPanel);
                    break;
                }
            }
        });
    }

}