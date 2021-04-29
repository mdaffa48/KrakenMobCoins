package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.enums.ConfigMessages;
import me.aglerr.krakenmobcoins.manager.RotatingManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefreshCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        FileConfiguration config = plugin.getConfig();

        if(config.getBoolean("rotatingShop.enabled")){

            sender.sendMessage(utils.color(ConfigMessages.REFRESH.toString())
                    .replace("%prefix%", utils.getPrefix()));

            RotatingManager rotatingManager = plugin.getRotatingManager();

            rotatingManager.setNormalTime(rotatingManager.getDefaultNormalTime());
            rotatingManager.setSpecialTime(rotatingManager.getDefaultSpecialTime());

            rotatingManager.refreshNormalItems();
            rotatingManager.refreshSpecialItems();

        }

        plugin.getItemStockManager().clearStock();
        plugin.getPurchaseLimitManager().resetLimit();

    }

}
