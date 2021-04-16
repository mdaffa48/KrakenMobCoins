package me.aglerr.krakenmobcoins.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.api.events.MobCoinsRedeemEvent;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    private final MobCoins plugin;
    public PlayerInteract(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Utils utils = plugin.getUtils();
        FileConfiguration config = plugin.getConfig();

        if(utils.hasOffhand()){
            if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        }

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack hand = player.getItemInHand();
            if(hand == null || hand.getType() == Material.AIR) return;

            NBTItem nbtItem = new NBTItem(hand);
            if(!nbtItem.hasNBTData()) return;

            String info = nbtItem.getString("info");
            if(!info.equals("krakenmobcoins")) return;

            double amount = 0;
            if(hand.getAmount() == 1){
                amount = nbtItem.getDouble("amount");
            } else if(hand.getAmount() > 1){
                amount = nbtItem.getDouble("amount") * hand.getAmount();
            }

            event.setCancelled(true);
            PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
            if(playerCoins == null){
                player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                return;
            }

            MobCoinsRedeemEvent mobCoinsRedeemEvent = new MobCoinsRedeemEvent(player, amount, hand);
            Bukkit.getPluginManager().callEvent(mobCoinsRedeemEvent);
            if(mobCoinsRedeemEvent.isCancelled()) return;

            player.setItemInHand(XMaterial.AIR.parseItem());
            player.updateInventory();

            playerCoins.setMoney(playerCoins.getMoney() + mobCoinsRedeemEvent.getAmount());
            player.sendMessage(utils.color(ConfigMessages.REDEEM.toString())
            .replace("%prefix%", utils.getPrefix())
            .replace("%coins%", utils.getDecimalFormat().format(amount)));

            if(config.getBoolean("sounds.onRedeem.enabled")){
                String name = config.getString("sounds.onRedeem.name").toUpperCase();
                float volume = (float) config.getDouble("sounds.onRedeem.volume");
                float pitch = (float) config.getDouble("sounds.onRedeem.pitch");

                player.playSound(player.getLocation(), XSound.matchXSound(name).get().parseSound(), volume, pitch);

            }

        }


    }

}
