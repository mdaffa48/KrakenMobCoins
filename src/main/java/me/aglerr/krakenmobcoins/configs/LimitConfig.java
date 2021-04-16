package me.aglerr.krakenmobcoins.configs;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class LimitConfig {

    public FileConfiguration data;
    public File cfg;

    public void setup() {
        Utils utils = MobCoins.getInstance().getUtils();
        if(!MobCoins.getInstance().getDataFolder().exists()) {
            MobCoins.getInstance().getDataFolder().mkdir();
        }

        cfg = new File(MobCoins.getInstance().getDataFolder(), "purchase_data.yml");

        if(!cfg.exists()) {
            MobCoins.getInstance().saveResource("purchase_data.yml", false);
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

}
