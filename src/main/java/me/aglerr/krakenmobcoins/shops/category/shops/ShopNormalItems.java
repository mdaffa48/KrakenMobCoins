package me.aglerr.krakenmobcoins.shops.category.shops;

import java.util.List;

public class ShopNormalItems {

    private final String category;
    private final String configKey;
    private final String type;
    private final String material;
    private final int amount;
    private final String name;
    private final boolean glow;
    private final List<String> lore;
    private final List<String> commands;
    private final int slot;
    private final List<Integer> slots;
    private final double price;
    private final int limit;
    private final boolean useStock;
    private final int stock;

    public ShopNormalItems(String category, String configKey, String type, String material, int amount, String name,
                           boolean glow, List<String> lore, List<String> commands, int slot, List<Integer> slots,
                           double price, int limit, boolean useStock, int stock) {
        this.category = category;
        this.configKey = configKey;
        this.type = type;
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.glow = glow;
        this.lore = lore;
        this.commands = commands;
        this.slot = slot;
        this.slots = slots;
        this.price = price;
        this.limit = limit;
        this.useStock = useStock;
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public boolean isGlow() {
        return glow;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getSlot() {
        return slot;
    }

    public double getPrice() {
        return price;
    }

    public int getLimit() {
        return limit;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public String getConfigKey() {
        return configKey;
    }

    public boolean isUseStock() {
        return useStock;
    }

    public int getStock() {
        return stock;
    }
}
