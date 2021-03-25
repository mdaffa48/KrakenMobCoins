package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.configs.LimitManager;
import me.aglerr.krakenmobcoins.shops.category.shops.ShopNormalItems;
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

    public NormalShopInventory(int size, String title, String category, Player player) {
        super(size, title);

        FileConfiguration config = MobCoins.getInstance().getConfig();
        Utils utils = MobCoins.getInstance().getUtils();
        LimitManager limitManager = MobCoins.getInstance().getLimitManager();
        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

        for(ShopNormalItems items : MobCoins.getInstance().getShopNormalLoader().getShopNormalItemsList()){

            List<String> lore = new ArrayList<>();
            if(items.isUseStock()){
                int finalStock;
                if(MobCoins.getInstance().getStock().containsKey(items.getConfigKey())){
                    finalStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                } else {
                    finalStock = items.getStock();
                    MobCoins.getInstance().getStock().put(items.getConfigKey(), items.getStock());
                }

                String placeholder;
                if(finalStock <= 0){
                    placeholder = config.getString("placeholders.outOfStock");
                } else {
                    placeholder = String.valueOf(finalStock);
                }

                for(String line : items.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, items.getConfigKey())))
                            .replace("%stock%", placeholder)
                            .replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
                            .replace("%price%", String.valueOf(items.getPrice())));
                }

            } else {

                for(String line : items.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, items.getConfigKey())))
                            .replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
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
                            MobCoins.getInstance().getShopUtils().buyHandler(event, items, player, stack);
                        }

                        if(items.getType().equals("back")){
                            utils.openShopMenu(player);
                        }

                    });
                } else {
                    int[] slots1 = Ints.toArray(items.getSlots());
                    setItems(slots1, stack, event -> {
                        if(items.getType().equals("shop")){

                            MobCoins.getInstance().getShopUtils().buyHandler(event, items, player, stack);
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
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(MobCoins.getInstance(), () -> {
                for(ShopNormalItems items : MobCoins.getInstance().getShopNormalLoader().getShopNormalItemsList()){

                    List<String> lore = new ArrayList<>();
                    if(items.isUseStock()){
                        int finalStock;
                        if(MobCoins.getInstance().getStock().containsKey(items.getConfigKey())){
                            finalStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                        } else {
                            finalStock = items.getStock();
                            MobCoins.getInstance().getStock().put(items.getConfigKey(), items.getStock());
                        }

                        String placeholder;
                        if(finalStock <= 0){
                            placeholder = config.getString("placeholders.outOfStock");
                        } else {
                            placeholder = String.valueOf(finalStock);
                        }
                        for(String line : items.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, items.getConfigKey())))
                                    .replace("%stock%", placeholder)
                                    .replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
                                    .replace("%price%", String.valueOf(items.getPrice())));
                        }

                    } else {

                        for(String line : items.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, items.getConfigKey())))
                                    .replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
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
                                    MobCoins.getInstance().getShopUtils().buyHandler(event, items, player, stack);
                                }

                                if(items.getType().equals("back")){
                                    utils.openShopMenu(player);
                                }

                            });
                        } else {
                            int[] slots1 = Ints.toArray(items.getSlots());
                            setItems(slots1, stack, event -> {
                                if(items.getType().equals("shop")){

                                    MobCoins.getInstance().getShopUtils().buyHandler(event, items, player, stack);
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
