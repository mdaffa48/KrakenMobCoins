package me.aglerr.krakenmobcoins.configs;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private FileConfiguration limitConfig;
    private FileConfiguration mobsConfig;
    private FileConfiguration shopConfig;
    private FileConfiguration tempDataConfig;

    private File limitFile;
    private File mobsFile;
    private File shopFile;
    private File tempDataFile;

    public void setup(ConfigType type){
        // Initializing all config and data.
    }

    public FileConfiguration getConfig(ConfigType type){
        switch(type){
            case LIMIT:
                return limitConfig;
            case MOBS:
                return mobsConfig;
            case SHOP:
                return shopConfig;
            case TEMP_DATA:
                return tempDataConfig;
        }
        return null;
    }

    public void saveConfig(ConfigType type){
        switch(type){
            case LIMIT:
                save(limitFile, limitConfig);
                break;
            case MOBS:
                save(mobsFile, mobsConfig);
                break;
            case SHOP:
                save(shopFile, shopConfig);
                break;
            case TEMP_DATA:
                save(tempDataFile, tempDataConfig);
                break;
        }
    }

    public void reloadConfig(ConfigType type){
        // Reloading all config.
    }

    private void save(File file, FileConfiguration config){
        try{
            config.save(file);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private enum ConfigType{
        LIMIT,
        MOBS,
        SHOP,
        TEMP_DATA
    }

}
