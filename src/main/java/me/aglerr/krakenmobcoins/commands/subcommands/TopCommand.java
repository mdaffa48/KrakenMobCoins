package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.enums.ConfigMessagesList;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class TopCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.top";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        for(String message : ConfigMessagesList.LEADERBOARD.toStringList()){
            sender.sendMessage(utils.color(message)
                    .replace("%name_top1%", utils.getTopName(0))
                    .replace("%money_top1%", utils.getTopMoney(0))
                    .replace("%name_top2%", utils.getTopName(1))
                    .replace("%money_top2%", utils.getTopMoney(1))
                    .replace("%name_top3%", utils.getTopName(2))
                    .replace("%money_top3%", utils.getTopMoney(2))
                    .replace("%name_top4%", utils.getTopName(3))
                    .replace("%money_top4%", utils.getTopMoney(3))
                    .replace("%name_top5%", utils.getTopName(4))
                    .replace("%money_top5%", utils.getTopMoney(4))
                    .replace("%name_top6%", utils.getTopName(5))
                    .replace("%money_top6%", utils.getTopMoney(5))
                    .replace("%name_top7%", utils.getTopName(6))
                    .replace("%money_top7%", utils.getTopMoney(6))
                    .replace("%name_top8%", utils.getTopName(7))
                    .replace("%money_top8%", utils.getTopMoney(7))
                    .replace("%name_top9%", utils.getTopName(8))
                    .replace("%money_top9%", utils.getTopMoney(8))
                    .replace("%name_top10%", utils.getTopName(9))
                    .replace("%money_top10%", utils.getTopMoney(9))
                    .replace("%money_format_top1%", utils.getTopMoneyFormat(0))
                    .replace("%money_format_top2%", utils.getTopMoneyFormat(1))
                    .replace("%money_format_top3%", utils.getTopMoneyFormat(2))
                    .replace("%money_format_top4%", utils.getTopMoneyFormat(3))
                    .replace("%money_format_top5%", utils.getTopMoneyFormat(4))
                    .replace("%money_format_top6%", utils.getTopMoneyFormat(5))
                    .replace("%money_format_top7%", utils.getTopMoneyFormat(6))
                    .replace("%money_format_top8%", utils.getTopMoneyFormat(7))
                    .replace("%money_format_top9%", utils.getTopMoneyFormat(8))
                    .replace("%money_format_top10%", utils.getTopMoneyFormat(9)));
        }

    }

}
