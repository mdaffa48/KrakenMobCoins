package me.aglerr.krakenmobcoins;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class PlayerCoins {

    private String uuid;
    private double coins = 0;

    public PlayerCoins(String uuid){
        this.uuid = uuid;
    }

    public void loadData(){
        try{
            Connection connection = MobCoins.getDatabase().getNewConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT Coins FROM krakencoins WHERE UUID='" + uuid + "'");
            if(rs.next()){
                this.coins = Double.parseDouble(rs.getString("Coins"));
            }

            rs.close();
            statement.close();
            connection.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
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

    public void save() throws SQLException {
        Connection connection = MobCoins.getDatabase().getNewConnection();
        String command = "SELECT UUID FROM krakencoins WHERE UUID='" + uuid + "'";
        PreparedStatement statement = connection.prepareStatement(command);
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            MobCoins.getDatabase().update(String.valueOf(coins), uuid);
        } else {
            MobCoins.getDatabase().insert(String.valueOf(coins), uuid);
        }

        rs.close();
        statement.close();
        connection.close();

    }

}
