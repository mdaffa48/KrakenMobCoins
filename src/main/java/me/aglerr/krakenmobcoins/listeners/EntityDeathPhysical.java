package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.coinmob.CoinMob;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDeathPhysical implements Listener {

    private final MobCoins plugin;
    public EntityDeathPhysical(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        FileConfiguration config = plugin.getConfig();
        if(!config.getBoolean("options.physicalMobCoin.enabled")) return;

        if(!config.getBoolean("options.physicalMobCoin.ignoreDeathCause")){
            if(event.getEntity().getLastDamageCause() == null) return;
            if(!plugin.getDamageCauses().contains(event.getEntity().getLastDamageCause().getCause())) return;
        }

        Utils utils = plugin.getUtils();
        LivingEntity entity = event.getEntity();

        List<String> worlds = config.getStringList("disabledWorlds");
        if(worlds.contains(entity.getWorld().getName())) return;

        if (config.getBoolean("options.disableMobCoinsFromSpawner")) {
            if (plugin.getMobSpawner().contains(entity.getUniqueId())) {
                plugin.getMobSpawner().remove(entity.getUniqueId());
                return;
            }
        }

        String type = entity.getType().toString();
        CoinMobManager manager = plugin.getCoinMobManager();

        CoinMob coinMob = manager.getCoinMob(type);
        if(coinMob == null) return;
        if(!coinMob.willDropCoins()) return;

        double amount = coinMob.getAmountToDrop();

        if(entity.getKiller() == null){
            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amount));

        } else {

            Player player = entity.getKiller();
            int multiplier = utils.getBooster(player);
            double multiplierAmount = amount * multiplier / 100;
            double amountAfter = amount + multiplierAmount;

            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amountAfter));
        }

    }

}
