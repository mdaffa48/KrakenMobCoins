package me.aglerr.krakenmobcoins.shops.items;

import java.util.List;

public class ShopItems {

    private final String configKey;
    private final String material;
    private final int amount;
    private final String name;
    private final boolean glowing;
    private final List<String> lore;
    private final List<String> commands;
    private final double price;
    private final boolean special;
    private final int limit;
    private final boolean useStock;
    private final int stock;

    public ShopItems(String configKey, String material, int amount, String name, boolean glowing, List<String> lore,
                     List<String> commands, double price, boolean special, int limit, boolean useStock, int stock){
        this.configKey = configKey;
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.glowing = glowing;
        this.lore = lore;
        this.commands = commands;
        this.price = price;
        this.special = special;
        this.limit = limit;
        this.useStock = useStock;
        this.stock = stock;

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

    public boolean isGlowing() {
        return glowing;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getCommands() {
        return commands;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSpecial() {
        return special;
    }

    public String getConfigKey() { return configKey; }

    public int getLimit() { return limit; }

    public boolean isUseStock() {
        return useStock;
    }

    public int getStock() {
        return stock;
    }
}
