package ru.moskalev.client.util;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Утилитный класс для создания и масштабирования иконок приложения.
 * <p>
 * Предоставляет статические методы для загрузки графических ресурсов
 * из classpath и их адаптации под требуемые размеры интерфейса.
 */
public class IconCreator {

    private static final Logger logger = Logger.getLogger(IconCreator.class.getName());

    private static final String ICON_PATH_SEND = "/icons/send-icon.png";
    private static final String ICON_PATH_LOGO = "/icons/telegram-logo.png";
    private static final int SEND_ICON_SIZE = 20;
    private static final int LOGO_ICON_SIZE = 140;
    private static final int IMAGE_SCALE_HINT = Image.SCALE_SMOOTH;

    private IconCreator() {
    }

    /**
     * Создаёт иконку кнопки отправки сообщения.
     * <p>
     * Загружает изображение из ресурсов, масштабирует до 20×20 пикселей
     * с использованием сглаживания. При ошибке загрузки возвращает пустую иконку
     * и записывает предупреждение в лог.
     *
     * @return иконка для кнопки отправки или пустая иконка при ошибке
     */
    public static Icon createSendIcon() {
        try {
            ImageIcon icon = new ImageIcon(IconCreator.class.getResource(ICON_PATH_SEND));
            Image scaledImg = icon.getImage().getScaledInstance(
                    SEND_ICON_SIZE, SEND_ICON_SIZE, IMAGE_SCALE_HINT);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load send icon from: " + ICON_PATH_SEND, e);
            return new ImageIcon();
        }
    }

    /**
     * Создаёт иконку логотипа для приветственного экрана.
     * <p>
     * Загружает изображение из ресурсов, масштабирует до 140×140 пикселей
     * с использованием сглаживания. При ошибке загрузки возвращает пустую иконку
     * и записывает предупреждение в лог.
     *
     * @return иконка логотипа или пустая иконка при ошибке
     */
    public static Icon createPaperPlaneIcon() {
        try {
            ImageIcon icon = new ImageIcon(IconCreator.class.getResource(ICON_PATH_LOGO));
            Image scaledImg = icon.getImage().getScaledInstance(
                    LOGO_ICON_SIZE, LOGO_ICON_SIZE, IMAGE_SCALE_HINT);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load logo icon from: " + ICON_PATH_LOGO, e);
            return new ImageIcon();
        }
    }
}