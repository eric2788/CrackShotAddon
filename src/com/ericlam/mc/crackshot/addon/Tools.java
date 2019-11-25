package com.ericlam.mc.crackshot.addon;

import com.ericlam.mc.crackshot.addon.main.CSAddon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class Tools {

    public static boolean isNotOwner(ItemStack item, Player player) {
        String name = player.getName();
        String tag = CSAddon.getStatTrakTag().replace("<owner>", name);
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return true;
        return checkLore(item.getItemMeta().getLore(), tag);
    }

    public static Optional<String> getHoldingWeapon(Player player) {
        return Optional.ofNullable(CSAddon.csDirector.returnParentNode(player));
    }

    private static String getPlainText(String str){
        return str.replaceAll("§[a-oA-O0-9rR]", "");
    }

    private static boolean checkLore(List<String> lore, String match){
        return lore.stream().noneMatch(l -> getPlainText(l).equalsIgnoreCase(getPlainText(match)));
    }

    public static Optional<ArmorDefender> getDefender(ItemStack item) {
        if (item.getItemMeta() == null) return Optional.empty();
        ArmorDefender armorDefender = null;
        List<String> lores = item.getItemMeta().getLore();
        if (lores == null) return Optional.empty();
        main:
        for (String line : lores) {
            for (ArmorDefender defender : CSAddon.getArmorSet()) {
                if (getPlainText(line).matches(getPlainText(defender.getLore()))) {
                    armorDefender = defender;
                    break main;
                }
            }
        }
        return Optional.ofNullable(armorDefender);
    }
}
