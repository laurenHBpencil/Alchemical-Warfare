package roguelike;

import java.util.List;

public class GoblinAI extends CreatureAI {
    private Creature player;

    public GoblinAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    public void onUpdate() {
        if (creature.isConfused())
        {
            // move randomly or in the opposite direction
            int mx = (int)(Math.random() * 3) - 1;
            int my = (int)(Math.random() * 3) - 1;
            creature.moveBy(mx, my, 0);
        } else {
            super.onUpdate(); //go back to normal behavior
        }
        if (canRangedWeaponAttack(player))
            creature.rangedWeaponAttack(player);
        else if (canThrowAt(player))
            creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
        else if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else if (canPickup())
            creature.pickup();
        else
            wander();
    }
}
