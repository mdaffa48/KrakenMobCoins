package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.manager.AccountManager;
import org.bukkit.entity.Player;

public class MobCoinsAPI {
    
    private final MobCoins plugin;
    private final AccountManager accountManager;
    public MobCoinsAPI(final MobCoins plugin){
        this.plugin = plugin;
        this.accountManager = plugin.getAccountManager();
    }

    public double getPlayerCoins(Player player){
        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());
        if(playerCoins != null){
            return playerCoins.getMoney();
        }
        return 0.0;
    }

    public void addPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() + amount);
        }
    }

    public void removePlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() - amount);
        }
    }

    public void setPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(amount);
        }
    }

}
