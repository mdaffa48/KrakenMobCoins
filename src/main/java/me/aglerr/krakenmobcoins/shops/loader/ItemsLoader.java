package me.aglerr.krakenmobcoins.shops.loader;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.shops.items.MainMenuItems;
import me.aglerr.krakenmobcoins.shops.items.RotatingItems;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.shops.items.ShopNormalItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemsLoader {

    private final MobCoins plugin;
    public ItemsLoader(final MobCoins plugin){
        this.plugin = plugin;
    }

    private final List<MainMenuItems> mainMenuItemsList = new ArrayList<>();
    private final List<RotatingItems> rotatingItemsList = new ArrayList<>();
    private final List<ShopItems> shopItemsList = new ArrayList<>();
    private final List<ShopNormalItems> shopNormalItemsList = new ArrayList<>();

    public void clearAllItemsList(){
        mainMenuItemsList.clear();
        rotatingItemsList.clear();
        shopItemsList.clear();
        shopNormalItemsList.clear();
    }

    public void loadAllItemsList(){
        loadMainMenu();
        loadRotatingItems();
        loadShopItems();
        loadShopNormal();
    }

    public void loadMainMenu(){
        FileConfiguration config = plugin.getConfig();
        for(String key : config.getConfigurationSection("normalShop.items").getKeys(false)){

            String type = config.getString("normalShop.items." + key + ".type");
            String material = config.getString("normalShop.items." + key + ".material");
            String name = config.getString("normalShop.items." + key + ".name");
            int slot = config.getInt("normalShop.items." + key + ".slot");
            List<Integer> slots = config.getIntegerList("normalShop.items." + key + ".slots");
            List<String> lore = config.getStringList("normalShop.items." + key + ".lore");
            boolean glow = config.getBoolean("normalShop.items." + key + ".glow");
            String category = config.getString("normalShop.items." + key + ".category");

            mainMenuItemsList.add(new MainMenuItems(type, material, name, slot, slots, lore, glow, category));

        }
    }

    public void loadRotatingItems(){
        FileConfiguration config = plugin.getConfig();
        for(String key : config.getConfigurationSection("rotatingShop.items").getKeys(false)){

            String material = config.getString("rotatingShop.items." + key + ".material");
            String name = config.getString("rotatingShop.items." + key + ".name");
            int slot = config.getInt("rotatingShop.items." + key + ".slot");
            List<Integer> slots = config.getIntegerList("rotatingShop.items." + key + ".slots");
            List<String> lore = config.getStringList("rotatingShop.items." + key + ".lore");
            boolean glow = config.getBoolean("rotatingShop.items." + key + ".glow");


            rotatingItemsList.add(new RotatingItems(material, name, slot, slots, lore, glow));
        }
    }

    public void loadShopItems(){
        FileConfiguration shop = plugin.getShopManager().getConfiguration();
        for(String key : shop.getConfigurationSection("shops").getKeys(false)){

            String material = shop.getString("shops." + key + ".material");
            int amount = shop.getInt("shops." + key + ".amount");
            String name = shop.getString("shops." + key + ".name");
            boolean glowing = shop.getBoolean("shops." + key + ".glowing");
            List<String> lore = shop.getStringList("shops." + key + ".lore");
            List<String> commands = shop.getStringList("shops." + key + ".commands");
            double price = shop.getDouble("shops." + key + ".price");
            boolean special = shop.getBoolean("shops." + key + ".special");
            int limit = shop.getInt("shops." + key + ".limit");
            boolean useStock = shop.getBoolean("shops." + key + ".useStock");
            int stock = shop.getInt("shops." + key + ".stock");

            shopItemsList.add(new ShopItems(key, material, amount, name, glowing, lore, commands, price, special, limit, useStock, stock));

        }
    }

    public void loadShopNormal(){

        File[] files = new File(plugin.getDataFolder() + File.separator + "categories").listFiles();
        FileConfiguration config;

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

            Utils utils = plugin.getUtils();
            System.out.println("[KrakenMobCoins] Couldn't find any category, creating one...");

            File file = new File(plugin.getDataFolder() + File.separator + "categories", "crates.yml");
            utils.exampleShop(file);
            plugin.getCategoryManager().addCategory(file.getName(), YamlConfiguration.loadConfiguration(file));

        }
    }

    public List<MainMenuItems> getMainMenuItemsList() { return mainMenuItemsList; }
    public List<RotatingItems> getRotatingItemsList() { return rotatingItemsList; }
    public List<ShopItems> getShopItemsList() { return shopItemsList; }
    public List<ShopNormalItems> getShopNormalItemsList() { return shopNormalItemsList; }


}
