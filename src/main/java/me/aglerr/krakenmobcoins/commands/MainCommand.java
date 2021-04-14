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

public class MainCommand implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    private final MobCoins plugin;
    public MainCommand(final MobCoins plugin){
        this.plugin = plugin;

        // Adding sub commands to the list.
        subCommands.add(new AddCommand());
        subCommands.add(new BalanceCommand());
        subCommands.add(new CategoryCommand());
        subCommands.add(new ConvertCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new PayCommand());
        subCommands.add(new RefreshCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new RemoveCommand());
        subCommands.add(new SetCommand());
        subCommands.add(new ShopCommand());
        subCommands.add(new ToggleCommand());
        subCommands.add(new TopCommand());
        subCommands.add(new WithdrawCommand());

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Utils utils = MobCoins.getInstance().getUtils();

        if(args.length == 0){
            this.sendHelp(sender);
            return true;
        }

        for(SubCommand subCommand : subCommands){
            if(args[0].equalsIgnoreCase(subCommand.getName())){

                if(subCommand.getPermission() != null){
                    if(!(sender.hasPermission(subCommand.getPermission()))){
                        sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%permission%", subCommand.getPermission()));
                        return true;
                    }
                }

                subCommand.perform(plugin, sender, args);
                break;

            }

            this.checkAliasesAndPerform(subCommand, args[0], sender, args);

        }

        return false;
    }

    private void checkAliasesAndPerform(SubCommand subCommand, String command, CommandSender sender, String[] args){
        Utils utils = MobCoins.getInstance().getUtils();
        if(subCommand.getAliases() != null){
            for(String alias : subCommand.getAliases()){
                if(alias.equalsIgnoreCase(command)){
                    if(subCommand.getPermission() != null){
                        if(!(sender.hasPermission(subCommand.getPermission()))){
                            sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                                    .replace("%prefix%", utils.getPrefix())
                                    .replace("%permission%", subCommand.getPermission()));
                            return;
                        }
                    }

                    subCommand.perform(plugin, sender, args);
                    break;
                }
            }
        }
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

}
