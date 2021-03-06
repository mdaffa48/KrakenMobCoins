package me.aglerr.krakenmobcoins.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.coinmob.CoinMob;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MythicMobDeathPhysical implements Listener {

    private final MobCoins plugin;
    public MythicMobDeathPhysical(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeathPhysical(MythicMobDeathEvent event){
        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();
        if(!config.getBoolean("options.physicalMobCoin.enabled")) return;

        if(!config.getBoolean("options.physicalMobCoin.ignoreDeathCause")){
            if(event.getEntity().getLastDamageCause() == null) return;
            if(!plugin.getDamageCauses().contains(event.getEntity().getLastDamageCause().getCause())) return;
        }

        Entity entity = event.getEntity();

        List<String> worlds = config.getStringList("disabledWorlds");
        if(worlds.contains(entity.getWorld().getName())) return;

        String type = event.getMobType().getInternalName();
        CoinMobManager manager = plugin.getCoinMobManager();

        CoinMob coinMob = manager.getCoinMob(type);
        if(coinMob == null) return;
        if(!coinMob.willDropCoins()) return;

        double amount = coinMob.getAmountToDrop();

        if(!(event.getKiller() instanceof Player)){

            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amount));

        } else {

            Player player = (Player) event.getKiller();
            int multiplier = utils.getBooster(player);
            double multiplierAmount = amount * multiplier / 100;
            double amountAfter = amount + multiplierAmount;

            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amountAfter));
        }

    }

}
