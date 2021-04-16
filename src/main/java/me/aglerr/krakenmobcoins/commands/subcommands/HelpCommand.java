package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HelpCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return null;
    }


    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {
        sendHelp(sender, plugin);
    }

    private void sendHelp(CommandSender sender, MobCoins plugin){
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
