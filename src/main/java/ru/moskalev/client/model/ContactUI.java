package ru.moskalev.client.model;
/**
 * DTO для передачи информации о контакте в интерфейс.
 *
 * @param login логин пользователя
 * @param displayName отображаемое имя
 * @param online статус подключения
 */
public record ContactUI(String login, String displayName, boolean online) {}