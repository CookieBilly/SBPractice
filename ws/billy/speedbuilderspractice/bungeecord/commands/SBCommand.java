 

package ws.billy.speedbuilderspractice.bungeecord.commands;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.command.CommandExecutor;

public class SBCommand implements CommandExecutor
{
    private SpeedBuilders plugin;
    
    public SBCommand() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] array) {
        if (!this.plugin.getConfigManager().getConfig("config.yml").getBoolean("plugin.command-visible") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfig("spigot.yml").getString("messages.unknown-command")));
            return true;
        }
        if (array.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lSpeedBuilders v" + this.plugin.getDescription().getVersion()));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb reload &8- &7" + Translations.translate("HMENU-RELOAD")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb setup &8- &7" + Translations.translate("HMENU-SETUP_THE_GAME")));
            if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb stats &8- &7" + Translations.translate("HMENU-SHOW_STATS")));
            }
            return true;
        }
        if (array.length == 1) {
            if (array[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.reload")) {
                    this.plugin.getConfigManager().reloadConfig("config.yml");
                    this.plugin.getConfigManager().reloadConfig("lobby.yml");
                    this.plugin.getConfigManager().reloadConfig("maps.yml");
                    this.plugin.getConfigManager().reloadConfig("messages.yml");
                    this.plugin.getConfigManager().reloadConfig("templates.yml");
                    this.plugin.getConfigManager().reloadConfig("spigot.yml");
                    for (final String s2 : this.plugin.getConfigManager().getConfig("messages.yml").getConfigurationSection("BUNGEECORD").getKeys(false)) {
                        Translations.messages.put(s2, this.plugin.getConfigManager().getConfig("messages.yml").getString("BUNGEECORD." + s2));
                    }
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Configuration files and messages are successfully reloaded."));
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("setup")) {
                if (commandSender instanceof Player) {
                    if (commandSender.hasPermission("sb.command.setup")) {
                        final Player player = (Player)commandSender;
                        if (this.plugin.getBungeeCord().setup.containsKey(player.getName())) {
                            final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.CHEST, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"));
                            final ItemStack itemStack = Materials.GUARDIAN_SPAWN_EGG.getItemStack(1);
                            final ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§r§fSet spawnpoint location for guardian plot");
                            itemStack.setItemMeta(itemMeta);
                            final ItemStack itemStack2 = Materials.PAPER.getItemStack(1);
                            final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                            itemMeta2.setDisplayName("§r§fSet template area positions for guardian plot");
                            itemStack2.setItemMeta(itemMeta2);
                            final ItemStack itemStack3 = Materials.WHITE_BED.getItemStack(1);
                            final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                            itemMeta3.setDisplayName("§r§fSet spawnpoint location for each player plot");
                            itemStack3.setItemMeta(itemMeta3);
                            final ItemStack itemStack4 = Materials.WOODEN_AXE.getItemStack(1);
                            final ItemMeta itemMeta4 = itemStack4.getItemMeta();
                            itemMeta4.setDisplayName("§r§fSet plot area positions for each player plot");
                            itemStack4.setItemMeta(itemMeta4);
                            final ItemStack itemStack5 = Materials.ARMOR_STAND.getItemStack(1);
                            final ItemMeta itemMeta5 = itemStack5.getItemMeta();
                            itemMeta5.setDisplayName("§r§fSet laser location for each player plot");
                            itemStack5.setItemMeta(itemMeta5);
                            final ItemStack itemStack6 = Materials.OAK_FENCE.getItemStack(1);
                            final ItemMeta itemMeta6 = itemStack6.getItemMeta();
                            itemMeta6.setDisplayName("§r§fSet build area positions for each player plot");
                            itemStack6.setItemMeta(itemMeta6);
                            final ItemStack itemStack7 = Materials.WRITABLE_BOOK.getItemStack(1);
                            final ItemMeta itemMeta7 = itemStack7.getItemMeta();
                            itemMeta7.setDisplayName("§r§fAdd build template(s)");
                            itemStack7.setItemMeta(itemMeta7);
                            final ItemStack itemStack8 = Materials.CLOCK.getItemStack(1);
                            final ItemMeta itemMeta8 = itemStack8.getItemMeta();
                            itemMeta8.setDisplayName("§r§fReturn to lobby");
                            itemStack8.setItemMeta(itemMeta8);
                            inventory.addItem(new ItemStack[] { itemStack, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6, itemStack7 });
                            inventory.setItem(22, itemStack8);
                            player.openInventory(inventory);
                        }
                        else {
                            final Inventory inventory2 = Bukkit.createInventory((InventoryHolder)player, 9, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"));
                            final ItemStack itemStack9 = Materials.NETHER_STAR.getItemStack(1);
                            final ItemMeta itemMeta9 = itemStack9.getItemMeta();
                            itemMeta9.setDisplayName("§r§fSet spawnpoint location for main lobby");
                            itemStack9.setItemMeta(itemMeta9);
                            final ItemStack itemStack10 = Materials.PODZOL.getItemStack(1);
                            final ItemMeta itemMeta10 = itemStack10.getItemMeta();
                            itemMeta10.setDisplayName("§r§fEdit map");
                            itemStack10.setItemMeta(itemMeta10);
                            inventory2.setItem(0, itemStack9);
                            inventory2.setItem(8, itemStack10);
                            player.openInventory(inventory2);
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("stats")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lSpeedBuilders v" + this.plugin.getDescription().getVersion()));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb stats show (playerName)"));
                return true;
            }
        }
        else if (array.length == 2) {
            if (array[0].equalsIgnoreCase("stats") && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled") && array[1].equalsIgnoreCase("show")) {
                if (commandSender instanceof Player) {
                    final Player player2 = (Player)commandSender;
                    this.plugin.getStatsManager().showStats(player2, player2);
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
        }
        else if (array.length == 3) {
            if (array[0].equalsIgnoreCase("setup")) {
                if (array[1].equalsIgnoreCase("spawnpoint")) {
                    if (commandSender instanceof Player) {
                        if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                            final Player player3 = (Player)commandSender;
                            if (this.plugin.getBungeeCord().setup.containsKey(player3.getName())) {
                                if (this.plugin.getBungeeCord().location1 != null) {
                                    final double d = this.plugin.getBungeeCord().location1.getBlockX() + 0.5;
                                    final double d2 = this.plugin.getBungeeCord().location1.getBlockY() + 1;
                                    final double d3 = this.plugin.getBungeeCord().location1.getBlockZ() + 0.5;
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player3.getName()) + ".plots." + array[2] + ".spawnpoint.x", (Object)d);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player3.getName()) + ".plots." + array[2] + ".spawnpoint.y", (Object)d2);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player3.getName()) + ".plots." + array[2] + ".spawnpoint.z", (Object)d3);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player3.getName()) + ".plots." + array[2] + ".spawnpoint.pitch", (Object)0.0);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player3.getName()) + ".plots." + array[2] + ".spawnpoint.yaw", (Object)0.0);
                                    new BukkitRunnable() {
                                        public void run() {
                                            SBCommand.this.plugin.getConfigManager().saveConfig("maps.yml");
                                        }
                                    }.runTaskAsynchronously((Plugin)this.plugin);
                                    player3.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Plot spawnpoint location is successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                    this.plugin.getBungeeCord().location1 = null;
                                }
                                else {
                                    player3.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for the plot spawnpoint does not exist."));
                                }
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                    }
                }
                else if (array[1].equalsIgnoreCase("plot-area")) {
                    if (commandSender instanceof Player) {
                        if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                            final Player player4 = (Player)commandSender;
                            if (this.plugin.getBungeeCord().setup.containsKey(player4.getName())) {
                                if (this.plugin.getBungeeCord().location2 != null && this.plugin.getBungeeCord().location3 != null) {
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.x1", (Object)this.plugin.getBungeeCord().location2.getBlockX());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.y1", (Object)this.plugin.getBungeeCord().location2.getBlockY());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.z1", (Object)this.plugin.getBungeeCord().location2.getBlockZ());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.x2", (Object)this.plugin.getBungeeCord().location3.getBlockX());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.y2", (Object)this.plugin.getBungeeCord().location3.getBlockY());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player4.getName()) + ".plots." + array[2] + ".area.z2", (Object)this.plugin.getBungeeCord().location3.getBlockZ());
                                    new BukkitRunnable() {
                                        public void run() {
                                            SBCommand.this.plugin.getConfigManager().saveConfig("maps.yml");
                                        }
                                    }.runTaskAsynchronously((Plugin)this.plugin);
                                    player4.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Plot area positions are successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                    this.plugin.getBungeeCord().location2 = null;
                                    this.plugin.getBungeeCord().location3 = null;
                                }
                                else {
                                    player4.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for one of the plot area positions does not exist."));
                                }
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                    }
                }
                else if (array[1].equalsIgnoreCase("laser")) {
                    if (commandSender instanceof Player) {
                        if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                            final Player player5 = (Player)commandSender;
                            if (this.plugin.getBungeeCord().setup.containsKey(player5.getName())) {
                                if (this.plugin.getBungeeCord().location1 != null) {
                                    final double d4 = this.plugin.getBungeeCord().location1.getBlockX() + 0.5;
                                    final double d5 = this.plugin.getBungeeCord().location1.getBlockY();
                                    final double d6 = this.plugin.getBungeeCord().location1.getBlockZ() + 0.5;
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player5.getName()) + ".plots." + array[2] + ".laser-beam.x", (Object)d4);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player5.getName()) + ".plots." + array[2] + ".laser-beam.y", (Object)d5);
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player5.getName()) + ".plots." + array[2] + ".laser-beam.z", (Object)d6);
                                    new BukkitRunnable() {
                                        public void run() {
                                            SBCommand.this.plugin.getConfigManager().saveConfig("maps.yml");
                                        }
                                    }.runTaskAsynchronously((Plugin)this.plugin);
                                    player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Laser spawnpoint location is successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                    this.plugin.getBungeeCord().location1 = null;
                                }
                                else {
                                    player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for the laser spawnpoint does not exist."));
                                }
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                    }
                }
                else if (array[1].equalsIgnoreCase("build-area")) {
                    if (commandSender instanceof Player) {
                        if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                            final Player player6 = (Player)commandSender;
                            if (this.plugin.getBungeeCord().setup.containsKey(player6.getName())) {
                                if (this.plugin.getBungeeCord().location2 != null && this.plugin.getBungeeCord().location3 != null) {
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.x1", (Object)this.plugin.getBungeeCord().location2.getBlockX());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.y1", (Object)this.plugin.getBungeeCord().location2.getBlockY());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.z1", (Object)this.plugin.getBungeeCord().location2.getBlockZ());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.x2", (Object)this.plugin.getBungeeCord().location3.getBlockX());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.y2", (Object)this.plugin.getBungeeCord().location3.getBlockY());
                                    this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + this.plugin.getBungeeCord().setup.get(player6.getName()) + ".plots." + array[2] + ".build-area.z2", (Object)this.plugin.getBungeeCord().location3.getBlockZ());
                                    new BukkitRunnable() {
                                        public void run() {
                                            SBCommand.this.plugin.getConfigManager().saveConfig("maps.yml");
                                        }
                                    }.runTaskAsynchronously((Plugin)this.plugin);
                                    player6.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Build area positions are successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                    this.plugin.getBungeeCord().location2 = null;
                                    this.plugin.getBungeeCord().location3 = null;
                                }
                                else {
                                    player6.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for one of the build area positions does not exist."));
                                }
                            }
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                    }
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("stats") && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled") && array[1].equalsIgnoreCase("show")) {
                if (commandSender instanceof Player) {
                    final Player player7 = (Player)commandSender;
                    if (Bukkit.getPlayer(array[2]) != null) {
                        this.plugin.getStatsManager().showStats(player7, Bukkit.getPlayer(array[2]));
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("STATS-PLAYER_IS_NOT_ONLINE").replaceAll("%PLAYER%", array[2])));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
        }
        else if (array.length >= 4 && array[0].equalsIgnoreCase("setup")) {
            if (array[1].equalsIgnoreCase("template")) {
                if (commandSender instanceof Player) {
                    if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                        final Player player8 = (Player)commandSender;
                        if (this.plugin.getBungeeCord().setup.containsKey(player8.getName())) {
                            if (this.plugin.getBungeeCord().blocks.containsKey(player8.getName())) {
                                final String s3 = array[2];
                                String string = "";
                                for (int i = 3; i < array.length; ++i) {
                                    string = string + " " + array[i];
                                }
                                this.plugin.getBungeeCord().getTemplateManager().saveTemplate(s3, string.replaceFirst(" ", ""), this.plugin.getBungeeCord().blocks.get(player8.getName()));
                                player8.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Blocks for template are successfully saved into configuration file."));
                                this.plugin.getBungeeCord().blocks.remove(player8.getName());
                            }
                            else {
                                player8.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Blocks for the template does not exist."));
                            }
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
            }
            return true;
        }
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lSpeedBuilders v" + this.plugin.getDescription().getVersion()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb reload &8- &7" + Translations.translate("HMENU-RELOAD")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb setup &8- &7" + Translations.translate("HMENU-SETUP_THE_GAME")));
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb stats &8- &7" + Translations.translate("HMENU-SHOW_STATS")));
        }
        return true;
    }
}
