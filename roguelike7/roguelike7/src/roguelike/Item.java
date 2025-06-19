package roguelike;

import java.awt.Color;

public class Item {

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    private String name;
    public String name() { return name; }

    private int foodValue;
    public int foodValue() { return foodValue; }
    public void modifyFoodValue(int amount) { foodValue += amount; }

    private int attackValue;
    public int attackValue() { return attackValue; }
    public void modifyAttackValue(int amount) { attackValue += amount; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }
    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    private int thrownAttackValue;
    public int thrownAttackValue() { return thrownAttackValue; }
    public void modifyThrownAttackValue(int amount) { thrownAttackValue += amount; }

    private int rangedAttackValue;
    public int rangedAttackValue() { return rangedAttackValue; }
    public void modifyRangedAttackValue(int amount) { rangedAttackValue += amount; }

    private Effect quaffEffect;
    public Effect quaffEffect() { return quaffEffect; }
    public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }

    public Item(char glyph, Color color, String name){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.thrownAttackValue = 1;
    }

    public String details() {
        String details = "";

        if (attackValue != 0)
            details += "     attack:" + attackValue;

        if (thrownAttackValue != 1)
            details += "  thrown:" + thrownAttackValue;

        if (defenseValue != 0)
            details += "     defense:" + defenseValue;

        if (foodValue != 0)
            details += "     food:" + foodValue;

        return details;
    }

    private String ammoType;
    private int ammoCount;

    public void setAmmoType(String type)
    {
        this.ammoType = type;
    }
    public String ammoType()
    {
        return ammoType;
    }

    public void setAmmoCount(int count)
    {
        this.ammoCount = count;
    }
    public int ammoCount()
    {
        return ammoCount;
    }
    public void modifyAmmoCount(int amount)
    {
        this.ammoCount += amount;
    }

    private int splashRadius = 0;
    public void setSplashRadius(int radius)
    {
        this.splashRadius = radius;
    }

    public int splashRadius()
    {
        return splashRadius;
    }



}