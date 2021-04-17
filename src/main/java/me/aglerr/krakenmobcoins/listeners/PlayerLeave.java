package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    private final MobCoins plugin;
    public PlayerLeave(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerCoins coins = plugin.getAccountManager().getPlayerData(player.getUniqueId().toString());
        if(coins != null){
            plugin.getAccountManager().savePlayerData(coins);
        }
    }

}
