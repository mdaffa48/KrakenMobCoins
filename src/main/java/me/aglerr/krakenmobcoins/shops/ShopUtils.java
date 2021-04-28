package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.manager.AccountManager;
import me.aglerr.krakenmobcoins.manager.ItemStockManager;
import me.aglerr.krakenmobcoins.manager.PurchaseLimitManager;
import me.aglerr.krakenmobcoins.shops.items.ShopNormalItems;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {
    
    private final MobCoins plugin;
    public ShopUtils(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void buyHandler(ShopItems items, Player player, ItemStack stack){

        ItemStockManager stockManager = plugin.getItemStockManager();
        AccountManager accountManager = plugin.getAccountManager();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();

        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());

        FileConfiguration config = plugin.getConfig();
        FileConfiguration shop = plugin.getShopManager().getConfiguration();

        Utils utils = plugin.getUtils();

        if(playerCoins.getMoney() >= items.getPrice()){
            if(config.getBoolean("options.confirmationMenu")){

                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.getPrice(), items.getCommands(), items.getConfigKey(), items.getLimit(), items.isUseStock(), plugin).open(player);

                return;
            }

            if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey())){
                    int playerLimit = limitManager.getLimit(player.getUniqueId(), items.getConfigKey());
                    if(playerLimit >= items.getLimit()){
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }
            }

            if(items.isUseStock()){
                int currentStock = stockManager.getItemStock(items.getConfigKey());
                if(currentStock <= 0){
                    player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }
            }

            playerCoins.reduceMoney(items.getPrice());

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
                int currentStock = stockManager.getItemStock(items.getConfigKey());
                stockManager.setStock(items.getConfigKey(), currentStock - 1);
            }

            if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey())){
                    limitManager.incrementLimit(player.getUniqueId(), items.getConfigKey());
                    return;
                }

                limitManager.modifyLimit(player.getUniqueId(), items.getConfigKey(), 1);

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

    public void buyHandler(ShopNormalItems items, Player player, ItemStack stack){

        ItemStockManager stockManager = plugin.getItemStockManager();
        AccountManager accountManager = plugin.getAccountManager();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();

        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());

        FileConfiguration config = plugin.getConfig();
        FileConfiguration shop = plugin.getShopManager().getConfiguration();

        Utils utils = plugin.getUtils();

        if(playerCoins.getMoney() >= items.getPrice()){
            if(config.getBoolean("options.confirmationMenu")){
                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.getPrice(), items.getCommands(), items.getConfigKey(), items.getLimit(), items.isUseStock(), plugin).open(player);
                return;
            }

            if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey())) {
                    int playerLimit = limitManager.getLimit(player.getUniqueId(), items.getConfigKey());
                    if (playerLimit >= items.getLimit()) {
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }
            }

            if(items.isUseStock()){
                int currentStock = stockManager.getItemStock(items.getConfigKey());
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
                int currentStock = stockManager.getItemStock(items.getConfigKey());
                stockManager.setStock(items.getConfigKey(), currentStock - 1);
            }

            if(config.getBoolean("options.purchaseLimit") && items.getLimit() > 0){
                if(limitManager.containsLimit(player.getUniqueId(), items.getConfigKey())){
                    limitManager.incrementLimit(player.getUniqueId(), items.getConfigKey());
                    return;
                }

                limitManager.modifyLimit(player.getUniqueId(), items.getConfigKey(), 1);

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

}
