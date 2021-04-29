package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.enums.ConfigMessagesList;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
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
                    .replace("%name_top1%", this.getTopName(plugin, 0))
                    .replace("%money_top1%", this.getTopMoney(plugin, 0))
                    .replace("%name_top2%", this.getTopName(plugin, 1))
                    .replace("%money_top2%", this.getTopMoney(plugin, 1))
                    .replace("%name_top3%", this.getTopName(plugin, 2))
                    .replace("%money_top3%", this.getTopMoney(plugin, 2))
                    .replace("%name_top4%", this.getTopName(plugin, 3))
                    .replace("%money_top4%", this.getTopMoney(plugin, 3))
                    .replace("%name_top5%", this.getTopName(plugin, 4))
                    .replace("%money_top5%", this.getTopMoney(plugin, 4))
                    .replace("%name_top6%", this.getTopName(plugin, 5))
                    .replace("%money_top6%", this.getTopMoney(plugin, 5))
                    .replace("%name_top7%", this.getTopName(plugin, 6))
                    .replace("%money_top7%", this.getTopMoney(plugin, 6))
                    .replace("%name_top8%", this.getTopName(plugin, 7))
                    .replace("%money_top8%", this.getTopMoney(plugin, 7))
                    .replace("%name_top9%", this.getTopName(plugin, 8))
                    .replace("%money_top9%", this.getTopMoney(plugin, 8))
                    .replace("%name_top10%", this.getTopName(plugin, 9))
                    .replace("%money_top10%", this.getTopMoney(plugin, 9)));
        }

    }

    private String getTopName(MobCoins plugin, int index){

        FileConfiguration config = plugin.getConfig();
        String nameEmpty = config.getString("placeholders.top.nameIfEmpty");

        List<PlayerCoins> playerCoinsList = plugin.getAccountManager().getTop();

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return playerCoins.getPlayerName();
        } catch(IndexOutOfBoundsException exception){
            return nameEmpty;
        }

    }

    private String getTopMoney(MobCoins plugin, int index){
        FileConfiguration config = plugin.getConfig();
        String moneyEmpty = config.getString("placeholders.top.moneyIfEmpty");

        List<PlayerCoins> playerCoinsList = plugin.getAccountManager().getTop();
        DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return df.format(playerCoins.getMoney());
        } catch(IndexOutOfBoundsException exception){
            return moneyEmpty;
        }

    }

}
