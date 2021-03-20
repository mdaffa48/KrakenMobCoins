package me.aglerr.krakenmobcoins.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MobCoinsRedeemEvent extends Event {

    private boolean isCancelled;

    private final Player player;
    private final double amount;
    private final ItemStack item;

    public MobCoinsRedeemEvent(Player player, double amount, ItemStack item){
        this.player = player;
        this.amount = amount;
        this.item = item;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer(){
        return this.player;
    }

    /**
     * @return the amount of mobcoins player redeemed.
     */
    public double getAmount(){
        return this.amount;
    }

    /**
     * @return the mobcoins itemstack.
     */
    public ItemStack getItem(){
        return this.item;
    }

}
