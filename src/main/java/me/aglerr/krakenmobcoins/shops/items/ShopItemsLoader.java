package me.aglerr.krakenmobcoins.shops.items;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ShopItemsLoader {

    private List<ShopItems> shopItemsList = new ArrayList<>();

    public void load(){
        FileConfiguration shop = MobCoins.getInstance().getShopManager().getConfiguration();
        for(String key : shop.getConfigurationSection("shops").getKeys(false)){

            String material = shop.getString("shops." + key + ".material");
            int amount = shop.getInt("shops." + key + ".amount");
            String name = shop.getString("shops." + key + ".name");
            boolean glowing = shop.getBoolean("shops." + key + ".glowing");
            List<String> lore = shop.getStringList("shops." + key + ".lore");
            List<String> commands = shop.getStringList("shops." + key + ".commands");
            double price = shop.getDouble("shops." + key + ".price");
            boolean special = shop.getBoolean("shops." + key + ".special");
            int limit = shop.getInt("shops." + key + ".limit");
            boolean useStock = shop.getBoolean("shops." + key + ".useStock");
            int stock = shop.getInt("shops." + key + ".stock");

            shopItemsList.add(new ShopItems(key, material, amount, name, glowing, lore, commands, price, special, limit, useStock, stock));

        }
    }

    public List<ShopItems> getShopItemsList() { return shopItemsList; }

}
