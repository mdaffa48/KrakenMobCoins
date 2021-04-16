package me.aglerr.krakenmobcoins.shops.category.mainmenu;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.shops.category.mainmenu.MainMenuItems;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainMenuLoader {

    private List<MainMenuItems> mainMenuItemsList = new ArrayList<>();

    private final MobCoins plugin;
    public MainMenuLoader(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void load(){
        FileConfiguration config = plugin.getConfig();
        for(String key : config.getConfigurationSection("normalShop.items").getKeys(false)){

            String type = config.getString("normalShop.items." + key + ".type");
            String material = config.getString("normalShop.items." + key + ".material");
            String name = config.getString("normalShop.items." + key + ".name");
            int slot = config.getInt("normalShop.items." + key + ".slot");
            List<Integer> slots = config.getIntegerList("normalShop.items." + key + ".slots");
            List<String> lore = config.getStringList("normalShop.items." + key + ".lore");
            boolean glow = config.getBoolean("normalShop.items." + key + ".glow");
            String category = config.getString("normalShop.items." + key + ".category");

            mainMenuItemsList.add(new MainMenuItems(type, material, name, slot, slots, lore, glow, category));

        }
    }

    public List<MainMenuItems> getMainMenuItemsList() { return mainMenuItemsList; }

}
