package me.aglerr.krakenmobcoins.listeners;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.coinmob.CoinMob;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.api.events.MobCoinsReceiveEvent;
import me.aglerr.krakenmobcoins.manager.DependencyManager;
import me.aglerr.krakenmobcoins.manager.SalaryManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class EntityDeath implements Listener {

    private final MobCoins plugin;
    public EntityDeath(final MobCoins plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();

        if(config.getBoolean("options.physicalMobCoin.enabled")) return;
        if(event.getEntity().getKiller() == null) return;

        Player player = event.getEntity().getKiller();
        PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId().toString());
        if(playerCoins == null) return;

        List<String> worlds = config.getStringList("disabledWorlds");
        if(worlds.contains(player.getWorld().getName())) return;

        LivingEntity entity = event.getEntity();
        if(config.getBoolean("options.disableMobCoinsFromSpawner")){
            if(plugin.getMobSpawner().contains(entity.getUniqueId())){
                plugin.getMobSpawner().remove(entity.getUniqueId());
                return;
            }
        }

        String type = entity.getType().toString();
        CoinMobManager manager = plugin.getCoinMobManager();

        CoinMob coinMob = manager.getCoinMob(type);
        if(coinMob == null) return;
        if(!coinMob.willDropCoins()) return;

        double amount = coinMob.getAmountToDrop(plugin.getConfig(), player);

        int multiplier = utils.getBooster(player);
        double multiplierAmount = amount * multiplier / 100;
        double amountAfter = amount + multiplierAmount;

        MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(player, amount, amountAfter, multiplierAmount, entity, multiplier);
        Bukkit.getPluginManager().callEvent(mobCoinsReceiveEvent);
        if(mobCoinsReceiveEvent.isCancelled()) return;

        if(config.getBoolean("options.wildStackerSupport")){
            final DependencyManager dependencyManager = plugin.getDependencyManager();
            if(dependencyManager.isWildStacker()) {
                mobCoinsReceiveEvent.setAmountAfterMultiplier(mobCoinsReceiveEvent.getAmountAfterMultiplier() * WildStackerAPI.getEntityAmount(entity));
            }
        }

        if(config.getBoolean("options.salaryMode.enabled")) {
            if (!config.getBoolean("options.salaryMode.receiveAfterMessage")) {
                playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
            }
        } else {
            playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
        }

        if(!plugin.getNotificationManager().isPlayerExist(player.getUniqueId())) {
            if (config.getBoolean("options.salaryMode.enabled")) {

                SalaryManager salaryManager = plugin.getSalaryManager();
                if(salaryManager.isPlayerExist(player.getUniqueId())){
                    double current = salaryManager.getPlayerSalary(player.getUniqueId());
                    double currentFinal = current + mobCoinsReceiveEvent.getAmountAfterMultiplier();
                    salaryManager.setPlayerSalary(player.getUniqueId(), currentFinal);
                } else {
                    salaryManager.setPlayerSalary(player.getUniqueId(), mobCoinsReceiveEvent.getAmountAfterMultiplier());
                }

            } else {

                utils.sendSound(player);
                utils.sendMessage(player, mobCoinsReceiveEvent.getAmountAfterMultiplier());
                utils.sendTitle(player, mobCoinsReceiveEvent.getAmountAfterMultiplier());
                utils.sendActionBar(player, mobCoinsReceiveEvent.getAmountAfterMultiplier());

            }
        }

    }

}
