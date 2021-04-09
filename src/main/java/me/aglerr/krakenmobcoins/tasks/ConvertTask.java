package me.aglerr.krakenmobcoins.tasks;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ConvertTask extends BukkitRunnable {

    @Override
    public void run(){
        Utils utils = MobCoins.getInstance().getUtils();
        File file = new File(Bukkit.getPluginManager().getPlugin("SuperMobCoins").getDataFolder(), "profiles.yml");
        if(file.exists()){
            FileConfiguration profile = YamlConfiguration.loadConfiguration(file);
            for(String uuid : profile.getConfigurationSection("Profile").getKeys(false)){
                String amount = String.valueOf(profile.getInt("Profile." + uuid + ".mobcoins"));

                MobCoins.getInstance().getDatabase().createAccount(uuid, Double.parseDouble(amount));
                System.out.println("[KrakenMobCoins] Converting " + uuid + " data.");

            }

            utils.sendConsoleMessage("Successfully converting all data!");
            utils.sendConsoleMessage("Please remove SuperMobCoins and restart the server to prevent any bugs!");
            utils.sendConsoleMessage("Thanks, and enjoy!");

        }

        cancel();
    }

}
