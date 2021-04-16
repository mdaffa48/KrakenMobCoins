package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import org.bukkit.entity.Player;

public class MobCoinsAPI {
    
    private final MobCoins plugin;
    public MobCoinsAPI(final MobCoins plugin){
        this.plugin = plugin;
    }

    public double getPlayerCoins(Player player){
        PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            return playerCoins.getMoney();
        }
        return 0.0;
    }

    public void addPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() + amount);
        }
    }

    public void removePlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() - amount);
        }
    }

    public void setPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(amount);
        }
    }

}
