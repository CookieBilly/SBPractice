 

package ws.billy.speedbuilderspractice.multiworld.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class SetupListener2 implements Listener
{
    private SpeedBuilders plugin;
    
    public SetupListener2() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final Action action = playerInteractEvent.getAction();
        if (!player.hasPermission("sb.command.setup")) {
            return;
        }
        if (!player.getItemInHand().hasItemMeta()) {
            return;
        }
        if (!player.getItemInHand().getItemMeta().hasDisplayName()) {
            return;
        }
        if (!player.getItemInHand().getItemMeta().hasLore()) {
            return;
        }
        if (!player.getItemInHand().getItemMeta().getLore().contains("§3Setup")) {
            return;
        }
        if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set spawnpoint location for main lobby")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                final double d = playerInteractEvent.getClickedBlock().getLocation().getBlockX() + 0.5;
                final double d2 = playerInteractEvent.getClickedBlock().getLocation().getBlockY() + 1;
                final double d3 = playerInteractEvent.getClickedBlock().getLocation().getBlockZ() + 0.5;
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.world", (Object)playerInteractEvent.getClickedBlock().getLocation().getWorld().getName());
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.x", (Object)d);
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.y", (Object)d2);
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.z", (Object)d3);
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.pitch", (Object)0.0);
                this.plugin.getConfigManager().getConfig("lobby.yml").set("lobby.spawn.yaw", (Object)0.0);
                new BukkitRunnable() {
                    public void run() {
                        SetupListener2.this.plugin.getConfigManager().saveConfig("lobby.yml");
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Spawnpoint location is successfully saved into configuration file for &fmain &7lobby!"));
            }
            return;
        }
        if (!this.plugin.getMultiWorld().setup.containsKey(player.getName())) {
            return;
        }
        if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set spawnpoint location for arena lobby")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                final String s = this.plugin.getMultiWorld().setup.get(player.getName());
                final double d4 = playerInteractEvent.getClickedBlock().getLocation().getBlockX() + 0.5;
                final double d5 = playerInteractEvent.getClickedBlock().getLocation().getBlockY() + 1;
                final double d6 = playerInteractEvent.getClickedBlock().getLocation().getBlockZ() + 0.5;
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.world", (Object)playerInteractEvent.getClickedBlock().getLocation().getWorld().getName());
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.x", (Object)d4);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.y", (Object)d5);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.z", (Object)d6);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.pitch", (Object)0.0);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s + ".lobby.spawn.yaw", (Object)0.0);
                new BukkitRunnable() {
                    public void run() {
                        SetupListener2.this.plugin.getConfigManager().saveConfig("arenas.yml");
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Spawnpoint location is successfully saved into configuration file for &farena &7lobby!"));
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set spawnpoint location for guardian plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                final String s2 = this.plugin.getMultiWorld().setup.get(player.getName());
                final double d7 = playerInteractEvent.getClickedBlock().getLocation().getBlockX() + 0.5;
                final double d8 = playerInteractEvent.getClickedBlock().getLocation().getBlockY() + 10;
                final double d9 = playerInteractEvent.getClickedBlock().getLocation().getBlockZ() + 0.5;
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.world", (Object)playerInteractEvent.getClickedBlock().getLocation().getWorld().getName());
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.x", (Object)d7);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.y", (Object)d8);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.z", (Object)d9);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.pitch", (Object)0.0);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + s2 + ".plots.guardian.spawnpoint.yaw", (Object)0.0);
                new BukkitRunnable() {
                    public void run() {
                        SetupListener2.this.plugin.getConfigManager().saveConfig("arenas.yml");
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Spawnpoint location is successfully saved into configuration file for &fguardian &7plot!"));
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set template area positions for guardian plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                final String str = this.plugin.getMultiWorld().setup.get(player.getName());
                final double d10 = playerInteractEvent.getClickedBlock().getLocation().getBlockX();
                final double d11 = playerInteractEvent.getClickedBlock().getLocation().getBlockY();
                final double d12 = playerInteractEvent.getClickedBlock().getLocation().getBlockZ();
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str + ".plots.guardian.template-area.x1", (Object)d10);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str + ".plots.guardian.template-area.y1", (Object)d11);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str + ".plots.guardian.template-area.z1", (Object)d12);
                new BukkitRunnable() {
                    public void run() {
                        SetupListener2.this.plugin.getConfigManager().saveConfig("arenas.yml");
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for template area position 1 was successfully saved into configuration file for &fguardian &7plot!"));
            }
            else if (action == Action.RIGHT_CLICK_BLOCK) {
                final String str2 = this.plugin.getMultiWorld().setup.get(player.getName());
                final double d13 = playerInteractEvent.getClickedBlock().getLocation().getBlockX();
                final double d14 = playerInteractEvent.getClickedBlock().getLocation().getBlockY();
                final double d15 = playerInteractEvent.getClickedBlock().getLocation().getBlockZ();
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str2 + ".plots.guardian.template-area.x2", (Object)d13);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str2 + ".plots.guardian.template-area.y2", (Object)d14);
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str2 + ".plots.guardian.template-area.z2", (Object)d15);
                new BukkitRunnable() {
                    public void run() {
                        SetupListener2.this.plugin.getConfigManager().saveConfig("arenas.yml");
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for template area position 2 was successfully saved into configuration file for &fguardian &7plot!"));
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set spawnpoint location for each player plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location1 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for plot spawnpoint was successfully saved into cache."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup spawnpoint <plot> &7to save the plot spawnpoint into configuration file."));
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set plot area positions for each player plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location2 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for plot area position 1 was successfully saved into cache."));
                if (this.plugin.getMultiWorld().location3 != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup plot-area <plot> &7to save both plot area positions into configuration file."));
                }
            }
            else if (action == Action.RIGHT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location3 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for plot area position 2 was successfully saved into cache."));
                if (this.plugin.getMultiWorld().location2 != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup plot-area <plot> &7to save both plot area positions into configuration file."));
                }
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set laser location for each player plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location1 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for laser spawnpoint was successfully saved into cache."));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup laser <plot> &7to save the laser spawnpoint into configuration file."));
            }
        }
        else if (player.getItemInHand().getItemMeta().getDisplayName().equals("Set build area positions for each player plot")) {
            playerInteractEvent.setCancelled(true);
            if (action == Action.LEFT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location2 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for build area position 1 was successfully saved into cache."));
                if (this.plugin.getMultiWorld().location3 != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup build-area <plot> &7to save both build area positions into configuration file."));
                }
            }
            else if (action == Action.RIGHT_CLICK_BLOCK) {
                this.plugin.getMultiWorld().location3 = playerInteractEvent.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for build area position 2 was successfully saved into cache."));
                if (this.plugin.getMultiWorld().location2 != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup build-area <plot> &7to save both build area positions into configuration file."));
                }
            }
        }
    }
}
