package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.tasks.ConvertTask;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConvertCommand extends SubCommand {

    @Override
    public @NotNull String getName() {
        return "convert";
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

        if(MobCoins.superMobCoinsHook){

            sender.sendMessage(utils.color("%prefix% &aStarting to convert data from SuperMobCoins to KrakenMobCoins, please wait!")
                    .replace("%prefix%", utils.getPrefix()));
            ConvertTask task = new ConvertTask();
            task.runTaskAsynchronously(MobCoins.getInstance());

        } else {
            sender.sendMessage(utils.color("%prefix% &cSuperMobCoins not found, please have it installed first!")
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

}
