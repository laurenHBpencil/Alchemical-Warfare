package roguelike;

public class BatAI extends CreatureAI {

    public BatAI(Creature creature) {
        super(creature);
    }

    public void onUpdate()
    {
        wander();
        wander();
        if (creature.isConfused())
        {
            //move randomly or in the opposite direction
            int mx = (int)(Math.random() * 3) - 1;
            int my = (int)(Math.random() * 3) - 1;
            creature.moveBy(mx, my, 0);
        } else {
            super.onUpdate(); //go back to normal behavior
        }
    }
}