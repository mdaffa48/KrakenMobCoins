package me.aglerr.krakenmobcoins.commands.subcommands;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.abstraction.SubCommand;
import me.aglerr.krakenmobcoins.configs.ConfigMessages;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CategoryCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if(args.length < 3){
            sender.sendMessage(utils.color("&cUsage: /mobcoins category add|remove|open <category>"));
            return;
        }

        String argument = args[1];
        String category = args[2];
        File file = this.getCategory(plugin, category);

        switch(argument.toLowerCase()){
            case "add": {
                if(!file.exists()){
                    this.createCategory(file);
                    utils.exampleShop(file);

                    FileConfiguration categoryConfig = YamlConfiguration.loadConfiguration(file);
                    plugin.getCategoryManager().addCategory(file.getName(), categoryConfig);
                    sender.sendMessage(utils.color(ConfigMessages.CREATE_CATEGORY.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%category%", category));

                } else {

                    sender.sendMessage(utils.color(ConfigMessages.CATEGORY_ALREADY_EXIST.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%category%", category));

                }
                break;
            }

            case "remove": {
                if(file.exists()){
                    file.delete();
                    sender.sendMessage(utils.color(ConfigMessages.REMOVE_CATEGORY.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%category%", category));

                } else {

                    sender.sendMessage(utils.color(ConfigMessages.CATEGORY_NOT_EXIST.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%category%", category));

                }
                break;
            }

            case "open": {
                if(args.length == 3){
                    if(sender instanceof Player){
                        utils.openCategory(category, (Player) sender);
                    } else {
                        sender.sendMessage(utils.color("&cUsage: /mobcoins category open <category> [player]"));
                    }
                    break;
                }

                if(args.length == 4){
                    Player target = Bukkit.getPlayer(args[3]);
                    if(target == null){
                        sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));

                    } else {
                        utils.openCategory(category, target);
                    }
                    break;
                }

            }

            default: {
                sender.sendMessage(utils.color("&cUsage: /mobcoins category add|remove|open <category>"));
                break;
            }

        }

    }

    private File getCategory(MobCoins plugin, String category){
        return new File(plugin.getDataFolder() + File.separator + "categories", category + ".yml");
    }

    private void createCategory(File file){
        try{
            file.createNewFile();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
