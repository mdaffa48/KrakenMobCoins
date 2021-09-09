package me.aglerr.krakenmobcoins.manager;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.database.SQL;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AccountManager {

    private final Map<String, PlayerCoins> playerCoins = new HashMap<>();

    private final MobCoins plugin;
    public AccountManager(final MobCoins plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public PlayerCoins getPlayerData(String uuid) {
        return playerCoins.get(uuid);
    }

    public void createPlayerData(@NotNull String uuid, double coinAmount) {
        PlayerCoins coins = new PlayerCoins(uuid);
        coins.setMoney(coinAmount);

        CompletableFuture.runAsync(() -> savePlayerData(coins)).thenAccept(result ->
                Bukkit.getScheduler().runTask(plugin, () -> playerCoins.put(uuid, coins)));

    }

    public void loadPlayerData(String uuid) {
        SQL sql = plugin.getDatabase();
        Utils utils = plugin.getUtils();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {

                Connection connection = sql.getNewConnection();
                String command = "SELECT Coins FROM krakencoins WHERE UUID='" + uuid + "'";
                PreparedStatement statement = connection.prepareStatement(command);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    PlayerCoins playerCoins = getPlayerData(uuid);
                    double money = Double.parseDouble(rs.getString("Coins"));
                    if(playerCoins == null){
                        PlayerCoins coins = new PlayerCoins(uuid);
                        coins.setMoney(money);
                        this.playerCoins.put(uuid, coins);
                    } else {
                        playerCoins.setMoney(money);
                    }

                } else {

                    double starting = plugin.getConfig().getDouble("options.startingBalance");
                    createPlayerData(uuid, starting);
                }

                rs.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                utils.sendConsoleMessage("Failed to load " + uuid + " data.");
                e.printStackTrace();
            }
        });


    }

    public void savePlayerData(PlayerCoins coins) {
        SQL sql = plugin.getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = sql.getNewConnection();
                String command = "SELECT Coins FROM krakencoins WHERE UUID='" + coins.getUUID() + "'";
                PreparedStatement statement = connection.prepareStatement(command);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    sql.update(connection, coins.getUUID(), String.valueOf(coins.getMoney()));
                } else {
                    sql.insert(connection, coins.getUUID(), String.valueOf(coins.getMoney()));
                }

                rs.close();
                statement.close();
                connection.close();
            } catch (SQLException exception) {
                plugin.getLogger().severe("Error saving player account data.");
                exception.printStackTrace();
            }
        });

    }

    public void loadAllPlayerData() {
        SQL sql = plugin.getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                String command = "SELECT * FROM krakencoins";
                Connection connection = sql.getNewConnection();
                PreparedStatement statement = connection.prepareStatement(command);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {

                    String uuid = rs.getString("UUID");
                    String money = rs.getString("Coins");

                    PlayerCoins coins = new PlayerCoins(uuid);
                    coins.setMoney(Double.parseDouble(money));

                    this.playerCoins.put(uuid, coins);

                }

                rs.close();
                statement.close();
                connection.close();

                plugin.getUtils().sendConsoleMessage("Successfully loaded all saved accounts!");

            } catch (SQLException e) {
                Bukkit.getPluginManager().disablePlugin(plugin);
                plugin.getUtils().sendConsoleMessage("Error loading database player accounts!");
            }

        });
    }

    public void saveAllPlayerData() {
        SQL sql = plugin.getDatabase();
        try {

            Connection connection = sql.getNewConnection();
            for (String uuid : playerCoins.keySet()) {
                PlayerCoins playerCoins = getPlayerData(uuid);
                String command = "UPDATE krakencoins SET Coins='" + playerCoins.getMoney() + "' WHERE UUID='" + playerCoins.getUUID() + "'";
                PreparedStatement statement = connection.prepareStatement(command);
                statement.executeUpdate();
            }

            connection.close();
            plugin.getUtils().sendConsoleMessage("Successfully saved all player data!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerCoins> getTop() {

        List<PlayerCoins> coins = new ArrayList<>();
        for (String a : playerCoins.keySet()) {
            coins.add(getPlayerData(a));
        }

        List<PlayerCoins> convert = new ArrayList<>(coins);

        convert.sort((pt1, pt2) -> {

            Float f1 = (float) pt1.getMoney();
            Float f2 = (float) pt2.getMoney();

            return f2.compareTo(f1);

        });
        if (convert.size() > 10) {
            convert = convert.subList(0, 10);
        }
        return convert;

    }

    public void startAutoSaveTask(){
        if(plugin.getConfig().getBoolean("autoSave.enabled")){
            int interval = plugin.getConfig().getInt("autoSave.interval");
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                    this::saveAllPlayerData, 0L, 20 * interval);
        }
    }

}
