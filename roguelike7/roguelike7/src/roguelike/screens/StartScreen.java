package roguelike.screens;

import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;

public class StartScreen implements Screen {
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("Welcome to the Roguelike Game", 1, 1);
        terminal.writeCenter("Press [Enter] to start", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            return new PlayScreen();
        }
        return this;
    }
}