package me.aglerr.krakenmobcoins.coinmob;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class CoinMob {

    private final String type;
    private final String coinAmount;
    private final double chance;

    public CoinMob(final String type, final String coinAmount, final double chance){
        this.type = type;
        this.coinAmount = coinAmount;
        this.chance = chance;
    }

    public String getType() {
        return type;
    }

    public String getCoinAmount(){
        return coinAmount;
    }

    public double getChance() {
        return chance;
    }

    public boolean willDropCoins(){
        return ThreadLocalRandom.current().nextDouble(101) <= getChance();
    }

    public double getAmountToDrop(){
        if(getCoinAmount().contains("-")){

            String[] split = getCoinAmount().split("-");
            double minimumCoin = Double.parseDouble(split[0]);
            double maximumCoin = Double.parseDouble(split[1]);

            return ThreadLocalRandom.current().nextDouble(maximumCoin - minimumCoin) + minimumCoin;

        } else {
            return Double.parseDouble(getCoinAmount());
        }

    }

    public double getAmountToDrop(FileConfiguration config, Player player){

        double amount;
        if(getCoinAmount().contains("-")){

            String[] split = getCoinAmount().split("-");
            double minimumCoin = Double.parseDouble(split[0]);
            double maximumCoin = Double.parseDouble(split[1]);

            if(config.getBoolean("options.lootingSystem")){
                ItemStack hand = player.getItemInHand();
                if(hand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)){
                    int level = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                    int finalLevel = level * 10;

                    minimumCoin = (minimumCoin) + ((minimumCoin * finalLevel) / 100);
                    maximumCoin = (maximumCoin) + ((maximumCoin * finalLevel) / 100);

                }

            }

            return ThreadLocalRandom.current().nextDouble(maximumCoin - minimumCoin) + minimumCoin;

        } else {

            amount = Double.parseDouble(getCoinAmount());
            if(config.getBoolean("options.lootingSystem")){
                ItemStack hand = player.getItemInHand();
                if(hand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)){
                    int level = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                    int finalLevel = level * 10;

                    amount = (amount) + ((Double.parseDouble(getCoinAmount()) * finalLevel) / 100);
                }
            }

            return amount;
        }

    }

}
