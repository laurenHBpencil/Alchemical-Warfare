package roguelike;

public class Inventory {

    private Item[] items;
    public Item[] getItems() { return items; }
    public Item get(int i) { return items[i]; }

    public Inventory(int max){
        items = new Item[max];
    }

    public void add(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == null){
                items[i] = item;
                break;
            }
        }
    }

    public void remove(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == item){
                items[i] = null;
                return;
            }
        }
    }

    public boolean contains(Item item) {
        for (Item i : items){
            if (i == item)
                return true;
        }
        return false;
    }

    public boolean isFull(){
        int size = 0;
        for (int i = 0; i < items.length; i++){
            if (items[i] != null)
                size++;
        }
        return size == items.length;
    }

    public Item getAmmo(String type)
    {
        for (Item item : getItems())
        {
            if (item != null)
            {
                if (type.equals(item.ammoType()) && item.ammoCount() > 0)
                {
                    return item;
                }
            }
        }
        return null;
    }


}
