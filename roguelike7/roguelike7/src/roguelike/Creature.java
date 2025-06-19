package roguelike;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Creature {
    private World world;

    public int x;
    public int y;
    public int z;

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    private Inventory inventory;
    public Inventory inventory() { return inventory; }

    private int maxHp;
    public int maxHp() { return maxHp; }

    private int hp;
    public int hp() { return hp; }

    private int xp;
    public int xp() { return xp; }

    //new list
    private List<Effect> effects = new ArrayList<Effect>();


    public void modifyXp(int amount) {
        xp += amount;

        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);

        while (xp > (int)(Math.pow(level, 1.5) * 20)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2);
        }
    }

    private int level;
    public int level() { return level; }

    private int attackValue;
    public void modifyAttackValue(int value) { attackValue += value; }
    public int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    private int defenseValue;
    public void modifyDefenseValue(int value) { defenseValue += value; }
    public int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.defenseValue())
                + (armor == null ? 0 : armor.defenseValue());
    }

    private int visionRadius;
    public int visionRadius() { return visionRadius; }

    private String name;
    public String name() { return name; }

    //private List<Effect> effects;
    public List<Effect> effects(){ return effects; }

    public boolean canSee(int wx, int wy, int wz){
        return ai.canSee(wx, wy, wz);
    }

    public Creature(World world, char glyph, Color color, String name, int maxHp, int attack, int defense){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;
        this.regenHpPer1000 = 10;

        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;

        this.level = 1;

        inventory = new Inventory(20);
        this.effects = new ArrayList<Effect>();
    }

    private CreatureAI ai;
    public void setCreatureAI(CreatureAI ai) {
        this.ai = ai;
    }

    public void dig(int wx, int wy, int wz) {
        modifyFood(-10);
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    public void pickup(){
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null){
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    public void drop(Item item){
        if (world.addAtEmptySpace(item, x, y, z)){
            doAction("drop a " + item.name());
            inventory.remove(item);
            unequip(item);
        } else {
            notify("There's nowhere to drop the %s.", item.name());
        }
    }

    public void moveBy(int mx, int my, int mz){
        if (mx==0 && my==0 && mz==0)
            return;

        Tile tile = world.tile(x+mx, y+my, z+mz);

        if (mz == -1){
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z+mz+1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1){
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z+mz+1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }
        Trap trap = world.trap(x + mx, y + my, z + mz);
        if (trap != null && trap.effect() != null)
        {
            this.addEffect(trap.effect());
            doAction("trigger a %s!", trap.name());
        }


        Creature other = world.creature(x+mx, y+my, z+mz);

        if (other == null)
            ai.onEnter(x+mx, y+my, z+mz, tile);
        else
            attack(other);
    }

    public void attack(Creature other){
        int amount = Math.max(0, attackValue() - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        doAction("attack the '%s' for %d damage", other.name, amount);

        other.modifyHp(-amount);
        if (other.hp < 1)
            gainXp(other);
    }

    public void gainXp(Creature other){
        int amount = other.maxHp
                + other.attackValue()
                + other.defenseValue()
                - level * 2;

        if (amount > 0)
            modifyXp(amount);
    }

    public void modifyHp(int amount) {
        hp += amount;
        if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }
    public void gainMaxHp() {
        maxHp += 10;
        hp += 10;
        doAction("look healthier");
    }

    public void gainAttackValue() {
        attackValue += 2;
        doAction("look stronger");
    }

    public void gainDefenseValue() {
        defenseValue += 2;
        doAction("look tougher");
    }

    public void gainVision() {
        visionRadius += 1;
        doAction("look more aware");
    }


    private void leaveCorpse(){
        Item corpse = new Item('%', color, name + " corpse");
        corpse.modifyFoodValue(maxHp);
        world.addAtEmptySpace(corpse, x, y, z);
        for (Item item : inventory.getItems()){
            if (item != null)
                drop(item);
        }
    }

    private int regenHpCooldown;
    private int regenHpPer1000;
    public void modifyRegenHpPer1000(int amount) { regenHpPer1000 += amount; }

    private void regenerateHealth(){
        regenHpCooldown -= regenHpPer1000;
        if (regenHpCooldown < 0){
            modifyHp(1);
            modifyFood(-1);
            regenHpCooldown += 1000;
        }
    }

    private int maxFood;
    public int maxFood() { return maxFood; }

    private int food;
    public int food() { return food; }

    public void modifyFood(int amount) {
        food += amount;

        if (food > maxFood) {
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000);
        }
    }

    public boolean isPlayer(){
        return glyph == '@';
    }

    private Item weapon;
    public Item weapon() { return weapon; }

    private Item armor;
    public Item armor() { return armor; }

    public void unequip(Item item){
        if (item == null)
            return;

        if (item == armor){
            doAction("remove a " + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    public void equip(Item item){
        if (!inventory.contains(item)) {
            if (inventory.isFull()) {
                notify("Can't equip %s since you're holding too much stuff.", item.name());
                return;
            } else {
                world.remove(item);
                inventory.add(item);
            }
        }

        if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() + item.rangedAttackValue() >= item.defenseValue()){
            unequip(weapon);
            doAction("wield a " + item.name());
            weapon = item;
        } else {
            unequip(armor);
            doAction("put on a " + item.name());
            armor = item;
        }
    }

    public void quaff(Item item){
        doAction("quaff a " + item.name());
        consume(item);
    }

    public void eat(Item item){
        doAction("eat a " + item.name());
        consume(item);
    }

    private void consume(Item item){
        if (item.foodValue() < 0)
            notify("Gross!");

        addEffect(item.quaffEffect());

        modifyFood(item.foodValue());
        getRidOf(item);
    }

    private void addEffect(Effect effect){
        if (effect == null)
            return;

        effect.start(this);
        effects.add(effect);
    }

    public void update(){
        modifyFood(-1);
        regenerateHealth();
        updateEffects();
        ai.onUpdate();
    }

    private void updateEffects(){
        List<Effect> done = new ArrayList<Effect>();

        for (Effect effect : effects){
            effect.update(this);
            if (effect.isDone()) {
                effect.end(this);
                done.add(effect);
            }
        }

        effects.removeAll(done);
    }

    public String details() {
        return String.format("     level:%d     attack:%d     defense:%d     hp:%d", level, attackValue(), defenseValue(), hp);
    }

    public Tile realTile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }


    public Tile tile(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.tile(wx, wy, wz);
        else
            return ai.rememberedTile(wx, wy, wz);
    }


    public Creature creature(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.creature(wx, wy, wz);
        else
            return null;
    }


    public Item item(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.item(wx, wy, wz);
        else
            return null;
    }

    public void throwItem(Item item, int wx, int wy, int wz) {
        Point end = new Point(x, y, 0);

        for (Point p : new Line(x, y, wx, wy)){
            if (!realTile(p.x, p.y, z).isGround())
                break;
            end = p;
        }

        wx = end.x;
        wy = end.y;

        Creature c = creature(wx, wy, wz);

        if (c != null)
            throwAttack(item, c);
        else
            doAction("throw a %s", item.name());

        if (item.quaffEffect() != null && c != null)
            getRidOf(item);
        else
            putAt(item, wx, wy, wz);
    }

    public void meleeAttack(Creature other){
        commonAttack(other, attackValue(), "attack the %s for %d damage", other.name);
    }

    private void throwAttack(Item item, Creature target) 
    {
        int radius = item.splashRadius();

        for (Creature c : neighbors8(target.x, target.y, target.z)) 
        {
            if (c == null) continue;
            if (item.quaffEffect() != null) 
            {
                c.addEffect(item.quaffEffect());
                c.doAction("get splashed by the %s!", item.name());
            }
        }

        commonAttack(target, attackValue / 2 + item.thrownAttackValue(),
                "throw a %s at the %s for %d damage", item.name(), target.name());
    }

    public List<Creature> neighbors8(int cx, int cy, int cz)
    {
        List<Creature> list = new ArrayList<Creature>();

        for (int ox = -1; ox <= 1; ox++)
        {
            for (int oy = -1; oy <= 1; oy++)
            {
                if (!(ox == 0 && oy == 0))
                {
                    Creature other = world.creature(cx + ox, cy + oy, cz);
                    if (other != null)
                        list.add(other);
                }
            }
        }

        return list;
    }




    public void rangedWeaponAttack(Creature other)
    {
        Item bow = weapon;
        String ammoType = bow.ammoType();

        if (ammoType != null)
        {
            Item quiver = inventory.getAmmo(ammoType);

            if (quiver == null || quiver.ammoCount() <= 0)
            {
                notify("Out of %ss!", ammoType);
                return;
            }

            quiver.modifyAmmoCount(-1);
        }

        commonAttack(other, attackValue / 2 + bow.rangedAttackValue(), "fire a %s at the %s for %d damage", bow.name(), other.name);
    }



    private void commonAttack(Creature other, int attack, String action, Object ... params) {
        modifyFood(-2);

        int amount = Math.max(0, attack - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        Object[] params2 = new Object[params.length+1];
        for (int i = 0; i < params.length; i++){
            params2[i] = params[i];
        }
        params2[params2.length - 1] = amount;

        doAction(action, params2);

        other.modifyHp(-amount);

        if (other.hp < 1)
            gainXp(other);
    }

    private void getRidOf(Item item){
        inventory.remove(item);
        unequip(item);
    }

    private void putAt(Item item, int wx, int wy, int wz){
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    public boolean canEnter(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
    }

    public void notify(String message, Object ... params){
        ai.onNotify(String.format(message, params));
    }

    public void doAction(String message, Object ... params){
        int r = 9;
        for (int ox = -r; ox < r+1; ox++){
            for (int oy = -r; oy < r+1; oy++){
                if (ox*ox + oy*oy > r*r)
                    continue;

                Creature other = world.creature(x+ox, y+oy, z);

                if (other == null)
                    continue;

                if (other == this)
                    other.notify("You " + message + ".", params);
                else if (other.canSee(x, y, z))
                    other.notify(String.format("The %s %s.", name, makeSecondPerson(message)), params);
            }
        }
    }

    private String makeSecondPerson(String text){
        String[] words = text.split(" ");
        words[0] = words[0] + "s";

        StringBuilder builder = new StringBuilder();
        for (String word : words){
            builder.append(" ");
            builder.append(word);
        }

        return builder.toString().trim();
    }
    public Effect effect(String name)
    {
        for (Effect e : effects) {
            if (e.name() != null && e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }


    public boolean isConfused()
    {
        return effect("confused") != null;
    }

}
