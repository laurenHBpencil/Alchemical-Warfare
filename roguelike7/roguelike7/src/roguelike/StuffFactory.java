package roguelike;

import asciiPanel.AsciiPanel;

import java.util.List;

public class StuffFactory {
    private World world;

    public StuffFactory(World world){
        this.world = world;
    }

    public Creature newPlayer(List<String> messages, FieldOfView fov){
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite, "player",100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new PlayerAI(player, messages, fov);
        player.inventory().add(newPotionOfConfusion(0));//gives potion of confusiton
        newPoisonTrap(0); //places trap somewhere near p
        player.inventory().add(newPotionOfConfusion2(0));
        player.inventory().add(newPotionOfPoison2(0));

        Trap testTrap = newPoisonTrap(0);
        testTrap.setX(player.x + 1);
        testTrap.setY(player.y);
        testTrap.setZ(player.z);
        world.add(testTrap);
        System.out.println("Trap placed: " + testTrap.x() + ", " + testTrap.y());


        player.inventory().add(newBow(0));
        player.inventory().add(newQuiver(0));


        return player;
    }


    public Creature newFungus(int depth){
        Creature fungus = new Creature(world, 'f', AsciiPanel.green, "fungus", 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAI(fungus, this);
        return fungus;
    }

    public Creature newBat(int depth){
        Creature bat = new Creature(world, 'b', AsciiPanel.yellow, "bat", 15, 5, 0);
        world.addAtEmptyLocation(bat, depth);
        new BatAI(bat);
        return bat;
    }

    public Creature newZombie(int depth, Creature player){
        Creature zombie = new Creature(world, 'z', AsciiPanel.white, "zombie", 50, 10, 10);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAI(zombie, player);
        return zombie;
    }

    public Creature newGoblin(int depth, Creature player){
        Creature goblin = new Creature(world, 'g', AsciiPanel.brightGreen, "goblin", 55, 12, 5);
        goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmor(depth));
        world.addAtEmptyLocation(goblin, depth);
        new GoblinAI(goblin, player);
        return goblin;
    }

    public Item newVictoryItem(int depth){
        Item item = new Item('*', AsciiPanel.brightWhite, "teddy bear");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newRock(int depth){
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        rock.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }

    public Item newBow(int depth) {
        Item item = new Item(')', AsciiPanel.yellow, "bow");
        item.setAmmoType("arrow");
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        return item;
    }


    public Item newDagger(int depth){
        Item item = new Item(')', AsciiPanel.white, "dagger");
        item.modifyAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth){
        Item item = new Item(')', AsciiPanel.brightWhite, "sword");
        item.modifyAttackValue(10);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newStaff(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "staff");
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newLightArmor(int depth){
        Item item = new Item('[', AsciiPanel.green, "tunic");
        item.modifyDefenseValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmor(int depth){
        Item item = new Item('[', AsciiPanel.white, "chainmail");
        item.modifyDefenseValue(4);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmor(int depth){
        Item item = new Item('[', AsciiPanel.brightWhite, "platemail");
        item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newPotionOfHealth(int depth){
        Item item = new Item('!', AsciiPanel.white, "health potion");
        item.setQuaffEffect(new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(15);
                creature.doAction("look healthier");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newPotionOfPoison(int depth){
        Item item = new Item('!', AsciiPanel.white, "poison potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature){
                creature.doAction("look sick");
            }

            public void update(Creature creature){
                super.update(creature);
                creature.modifyHp(-1);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newPotionOfWarrior(int depth){
        Item item = new Item('!', AsciiPanel.white, "warrior's potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature){
                creature.modifyAttackValue(5);
                creature.modifyDefenseValue(5);
                creature.doAction("look stronger");
            }
            public void end(Creature creature){
                creature.modifyAttackValue(-5);
                creature.modifyDefenseValue(-5);
                creature.doAction("look less strong");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newPotionOfConfusion(int depth)
    {
        Item potion = new Item('!', AsciiPanel.brightMagenta, "potion of confusion");
        potion.setQuaffEffect(new Effect(10)
        {
            public void start(Creature creature)
            {
                creature.doAction("look confused");
            }

            public void update(Creature creature) {}

            public void end(Creature creature)
            {
                creature.doAction("seem less confused");
            }
        });
        return potion;
    }


    public Item randomWeapon(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newDagger(depth);
            case 1: return newSword(depth);
            case 2: return newBow(depth);
            default: return newStaff(depth);
        }
    }

    public Item randomArmor(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newLightArmor(depth);
            case 1: return newMediumArmor(depth);
            default: return newHeavyArmor(depth);
        }
    }

    public Item randomPotion(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newPotionOfHealth(depth);
            case 1: return newPotionOfPoison(depth);
            case 2: return newPotionOfWarrior(depth);
            default: return newPotionOfConfusion(depth);
        }
    }

    public Item newEdibleWeapon(int depth){
        Item item = new Item(')', AsciiPanel.yellow, "baguette");
        item.modifyAttackValue(3);
        item.modifyFoodValue(50);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Trap newPoisonTrap(int depth)
    {
        System.out.println("trap spawned");
        Trap trap = new Trap('^', AsciiPanel.green, "poison spike");

        trap.setEffect(new Effect(20, "poison")
        {
            public void start(Creature creature)
            {
                creature.doAction("step on a poison spike and feel sick");
            }

            public void update(Creature creature)
            {
                super.update(creature);
                creature.modifyHp(-1);
            }

            public void end(Creature creature)
            {
                creature.doAction("feel better");
            }
        });
        return trap;
    }

    public Item newQuiver(int depth)
    {
        Item item = new Item('}', AsciiPanel.brightYellow, "quiver of arrows");
        item.setAmmoType("arrow");
        item.setAmmoCount(10);
        return item;
    }

    public Item newPotionOfConfusion2(int depth)
    {
        Item potion = new Item('!', AsciiPanel.brightMagenta, "potion of confusion");
        potion.setSplashRadius(1);
        potion.setQuaffEffect(new Effect(10, "confused") {
            public void start(Creature c) { c.doAction("look confused"); }
            public void end(Creature c) { c.doAction("seem less confused"); }
            public void update(Creature c) { }
        });
        return potion;
    }
    public Item newPotionOfPoison2(int depth){
        Item item = new Item('!', AsciiPanel.white, "poison potion");
        item.setSplashRadius(1); // 👈 splash enabled
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature){
                creature.doAction("look sick");
            }

            public void update(Creature creature){
                super.update(creature);
                creature.modifyHp(-1);
            }

            public void end(Creature creature){
                creature.doAction("feel better");
            }
        });

        return item;
    }





}
