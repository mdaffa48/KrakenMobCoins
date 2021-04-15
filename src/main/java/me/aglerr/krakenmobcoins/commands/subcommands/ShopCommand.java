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

import java.util.Arrays;
import java.util.List;

public class ShopCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.shop";
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if(args.length == 1){
            if(sender instanceof Player){

                Player player = (Player) sender;
                PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());

                if(playerCoins == null){
                    player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }

                utils.openShopMenu(player);

            } else {
                sender.sendMessage(utils.color("&cUsage: /mobcoins shop <player]"));
            }

        } else if(args.length == 2){
            if(!(sender.hasPermission("krakenmobcoins.shop.others"))){
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.shop.others"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));
                return;
            } else {

                PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(target.getUniqueId().toString());
                if(playerCoins == null){
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", target.getName()));
                    return;
                }

                utils.openShopMenu(target);

                sender.sendMessage(utils.color(ConfigMessages.OPENED_SHOP_MENU.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", target.getName()));

            }

        }

    }
}
