package me.aglerr.krakenmobcoins.manager;

import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RotatingManager {

    private final List<ShopItems> normalItems = new ArrayList<>();
    private final List<ShopItems> specialItems = new ArrayList<>();

    private int normalTime;
    private int specialTime;

    private final MobCoins plugin;
    public RotatingManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public int getNormalTime(){
        return normalTime;
    }

    public int getSpecialTime(){
        return specialTime;
    }
    public void setNormalTime(int time){
        normalTime = time;
    }
    public void setSpecialTime(int time){
        specialTime = time;
    }

    public int getDefaultNormalTime(){
        return plugin.getConfig().getInt("rotatingShop.normalTimeReset");
    }

    public int getDefaultSpecialTime(){
        return plugin.getConfig().getInt("rotatingShop.specialTimeReset");
    }

    public void clearNormalAndSpecialItems(){
        normalItems.clear();
        specialItems.clear();
    }

    public void saveNormalAndSpecialTime(){

        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();
        if(plugin.getConfig().getBoolean("rotatingShop.enabled")){
            temp.set("normalTimeReset", String.valueOf(normalTime));
            temp.set("specialTimeReset", String.valueOf(specialTime));
            plugin.getTempDataManager().saveData();
        }

    }

    public void refreshNormalItems(){

        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("options.shuffleRotating")){
            Collections.shuffle(normalItems);
            return;
        }

        List<Integer> normalSlots = config.getIntegerList("rotatingShop.normalItemSlots");
        List<ShopItems> removed = new ArrayList<>();

        if(normalItems.size() > normalSlots.size()){
            for(int i = 0; i < normalSlots.size(); i++){
                removed.add(normalItems.get(0));
                normalItems.remove(0);
            }
        }

        if(!removed.isEmpty()) normalItems.addAll(removed);
        removed.clear();

    }

    public void refreshSpecialItems(){

        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("options.shuffleRotating")){
            Collections.shuffle(specialItems);
            return;
        }

        List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");
        List<ShopItems> removed = new ArrayList<>();

        if(specialItems.size() > specialSlots.size()){
            for(int i = 0; i < specialSlots.size(); i++) {
                removed.add(specialItems.get(0));
                specialItems.remove(0);
            }
        }

        if(!removed.isEmpty()) specialItems.addAll(removed);
        removed.clear();

    }

    public void loadNormalAndSpecialTime(){

        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();

        String configNormalTime = temp.getString("normalTimeReset");
        if(configNormalTime == null){
            normalTime = getDefaultNormalTime();
        } else {
            normalTime = Integer.parseInt(configNormalTime);
            temp.set("normalTimeReset", null);
        }

        String configSpecialTime = temp.getString("specialTimeReset");
        if(configSpecialTime == null){
            specialTime = getDefaultSpecialTime();
        } else {
            specialTime = Integer.parseInt(configSpecialTime);
            temp.set("specialTimeReset", null);
        }

        plugin.getTempDataManager().saveData();

    }

    public void saveRewards(){

        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();

        List<String> normal = new ArrayList<>();
        for(ShopItems items : normalItems){
            normal.add(items.getConfigKey());
        }

        List<String> special = new ArrayList<>();
        for(ShopItems items : specialItems){
            special.add(items.getConfigKey());
        }

        temp.set("normalItems", normal);
        temp.set("specialItems", special);

        plugin.getTempDataManager().saveData();

    }

    public void loadRewards(){

        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();

        List<String> normal = temp.getStringList("normalItems");
        if(normal.isEmpty()){
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()) {
                if(!items.isSpecial()) normalItems.add(items);
            }
        } else {
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()){
                for(String key : normal){
                    if(items.getConfigKey().equalsIgnoreCase(key)){
                        normalItems.add(items);
                    }
                }
            }
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()){
                for(String key : normal){
                    if(!items.getConfigKey().equalsIgnoreCase(key) && !items.isSpecial()){
                        normalItems.add(items);
                    }
                }
            }
        }

        List<String> special = temp.getStringList("specialItems");
        if(special.isEmpty()){
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()){
                if(items.isSpecial()) specialItems.add(items);
            }
        } else {
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()){
                for(String key : special){
                    if(items.getConfigKey().equalsIgnoreCase(key)){
                        specialItems.add(items);
                    }
                }
            }
            for(ShopItems items : plugin.getItemsLoader().getShopItemsList()){
                for(String key : special){
                    if(!items.getConfigKey().equalsIgnoreCase(key) && items.isSpecial()){
                        specialItems.add(items);
                    }
                }
            }
        }

        temp.set("normalItems", new ArrayList<>());
        temp.set("specialItems", new ArrayList<>());
        plugin.getTempDataManager().saveData();

    }

    public void startCounting(){

        ItemStockManager stockManager = plugin.getItemStockManager();
        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            normalTime--;
            specialTime--;

            if(normalTime <= 0){

                normalTime = getDefaultNormalTime();
                refreshNormalItems();
                Bukkit.getScheduler().runTask(plugin, this::closeAndOpenShop);

                for(String message : config.getStringList("rotatingShop.messages.normalRefresh")){
                    Bukkit.broadcastMessage(utils.color(message));
                }

                for(ShopItems items : normalItems){
                    stockManager.removeStock(items.getConfigKey());
                }

            }

            if(specialTime <= 0){

                specialTime = getDefaultSpecialTime();
                refreshSpecialItems();
                Bukkit.getScheduler().runTask(plugin, this::closeAndOpenShop);

                for(String message : config.getStringList("rotatingShop.messages.specialRefresh")){
                    Bukkit.broadcastMessage(utils.color(message));
                }

                for(ShopItems items : specialItems){
                    stockManager.removeStock(items.getConfigKey());
                }

            }

        }, 0L, 20L);

    }

    private void closeAndOpenShop(){

        List<Player> savedPlayer = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getOpenInventory().getTopInventory().getHolder() instanceof FastInv){
                player.closeInventory();
                savedPlayer.add(player);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for(Player player : savedPlayer){
                plugin.getUtils().openShopMenu(player);
            }
            savedPlayer.clear();
        }, 2L);

    }

    public String getFormattedResetTime(boolean isSpecial){

        int timeRemaining;
        if(isSpecial){
            timeRemaining = getNormalTime();
        } else {
            timeRemaining = getSpecialTime();
        }

        if (timeRemaining < 60) {
            return timeRemaining + "s";
        }
        int minutes = timeRemaining / 60;
        int s = 60 * minutes;
        int secondsLeft = timeRemaining - s;
        if (minutes < 60) {
            if (secondsLeft > 0) {
                return minutes + "m" + " " + secondsLeft + "s";
            }
            return minutes + "m";
        }
        if (minutes < 1440) {
            String time;
            int hours = minutes / 60;
            time = hours + "h";
            int inMins = 60 * hours;
            int leftOver = minutes - inMins;
            if (leftOver >= 1) {
                time = time + " " + leftOver + "m";
            } else {
                time = time + " " + "0m";
            }
            return time;
        }
        String time;
        int days = minutes / 1440;
        time = days + "d";
        int inMins = 1440 * days;
        int leftOver = minutes - inMins;
        if (leftOver >= 1) {
            int hours = leftOver / 60;
            time = time + " " + hours + "h";
        } else {
            time = time + " 0h";
        }

        return time;

    }

    public List<ShopItems> getNormalItems() { return normalItems; }
    public List<ShopItems> getSpecialItems() { return specialItems; }

}
