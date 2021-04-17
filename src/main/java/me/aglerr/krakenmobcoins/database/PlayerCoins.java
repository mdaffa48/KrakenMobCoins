package me.aglerr.krakenmobcoins.database;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class PlayerCoins {

    private final String uuid;
    private double coins = 0;

    public PlayerCoins(String uuid){
        this.uuid = uuid;
    }

    public double getMoney(){
        return this.coins;
    }

    public String getUUID(){
        return this.uuid;
    }

    public void setMoney(double value){
        this.coins = value;
    }

    public void reduceMoney(double value){
        setMoney(getMoney() - value);
    }

    public void addMoney(double value){
        setMoney(getMoney() + value);
    }

    public String getPlayerName(){
        return Bukkit.getOfflinePlayer(UUID.fromString(this.uuid)).getName();
    }

}
