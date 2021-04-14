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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class BalanceCommand extends SubCommand {

    @Override
    public @NotNull String getName() {
        return "balance";
    }

    @Override
    public @Nullable String getPermission(){ return "krakenmobcoins.balance"; }

    @Override
    public @Nullable List<String> getAliases() {
        return Arrays.asList("bal", "coin", "coins");
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        DecimalFormat df = utils.getDFormat();

        if(args.length == 1){
            if(sender instanceof Player){
                Player player = (Player) sender;
                PlayerCoins coins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

                if(coins == null){
                    player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;

                } else {

                    player.sendMessage(utils.color(ConfigMessages.BALANCE.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%coins%", df.format(coins.getMoney())));

                }

            } else {

                sender.sendMessage(utils.color("&cUsage: /mobcoins balance <player>"));
                return;

            }

        } else if(args.length == 2){
            if(!(sender.hasPermission("krakenmobcoins.balance.others"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.balance.others"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));
                return;

            } else {

                PlayerCoins coins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                if(coins == null){
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));
                    return;

                } else {

                    sender.sendMessage(utils.color(ConfigMessages.BALANCE_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%coins%", df.format(coins.getMoney()))
                            .replace("%player%", args[1]));

                }

            }

        }

    }

}
