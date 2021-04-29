package me.aglerr.krakenmobcoins.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PurchaseLimitManager {

    private final Table<UUID, String, Integer> table = HashBasedTable.create();

    private final MobCoins plugin;
    public PurchaseLimitManager(final MobCoins plugin){
        this.plugin = plugin;
    }

    public boolean isPlayerExistOnTable(UUID uuid){
        return table.containsRow(uuid);
    }

    public int getLimit(UUID uuid, String key){
        return table.get(uuid, key);
    }

    public void modifyLimit(UUID uuid, String key, int amount){
        table.put(uuid, key, amount);
    }

    public void incrementLimit(UUID uuid, String key){
        table.put(uuid, key, getLimit(uuid, key) + 1);
    }

    public void resetLimit(){
        table.clear();
    }

    public boolean containsLimit(UUID uuid, String key){
        return table.contains(uuid, key);
    }

    public void clearSpecificItem(String key){
        for(UUID uuid : table.rowKeySet()){
            if(table.containsColumn(key)){
                table.remove(uuid, key);
            }
        }
    }

    public void saveLimit(){

        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();
        for(UUID uuid : table.rowKeySet()){
            for(String key : table.columnKeySet()){
                int limit = getLimit(uuid, key);
                temp.set("purchaseLimit." + uuid + "." + key, limit);
            }
        }

        plugin.getTempDataManager().saveData();

    }

    public void loadLimit(){
        FileConfiguration temp = plugin.getTempDataManager().getConfiguration();
        if(!temp.isConfigurationSection("purchaseLimit")) return;

        for(String uuid : temp.getConfigurationSection("purchaseLimit").getKeys(false)){
            for(String key : temp.getConfigurationSection("purchaseLimit." + uuid).getKeys(false)){
                int limit = temp.getInt("purchaseLimit." + uuid + "." + key);
                table.put(UUID.fromString(uuid), key, limit);
            }
        }

        temp.set("purchaseLimit", null);
        plugin.getTempDataManager().saveData();

    }

}
