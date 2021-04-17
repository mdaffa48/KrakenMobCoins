package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        FileConfiguration config = plugin.getConfig();

        if(args.length < 3){
            sender.sendMessage(utils.color("&cUsage: /mobcoins remove <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if(target == null){
            sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%player%", args[1]));
            return;
        } else {
            if(utils.isDouble(args[2])){
                double amount = Double.parseDouble(args[2]);
                PlayerCoins targetCoins = plugin.getAccountManager().getPlayerData(target.getUniqueId().toString());
                if(targetCoins == null){
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));
                    return;
                } else {
                    double total = targetCoins.getMoney() - amount;
                    if(!config.getBoolean("options.canGoNegative")){
                        if(total < 0){
                            sender.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                                    .replace("%prefix%", utils.getPrefix()));
                            return;
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
                return;
            }
        }

    }

}
