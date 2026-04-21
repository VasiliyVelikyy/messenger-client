package ru.moskalev.client;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import ru.moskalev.client.ui.frames.WelcomeFrame;

import javax.swing.*;

public class ClientApp {

    public static void main(String[] args) {
        FlatMacDarkLaf.setup();

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);


        SwingUtilities.invokeLater(() -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
        });
    }
}
