 

package ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_9_R2;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.Vector;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import fr.neatmonster.nocheatplus.checks.CheckType;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.Effect;
import org.bukkit.block.Skull;
import org.bukkit.block.Bed;
import org.bukkit.block.Banner;
import com.google.common.io.ByteStreams;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.entity.Player;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    public SpeedBuilders plugin;
    
    public PlayerListener() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
            if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                player.teleport(location);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
            }
            this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").addPlayer((OfflinePlayer)player);
            this.plugin.getBungeeCord().getKitManager().setKit(player, "None");
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
            final Iterator iterator = player.getActivePotionEffects().iterator();
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
            playerJoinEvent.setJoinMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_JOIN")).replaceAll("%PLAYER%", player.getName()));
            for (final Player player2 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getBungeeCord().playerStartScoreboard.get(player2.getName()) != null) {
                    final Scoreboard scoreboard = this.plugin.getBungeeCord().playerStartScoreboard.get(player2.getName());
                    final Objective objective = scoreboard.getObjective("SpeedBuilders");
                    final Iterator iterator3 = scoreboard.getEntries().iterator();
                    while (iterator3.hasNext()) {
                        scoreboard.resetScores((String)iterator3.next());
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + this.plugin.getBungeeCord().maxPlayers), scoreboard)).setScore(4);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player2).toUpperCase())), scoreboard)).setScore(1);
                    player2.setScoreboard(scoreboard);
                }
                else {
                    final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + this.plugin.getBungeeCord().maxPlayers), newScoreboard)).setScore(4);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player2).toUpperCase())), newScoreboard)).setScore(1);
                    player2.setScoreboard(newScoreboard);
                    this.plugin.getBungeeCord().playerStartScoreboard.put(player2.getName(), newScoreboard);
                }
            }
            if (Bukkit.getOnlinePlayers().size() == this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.needed-players")) {
                this.plugin.getBungeeCord().getTimerManager().startTimer();
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
            if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                final Location location2 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                location2.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                location2.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                player.teleport(location2);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
            }
            this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").addPlayer((OfflinePlayer)player);
            this.plugin.getBungeeCord().getKitManager().setKit(player, "None");
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
            final Iterator iterator4 = player.getActivePotionEffects().iterator();
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
            playerJoinEvent.setJoinMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_JOIN")).replaceAll("%PLAYER%", player.getName()));
            for (final Player player3 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getBungeeCord().playerStartScoreboard.get(player3.getName()) != null) {
                    final Scoreboard scoreboard2 = this.plugin.getBungeeCord().playerStartScoreboard.get(player3.getName());
                    final Objective objective2 = scoreboard2.getObjective("SpeedBuilders");
                    final Iterator iterator6 = scoreboard2.getEntries().iterator();
                    while (iterator6.hasNext()) {
                        scoreboard2.resetScores((String)iterator6.next());
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard2)).setScore(6);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard2)).setScore(5);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + this.plugin.getBungeeCord().maxPlayers), scoreboard2)).setScore(4);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard2)).setScore(3);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard2)).setScore(2);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player3).toUpperCase())), scoreboard2)).setScore(1);
                    player3.setScoreboard(scoreboard2);
                }
                else {
                    final Scoreboard newScoreboard2 = Bukkit.getScoreboardManager().getNewScoreboard();
                    final Objective registerNewObjective2 = newScoreboard2.registerNewObjective("SpeedBuilders", "dummy");
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    registerNewObjective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard2)).setScore(6);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard2)).setScore(5);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + this.plugin.getBungeeCord().maxPlayers), newScoreboard2)).setScore(4);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard2)).setScore(3);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard2)).setScore(2);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player3).toUpperCase())), newScoreboard2)).setScore(1);
                    player3.setScoreboard(newScoreboard2);
                    this.plugin.getBungeeCord().playerStartScoreboard.put(player3.getName(), newScoreboard2);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            for (final Player player2 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getBungeeCord().playerStartScoreboard.get(player2.getName()) != null) {
                    final Scoreboard scoreboard = this.plugin.getBungeeCord().playerStartScoreboard.get(player2.getName());
                    final Objective objective = scoreboard.getObjective("SpeedBuilders");
                    final Iterator iterator2 = scoreboard.getEntries().iterator();
                    while (iterator2.hasNext()) {
                        scoreboard.resetScores((String)iterator2.next());
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() - 1 + "/" + this.plugin.getBungeeCord().maxPlayers), scoreboard)).setScore(4);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                    objective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player2).toUpperCase())), scoreboard)).setScore(1);
                    player2.setScoreboard(scoreboard);
                }
                else {
                    final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() - 1 + "/" + this.plugin.getBungeeCord().maxPlayers), newScoreboard)).setScore(4);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                    registerNewObjective.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player2).toUpperCase())), newScoreboard)).setScore(1);
                    player2.setScoreboard(newScoreboard);
                    this.plugin.getBungeeCord().playerStartScoreboard.put(player2.getName(), newScoreboard);
                }
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator3 = player.getActivePotionEffects().iterator();
            while (iterator3.hasNext()) {
                player.removePotionEffect(iterator3.next().getType());
            }
            player.updateInventory();
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            if (Bukkit.getOnlinePlayers().size() - 1 < this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.needed-players")) {
                Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getStartTimerID());
                this.plugin.getBungeeCord().gameState = GameState.WAITING;
                Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, Bukkit.getOnlinePlayers().size()));
            }
            for (final Player player3 : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getBungeeCord().playerStartScoreboard.get(player3.getName()) != null) {
                    final Scoreboard scoreboard2 = this.plugin.getBungeeCord().playerStartScoreboard.get(player3.getName());
                    final Objective objective2 = scoreboard2.getObjective("SpeedBuilders");
                    final Iterator iterator5 = scoreboard2.getEntries().iterator();
                    while (iterator5.hasNext()) {
                        scoreboard2.resetScores((String)iterator5.next());
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard2)).setScore(6);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard2)).setScore(5);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() - 1 + "/" + this.plugin.getBungeeCord().maxPlayers), scoreboard2)).setScore(4);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard2)).setScore(3);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard2)).setScore(2);
                    objective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player3).toUpperCase())), scoreboard2)).setScore(1);
                    player3.setScoreboard(scoreboard2);
                }
                else {
                    final Scoreboard newScoreboard2 = Bukkit.getScoreboardManager().getNewScoreboard();
                    final Objective registerNewObjective2 = newScoreboard2.registerNewObjective("SpeedBuilders", "dummy");
                    if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                    }
                    if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                        registerNewObjective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", this.plugin.getBungeeCord().getTimerManager().timeString(this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                    }
                    registerNewObjective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard2)).setScore(6);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard2)).setScore(5);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() - 1 + "/" + this.plugin.getBungeeCord().maxPlayers), newScoreboard2)).setScore(4);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard2)).setScore(3);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard2)).setScore(2);
                    registerNewObjective2.getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getBungeeCord().getKitManager().getKit(player3).toUpperCase())), newScoreboard2)).setScore(1);
                    player3.setScoreboard(newScoreboard2);
                    this.plugin.getBungeeCord().playerStartScoreboard.put(player3.getName(), newScoreboard2);
                }
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator6 = player.getActivePotionEffects().iterator();
            while (iterator6.hasNext()) {
                player.removePotionEffect(iterator6.next().getType());
            }
            player.updateInventory();
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.GAME_STARTING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s = this.plugin.getBungeeCord().plots.get(player.getName());
            if (s != null) {
                this.plugin.getBungeeCord().playerPercent.remove(player.getName());
                this.plugin.getBungeeCord().plots.remove(player.getName());
                if (this.plugin.getBungeeCord().judgedPlayerArmorStand != null) {
                    this.plugin.getBungeeCord().judgedPlayerArmorStand.remove();
                }
                this.plugin.getBungeeCord().getGuardianManager().laserGuardian(false);
                if (this.plugin.getBungeeCord().plots.size() == 1) {
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                    this.plugin.getBungeeCord().secondPlace = player.getName();
                    this.plugin.getBungeeCord().firstPlace = (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                    for (final Player player4 : Bukkit.getOnlinePlayers()) {
                        final Iterator<String> iterator8 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator8.hasNext()) {
                            player4.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator8.next().replaceAll("%PLAYER1%", this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", this.plugin.getBungeeCord().thirdPlace)));
                        }
                        this.plugin.getBungeeCord().getNMSManager().showTitle(player4, 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(player4, 1.0f, 1.0f);
                        if (player4.getName().equals(this.plugin.getBungeeCord().firstPlace)) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = PlayerListener.this.plugin.getBungeeCord().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player4.getName()));
                                    }
                                    PlayerListener.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player4, 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player4));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s);
                    new BukkitRunnable() {
                        public void run() {
                            PlayerListener.this.plugin.getBungeeCord().getTimerManager().stop();
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (this.plugin.getBungeeCord().plots.size() == 2) {
                        this.plugin.getBungeeCord().thirdPlace = player.getName();
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s);
                }
                final Iterator<String> iterator9 = this.plugin.getBungeeCord().loserCommands.iterator();
                while (iterator9.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator9.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator<String> iterator10 = this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
            while (iterator10.hasNext()) {
                this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator10.next());
            }
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', this.plugin.getBungeeCord().currentBuildDisplayName), this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + this.plugin.getBungeeCord().currentRound), this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
            int score = 7;
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0]), this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[1]), this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex2) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[2]), this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex3) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[3]), this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex4) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[4]), this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex5) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[5]), this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex6) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[6]), this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex7) {}
            for (final Player player5 : Bukkit.getOnlinePlayers()) {
                if (player5 != player && score >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player5.getName())) {
                    this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player5.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score);
                    --score;
                }
            }
            final Iterator<Player> iterator12 = Bukkit.getOnlinePlayers().iterator();
            while (iterator12.hasNext()) {
                iterator12.next().setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator13 = player.getActivePotionEffects().iterator();
            while (iterator13.hasNext()) {
                player.removePotionEffect(iterator13.next().getType());
            }
            player.updateInventory();
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.SHOWCASING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s2 = this.plugin.getBungeeCord().plots.get(player.getName());
            if (s2 != null) {
                this.plugin.getBungeeCord().playerPercent.remove(player.getName());
                this.plugin.getBungeeCord().plots.remove(player.getName());
                if (this.plugin.getBungeeCord().judgedPlayerArmorStand != null) {
                    this.plugin.getBungeeCord().judgedPlayerArmorStand.remove();
                }
                this.plugin.getBungeeCord().getGuardianManager().laserGuardian(false);
                if (this.plugin.getBungeeCord().plots.size() == 1) {
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                    this.plugin.getBungeeCord().secondPlace = player.getName();
                    this.plugin.getBungeeCord().firstPlace = (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                    for (final Player player6 : Bukkit.getOnlinePlayers()) {
                        final Iterator<String> iterator15 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator15.hasNext()) {
                            player6.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator15.next().replaceAll("%PLAYER1%", this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", this.plugin.getBungeeCord().thirdPlace)));
                        }
                        this.plugin.getBungeeCord().getNMSManager().showTitle(player6, 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(player6, 1.0f, 1.0f);
                        if (player6.getName().equals(this.plugin.getBungeeCord().firstPlace)) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = PlayerListener.this.plugin.getBungeeCord().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player6.getName()));
                                    }
                                    PlayerListener.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player6, 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player6));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s2);
                    new BukkitRunnable() {
                        public void run() {
                            PlayerListener.this.plugin.getBungeeCord().getTimerManager().stop();
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (this.plugin.getBungeeCord().plots.size() == 2) {
                        this.plugin.getBungeeCord().thirdPlace = player.getName();
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s2);
                }
                final Iterator<String> iterator16 = this.plugin.getBungeeCord().loserCommands.iterator();
                while (iterator16.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator16.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator<String> iterator17 = this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
            while (iterator17.hasNext()) {
                this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator17.next());
            }
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', this.plugin.getBungeeCord().currentBuildDisplayName), this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + this.plugin.getBungeeCord().currentRound), this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
            int score2 = 7;
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0]), this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex8) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[1]), this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex9) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[2]), this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex10) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[3]), this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex11) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[4]), this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex12) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[5]), this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex13) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[6]), this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                --score2;
            }
            catch (ArrayIndexOutOfBoundsException ex14) {}
            for (final Player player7 : Bukkit.getOnlinePlayers()) {
                if (player7 != player && score2 >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player7.getName())) {
                    this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player7.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score2);
                    --score2;
                }
            }
            final Iterator<Player> iterator19 = Bukkit.getOnlinePlayers().iterator();
            while (iterator19.hasNext()) {
                iterator19.next().setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator20 = player.getActivePotionEffects().iterator();
            while (iterator20.hasNext()) {
                player.removePotionEffect(iterator20.next().getType());
            }
            player.updateInventory();
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.BUILDING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s3 = this.plugin.getBungeeCord().plots.get(player.getName());
            if (s3 != null) {
                this.plugin.getBungeeCord().playerPercent.remove(player.getName());
                this.plugin.getBungeeCord().plots.remove(player.getName());
                if (this.plugin.getBungeeCord().judgedPlayerArmorStand != null) {
                    this.plugin.getBungeeCord().judgedPlayerArmorStand.remove();
                }
                this.plugin.getBungeeCord().getGuardianManager().laserGuardian(false);
                if (this.plugin.getBungeeCord().plots.size() == 1) {
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                    this.plugin.getBungeeCord().secondPlace = player.getName();
                    this.plugin.getBungeeCord().firstPlace = (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                    for (final Player player8 : Bukkit.getOnlinePlayers()) {
                        final Iterator<String> iterator22 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator22.hasNext()) {
                            player8.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator22.next().replaceAll("%PLAYER1%", this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", this.plugin.getBungeeCord().thirdPlace)));
                        }
                        this.plugin.getBungeeCord().getNMSManager().showTitle(player8, 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(player8, 1.0f, 1.0f);
                        if (player8.getName().equals(this.plugin.getBungeeCord().firstPlace)) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = PlayerListener.this.plugin.getBungeeCord().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player8.getName()));
                                    }
                                    PlayerListener.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player8, 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player8));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s3);
                    new BukkitRunnable() {
                        public void run() {
                            PlayerListener.this.plugin.getBungeeCord().getTimerManager().stop();
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (this.plugin.getBungeeCord().playerPercent.size() >= 2 && Collections.min((Collection<? extends Integer>)this.plugin.getBungeeCord().playerPercent.values()) == 100) {
                        this.plugin.getBungeeCord().getTimerManager().guardianIsImpressed();
                    }
                    if (this.plugin.getBungeeCord().plots.size() == 2) {
                        this.plugin.getBungeeCord().thirdPlace = player.getName();
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s3);
                }
                final Iterator<String> iterator23 = this.plugin.getBungeeCord().loserCommands.iterator();
                while (iterator23.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator23.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            final Iterator<String> iterator24 = this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
            while (iterator24.hasNext()) {
                this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator24.next());
            }
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', this.plugin.getBungeeCord().currentBuildDisplayName), this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + this.plugin.getBungeeCord().currentRound), this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
            int score3 = 7;
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0]), this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex15) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[1]), this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex16) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[2]), this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex17) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[3]), this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex18) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[4]), this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex19) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[5]), this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex20) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[6]), this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                --score3;
            }
            catch (ArrayIndexOutOfBoundsException ex21) {}
            for (final Player player9 : Bukkit.getOnlinePlayers()) {
                if (player9 != player && score3 >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player9.getName())) {
                    this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player9.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score3);
                    --score3;
                }
            }
            final Iterator<Player> iterator26 = Bukkit.getOnlinePlayers().iterator();
            while (iterator26.hasNext()) {
                iterator26.next().setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator27 = player.getActivePotionEffects().iterator();
            while (iterator27.hasNext()) {
                player.removePotionEffect(iterator27.next().getType());
            }
            player.updateInventory();
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.JUDGING) {
            playerQuitEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT")).replaceAll("%PLAYER%", player.getName()));
            final String s4 = this.plugin.getBungeeCord().plots.get(player.getName());
            if (s4 != null) {
                this.plugin.getBungeeCord().playerPercent.remove(player.getName());
                this.plugin.getBungeeCord().plots.remove(player.getName());
                if (this.plugin.getBungeeCord().judgedPlayerArmorStand != null) {
                    this.plugin.getBungeeCord().judgedPlayerArmorStand.remove();
                }
                this.plugin.getBungeeCord().getGuardianManager().laserGuardian(false);
                if (player.getName().equalsIgnoreCase(this.plugin.getBungeeCord().judgedPlayerName)) {
                    if (this.plugin.getBungeeCord().plots.size() == 1) {
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                        this.plugin.getBungeeCord().secondPlace = player.getName();
                        this.plugin.getBungeeCord().firstPlace = (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                        for (final Player player10 : Bukkit.getOnlinePlayers()) {
                            final Iterator<String> iterator29 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                            while (iterator29.hasNext()) {
                                player10.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator29.next().replaceAll("%PLAYER1%", this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", this.plugin.getBungeeCord().thirdPlace)));
                            }
                            this.plugin.getBungeeCord().getNMSManager().showTitle(player10, 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                            Sounds.ENTITY_PLAYER_LEVELUP.play(player10, 1.0f, 1.0f);
                            if (player10.getName().equals(this.plugin.getBungeeCord().firstPlace)) {
                                new BukkitRunnable() {
                                    public void run() {
                                        final Iterator<String> iterator = PlayerListener.this.plugin.getBungeeCord().winnerCommands.iterator();
                                        while (iterator.hasNext()) {
                                            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player10.getName()));
                                        }
                                        PlayerListener.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player10, 1);
                                        Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player10));
                                    }
                                }.runTaskLater((Plugin)this.plugin, 5L);
                            }
                        }
                        this.plugin.getBungeeCord().getTemplateManager().explodePlot(s4);
                        new BukkitRunnable() {
                            public void run() {
                                PlayerListener.this.plugin.getBungeeCord().getTimerManager().stop();
                            }
                        }.runTaskLater((Plugin)this.plugin, 200L);
                    }
                    else {
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                        Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                        if (this.plugin.getBungeeCord().plots.size() == 2) {
                            this.plugin.getBungeeCord().thirdPlace = player.getName();
                        }
                        this.plugin.getBungeeCord().getTemplateManager().explodePlot(s4);
                        new BukkitRunnable() {
                            public void run() {
                                for (final Player player : Bukkit.getOnlinePlayers()) {
                                    if (PlayerListener.this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
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
                                        final Iterator iterator2 = player.getActivePotionEffects().iterator();
                                        while (iterator2.hasNext()) {
                                            player.removePotionEffect(iterator2.next().getType());
                                        }
                                        if (PlayerListener.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player) == null || !PlayerListener.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player).getName().equals("Players")) {
                                            continue;
                                        }
                                        final String str = PlayerListener.this.plugin.getBungeeCord().plots.get(player.getName());
                                        final Location location = new Location(Bukkit.getWorld(PlayerListener.this.plugin.getBungeeCord().currentMap), PlayerListener.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + PlayerListener.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.x"), PlayerListener.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + PlayerListener.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.y"), PlayerListener.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + PlayerListener.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.z"));
                                        location.setPitch((float)PlayerListener.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + PlayerListener.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.pitch"));
                                        location.setYaw((float)PlayerListener.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + PlayerListener.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.yaw"));
                                        player.teleport(location);
                                        player.setFallDistance(0.0f);
                                    }
                                }
                                PlayerListener.this.plugin.getBungeeCord().getTimerManager().showCaseTimer();
                            }
                        }.runTaskLater((Plugin)this.plugin, 5L);
                    }
                }
                else if (this.plugin.getBungeeCord().plots.size() == 1) {
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getGameStartTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getShowCaseTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getBuildTimerID());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID1());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID2());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID3());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID4());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID5());
                    Bukkit.getScheduler().cancelTask(this.plugin.getBungeeCord().getTimerManager().getJudgeTimerID6());
                    this.plugin.getBungeeCord().secondPlace = (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                    this.plugin.getBungeeCord().firstPlace = player.getName();
                    for (final Player player11 : Bukkit.getOnlinePlayers()) {
                        final Iterator<String> iterator31 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                        while (iterator31.hasNext()) {
                            player11.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator31.next().replaceAll("%PLAYER1%", this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", this.plugin.getBungeeCord().thirdPlace)));
                        }
                        this.plugin.getBungeeCord().getNMSManager().showTitle(player11, 0, 140, 20, ChatColor.translateAlternateColorCodes('&', "&e" + this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                        Sounds.ENTITY_PLAYER_LEVELUP.play(player11, 1.0f, 1.0f);
                        if (player11.getName().equals(this.plugin.getBungeeCord().firstPlace)) {
                            new BukkitRunnable() {
                                public void run() {
                                    final Iterator<String> iterator = PlayerListener.this.plugin.getBungeeCord().winnerCommands.iterator();
                                    while (iterator.hasNext()) {
                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player11.getName()));
                                    }
                                    PlayerListener.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player11, 1);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player11));
                                }
                            }.runTaskLater((Plugin)this.plugin, 5L);
                        }
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s4);
                    new BukkitRunnable() {
                        public void run() {
                            PlayerListener.this.plugin.getBungeeCord().getTimerManager().stop();
                        }
                    }.runTaskLater((Plugin)this.plugin, 200L);
                }
                else {
                    if (this.plugin.getBungeeCord().plots.size() == 2) {
                        this.plugin.getBungeeCord().thirdPlace = player.getName();
                    }
                    this.plugin.getBungeeCord().getTemplateManager().explodePlot(s4);
                }
            }
            final Iterator<String> iterator32 = this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
            while (iterator32.hasNext()) {
                this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator32.next());
            }
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', this.plugin.getBungeeCord().currentBuildDisplayName), this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + this.plugin.getBungeeCord().currentRound), this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
            int score4 = 7;
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[0]), this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex22) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[1]), this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex23) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[2]), this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex24) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[3]), this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex25) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[4]), this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex26) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[5]), this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex27) {}
            try {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[6]), this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                --score4;
            }
            catch (ArrayIndexOutOfBoundsException ex28) {}
            for (final Player player12 : Bukkit.getOnlinePlayers()) {
                if (player12 != player && score4 >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player12.getName())) {
                    this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player12.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score4);
                    --score4;
                }
            }
            final Iterator<Player> iterator34 = Bukkit.getOnlinePlayers().iterator();
            while (iterator34.hasNext()) {
                iterator34.next().setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
            }
            if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            else if (this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").hasPlayer((OfflinePlayer)player)) {
                this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").removePlayer((OfflinePlayer)player);
                this.plugin.getBungeeCord().getKitManager().setKit(player, null);
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
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator35 = player.getActivePotionEffects().iterator();
            while (iterator35.hasNext()) {
                player.removePotionEffect(iterator35.next().getType());
            }
            player.updateInventory();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final Action action = playerInteractEvent.getAction();
        if (this.plugin.getBungeeCord().gameState == GameState.WAITING) {
            if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
                    dataOutput.writeUTF("Connect");
                    dataOutput.writeUTF(this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", dataOutput.toByteArray());
                }
            }
            if (player.getItemInHand().getType() == Materials.BOOK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM"))) && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    this.plugin.getStatsManager().showStats(player, player);
                }
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.STARTING) {
            if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final ByteArrayDataOutput dataOutput2 = ByteStreams.newDataOutput();
                    dataOutput2.writeUTF("Connect");
                    dataOutput2.writeUTF(this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", dataOutput2.toByteArray());
                }
            }
            if (player.getItemInHand().getType() == Materials.BOOK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM"))) && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    this.plugin.getStatsManager().showStats(player, player);
                }
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.SHOWCASING) {
            if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final ByteArrayDataOutput dataOutput3 = ByteStreams.newDataOutput();
                    dataOutput3.writeUTF("Connect");
                    dataOutput3.writeUTF(this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", dataOutput3.toByteArray());
                }
            }
            if (player.getItemInHand().getType() == Materials.BOOK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM"))) && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    this.plugin.getStatsManager().showStats(player, player);
                }
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.BUILDING) {
            if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final ByteArrayDataOutput dataOutput4 = ByteStreams.newDataOutput();
                    dataOutput4.writeUTF("Connect");
                    dataOutput4.writeUTF(this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", dataOutput4.toByteArray());
                }
            }
            if (player.getItemInHand().getType() == Materials.BOOK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM"))) && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    this.plugin.getStatsManager().showStats(player, player);
                }
            }
            if (this.plugin.getBungeeCord().plots.containsKey(player.getName()) && this.plugin.getBungeeCord().playerPercent.containsKey(player.getName()) && this.plugin.getBungeeCord().playerPercent.get(player.getName()) < 100 && action == Action.LEFT_CLICK_BLOCK) {
                playerInteractEvent.setCancelled(true);
                final Block clickedBlock = playerInteractEvent.getClickedBlock();
                if (this.isBlockInside(clickedBlock.getLocation(), new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.z1")), new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.x2"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.y2"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.z2")))) {
                    final BlockState state = clickedBlock.getState();
                    final String string = state.getType().toString();
                    final byte rawData = state.getRawData();
                    if (string.equals("STANDING_BANNER") || string.equals("WALL_BANNER")) {
                        player.getInventory().addItem(new ItemStack[] { Materials.fromString("BANNER:" + (15 - ((Banner)state).getBaseColor().ordinal())).getItemStack(1) });
                    }
                    else if (string.equals("BED_BLOCK")) {
                        player.getInventory().addItem(new ItemStack[] { Materials.fromString("BED:" + (15 - ((Bed)state).getColor().ordinal())).getItemStack(1) });
                    }
                    else if (string.contains("DOUBLE_SLAB") || string.contains("DOUBLE_STEP") || string.contains("DOUBLE_STONE_SLAB2")) {
                        player.getInventory().addItem(new ItemStack[] { Materials.fromString(string + ":" + rawData).getItemStack(2) });
                    }
                    else if (string.equals("SKULL")) {
                        player.getInventory().addItem(new ItemStack[] { Materials.fromString("SKULL_ITEM:" + ((Skull)state).getSkullType().ordinal()).getItemStack(1) });
                    }
                    else {
                        player.getInventory().addItem(new ItemStack[] { Materials.fromString(string + ":" + rawData).getItemStack(1) });
                    }
                    clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, (Object)clickedBlock.getType());
                    state.setType(Materials.AIR.getType("block"));
                    state.update(true, false);
                    this.plugin.getBungeeCord().getNMSManager().updateBlockConnections(clickedBlock);
                    this.plugin.getBungeeCord().getTemplateManager().check(this.plugin.getBungeeCord().plots.get(player.getName()), player);
                }
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.JUDGING) {
            if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final ByteArrayDataOutput dataOutput5 = ByteStreams.newDataOutput();
                    dataOutput5.writeUTF("Connect");
                    dataOutput5.writeUTF(this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    player.sendPluginMessage((Plugin)this.plugin, "BungeeCord", dataOutput5.toByteArray());
                }
            }
            if (player.getItemInHand().getType() == Materials.BOOK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM"))) && this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                }
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    this.plugin.getStatsManager().showStats(player, player);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent playerMoveEvent) {
        final Player player = playerMoveEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState == GameState.GAME_STARTING) {
            if (this.plugin.getBungeeCord().plots.containsKey(player.getName()) && (playerMoveEvent.getTo().getX() != playerMoveEvent.getFrom().getX() || playerMoveEvent.getTo().getZ() != playerMoveEvent.getFrom().getZ())) {
                final Location from = playerMoveEvent.getFrom();
                from.setPitch(playerMoveEvent.getTo().getPitch());
                from.setYaw(playerMoveEvent.getTo().getYaw());
                playerMoveEvent.setTo(from);
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.SHOWCASING) {
            if (this.plugin.getBungeeCord().plots.containsKey(player.getName()) && !this.isPlayerInsideAsPlayer(playerMoveEvent.getTo(), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.x1"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.y1"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.z1")), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.x2"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.y2"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.z2")))) {
                final String str = this.plugin.getBungeeCord().plots.get(player.getName());
                final Location to = new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.z"));
                to.setPitch((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.pitch"));
                to.setYaw((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.yaw"));
                playerMoveEvent.setTo(to);
                player.setFallDistance(0.0f);
                this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_CANNOT_LEAVE")));
            }
        }
        else if (this.plugin.getBungeeCord().gameState == GameState.BUILDING && this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
            if (!this.isPlayerInsideAsPlayer(playerMoveEvent.getTo(), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.x1"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.y1"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.z1")), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.x2"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.y2"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".area.z2")))) {
                final String str2 = this.plugin.getBungeeCord().plots.get(player.getName());
                final Location to2 = new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str2 + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str2 + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str2 + ".spawnpoint.z"));
                to2.setPitch((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str2 + ".spawnpoint.pitch"));
                to2.setYaw((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str2 + ".spawnpoint.yaw"));
                playerMoveEvent.setTo(to2);
                player.setFallDistance(0.0f);
                this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_CANNOT_LEAVE")));
            }
            if (!this.plugin.getBungeeCord().playersDoubleJumpCooldowned.containsKey(player.getName())) {
                player.setAllowFlight(true);
            }
            else {
                player.setAllowFlight(false);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        if (playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            playerTeleportEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageEvent entityDamageEvent) {
        entityDamageEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent foodLevelChangeEvent) {
        foodLevelChangeEvent.setFoodLevel(20);
        foodLevelChangeEvent.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent playerToggleFlightEvent) {
        final Player player = playerToggleFlightEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState == GameState.BUILDING && !this.plugin.getBungeeCord().playersDoubleJumpCooldowned.containsKey(player.getName()) && this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
            playerToggleFlightEvent.setCancelled(true);
            try {
                NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_SURVIVALFLY);
            }
            catch (NoClassDefFoundError noClassDefFoundError) {}
            player.setVelocity(new Vector(0, 1, 0).multiply(1.05));
            Sounds.ENTITY_BLAZE_SHOOT.play(player, 1.0f, 1.0f);
            this.plugin.getBungeeCord().playersDoubleJumpCooldowned.put(player.getName(), 1.5f);
            this.plugin.getBungeeCord().getTimerManager().cooldownTimer(player.getName());
            new BukkitRunnable() {
                public void run() {
                    try {
                        NCPExemptionManager.unexempt(player, CheckType.MOVING_SURVIVALFLY);
                    }
                    catch (NoClassDefFoundError noClassDefFoundError) {}
                }
            }.runTaskLater((Plugin)this.plugin, 60L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent inventoryClickEvent) {
        final Player player = (Player)inventoryClickEvent.getWhoClicked();
        final InventoryView view = inventoryClickEvent.getView();
        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();
        if (currentItem != null) {
            if (view.getTitle().startsWith(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY")))) {
                inventoryClickEvent.setCancelled(true);
                player.updateInventory();
            }
            else if (player.hasPermission("sb.command.setup")) {
                if (this.plugin.getBungeeCord().gameState != GameState.WAITING && this.plugin.getBungeeCord().gameState != GameState.BUILDING) {
                    inventoryClickEvent.setCancelled(true);
                    player.updateInventory();
                }
                else {
                    final ItemStack itemStack = Materials.CLOCK.getItemStack(1);
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                    itemStack.setItemMeta(itemMeta);
                    final ItemStack itemStack2 = Materials.BOOK.getItemStack(1);
                    final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                    itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                    itemStack2.setItemMeta(itemMeta2);
                    if (currentItem.equals((Object)itemStack) || currentItem.equals((Object)itemStack2)) {
                        inventoryClickEvent.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
            else if (this.plugin.getBungeeCord().gameState != GameState.BUILDING) {
                inventoryClickEvent.setCancelled(true);
                player.updateInventory();
            }
            else {
                final ItemStack itemStack3 = Materials.CLOCK.getItemStack(1);
                final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                itemStack3.setItemMeta(itemMeta3);
                final ItemStack itemStack4 = Materials.BOOK.getItemStack(1);
                final ItemMeta itemMeta4 = itemStack4.getItemMeta();
                itemMeta4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                itemStack4.setItemMeta(itemMeta4);
                if (currentItem.equals((Object)itemStack3) || currentItem.equals((Object)itemStack4)) {
                    inventoryClickEvent.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent playerDropItemEvent) {
        final Player player = playerDropItemEvent.getPlayer();
        final ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        if (itemStack != null) {
            if (player.hasPermission("sb.command.setup")) {
                if (this.plugin.getBungeeCord().gameState != GameState.WAITING) {
                    playerDropItemEvent.setCancelled(true);
                    player.updateInventory();
                }
                else {
                    final ItemStack itemStack2 = Materials.CLOCK.getItemStack(1);
                    final ItemMeta itemMeta = itemStack2.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                    itemStack2.setItemMeta(itemMeta);
                    final ItemStack itemStack3 = Materials.BOOK.getItemStack(1);
                    final ItemMeta itemMeta2 = itemStack3.getItemMeta();
                    itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                    itemStack3.setItemMeta(itemMeta2);
                    if (itemStack.equals((Object)itemStack2) || itemStack.equals((Object)itemStack3)) {
                        playerDropItemEvent.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
            else {
                playerDropItemEvent.setCancelled(true);
                player.updateInventory();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent blockBreakEvent) {
        final Player player = blockBreakEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState != GameState.WAITING) {
            blockBreakEvent.getBlock().getRelative(BlockFace.UP).getState().update();
            blockBreakEvent.getBlock().getState().update();
            blockBreakEvent.getBlock().getRelative(BlockFace.DOWN).getState().update();
            blockBreakEvent.setCancelled(true);
        }
        else if (!player.isOp()) {
            blockBreakEvent.getBlock().getRelative(BlockFace.UP).getState().update();
            blockBreakEvent.getBlock().getState().update();
            blockBreakEvent.getBlock().getRelative(BlockFace.DOWN).getState().update();
            blockBreakEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent blockPlaceEvent) {
        final Player player = blockPlaceEvent.getPlayer();
        if (this.plugin.getBungeeCord().gameState == GameState.BUILDING) {
            if (this.isBlockInside(blockPlaceEvent.getBlock().getLocation(), new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.z1")), new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.x2"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.y2"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(player.getName()) + ".build-area.z2")))) {
                new BukkitRunnable() {
                    public void run() {
                        PlayerListener.this.plugin.getBungeeCord().getTemplateManager().check(PlayerListener.this.plugin.getBungeeCord().plots.get(player.getName()), player);
                    }
                }.runTaskLater((Plugin)this.plugin, 1L);
            }
            else {
                blockPlaceEvent.setCancelled(true);
            }
        }
        else if (!player.isOp()) {
            blockPlaceEvent.getBlock().getRelative(BlockFace.UP).getState().update();
            blockPlaceEvent.getBlock().getState().update();
            blockPlaceEvent.getBlock().getRelative(BlockFace.DOWN).getState().update();
            blockPlaceEvent.setCancelled(true);
        }
    }
    
    public Location getCenter(final Location location, final Location location2) {
        final double min = Math.min(location.getX(), location2.getX());
        final double min2 = Math.min(location.getY(), location2.getY());
        final double min3 = Math.min(location.getZ(), location2.getZ());
        return new Location(location.getWorld(), min + (Math.max(location.getX(), location2.getX()) - min) / 2.0, min2, min3 + (Math.max(location.getZ(), location2.getZ()) - min3) / 2.0);
    }
    
    public boolean isBlockInside(final Location location, final Location location2, final Location location3) {
        final double n = location.getBlockX();
        final double n2 = location.getBlockY();
        final double n3 = location.getBlockZ();
        final double n4 = Math.min(location2.getBlockX(), location3.getBlockX());
        final double n5 = Math.min(location2.getBlockY(), location3.getBlockY()) + 1;
        final double n6 = Math.min(location2.getBlockZ(), location3.getBlockZ());
        final double n7 = Math.max(location2.getBlockX(), location3.getBlockX());
        final double n8 = Math.max(location2.getBlockY(), location3.getBlockY());
        final double n9 = Math.max(location2.getBlockZ(), location3.getBlockZ());
        return n >= n4 && n2 >= n5 && n3 >= n6 && n <= n7 && n2 <= n8 && n3 <= n9;
    }
    
    public boolean isPlayerInsideAsPlayer(final Location location, final Location location2, final Location location3) {
        final double n = location.getBlockX();
        final double n2 = location.getBlockY();
        final double n3 = location.getBlockZ();
        final double n4 = Math.min(location2.getBlockX(), location3.getBlockX());
        final double n5 = Math.min(location2.getBlockY(), location3.getBlockY());
        final double n6 = Math.min(location2.getBlockZ(), location3.getBlockZ());
        final double n7 = Math.max(location2.getBlockX(), location3.getBlockX());
        final double n8 = Math.max(location2.getBlockY(), location3.getBlockY());
        final double n9 = Math.max(location2.getBlockZ(), location3.getBlockZ());
        return n >= n4 && n2 >= n5 && n3 >= n6 && n <= n7 && n2 <= n8 && n3 <= n9;
    }
    
    public boolean isPlayerInsideAsSpectator(final Location location, final Location location2, final Location location3) {
        final double n = location.getBlockX();
        final double n2 = location.getBlockY();
        final double n3 = location.getBlockZ();
        final double n4 = Math.min(location2.getBlockX(), location3.getBlockX()) - 2;
        final double n5 = Math.min(location2.getBlockY(), location3.getBlockY()) + 1;
        final double n6 = Math.min(location2.getBlockZ(), location3.getBlockZ()) - 2;
        final double n7 = Math.max(location2.getBlockX(), location3.getBlockX()) + 2;
        final double n8 = Math.max(location2.getBlockY(), location3.getBlockY());
        final double n9 = Math.max(location2.getBlockZ(), location3.getBlockZ()) + 2;
        return n >= n4 && n2 >= n5 && n3 >= n6 && n <= n7 && n2 <= n8 && n3 <= n9;
    }
}
