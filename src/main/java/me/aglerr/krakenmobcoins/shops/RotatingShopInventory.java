package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.configs.LimitManager;
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

    public RotatingShopInventory(int size, String title, Player player) {
        super(size, title);

        FileConfiguration config = MobCoins.getInstance().getConfig();
        LimitManager limitManager = MobCoins.getInstance().getLimitManager();
        Utils utils = MobCoins.getInstance().getUtils();

        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

        List<Integer> normalSlots = config.getIntegerList("rotatingShop.normalItemSlots");

        int normalCounter = 0;
        for(ShopItems normal : MobCoins.getInstance().getNormalItems()){

            List<String> lore = new ArrayList<>();
            if(normal.isUseStock()){
                int finalStock;
                if(MobCoins.getInstance().getStock().containsKey(normal.getConfigKey())){
                    finalStock = MobCoins.getInstance().getStock().get(normal.getConfigKey());
                } else {
                    finalStock = normal.getStock();
                    MobCoins.getInstance().getStock().put(normal.getConfigKey(), normal.getStock());
                }

                String placeholder;
                if(finalStock <= 0){
                    placeholder = config.getString("placeholders.outOfStock");
                } else {
                    placeholder = String.valueOf(finalStock);
                }
                for(String line : normal.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, normal.getConfigKey())))
                            .replace("%stock%", placeholder));
                }
            } else {
                for(String line : normal.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, normal.getConfigKey()))));
                }
            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.getMaterial()).get().parseItem())
                    .name(normal.getName())
                    .amount(normal.getAmount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if(normal.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(normalSlots.get(normalCounter), stack, event -> MobCoins.getInstance().getShopUtils().buyHandler(event, normal, player, stack));
            normalCounter++;

            if(normalCounter == normalSlots.size()) break;
        }

        int specialCounter = 0;
        for(ShopItems special : MobCoins.getInstance().getSpecialItems()){
            List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

            List<String> lore = new ArrayList<>();
            if(special.isUseStock()){
                int finalStock;
                if(MobCoins.getInstance().getStock().containsKey(special.getConfigKey())){
                    finalStock = MobCoins.getInstance().getStock().get(special.getConfigKey());
                } else {
                    finalStock = special.getStock();
                    MobCoins.getInstance().getStock().put(special.getConfigKey(), special.getStock());
                }
                String placeholder;
                if(finalStock <= 0){
                    placeholder = config.getString("placeholders.outOfStock");
                } else {
                    placeholder = String.valueOf(finalStock);
                }


                for(String line : special.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, special.getConfigKey())))
                            .replace("%stock%", placeholder));
                }

            } else {
                for(String line : special.getLore()){
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                            .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, special.getConfigKey()))));
                }

            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.getMaterial()).get().parseItem())
                    .name(special.getName())
                    .amount(special.getAmount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if(special.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(specialSlots.get(specialCounter), stack, event -> MobCoins.getInstance().getShopUtils().buyHandler(event, special, player, stack));
            specialCounter++;

            if(specialCounter == specialSlots.size()) break;

        }

        for(RotatingItems items : MobCoins.getInstance().getRotatingLoader().getRotatingItemsList()){

            long normalRemaining = MobCoins.getInstance().getNormalTime() - System.currentTimeMillis();
            long specialRemaining = MobCoins.getInstance().getSpecialTime() - System.currentTimeMillis();

            List<String> lore = new ArrayList<>();
            for(String line : items.getLore()){
                lore.add(line.replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
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
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(MobCoins.getInstance(), () -> {
                int taskNormal = 0;
                for(ShopItems normal : MobCoins.getInstance().getNormalItems()){

                    List<String> lore = new ArrayList<>();
                    if(normal.isUseStock()){
                        int finalStock;
                        if(MobCoins.getInstance().getStock().containsKey(normal.getConfigKey())){
                            finalStock = MobCoins.getInstance().getStock().get(normal.getConfigKey());
                        } else {
                            finalStock = normal.getStock();
                            MobCoins.getInstance().getStock().put(normal.getConfigKey(), normal.getStock());
                        }

                        String placeholder;
                        if(finalStock <= 0){
                            placeholder = config.getString("placeholders.outOfStock");
                        } else {
                            placeholder = String.valueOf(finalStock);
                        }
                        for(String line : normal.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, normal.getConfigKey())))
                                    .replace("%stock%", placeholder));
                        }
                    } else {
                        for(String line : normal.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, normal.getConfigKey()))));
                        }
                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.getMaterial()).get().parseItem())
                            .name(normal.getName())
                            .amount(normal.getAmount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if(normal.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(normalSlots.get(taskNormal), stack, event -> MobCoins.getInstance().getShopUtils().buyHandler(event, normal, player, stack));
                    taskNormal++;

                    if(taskNormal == normalSlots.size()) break;
                }

                int taskSpecial = 0;
                for(ShopItems special : MobCoins.getInstance().getSpecialItems()){
                    List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

                    List<String> lore = new ArrayList<>();
                    if(special.isUseStock()){
                        int finalStock;
                        if(MobCoins.getInstance().getStock().containsKey(special.getConfigKey())){
                            finalStock = MobCoins.getInstance().getStock().get(special.getConfigKey());
                        } else {
                            finalStock = special.getStock();
                            MobCoins.getInstance().getStock().put(special.getConfigKey(), special.getStock());
                        }
                        String placeholder;
                        if(finalStock <= 0){
                            placeholder = config.getString("placeholders.outOfStock");
                        } else {
                            placeholder = String.valueOf(finalStock);
                        }


                        for(String line : special.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, special.getConfigKey())))
                                    .replace("%stock%", placeholder));
                        }

                    } else {
                        for(String line : special.getLore()){
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.getLimit()))
                                    .replace("%limit%", String.valueOf(limitManager.getPlayerLimit(player, special.getConfigKey()))));
                        }

                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.getMaterial()).get().parseItem())
                            .name(special.getName())
                            .amount(special.getAmount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if(special.isGlowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(specialSlots.get(taskSpecial), stack, event -> MobCoins.getInstance().getShopUtils().buyHandler(event, special, player, stack));
                    taskSpecial++;

                    if(taskSpecial == specialSlots.size()) break;

                }

                for(RotatingItems items : MobCoins.getInstance().getRotatingLoader().getRotatingItemsList()){

                    long normalRemaining = MobCoins.getInstance().getNormalTime() - System.currentTimeMillis();
                    long specialRemaining = MobCoins.getInstance().getSpecialTime() - System.currentTimeMillis();

                    List<String> lore = new ArrayList<>();
                    for(String line : items.getLore()){
                        lore.add(line.replace("%coins%", utils.getDFormat().format(playerCoins.getMoney()))
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
