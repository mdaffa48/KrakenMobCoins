package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoin implements Listener {

    private final MobCoins plugin;
    public PlayerJoin(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        Utils utils = plugin.getUtils();

        FileConfiguration config = plugin.getConfig();
        double starting = config.getDouble("options.startingBalance");

        if(plugin.getPlayerCoins(uuid) == null){
            plugin.getDatabase().createAccount(uuid, starting);
            utils.sendConsoleMessage("Successfully creating a new account for " + player.getName());
            utils.sendConsoleMessage("UUID: " + uuid);
        }

    }

}
