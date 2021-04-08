package me.aglerr.krakenmobcoins.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import me.aglerr.krakenmobcoins.MobCoins;
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

    @EventHandler
    public void onDeathPhysical(MythicMobDeathEvent event){
        FileConfiguration config = MobCoins.getInstance().getConfig();
        if(!config.getBoolean("options.physicalMobCoin.enabled")) return;

        if(!config.getBoolean("options.physicalMobCoin.ignoreDeathCause")){
            EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
            if(cause == null) return;
            if(!MobCoins.getInstance().getDamageCauses().contains(cause)) return;
        }

        Utils utils = MobCoins.getInstance().getUtils();
        Entity entity = event.getEntity();

        List<String> worlds = config.getStringList("disabledWorlds");
        if(worlds.contains(entity.getWorld().getName())) return;

        String type = event.getMobType().getInternalName();
        if(MobCoins.getInstance().getChance().containsKey(type) && MobCoins.getInstance().getDropAmount().containsKey(type)){

            int chance = MobCoins.getInstance().getChance().get(type);
            String checkAmount = MobCoins.getInstance().getDropAmount().get(type);
            double amount;

            if (checkAmount.contains("-")) {

                String[] split = checkAmount.split("-");
                double minimumRange = Double.parseDouble(split[0]);
                double maximumRange = Double.parseDouble(split[1]);

                amount = this.getRandomNumber(minimumRange, maximumRange);

            } else {

                double finalAmount = Double.parseDouble(checkAmount);
                amount = finalAmount;

            }

            int random = ThreadLocalRandom.current().nextInt(101);
            if(random <= chance){
                if(event.getKiller() instanceof Player){
                    Player player = (Player) event.getKiller();

                    int multiplier = utils.getBooster(player);
                    double multiplierAmount = amount * multiplier / 100;
                    double amountAfter = amount + multiplierAmount;

                    entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amountAfter));
                } else {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amount));
                }
            }

        }

    }

    private double getRandomNumber(double min, double max){
        return ThreadLocalRandom.current().nextDouble(max - min) + min;
    }

}
