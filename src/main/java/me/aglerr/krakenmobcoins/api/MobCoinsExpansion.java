package me.aglerr.krakenmobcoins.api;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.manager.AccountManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class MobCoinsExpansion extends PlaceholderExpansion {

    private final MobCoins plugin;
    private final AccountManager accountManager;
    public MobCoinsExpansion(final MobCoins plugin){
        this.plugin = plugin;
        this.accountManager = plugin.getAccountManager();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "krakenmobcoins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "aglerr";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null) return null;

        Utils utils = plugin.getUtils();
        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId().toString());

        DecimalFormat df = utils.getDecimalFormat();

        if(identifier.equalsIgnoreCase("balance")){
            if(playerCoins == null) return null;
            return String.valueOf(playerCoins.getMoney());
        }

        if(identifier.equalsIgnoreCase("balance_formatted")){
            if(playerCoins == null) return null;
            return df.format(playerCoins.getMoney());

        }

        if(identifier.equalsIgnoreCase("balance_shortformat")){
            if(playerCoins == null) return null;
            return utils.formatShort(playerCoins.getMoney());
        }

        if(identifier.equalsIgnoreCase("balance_rounded")){
            if(playerCoins == null) return null;
            return String.valueOf(Math.round(playerCoins.getMoney()));
        }

        if(identifier.equalsIgnoreCase("normal_time")){
            return plugin.getRotatingManager().getFormattedResetTime(false);
        }

        if(identifier.equalsIgnoreCase("special_time")){
            return plugin.getRotatingManager().getFormattedResetTime(true);
        }

        if(identifier.equalsIgnoreCase("name_top1"))
            return utils.getTopName(0);
        if(identifier.equalsIgnoreCase("money_top1"))
            return utils.getTopMoney(0);
        if(identifier.equalsIgnoreCase("money_format_top1"))
            return utils.getTopMoneyFormat(0);

        if(identifier.equalsIgnoreCase("name_top2"))
            return utils.getTopName(1);
        if(identifier.equalsIgnoreCase("money_top2"))
            return utils.getTopMoney(1);
        if(identifier.equalsIgnoreCase("money_format_top2"))
            return utils.getTopMoneyFormat(1);

        if(identifier.equalsIgnoreCase("name_top3"))
            return utils.getTopName(2);
        if(identifier.equalsIgnoreCase("money_top3"))
            return utils.getTopMoney(2);
        if(identifier.equalsIgnoreCase("money_format_top3"))
            return utils.getTopMoneyFormat(2);

        if(identifier.equalsIgnoreCase("name_top4"))
            return utils.getTopName(3);
        if(identifier.equalsIgnoreCase("money_top4"))
            return utils.getTopMoney(3);
        if(identifier.equalsIgnoreCase("money_format_top4"))
            return utils.getTopMoneyFormat(3);

        if(identifier.equalsIgnoreCase("name_top5"))
            return utils.getTopName(4);
        if(identifier.equalsIgnoreCase("money_top5"))
            return utils.getTopMoney(4);
        if(identifier.equalsIgnoreCase("money_format_top5"))
            return utils.getTopMoneyFormat(4);

        if(identifier.equalsIgnoreCase("name_top6"))
            return utils.getTopName(5);
        if(identifier.equalsIgnoreCase("money_top6"))
            return utils.getTopMoney(5);
        if(identifier.equalsIgnoreCase("money_format_top6"))
            return utils.getTopMoneyFormat(5);

        if(identifier.equalsIgnoreCase("name_top7"))
            return utils.getTopName(6);
        if(identifier.equalsIgnoreCase("money_top7"))
            return utils.getTopMoney(6);
        if(identifier.equalsIgnoreCase("money_format_top7"))
            return utils.getTopMoneyFormat(6);

        if(identifier.equalsIgnoreCase("name_top8"))
            return utils.getTopName(7);
        if(identifier.equalsIgnoreCase("money_top8"))
            return utils.getTopMoney(7);
        if(identifier.equalsIgnoreCase("money_format_top8"))
            return utils.getTopMoneyFormat(7);

        if(identifier.equalsIgnoreCase("name_top9"))
            return utils.getTopName(8);
        if(identifier.equalsIgnoreCase("money_top9"))
            return utils.getTopMoney(8);
        if(identifier.equalsIgnoreCase("money_format_top9"))
            return utils.getTopMoneyFormat(8);

        if(identifier.equalsIgnoreCase("name_top10"))
            return utils.getTopName(9);
        if(identifier.equalsIgnoreCase("money_top10"))
            return utils.getTopMoney(9);
        if(identifier.equalsIgnoreCase("money_format_top10"))
            return utils.getTopMoneyFormat(9);

        return null;
    }

}
