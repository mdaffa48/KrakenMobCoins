package me.aglerr.krakenmobcoins.commands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.commands.subcommands.*;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    private final MobCoins plugin;
    public MainCommand(final MobCoins plugin){
        this.plugin = plugin;

        AddCommand addCommand = new AddCommand();
        BalanceCommand balanceCommand = new BalanceCommand();
        CategoryCommand categoryCommand = new CategoryCommand();
        ConvertCommand convertCommand = new ConvertCommand();
        HelpCommand helpCommand = new HelpCommand();
        PayCommand payCommand = new PayCommand();
        RefreshCommand refreshCommand = new RefreshCommand();
        ReloadCommand reloadCommand = new ReloadCommand();
        RemoveCommand removeCommand = new RemoveCommand();
        SetCommand setCommand = new SetCommand();
        ShopCommand shopCommand = new ShopCommand();
        ToggleCommand toggleCommand = new ToggleCommand();
        TopCommand topCommand = new TopCommand();
        WithdrawCommand withdrawCommand = new WithdrawCommand();

        subCommands.put("add", addCommand);

        subCommands.put("balance", balanceCommand);
        subCommands.put("bal", balanceCommand);

        subCommands.put("category", categoryCommand);
        subCommands.put("convert", convertCommand);
        subCommands.put("help", helpCommand);
        subCommands.put("pay", payCommand);
        subCommands.put("refresh", refreshCommand);
        subCommands.put("reload", reloadCommand);
        subCommands.put("remove", removeCommand);
        subCommands.put("set", setCommand);
        subCommands.put("shop", shopCommand);
        subCommands.put("toggle", toggleCommand);
        subCommands.put("top", topCommand);

        subCommands.put("withdraw", withdrawCommand);
        subCommands.put("wd", withdrawCommand);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Utils utils = plugin.getUtils();

        if(args.length > 0){
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if(subCommand != null){
                if(subCommand.getPermission() != null){
                    if(!(sender.hasPermission(subCommand.getPermission()))){
                        sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%permission%", subCommand.getPermission()));
                        return true;
                    }
                }

                subCommand.perform(plugin, sender, args);
                return true;

            }

        } else {
            this.sendHelp(sender);
        }

        return false;
    }

    private void sendHelp(CommandSender sender){
        Utils utils = plugin.getUtils();

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

}
