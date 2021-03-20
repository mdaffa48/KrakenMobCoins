package me.aglerr.krakenmobcoins.listeners;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.api.MobCoinsAPI;
import me.aglerr.krakenmobcoins.api.events.MobCoinsReceiveEvent;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDeath implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        FileConfiguration config = MobCoins.getInstance().getConfig();
        if(config.getBoolean("options.physicalMobCoin.enabled")) return;

        if (!(event.getEntity().getKiller() instanceof Player)) return;
        Utils utils = MobCoins.getInstance().getUtils();

        Player player = event.getEntity().getKiller();

        List<String> worlds = config.getStringList("disabledWorlds");
        if (worlds.contains(player.getWorld().getName())) return;

        LivingEntity entity = event.getEntity();
        if(config.getBoolean("options.disableMobCoinsFromSpawner")){
            if(MobCoins.getInstance().getMobSpawner().contains(entity)){
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
                double minimumRange;
                double maximumRange;
                if (config.getBoolean("options.lootingSystem")) {
                    ItemStack hand = player.getItemInHand();
                    if (hand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                        int level = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                        int finalLevel = level * 10;

                        double minimumLooting = (Double.parseDouble(split[0]) * finalLevel) / 100;
                        double maximumLooting = (Double.parseDouble(split[1]) * finalLevel) / 100;

                        minimumRange = Double.parseDouble(split[0]) + minimumLooting;
                        maximumRange = Double.parseDouble(split[1]) + maximumLooting;

                    } else {

                        minimumRange = Double.parseDouble(split[0]);
                        maximumRange = Double.parseDouble(split[1]);

                    }
                } else {

                    minimumRange = Double.parseDouble(split[0]);
                    maximumRange = Double.parseDouble(split[1]);

                }

                amount = this.getRandomNumber(minimumRange, maximumRange);

            } else {

                double finalAmount = Double.parseDouble(checkAmount);
                if (config.getBoolean("options.lootingSystem")) {
                    ItemStack hand = player.getItemInHand();
                    if (hand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                        int level = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                        int finalLevel = level * 10;

                        finalAmount = (Double.parseDouble(checkAmount) * finalLevel) / 100;
                    }

                }

                amount = finalAmount;

            }

            int random = ThreadLocalRandom.current().nextInt(101);
            if (random <= chance) {

                int multiplier = utils.getBooster(player);
                double multiplierAmount = amount * multiplier / 100;
                double amountAfter = amount + multiplierAmount;

                MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(player, amount, amountAfter, multiplierAmount, entity, multiplier);
                Bukkit.getPluginManager().callEvent(mobCoinsReceiveEvent);
                if (mobCoinsReceiveEvent.isCancelled()) return;

                PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
                if (playerCoins != null) {
                    if (config.getBoolean("options.salaryMode.enabled")) {
                        if (!config.getBoolean("options.salaryMode.receiveAfterMessage")) {
                            playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
                        }
                    } else {
                        playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
                    }

                    if (!MobCoins.getInstance().getToggled().contains(player.getUniqueId().toString())) {
                        if (config.getBoolean("options.salaryMode.enabled")) {
                            if (MobCoins.getInstance().getSalary().containsKey(player.getUniqueId())) {
                                double current = MobCoins.getInstance().getSalary().get(player.getUniqueId());
                                double currentFinal = current + mobCoinsReceiveEvent.getAmountAfterMultiplier();
                                MobCoins.getInstance().getSalary().put(player.getUniqueId(), currentFinal);
                            } else {

                                MobCoins.getInstance().getSalary().put(player.getUniqueId(), mobCoinsReceiveEvent.getAmountAfterMultiplier());

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
        }

    }


    private double getRandomNumber(double min, double max){
        return ThreadLocalRandom.current().nextDouble(max - min) + min;
    }

}
