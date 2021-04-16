package me.aglerr.krakenmobcoins.manager;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.api.MobCoinsExpansion;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class DependencyManager {

    private boolean superMobCoins = false;
    private boolean mythicMobs = false;
    private boolean wildStacker = false;

    private final MobCoins plugin;
    public DependencyManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void setupDependency(){
        PluginManager pm = Bukkit.getPluginManager();
        Utils utils = plugin.getUtils();

        int totalHooks = 0;
        if(pm.getPlugin("PlaceholderAPI") != null){
            utils.sendConsoleMessage("PlaceholderAPI found, enabling hooks!");
            new MobCoinsExpansion().register();
            totalHooks++;
        }

        if(pm.getPlugin("SuperMobCoins") != null){
            utils.sendConsoleMessage("SuperMobCoins found, enabling hooks!");
            setSuperMobCoins(true);
            totalHooks++;
        }

        if(pm.getPlugin("MythicMobs") != null){
            utils.sendConsoleMessage("MythicMobs found, enabling hooks!");
            setMythicMobs(true);
            totalHooks++;
        }

        if(pm.getPlugin("WildStacker") != null){
            utils.sendConsoleMessage("WildStacker found, enabling hooks!");
            setWildStacker(true);
            totalHooks++;
        }

        utils.sendConsoleMessage("Successfully hooked " + totalHooks + " plugins, enjoy!");
    }

    public boolean isSuperMobCoins() {
        return superMobCoins;
    }

    public void setSuperMobCoins(boolean superMobCoins) {
        this.superMobCoins = superMobCoins;
    }

    public boolean isMythicMobs() {
        return mythicMobs;
    }

    public void setMythicMobs(boolean mythicMobs) {
        this.mythicMobs = mythicMobs;
    }

    public boolean isWildStacker() {
        return wildStacker;
    }

    public void setWildStacker(boolean wildStacker) {
        this.wildStacker = wildStacker;
    }
}
