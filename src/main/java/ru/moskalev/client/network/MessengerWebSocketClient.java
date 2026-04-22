package ru.moskalev.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.moskalev.client.ui.frames.ChatFrame;
import ru.moskalev.client.ui.frames.LoginFrame;
import ru.moskalev.client.model.ContactUI;

import javax.swing.*;
import java.net.URI;
import java.util.List;
import java.util.Map;


/**
 * Клиент WebSocket для обмена сообщениями с сервером чата.
 * Обрабатывает авторизацию, отправку и получение сообщений, управление контактами.
 */
public class MessengerWebSocketClient extends WebSocketClient {

    private static final Logger logger = Logger.getLogger(MessengerWebSocketClient.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();
    private final String currentUser;
    private final String password;
    private LoginFrame loginFrame;
    private ChatFrame chatFrame;

    /**
     * Создаёт новый клиент для подключения к серверу.
     *
     * @param serverUri  адрес WebSocket-сервера
     * @param login      логин пользователя
     * @param password   пароль пользователя
     * @param loginFrame ссылка на окно авторизации для обновления UI
     * @throws Exception если не удалось создать URI или подключиться
     */
    public MessengerWebSocketClient(String serverUri, String login, String password, LoginFrame loginFrame) throws Exception {
        super(new URI(serverUri));
        this.currentUser = login;
        this.password = password;
        this.loginFrame = loginFrame;
    }

    /**
     * Устанавливает ссылку на окно чата после успешной авторизации.
     *
     * @param chatFrame экземпляр ChatFrame для обновления интерфейса
     */
    public void setChatFrame(ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
        this.loginFrame = null;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket connection opened");
        sendAuth();
    }

    /**
     * Отправляет запрос авторизации на сервер.
     */
    private void sendAuth() {
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "type", "AUTH",
                    "payload", Map.of("login", currentUser, "password", password)
            ));
            send(json);
            logger.fine("Authorization request sent for user: " + currentUser);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send authorization request", e);
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map<String, Object> json = mapper.readValue(message, Map.class);
            String type = (String) json.get("type");

