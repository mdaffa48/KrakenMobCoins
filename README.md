# KrakenMobCoins
KrakenMobCoins is a virtual custom-currency plugin, every aspect on this plugin are configurable to your liking, you can configure drop chances, and amount of mobcoins to be dropped each mobs with decimals supports, and also with friendly Developer APIs. For the full feature, check the list below!

# API Usage
Example Usage:
```java
public class ExampleAPIUsage {
  
  public double getPlayerCoins(Player player){
    MobCoinsAPI api = MobCoins.getAPI();
    // PlayerCoins is nullable, so you want to nullcheck it first.
    PlayerCoins playerCoins = api.getPlayerData(player);
    
    return playerCoins.getMoney();
  }
  
}
```
Check this class for full method https://github.com/lilsketchy/KrakenMobCoins/blob/master/src/main/java/me/aglerr/krakenmobcoins/database/PlayerCoins.java

# Useful Links
<ul>
<li>SpigotMC Resource: https://www.spigotmc.org/resources/krakenmobcoins-1-8-1-16-the-most-feature-rich-mobcoins-plugin-with-decimals-support.88972/</li>
<li>MC-Market Resource: https://www.mc-market.org/resources/19018/</li>
</ul>


