package me.aglerr.krakenmobcoins.shops.category.mainmenu;

import java.util.List;

public class MainMenuItems {

    private final String type;
    private final String material;
    private final String name;
    private final int slot;
    private final List<Integer> slots;
    private final List<String> lore;
    private final boolean glow;

    private final String category;

    public MainMenuItems(String type, String material, String name, int slot, List<Integer> slots, List<String> lore,
                         boolean glow, String category){
        this.type = type;
        this.material = material;
        this.name = name;
        this.slot = slot;
        this.slots = slots;
        this.lore = lore;
        this.glow = glow;
        this.category = category;
    }


    public String getType() {
        return type;
    }

    public String getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isGlow() {
        return glow;
    }

    public String getCategory() {
        return category;
    }
}
