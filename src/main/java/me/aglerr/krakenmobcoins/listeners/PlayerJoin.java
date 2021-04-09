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

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        Utils utils = MobCoins.getInstance().getUtils();

        FileConfiguration config = MobCoins.getInstance().getConfig();
        double starting = config.getDouble("options.startingBalance");

        if(MobCoins.getInstance().getPlayerCoins(uuid) == null){
            MobCoins.getInstance().getDatabase().createAccount(uuid, starting);
            utils.sendConsoleMessage("Successfully creating a new account for " + player.getName());
            utils.sendConsoleMessage("UUID: " + uuid);
        }

    }

}
