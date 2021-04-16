package me.aglerr.krakenmobcoins.configs;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MobsConfig {

    public FileConfiguration data;
    public File cfg;
    
    private final MobCoins plugin;
    public MobsConfig(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void setup() {
        Utils utils = plugin.getUtils();
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        cfg = new File(plugin.getDataFolder(), "mobs.yml");

        if(!cfg.exists()) {
            plugin.saveResource("mobs.yml", false);
            utils.sendConsoleMessage("mobs.yml not found, creating mobs.yml...");
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
