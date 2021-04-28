package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.manager.SalaryManager;
import me.aglerr.krakenmobcoins.manager.ToggleNotificationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MobCoinsAPI {
    
    private final MobCoins plugin;
    public MobCoinsAPI(final MobCoins plugin){
        this.plugin = plugin;
    }

    @Nullable
    public PlayerCoins getPlayerData(Player player){
        return plugin.getAccountManager().getPlayerData(player.getUniqueId().toString());
    }

    public ToggleNotificationManager getNotificationManager() { return plugin.getNotificationManager(); }

    public SalaryManager getSalaryManager() { return plugin.getSalaryManager(); }

}
