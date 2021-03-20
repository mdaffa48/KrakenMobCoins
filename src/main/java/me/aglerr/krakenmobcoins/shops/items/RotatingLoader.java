package me.aglerr.krakenmobcoins.shops.items;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RotatingLoader {

    private List<RotatingItems> rotatingItemsList = new ArrayList<>();

    public void load(){
        FileConfiguration config = MobCoins.getInstance().getConfig();
        for(String key : config.getConfigurationSection("rotatingShop.items").getKeys(false)){

            String material = config.getString("rotatingShop.items." + key + ".material");
            String name = config.getString("rotatingShop.items." + key + ".name");
            int slot = config.getInt("rotatingShop.items." + key + ".slot");
            List<Integer> slots = config.getIntegerList("rotatingShop.items." + key + ".slots");
            List<String> lore = config.getStringList("rotatingShop.items." + key + ".lore");
            boolean glow = config.getBoolean("rotatingShop.items." + key + ".glow");


            rotatingItemsList.add(new RotatingItems(material, name, slot, slots, lore, glow));
        }
    }

    public List<RotatingItems> getRotatingItemsList() { return rotatingItemsList; }

}
