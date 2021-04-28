package me.aglerr.krakenmobcoins.coinmob;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CoinMobManager {

    private final List<CoinMob> coinMobList = new ArrayList<>();

    private final MobCoins plugin;
    public CoinMobManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public List<CoinMob> getCoinMobList() { return coinMobList; }

    public CoinMob getCoinMob(String type){
        return getCoinMobList().stream().filter(coinMob -> coinMob.getType().equalsIgnoreCase(type))
                .findFirst().orElse(null);
    }
    public void clearCoinMob(){
        coinMobList.clear();
    }

    public void loadCoinMob(){
        FileConfiguration mobs = plugin.getMobsManager().getConfiguration();
        Utils utils = plugin.getUtils();

        if(!coinMobList.isEmpty()) coinMobList.clear();

        int totalMobs = 0;
        for(String key : mobs.getConfigurationSection("entities").getKeys(false)){

            totalMobs++;
            coinMobList.add(new CoinMob(key, mobs.getString("entities." + key + ".amount"), mobs.getDouble("entities." + key + ".chance")));

        }

        utils.sendConsoleMessage("Successfully loaded " + totalMobs + " mobs, enjoy!");

    }

}
