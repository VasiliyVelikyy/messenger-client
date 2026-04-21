package ru.moskalev.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import ru.moskalev.client.ui.frames.ChatFrame;
import ru.moskalev.client.ui.frames.LoginFrame;
import org.java_websocket.client.WebSocketClient;


import javax.swing.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessengerWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper;
    private LoginFrame loginFrame;
    private ChatFrame chatFrame;

    private final ConcurrentLinkedQueue<Runnable> messageQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean isConnected = false;

    public MessengerWebSocketClient(String serverUri, LoginFrame loginFrame) throws Exception {
        super(new URI(serverUri));
        this.objectMapper = new ObjectMapper();
        this.loginFrame = loginFrame;
        this.chatFrame = null;;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("✅ [WS] Соединение установлено");
        isConnected = true;

        // Отправляем всё, что накопилось в очереди
        Runnable task;
        while ((task = messageQueue.poll()) != null) {
            task.run();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map<String, Object> json = objectMapper.readValue(message, Map.class);
            String type = (String) json.get("type");

            switch (type) {
                case "AUTH_SUCCESS" -> handleAuthSuccess(json);
                case "AUTH_ERROR" -> handleAuthError(json);
                case "CONTACTS_LIST" -> handleContactsList(json);
                case "NEW_MESSAGE" -> handleNewMessage(json);
                case "ERROR" -> handleError(json);
                default -> System.out.println("⚠️ [WS] Неизвестный тип: " + type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔌 [WS] Закрыто: " + reason);
        isConnected = false;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("❌ [WS] Ошибка: " + ex.getMessage());
        isConnected = false;
    }

    public void sendAuth(String login, String password) {
        try {
            Map<String, Object> authMessage = new HashMap<>();
            authMessage.put("type", "AUTH");
            authMessage.put("payload", Map.of("login", login, "password", password));
           // send(objectMapper.writeValueAsString(authMessage));
            safeSend(objectMapper.writeValueAsString(authMessage), "AUTH");
            System.out.println("📤 Отправлена авторизация: " + login);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestContacts() {
        try {
            String json = objectMapper.writeValueAsString(Map.of("type", "GET_CONTACTS"));
           // send(json);
            safeSend(json, "GET_CONTACTS");
            System.out.println("📤 [CLIENT] Запрошен список контактов");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String toLogin, String text) {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "MESSAGE");
            msg.put("payload", Map.of("to", toLogin, "text", text));
            send(objectMapper.writeValueAsString(msg));
            System.out.println("📤 [CLIENT] Сообщение -> " + toLogin + ": " + text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleContactsList(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        List<Map<String, Object>> contacts = (List<Map<String, Object>>) payload.get("contacts");

        // Преобразуем в простой формат для UI
        List<ContactUI> uiContacts = contacts.stream().map(c ->
                new ContactUI((String) c.get("login"), (String) c.get("displayName"), (boolean) c.get("online"))
        ).toList();

        // Обновляем UI в потоке Swing
        if (chatFrame != null) {
            SwingUtilities.invokeLater(() -> chatFrame.updateContactsList(uiContacts));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleNewMessage(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String from = (String) payload.get("from");
        String text = (String) payload.get("text");

        if (chatFrame != null) {
            SwingUtilities.invokeLater(() -> chatFrame.receiveMessage(from, text));
        }
    }

    // Вспомогательный класс для передачи в UI (можно вынести в отдельный файл)
    public record ContactUI(String login, String displayName, boolean online) {
    }

    @SuppressWarnings("unchecked")
    private void handleAuthSuccess(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String displayName = (String) payload.get("displayName");
        loginFrame.onAuthSuccess(displayName);
    }

    @SuppressWarnings("unchecked")
    private void handleAuthError(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String errorCode = (String) payload.get("code");
        loginFrame.onAuthError(errorCode);
    }

    @SuppressWarnings("unchecked")
    private void handleError(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String message = (String) payload.get("message");
        System.err.println("❌ Ошибка сервера: " + message);
    }

    public void setChatFrame(ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
        this.loginFrame = null;
    }

    private void queueMessage(String json, String description) {
        System.out.println("⏳ [WS] В очереди (ждём соединения): " + description);
        messageQueue.add(() -> {
            try { send(json); } catch (Exception e) { e.printStackTrace(); }
        });
    }
    private void safeSend(String json, String description) {
        if (isConnected && getReadyState() == ReadyState.OPEN) {
            try {
                send(json);
                System.out.println("📤 [WS] Отправлено: " + description);
            } catch (WebsocketNotConnectedException e) {
                queueMessage(json, description);
            }
        } else {
            queueMessage(json, description);
        }
    }
}
