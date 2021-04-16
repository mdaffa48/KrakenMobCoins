package me.aglerr.krakenmobcoins.manager;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ToggleNotificationManager {

    private final Set<UUID> toggledList = new HashSet<>();

    private final MobCoins plugin;
    public ToggleNotificationManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public boolean isPlayerExist(UUID uuid){
        return toggledList.contains(uuid);
    }

    public void blockNotification(UUID uuid){
        toggledList.add(uuid);
    }

    public void unBlockNotification(UUID uuid){
        toggledList.remove(uuid);
    }

    public void saveToggledListToConfig(){
        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        List<String> list = new ArrayList<>();

        if(!toggledList.isEmpty()){
            for(UUID uuid : toggledList){
                list.add(uuid.toString());
            }
        }

        config.set("data", list);
        plugin.getTempDataManager().saveData();


    }

    public void loadToggledListFromConfig(){
        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        List<String> list = new ArrayList<>(config.getStringList("data"));

        for(String uuid : list){
            blockNotification(UUID.fromString(uuid));
        }

        config.set("data", new ArrayList<>());
        plugin.getTempDataManager().saveData();

    }

}
