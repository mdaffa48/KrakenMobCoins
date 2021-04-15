package me.aglerr.krakenmobcoins.listeners;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CreatureSpawn implements Listener {

    private final MobCoins plugin;
    public CreatureSpawn(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("options.disableMobCoinsFromSpawner")){
            if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER){
                plugin.getMobSpawner().add(event.getEntity());
            }
        }

    }

}
