package me.aglerr.krakenmobcoins.configs;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public enum ConfigMessagesList {
    LEADERBOARD("messages.leaderboard"),
    HELP("messages.help"),
    HELP_ADMIN("messages.helpAdmin"),
    SALARY("options.salaryMode.messages");

    private final String configPath;
    private List<String> value = Arrays.asList("Not Loaded! Please contact administrator!");

    ConfigMessagesList(String configPath) { this.configPath = configPath; }

    public static void initialize(FileConfiguration config){
        for(ConfigMessagesList configMessagesList : ConfigMessagesList.values()){
            configMessagesList.value = config.getStringList(configMessagesList.configPath);
        }
    }

    public List<String> toStringList() { return this.value; }

}
