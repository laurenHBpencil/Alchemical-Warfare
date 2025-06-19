package roguelike;

import java.awt.Color;

public class Trap
{
    private char glyph;
    private Color color;
    private String name;
    private int x, y, z;
    private Effect effect;

    public Trap(char glyph, Color color, String name)
    {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    public char glyph() { return glyph; }
    public Color color() { return color; }
    public String name() { return name; }

    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setZ(int z) { this.z = z; }

    public void setEffect(Effect effect)
    {
        this.effect = effect;
    }

    public Effect effect()
    {
        return effect;
    }
}
