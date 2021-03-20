package me.aglerr.krakenmobcoins.shops;

import me.aglerr.krakenmobcoins.utils.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import fr.mrmicky.fastinv.FastInv;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.PlayerCoins;
import me.aglerr.krakenmobcoins.shops.category.mainmenu.MainMenuItems;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CategoryInventory extends FastInv {

    public CategoryInventory(int size, String title, Player player) {
        super(size, title);

        PlayerCoins playerCoins = MobCoins.getInstance().getPlayerCoins(player.getUniqueId().toString());
        Utils utils = MobCoins.getInstance().getUtils();

        for(MainMenuItems items : MobCoins.getInstance().getMainMenuLoader().getMainMenuItemsList()){

            List<String> lore = new ArrayList<>();
            for(String line : items.getLore()){
                lore.add(line.replace("%coins%", utils.getDFormat().format(playerCoins.getMoney())));
            }
            String category = items.getCategory();

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.getMaterial()).get().parseItem())
                    .name(items.getName())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);
            if(items.isGlow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            if(items.getSlots().isEmpty()){
                setItem(items.getSlot(), stack, event -> {
                    if(items.getType().equals("category")){
                        utils.openCategory(category, player);
                    }
                });
            } else {
                int[] slots1 = Ints.toArray(items.getSlots());
                setItems(slots1, stack, event -> {
                    if(items.getType().equals("category")){
                        utils.openCategory(category, player);
                    }
                });
            }

        }

    }

}
