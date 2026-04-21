```
messenger-client/
├── pom.xml                                          # Конфигурация Maven
├── README.md                                        # Документация проекта
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── yourpackage/
│       │           └── messenger/
│       │               └── client/
│       │                   ├── ClientApp.java           # 🔹 Точка входа (main)
│       │                   │
│       │                   ├── config/                  # Конфигурация
│       │                   │   ├── ClientConfig.java    # Настройки клиента (сервер, таймауты)
│       │                   │   └── UiConfig.java        # UI-параметры (цвета, шрифты, размеры)
│       │                   │
│       │                   ├── ui/                      # Графический интерфейс (Swing)
│       │                   │   ├── components/          # Кастомные UI-компоненты
│       │                   │   │   ├── AvatarLabel.java # Аватарка пользователя
│       │                   │   │   ├── MessageBubble.java # Пузырь сообщения (входящий/исходящий)
│       │                   │   │   ├── UserListItem.java # Элемент списка контактов
│       │                   │   │   └── StatusIndicator.java # Индикатор онлайн/офлайн
│       │                   │   │
│       │                   │   ├── frames/              # Основные окна (JFrame)
│       │                   │   │   ├── WelcomeFrame.java # Начальный экран ("Начать общение")
│       │                   │   │   ├── LoginFrame.java  # Окно авторизации
│       │                   │   │   ├── ChatFrame.java   # Главное окно чата
│       │                   │   │   └── SettingsFrame.java # Настройки (опционально)
│       │                   │   │
│       │                   │   ├── dialogs/             # Модальные диалоги
│       │                   │   │   ├── ErrorDialog.java # Окно ошибки
│       │                   │   │   └── ConfirmDialog.java # Подтверждение действия
│       │                   │   │
│       │                   │   └── util/                # UI-утилиты
│       │                   │       ├── FlatLafTheme.java # Настройка темы
│       │                   │       ├── IconFactory.java  # Генерация иконок
│       │                   │       └── SwingUtils.java   # Helper-методы для Swing
│       │                   │
│       │                   ├── network/                 # Сетевой слой
│       │                   │   ├── WebSocketClient.java # Клиент WebSocket (Java-WebSocket)
│       │                   │   ├── MessageHandler.java  # Обработчик входящих сообщений
│       │                   │   ├── ConnectionListener.java # Слушатель событий подключения
│       │                   │   └── ReconnectStrategy.java # Стратегия переподключения
│       │                   │
│       │                   ├── protocol/                # Протокол обмена (DTO)
│       │                   │   ├── Packet.java          # Базовый пакет: {type, payload}
│       │                   │   ├── AuthRequest.java     # Запрос авторизации
│       │                   │   ├── AuthResponse.java    # Ответ авторизации
│       │                   │   ├── MessageRequest.java  # Отправка сообщения
│       │                   │   ├── MessageResponse.java # Получение сообщения
│       │                   │   ├── ErrorResponse.java   # Ошибка от сервера
│       │                   │   └── PacketType.java      # Enum типов сообщений
│       │                   │
│       │                   ├── service/                 # Бизнес-логика клиента
│       │                   │   ├── AuthService.java     # Логика авторизации (вызовы к серверу)
│       │                   │   ├── ChatService.java     # Отправка/приём сообщений
│       │                   │   ├── UserService.java     # Работа со списком контактов
│       │                   │   └── SessionManager.java  # Управление сессией пользователя
│       │                   │
│       │                   ├── model/                   # Доменные модели
│       │                   │   ├── User.java            # Пользователь (login, displayName, online)
│       │                   │   ├── ChatMessage.java     # Сообщение (from, to, text, timestamp)
│       │                   │   └── ConnectionState.java # Статус подключения (enum)
│       │                   │
│       │                   └── util/                    # Общие утилиты
│       │                       ├── JsonUtil.java        # Singleton ObjectMapper
│       │                       ├── ValidationUtil.java  # Валидация ввода
│       │                       └── Logger.java          # Простой логгер в консоль/файл
│       │
│       └── resources/                                   # Ресурсы
│           ├── application.properties                   # Конфиг клиента (сервер, порт)
│           ├── icons/                                   # Иконки приложения
│           │   ├── send.png
│           │   ├── attach.png
│           │   ├── emoji.png
│           │   ├── online_dot.png
│           │   └── offline_dot.png
│           │
│           └── themes/                                  # Кастомные темы FlatLaf (опционально)
│               └── messenger.theme.properties
│
├── src/test/
│   └── java/
│       └── com/
│           └── yourpackage/
│               └── messenger/
│                   └── client/
│                       ├── network/
│                       │   └── WebSocketClientTest.java # Тесты сетевого слоя
│                       ├── protocol/
│                       │   └── PacketTest.java          # Тесты сериализации JSON
│                       └── service/
│                           └── AuthServiceTest.java     # Тесты авторизации
│
├── target/                                              # [GENERATED] Сборка Maven
│   ├── classes/
│   ├── messenger-client-1.0.0-SNAPSHOT.jar
│   └── messenger-client-1.0.0-SNAPSHOT-jar-with-dependencies.jar
│
├── .gitignore                                           # Исключения для Git
├── .editorconfig                                        # Настройки форматирования кода
└── LICENSE                                              # Лицензия (опционально)
```


