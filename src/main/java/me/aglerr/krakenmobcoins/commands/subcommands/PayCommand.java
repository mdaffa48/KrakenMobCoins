package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PayCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.pay";
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length < 3){
                player.sendMessage(utils.color("&cUsage: /mobcoins pay <player> <amount>"));
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
                    if(amount < 0){
                        sender.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }

                    PlayerCoins playerCoins = plugin.getPlayerCoins(player.getUniqueId().toString());
                    PlayerCoins targetCoins = plugin.getPlayerCoins(target.getUniqueId().toString());

                    if(playerCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }

                    if(targetCoins == null){
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return;
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
                        return;
                    }


                } else {
                    player.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }
            }

        } else {
            sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                    .replace("%player%", utils.getPrefix()));
            return;
        }

    }

}
