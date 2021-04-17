package me.aglerr.krakenmobcoins;

import fr.mrmicky.fastinv.FastInvManager;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.commands.MainCommand;
import me.aglerr.krakenmobcoins.configs.*;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.database.SQL;
import me.aglerr.krakenmobcoins.listeners.*;
import me.aglerr.krakenmobcoins.manager.*;
import me.aglerr.krakenmobcoins.shops.ShopUtils;
import me.aglerr.krakenmobcoins.shops.category.mainmenu.MainMenuLoader;
import me.aglerr.krakenmobcoins.shops.category.shops.ShopNormalLoader;
import me.aglerr.krakenmobcoins.shops.items.RotatingLoader;
import me.aglerr.krakenmobcoins.shops.items.ShopItems;
import me.aglerr.krakenmobcoins.shops.items.ShopItemsLoader;
import me.aglerr.krakenmobcoins.utils.ConfigUpdater;
import me.aglerr.krakenmobcoins.utils.Metrics;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class MobCoins extends JavaPlugin {

    /**
     * Version 2.1
     * Main goal is to remove singletons usage and switching it
     * into dependency injection and also make the code more object oriented
     *
     * TODO: LastRewardManager.class
     * TODO: Fixing database bugs
     *
     */

    private static MobCoins instance;
    private SQL database;

    private final Set<Entity> mobSpawner = new HashSet<>();

    private final List<EntityDamageEvent.DamageCause> damageCauses = new ArrayList<>();

    public List<ShopItems> normalItems = new ArrayList<>();
    public List<ShopItems> specialItems = new ArrayList<>();

    private final TempDataConfig tempDataConfig = new TempDataConfig(this);
    private final MobsConfig mobsConfig = new MobsConfig(this);
    private final ShopConfig shopConfig = new ShopConfig(this);
    private final LimitConfig limitConfig = new LimitConfig(this);

    private final ShopItemsLoader shopItemsLoader = new ShopItemsLoader(this);
    private final MainMenuLoader mainMenuLoader = new MainMenuLoader(this);
    private final ShopNormalLoader shopNormalLoader = new ShopNormalLoader(this);
    private final RotatingLoader rotatingLoader = new RotatingLoader(this);

    private final ShopUtils shopUtils = new ShopUtils(this);
    private final Utils utils = new Utils(this);
    private final CoinMobManager coinMobManager = new CoinMobManager(this);
    private final SalaryManager salaryManager = new SalaryManager(this);
    private final DependencyManager dependencyManager = new DependencyManager(this);
    private final ItemStockManager itemStockManager = new ItemStockManager(this);
    private final ToggleNotificationManager notificationManager = new ToggleNotificationManager(this);
    private final RotatingShopTimeManager timeManager = new RotatingShopTimeManager(this);
    private final CategoryManager categoryManager = new CategoryManager(this);
    private final AccountManager accountManager = new AccountManager(this);

    @Override
    public void onEnable() {
        instance = this;

        createDatabaseFile();
        File categoriesFolder = new File("plugins/KrakenMobcoins/categories");
        if(!categoriesFolder.exists()){
            categoriesFolder.mkdirs();
        }

        registerConfigs();
        updateConfigs();

        dependencyManager.setupDependency();
        registerListeners();
        registerCommands();

        database = new SQL(this);

        utils.sendConsoleMessage("Loading all saved accounts!");
        accountManager.loadAllPlayerData();
        notificationManager.loadToggledListFromConfig();
        timeManager.loadTimeFromConfig();

        this.runAutoSave();
        new Metrics(this, 10310);
        FastInvManager.register(this);

        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            rotatingLoader.load();
            shopItemsLoader.load();
            timeManager.startCounting();
            this.loadRewards();
        } else {
            categoryManager.loadCategory();
            mainMenuLoader.load();
            shopNormalLoader.load();
        }

        salaryManager.beginSalaryTask();
        coinMobManager.loadCoinMob();
        loadDamageCausePhysical();
        itemStockManager.loadStockFromConfig();

        ConfigMessages.initialize(this.getConfig());
        ConfigMessagesList.initialize(this.getConfig());

    }

    @Override
    public void onDisable(){
        utils.sendConsoleMessage("Saving all player data...");
        notificationManager.saveToggledListToConfig();
        timeManager.saveTimeToConfig();
        accountManager.saveAllPlayerData();
        itemStockManager.saveStockToConfig();
        this.saveLastRewards();
        this.clearHash();
    }

    private void saveLastRewards(){
        FileConfiguration data = tempDataConfig.getConfiguration();
        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            if(!this.normalItems.isEmpty()){
                for(ShopItems items : this.normalItems){
                    data.set("normalItems." + items.getConfigKey() + ".material", items.getMaterial());
                    data.set("normalItems." + items.getConfigKey() + ".name", items.getName());
                    data.set("normalItems." + items.getConfigKey() + ".amount", items.getAmount());
                    data.set("normalItems." + items.getConfigKey() + ".glowing", items.isGlowing());
                    data.set("normalItems." + items.getConfigKey() + ".price", items.getPrice());
                    data.set("normalItems." + items.getConfigKey() + ".special", items.isSpecial());
                    data.set("normalItems." + items.getConfigKey() + ".limit", items.getLimit());
                    data.set("normalItems." + items.getConfigKey() + ".lore", items.getLore());
                    data.set("normalItems." + items.getConfigKey() + ".commands", items.getCommands());
                    data.set("normalItems." + items.getConfigKey() + ".useStock", items.isUseStock());
                    data.set("normalItems." + items.getConfigKey() + ".stock", items.getStock());
                }
            }

            if(!this.specialItems.isEmpty()){
                for(ShopItems items1 : this.specialItems){
                    data.set("specialItems." + items1.getConfigKey() + ".material", items1.getMaterial());
                    data.set("specialItems." + items1.getConfigKey() + ".name", items1.getName());
                    data.set("specialItems." + items1.getConfigKey() + ".amount", items1.getAmount());
                    data.set("specialItems." + items1.getConfigKey() + ".glowing", items1.isGlowing());
                    data.set("specialItems." + items1.getConfigKey() + ".price", items1.getPrice());
                    data.set("specialItems." + items1.getConfigKey() + ".special", items1.isSpecial());
                    data.set("specialItems." + items1.getConfigKey() + ".limit", items1.getLimit());
                    data.set("specialItems." + items1.getConfigKey() + ".lore", items1.getLore());
                    data.set("specialItems." + items1.getConfigKey() + ".commands", items1.getCommands());
                    data.set("specialItems." + items1.getConfigKey() + ".useStock", items1.isUseStock());
                    data.set("specialItems." + items1.getConfigKey() + ".stock", items1.getStock());
                }
            }

            tempDataConfig.saveData();
        }

    }

    private void loadRewards(){
        FileConfiguration data = tempDataConfig.getConfiguration();
        FileConfiguration shop = shopConfig.getConfiguration();

        if(data.isConfigurationSection("normalItems")){
            for(String key : data.getConfigurationSection("normalItems").getKeys(false)){
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

                normalItems.add(new ShopItems(key, material, amount, name, glowing, lore, commands,
                        price, special, limit, useStock, stock));

            }

            List<String> normalItemsList = new ArrayList<>();
            for(ShopItems normal : normalItems){
                normalItemsList.add(normal.getConfigKey());
            }

            for(ShopItems items : shopItemsLoader.getShopItemsList()){
                if(!normalItemsList.contains(items.getConfigKey())){
                    if(!items.isSpecial()){
                        normalItems.add(new ShopItems(items.getConfigKey(), items.getMaterial(), items.getAmount(),
                                items.getName(), items.isGlowing(), items.getLore(), items.getCommands(),
                                items.getPrice(), items.isSpecial(), items.getLimit(), items.isUseStock(),
                                items.getStock()));
                    }

                }
            }

            data.set("normalItems", null);
            tempDataConfig.saveData();

        } else {
            for(ShopItems items : shopItemsLoader.getShopItemsList()){
                if(!items.isSpecial()){
                    normalItems.add(items);
                }
            }
        }

        if(data.isConfigurationSection("specialItems")){
            for(String key : data.getConfigurationSection("specialItems").getKeys(false)){
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

                specialItems.add(new ShopItems(key, material, amount, name, glowing, lore, commands,
                        price, special, limit, useStock, stock));
            }

            List<String> specialItemsList = new ArrayList<>();
            for(ShopItems special : specialItems){
                specialItemsList.add(special.getConfigKey());
            }

            for(ShopItems items : shopItemsLoader.getShopItemsList()){
                if(!specialItemsList.contains(items.getConfigKey())){
                    if(items.isSpecial()){
                        specialItems.add(new ShopItems(items.getConfigKey(), items.getMaterial(), items.getAmount(),
                                items.getName(), items.isGlowing(), items.getLore(), items.getCommands(),
                                items.getPrice(), items.isSpecial(), items.getLimit(), items.isUseStock(),
                                items.getStock()));
                    }
                }
            }

            data.set("specialItems", null);
            tempDataConfig.saveData();

        } else {
            for(ShopItems items : shopItemsLoader.getShopItemsList()){
                if(items.isSpecial()){
                    specialItems.add(items);
                }
            }
        }
    }

    private void createDatabaseFile(){
        File pluginFolder = new File("plugins/KrakenMobcoins");
        if(!pluginFolder.exists()){
            pluginFolder.mkdirs();
        }

        File dataFolder = new File(this.getDataFolder(), "database.db");
        if(!dataFolder.exists()){
            try{
                dataFolder.createNewFile();
                utils.sendConsoleMessage("Successfully creating database file.");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void registerConfigs(){
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        tempDataConfig.setup();
        mobsConfig.setup();
        shopConfig.setup();
        limitConfig.setup();
    }

    public void reloadConfigs(){
        this.reloadConfig();
        mobsConfig.reloadData();
        shopConfig.reloadData();
        limitConfig.reloadData();

        saveLastRewards();
        shopItemsLoader.getShopItemsList().clear();
        mainMenuLoader.getMainMenuItemsList().clear();
        shopNormalLoader.getShopNormalItemsList().clear();
        categoryManager.clearCategory();
        rotatingLoader.getRotatingItemsList().clear();
        normalItems.clear();
        specialItems.clear();
        damageCauses.clear();
        coinMobManager.loadCoinMob();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

            shopItemsLoader.load();
            mainMenuLoader.load();
            shopNormalLoader.load();
            rotatingLoader.load();

            loadRewards();
            categoryManager.loadCategory();
            loadDamageCausePhysical();

            ConfigMessages.initialize(this.getConfig());
            ConfigMessagesList.initialize(this.getConfig());

        }, 1L);

    }

    private void updateConfigs(){
        File configFile = new File(this.getDataFolder(), "config.yml");
        List<String> listConfig = Arrays.asList("normalShop.items", "rotatingShop.items");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, listConfig);
        }catch(IOException e){
            e.printStackTrace();
        }

        this.reloadConfig();

    }

    private void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerLeave(this), this);
        pm.registerEvents(new PlayerInteract(this), this);
        pm.registerEvents(new EntityDeath(this), this);
        pm.registerEvents(new EntityDeathPhysical(this), this);
        pm.registerEvents(new CreatureSpawn(this), this);
        if(dependencyManager.isMythicMobs()){
            pm.registerEvents(new MythicMobDeath(this), this);
            pm.registerEvents(new MythicMobDeathPhysical(this), this);
        }
    }

    private void registerCommands(){
        this.getCommand("mobcoins").setExecutor(new MainCommand(this));
    }

    private void runAutoSave(){
        if(this.getConfig().getBoolean("autoSave.enabled")){
            int interval = this.getConfig().getInt("autoSave.interval");
            Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                    accountManager::saveAllPlayerData, 0L, 20 * interval);
        }
    }

    private void loadDamageCausePhysical(){
        for(String string : this.getConfig().getStringList("options.physicalMobCoin.deathCause")){
            try{
                damageCauses.add(EntityDamageEvent.DamageCause.valueOf(string));
            } catch (IllegalArgumentException exception) {
                utils.sendConsoleMessage("Damage Cause with name '{string}' is invalid!".replace("{string}", string));
                exception.printStackTrace();
            }
        }
    }

    private void clearHash(){
        shopItemsLoader.getShopItemsList().clear();
        mainMenuLoader.getMainMenuItemsList().clear();
        shopNormalLoader.getShopNormalItemsList().clear();
        mobSpawner.clear();
        damageCauses.clear();
    }

    public static MobCoins getInstance() { return instance; }
    public Utils getUtils() { return utils; }
    public SQL getDatabase() { return database; }
    public TempDataConfig getTempDataManager() { return tempDataConfig; }
    public ShopConfig getShopManager() { return shopConfig; }
    public List<ShopItems> getNormalItems() { return normalItems; }
    public List<ShopItems> getSpecialItems() { return specialItems; }
    public LimitConfig getLimitManager() { return limitConfig; }
    public MainMenuLoader getMainMenuLoader() { return mainMenuLoader; }
    public ShopNormalLoader getShopNormalLoader() { return shopNormalLoader; }
    public RotatingLoader getRotatingLoader() { return rotatingLoader; }
    public Set<Entity> getMobSpawner() { return mobSpawner; }
    public ShopUtils getShopUtils() { return shopUtils; }
    public List<EntityDamageEvent.DamageCause> getDamageCauses() { return damageCauses; }
    public CoinMobManager getCoinMobManager() { return coinMobManager; }
    public SalaryManager getSalaryManager() { return salaryManager; }
    public DependencyManager getDependencyManager() { return dependencyManager; }
    public MobsConfig getMobsManager() { return mobsConfig; }
    public ItemStockManager getItemStockManager() { return itemStockManager; }
    public ToggleNotificationManager getNotificationManager() { return notificationManager; }
    public RotatingShopTimeManager getTimeManager() { return timeManager; }
    public CategoryManager getCategoryManager() { return categoryManager; }
    public AccountManager getAccountManager() { return accountManager; }

}
