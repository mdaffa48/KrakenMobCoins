package me.aglerr.krakenmobcoins.utils;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoinsData {

    public static void createAccount(String uuid, double amount) {
        PlayerCoins coins = new PlayerCoins(uuid);
        coins.setMoney(amount);

        MobCoins.getInstance().getAccounts().put(uuid, coins);
    }

    public static void createAccount(String uuid) throws SQLException {
        FileConfiguration config = MobCoins.getInstance().getConfig();

        double balance = config.getDouble("options.startingBalance");

        PlayerCoins coins = new PlayerCoins(uuid);
        coins.setMoney(balance);
        coins.save();

        MobCoins.getInstance().getAccounts().put(uuid, coins);

    }

    public static void loadAccounts(){
        Thread th = new Thread(() -> {
            try{
                String command = "SELECT * FROM krakencoins";
                Connection connection = MobCoins.getDatabase().getNewConnection();
                PreparedStatement statement = connection.prepareStatement(command);
                ResultSet rs = statement.executeQuery();
                while(rs.next()){

                    String uuid = rs.getString("UUID");
                    double money = Double.valueOf(rs.getString("Coins"));
                    PlayerCoins coins = new PlayerCoins(uuid);
                    coins.setMoney(money);

                    MobCoins.getInstance().getAccounts().put(uuid, coins);
                    MobCoins.getInstance().getValueModify().put(coins, money);

                }

                rs.close();
                statement.close();
                connection.close();

                MobCoins.getInstance().getUtils().sendConsoleMessage("Successfully loaded all saved accounts!");

            } catch (SQLException e) {
                Bukkit.getPluginManager().disablePlugin(MobCoins.getInstance());
                MobCoins.getInstance().getUtils().sendConsoleMessage("&cError loading database player accounts!");
            }
        });
        th.start();
    }

}
