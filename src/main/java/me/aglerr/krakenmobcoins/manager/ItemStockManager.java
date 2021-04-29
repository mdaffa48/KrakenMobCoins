package me.aglerr.krakenmobcoins.manager;

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ItemStockManager {

    private final Map<String, Integer> stock = new HashMap<>();

    private final MobCoins plugin;
    public ItemStockManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public void clearStock(){
        stock.clear();
    }

    public void setStock(String item, int amount){
        stock.put(item, amount);
    }

    public void removeStock(String item){
        stock.remove(item);
    }

    public int getItemStock(String item){
        return stock.get(item);
    }

    public boolean isItemExist(String item){
        return stock.containsKey(item);
    }

    public void saveStockToConfig(){

        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        for(String key : stock.keySet()){
            config.set("stock." + key, stock.get(key));
        }

        plugin.getTempDataManager().saveData();

    }

    public void loadStockFromConfig(){
        FileConfiguration config = plugin.getTempDataManager().getConfiguration();
        if(!config.isConfigurationSection("stock")) return;

        for(String key : config.getConfigurationSection("stock").getKeys(false)){
            setStock(key, config.getInt("stock." + key));
        }

        config.set("stock", null);
        plugin.getTempDataManager().saveData();

    }

}
