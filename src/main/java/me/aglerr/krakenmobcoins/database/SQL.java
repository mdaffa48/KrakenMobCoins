package me.aglerr.krakenmobcoins.database;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

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
        if(config.getBoolean("MYSQL.enabled")){
            utils.sendConsoleMessage("Trying to connect to the database.");
            try{

                host = config.getString("MYSQL.hostname");
                database = config.getString("MYSQL.database");
                username = config.getString("MYSQL.username");
                password = config.getString("MYSQL.password");
                port = config.getInt("MYSQL.port");
                useSSL = config.getBoolean("MYSQL.useSSL");

                Class.forName("com.mysql.jdbc.Driver");
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

                Class.forName("org.sqlite.JDBC");
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
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("MYSQL.enabled")){
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?verifyServerCertificate=false&useSSL=false",
                    this.username, this.password);
        } else {
            return DriverManager.getConnection("jdbc:sqlite:plugins/KrakenMobcoins/database.db");
        }
    }

    public void insert(String value, String uuid) {
        new BukkitRunnable(){
            @Override
            public void run(){
                try{

                    Connection connection = getNewConnection();
                    String command = "INSERT INTO " + table + " (UUID, Coins) VALUES ('" + uuid + "','" + value + "');";
                    PreparedStatement statement = connection.prepareStatement(command);
                    statement.execute();

                    statement.close();
                    connection.close();

                } catch(SQLException exception){
                    System.out.println("[KrakenMobCoins] Error trying to insert to the database!");
                    exception.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

    }

    public void update(String value, String uuid) {
        new BukkitRunnable(){
            @Override
            public void run(){
                try{
                    Connection connection = getNewConnection();
                    String command = "UPDATE " + table + " SET Coins='" + value + "' WHERE UUID='" + uuid + "'";
                    PreparedStatement statement = connection.prepareStatement(command);
                    statement.executeUpdate();

                    statement.close();
                    connection.close();
                } catch(SQLException exception){
                    System.out.println("[KrakenMobCoins] Error trying to update to the database!");
                    exception.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

    }

    public void createAccount(String uuid, double amount) {
        PlayerCoins coins = new PlayerCoins(uuid);
        coins.setMoney(amount);
        coins.save(true);

        plugin.getAccounts().put(uuid, coins);
    }

    public void loadAccounts(){
        new BukkitRunnable(){
            @Override
            public void run(){
                try{
                    String command = "SELECT * FROM krakencoins";
                    Connection connection = getNewConnection();
                    PreparedStatement statement = connection.prepareStatement(command);
                    ResultSet rs = statement.executeQuery();
                    while(rs.next()){

                        String uuid = rs.getString("UUID");
                        double money = Double.parseDouble(rs.getString("Coins"));
                        PlayerCoins coins = new PlayerCoins(uuid);
                        coins.setMoney(money);

                        plugin.getAccounts().put(uuid, coins);

                    }

                    rs.close();
                    statement.close();
                    connection.close();

                    plugin.getUtils().sendConsoleMessage("Successfully loaded all saved accounts!");

                } catch (SQLException e) {
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    plugin.getUtils().sendConsoleMessage("&cError loading database player accounts!");
                }
            }
        }.runTaskAsynchronously(plugin);

    }

}
