package me.aglerr.krakenmobcoins.commands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.api.events.MobCoinsWithdrawEvent;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.tasks.ConvertTask;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobCoinsCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        FileConfiguration config = MobCoins.getInstance().getConfig();
        Utils utils = MobCoins.getInstance().getUtils();

        if(args.length == 0){
            sendHelp(sender);
        } else if(args[0].equalsIgnoreCase("help")){
            sendHelp(sender);
        } else if(args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")){
            DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");
            if(args.length == 1){
                if(!(sender.hasPermission("krakenmobcoins.balance"))){
                    sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%permission%", "krakenmobcoins.balance"));
                    return true;
                }

                if(sender instanceof Player){
                    Player player = (Player) sender;
                    PlayerCoins coins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

                    if(coins == null){
                        player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                        return true;

                    } else {

                        player.sendMessage(utils.color(ConfigMessages.BALANCE.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%coins%", df.format(coins.getMoney())));
                    }


                } else {
                    sender.sendMessage(utils.color("&cUsage: /mobcoins balance <player>"));
                    return true;
                }
            } else if(args.length == 2){
                if(!(sender.hasPermission("krakenmobcoins.balance.others"))){
                    sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%permission%", "krakenmobcoins.balance.others"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%player%", args[1]));
                    return true;
                } else {

                    PlayerCoins coins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                    if(coins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return true;

                    } else {

                        sender.sendMessage(utils.color(ConfigMessages.BALANCE_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%coins%", df.format(coins.getMoney()))
                                .replace("%player%", args[1]));
                    }


                }
            }

        } else if(args[0].equalsIgnoreCase("pay")){
            if(!(sender.hasPermission("krakenmobcoins.pay"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.pay"));
                return true;
            }
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length < 3){
                    player.sendMessage(utils.color("&cUsage: /mobcoins pay <player> <amount>"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));
                    return true;
                } else {
                    if(utils.isDouble(args[2])){
                        double amount = Double.parseDouble(args[2]);

                        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
                        PlayerCoins targetCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());

                        if(playerCoins == null){
                            sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                                    .replace("%prefix%", utils.getPrefix()));
                            return true;
                        }

                        if(targetCoins == null){
                            sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                    .replace("%prefix%", utils.getPrefix())
                                    .replace("%player%", args[1]));
                            return true;
                        }

                        if(playerCoins.getMoney() >= amount){
                            playerCoins.setMoney(playerCoins.getMoney() - amount);
                            targetCoins.setMoney(targetCoins.getMoney() + amount);

                            player.sendMessage(utils.color(ConfigMessages.SEND_COINS.toString())
                                    .replace("%prefix%", utils.getPrefix())
                                    .replace("%player%", target.getName())
                                    .replace("%coins%", String.valueOf(amount)));

                            target.sendMessage(utils.color(ConfigMessages.RECEIVED_COINS.toString())
                                    .replace("%prefix%", utils.getPrefix())
                                    .replace("%player%", player.getName())
                                    .replace("%coins%", String.valueOf(amount)));
                        } else {
                            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                            .replace("%prefix%", utils.getPrefix()));
                            return true;
                        }


                    } else {
                        player.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                        .replace("%prefix%", utils.getPrefix()));
                        return true;
                    }
                }

            } else {
                sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                .replace("%player%", utils.getPrefix()));
                return true;
            }
        } else if(args[0].equalsIgnoreCase("withdraw")){
            if(!(sender.hasPermission("krakenmobcoins.withdraw"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.withdraw"));
                return true;
            }
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length < 2){
                    sender.sendMessage(utils.color("&cUsage: /mobcoins withdraw <amount>"));
                    return true;
                }

                PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
                if(playerCoins == null){
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return true;
                }

                if(utils.isDouble(args[1])){
                    double amount = Double.parseDouble(args[1]);
                    if(playerCoins.getMoney() >= amount){
                        if(player.getInventory().firstEmpty() == -1){
                            player.sendMessage(utils.color(ConfigMessages.INVENTORY_FULL.toString())
                            .replace("%prefix%", utils.getPrefix()));
                            return true;
                        }

                        MobCoinsWithdrawEvent mobCoinsWithdrawEvent = new MobCoinsWithdrawEvent(player, amount, utils.getMobCoinItem(amount));
                        Bukkit.getPluginManager().callEvent(mobCoinsWithdrawEvent);
                        if(mobCoinsWithdrawEvent.isCancelled()) return true;

                        playerCoins.setMoney(playerCoins.getMoney() - amount);
                        player.getInventory().addItem(utils.getMobCoinItem(amount));
                        player.sendMessage(utils.color(ConfigMessages.WITHDRAW.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%coins%", String.valueOf(amount)));
                        player.updateInventory();

                    } else {
                        player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return true;
                    }

                } else {
                    player.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return true;
                }

            } else {
                sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                        .replace("%player%", utils.getPrefix()));
                return true;
            }
        } else if(args[0].equalsIgnoreCase("toggle")){
            if(!(sender.hasPermission("krakenmobcoins.toggle"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.toggle"));
                return true;
            }
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(MobCoins.getInstance().getToggled().contains(player.getUniqueId().toString())){

                    MobCoins.getInstance().getToggled().remove(player.getUniqueId().toString());
                    player.sendMessage(utils.color(ConfigMessages.TOGGLE_ON.toString())
                    .replace("%prefix%", utils.getPrefix()));

                } else {

                    MobCoins.getInstance().getToggled().add(player.getUniqueId().toString());
                    player.sendMessage(utils.color(ConfigMessages.TOGGLE_OFF.toString())
                    .replace("%prefix%", utils.getPrefix()));
                }

            } else {
                sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                        .replace("%player%", utils.getPrefix()));
            }
            return true;
        } else if(args[0].equalsIgnoreCase("top")){
            if(!(sender.hasPermission("krakenmobcoins.top"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.top"));
                return true;
            }

            for(String message : ConfigMessagesList.LEADERBOARD.toStringList()){
                sender.sendMessage(utils.color(message)
                .replace("%name_top1%", this.getTopName(0))
                .replace("%money_top1%", this.getTopMoney(0))
                .replace("%name_top2%", this.getTopName(1))
                .replace("%money_top2%", this.getTopMoney(1))
                .replace("%name_top3%", this.getTopName(2))
                .replace("%money_top3%", this.getTopMoney(2))
                .replace("%name_top4%", this.getTopName(3))
                .replace("%money_top4%", this.getTopMoney(3))
                .replace("%name_top5%", this.getTopName(4))
                .replace("%money_top5%", this.getTopMoney(4))
                .replace("%name_top6%", this.getTopName(5))
                .replace("%money_top6%", this.getTopMoney(5))
                .replace("%name_top7%", this.getTopName(6))
                .replace("%money_top7%", this.getTopMoney(6))
                .replace("%name_top8%", this.getTopName(7))
                .replace("%money_top8%", this.getTopMoney(7))
                .replace("%name_top9%", this.getTopName(8))
                .replace("%money_top9%", this.getTopMoney(8))
                .replace("%name_top10%", this.getTopName(9))
                .replace("%money_top10%", this.getTopMoney(9)));
            }

        } else if(args[0].equalsIgnoreCase("shop")){
            if(!(sender.hasPermission("krakenmobcoins.shop"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.shop"));
                return true;
            }

            if(args.length == 1){
                if(sender instanceof Player){

                    Player player = (Player) sender;
                    PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

                    if(playerCoins == null){
                        player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return true;
                    }

                    utils.openShopMenu(player);

                } else {
                    sender.sendMessage(utils.color("&cUsage: /mobcoins shop <player]"));
                    return true;
                }
            } else if(args.length == 2){
                if(!(sender.hasPermission("krakenmobcoins.shop.others"))){
                    sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%permission%", "krakenmobcoins.shop.others"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));
                    return true;
                } else {

                    PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                    if(playerCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return true;
                    }

                    utils.openShopMenu(target);

                    sender.sendMessage(utils.color(ConfigMessages.OPENED_SHOP_MENU.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%player%", target.getName()));

                }

            }

        } else if(args[0].equalsIgnoreCase("reload")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            sender.sendMessage(utils.color(ConfigMessages.RELOAD.toString())
            .replace("%prefix%", utils.getPrefix()));
            MobCoins.getInstance().reloadConfigs();

        } else if(args[0].equalsIgnoreCase("add")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(args.length < 3){
                sender.sendMessage(utils.color("&cUsage: /mobcoins add <player> <amount>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));
                return true;
            } else {
                if(utils.isDouble(args[2])){
                    double amount = Double.parseDouble(args[2]);
                    PlayerCoins targetCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                    if(targetCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return true;
                    } else {

                        targetCoins.setMoney(targetCoins.getMoney() + amount);
                        sender.sendMessage(utils.color(ConfigMessages.ADD_COINS.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", target.getName())
                        .replace("%coins%", String.valueOf(amount)));

                        target.sendMessage(utils.color(ConfigMessages.TARGET_ADD_COINS.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%coins%", String.valueOf(amount)));

                    }

                } else {
                    sender.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return true;
                }
            }

        } else if(args[0].equalsIgnoreCase("remove")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(args.length < 3){
                sender.sendMessage(utils.color("&cUsage: /mobcoins remove <player> <amount>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));
                return true;
            } else {
                if(utils.isDouble(args[2])){
                    double amount = Double.parseDouble(args[2]);
                    PlayerCoins targetCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                    if(targetCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return true;
                    } else {
                        double total = targetCoins.getMoney() - amount;
                        if(!config.getBoolean("options.canGoNegative")){
                            if(total < 0){
                                sender.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                                return true;
                            }
                        }

                        targetCoins.setMoney(total);
                        sender.sendMessage(utils.color(ConfigMessages.REMOVE_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", target.getName())
                                .replace("%coins%", String.valueOf(amount)));

                        target.sendMessage(utils.color(ConfigMessages.TARGET_REMOVE_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%coins%", String.valueOf(amount)));

                    }

                } else {
                    sender.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return true;
                }
            }

        } else if(args[0].equalsIgnoreCase("set")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(args.length < 3){
                sender.sendMessage(utils.color("&cUsage: /mobcoins set <player> <amount>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));
                return true;
            } else {
                if(utils.isDouble(args[2])){
                    double amount = Double.parseDouble(args[2]);
                    PlayerCoins targetCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                    if(targetCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return true;
                    } else {
                        if(!config.getBoolean("options.canGoNegative")){
                            if(amount < 0){
                                sender.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                                        .replace("%prefix%", utils.getPrefix()));
                                return true;
                            }
                        }

                        targetCoins.setMoney(amount);
                        sender.sendMessage(utils.color(ConfigMessages.SET_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", target.getName())
                                .replace("%coins%", String.valueOf(amount)));

                        target.sendMessage(utils.color(ConfigMessages.TARGET_SET_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%coins%", String.valueOf(amount)));

                    }

                } else {
                    sender.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return true;
                }
            }
        }  else if(args[0].equalsIgnoreCase("refresh")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(config.getBoolean("rotatingShop.enabled")){

                sender.sendMessage(utils.color(ConfigMessages.REFRESH.toString())
                .replace("%prefix%", utils.getPrefix()));

                MobCoins.getInstance().normalTime = System.currentTimeMillis() + (config.getInt("rotatingShop.normalTimeReset") * 3600 * 1000);
                MobCoins.getInstance().specialTime = System.currentTimeMillis() + (config.getInt("rotatingShop.specialTimeReset") * 3600 * 1000);
                utils.refreshNormalItems();
                utils.refreshSpecialItems();

            }

            MobCoins.getInstance().getStock().clear();
            MobCoins.getInstance().getLimitManager().getConfiguration().set("items", new ArrayList<>());
            MobCoins.getInstance().getLimitManager().saveData();

        } else if(args[0].equalsIgnoreCase("category")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(args.length < 3){
                sender.sendMessage(utils.color("&cUsage: /mobcoins category add/remove/open <category>"));
                return true;
            }

            String argument = args[1];
            String category = args[2];
            File file = this.getCategory(category);
            if(argument.equalsIgnoreCase("add")){
                if(!file.exists()){

                    this.createCategory(file);
                    utils.exampleShop(file);

                    FileConfiguration categoryConfig = YamlConfiguration.loadConfiguration(file);
                    MobCoins.getInstance().getCategories().put(file.getName(), categoryConfig);

                    sender.sendMessage(utils.color(ConfigMessages.CREATE_CATEGORY.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%category%", category));

                } else {
                    sender.sendMessage(utils.color(ConfigMessages.CATEGORY_ALREADY_EXIST.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%category%", category));
                    return true;
                }

            } else if(argument.equalsIgnoreCase("remove")){
                if(file.exists()){

                    file.delete();
                    sender.sendMessage(utils.color(ConfigMessages.REMOVE_CATEGORY.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%category%", category));

                } else {

                    sender.sendMessage(utils.color(ConfigMessages.CATEGORY_NOT_EXIST.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%category%", category));
                    return true;
                }

            } else if(argument.equalsIgnoreCase("open")){
                if(args.length == 3){
                    if(sender instanceof Player){
                        Player player = (Player) sender;
                        utils.openCategory(category, player);
                    } else {
                        sender.sendMessage(utils.color("&cUsage: /mobcoins category open <category> [player]"));
                        return true;
                    }
                }

                if(args.length == 4){
                    Player target = Bukkit.getPlayer(args[3]);
                    if(target == null){
                        sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return true;
                    } else {
                        utils.openCategory(category, target);
                    }
                }
            }

        } else if(args[0].equalsIgnoreCase("convert")){
            if(!(sender.hasPermission("krakenmobcoins.admin"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.admin"));
                return true;
            }

            if(MobCoins.superMobCoinsHook){

                sender.sendMessage(utils.color("%prefix% &aStarting to convert data from SuperMobCoins to KrakenMobCoins, please wait!")
                        .replace("%prefix%", utils.getPrefix()));
                ConvertTask task = new ConvertTask();
                task.runTaskAsynchronously(MobCoins.getInstance());

            } else {
                sender.sendMessage(utils.color("%prefix% &cSuperMobCoins not found, please have it installed first!")
                .replace("%prefix%", utils.getPrefix()));
                return true;
            }
        }

        return false;
    }

    private void sendHelp(CommandSender sender){
        Utils utils = MobCoins.getInstance().getUtils();

        if(sender.hasPermission("krakenmobcoins.admin")){
            for(String message : ConfigMessagesList.HELP_ADMIN.toStringList()){
                sender.sendMessage(utils.color(message));
            }
        } else {
            for(String message : ConfigMessagesList.HELP.toStringList()){
                sender.sendMessage(utils.color(message));
            }
        }
    }

    private File getCategory(String category){
        File file = new File(MobCoins.getInstance().getDataFolder() + File.separator + "categories", category + ".yml");
        return file;
    }

    private void createCategory(File file){
        try{
            file.createNewFile();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private String getTopName(int index){

        FileConfiguration config = MobCoins.getInstance().getConfig();
        String nameEmpty = config.getString("placeholders.top.nameIfEmpty");

        List<PlayerCoins> playerCoinsList = MobCoins.getInstance().getTop();

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return playerCoins.getPlayerName();
        } catch(IndexOutOfBoundsException exception){
            return nameEmpty;
        }

    }

    private String getTopMoney(int index){
        FileConfiguration config = MobCoins.getInstance().getConfig();
        String moneyEmpty = config.getString("placeholders.top.moneyIfEmpty");

        List<PlayerCoins> playerCoinsList = MobCoins.getInstance().getTop();
        DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return df.format(playerCoins.getMoney());
        } catch(IndexOutOfBoundsException exception){
            return moneyEmpty;
        }

    }

}
