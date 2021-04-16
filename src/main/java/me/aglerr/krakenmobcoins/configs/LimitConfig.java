package me.aglerr.krakenmobcoins.configs;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LimitConfig {

    public FileConfiguration data;
    public File cfg;

    private final MobCoins plugin;
    public LimitConfig(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void setup() {
        Utils utils = plugin.getUtils();
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        cfg = new File(plugin.getDataFolder(), "purchase_data.yml");

        if(!cfg.exists()) {
            plugin.saveResource("purchase_data.yml", false);
            utils.sendConsoleMessage("purchase_data.yml not found, creating purchase_data.yml...");
        }

        data = YamlConfiguration.loadConfiguration(cfg);

    }

    public FileConfiguration getConfiguration() {
        return data;
    }

    public void saveData() {
        try {
            data.save(cfg);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(cfg);
    }

    public int getPlayerLimit(Player player, String key){
        return data.getInt("items." + player.getUniqueId().toString() + "." + key);
    }

    public void setPlayerLimit(Player player, String key, int amount){
        data.set("items." + player.getUniqueId().toString() + "." + key, amount);
        this.saveData();
    }

    public void clearPlayerLimit(){
        data.set("items", new ArrayList<>());
        this.saveData();
    }

}
