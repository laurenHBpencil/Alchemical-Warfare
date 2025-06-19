package roguelike;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class World
{
    private Tile[][][] tiles;
    private Item[][][] items;
    private Trap[][][] traps;


    private int width;
    public int width() { return width; }

    private int height;
    public int height() { return height; }

    private int depth;
    public int depth() { return depth; }

    private List<Creature> creatures;

    public World(Tile[][][] tiles){
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;

        this.creatures = new ArrayList<Creature>();
        this.items = new Item[width][height][depth];
        traps = new Trap[width][height][depth];

    }

    public Creature creature(int x, int y, int z){
        for (Creature c : creatures){
            if (c.x == x && c.y == y && c.z == z)
                return c;
        }
        return null;
    }

    public Item item(int x, int y, int z){
        return items[x][y][z];
    }

    public List<Creature> creatures(){
        return creatures;
    }

    public Tile tile(int x, int y, int z){
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
            return Tile.BOUNDS;
        else
            return tiles[x][y][z];
    }

    public char glyph(int x, int y, int z){
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.glyph();

        if (item(x,y,z) != null)
            return item(x,y,z).glyph();

        return tile(x, y, z).glyph();
    }

    public Color color(int x, int y, int z){
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.color();

        if (item(x,y,z) != null)
            return item(x,y,z).color();

        return tile(x, y, z).color();
    }

    public void dig(int x, int y, int z) {
        if (tile(x,y,z).isDiggable())
            tiles[x][y][z] = Tile.FLOOR;
    }

    public void remove(Creature other) {
        creatures.remove(other);
    }

    public void remove(Item item) {
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                for (int z = 0; z < depth; z++){
                    if (items[x][y][z] == item) {
                        items[x][y][z] = null;
                        return;
                    }
                }
            }
        }
    }

    public void addAtEmptyLocation(Creature creature, int z){
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        }
        while (!tile(x,y,z).isGround() || creature(x,y,z) != null);

        creature.x = x;
        creature.y = y;
        creature.z = z;
        creatures.add(creature);
    }

    public void addAtEmptyLocation(Item item, int depth) {
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        }
        while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);

        items[x][y][depth] = item;
    }

    public void remove(int x, int y, int z) {
        items[x][y][z] = null;
    }

    public boolean addAtEmptySpace(Item item, int x, int y, int z){
        if (item == null)
            return true;

        List<Point> points = new ArrayList<Point>();
        List<Point> checked = new ArrayList<Point>();

        points.add(new Point(x, y, z));

        while (!points.isEmpty()){
            Point p = points.remove(0);
            checked.add(p);

            if (!tile(p.x, p.y, p.z).isGround())
                continue;

            if (items[p.x][p.y][p.z] == null){
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name());
                return true;
            } else {
                List<Point> neighbors = p.neighbors8();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
        return false;
    }

    public void update(){
        List<Creature> toUpdate = new ArrayList<Creature>(creatures);
        for (Creature creature : toUpdate){
            creature.update();
        }
    }

    public Trap trap(int x, int y, int z) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
            return null;
        return traps[x][y][z];
    }

    public void add(Trap trap)
    {
        for (int ox = -1; ox < 2; ox++)
        {
            for (int oy = -1; oy < 2; oy++)
            {
                int nx = trap.x() + ox;
                int ny = trap.y() + oy;
                int nz = trap.z();

                if (tile(nx, ny, nz).isGround() && trap(nx, ny, nz) == null)
                {
                    traps[nx][ny][nz] = trap;
                    trap.setX(nx);
                    trap.setY(ny);
                    trap.setZ(nz);
                    return;
                }
            }
        }
    }

}