 

package ws.billy.speedbuilderspractice.multiworld.managers;

import org.bukkit.scoreboard.Team;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import java.util.Collection;
import java.util.Collections;
import ws.billy.speedbuilderspractice.api.events.PlayerLoseEvent;
import org.bukkit.plugin.Plugin;
import ws.billy.speedbuilderspractice.api.events.PlayerWinEvent;
import ws.billy.speedbuilderspractice.utils.StatsType;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import ws.billy.speedbuilderspractice.utils.Sounds;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.GameStateChangeEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.entity.Player;
import java.util.Iterator;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class ArenaManager
{
    private SpeedBuilders plugin;
    
    public ArenaManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public Arena getArena(final String anObject) {
        for (final Arena arena : Arena.arenaObjects) {
            if (arena.getName().equals(anObject)) {
                return arena;
            }
        }
        return null;
    }
    
    public void addPlayer(final Player player, final String s) {
        final Arena arena = this.getArena(s);
        arena.getPlayers().add(player.getName());
        if (arena.getGameState() == GameState.WAITING) {
            if (this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.world") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.x") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.y") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.z")) {
                final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("arenas.yml").getString("arenas." + arena.getName() + ".lobby.spawn.world")), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.z"));
                location.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.pitch"));
                location.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.yaw"));
                player.teleport(location);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
            }
            arena.getGameScoreboard().getTeam("Players").addPlayer((OfflinePlayer)player);
            this.plugin.getMultiWorld().getKitManager().setKit(player, "None", arena.getName());
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            final Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();
            while (iterator.hasNext()) {
                player.removePotionEffect(iterator.next().getType());
            }
            final ItemStack itemStack = Materials.CLOCK.getItemStack(1);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
            itemStack.setItemMeta(itemMeta);
            if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                final ItemStack itemStack2 = Materials.BOOK.getItemStack(1);
                final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                itemStack2.setItemMeta(itemMeta2);
                player.getInventory().setItem(this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack2);
            }
            player.getInventory().setItem(this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack);
            player.updateInventory();
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_JOIN")).replaceAll("%PLAYER%", player.getName()));
            for (final String s2 : arena.getPlayers()) {
                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName()) != null) {
                    final Scoreboard scoreboard = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName());
                    final Objective objective = scoreboard.getObjective("SpeedBuilders");
                    final Iterator iterator3 = scoreboard.getEntries().iterator();
                    while (iterator3.hasNext()) {
                        scoreboard.resetScores((String)iterator3.next());
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
                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s2), arena.getName()).toUpperCase())), scoreboard)).setScore(1);
                    Bukkit.getPlayer(s2).setScoreboard(scoreboard);
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
                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s2), arena.getName()).toUpperCase())), newScoreboard)).setScore(1);
                    Bukkit.getPlayer(s2).setScoreboard(newScoreboard);
                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s2).getName(), newScoreboard);
                }
            }
            if (arena.getPlayers().size() == arena.getNeededPlayers()) {
                this.plugin.getMultiWorld().getTimerManager().startTimer(arena.getName());
            }
        }
        else if (arena.getGameState() == GameState.STARTING) {
            if (this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.world") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.x") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.y") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".lobby.spawn.z")) {
                final Location location2 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("arenas.yml").getString("arenas." + arena.getName() + ".lobby.spawn.world")), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.z"));
                location2.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.pitch"));
                location2.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".lobby.spawn.yaw"));
                player.teleport(location2);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
            }
            arena.getGameScoreboard().getTeam("Players").addPlayer((OfflinePlayer)player);
            this.plugin.getMultiWorld().getKitManager().setKit(player, "None", arena.getName());
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            final Iterator<PotionEffect> iterator4 = player.getActivePotionEffects().iterator();
            while (iterator4.hasNext()) {
                player.removePotionEffect(iterator4.next().getType());
            }
            final ItemStack itemStack3 = Materials.CLOCK.getItemStack(1);
            final ItemMeta itemMeta3 = itemStack3.getItemMeta();
            itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
            itemStack3.setItemMeta(itemMeta3);
            if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                final ItemStack itemStack4 = Materials.BOOK.getItemStack(1);
                final ItemMeta itemMeta4 = itemStack4.getItemMeta();
                itemMeta4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                itemStack4.setItemMeta(itemMeta4);
                player.getInventory().setItem(this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack4);
            }
            player.getInventory().setItem(this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack3);
            player.updateInventory();
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_JOIN")).replaceAll("%PLAYER%", player.getName()));
            for (final String s3 : arena.getPlayers()) {
                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName()) != null) {
                    final Scoreboard scoreboard2 = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName());
                    final Objective objective2 = scoreboard2.getObjective("SpeedBuilders");
                    final Iterator iterator6 = scoreboard2.getEntries().iterator();
                    while (iterator6.hasNext()) {
                        scoreboard2.resetScores((String)iterator6.next());
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
                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), scoreboard2)).setScore(1);
                    Bukkit.getPlayer(s3).setScoreboard(scoreboard2);
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
                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), newScoreboard2)).setScore(1);
                    Bukkit.getPlayer(s3).setScoreboard(newScoreboard2);
                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s3).getName(), newScoreboard2);
                }
            }
        }
        this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
    }
    
    public void removePlayer(final Player player, final String s) {
        final Arena arena = this.getArena(s);
        arena.getPlayers().remove(player.getName());
        if (arena.getGameState() == GameState.WAITING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            for (final String s2 : arena.getPlayers()) {
                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName()) != null) {
                    final Scoreboard scoreboard = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName());
                    final Objective objective = scoreboard.getObjective("SpeedBuilders");
                    final Iterator iterator2 = scoreboard.getEntries().iterator();
                    while (iterator2.hasNext()) {
                        scoreboard.resetScores((String)iterator2.next());
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
                    objective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s2), arena.getName()).toUpperCase())), scoreboard)).setScore(1);
                    Bukkit.getPlayer(s2).setScoreboard(scoreboard);
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
                    registerNewObjective.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s2), arena.getName()).toUpperCase())), newScoreboard)).setScore(1);
                    Bukkit.getPlayer(s2).setScoreboard(newScoreboard);
                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s2).getName(), newScoreboard);
                }
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location2 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location2.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location2.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location2);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator3 = player.getActivePotionEffects().iterator();
            while (iterator3.hasNext()) {
                player.removePotionEffect(iterator3.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        else if (arena.getGameState() == GameState.STARTING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            if (arena.getPlayers().size() < arena.getNeededPlayers()) {
                Bukkit.getScheduler().cancelTask(arena.getStartTimerID());
                arena.setGameState(GameState.WAITING);
                Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, arena.getPlayers().size()));
            }
            for (final String s3 : arena.getPlayers()) {
                if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName()) != null) {
                    final Scoreboard scoreboard2 = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName());
                    final Objective objective2 = scoreboard2.getObjective("SpeedBuilders");
                    final Iterator iterator5 = scoreboard2.getEntries().iterator();
                    while (iterator5.hasNext()) {
                        scoreboard2.resetScores((String)iterator5.next());
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
                    objective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), scoreboard2)).setScore(1);
                    Bukkit.getPlayer(s3).setScoreboard(scoreboard2);
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
                    registerNewObjective2.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s3), arena.getName()).toUpperCase())), newScoreboard2)).setScore(1);
                    Bukkit.getPlayer(s3).setScoreboard(newScoreboard2);
                    arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s3).getName(), newScoreboard2);
                }
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location3 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location3.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location3.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location3);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location4 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location4.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location4.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location4);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator6 = player.getActivePotionEffects().iterator();
            while (iterator6.hasNext()) {
                player.removePotionEffect(iterator6.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        else if (arena.getGameState() == GameState.GAME_STARTING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s4 = arena.getPlots().get(player.getName());
            if (s4 != null) {
                arena.getPlayerPercent().remove(player.getName());
                arena.getPlots().remove(player.getName());
                if (arena.getJudgedPlayerArmorStand() != null) {
                    arena.getJudgedPlayerArmorStand().remove();
                }
                this.plugin.getMultiWorld().getGuardianManager().laserGuardian(false, arena.getName());
                if (arena.getPlots().size() == 1) {
                    Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                    arena.setSecondPlace(player.getName());
                    arena.setFirstPlace((String)arena.getPlots().keySet().toArray()[0]);
                    for (final String s5 : arena.getPlayers()) {
                        final Iterator<String> iterator8 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator8.hasNext()) {
                            Bukkit.getPlayer(s5).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator8.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                        }
                        this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s5), 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s5), 1.0f, 1.0f);
                        if (Bukkit.getPlayer(s5).getName().equals(arena.getFirstPlace())) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = ArenaManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s5).getName()));
                                    }
                                    ArenaManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s5), 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s5)));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s4, arena.getName());
                    new BukkitRunnable() {
                        public void run() {
                            ArenaManager.this.endArena(arena.getName());
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (arena.getPlots().size() == 2) {
                        arena.setThirdPlace(player.getName());
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s4, arena.getName());
                }
                final Iterator<String> iterator9 = this.plugin.getMultiWorld().loserCommands.iterator();
                while (iterator9.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator9.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator iterator10 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator10.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator10.next());
            }
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
            int score = 7;
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex2) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex3) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex4) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex5) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex6) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex7) {}
            for (final String s6 : arena.getPlayers()) {
                if (Bukkit.getPlayer(s6) != player && score >= 0 && !arena.getPlots().containsKey(Bukkit.getPlayer(s6).getName())) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + Bukkit.getPlayer(s6).getName()), arena.getGameScoreboard())).setScore(score);
                    --score;
                }
            }
            final Iterator<String> iterator12 = arena.getPlayers().iterator();
            while (iterator12.hasNext()) {
                Bukkit.getPlayer((String)iterator12.next()).setScoreboard(arena.getGameScoreboard());
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location5 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location5.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location5.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location5);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location6 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location6.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location6.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location6);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator13 = player.getActivePotionEffects().iterator();
            while (iterator13.hasNext()) {
                player.removePotionEffect(iterator13.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        else if (arena.getGameState() == GameState.SHOWCASING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s7 = arena.getPlots().get(player.getName());
            if (s7 != null) {
                arena.getPlayerPercent().remove(player.getName());
                arena.getPlots().remove(player.getName());
                if (arena.getJudgedPlayerArmorStand() != null) {
                    arena.getJudgedPlayerArmorStand().remove();
                }
                this.plugin.getMultiWorld().getGuardianManager().laserGuardian(false, arena.getName());
                if (arena.getPlots().size() == 1) {
                    Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                    arena.setSecondPlace(player.getName());
                    arena.setFirstPlace((String)arena.getPlots().keySet().toArray()[0]);
                    for (final String s8 : arena.getPlayers()) {
                        final Iterator<String> iterator15 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator15.hasNext()) {
                            Bukkit.getPlayer(s8).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator15.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                        }
                        this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s8), 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s8), 1.0f, 1.0f);
                        if (Bukkit.getPlayer(s8).getName().equals(arena.getFirstPlace())) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = ArenaManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s8).getName()));
                                    }
                                    ArenaManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s8), 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s8)));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s7, arena.getName());
                    new BukkitRunnable() {
                        public void run() {
                            ArenaManager.this.endArena(arena.getName());
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (arena.getPlots().size() == 2) {
                        arena.setThirdPlace(player.getName());
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s7, arena.getName());
                }
                final Iterator<String> iterator16 = this.plugin.getMultiWorld().loserCommands.iterator();
                while (iterator16.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator16.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator iterator17 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator17.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator17.next());
            }
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
            int score2 = 7;
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex8) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex9) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex10) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex11) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex12) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex13) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex14) {}
            for (final String s9 : arena.getPlayers()) {
                if (Bukkit.getPlayer(s9) != player && score2 >= 0 && !arena.getPlots().containsKey(Bukkit.getPlayer(s9).getName())) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + Bukkit.getPlayer(s9).getName()), arena.getGameScoreboard())).setScore(score2);
                    --score2;
                }
            }
            final Iterator<String> iterator19 = arena.getPlayers().iterator();
            while (iterator19.hasNext()) {
                Bukkit.getPlayer((String)iterator19.next()).setScoreboard(arena.getGameScoreboard());
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location7 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location7.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location7.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location7);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location8 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location8.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location8.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location8);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator20 = player.getActivePotionEffects().iterator();
            while (iterator20.hasNext()) {
                player.removePotionEffect(iterator20.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        else if (arena.getGameState() == GameState.BUILDING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s10 = arena.getPlots().get(player.getName());
            if (s10 != null) {
                arena.getPlayerPercent().remove(player.getName());
                arena.getPlots().remove(player.getName());
                if (arena.getJudgedPlayerArmorStand() != null) {
                    arena.getJudgedPlayerArmorStand().remove();
                }
                this.plugin.getMultiWorld().getGuardianManager().laserGuardian(false, arena.getName());
                if (arena.getPlots().size() == 1) {
                    Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                    arena.setSecondPlace(player.getName());
                    arena.setFirstPlace((String)arena.getPlots().keySet().toArray()[0]);
                    for (final String s11 : arena.getPlayers()) {
                        final Iterator<String> iterator22 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator22.hasNext()) {
                            Bukkit.getPlayer(s11).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator22.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                        }
                        this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s11), 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s11), 1.0f, 1.0f);
                        if (Bukkit.getPlayer(s11).getName().equals(arena.getFirstPlace())) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = ArenaManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s11).getName()));
                                    }
                                    ArenaManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s11), 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s11)));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s10, arena.getName());
                    new BukkitRunnable() {
                        public void run() {
                            ArenaManager.this.endArena(arena.getName());
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (arena.getPlayerPercent().size() >= 2 && (int)Collections.min((Collection<?>)arena.getPlayerPercent().values()) == 100) {
                        this.plugin.getMultiWorld().getTimerManager().guardianIsImpressed(arena.getName());
                    }
                    if (arena.getPlots().size() == 2) {
                        arena.setThirdPlace(player.getName());
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s10, arena.getName());
                }
                final Iterator<String> iterator23 = this.plugin.getMultiWorld().loserCommands.iterator();
                while (iterator23.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator23.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator iterator24 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator24.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator24.next());
            }
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
            int score3 = 7;
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex15) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex16) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex17) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex18) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex19) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex20) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex21) {}
            for (final String s12 : arena.getPlayers()) {
                if (Bukkit.getPlayer(s12) != player && score3 >= 0 && !arena.getPlots().containsKey(Bukkit.getPlayer(s12).getName())) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + Bukkit.getPlayer(s12).getName()), arena.getGameScoreboard())).setScore(score3);
                    --score3;
                }
            }
            final Iterator<String> iterator26 = arena.getPlayers().iterator();
            while (iterator26.hasNext()) {
                Bukkit.getPlayer((String)iterator26.next()).setScoreboard(arena.getGameScoreboard());
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location9 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location9.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location9.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location9);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location10 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location10.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location10.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location10);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator27 = player.getActivePotionEffects().iterator();
            while (iterator27.hasNext()) {
                player.removePotionEffect(iterator27.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        else if (arena.getGameState() == GameState.JUDGING) {
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s13 = arena.getPlots().get(player.getName());
            if (s13 != null) {
                arena.getPlayerPercent().remove(player.getName());
                arena.getPlots().remove(player.getName());
                if (arena.getJudgedPlayerArmorStand() != null) {
                    arena.getJudgedPlayerArmorStand().remove();
                }
                this.plugin.getMultiWorld().getGuardianManager().laserGuardian(false, arena.getName());
                if (player.getName().equalsIgnoreCase(arena.getJudgedPlayerName())) {
                    if (arena.getPlots().size() == 1) {
                        Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                        arena.setSecondPlace(player.getName());
                        arena.setFirstPlace((String)arena.getPlots().keySet().toArray()[0]);
                        for (final String s14 : arena.getPlayers()) {
                            final Iterator<String> iterator29 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                            while (iterator29.hasNext()) {
                                Bukkit.getPlayer(s14).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator29.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                            }
                            this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s14), 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                            Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s14), 1.0f, 1.0f);
                            if (Bukkit.getPlayer(s14).getName().equals(arena.getFirstPlace())) {
                                new BukkitRunnable() {
                                    public void run() {
                                        final Iterator<String> iterator = ArenaManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                        while (iterator.hasNext()) {
                                            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s14).getName()));
                                        }
                                        ArenaManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s14), 1);
                                        Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s14)));
                                    }
                                }.runTaskLater((Plugin)this.plugin, 5L);
                            }
                        }
                        this.plugin.getMultiWorld().getTemplateManager().explodePlot(s13, arena.getName());
                        new BukkitRunnable() {
                            public void run() {
                                ArenaManager.this.endArena(arena.getName());
                            }
                        }.runTaskLater((Plugin)this.plugin, 200L);
                    }
                    else {
                        Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                        if (arena.getPlots().size() == 2) {
                            arena.setThirdPlace(player.getName());
                        }
                        this.plugin.getMultiWorld().getTemplateManager().explodePlot(s13, arena.getName());
                        new BukkitRunnable() {
                            public void run() {
                                for (final String s : arena.getPlayers()) {
                                    if (arena.getPlots().containsKey(Bukkit.getPlayer(s).getName())) {
                                        Bukkit.getPlayer(s).getInventory().setArmorContents((ItemStack[])null);
                                        Bukkit.getPlayer(s).getInventory().clear();
                                        Bukkit.getPlayer(s).setExp(0.0f);
                                        Bukkit.getPlayer(s).setFireTicks(0);
                                        Bukkit.getPlayer(s).setFoodLevel(20);
                                        Bukkit.getPlayer(s).setGameMode(GameMode.SURVIVAL);
                                        Bukkit.getPlayer(s).setHealth(20.0);
                                        Bukkit.getPlayer(s).setLevel(0);
                                        Bukkit.getPlayer(s).setAllowFlight(false);
                                        Bukkit.getPlayer(s).setFlying(false);
                                        final Iterator iterator2 = Bukkit.getPlayer(s).getActivePotionEffects().iterator();
                                        while (iterator2.hasNext()) {
                                            Bukkit.getPlayer(s).removePotionEffect(iterator2.next().getType());
                                        }
                                        if (arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)Bukkit.getPlayer(s)) == null || !arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)Bukkit.getPlayer(s)).getName().equals("Players")) {
                                            continue;
                                        }
                                        final String str = arena.getPlots().get(Bukkit.getPlayer(s).getName());
                                        final Location location = new Location(Bukkit.getWorld(arena.getName()), ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.x"), ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.y"), ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.z"));
                                        location.setPitch((float)ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.pitch"));
                                        location.setYaw((float)ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.yaw"));
                                        Bukkit.getPlayer(s).teleport(location);
                                        Bukkit.getPlayer(s).setFallDistance(0.0f);
                                    }
                                }
                                ArenaManager.this.plugin.getMultiWorld().getTimerManager().showCaseTimer(arena.getName());
                            }
                        }.runTaskLater((Plugin)this.plugin, 5L);
                    }
                }
                else if (arena.getPlots().size() == 1) {
                    Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                    arena.setSecondPlace((String)arena.getPlots().keySet().toArray()[0]);
                    arena.setFirstPlace(player.getName());
                    for (final String s15 : arena.getPlayers()) {
                        final Iterator<String> iterator31 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator31.hasNext()) {
                            Bukkit.getPlayer(s15).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator31.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                        }
                        this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s15), 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s15), 1.0f, 1.0f);
                        if (Bukkit.getPlayer(s15).getName().equals(arena.getFirstPlace())) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = ArenaManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s15).getName()));
                                    }
                                    ArenaManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s15), 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s15)));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s13, arena.getName());
                    new BukkitRunnable() {
                        public void run() {
                            ArenaManager.this.endArena(arena.getName());
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (arena.getPlots().size() == 2) {
                        arena.setThirdPlace(player.getName());
                    }
                    this.plugin.getMultiWorld().getTemplateManager().explodePlot(s13, arena.getName());
                }
            }
            final Iterator iterator32 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator32.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator32.next());
            }
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
            int score4 = 7;
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex22) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex23) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex24) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex25) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex26) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex27) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex28) {}
            for (final String s16 : arena.getPlayers()) {
                if (Bukkit.getPlayer(s16) != player && score4 >= 0 && !arena.getPlots().containsKey(Bukkit.getPlayer(s16).getName())) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + Bukkit.getPlayer(s16).getName()), arena.getGameScoreboard())).setScore(score4);
                    --score4;
                }
            }
            final Iterator<String> iterator34 = arena.getPlayers().iterator();
            while (iterator34.hasNext()) {
                Bukkit.getPlayer((String)iterator34.next()).setScoreboard(arena.getGameScoreboard());
            }
            if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location11 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location11.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location11.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location11);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getMultiWorld().getKitManager().setKit(player, null, arena.getName());
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location12 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location12.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location12.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location12);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                }
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            final Iterator<PotionEffect> iterator35 = player.getActivePotionEffects().iterator();
            while (iterator35.hasNext()) {
                player.removePotionEffect(iterator35.next().getType());
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
                this.plugin.getMultiWorld().loadTempInfo(player);
            }
            player.updateInventory();
        }
        this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
    }
    
    public void endArena(final String str) {
        final Arena arena = this.getArena(str);
        if (arena != null) {
            for (final String s : arena.getPlayers()) {
                this.plugin.getMultiWorld().getNMSManager().setPlayerVisibility(Bukkit.getPlayer(s), null, true);
                if (arena.getGameScoreboard().getTeam("Players").hasPlayer((OfflinePlayer)Bukkit.getPlayer(s))) {
                    arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)Bukkit.getPlayer(s));
                    this.plugin.getMultiWorld().getKitManager().setKit(Bukkit.getPlayer(s), null, arena.getName());
                    if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                        final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                        location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                        location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                        Bukkit.getPlayer(s).teleport(location);
                    }
                    else {
                        Bukkit.getPlayer(s).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                    }
                }
                else if (arena.getGameScoreboard().getTeam("Guardians").hasPlayer((OfflinePlayer)Bukkit.getPlayer(s))) {
                    arena.getGameScoreboard().getTeam("Guardians").removePlayer((OfflinePlayer)Bukkit.getPlayer(s));
                    this.plugin.getMultiWorld().getKitManager().setKit(Bukkit.getPlayer(s), null, arena.getName());
                    if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                        final Location location2 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                        location2.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                        location2.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                        Bukkit.getPlayer(s).teleport(location2);
                    }
                    else {
                        Bukkit.getPlayer(s).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                    }
                }
                Bukkit.getPlayer(s).getInventory().setArmorContents((ItemStack[])null);
                Bukkit.getPlayer(s).getInventory().clear();
                Bukkit.getPlayer(s).setExp(0.0f);
                Bukkit.getPlayer(s).setFireTicks(0);
                Bukkit.getPlayer(s).setFoodLevel(20);
                Bukkit.getPlayer(s).setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                Bukkit.getPlayer(s).setHealth(20.0);
                Bukkit.getPlayer(s).setLevel(0);
                Bukkit.getPlayer(s).setAllowFlight(false);
                Bukkit.getPlayer(s).setFlying(false);
                Bukkit.getPlayer(s).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                final Iterator iterator2 = Bukkit.getPlayer(s).getActivePotionEffects().iterator();
                while (iterator2.hasNext()) {
                    Bukkit.getPlayer(s).removePotionEffect(iterator2.next().getType());
                }
                if (this.plugin.getMultiWorld().playerTempHealth.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(Bukkit.getPlayer(s).getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(Bukkit.getPlayer(s).getName())) {
                    this.plugin.getMultiWorld().loadTempInfo(Bukkit.getPlayer(s));
                }
                Bukkit.getPlayer(s).updateInventory();
            }
            Bukkit.getScheduler().cancelTask(arena.getStartTimerID());
            Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
            Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
            Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
            this.plugin.getMultiWorld().getTemplateManager().resetPlots(arena.getName());
            arena.getPlayers().clear();
            arena.setStartTime(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".start-time"));
            arena.setGameStartTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".game-start-time"));
            arena.setShowCaseTime(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".showcase-time"));
            arena.setBuildTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".build-time"));
            arena.setJudgeTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".judge-time"));
            arena.getUnusedTemplates().clear();
            arena.getUsedTemplates().clear();
            arena.getPlayersDoubleJumpCooldowned().clear();
            arena.getPlayerPercent().clear();
            arena.getPlayerStartScoreboard().clear();
            arena.getPlayersKit().clear();
            arena.getPlots().clear();
            arena.setBuildTimeSubtractor(0);
            arena.setCurrentRound(0);
            int maxPlayers = 0;
            try {
                for (final String str2 : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots").getKeys(false)) {
                    if (this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str2 + ".spawnpoint") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str2 + ".area") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str2 + ".laser-beam") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str2 + ".build-area") && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str2 + ".blocks")) {
                        ++maxPlayers;
                    }
                }
            }
            catch (NullPointerException ex) {}
            arena.setMaxPlayers(maxPlayers);
            arena.setCurrentBuildDisplayName(Translations.translate("MAIN-NONE"));
            arena.setCurrentBuildRawName(Translations.translate("MAIN-NONE"));
            arena.setFirstPlace(Translations.translate("MAIN-NONE"));
            arena.setSecondPlace(Translations.translate("MAIN-NONE"));
            arena.setThirdPlace(Translations.translate("MAIN-NONE"));
            arena.setGameState(GameState.WAITING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, arena.getPlayers().size()));
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void loadArenas() {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas")) {
                    Arena.arenaObjects.clear();
                    for (final String s : ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas").getKeys(false)) {
                        final int int1 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".start-time");
                        final int int2 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".game-start-time");
                        final int int3 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".showcase-time");
                        final int int4 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".build-time");
                        final int int5 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".judge-time");
                        final int int6 = ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + s + ".needed-players");
                        int maxPlayers = 0;
                        try {
                            for (final String str : ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + s + ".plots").getKeys(false)) {
                                if (ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + s + ".plots." + str + ".spawnpoint") && ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + s + ".plots." + str + ".area") && ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + s + ".plots." + str + ".laser-beam") && ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + s + ".plots." + str + ".build-area") && ArenaManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + s + ".plots." + str + ".blocks")) {
                                    ++maxPlayers;
                                }
                            }
                        }
                        catch (NullPointerException ex) {}
                        final WorldCreator worldCreator = new WorldCreator(s);
                        worldCreator.generator((ChunkGenerator)new VoidGenerator());
                        Bukkit.createWorld(worldCreator);
                        ArenaManager.this.plugin.getMultiWorld().getTemplateManager().resetPlots(s);
                        final Arena arena = new Arena(s, int1, int2, int3, int4, int5, int6);
                        arena.getUnusedTemplates().clear();
                        arena.getUsedTemplates().clear();
                        arena.getPlayersDoubleJumpCooldowned().clear();
                        arena.getPlayerPercent().clear();
                        arena.getPlayerStartScoreboard().clear();
                        arena.getPlayersKit().clear();
                        arena.getPlots().clear();
                        arena.setBuildTimeSubtractor(0);
                        arena.setCurrentRound(0);
                        arena.setMaxPlayers(maxPlayers);
                        arena.setCurrentBuildDisplayName(Translations.translate("MAIN-NONE"));
                        arena.setCurrentBuildRawName(Translations.translate("MAIN-NONE"));
                        arena.setFirstPlace(Translations.translate("MAIN-NONE"));
                        arena.setSecondPlace(Translations.translate("MAIN-NONE"));
                        arena.setThirdPlace(Translations.translate("MAIN-NONE"));
                        arena.setGameState(GameState.WAITING);
                        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, arena.getPlayers().size()));
                        arena.gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        final Objective registerNewObjective = arena.gameScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-SPEEDBUILDERS")));
                        registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        final Team registerNewTeam = arena.gameScoreboard.registerNewTeam("Players");
                        final Team registerNewTeam2 = arena.gameScoreboard.registerNewTeam("Guardians");
                        registerNewTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
                        registerNewTeam2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
                        ArenaManager.this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
                    }
                }
            }
        }, 0L);
    }
    
    public void createArena(final String s, final int i, final int j, final int k, final int l, final int m, final int i2) {
        final Arena arena = new Arena(s, i, j, k, l, m, i2);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName(), (Object)null);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".start-time", (Object)i);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".game-start-time", (Object)j);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".showcase-time", (Object)k);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".build-time", (Object)l);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".judge-time", (Object)m);
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".needed-players", (Object)i2);
        new BukkitRunnable() {
            public void run() {
                ArenaManager.this.plugin.getConfigManager().saveConfig("arenas.yml");
                ArenaManager.this.loadArenas();
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
    
    public void deleteArena(final String str) {
        this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + str, (Object)null);
        new BukkitRunnable() {
            public void run() {
                ArenaManager.this.plugin.getConfigManager().saveConfig("arenas.yml");
                ArenaManager.this.loadArenas();
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
}
