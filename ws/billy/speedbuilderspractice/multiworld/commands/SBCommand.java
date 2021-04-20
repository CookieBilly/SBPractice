 

package ws.billy.speedbuilderspractice.multiworld.commands;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Objective;
import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import java.util.Random;
import java.util.ArrayList;
import ws.billy.speedbuilderspractice.utils.GameState;
import java.io.FilenameFilter;
import java.io.File;
import ws.billy.speedbuilderspractice.multiworld.Arena;
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
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb arena &8- &7" + Translations.translate("HMENU-MANAGE_ARENAS")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb autojoin &8- &7" + Translations.translate("HMENU-AUTOJOIN_ARENA")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb join <arenaName> &8- &7" + Translations.translate("HMENU-JOIN_ARENA")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb leave &8- &7" + Translations.translate("HMENU-LEAVE_ARENA")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb reload &8- &7" + Translations.translate("HMENU-RELOAD")));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb setup &8- &7" + Translations.translate("HMENU-SETUP_THE_GAME")));
            if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb stats &8- &7" + Translations.translate("HMENU-SHOW_STATS")));
            }
            return true;
        }
        if (array.length == 1) {
            if (array[0].equalsIgnoreCase("arena")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lSpeedBuilders v" + this.plugin.getDescription().getVersion()));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb arena create <arenaName> <startTime> <gameStartTime> <showCaseTime> <buildTime> <judgeTime> <neededPlayers>"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb arena delete <arenaName>"));
                return true;
            }
            if (array[0].equalsIgnoreCase("autojoin")) {
                if (commandSender instanceof Player) {
                    final Player player = (Player)commandSender;
                    boolean b = false;
                    final Iterator<Arena> iterator = Arena.arenaObjects.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().getPlayers().contains(player.getName())) {
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        boolean b2 = false;
                        for (final Arena arena : Arena.arenaObjects) {
                            final String[] list = new File(".").list(new FilenameFilter() {
                                @Override
                                public boolean accept(final File parent, final String child) {
                                    return new File(parent, child).isDirectory();
                                }
                            });
                            boolean b3 = false;
                            final String[] array2 = list;
                            for (int length = array2.length, i = 0; i < length; ++i) {
                                if (array2[i].equals(arena.getName())) {
                                    b3 = true;
                                    break;
                                }
                            }
                            if (b3 && (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING)) {
                                if (arena.getPlayers().size() < arena.getMaxPlayers()) {
                                    b2 = true;
                                    this.plugin.getMultiWorld().saveTempInfo(player);
                                    this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena.getName());
                                    break;
                                }
                                if (player.hasPermission("sb.server.joinfullgame")) {
                                    this.plugin.getMultiWorld().saveTempInfo(player);
                                    this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena.getName());
                                    final ArrayList<Player> list2 = new ArrayList<Player>();
                                    for (final String s2 : arena.getPlayers()) {
                                        if (Bukkit.getPlayer(s2) != player && !Bukkit.getPlayer(s2).hasPermission("sb.server.joinfullgame")) {
                                            list2.add(Bukkit.getPlayer(s2));
                                        }
                                    }
                                    if (!list2.isEmpty()) {
                                        final Player player2 = list2.get(new Random().nextInt(list2.size()));
                                        arena.getPlayers().remove(player2.getName());
                                        if (arena.getGameState() == GameState.WAITING) {
                                            for (final String s3 : arena.getPlayers()) {
                                                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName()) != null) {
                                                    final Scoreboard scoreboard = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName());
                                                    final Objective objective = scoreboard.getObjective("SpeedBuilders");
                                                    final Iterator iterator5 = scoreboard.getEntries().iterator();
                                                    while (iterator5.hasNext()) {
                                                        scoreboard.resetScores((String)iterator5.next());
                                                    }
                                                    if (arena.getGameState() == GameState.WAITING) {
                                                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                    }
                                                    if (arena.getGameState() == GameState.STARTING) {
                                                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena.getStartTime()))));
                                                    }
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), scoreboard)).setScore(4);
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                                                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), scoreboard)).setScore(1);
                                                    Bukkit.getPlayer(s3).setScoreboard(scoreboard);
                                                }
                                                else {
                                                    final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                                                    final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                                                    if (arena.getGameState() == GameState.WAITING) {
                                                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                    }
                                                    if (arena.getGameState() == GameState.STARTING) {
                                                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena.getStartTime()))));
                                                    }
                                                    registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), newScoreboard)).setScore(4);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                                                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), newScoreboard)).setScore(1);
                                                    Bukkit.getPlayer(s3).setScoreboard(newScoreboard);
                                                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s3).getName(), newScoreboard);
                                                }
                                            }
                                            arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)player2).removePlayer((OfflinePlayer)player2);
                                            this.plugin.getMultiWorld().getKitManager().setKit(player2, null, arena.getName());
                                            if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                player2.teleport(location);
                                            }
                                            else {
                                                player2.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                            }
                                            player2.getInventory().setArmorContents((ItemStack[])null);
                                            player2.getInventory().clear();
                                            player2.setAllowFlight(false);
                                            player2.setExp(0.0f);
                                            player2.setFireTicks(0);
                                            player2.setFlying(false);
                                            player2.setFoodLevel(20);
                                            player2.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                            player2.setHealth(20.0);
                                            player2.setLevel(0);
                                            final Iterator iterator6 = player2.getActivePotionEffects().iterator();
                                            while (iterator6.hasNext()) {
                                                player2.removePotionEffect(iterator6.next().getType());
                                            }
                                            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player2.getName())) {
                                                this.plugin.getMultiWorld().loadTempInfo(player2);
                                            }
                                            player2.updateInventory();
                                        }
                                        else if (arena.getGameState() == GameState.STARTING) {
                                            for (final String s4 : arena.getPlayers()) {
                                                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s4).getName()) != null) {
                                                    final Scoreboard scoreboard2 = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s4).getName());
                                                    final Objective objective2 = scoreboard2.getObjective("SpeedBuilders");
                                                    final Iterator iterator8 = scoreboard2.getEntries().iterator();
                                                    while (iterator8.hasNext()) {
                                                        scoreboard2.resetScores((String)iterator8.next());
                                                    }
                                                    if (arena.getGameState() == GameState.WAITING) {
                                                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                    }
                                                    if (arena.getGameState() == GameState.STARTING) {
                                                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena.getStartTime()))));
                                                    }
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard2)).setScore(6);
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard2)).setScore(5);
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), scoreboard2)).setScore(4);
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard2)).setScore(3);
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard2)).setScore(2);
                                                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s4), arena.getName()).toUpperCase())), scoreboard2)).setScore(1);
                                                    Bukkit.getPlayer(s4).setScoreboard(scoreboard2);
                                                }
                                                else {
                                                    final Scoreboard newScoreboard2 = Bukkit.getScoreboardManager().getNewScoreboard();
                                                    final Objective registerNewObjective2 = newScoreboard2.registerNewObjective("SpeedBuilders", "dummy");
                                                    if (arena.getGameState() == GameState.WAITING) {
                                                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                    }
                                                    if (arena.getGameState() == GameState.STARTING) {
                                                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena.getStartTime()))));
                                                    }
                                                    registerNewObjective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard2)).setScore(6);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard2)).setScore(5);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), newScoreboard2)).setScore(4);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard2)).setScore(3);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard2)).setScore(2);
                                                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s4), arena.getName()).toUpperCase())), newScoreboard2)).setScore(1);
                                                    Bukkit.getPlayer(s4).setScoreboard(newScoreboard2);
                                                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s4).getName(), newScoreboard2);
                                                }
                                            }
                                            arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)player2).removePlayer((OfflinePlayer)player2);
                                            this.plugin.getMultiWorld().getKitManager().setKit(player2, null, arena.getName());
                                            if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                final Location location2 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                location2.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                location2.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                player2.teleport(location2);
                                            }
                                            else {
                                                player2.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                            }
                                            player2.getInventory().setArmorContents((ItemStack[])null);
                                            player2.getInventory().clear();
                                            player2.setAllowFlight(false);
                                            player2.setExp(0.0f);
                                            player2.setFireTicks(0);
                                            player2.setFlying(false);
                                            player2.setFoodLevel(20);
                                            player2.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                            player2.setHealth(20.0);
                                            player2.setLevel(0);
                                            final Iterator iterator9 = player2.getActivePotionEffects().iterator();
                                            while (iterator9.hasNext()) {
                                                player2.removePotionEffect(iterator9.next().getType());
                                            }
                                            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player2.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player2.getName())) {
                                                this.plugin.getMultiWorld().loadTempInfo(player2);
                                            }
                                            player2.updateInventory();
                                        }
                                    }
                                    list2.clear();
                                    break;
                                }
                                continue;
                            }
                        }
                        if (!b2) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_JOINABLE_ARENAS")));
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYING")));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("leave")) {
                if (commandSender instanceof Player) {
                    final Player player3 = (Player)commandSender;
                    boolean b4 = false;
                    for (final Arena arena2 : Arena.arenaObjects) {
                        if (arena2.getPlayers().contains(player3.getName())) {
                            b4 = true;
                            this.plugin.getMultiWorld().getArenaManager().removePlayer(player3, arena2.getName());
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player3.getName())));
                            break;
                        }
                    }
                    if (!b4) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NOT_PLAYING")));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.reload")) {
                    this.plugin.getConfigManager().reloadConfig("arenas.yml");
                    this.plugin.getConfigManager().reloadConfig("config.yml");
                    this.plugin.getConfigManager().reloadConfig("lobby.yml");
                    this.plugin.getConfigManager().reloadConfig("messages.yml");
                    this.plugin.getConfigManager().reloadConfig("signs.yml");
                    this.plugin.getConfigManager().reloadConfig("templates.yml");
                    this.plugin.getConfigManager().reloadConfig("spigot.yml");
                    for (final String s5 : this.plugin.getConfigManager().getConfig("messages.yml").getConfigurationSection("MULTIWORLD").getKeys(false)) {
                        Translations.messages.put(s5, this.plugin.getConfigManager().getConfig("messages.yml").getString("MULTIWORLD." + s5));
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
                        final Player player4 = (Player)commandSender;
                        if (this.plugin.getMultiWorld().setup.containsKey(player4.getName())) {
                            final Inventory inventory = Bukkit.createInventory((InventoryHolder)player4, InventoryType.CHEST, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"));
                            final ItemStack itemStack = Materials.BEACON.getItemStack(1);
                            final ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§r§fSet spawnpoint location for arena lobby");
                            itemStack.setItemMeta(itemMeta);
                            final ItemStack itemStack2 = Materials.GUARDIAN_SPAWN_EGG.getItemStack(1);
                            final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                            itemMeta2.setDisplayName("§r§fSet spawnpoint location for guardian plot");
                            itemStack2.setItemMeta(itemMeta2);
                            final ItemStack itemStack3 = Materials.PAPER.getItemStack(1);
                            final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                            itemMeta3.setDisplayName("§r§fSet template area positions for guardian plot");
                            itemStack3.setItemMeta(itemMeta3);
                            final ItemStack itemStack4 = Materials.WHITE_BED.getItemStack(1);
                            final ItemMeta itemMeta4 = itemStack4.getItemMeta();
                            itemMeta4.setDisplayName("§r§fSet spawnpoint location for each player plot");
                            itemStack4.setItemMeta(itemMeta4);
                            final ItemStack itemStack5 = Materials.WOODEN_AXE.getItemStack(1);
                            final ItemMeta itemMeta5 = itemStack5.getItemMeta();
                            itemMeta5.setDisplayName("§r§fSet plot area positions for each player plot");
                            itemStack5.setItemMeta(itemMeta5);
                            final ItemStack itemStack6 = Materials.ARMOR_STAND.getItemStack(1);
                            final ItemMeta itemMeta6 = itemStack6.getItemMeta();
                            itemMeta6.setDisplayName("§r§fSet laser location for each player plot");
                            itemStack6.setItemMeta(itemMeta6);
                            final ItemStack itemStack7 = Materials.OAK_FENCE.getItemStack(1);
                            final ItemMeta itemMeta7 = itemStack7.getItemMeta();
                            itemMeta7.setDisplayName("§r§fSet build area positions for each player plot");
                            itemStack7.setItemMeta(itemMeta7);
                            final ItemStack itemStack8 = Materials.WRITABLE_BOOK.getItemStack(1);
                            final ItemMeta itemMeta8 = itemStack8.getItemMeta();
                            itemMeta8.setDisplayName("§r§fAdd build template(s)");
                            itemStack8.setItemMeta(itemMeta8);
                            final ItemStack itemStack9 = Materials.CLOCK.getItemStack(1);
                            final ItemMeta itemMeta9 = itemStack9.getItemMeta();
                            itemMeta9.setDisplayName("§r§fReturn to lobby");
                            itemStack9.setItemMeta(itemMeta9);
                            inventory.addItem(new ItemStack[] { itemStack, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6, itemStack7, itemStack8 });
                            inventory.setItem(22, itemStack9);
                            player4.openInventory(inventory);
                        }
                        else {
                            final Inventory inventory2 = Bukkit.createInventory((InventoryHolder)player4, 9, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"));
                            final ItemStack itemStack10 = Materials.NETHER_STAR.getItemStack(1);
                            final ItemMeta itemMeta10 = itemStack10.getItemMeta();
                            itemMeta10.setDisplayName("§r§fSet spawnpoint location for main lobby");
                            itemStack10.setItemMeta(itemMeta10);
                            final ItemStack itemStack11 = Materials.PODZOL.getItemStack(1);
                            final ItemMeta itemMeta11 = itemStack11.getItemMeta();
                            itemMeta11.setDisplayName("§r§fEdit arena");
                            itemStack11.setItemMeta(itemMeta11);
                            inventory2.setItem(0, itemStack10);
                            inventory2.setItem(8, itemStack11);
                            player4.openInventory(inventory2);
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
            if (array[0].equalsIgnoreCase("join")) {
                final Arena arena3 = this.plugin.getMultiWorld().getArenaManager().getArena(array[1]);
                if (arena3 != null) {
                    if (commandSender instanceof Player) {
                        final Player player5 = (Player)commandSender;
                        boolean b5 = false;
                        final Iterator<Arena> iterator12 = Arena.arenaObjects.iterator();
                        while (iterator12.hasNext()) {
                            if (iterator12.next().getPlayers().contains(player5.getName())) {
                                b5 = true;
                            }
                        }
                        if (!b5) {
                            final String[] list3 = new File(".").list(new FilenameFilter() {
                                @Override
                                public boolean accept(final File parent, final String child) {
                                    return new File(parent, child).isDirectory();
                                }
                            });
                            boolean b6 = false;
                            final String[] array3 = list3;
                            for (int length2 = array3.length, j = 0; j < length2; ++j) {
                                if (array3[j].equals(arena3.getName())) {
                                    b6 = true;
                                    break;
                                }
                            }
                            if (b6) {
                                if (arena3.getGameState() == GameState.WAITING || arena3.getGameState() == GameState.STARTING) {
                                    if (arena3.getPlayers().size() < arena3.getMaxPlayers()) {
                                        this.plugin.getMultiWorld().saveTempInfo(player5);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player5, arena3.getName());
                                    }
                                    else if (player5.hasPermission("sb.server.joinfullgame")) {
                                        this.plugin.getMultiWorld().saveTempInfo(player5);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player5, arena3.getName());
                                        final ArrayList<Player> list4 = new ArrayList<Player>();
                                        for (final String s6 : arena3.getPlayers()) {
                                            if (Bukkit.getPlayer(s6) != player5 && !Bukkit.getPlayer(s6).hasPermission("sb.server.joinfullgame")) {
                                                list4.add(Bukkit.getPlayer(s6));
                                            }
                                        }
                                        if (!list4.isEmpty()) {
                                            final Player player6 = list4.get(new Random().nextInt(list4.size()));
                                            arena3.getPlayers().remove(player6.getName());
                                            if (arena3.getGameState() == GameState.WAITING) {
                                                for (final String s7 : arena3.getPlayers()) {
                                                    if (arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s7).getName()) != null) {
                                                        final Scoreboard scoreboard3 = arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s7).getName());
                                                        final Objective objective3 = scoreboard3.getObjective("SpeedBuilders");
                                                        final Iterator iterator15 = scoreboard3.getEntries().iterator();
                                                        while (iterator15.hasNext()) {
                                                            scoreboard3.resetScores((String)iterator15.next());
                                                        }
                                                        if (arena3.getGameState() == GameState.WAITING) {
                                                            objective3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                        }
                                                        if (arena3.getGameState() == GameState.STARTING) {
                                                            objective3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena3.getStartTime()))));
                                                        }
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard3)).setScore(6);
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard3)).setScore(5);
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena3.getPlayers().size() + "/" + arena3.getMaxPlayers()), scoreboard3)).setScore(4);
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard3)).setScore(3);
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard3)).setScore(2);
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s7), arena3.getName()).toUpperCase())), scoreboard3)).setScore(1);
                                                        Bukkit.getPlayer(s7).setScoreboard(scoreboard3);
                                                    }
                                                    else {
                                                        final Scoreboard newScoreboard3 = Bukkit.getScoreboardManager().getNewScoreboard();
                                                        final Objective registerNewObjective3 = newScoreboard3.registerNewObjective("SpeedBuilders", "dummy");
                                                        if (arena3.getGameState() == GameState.WAITING) {
                                                            registerNewObjective3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                        }
                                                        if (arena3.getGameState() == GameState.STARTING) {
                                                            registerNewObjective3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena3.getStartTime()))));
                                                        }
                                                        registerNewObjective3.setDisplaySlot(DisplaySlot.SIDEBAR);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard3)).setScore(6);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard3)).setScore(5);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena3.getPlayers().size() + "/" + arena3.getMaxPlayers()), newScoreboard3)).setScore(4);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard3)).setScore(3);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard3)).setScore(2);
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s7), arena3.getName()).toUpperCase())), newScoreboard3)).setScore(1);
                                                        Bukkit.getPlayer(s7).setScoreboard(newScoreboard3);
                                                        arena3.getPlayerStartScoreboard().put(Bukkit.getPlayer(s7).getName(), newScoreboard3);
                                                    }
                                                }
                                                arena3.getGameScoreboard().getPlayerTeam((OfflinePlayer)player6).removePlayer((OfflinePlayer)player6);
                                                this.plugin.getMultiWorld().getKitManager().setKit(player6, null, arena3.getName());
                                                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                    final Location location3 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                    location3.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                    location3.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                    player6.teleport(location3);
                                                }
                                                else {
                                                    player6.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                                }
                                                player6.getInventory().setArmorContents((ItemStack[])null);
                                                player6.getInventory().clear();
                                                player6.setAllowFlight(false);
                                                player6.setExp(0.0f);
                                                player6.setFireTicks(0);
                                                player6.setFlying(false);
                                                player6.setFoodLevel(20);
                                                player6.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                                player6.setHealth(20.0);
                                                player6.setLevel(0);
                                                final Iterator iterator16 = player6.getActivePotionEffects().iterator();
                                                while (iterator16.hasNext()) {
                                                    player6.removePotionEffect(iterator16.next().getType());
                                                }
                                                if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player6.getName())) {
                                                    this.plugin.getMultiWorld().loadTempInfo(player6);
                                                }
                                                player6.updateInventory();
                                            }
                                            else if (arena3.getGameState() == GameState.STARTING) {
                                                for (final String s8 : arena3.getPlayers()) {
                                                    if (arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s8).getName()) != null) {
                                                        final Scoreboard scoreboard4 = arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s8).getName());
                                                        final Objective objective4 = scoreboard4.getObjective("SpeedBuilders");
                                                        final Iterator iterator18 = scoreboard4.getEntries().iterator();
                                                        while (iterator18.hasNext()) {
                                                            scoreboard4.resetScores((String)iterator18.next());
                                                        }
                                                        if (arena3.getGameState() == GameState.WAITING) {
                                                            objective4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                        }
                                                        if (arena3.getGameState() == GameState.STARTING) {
                                                            objective4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena3.getStartTime()))));
                                                        }
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard4)).setScore(6);
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard4)).setScore(5);
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena3.getPlayers().size() + "/" + arena3.getMaxPlayers()), scoreboard4)).setScore(4);
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard4)).setScore(3);
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard4)).setScore(2);
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s8), arena3.getName()).toUpperCase())), scoreboard4)).setScore(1);
                                                        Bukkit.getPlayer(s8).setScoreboard(scoreboard4);
                                                    }
                                                    else {
                                                        final Scoreboard newScoreboard4 = Bukkit.getScoreboardManager().getNewScoreboard();
                                                        final Objective registerNewObjective4 = newScoreboard4.registerNewObjective("SpeedBuilders", "dummy");
                                                        if (arena3.getGameState() == GameState.WAITING) {
                                                            registerNewObjective4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                                                        }
                                                        if (arena3.getGameState() == GameState.STARTING) {
                                                            registerNewObjective4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getMultiWorld().getTimerManager().timeString(arena3.getStartTime()))));
                                                        }
                                                        registerNewObjective4.setDisplaySlot(DisplaySlot.SIDEBAR);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard4)).setScore(6);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard4)).setScore(5);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena3.getPlayers().size() + "/" + arena3.getMaxPlayers()), newScoreboard4)).setScore(4);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard4)).setScore(3);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard4)).setScore(2);
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s8), arena3.getName()).toUpperCase())), newScoreboard4)).setScore(1);
                                                        Bukkit.getPlayer(s8).setScoreboard(newScoreboard4);
                                                        arena3.getPlayerStartScoreboard().put(Bukkit.getPlayer(s8).getName(), newScoreboard4);
                                                    }
                                                }
                                                arena3.getGameScoreboard().getPlayerTeam((OfflinePlayer)player6).removePlayer((OfflinePlayer)player6);
                                                this.plugin.getMultiWorld().getKitManager().setKit(player6, null, arena3.getName());
                                                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                    final Location location4 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                    location4.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                    location4.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                    player6.teleport(location4);
                                                }
                                                else {
                                                    player6.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                                }
                                                player6.getInventory().setArmorContents((ItemStack[])null);
                                                player6.getInventory().clear();
                                                player6.setAllowFlight(false);
                                                player6.setExp(0.0f);
                                                player6.setFireTicks(0);
                                                player6.setFlying(false);
                                                player6.setFoodLevel(20);
                                                player6.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                                player6.setHealth(20.0);
                                                player6.setLevel(0);
                                                final Iterator iterator19 = player6.getActivePotionEffects().iterator();
                                                while (iterator19.hasNext()) {
                                                    player6.removePotionEffect(iterator19.next().getType());
                                                }
                                                if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player6.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player6.getName())) {
                                                    this.plugin.getMultiWorld().loadTempInfo(player6);
                                                }
                                                player6.updateInventory();
                                            }
                                        }
                                        list4.clear();
                                    }
                                    else {
                                        player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-ARENA_IS_FULL")));
                                    }
                                }
                                else {
                                    player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-GAME_RUNNING")));
                                }
                            }
                            else {
                                player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Arena &f" + arena3.getName() + " &7is missing a world folder!"));
                                player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Shut down the server and upload the world to this server!"));
                            }
                        }
                        else {
                            player5.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYING")));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-ARENA_DOES_NOT_EXIST")));
                }
                return true;
            }
            if (array[0].equalsIgnoreCase("stats") && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled") && array[1].equalsIgnoreCase("show")) {
                if (commandSender instanceof Player) {
                    final Player player7 = (Player)commandSender;
                    this.plugin.getStatsManager().showStats(player7, player7);
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYER_COMMAND")));
                }
                return true;
            }
        }
        else if (array.length == 3) {
            if (array[0].equalsIgnoreCase("arena")) {
                if (array[1].equalsIgnoreCase("delete")) {
                    if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.arena.*") || commandSender.hasPermission("sb.command.arena.delete")) {
                        this.plugin.getMultiWorld().getArenaManager().deleteArena(array[2]);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Arena &f" + array[2] + " &7is successfully deleted from configuration file!"));
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Checking for arena world folder. Please wait..."));
                        final String[] list5 = new File(".").list(new FilenameFilter() {
                            @Override
                            public boolean accept(final File parent, final String child) {
                                return new File(parent, child).isDirectory();
                            }
                        });
                        boolean b7 = false;
                        final String[] array4 = list5;
                        for (int length3 = array4.length, k = 0; k < length3; ++k) {
                            if (array4[k].equals(array[2])) {
                                b7 = true;
                                break;
                            }
                        }
                        if (b7) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Arena &f" + array[2] + " &7has a world folder!"));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Shut down the server and delete the world folder from this server!"));
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Arena &f" + array[2] + " &7does not have a world folder."));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                    }
                    return true;
                }
            }
            else {
                if (array[0].equalsIgnoreCase("setup")) {
                    if (array[1].equalsIgnoreCase("spawnpoint")) {
                        if (commandSender instanceof Player) {
                            if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                                final Player player8 = (Player)commandSender;
                                final Arena arena4 = this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getMultiWorld().setup.get(player8.getName()));
                                if (arena4 != null) {
                                    if (this.plugin.getMultiWorld().location1 != null) {
                                        final double d = this.plugin.getMultiWorld().location1.getBlockX() + 0.5;
                                        final double d2 = this.plugin.getMultiWorld().location1.getBlockY() + 1;
                                        final double d3 = this.plugin.getMultiWorld().location1.getBlockZ() + 0.5;
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena4.getName() + ".plots." + array[2] + ".spawnpoint.x", (Object)d);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena4.getName() + ".plots." + array[2] + ".spawnpoint.y", (Object)d2);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena4.getName() + ".plots." + array[2] + ".spawnpoint.z", (Object)d3);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena4.getName() + ".plots." + array[2] + ".spawnpoint.pitch", (Object)0.0);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena4.getName() + ".plots." + array[2] + ".spawnpoint.yaw", (Object)0.0);
                                        new BukkitRunnable() {
                                            public void run() {
                                                SBCommand.this.plugin.getConfigManager().saveConfig("arenas.yml");
                                            }
                                        }.runTaskAsynchronously((Plugin)this.plugin);
                                        player8.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Plot spawnpoint location is successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                        this.plugin.getMultiWorld().location1 = null;
                                    }
                                    else {
                                        player8.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for the plot spawnpoint does not exist."));
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
                                final Player player9 = (Player)commandSender;
                                final Arena arena5 = this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getMultiWorld().setup.get(player9.getName()));
                                if (arena5 != null) {
                                    if (this.plugin.getMultiWorld().location2 != null && this.plugin.getMultiWorld().location3 != null) {
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.x1", (Object)this.plugin.getMultiWorld().location2.getBlockX());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.y1", (Object)this.plugin.getMultiWorld().location2.getBlockY());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.z1", (Object)this.plugin.getMultiWorld().location2.getBlockZ());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.x2", (Object)this.plugin.getMultiWorld().location3.getBlockX());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.y2", (Object)this.plugin.getMultiWorld().location3.getBlockY());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena5.getName() + ".plots." + array[2] + ".area.z2", (Object)this.plugin.getMultiWorld().location3.getBlockZ());
                                        new BukkitRunnable() {
                                            public void run() {
                                                SBCommand.this.plugin.getConfigManager().saveConfig("arenas.yml");
                                            }
                                        }.runTaskAsynchronously((Plugin)this.plugin);
                                        player9.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Plot area positions are successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                        this.plugin.getMultiWorld().location2 = null;
                                        this.plugin.getMultiWorld().location3 = null;
                                    }
                                    else {
                                        player9.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for one of the plot area positions does not exist."));
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
                                final Player player10 = (Player)commandSender;
                                final Arena arena6 = this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getMultiWorld().setup.get(player10.getName()));
                                if (arena6 != null) {
                                    if (this.plugin.getMultiWorld().location1 != null) {
                                        final double d4 = this.plugin.getMultiWorld().location1.getBlockX() + 0.5;
                                        final double d5 = this.plugin.getMultiWorld().location1.getBlockY();
                                        final double d6 = this.plugin.getMultiWorld().location1.getBlockZ() + 0.5;
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena6.getName() + ".plots." + array[2] + ".laser-beam.x", (Object)d4);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena6.getName() + ".plots." + array[2] + ".laser-beam.y", (Object)d5);
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena6.getName() + ".plots." + array[2] + ".laser-beam.z", (Object)d6);
                                        new BukkitRunnable() {
                                            public void run() {
                                                SBCommand.this.plugin.getConfigManager().saveConfig("arenas.yml");
                                            }
                                        }.runTaskAsynchronously((Plugin)this.plugin);
                                        player10.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Laser spawnpoint location is successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                        this.plugin.getMultiWorld().location1 = null;
                                    }
                                    else {
                                        player10.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for the laser spawnpoint does not exist."));
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
                                final Player player11 = (Player)commandSender;
                                final Arena arena7 = this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getMultiWorld().setup.get(player11.getName()));
                                if (arena7 != null) {
                                    if (this.plugin.getMultiWorld().location2 != null && this.plugin.getMultiWorld().location3 != null) {
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.x1", (Object)this.plugin.getMultiWorld().location2.getBlockX());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.y1", (Object)this.plugin.getMultiWorld().location2.getBlockY());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.z1", (Object)this.plugin.getMultiWorld().location2.getBlockZ());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.x2", (Object)this.plugin.getMultiWorld().location3.getBlockX());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.y2", (Object)this.plugin.getMultiWorld().location3.getBlockY());
                                        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena7.getName() + ".plots." + array[2] + ".build-area.z2", (Object)this.plugin.getMultiWorld().location3.getBlockZ());
                                        new BukkitRunnable() {
                                            public void run() {
                                                SBCommand.this.plugin.getConfigManager().saveConfig("arenas.yml");
                                            }
                                        }.runTaskAsynchronously((Plugin)this.plugin);
                                        player11.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Build area positions are successfully saved into configuration file for &f" + array[2] + " &7plot!"));
                                        this.plugin.getMultiWorld().location2 = null;
                                        this.plugin.getMultiWorld().location3 = null;
                                    }
                                    else {
                                        player11.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for one of the build area positions does not exist."));
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
                        final Player player12 = (Player)commandSender;
                        if (Bukkit.getPlayer(array[2]) != null) {
                            this.plugin.getStatsManager().showStats(player12, Bukkit.getPlayer(array[2]));
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
        }
        else if (array.length == 9) {
            if (array[0].equalsIgnoreCase("arena") && array[1].equalsIgnoreCase("create")) {
                if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.arena.*") || commandSender.hasPermission("sb.command.arena.create")) {
                    boolean b8 = false;
                    if (Arena.arenaObjects.contains(this.plugin.getMultiWorld().getArenaManager().getArena(array[2]))) {
                        b8 = true;
                    }
                    if (!b8) {
                        final String[] list6 = new File(".").list(new FilenameFilter() {
                            @Override
                            public boolean accept(final File parent, final String child) {
                                return new File(parent, child).isDirectory();
                            }
                        });
                        boolean b9 = false;
                        final String[] array5 = list6;
                        for (int length4 = array5.length, l = 0; l < length4; ++l) {
                            if (array5[l].equals(array[2])) {
                                b9 = true;
                                break;
                            }
                        }
                        if (b9) {
                            final WorldCreator worldCreator = new WorldCreator(array[2]);
                            worldCreator.generator((ChunkGenerator)new VoidGenerator());
                            Bukkit.createWorld(worldCreator);
                            this.plugin.getMultiWorld().getArenaManager().createArena(array[2], Integer.parseInt(array[3]), Integer.parseInt(array[4]), Integer.parseInt(array[5]), Integer.parseInt(array[6]), Integer.parseInt(array[7]), Integer.parseInt(array[8]));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Arena is successfully saved into configuration file!"));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup &7to start setting up your arena."));
                        }
                        else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7World folder for this arena can not be found!"));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Make sure that arena name matches with the world name!"));
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Shut down the server and upload the world to this server!"));
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7That arena already exists!"));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                }
                return true;
            }
        }
        else if (array.length >= 4 && array[0].equalsIgnoreCase("setup")) {
            if (array[1].equalsIgnoreCase("template")) {
                if (commandSender instanceof Player) {
                    if (commandSender.hasPermission("sb.command.*") || commandSender.hasPermission("sb.command.setup")) {
                        final Player player13 = (Player)commandSender;
                        if (this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getMultiWorld().setup.get(player13.getName())) != null) {
                            if (this.plugin.getMultiWorld().blocks.containsKey(player13.getName())) {
                                final String s9 = array[2];
                                String string = "";
                                for (int n = 3; n < array.length; ++n) {
                                    string = string + " " + array[n];
                                }
                                this.plugin.getMultiWorld().getTemplateManager().saveTemplate(s9, string.replaceFirst(" ", ""), this.plugin.getMultiWorld().blocks.get(player13.getName()));
                                player13.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Blocks for template are successfully saved into configuration file."));
                                this.plugin.getMultiWorld().blocks.remove(player13.getName());
                            }
                            else {
                                player13.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Blocks for the template does not exist."));
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
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb arena &8- &7" + Translations.translate("HMENU-MANAGE_ARENAS")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb autojoin &8- &7" + Translations.translate("HMENU-AUTOJOIN_ARENA")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb join <arenaName> &8- &7" + Translations.translate("HMENU-JOIN_ARENA")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb leave &8- &7" + Translations.translate("HMENU-LEAVE_ARENA")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb reload &8- &7" + Translations.translate("HMENU-RELOAD")));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb setup &8- &7" + Translations.translate("HMENU-SETUP_THE_GAME")));
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f- &7/sb stats &8- &7" + Translations.translate("HMENU-SHOW_STATS")));
        }
        return true;
    }
}