## 🎉 Поздравляю! Авторизация работает!

Отлично, что вы прошли самый сложный этап — установление WebSocket-соединения и авторизацию. Теперь можно переходить к «мясу»: интерфейсу чата и обмену сообщениями.

---

## 🗺️ Что делать дальше: дорожная карта

```
✅ Авторизация (готово)
   │
   ▼
🔲 1. Главный экран чата (ChatFrame) — UI-разметка
   │   ├─ Левая панель: список контактов с аватарами и статусами
   │   ├─ Правая панель: шапка чата + история сообщений + поле ввода
   │   └─ Стиль: как в Telegram (темная тема, отступы, скругления)
   │
   ▼
🔲 2. Получение списка контактов с сервера
   │   ├─ Сервер: эндпоинт `/api/users` или сообщение `GET_CONTACTS`
   │   ├─ Клиент: запрос списка после успешной авторизации
   │   └─ Отображение: `UserListItem` с онлайн-статусом
   │
   ▼
🔲 3. Отправка сообщений
   │   ├─ Клиент: `{"type":"MESSAGE", "payload":{to, text}}`
   │   ├─ Сервер: маршрутизация получателю
   │   └─ Клиент: отображение своего сообщения в чате
   │
   ▼
🔲 4. Приём сообщений в реальном времени
   │   ├─ Сервер: пересылка `NEW_MESSAGE` активному получателю
   │   ├─ Клиент: обработка `onMessage()` → добавление в UI
   │   └─ Auto-scroll к последнему сообщению
   │
   ▼
🔲 5. Индикация онлайн-статусов (опционально, но красиво)
       ├─ Сервер: рассылка `USER_ONLINE` / `USER_OFFLINE`
       └─ Клиент: обновление иконки в `UserListItem`
```

---

## 🎯 Рекомендация: начните с **разметки ChatFrame**

Потому что без визуальной структуры сложно тестировать отправку/приём сообщений. Вот готовый шаблон, который повторяет интерфейс Telegram:

### 📐 Макет ChatFrame

```
┌─────────────────────────────────────────────┐
│  🔍 Поиск      ⚙️ Настройки     [Василий] ▼ │  ← Верхняя панель
├─────────────┬───────────────────────────────┤
│             │  💬 Анна ████████ 12:34      │  ← Шапка чата
│  📋 КОНТАКТЫ│  ─────────────────────────  │
│  ● Анна     │                               │
│  ○ Иван     │  [Сообщения будут здесь]     │
│  ○ Мария    │  • Привет!                   │
│             │  • Как дела?    ← История    │
│             │  • Я тут        ← сообщений  │
│             │                               │
│             │  ─────────────────────────  │
│             │  [Введите сообщение...] [➤]  │  ← Поле ввода
└─────────────┴───────────────────────────────┘
```

---

## 🧱 Базовая структура `ChatFrame.java` (заготовка)

