package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessagesList;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class TopCommand extends SubCommand {

    @Override
    public @NotNull String getName() {
        return "top";
    }

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.top";
    }

    @Override
    public @Nullable List<String> getAliases() {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        for(String message : ConfigMessagesList.LEADERBOARD.toStringList()){
            sender.sendMessage(utils.color(message)
                    .replace("%name_top1%", this.getTopName(0))
                    .replace("%money_top1%", this.getTopMoney(0))
                    .replace("%name_top2%", this.getTopName(1))
                    .replace("%money_top2%", this.getTopMoney(1))
                    .replace("%name_top3%", this.getTopName(2))
                    .replace("%money_top3%", this.getTopMoney(2))
                    .replace("%name_top4%", this.getTopName(3))
                    .replace("%money_top4%", this.getTopMoney(3))
                    .replace("%name_top5%", this.getTopName(4))
                    .replace("%money_top5%", this.getTopMoney(4))
                    .replace("%name_top6%", this.getTopName(5))
                    .replace("%money_top6%", this.getTopMoney(5))
                    .replace("%name_top7%", this.getTopName(6))
                    .replace("%money_top7%", this.getTopMoney(6))
                    .replace("%name_top8%", this.getTopName(7))
                    .replace("%money_top8%", this.getTopMoney(7))
                    .replace("%name_top9%", this.getTopName(8))
                    .replace("%money_top9%", this.getTopMoney(8))
                    .replace("%name_top10%", this.getTopName(9))
                    .replace("%money_top10%", this.getTopMoney(9)));
        }

    }

    private String getTopName(int index){

        FileConfiguration config = MobCoins.getInstance().getConfig();
        String nameEmpty = config.getString("placeholders.top.nameIfEmpty");

        List<PlayerCoins> playerCoinsList = MobCoins.getInstance().getTop();

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return playerCoins.getPlayerName();
        } catch(IndexOutOfBoundsException exception){
            return nameEmpty;
        }

    }

    private String getTopMoney(int index){
        FileConfiguration config = MobCoins.getInstance().getConfig();
        String moneyEmpty = config.getString("placeholders.top.moneyIfEmpty");

        List<PlayerCoins> playerCoinsList = MobCoins.getInstance().getTop();
        DecimalFormat df = new DecimalFormat("###,###,###,###,###.##");

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return df.format(playerCoins.getMoney());
        } catch(IndexOutOfBoundsException exception){
            return moneyEmpty;
        }

    }

}
