package me.aglerr.krakenmobcoins.configs;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class TempDataConfig {

    public FileConfiguration data;
    public File cfg;

    private final MobCoins plugin;
    public TempDataConfig(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void setup() {
        Utils utils = plugin.getUtils();
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        cfg = new File(plugin.getDataFolder(), "temp_data.yml");

        if(!cfg.exists()) {
            plugin.saveResource("temp_data.yml", false);
            utils.sendConsoleMessage("temp_data.yml not found, creating temp_data.yml...");
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

}