```java
package com.yourpackage.messenger.client.ui.frames;

import com.formdev.flatlaf.FlatClientProperties;
import com.yourpackage.messenger.client.network.MessengerWebSocketClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChatFrame extends JFrame {
    
    private final String currentUser;
    private final MessengerWebSocketClient wsClient;
    
    // UI-компоненты
    private JPanel contactsPanel;      // Левая панель: список контактов
    private JPanel chatHistoryPanel;   // Правая панель: история сообщений
    private JTextArea messageInput;    // Поле ввода
    private JButton sendButton;        // Кнопка отправки
    private JLabel chatHeaderLabel;    // Заголовок текущего чата
    
    public ChatFrame(String currentUser, MessengerWebSocketClient wsClient) {
        this.currentUser = currentUser;
        this.wsClient = wsClient;
        
        initUI();
        loadContacts(); // Запрос списка контактов у сервера
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
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;
        
        // TODO: отправить через WebSocket
        // wsClient.sendMessage(targetLogin, text);
        
        // TODO: отобразить своё сообщение в чате
        // addMessageToChat(text, true); // isOwn = true
        
        messageInput.setText("");
    }
    
    private void loadContacts() {
        // TODO: запросить список контактов у сервера
        // Например: wsClient.requestContacts();
        
        // Пока добавим заглушки для тестов
        addContactItem("Анна", true);
        addContactItem("Иван", false);
        addContactItem("Мария", true);
    }
    
    private void addContactItem(String displayName, boolean online) {
        JPanel contactItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contactItem.setBackground(new Color(32, 44, 59));
        contactItem.setPreferredSize(new Dimension(260, 60));
        contactItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Аватар (заглушка)
        JLabel avatar = new JLabel(displayName.substring(0, 1).toUpperCase());
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setBackground(new Color(41, 103, 154));
        avatar.setOpaque(true);
        avatar.putClientProperty("FlatLaf.style", "arc: 20");
        
        // Имя + статус
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel statusLabel = new JLabel(online ? "в сети" : "был(а) недавно");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(online ? new Color(76, 201, 120) : new Color(120, 130, 140));
        
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Индикатор онлайн
        JLabel onlineDot = new JLabel("●");
        onlineDot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        onlineDot.setForeground(online ? new Color(76, 201, 120) : new Color(80, 90, 100));
        onlineDot.setPreferredSize(new Dimension(20, 20));
        
        contactItem.add(avatar);
        contactItem.add(infoPanel);
        contactItem.add(onlineDot);
        
        // Клик по контакту
        contactItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chatHeaderLabel.setText("💬 " + displayName);
                chatHistoryPanel.removeAll();
                chatHistoryPanel.revalidate();
                chatHistoryPanel.repaint();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                contactItem.setBackground(new Color(40, 54, 71));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                contactItem.setBackground(new Color(32, 44, 59));
            }
        });
        
        contactsPanel.add(contactItem);
        contactsPanel.revalidate();
        contactsPanel.repaint();
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
```

---

## 🔗 Как обновить переход из `LoginFrame`

```java
// В LoginFrame.java, метод onAuthSuccess():
public void onAuthSuccess(String displayName) {
    SwingUtilities.invokeLater(() -> {
        dispose();
        // Передаём текущий экземпляр WebSocket-клиента в ChatFrame
        ChatFrame chatFrame = new ChatFrame(displayName, client);
        chatFrame.setVisible(true);
    });
}
```

---

## ▶️ Что делаем сразу после UI-разметки?

1. **Добавить протокол для списка контактов**:
   ```json
   // Клиент → Сервер
   {"type": "GET_CONTACTS"}
   
   // Сервер → Клиент
   {"type": "CONTACTS_LIST", "payload": [
     {"login": "anna", "displayName": "Анна", "online": true},
     {"login": "ivan", "displayName": "Иван", "online": false}
   ]}
   ```

2. **Реализовать отправку сообщения**:
   ```java
   // В MessengerWebSocketClient.java
   public void sendMessage(String toLogin, String text) {
       // сформировать JSON и отправить через send()
   }
   ```

3. **Обработать входящее сообщение на клиенте**:
   ```java
   // В onMessage() добавить кейс:
   case "NEW_MESSAGE" -> {
       String from = payload.get("from");
       String text = payload.get("text");
       // Если этот чат открыт → addMessageToChat(text, false)
   }
   ```

---

## ✅ Ваш выбор: с чего начнём код?

| Вариант | Что делаем | Время |
|---------|-----------|-------|
| **🎨 Улучшаем UI ChatFrame** | Аватары, скролл, анимации, темы | 30-40 мин |
| **📡 Протокол контактов** | Серверный эндпоинт + клиентский запрос | 20-30 мин |
| **💬 Отправка сообщений** | `sendMessage()` + отображение в чате | 25-35 мин |

