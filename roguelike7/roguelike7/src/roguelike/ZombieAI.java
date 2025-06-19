package roguelike;

import java.util.List;

public class ZombieAI extends CreatureAI {
    private Creature player;

    public ZombieAI(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    public void onUpdate()
    {
        if (creature.isConfused())
        {
            int mx = (int)(Math.random() * 3) - 1;
            int my = (int)(Math.random() * 3) - 1;
            creature.moveBy(mx, my, 0);
            return;
        }

        if (Math.random() < 0.2)
            return;

        if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else
            wander();
    }

}
