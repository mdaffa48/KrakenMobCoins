package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefreshCommand extends SubCommand {

    @Override
    public @NotNull String getName() {
        return "refresh";
    }

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public @Nullable List<String> getAliases() {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        FileConfiguration config = plugin.getConfig();

        if(config.getBoolean("rotatingShop.enabled")){

            sender.sendMessage(utils.color(ConfigMessages.REFRESH.toString())
                    .replace("%prefix%", utils.getPrefix()));

            MobCoins.getInstance().normalTime = System.currentTimeMillis() + (config.getInt("rotatingShop.normalTimeReset") * 3600 * 1000);
            MobCoins.getInstance().specialTime = System.currentTimeMillis() + (config.getInt("rotatingShop.specialTimeReset") * 3600 * 1000);
            utils.refreshNormalItems();
            utils.refreshSpecialItems();

        }

        utils.resetStock();
        utils.resetLimit();

    }

}
