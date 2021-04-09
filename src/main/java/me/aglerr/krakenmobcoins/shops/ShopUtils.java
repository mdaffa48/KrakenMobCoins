package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.shops.category.shops.ShopNormalItems;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

    public void buyHandler(InventoryClickEvent event, ShopItems items, Player player, ItemStack stack){

        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        FileConfiguration config = MobCoins.getInstance().getConfig();
        Utils utils = MobCoins.getInstance().getUtils();
        FileConfiguration shop = MobCoins.getInstance().getShopManager().getConfiguration();

        if(playerCoins.getMoney() >= items.getPrice()){
            if(config.getBoolean("options.confirmationMenu")){

                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.getPrice(), items.getCommands(), items.getConfigKey(), items.getLimit(), items.isUseStock(), items.getStock()).open(player);

            } else {

                if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, items.getConfigKey());
                    if(playerLimit >= items.getLimit()){
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }

                if(items.isUseStock()){
                    int currentStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                    if(currentStock <= 0){
                        player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }

                playerCoins.setMoney(playerCoins.getMoney() - items.getPrice());
                player.sendMessage(utils.color(ConfigMessages.PURCHASED_ITEM.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%item%", utils.color(items.getName())));
                for(String command : items.getCommands()){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }

                if(config.getBoolean("options.closeAfterPurchase")){
                    player.closeInventory();
                }

                if(items.isUseStock()){
                    int currentStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                    MobCoins.getInstance().getStock().put(items.getConfigKey(), currentStock - 1);
                }

                if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, items.getConfigKey());
                    MobCoins.getInstance().getLimitManager().setPlayerLimit(player, items.getConfigKey(), playerLimit + 1);
                }

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));
            return;
        }

    }

    public void buyHandler(InventoryClickEvent event, ShopNormalItems items, Player player, ItemStack stack){

        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        FileConfiguration config = MobCoins.getInstance().getConfig();
        Utils utils = MobCoins.getInstance().getUtils();
        FileConfiguration shop = MobCoins.getInstance().getShopManager().getConfiguration();

        if(playerCoins.getMoney() >= items.getPrice()){
            if(config.getBoolean("options.confirmationMenu")){

                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.getPrice(), items.getCommands(), items.getConfigKey(), items.getLimit(), items.isUseStock(), items.getStock()).open(player);

            } else {

                if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, items.getConfigKey());
                    if(playerLimit >= items.getLimit()){
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }

                if(items.isUseStock()){
                    int currentStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                    if(currentStock <= 0){
                        player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }

                playerCoins.setMoney(playerCoins.getMoney() - items.getPrice());
                player.sendMessage(utils.color(ConfigMessages.PURCHASED_ITEM.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%item%", utils.color(items.getName())));

                for(String command : items.getCommands()){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }

                if(config.getBoolean("options.closeAfterPurchase")){
                    player.closeInventory();
                }

                if(items.isUseStock()){
                    int currentStock = MobCoins.getInstance().getStock().get(items.getConfigKey());
                    MobCoins.getInstance().getStock().put(items.getConfigKey(), currentStock - 1);
                }

                if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, items.getConfigKey());
                    MobCoins.getInstance().getLimitManager().setPlayerLimit(player, items.getConfigKey(), playerLimit + 1);
                }

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));
            return;
        }

    }

}
