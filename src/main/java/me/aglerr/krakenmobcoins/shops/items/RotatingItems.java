package me.aglerr.krakenmobcoins.shops.items;

import java.util.List;

public class RotatingItems {

    private final String material;
    private final String name;
    private final int slot;
    private final List<Integer> slots;
    private final List<String> lore;
    private final boolean glow;

    public RotatingItems(String material, String name, int slot, List<Integer> slots, List<String> lore, boolean glow) {
        this.material = material;
        this.name = name;
        this.slot = slot;
        this.slots = slots;
        this.lore = lore;
        this.glow = glow;
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
}
