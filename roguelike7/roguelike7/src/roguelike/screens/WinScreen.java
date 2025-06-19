package roguelike.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

public class WinScreen implements Screen {
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You won!", 1, 1);
        terminal.writeCenter("Press [Enter] to play again", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            return new PlayScreen();
        }
        return this;
    }
}