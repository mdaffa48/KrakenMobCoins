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

        if(identifier.equalsIgnoreCase("name_top1")) return this.getTopName(0);
        if(identifier.equalsIgnoreCase("money_top1")) return this.getTopMoney(0);
        if(identifier.equalsIgnoreCase("name_top2")) return this.getTopName(1);
        if(identifier.equalsIgnoreCase("money_top2")) return this.getTopMoney(1);
        if(identifier.equalsIgnoreCase("name_top3")) return this.getTopName(2);
        if(identifier.equalsIgnoreCase("money_top3")) return this.getTopMoney(2);
        if(identifier.equalsIgnoreCase("name_top4")) return this.getTopName(3);
        if(identifier.equalsIgnoreCase("money_top4")) return this.getTopMoney(3);
        if(identifier.equalsIgnoreCase("name_top5")) return this.getTopName(4);
        if(identifier.equalsIgnoreCase("money_top5")) return this.getTopMoney(4);
        if(identifier.equalsIgnoreCase("name_top6")) return this.getTopName(5);
        if(identifier.equalsIgnoreCase("money_top6")) return this.getTopMoney(5);
        if(identifier.equalsIgnoreCase("name_top7")) return this.getTopName(6);
        if(identifier.equalsIgnoreCase("money_top7")) return this.getTopMoney(6);
        if(identifier.equalsIgnoreCase("name_top8")) return this.getTopName(7);
        if(identifier.equalsIgnoreCase("money_top8")) return this.getTopMoney(7);
        if(identifier.equalsIgnoreCase("name_top9")) return this.getTopName(8);
        if(identifier.equalsIgnoreCase("money_top9")) return this.getTopMoney(8);
        if(identifier.equalsIgnoreCase("name_top10")) return this.getTopName(9);
        if(identifier.equalsIgnoreCase("money_top10")) return this.getTopMoney(9);

        return null;
    }

    private String getTopName(int index){

        FileConfiguration config = plugin.getConfig();
        String nameEmpty = config.getString("placeholders.top.nameIfEmpty");

        List<PlayerCoins> playerCoinsList = accountManager.getTop();

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return playerCoins.getPlayerName();
        } catch(IndexOutOfBoundsException exception){
            return nameEmpty;
        }

    }

    private String getTopMoney(int index){
        FileConfiguration config = plugin.getConfig();
        String moneyEmpty = config.getString("placeholders.top.moneyIfEmpty");

        List<PlayerCoins> playerCoinsList = accountManager.getTop();
        DecimalFormat df = plugin.getUtils().getDecimalFormat();

        try{
            PlayerCoins playerCoins = playerCoinsList.get(index);
            return df.format(playerCoins.getMoney());
        } catch(IndexOutOfBoundsException exception){
            return moneyEmpty;
        }

    }

}
