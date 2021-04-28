package me.aglerr.krakenmobcoins;

import fr.mrmicky.fastinv.FastInvManager;
import me.aglerr.krakenmobcoins.api.MobCoinsAPI;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.commands.MainCommand;
import me.aglerr.krakenmobcoins.configs.*;
import me.aglerr.krakenmobcoins.database.SQL;
import me.aglerr.krakenmobcoins.listeners.*;
import me.aglerr.krakenmobcoins.manager.*;
import me.aglerr.krakenmobcoins.shops.ShopUtils;
import me.aglerr.krakenmobcoins.shops.loader.ItemsLoader;
import me.aglerr.krakenmobcoins.utils.ConfigUpdater;
import me.aglerr.krakenmobcoins.utils.Metrics;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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

    private SQL database;

    private final Set<UUID> mobSpawner = new HashSet<>();

    private final List<EntityDamageEvent.DamageCause> damageCauses = new ArrayList<>();

    private final TempDataConfig tempDataConfig = new TempDataConfig(this);
    private final MobsConfig mobsConfig = new MobsConfig(this);
    private final ShopConfig shopConfig = new ShopConfig(this);

    private final ItemsLoader itemsLoader = new ItemsLoader(this);

    private final ShopUtils shopUtils = new ShopUtils(this);
    private final Utils utils = new Utils(this);
    private final CoinMobManager coinMobManager = new CoinMobManager(this);
    private final SalaryManager salaryManager = new SalaryManager(this);
    private final DependencyManager dependencyManager = new DependencyManager(this);
    private final ItemStockManager itemStockManager = new ItemStockManager(this);
    private final ToggleNotificationManager notificationManager = new ToggleNotificationManager(this);
    private final RotatingManager rotatingManager = new RotatingManager(this);
    private final CategoryManager categoryManager = new CategoryManager(this);
    private final AccountManager accountManager = new AccountManager(this);
    private final PurchaseLimitManager limitManager = new PurchaseLimitManager(this);

    private static MobCoinsAPI api;

    @Override
    public void onEnable() {
        api = new MobCoinsAPI(this);

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

        accountManager.startAutoSaveTask();
        new Metrics(this, 10310);
        FastInvManager.register(this);

        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            rotatingManager.loadNormalAndSpecialTime();
            itemsLoader.loadRotatingItems();
            itemsLoader.loadShopItems();
            rotatingManager.loadRewards();
            Bukkit.getScheduler().runTask(this, rotatingManager::startCounting);
        } else {
            categoryManager.loadCategory();
            itemsLoader.loadMainMenu();
            itemsLoader.loadShopNormal();
        }

        salaryManager.beginSalaryTask();
        coinMobManager.loadCoinMob();
        loadDamageCausePhysical();
        itemStockManager.loadStockFromConfig();

        ConfigMessages.initialize(this.getConfig());
        ConfigMessagesList.initialize(this.getConfig());

        limitManager.loadLimit();

    }

    @Override
    public void onDisable(){
        utils.sendConsoleMessage("Saving all player data...");
        notificationManager.saveToggledListToConfig();
        rotatingManager.saveNormalAndSpecialTime();
        accountManager.saveAllPlayerData();
        itemStockManager.saveStockToConfig();
        rotatingManager.saveRewards();
        limitManager.saveLimit();
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
    }

    public void reloadConfigs(){
        this.reloadConfig();
        mobsConfig.reloadData();
        shopConfig.reloadData();

        rotatingManager.saveRewards();
        itemsLoader.clearAllItemsList();
        categoryManager.clearCategory();
        rotatingManager.clearNormalAndSpecialItems();
        damageCauses.clear();
        coinMobManager.clearCoinMob();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

            itemsLoader.loadAllItemsList();

            rotatingManager.loadRewards();
            categoryManager.loadCategory();
            coinMobManager.loadCoinMob();
            loadDamageCausePhysical();

            ConfigMessages.initialize(this.getConfig());
            ConfigMessagesList.initialize(this.getConfig());

        }, 3L);

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
        pm.registerEvents(new PlayerListener(this), this);
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

    public static MobCoinsAPI getAPI() { return api; }
    public Utils getUtils() { return utils; }
    public SQL getDatabase() { return database; }
    public TempDataConfig getTempDataManager() { return tempDataConfig; }
    public ShopConfig getShopManager() { return shopConfig; }
    public Set<UUID> getMobSpawner() { return mobSpawner; }
    public ShopUtils getShopUtils() { return shopUtils; }
    public List<EntityDamageEvent.DamageCause> getDamageCauses() { return damageCauses; }
    public CoinMobManager getCoinMobManager() { return coinMobManager; }
    public SalaryManager getSalaryManager() { return salaryManager; }
    public DependencyManager getDependencyManager() { return dependencyManager; }
    public MobsConfig getMobsManager() { return mobsConfig; }
    public ItemStockManager getItemStockManager() { return itemStockManager; }
    public ToggleNotificationManager getNotificationManager() { return notificationManager; }
    public CategoryManager getCategoryManager() { return categoryManager; }
    public AccountManager getAccountManager() { return accountManager; }
    public ItemsLoader getItemsLoader() { return itemsLoader; }
    public RotatingManager getRotatingManager() { return rotatingManager; }
    public PurchaseLimitManager getPurchaseLimitManager() { return limitManager; }
}
