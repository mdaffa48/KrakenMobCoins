package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.enums.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadCommand extends SubCommand {

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

        sender.sendMessage(utils.color(ConfigMessages.RELOAD.toString())
                .replace("%prefix%", utils.getPrefix()));
        plugin.reloadConfigs();

    }

}
