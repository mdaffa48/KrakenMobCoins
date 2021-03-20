package me.aglerr.krakenmobcoins.shops;

import com.cryptomorin.xseries.XMaterial;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmationInventory extends FastInv {
    public ConfirmationInventory(int size, String title, ItemStack itemStack, double price, List<String> commands, String configKey, int limit, boolean useStock, int stock) {
        super(size, title);

        FileConfiguration config = MobCoins.getInstance().getConfig();
        FileConfiguration shop = MobCoins.getInstance().getShopManager().getConfiguration();
        Utils utils = MobCoins.getInstance().getUtils();

        String acceptMaterial = shop.getString("confirmationMenu.items.acceptButton.material").toUpperCase();
        String acceptName = shop.getString("confirmationMenu.items.acceptButton.name");
        int acceptSlot = shop.getInt("confirmationMenu.items.acceptButton.slot");
        List<String> acceptLore = shop.getStringList("confirmationMenu.items.acceptButton.lore");

        ItemStack accept = XMaterial.matchXMaterial(acceptMaterial).get().parseItem();
        ItemMeta am = accept.getItemMeta();
        am.setDisplayName(utils.color(acceptName));
        am.setLore(utils.color(acceptLore));
        accept.setItemMeta(am);

        setItem(acceptSlot, accept, event -> {
            if(!(event.getWhoClicked() instanceof Player)) return;

            Player player = (Player) event.getWhoClicked();
            PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
            if(playerCoins != null){
                if(player.getInventory().firstEmpty() == -1){
                    player.closeInventory();
                    player.sendMessage(utils.color(ConfigMessages.INVENTORY_FULL.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }

                if(config.getBoolean("options.purchaseLimit") && limit > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, configKey);
                    if(playerLimit >= limit){
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                        player.closeInventory();
                        return;
                    }
                }

                if(useStock){
                    int currentStock = MobCoins.getInstance().getStock().get(configKey);
                    if(currentStock <= 0){
                        player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }

                playerCoins.setMoney(playerCoins.getMoney() - price);
                player.sendMessage(utils.color(ConfigMessages.PURCHASED_ITEM.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%item%", utils.color(itemStack.getItemMeta().getDisplayName())));

                for(String command : commands){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }

                if(config.getBoolean("options.closeAfterPurchase")){
                    player.closeInventory();
                } else {
                    utils.openShopMenu(player);
                }

                if(useStock){
                    int currentStock = MobCoins.getInstance().getStock().get(configKey);
                    MobCoins.getInstance().getStock().put(configKey, currentStock - 1);
                }

                if(config.getBoolean("options.purchaseLimit") && limit > 0){
                    int playerLimit = MobCoins.getInstance().getLimitManager().getPlayerLimit(player, configKey);
                    MobCoins.getInstance().getLimitManager().setPlayerLimit(player, configKey, playerLimit + 1);
                }

            } else {

                player.closeInventory();
                player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                return;
            }
        });

        String cancelMaterial = shop.getString("confirmationMenu.items.cancelButton.material").toUpperCase();
        String cancelName = shop.getString("confirmationMenu.items.cancelButton.name");
        int cancelSlot = shop.getInt("confirmationMenu.items.cancelButton.slot");
        List<String> cancelLore = shop.getStringList("confirmationMenu.items.cancelButton.lore");

        ItemStack cancel = XMaterial.matchXMaterial(cancelMaterial).get().parseItem();
        ItemMeta cm = cancel.getItemMeta();
        cm.setDisplayName(utils.color(cancelName));
        cm.setLore(utils.color(cancelLore));
        cancel.setItemMeta(cm);

        setItem(cancelSlot, cancel, event -> {
            Player clicked = (Player) event.getWhoClicked();
            utils.openShopMenu(clicked);
        });

        if(shop.getBoolean("confirmationMenu.items.purchasedItem.enabled")){
            int slot = shop.getInt("confirmationMenu.items.purchasedItem.slot");
            setItem(slot, itemStack);
        }

        if(shop.getBoolean("confirmationMenu.items.fillGUI.enabled")){
            String material = shop.getString("confirmationMenu.items.fillGUI.material");
            String name = shop.getString("confirmationMenu.items.fillGUI.name");

            ItemStack fill = XMaterial.matchXMaterial(material).get().parseItem();
            ItemMeta fm = fill.getItemMeta();
            fm.setDisplayName(utils.color(name));
            fill.setItemMeta(fm);

            for(int x = 0; x < size; x++){
                if(getInventory().getItem(x) == null || getInventory().getItem(x).getType() == Material.AIR){
                    setItem(x, fill);
                }
            }
        }

    }
}
