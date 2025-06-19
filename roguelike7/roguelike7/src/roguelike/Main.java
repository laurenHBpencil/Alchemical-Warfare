package roguelike;

import javax.swing.JFrame;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import roguelike.screens.Screen;
import roguelike.screens.StartScreen;

public class Main extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;

    public Main(){
        super();
        terminal = new AsciiPanel();
        add(terminal);
        pack();
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
    }

    public void repaint(){
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }

    public void keyPressed(KeyEvent e) {
        screen = screen.respondToUserInput(e);
        repaint();
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e) { }

    public static void main(String[] args) {
        Main app = new Main();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}