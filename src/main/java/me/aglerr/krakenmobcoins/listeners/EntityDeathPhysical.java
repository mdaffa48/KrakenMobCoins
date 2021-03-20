package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDeathPhysical implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        FileConfiguration config = MobCoins.getInstance().getConfig();
        if(!config.getBoolean("options.physicalMobCoin.enabled")) return;

        if (!MobCoins.getInstance().getDamageCauses().contains(event.getEntity().getLastDamageCause().getCause())) return;
        Utils utils = MobCoins.getInstance().getUtils();

        LivingEntity entity = event.getEntity();

        List<String> worlds = config.getStringList("disabledWorlds");
        if (worlds.contains(entity.getWorld().getName())) return;

        if (config.getBoolean("options.disableMobCoinsFromSpawner")) {
            if (MobCoins.getInstance().getMobSpawner().contains(entity)) {
                MobCoins.getInstance().getMobSpawner().remove(entity);
                return;
            }
        }

        String type = entity.getType().toString();
        if (MobCoins.getInstance().getChance().containsKey(type) && MobCoins.getInstance().getDropAmount().containsKey(type)) {

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
                entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amount));
            }

        }

    }

    private double getRandomNumber(double min, double max){
        return ThreadLocalRandom.current().nextDouble(max - min) + min;
    }

}
