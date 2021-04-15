package me.aglerr.krakenmobcoins.database;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class PlayerCoins {

    private String uuid;
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

    public String getPlayerName(){
        UUID playerUUID = UUID.fromString(this.uuid);
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerUUID);
        return target.getName();
    }

    public void save(boolean async) {
        SQL database = MobCoins.getInstance().getDatabase();
        if(async){
            Bukkit.getScheduler().runTaskAsynchronously(MobCoins.getInstance(), () -> {
                try{
                    Connection connection = database.getNewConnection();
                    String command = "SELECT UUID FROM krakencoins WHERE UUID='" + uuid + "'";
                    PreparedStatement statement = connection.prepareStatement(command);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()){
                        database.update(String.valueOf(coins), uuid);
                    } else {
                        database.insert(String.valueOf(coins), uuid);
                    }

                    rs.close();
                    statement.close();
                    connection.close();
                } catch(SQLException exception){
                    System.out.println("[KrakenMobCoins] Error saving player account data.");
                    exception.printStackTrace();
                }
            });
        } else {

            try{
                Connection connection = database.getNewConnection();
                String command = "SELECT UUID FROM krakencoins WHERE UUID='" + uuid + "'";
                PreparedStatement statement = connection.prepareStatement(command);
                ResultSet rs = statement.executeQuery();
                if(rs.next()){
                    database.update(String.valueOf(coins), uuid);
                } else {
                    database.insert(String.valueOf(coins), uuid);
                }

                rs.close();
                statement.close();
                connection.close();
            } catch(SQLException exception){
                System.out.println("[KrakenMobCoins] Error saving player account data.");
                exception.printStackTrace();
            }

        }

    }

}
