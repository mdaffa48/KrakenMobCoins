package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.manager.DependencyManager;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class ConvertCommand extends SubCommand {

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

        final DependencyManager dependencyManager = plugin.getDependencyManager();
        if(dependencyManager.isSuperMobCoins()){

            sender.sendMessage(utils.color("%prefix% &aStarting to convert data from SuperMobCoins to KrakenMobCoins, please wait!")
                    .replace("%prefix%", utils.getPrefix()));
            convertSuperMobCoinsData(plugin);

        } else {
            sender.sendMessage(utils.color("%prefix% &cSuperMobCoins not found, please have it installed first!")
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

    private void convertSuperMobCoinsData(MobCoins plugin){
        Utils utils = plugin.getUtils();
        File file = new File(Bukkit.getPluginManager().getPlugin("SuperMobCoins").getDataFolder(), "profiles.yml");
        if(file.exists()){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                FileConfiguration profile = YamlConfiguration.loadConfiguration(file);
                for(String uuid : profile.getConfigurationSection("Profile").getKeys(false)){
                    String amount = String.valueOf(profile.getInt("Profile." + uuid + ".mobcoins"));

                    plugin.getAccountManager().createPlayerData(uuid, Double.parseDouble(amount));
                    plugin.getLogger().info("Converting " + uuid + " data.");

                }

                utils.sendConsoleMessage("Successfully converting all data!");
                utils.sendConsoleMessage("Please remove SuperMobCoins and restart the server to prevent any bugs!");
                utils.sendConsoleMessage("Thanks, and enjoy!");
            });

        }

    }

}
