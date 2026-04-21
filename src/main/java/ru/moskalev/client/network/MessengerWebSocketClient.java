package ru.moskalev.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import ru.moskalev.client.ui.frames.ChatFrame;
import ru.moskalev.client.ui.frames.LoginFrame;

import javax.swing.*;
import java.net.URI;
import java.util.List;
import java.util.Map;


public class MessengerWebSocketClient extends WebSocketClient {

    private final ObjectMapper mapper = new ObjectMapper();
    private final String login;
    private final String password;
    private LoginFrame loginFrame;
    private ChatFrame chatFrame;

    // 🔥 Флаг: соединение открыто И авторизация прошла
    private volatile boolean isReady = false;

    public MessengerWebSocketClient(String serverUri, String login, String password, LoginFrame loginFrame) throws Exception {
        super(new URI(serverUri));
        this.login = login;
        this.password = password;
        this.loginFrame = loginFrame;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("✅ [WS] onOpen: канал открыт");
        sendAuth(); // Отправляем авторизацию сразу
    }

    private void sendAuth() {
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "type", "AUTH",
                    "payload", Map.of("login", login, "password", password)
            ));
            send(json); // ← Теперь безопасно: onOpen гарантирует OPEN
            System.out.println("📤 [WS] Отправлен AUTH");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map<String, Object> json = mapper.readValue(message, Map.class);
            String type = (String) json.get("type");

            switch (type) {
                case "AUTH_SUCCESS" -> {
                    System.out.println("✅ [WS] AUTH_SUCCESS received");
                    isReady = true; // 🔥 Теперь можно слать сообщения!
                    handleAuthSuccess(json);
                }
                case "AUTH_ERROR" -> handleAuthError(json);
                case "CONTACTS_LIST" -> handleContactsList(json);
                case "NEW_MESSAGE" -> handleNewMessage(json);
                case "ERROR" -> handleError(json);
                default -> System.out.println("⚠️ Неизвестный тип: " + type);
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка парсинга: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔌 [WS] onClose: " + reason + " (code=" + code + ")");
        isReady = false;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("❌ [WS] onError: " + ex.getMessage());
        isReady = false;
    }

    // === Публичные методы ===

    // 🔥 Вызывается ТОЛЬКО после AUTH_SUCCESS из ChatFrame
    public void requestContacts() {
        if (!isReady) {
            System.out.println("⚠️ [WS] requestContacts() вызван, но isReady=false. Пропущено.");
            return;
        }
        try {
            String json = mapper.writeValueAsString(Map.of("type", "GET_CONTACTS"));
            send(json);
            System.out.println("📤 [WS] Запрошен список контактов");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String to, String text) {
        if (!isReady) {
            System.out.println("⚠️ [WS] sendMessage() вызван, но isReady=false. Пропущено.");
            return;
        }
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "type", "MESSAGE",
                    "payload", Map.of("to", to, "text", text)
            ));
            send(json);
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
        if (loginFrame != null) {
            SwingUtilities.invokeLater(() -> {
                loginFrame.dispose();
                ChatFrame chatFrame = new ChatFrame(displayName, MessengerWebSocketClient.this);
                chatFrame.setVisible(true);
                setChatFrame(chatFrame);
                requestContacts();

            });
        }
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

}
