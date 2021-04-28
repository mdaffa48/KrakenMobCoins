package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.manager.ItemStockManager;
import me.aglerr.krakenmobcoins.manager.PurchaseLimitManager;
import me.aglerr.krakenmobcoins.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.shops.items.ShopNormalItems;
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

public class NormalShopInventory extends FastInv {

    public NormalShopInventory(int size, String title, String category, Player player, MobCoins plugin) {
        super(size, title);

        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();
        PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId().toString());
        final ItemStockManager stockManager = plugin.getItemStockManager();

        for(ShopNormalItems items : plugin.getItemsLoader().getShopNormalItemsList()){

            List<String> lore = new ArrayList<>();
            if(items.isUseStock()){
                int finalStock = 0;
                if(stockManager.isItemExist(items.getConfigKey()))
                    finalStock = stockManager.getItemStock(items.getConfigKey());

                if(!stockManager.isItemExist(items.getConfigKey())){
                    finalStock = items.getStock();
                    stockManager.setStock(items.getConfigKey(), items.getStock());
                }

                String placeholder = null;
                if(finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                if(finalStock > 0) placeholder = String.valueOf(finalStock);
                if(placeholder == null) placeholder = "Placeholder Error!";

                String limit = null;
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.getConfigKey()));

                if(limit == null) limit = "0";

                for(String line : items.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                            .replace("%limit%",  limit)
                            .replace("%stock%", placeholder)
                            .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                            .replace("%price%", String.valueOf(items.getPrice())));
                }

            } else {

                String limit = null;
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.getConfigKey()));

                if(limit == null) limit = "0";

                for(String line : items.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                            .replace("%limit%", limit)
                            .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                            .replace("%price%", String.valueOf(items.getPrice())));
                }

            }

            if(items.getCategory().equalsIgnoreCase(category)){

                ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.getMaterial()).get().parseItem())
                        .amount(items.getAmount())
                        .name(items.getName())
                        .lore(lore)
                        .flag(ItemFlag.HIDE_ATTRIBUTES);
                if(items.isGlow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                ItemStack stack = builder.build();

                if(items.getSlots().isEmpty()){
                    setItem(items.getSlot(), stack, event -> {

                        if(items.getType().equals("shop")){
                            plugin.getShopUtils().buyHandler(items, player, stack);
                        }

                        if(items.getType().equals("back")){
                            utils.openShopMenu(player);
                        }

                    });
                } else {

                    setItems(Ints.toArray(items.getSlots()), stack, event -> {
                        if(items.getType().equals("shop")){

                            plugin.getShopUtils().buyHandler(items, player, stack);
                        }

                        if(items.getType().equals("back")){
                            utils.openShopMenu(player);
                        }

                    });
                }

            }


        }

        if(config.getBoolean("options.autoUpdateGUI.enabled")){
            int tick = config.getInt("options.autoUpdateGUI.updateEvery");
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for(ShopNormalItems items : plugin.getItemsLoader().getShopNormalItemsList()){

                    List<String> lore = new ArrayList<>();
                    if(items.isUseStock()){
                        int finalStock = 0;
                        if(stockManager.isItemExist(items.getConfigKey()))
                            finalStock = stockManager.getItemStock(items.getConfigKey());

                        if(!stockManager.isItemExist(items.getConfigKey())){
                            finalStock = items.getStock();
                            stockManager.setStock(items.getConfigKey(), items.getStock());
                        }

                        String placeholder = null;
                        if(finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                        if(finalStock > 0) placeholder = String.valueOf(finalStock);
                        if(placeholder == null) placeholder = "Placeholder Error!";

                        String limit = null;
                        if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.getConfigKey()));

                        if(limit == null) limit = "0";

                        for(String line : items.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                                    .replace("%limit%",  limit)
                                    .replace("%stock%", placeholder)
                                    .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                    .replace("%price%", String.valueOf(items.getPrice())));
                        }

                    } else {

                        String limit = null;
                        if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.getConfigKey()));

                        if(limit == null) limit = "0";

                        for(String line : items.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                                    .replace("%limit%", limit)
                                    .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                    .replace("%price%", String.valueOf(items.getPrice())));
                        }

                    }

                    if(items.getCategory().equalsIgnoreCase(category)){

                        ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.getMaterial()).get().parseItem())
                                .amount(items.getAmount())
                                .name(items.getName())
                                .lore(lore)
                                .flag(ItemFlag.HIDE_ATTRIBUTES);
                        if(items.isGlow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                        ItemStack stack = builder.build();

                        if(items.getSlots().isEmpty()){
                            setItem(items.getSlot(), stack, event -> {

                                if(items.getType().equals("shop")){
                                    plugin.getShopUtils().buyHandler(items, player, stack);
                                }

                                if(items.getType().equals("back")){
                                    utils.openShopMenu(player);
                                }

                            });
                        } else {

                            setItems(Ints.toArray(items.getSlots()), stack, event -> {
                                if(items.getType().equals("shop")){

                                    plugin.getShopUtils().buyHandler(items, player, stack);
                                }

                                if(items.getType().equals("back")){
                                    utils.openShopMenu(player);
                                }

                            });
                        }

                    }


                }
            }, 0L, tick);
            this.addCloseHandler(event -> task.cancel());
        }


    }
}
