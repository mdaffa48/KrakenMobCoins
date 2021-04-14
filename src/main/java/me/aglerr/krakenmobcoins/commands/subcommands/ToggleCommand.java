package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToggleCommand extends SubCommand {

    @Override
    public @NotNull String getName() {
        return "toggle";
    }

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.toggle";
    }

    @Override
    public @Nullable List<String> getAliases() {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(MobCoins.getInstance().getToggled().contains(player.getUniqueId().toString())){

                MobCoins.getInstance().getToggled().remove(player.getUniqueId().toString());
                player.sendMessage(utils.color(ConfigMessages.TOGGLE_ON.toString())
                        .replace("%prefix%", utils.getPrefix()));

            } else {

                MobCoins.getInstance().getToggled().add(player.getUniqueId().toString());
                player.sendMessage(utils.color(ConfigMessages.TOGGLE_OFF.toString())
                        .replace("%prefix%", utils.getPrefix()));
            }

        } else {
            sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                    .replace("%player%", utils.getPrefix()));
        }

    }
}
