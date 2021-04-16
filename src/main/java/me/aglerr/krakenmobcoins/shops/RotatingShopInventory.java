package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.manager.ItemStockManager;
import me.aglerr.krakenmobcoins.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.configs.LimitConfig;
import me.aglerr.krakenmobcoins.shops.items.RotatingItems;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RotatingShopInventory extends FastInv {

    public RotatingShopInventory(int size, String title, Player player, MobCoins plugin) {
        super(size, title);

        final ItemStockManager stockManager = plugin.getItemStockManager();

        FileConfiguration config = plugin.getConfig();
        LimitConfig limitConfig = plugin.getLimitManager();
        Utils utils = plugin.getUtils();

        PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());

        List<Integer> normalSlots = config.getIntegerList("rotatingShop.normalItemSlots");

        int normalCounter = 0;
        for(ShopItems normal : plugin.getNormalItems()){

            List<String> lore = new ArrayList<>();
            if(normal.isUseStock()){
                int finalStock;
                if(stockManager.isItemExist(normal.getConfigKey())){
                    finalStock = stockManager.getItemStock(normal.getConfigKey());
                } else {
                    finalStock = normal.getStock();
                    stockManager.setStock(normal.getConfigKey(), normal.getStock());
                }

                String placeholder;
                if(finalStock <= 0){
                    placeholder = config.getString("placeholders.outOfStock");
                } else {
                    placeholder = String.valueOf(finalStock);
                }
                for(String line : normal.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                            .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, normal.getConfigKey())))
                            .replace("%stock%", placeholder)
                            .replace("%price%", String.valueOf(normal.getPrice())));
                }
            } else {
                for(String line : normal.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                            .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, normal.getConfigKey())))
                            .replace("%price%", String.valueOf(normal.getPrice())));
                }
            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.getMaterial()).get().parseItem())
                    .name(normal.getName())
                    .amount(normal.getAmount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if(normal.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(normalSlots.get(normalCounter), stack, event -> plugin.getShopUtils().buyHandler(normal, player, stack));
            normalCounter++;

            if(normalCounter == normalSlots.size()) break;
        }

        int specialCounter = 0;
        for(ShopItems special : plugin.getSpecialItems()){
            List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

            List<String> lore = new ArrayList<>();
            if(special.isUseStock()){
                int finalStock;
                if(stockManager.isItemExist(special.getConfigKey())){
                    finalStock = stockManager.getItemStock(special.getConfigKey());
                } else {
                    finalStock = special.getStock();
                    stockManager.setStock(special.getConfigKey(), special.getStock());
                }

                String placeholder;
                if(finalStock <= 0){
                    placeholder = config.getString("placeholders.outOfStock");
                } else {
                    placeholder = String.valueOf(finalStock);
                }


                for(String line : special.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                            .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, special.getConfigKey())))
                            .replace("%stock%", placeholder)
                            .replace("%price%", String.valueOf(special.getPrice())));
                }

            } else {
                for(String line : special.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                            .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, special.getConfigKey())))
                            .replace("%price%", String.valueOf(special.getPrice())));
                }

            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.getMaterial()).get().parseItem())
                    .name(special.getName())
                    .amount(special.getAmount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if(special.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(specialSlots.get(specialCounter), stack, event -> plugin.getShopUtils().buyHandler(special, player, stack));
            specialCounter++;

            if(specialCounter == specialSlots.size()) break;

        }

        for(RotatingItems items : plugin.getRotatingLoader().getRotatingItemsList()){

            long normalRemaining = plugin.getTimeManager().getNormalTime() - System.currentTimeMillis();
            long specialRemaining = plugin.getTimeManager().getSpecialTime() - System.currentTimeMillis();

            List<String> lore = new ArrayList<>();
            for(String line : items.getLore()){
                lore.add(line.replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                        .replace("%timeNormal%", utils.getFormattedString(normalRemaining))
                        .replace("%timeSpecial%", utils.getFormattedString(specialRemaining)));
            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.getMaterial()).get().parseItem())
                    .name(items.getName())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);
            if(items.isGlow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ATTRIBUTES);
            ItemStack stack = builder.build();

            if(items.getSlots().isEmpty()){
                setItem(items.getSlot(), stack);
            } else {
                int[] slots1 = Ints.toArray(items.getSlots());
                setItems(slots1, stack);
            }

        }

        if(config.getBoolean("options.autoUpdateGUI.enabled")){
            int tick = config.getInt("options.autoUpdateGUI.updateEvery");
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                int taskNormal = 0;
                for(ShopItems normal : plugin.getNormalItems()){

                    List<String> lore = new ArrayList<>();
                    if(normal.isUseStock()){
                        int finalStock;
                        if(stockManager.isItemExist(normal.getConfigKey())){
                            finalStock = stockManager.getItemStock(normal.getConfigKey());
                        } else {
                            finalStock = normal.getStock();
                            stockManager.setStock(normal.getConfigKey(), normal.getStock());
                        }

                        String placeholder;
                        if(finalStock <= 0){
                            placeholder = config.getString("placeholders.outOfStock");
                        } else {
                            placeholder = String.valueOf(finalStock);
                        }
                        for(String line : normal.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                                    .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, normal.getConfigKey())))
                                    .replace("%stock%", placeholder)
                                    .replace("%price%", String.valueOf(normal.getPrice())));
                        }
                    } else {
                        for(String line : normal.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                                    .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, normal.getConfigKey())))
                                    .replace("%price%", String.valueOf(normal.getPrice())));
                        }
                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.getMaterial()).get().parseItem())
                            .name(normal.getName())
                            .amount(normal.getAmount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if(normal.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(normalSlots.get(taskNormal), stack, event -> plugin.getShopUtils().buyHandler(normal, player, stack));
                    taskNormal++;

                    if(taskNormal == normalSlots.size()) break;
                }

                int taskSpecial = 0;
                for(ShopItems special : plugin.getSpecialItems()){
                    List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

                    List<String> lore = new ArrayList<>();
                    if(special.isUseStock()){
                        int finalStock;
                        if(stockManager.isItemExist(special.getConfigKey())){
                            finalStock = stockManager.getItemStock(special.getConfigKey());
                        } else {
                            finalStock = special.getStock();
                            stockManager.setStock(special.getConfigKey(), special.getStock());
                        }

                        String placeholder;
                        if(finalStock <= 0){
                            placeholder = config.getString("placeholders.outOfStock");
                        } else {
                            placeholder = String.valueOf(finalStock);
                        }


                        for(String line : special.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                                    .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, special.getConfigKey())))
                                    .replace("%stock%", placeholder)
                                    .replace("%price%", String.valueOf(special.getPrice())));
                        }

                    } else {
                        for(String line : special.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                                    .replace("%limit%", String.valueOf(limitConfig.getPlayerLimit(player, special.getConfigKey())))
                                    .replace("%price%", String.valueOf(special.getPrice())));
                        }

                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.getMaterial()).get().parseItem())
                            .name(special.getName())
                            .amount(special.getAmount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if(special.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(specialSlots.get(taskSpecial), stack, event -> plugin.getShopUtils().buyHandler(special, player, stack));
                    taskSpecial++;

                    if(taskSpecial == specialSlots.size()) break;

                }

                for(RotatingItems items : plugin.getRotatingLoader().getRotatingItemsList()){

                    long normalRemaining = plugin.getTimeManager().getNormalTime() - System.currentTimeMillis();
                    long specialRemaining = plugin.getTimeManager().getSpecialTime() - System.currentTimeMillis();

                    List<String> lore = new ArrayList<>();
                    for(String line : items.getLore()){
                        lore.add(line.replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                .replace("%timeNormal%", utils.getFormattedString(normalRemaining))
                                .replace("%timeSpecial%", utils.getFormattedString(specialRemaining)));
                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.getMaterial()).get().parseItem())
                            .name(items.getName())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);
                    if(items.isGlow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ATTRIBUTES);
                    ItemStack stack = builder.build();

                    if(items.getSlots().isEmpty()){
                        setItem(items.getSlot(), stack);
                    } else {
                        int[] slots1 = Ints.toArray(items.getSlots());
                        setItems(slots1, stack);
                    }

                }
            }, 0L, tick);
            this.addCloseHandler(event -> task.cancel());
        }

    }

}
