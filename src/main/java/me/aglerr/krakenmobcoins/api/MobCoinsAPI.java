package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import org.bukkit.entity.Player;

public class MobCoinsAPI {

    public static double getPlayerCoins(Player player){
        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            return playerCoins.getMoney();
        }
        return 0.0;
    }

    public static void addPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() + amount);
        }
    }

    public static void removePlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(playerCoins.getMoney() - amount);
        }
    }

    public static void setPlayerCoins(Player player, int amount){
        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        if(playerCoins != null){
            playerCoins.setMoney(amount);
        }
    }

}
