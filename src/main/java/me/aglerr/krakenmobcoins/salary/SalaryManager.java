package me.aglerr.krakenmobcoins.salary;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SalaryManager {

    private final Map<UUID, Double> playerSalary = new HashMap<>();

    private final MobCoins plugin;
    public SalaryManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public boolean isPlayerExist(UUID uuid){
        return playerSalary.containsKey(uuid);
    }

    public double getPlayerSalary(UUID uuid){
        return playerSalary.get(uuid);
    }

    public void setPlayerSalary(UUID uuid, double amount){
        playerSalary.put(uuid, amount);
    }

    public void removePlayer(UUID uuid){
        playerSalary.remove(uuid);
    }

    public void beginSalaryTask(){
        FileConfiguration config = plugin.getConfig();
        int delay = config.getInt("options.salaryMode.announceEvery");

        Utils utils = plugin.getUtils();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if(config.getBoolean("options.salaryMode.enabled")){
                for(UUID uuid : playerSalary.keySet()){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player != null){
                        PlayerCoins playerCoins = plugin.getPlayerCoins(uuid.toString());
                        double amount = getPlayerSalary(uuid);
                        for(String message : ConfigMessagesList.SALARY.toStringList()){
                            player.sendMessage(utils.color(message.replace("%coins%", utils.getDFormat().format(amount))));
                        }

                        utils.sendSound(player);
                        utils.sendTitle(player, amount);
                        utils.sendActionBar(player, amount);

                        if(config.getBoolean("options.salaryMode.receiveAfterMessage")){
                            playerCoins.setMoney(playerCoins.getMoney() + amount);
                        }

                        removePlayer(uuid);

                    }
                }
            }
        }, 0L, 20 * delay);
    }

}
