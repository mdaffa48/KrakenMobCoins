package me.aglerr.krakenmobcoins.manager;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class RotatingShopTimeManager {

    private long normalTime;
    private long specialTime;

    private final MobCoins plugin;
    public RotatingShopTimeManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public long getNormalTime(){
        return normalTime;
    }

    public long getSpecialTime(){
        return specialTime;
    }

    public void setNormalTime(long value){
        normalTime = value;
    }

    public void setSpecialTime(long value){
        specialTime = value;
    }

    public void saveTimeToConfig(){
        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        if(plugin.getConfig().getBoolean("rotatingShop.enabled")){
            config.set("normalTimeReset", normalTime);
            config.set("specialTimeReset", specialTime);
        }

        plugin.getTempDataManager().saveData();

    }

    public void loadTimeFromConfig(){
        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        if(plugin.getConfig().getBoolean("rotatingShop.enabled")){
            if(config.contains("normalTimeReset") && config.contains("specialTimeReset")){
                System.out.println("[KrakenMobCoins] Loading saved normal time and special time reset.");

                setNormalTime(config.getLong("normalTimeReset"));
                setSpecialTime(config.getLong("specialTimeReset"));

                config.set("normalTimeReset", null);
                config.set("specialTimeReset", null);

            } else {

                System.out.println("[KrakenMobCoins] Adding default value to the normal time and special time.");
                setNormalTime(System.currentTimeMillis() + (plugin.getConfig().getInt("rotatingShop.normalTimeReset") * 3600 * 1000));
                setSpecialTime(System.currentTimeMillis() + (plugin.getConfig().getInt("rotatingShop.specialTimeReset") * 3600 * 1000));

            }

            plugin.getTempDataManager().saveData();

        }
    }

    public void startCounting(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if(getNormalTime() < System.currentTimeMillis()){
                setNormalTime(System.currentTimeMillis() + (plugin.getConfig().getInt("rotatingShop.normalTimeReset") * 3600 * 1000));
                plugin.getUtils().refreshNormalItems();
                for(String message : plugin.getConfig().getStringList("rotatingShop.messages.normalRefresh")){
                    Bukkit.broadcastMessage(plugin.getUtils().color(message));
                }
            }

            if(getSpecialTime() < System.currentTimeMillis()){
                setSpecialTime(System.currentTimeMillis() + (plugin.getConfig().getInt("rotatingShop.specialTimeReset") * 3600 * 1000));
                plugin.getUtils().refreshSpecialItems();
                for(String message : plugin.getConfig().getStringList("rotatingShop.messages.specialRefresh")){
                    Bukkit.broadcastMessage(plugin.getUtils().color(message));
                }
            }

        }, 0L, 20L);

    }

}
