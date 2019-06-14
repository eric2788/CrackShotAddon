package com.ericlam.mc.crackshot.addon;

import com.ericlam.mc.crackshot.addon.main.CSAddon;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CSAddonConfig {

    private Set<ArmorDefender> armorDefenderSet = new HashSet<>();
    private File weaFile;

    public CSAddonConfig(CSAddon plugin){
        weaFile = new File(plugin.getDataFolder(), "weapons.yml");
        if (!weaFile.exists()) plugin.saveResource("weapons.yml",true);

    }

    public void loadConfig() {
        FileConfiguration weapons = YamlConfiguration.loadConfiguration(weaFile);
        armorDefenderSet.clear();
        for (String key : weapons.getKeys(false)) {
            String lore = ".*" + weapons.getString(key + ".lore").replaceAll("&[a-fA-F0-9]", "") + ".*";
            Map<Material,Double> defend = new HashMap<>();
            for (String line : weapons.getStringList(key + ".armors")) {
                String[] l = line.split(":");
                if (l.length < 2) continue;
                double damage = Double.parseDouble(l[1]);
                Material material = Material.valueOf(l[0]);
                defend.put(material,damage);
            }
            armorDefenderSet.add(new ArmorDefender(lore,defend));
        }
    }

    public Set<ArmorDefender> getArmorDefenderSet() {
        return armorDefenderSet;
    }
}
