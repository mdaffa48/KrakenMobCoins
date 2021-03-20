package me.aglerr.krakenmobcoins.database;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class SQL {

    public String host, database, username, password;
    public int port;
    public boolean useSSL;

    private String table = "krakencoins";

    public SQL() {
        FileConfiguration config = MobCoins.getInstance().getConfig();
        Utils utils = MobCoins.getInstance().getUtils();
        if(config.getBoolean("MYSQL.enabled")){
            utils.sendConsoleMessage("Trying to connect to the database.");
            try{

                host = config.getString("MYSQL.hostname");
                database = config.getString("MYSQL.database");
                username = config.getString("MYSQL.username");
                password = config.getString("MYSQL.password");
                port = config.getInt("MYSQL.port");
                useSSL = config.getBoolean("MYSQL.useSSL");

                Connection connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?verifyServerCertificate=false&useSSL=false",
                        this.username, this.password);
                String command = "CREATE TABLE IF NOT EXISTS krakencoins (UUID Text, Coins Text)";
                PreparedStatement statement = connection.prepareStatement(command);
                statement.execute();

                utils.sendConsoleMessage("MySQL connected!");
                statement.close();
                connection.close();

            } catch(Exception e){
                utils.sendConsoleMessage("MySQL error!");
                e.printStackTrace();
            }
        } else {
            utils.sendConsoleMessage("Trying to connect to the database.");
            try{

                Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/KrakenMobcoins/database.db");
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

    public void setTable(String table){
        this.table = table;
    }

    public Connection getNewConnection() throws SQLException {
        FileConfiguration config = MobCoins.getInstance().getConfig();
        if(config.getBoolean("MYSQL.enabled")){
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?verifyServerCertificate=false&useSSL=false",
                    this.username, this.password);
        } else {
            return DriverManager.getConnection("jdbc:sqlite:plugins/KrakenMobcoins/database.db");
        }
    }

    public void insert(String value, String uuid) throws SQLException {
        Connection connection = this.getNewConnection();
        String command = "INSERT INTO " + table + " (UUID, Coins) VALUES ('" + uuid + "','" + value + "');";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.execute();

        statement.close();
        connection.close();

    }

    public void update(String value, String uuid) throws SQLException {
        Connection connection = this.getNewConnection();
        String command = "UPDATE " + table + " SET Coins='" + value + "' WHERE UUID='" + uuid + "'";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.executeUpdate();

        statement.close();
        connection.close();

    }

}
