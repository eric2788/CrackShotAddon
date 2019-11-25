package com.ericlam.mc.crackshot.addon.main;

import com.ericlam.mc.crackshot.addon.ArmorDefender;
import com.ericlam.mc.crackshot.addon.CSAddonConfig;
import com.ericlam.mc.crackshot.addon.Tools;
import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.Set;

public class CSAddon extends JavaPlugin implements Listener {

    private static String statTrakTag;

    private static CSAddon plugin;

    public static String getStatTrakTag() {
        return statTrakTag;
    }

    public static CSDirector csDirector;

    private CSAddonConfig config;

    public static Set<ArmorDefender> getArmorSet() {
        return plugin.config.getArmorDefenderSet();
    }

    @Override
    public void onEnable() {
        plugin = this;
        config = new CSAddonConfig(this);
        this.saveDefaultConfig();
        this.reloadConfig();
        csDirector = (CSDirector) CSDirector.getProvidingPlugin(CSDirector.class);
        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        config.loadConfig();
        statTrakTag = this.getConfig().getString("statrak-tag");
    }


   private void sendMessage(String path, Player sender){
       FileConfiguration config = this.getConfig();
        boolean actionbar = config.getBoolean(path+".action-bar");
       String message = ChatColor.translateAlternateColorCodes('&', (actionbar ? "" : config.getString("prefix")) + config.getString(path + ".text"));
        sender.spigot().sendMessage(actionbar ? ChatMessageType.ACTION_BAR : ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
   }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("csa-reload")){

            if (!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "you are not player");
                return false;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("csa.reload")){
                sendMessage("messages.no-permission",player);
                return false;
            }

            this.reloadConfig();

            sendMessage("messages.reload-success",player);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onCrackShotDamage(WeaponDamageEntityEvent e){
        final double origninal_damage = e.getDamage();
        Player player = e.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (Tools.isNotOwner(gun, player)) {
            e.setCancelled(true);
            this.sendMessage("messages.not-owner",player);
            return;
        }
        if (!(e.getVictim() instanceof Player)) return;
        PlayerInventory inventory = ((Player) e.getVictim()).getInventory();
        Optional<ArmorDefender> defenderOptional = Tools.getDefender(gun);
        if (!defenderOptional.isPresent()) return;
        ArmorDefender defender = defenderOptional.get();
        ItemStack[] armors = inventory.getArmorContents();
        double finalDamage = origninal_damage;
        for (ItemStack armor : armors) {
            if (armor == null) continue;
            finalDamage -= defender.getDamage(armor.getType());
        }
        e.setDamage(finalDamage < 0 ? 0 : finalDamage);
    }

    @EventHandler
    public void onPreShoot(WeaponPrepareShootEvent e) {
        Player player = e.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (Tools.isNotOwner(gun, player)) {
            e.setCancelled(true);
            this.sendMessage("messages.not-owner",player);
        }
    }

    @EventHandler
    public void onAttachToggle(WeaponAttachmentToggleEvent e){
        Player player = e.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (Tools.isNotOwner(gun, player)) {
            e.setCancelled(true);
            this.sendMessage("messages.not-owner",player);
        }
    }

    @EventHandler
    public void onWeaponScope(WeaponScopeEvent e){
        Player player = e.getPlayer();
        ItemStack gun = player.getInventory().getItemInMainHand();
        if (Tools.isNotOwner(gun, player)) {
            e.setCancelled(true);
            this.sendMessage("messages.not-owner",player);
        }
    }

    @EventHandler
    public void onWeaponTrigger(WeaponTriggerEvent e){
        Player player = e.getPlayer();
        if (!player.isOnline()) return;
        ItemStack gun = player.getInventory().getItemInMainHand();
        if ( Tools.isNotOwner(gun, player)) {
            e.setCancelled(true);
            this.sendMessage("messages.not-owner",player);
        }
    }

}
