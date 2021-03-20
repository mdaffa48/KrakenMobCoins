package me.aglerr.krakenmobcoins.configs;

import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigMessages {
    PREFIX("messages.prefix"),
    NO_PERMISSION("messages.noPermission"),
    RELOAD("messages.reload"),
    TARGET_NOT_FOUND("messages.targetNotFound"),
    NO_ACCOUNT("messages.noAccount"),
    NO_ACCOUNT_OTHERS("messages.noAccountOthers"),
    BALANCE("messages.balance"),
    BALANCE_OTHERS("messages.balanceOthers"),
    ONLY_PLAYER("messages.onlyPlayer"),
    NOT_INTEGER("messages.notInt"),
    SEND_COINS("messages.sendCoins"),
    RECEIVED_COINS("messages.receivedCoins"),
    NOT_ENOUGH_COINS("messages.notEnoughCoins"),
    WITHDRAW("messages.withdraw"),
    TOGGLE_ON("messages.toggleOn"),
    TOGGLE_OFF("messages.toggleOff"),
    ADD_COINS("messages.addCoins"),
    TARGET_ADD_COINS("messages.targetAddCoins"),
    REMOVE_COINS("messages.removeCoins"),
    TARGET_REMOVE_COINS("messages.targetRemoveCoins"),
    SET_COINS("messages.setCoins"),
    TARGET_SET_COINS("messages.targetSetCoins"),
    NEGATIVE_AMOUNT("messages.negativeAmount"),
    REDEEM("messages.redeem"),
    INVENTORY_FULL("messages.inventoryFull"),
    PURCHASED_ITEM("messages.purchasedItem"),
    OPENED_SHOP_MENU("messages.openedShopMenu"),
    REFRESH("messages.refresh"),
    MAX_LIMIT("messages.maxLimit"),
    CREATE_CATEGORY("messages.createCategory"),
    CATEGORY_NOT_EXIST("messages.categoryNotExist"),
    CATEGORY_ALREADY_EXIST("messages.categoryAlreadyExist"),
    REMOVE_CATEGORY("messages.removeCategory"),
    OUT_OF_STOCK("messages.outOfStock");

    private final String configPath;
    private String value = "Not Loaded! Please contact administrator!";

    ConfigMessages(String configPath) {
        this.configPath = configPath;
    }

    public static void initialize(FileConfiguration config){
        for(ConfigMessages configMessages : ConfigMessages.values()){
            configMessages.value = config.getString(configMessages.configPath);
        }
    }

    @Override
    public String toString() { return this.value; }

}
