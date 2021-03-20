package me.aglerr.krakenmobcoins.shops.category.shops;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopNormalLoader {

    public FileConfiguration config;
    private List<ShopNormalItems> shopNormalItemsList = new ArrayList<>();

    public void load(){
        File[] files = new File(MobCoins.getInstance().getDataFolder() + File.separator + "categories").listFiles();
        if(files.length > 0){
            for(File file : files){
                config = YamlConfiguration.loadConfiguration(file);
                String fileName = file.getName();
                for(String key : config.getConfigurationSection("items").getKeys(false)){

                    String type = config.getString("items." + key + ".type");
                    String material = config.getString("items." + key + ".material");
                    int amount = config.getInt("items." + key + ".amount");
                    String name = config.getString("items." + key + ".name");
                    boolean glow = config.getBoolean("items." + key + ".glow");
                    List<String> lore = config.getStringList("items." + key + ".lore");
                    List<String> commands = config.getStringList("items." + key + ".commands");
                    int slot = config.getInt("items." + key + ".slot");
                    List<Integer> slots = config.getIntegerList("items." + key + ".slots");
                    double price = config.getDouble("items." + key + ".price");
                    int limit = config.getInt("items." + key + ".limit");
                    boolean useStock = config.getBoolean("items." + key + ".useStock");
                    int stock = config.getInt("items." + key + ".stock");

                    shopNormalItemsList.add(new ShopNormalItems(fileName, key, type, material, amount, name, glow, lore, commands, slot, slots, price, limit, useStock, stock));

                }
            }
        } else {

            Utils utils = MobCoins.getInstance().getUtils();
            System.out.println("[KrakenMobCoins] Couldn't find any category, creating one...");

            File file = new File(MobCoins.getInstance().getDataFolder() + File.separator + "categories", "crates.yml");
            utils.exampleShop(file);

        }
    }

    public List<ShopNormalItems> getShopNormalItemsList() { return shopNormalItemsList; }

}
