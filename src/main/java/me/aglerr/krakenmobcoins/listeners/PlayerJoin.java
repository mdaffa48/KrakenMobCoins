package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.CoinsData;
import me.aglerr.krakenmobcoins.utils.Utils;
import me.swanis.mobcoins.MobCoinsAPI;
import me.swanis.mobcoins.profile.Profile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        Utils utils = MobCoins.getInstance().getUtils();

        if(MobCoins.getInstance().getPlayerCoins(uuid) == null){
            try{
                CoinsData.createAccount(uuid);
                utils.sendConsoleMessage("Successfully creating a new account for " + player.getName());
                utils.sendConsoleMessage("UUID: " + uuid);
            } catch(SQLException exception){
                System.out.println("[KrakenMobCoins] Error trying to create a new account!");
                exception.printStackTrace();
            }
        }

    }

}
