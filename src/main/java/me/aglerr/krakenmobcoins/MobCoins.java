package me.aglerr.krakenmobcoins;

import fr.mrmicky.fastinv.FastInvManager;
import me.aglerr.krakenmobcoins.api.MobCoinsExpansion;
import me.aglerr.krakenmobcoins.coinmob.CoinMob;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.commands.MainCommand;
import me.aglerr.krakenmobcoins.configs.*;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.database.SQL;
import me.aglerr.krakenmobcoins.listeners.*;
import me.aglerr.krakenmobcoins.salary.SalaryManager;
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
import org.bukkit.entity.Player;
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

    private static MobCoins instance;
    private SQL database;

    public Map<String, PlayerCoins> accounts = new HashMap<>();

    private final Set<String> toggled = new HashSet<>();
    private final Map<String, FileConfiguration> categories = new HashMap<>();
    private final Set<Entity> mobSpawner = new HashSet<>();

    private final Map<String, Integer> limit = new HashMap<>();
    private final Map<String, Integer> stock = new HashMap<>();

    private final List<EntityDamageEvent.DamageCause> damageCauses = new ArrayList<>();

    public List<ShopItems> normalItems = new ArrayList<>();
    public List<ShopItems> specialItems = new ArrayList<>();

    public TempDataManager tempDataManager = new TempDataManager();
    public MobsManager mobsManager = new MobsManager();
    public ShopManager shopManager = new ShopManager();
    public LimitManager limitManager = new LimitManager();

    public ShopItemsLoader shopItemsLoader = new ShopItemsLoader();
    public MainMenuLoader mainMenuLoader = new MainMenuLoader();
    public ShopNormalLoader shopNormalLoader = new ShopNormalLoader();
    public RotatingLoader rotatingLoader = new RotatingLoader();

    public ShopUtils shopUtils = new ShopUtils();

    private final Utils utils = new Utils();
    private final CoinMobManager coinMobManager = new CoinMobManager(this);
    private final SalaryManager salaryManager = new SalaryManager(this);

    public long normalTime;
    public long specialTime;

    public static boolean superMobCoinsHook = false;
    public static boolean mythicMobsHook = false;
    public static boolean wildStackerHook = false;

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

        registerHooks();
        registerListeners();
        registerCommands();

        database = new SQL();

        utils.sendConsoleMessage("Loading all saved accounts!");
        database.loadAccounts();
        this.loadPlayerToggled();

        this.runAutoSave();
        new Metrics(this, 10310);
        FastInvManager.register(this);

        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            rotatingLoader.load();
            shopItemsLoader.load();
            this.startCounting();
            this.loadRewards();
        } else {
            loadCategories();
            mainMenuLoader.load();
            shopNormalLoader.load();
        }

        salaryManager.beginSalaryTask();
        loadStocks();
        coinMobManager.loadCoinMob();
        loadDamageCausePhysical();

        ConfigMessages.initialize(this.getConfig());
        ConfigMessagesList.initialize(this.getConfig());

    }

    @Override
    public void onDisable(){
        utils.sendConsoleMessage("Saving all player data...");
        this.savePlayerToggled();
        this.savePlayerData();
        this.saveLastRewards();
        this.saveStocks();
        this.clearHash();
    }

    private void saveStocks(){
        FileConfiguration purchase = limitManager.getConfiguration();
        for(String key : stock.keySet()){
            purchase.set("stock." + key, stock.get(key));
        }

        limitManager.saveData();
        stock.clear();
    }

    private void loadStocks(){
        FileConfiguration purchase = limitManager.getConfiguration();
        if(purchase.isConfigurationSection("stock")){
            for(String key : purchase.getConfigurationSection("stock").getKeys(false)){
                stock.put(key, purchase.getInt("stock." + key));
            }
            purchase.set("stock", null);
            limitManager.saveData();
        }
    }

    private void loadCategories(){
        FileConfiguration category;
        File[] files = new File(this.getDataFolder() + File.separator + "categories").listFiles();
        if(files.length > 0){
            for(File file : files){
                category = YamlConfiguration.loadConfiguration(file);
                categories.put(file.getName(), category);
            }
        }

    }

    private void saveLastRewards(){
        FileConfiguration data = tempDataManager.getConfiguration();
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

            tempDataManager.saveData();
        }

    }

    private void loadRewards(){
        FileConfiguration data = tempDataManager.getConfiguration();
        FileConfiguration shop = shopManager.getConfiguration();

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
            tempDataManager.saveData();

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
            tempDataManager.saveData();

        } else {
            for(ShopItems items : shopItemsLoader.getShopItemsList()){
                if(items.isSpecial()){
                    specialItems.add(items);
                }
            }
        }
    }

    private void registerHooks(){
        PluginManager pm = Bukkit.getPluginManager();
        int totalHooks = 0;
        if(pm.getPlugin("PlaceholderAPI") != null){
            utils.sendConsoleMessage("PlaceholderAPI found, enabling hooks!");
            new MobCoinsExpansion().register();
            totalHooks++;
        }

        if(pm.getPlugin("SuperMobCoins") != null){
            utils.sendConsoleMessage("SuperMobCoins found, enabling hooks!");
            superMobCoinsHook = true;
            totalHooks++;
        }

        if(pm.getPlugin("MythicMobs") != null){
            utils.sendConsoleMessage("MythicMobs found, enabling hooks!");
            mythicMobsHook = true;
            totalHooks++;
        }

        if(pm.getPlugin("WildStacker") != null){
            utils.sendConsoleMessage("WildStacker found, enabling hooks!");
            wildStackerHook = true;
            totalHooks++;
        }

        utils.sendConsoleMessage("Successfully hooked " + totalHooks + " plugins, enjoy!");
    }

    public PlayerCoins getPlayerCoins(String uuid){
        if(accounts.containsKey(uuid)){
            return accounts.get(uuid);
        }
        return null;
    }

    private void createDatabaseFile(){
        File pluginFolder = new File("plugins/KrakenMobcoins");
        if(!pluginFolder.exists()){
            pluginFolder.mkdirs();
        }

        File dataFolder = new File(MobCoins.getInstance().getDataFolder(), "database.db");
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

        tempDataManager.setup();
        mobsManager.setup();
        shopManager.setup();
        limitManager.setup();
    }

    public void reloadConfigs(){
        this.reloadConfig();
        mobsManager.reloadData();
        shopManager.reloadData();
        limitManager.reloadData();

        saveLastRewards();
        shopItemsLoader.getShopItemsList().clear();
        mainMenuLoader.getMainMenuItemsList().clear();
        shopNormalLoader.getShopNormalItemsList().clear();
        categories.clear();
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
            loadCategories();
            loadDamageCausePhysical();

            ConfigMessages.initialize(this.getConfig());
            ConfigMessagesList.initialize(this.getConfig());

        }, 1L);

    }

    private void updateConfigs(){
        File configFile = new File(this.getDataFolder(), "config.yml");
        ArrayList<String> listConfig = new ArrayList<>();
        listConfig.add("normalShop.items");
        listConfig.add("rotatingShop.items");

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
        if(mythicMobsHook){
            pm.registerEvents(new MythicMobDeath(this), this);
            pm.registerEvents(new MythicMobDeathPhysical(this), this);
        }
    }

    private void registerCommands(){
        this.getCommand("mobcoins").setExecutor(new MainCommand(this));
    }

    private void startCounting(){
        new BukkitRunnable(){
            public void run(){
                if(normalTime < System.currentTimeMillis()){
                    normalTime = System.currentTimeMillis() + (getConfig().getInt("rotatingShop.normalTimeReset") * 3600 * 1000);
                    utils.refreshNormalItems();
                    for(String message : getConfig().getStringList("rotatingShop.messages.normalRefresh")){
                        Bukkit.broadcastMessage(utils.color(message));
                    }
                }

                if(specialTime < System.currentTimeMillis()){
                    specialTime = System.currentTimeMillis() + (getConfig().getInt("rotatingShop.specialTimeReset") * 3600 * 1000);
                    utils.refreshSpecialItems();
                    for(String message : getConfig().getStringList("rotatingShop.messages.specialRefresh")){
                        Bukkit.broadcastMessage(utils.color(message));
                    }
                }

            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    private void savePlayerToggled(){
        ArrayList<String> list = new ArrayList<>();
        FileConfiguration data = this.getTempDataManager().getConfiguration();

        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            data.set("normalTimeReset", normalTime);
            data.set("specialTimeReset", specialTime);
        }

        if(!this.toggled.isEmpty()){
            list.addAll(this.toggled);

            data.set("data", list);

        }

        this.tempDataManager.saveData();
    }

    private void loadPlayerToggled(){
        FileConfiguration data = this.tempDataManager.getConfiguration();

        ArrayList<String> list = new ArrayList<>(data.getStringList("data"));
        this.toggled.addAll(list);

        data.set("data", new ArrayList<>());

        if(this.getConfig().getBoolean("rotatingShop.enabled")){
            if(data.contains("normalTimeReset") && data.contains("specialTimeReset")){
                System.out.println("[KrakenMobCoins] Loading saved normal time and special time reset.");
                long savedNormal = data.getLong("normalTimeReset");
                long savedSpecial = data.getLong("specialTimeReset");

                normalTime = savedNormal;
                specialTime = savedSpecial;

                data.set("normalTimeReset", null);
                data.set("specialTimeReset", null);
            } else {

                System.out.println("[KrakenMobCoins] Adding default value to the normal time and special time.");
                normalTime = System.currentTimeMillis() + (this.getConfig().getInt("rotatingShop.normalTimeReset") * 3600 * 1000);
                specialTime = System.currentTimeMillis() + (this.getConfig().getInt("rotatingShop.specialTimeReset") * 3600 * 1000);
            }

        }

        this.tempDataManager.saveData();
    }

    public void savePlayerData(){
        try{

            Connection connection = database.getNewConnection();
            for(String uuid : accounts.keySet()){
                PlayerCoins playerCoins = getPlayerCoins(uuid);
                String command = "UPDATE krakencoins SET Coins='" + playerCoins.getMoney() + "' WHERE UUID='" + playerCoins.getUUID() + "'";
                PreparedStatement statement = connection.prepareStatement(command);
                statement.executeUpdate();
            }

            connection.close();
            utils.sendConsoleMessage("Successfully saved all player data!");

        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public List<PlayerCoins> getTop(){

        List<PlayerCoins> coins = new ArrayList<>();
        for (String a : accounts.keySet()){
            coins.add(accounts.get(a));
        }

        List<PlayerCoins> convert = new ArrayList<>(coins);

        convert.sort((pt1, pt2) -> {

            Float f1 = (float) pt1.getMoney();
            Float f2 = (float) pt2.getMoney();

            return f2.compareTo(f1);

        });
        //	Collections.reverse(convert);
        if (convert.size() > 10){
            convert = convert.subList(0, 10);
        }
        return convert;

    }

    private void runAutoSave(){
        if(this.getConfig().getBoolean("autoSave.enabled")){
            int interval = this.getConfig().getInt("autoSave.interval");
            new BukkitRunnable(){
                @Override
                public void run(){
                    savePlayerData();
                }
            }.runTaskTimerAsynchronously(this, 0L, 20 * interval);
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
        categories.clear();
        mobSpawner.clear();
        toggled.clear();
        damageCauses.clear();
    }

    public static MobCoins getInstance() { return instance; }
    public Utils getUtils() { return utils; }
    public SQL getDatabase() { return database; }
    public Map<String, PlayerCoins> getAccounts() { return accounts; }
    public Set<String> getToggled() { return toggled; }
    public TempDataManager getTempDataManager() { return tempDataManager; }
    public ShopManager getShopManager() { return shopManager; }
    public long getNormalTime() { return normalTime; }
    public long getSpecialTime() { return specialTime; }
    public List<ShopItems> getNormalItems() { return normalItems; }
    public List<ShopItems> getSpecialItems() { return specialItems; }
    public LimitManager getLimitManager() { return limitManager; }
    public MainMenuLoader getMainMenuLoader() { return mainMenuLoader; }
    public ShopNormalLoader getShopNormalLoader() { return shopNormalLoader; }
    public Map<String, FileConfiguration> getCategories() { return categories; }
    public RotatingLoader getRotatingLoader() { return rotatingLoader; }
    public Set<Entity> getMobSpawner() { return mobSpawner; }
    public ShopUtils getShopUtils() { return shopUtils; }
    public Map<String, Integer> getLimit() { return limit; }
    public Map<String, Integer> getStock() { return stock; }
    public List<EntityDamageEvent.DamageCause> getDamageCauses() { return damageCauses; }
    public CoinMobManager getCoinMobManager() { return coinMobManager; }
    public SalaryManager getSalaryManager() { return salaryManager; }

}
