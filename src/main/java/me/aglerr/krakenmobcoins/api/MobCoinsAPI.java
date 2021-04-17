package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.manager.AccountManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MobCoinsAPI {
    
    private final MobCoins plugin;
    private final AccountManager accountManager;
    public MobCoinsAPI(final MobCoins plugin){
        this.plugin = plugin;
        this.accountManager = plugin.getAccountManager();
    }

    @Nullable
    public PlayerCoins getPlayerData(Player player){
        return accountManager.getPlayerData(player.getUniqueId().toString());
    }

}
