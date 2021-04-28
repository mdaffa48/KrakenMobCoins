package me.aglerr.krakenmobcoins.database;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class SQL {

    public String host, database, username, password;
    public int port;
    public boolean useSSL;

    private String table = "krakencoins";
    private final MobCoins plugin;

    public SQL(final MobCoins plugin) {
        this.plugin = plugin;

        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();
        if (config.getBoolean("MYSQL.enabled")) {
            utils.sendConsoleMessage("Trying to connect to the database.");
            try {

                host = config.getString("MYSQL.hostname");
                database = config.getString("MYSQL.database");
                username = config.getString("MYSQL.username");
                password = config.getString("MYSQL.password");
                port = config.getInt("MYSQL.port");
                useSSL = config.getBoolean("MYSQL.useSSL");

                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = getNewConnection();
                String command = "CREATE TABLE IF NOT EXISTS krakencoins (UUID Text, Coins Text)";
                PreparedStatement statement = connection.prepareStatement(command);
                statement.execute();

                utils.sendConsoleMessage("MySQL connected!");
                statement.close();
                connection.close();

            } catch (Exception e) {
                utils.sendConsoleMessage("MySQL error!");
                e.printStackTrace();
            }
        } else {
            utils.sendConsoleMessage("Trying to connect to the database.");
            try {

                Class.forName("org.sqlite.JDBC");
                Connection connection = getNewConnection();
                String command = "CREATE TABLE IF NOT EXISTS krakencoins (UUID Text, Coins Text)";
                PreparedStatement statement = connection.prepareStatement(command);
                statement.execute();

                utils.sendConsoleMessage("SQLite connected!");
                statement.close();
                connection.close();

            } catch (Exception e) {
                utils.sendConsoleMessage("SQLite error!");
                e.printStackTrace();
            }
        }
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Connection getNewConnection() throws SQLException {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("MYSQL.enabled")) {
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?verifyServerCertificate=false&useSSL=" + this.useSSL,
                    this.username, this.password);
        } else {
            return DriverManager.getConnection("jdbc:sqlite:plugins/KrakenMobcoins/database.db");
        }
    }

    public void insert(Connection connection, String uuid, String value) {
        try {

            String command = "INSERT INTO " + table + " (UUID, Coins) VALUES ('" + uuid + "','" + value + "');";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.execute();

            statement.close();

        } catch (SQLException exception) {
            System.out.println("[KrakenMobCoins] Error trying to insert to the database!");
            exception.printStackTrace();
        }

    }

    public void update(Connection connection, String uuid, String value) {
        try {

            String command = "UPDATE " + table + " SET Coins='" + value + "' WHERE UUID='" + uuid + "'";
            PreparedStatement statement = connection.prepareStatement(command);
            statement.executeUpdate();

            statement.close();

        } catch (SQLException exception) {
            System.out.println("[KrakenMobCoins] Error trying to update to the database!");
            exception.printStackTrace();
        }

    }

}