            switch (type) {
                case "AUTH_SUCCESS" -> handleAuthSuccess(json);
                case "AUTH_ERROR" -> handleAuthError(json);
                case "CONTACTS_LIST" -> handleContactsList(json);
                case "HISTORY" -> handleHistory(json);
                case "NEW_MESSAGE" -> handleIncomingMessage(json);
                case "ERROR" -> handleError(json);
                case "MESSAGE_UPDATED" -> handleUpdatedMessage(json);
                case "MESSAGE_DELETED" -> handleDeletedMessage(json);
                default -> logger.warning("Unknown message type: " + type);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse incoming message", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed: " + reason + " (code=" + code + ", remote=" + remote + ")");
    }

    @Override
    public void onError(Exception ex) {
        logger.log(Level.SEVERE, "WebSocket error occurred", ex);
    }

    /**
     * Запрашивает список контактов у сервера.
     */
    public void requestContacts() {
        sendJson("GET_CONTACTS", Map.of());
    }

    /**
     * Отправляет текстовое сообщение указанному пользователю.
     *
     * @param to   логин получателя
     * @param text текст сообщения
     */
    public void sendMessage(String to, String text) {
        sendJson("SEND_MESSAGE", Map.of("to", to, "text", text));
    }

    /**
     * Запрашивает историю переписки с указанным пользователем.
     *
     * @param targetLogin логин собеседника
     */
    public void requestHistory(String targetLogin) {
        sendJson("GET_HISTORY", Map.of("targetLogin", targetLogin));
    }

    /**
     * Запрашивает редактирование существующего сообщения.
     *
     * @param to           логин получателя диалога
     * @param originalText исходный текст сообщения
     * @param newText      новый текст сообщения
     */
    public void editMessage(String to, String originalText, String newText) {
        sendJson("EDIT_MESSAGE", Map.of(
                "to", to,
                "originalText", originalText,
                "newText", newText
        ));
    }

    /**
     * Запрашивает удаление сообщения.
     *
     * @param to   логин получателя диалога
     * @param text текст сообщения для удаления
     */
    public void deleteMessage(String to, String text) {
        sendJson("DELETE_MESSAGE", Map.of(
                "to", to,
                "text", text
        ));
    }

    /**
     * Формирует и отправляет JSON-сообщение на сервер.
     *
     * @param type    тип сообщения
     * @param payload данные сообщения
     */
    private void sendJson(String type, Map<String, ?> payload) {
        try {
            String json = mapper.writeValueAsString(Map.of("type", type, "payload", payload));
            send(json);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send message of type: " + type, e);
        }
    }

    /**
     * Обрабатывает успешный ответ авторизации от сервера.
     * <p>
     * Метод извлекает отображаемое имя пользователя из ответа, закрывает окно авторизации,
     * создаёт и отображает окно чата, связывает его с клиентом и запрашивает список контактов.
     * Все операции с интерфейсом выполняются в потоке Event Dispatch Thread.
     *
     * @param json ответ сервера в формате Map с типом "AUTH_SUCCESS"
     */
    @SuppressWarnings("unchecked")
    private void handleAuthSuccess(Map<String, Object> json) {
        String displayName = (String) ((Map<String, Object>) json.get("payload")).get("displayName");

        if (loginFrame != null) {
            SwingUtilities.invokeLater(() -> {
                loginFrame.dispose();
                ChatFrame chat = new ChatFrame(displayName, this);
                chat.setVisible(true);
                setChatFrame(chat);
                requestContacts();
                logger.info("User authenticated: " + displayName);
            });
        }
    }

    /**
     * Обрабатывает ошибку авторизации от сервера.
     * <p>
     * Метод извлекает код ошибки из ответа и передаёт его в окно авторизации
     * для отображения пользователю. Выполняется в Event Dispatch Thread.
     *
     * @param json ответ сервера в формате Map с типом "AUTH_ERROR"
     */
    @SuppressWarnings("unchecked")
    private void handleAuthError(Map<String, Object> json) {
        String code = (String) ((Map<String, Object>) json.get("payload")).get("code");
        if (loginFrame != null) {
            SwingUtilities.invokeLater(() -> loginFrame.onAuthError(code));
        }
        logger.warning("Authentication failed with code: " + code);
    }

    /**
     * Обрабатывает список контактов, полученный от сервера.
     * <p>
     * Преобразует сырые данные из JSON в типобезопасные объекты {@link ContactUI}
     * и передаёт их в окно чата для отображения в интерфейсе.
     *
     * @param json ответ сервера в формате Map с типом "CONTACTS_LIST"
     */
    @SuppressWarnings("unchecked")
    private void handleContactsList(Map<String, Object> json) {
        List<Map<String, Object>> list = (List<Map<String, Object>>)
                ((Map<String, Object>) json.get("payload")).get("contacts");

        List<ContactUI> contacts = list.stream()
                .map(c -> new ContactUI(
                        (String) c.get("login"),
                        (String) c.get("displayName"),
                        (Boolean) c.get("online")
                ))
                .toList();

        if (chatFrame != null) {
            SwingUtilities.invokeLater(() -> chatFrame.updateContactsList(contacts));
        }
    }

    /**
     * Обрабатывает входящее сообщение от другого пользователя.
     * <p>
     * Метод игнорирует сообщения, отправленные текущим пользователем (они уже
     * отображены локально при отправке). Для чужих сообщений вызывает обновление
     * интерфейса чата в Event Dispatch Thread.
     *
     * @param json сообщение сервера в формате Map с типом "NEW_MESSAGE"
     */
    @SuppressWarnings("unchecked")
    private void handleIncomingMessage(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String from = (String) payload.get("from");
        String text = (String) payload.get("text");

        if (from.equals(currentUser)) {
            return;
        }

        if (chatFrame != null) {
            SwingUtilities.invokeLater(() -> {
                chatFrame.addMessageToChat(text, false);
            });
        }
    }

    /**
     * Обрабатывает историю переписки, полученную от сервера.
     * <p>
     * Очищает текущее содержимое чата, преобразует каждое сообщение из истории
     * и отображает его с правильным визуальным стилем (свои/чужие). После загрузки
     * всех сообщений выполняет прокрутку вниз.
     *
     * @param json ответ сервера в формате Map с типом "HISTORY"
     */
    @SuppressWarnings("unchecked")
    private void handleHistory(Map<String, Object> json) {
        List<Map<String, Object>> messages = (List<Map<String, Object>>)
                ((Map<String, Object>) json.get("payload")).get("messages");

        if (chatFrame != null) {
            SwingUtilities.invokeLater(() -> {
                chatFrame.clearChat();

                for (Map<String, Object> msg : messages) {
                    String from = (String) msg.get("from");
                    String text = (String) msg.get("text");
                    boolean isMine = from.equals(currentUser);
                    chatFrame.addMessageToChat(text, isMine);
                }
                chatFrame.scrollToBottom();
            });
        }
    }

    /**
     * Обрабатывает сообщение об ошибке от сервера.
     * <p>
     * Извлекает текст ошибки из ответа и записывает его в лог с уровнем SEVERE.
     *
     * @param json сообщение сервера в формате Map с типом "ERROR"
     */
    @SuppressWarnings("unchecked")
    private void handleError(Map<String, Object> json) {
        String msg = (String) ((Map<String, Object>) json.get("payload")).get("message");
        logger.severe("Server error received: " + msg);
    }

    /**
     * Обрабатывает уведомление об обновлении сообщения.
     * <p>
     * Передаёт исходный и новый текст сообщения в окно чата для поиска
     * и визуального обновления соответствующего пузыря.
     *
     * @param json уведомление сервера в формате Map с типом "MESSAGE_UPDATED"
     */
    @SuppressWarnings("unchecked")
    private void handleUpdatedMessage(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String originalText = (String) payload.get("originalText");
        String newText = (String) payload.get("newText");

        if (chatFrame != null) {
            chatFrame.updateMessage(originalText, newText);
        }
    }

    /**
     * Обрабатывает уведомление об удалении сообщения.
     * <p>
     * Передаёт текст удалённого сообщения в окно чата для поиска
     * и удаления соответствующего пузыря из интерфейса.
     *
     * @param json уведомление сервера в формате Map с типом "MESSAGE_DELETED"
     */
    @SuppressWarnings("unchecked")
    private void handleDeletedMessage(Map<String, Object> json) {
        Map<String, Object> payload = (Map<String, Object>) json.get("payload");
        String text = (String) payload.get("text");

        if (chatFrame != null) {
            chatFrame.deleteMessageByContent(text);
        }
    }
}