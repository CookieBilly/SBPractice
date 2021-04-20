 

package ws.billy.speedbuilderspractice.multiworld.managers;

import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.speedbuilderspractice.api.events.PlayerWinEvent;
import org.bukkit.entity.Player;
import ws.billy.speedbuilderspractice.api.events.PlayerLoseEvent;
import ws.billy.speedbuilderspractice.utils.StatsType;
import org.bukkit.command.CommandSender;
import java.util.Map;
import java.util.Random;
import org.bukkit.block.BlockState;
import java.util.Locale;
import ws.billy.speedbuilderspractice.utils.Materials;
import ws.billy.speedbuilderspractice.utils.Sounds;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import java.util.ArrayList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import java.util.Iterator;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.scoreboard.Scoreboard;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.GameStateChangeEvent;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class TimerManager
{
    private SpeedBuilders plugin;
    
    public TimerManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public void startTimer(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            arena.setGameState(GameState.STARTING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.STARTING, arena.getPlayers().size()));
            arena.setStartTime(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".start-time"));
            arena.setStartTimerID(new BukkitRunnable() {
                public void run() {
                    arena.setStartTime(arena.getStartTime() - 1);
                    for (final String s : arena.getPlayers()) {
                        if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s).getName()) != null) {
                            final Scoreboard scoreboard = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s).getName());
                            final Objective objective = scoreboard.getObjective("SpeedBuilders");
                            final Iterator iterator2 = scoreboard.getEntries().iterator();
                            while (iterator2.hasNext()) {
                                scoreboard.resetScores((String)iterator2.next());
                            }
                            if (arena.getGameState() == GameState.WAITING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (arena.getGameState() == GameState.STARTING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", TimerManager.this.timeString(arena.getStartTime()))));
                            }
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), scoreboard)).setScore(4);
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                            objective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + TimerManager.this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s), arena.getName()).toUpperCase())), scoreboard)).setScore(1);
                            Bukkit.getPlayer(s).setScoreboard(scoreboard);
                        }
                        else {
                            final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                            final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                            if (arena.getGameState() == GameState.WAITING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (arena.getGameState() == GameState.STARTING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", TimerManager.this.timeString(arena.getStartTime()))));
                            }
                            registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getPlayers().size() + "/" + arena.getMaxPlayers()), newScoreboard)).setScore(4);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                            registerNewObjective.getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + TimerManager.this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s), arena.getName()).toUpperCase())), newScoreboard)).setScore(1);
                            Bukkit.getPlayer(s).setScoreboard(newScoreboard);
                            arena.getPlayerStartScoreboard().put(Bukkit.getPlayer(s).getName(), newScoreboard);
                        }
                    }
                    if (arena.getStartTime() == 0) {
                        this.cancel();
                        arena.setStartTime(TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".start-time"));
                        TimerManager.this.gameStartTimer(arena.getName());
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 20L).getTaskId());
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void gameStartTimer(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            for (final Entity entity : Bukkit.getWorld(arena.getName()).getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
            this.plugin.getMultiWorld().getGuardianManager().spawnGuardian(arena.getName());
            this.plugin.getMultiWorld().getTemplateManager().resetPlots(arena.getName());
            final ArrayList<String> list = new ArrayList<String>();
            for (final String s2 : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots").getKeys(false)) {
                if (!s2.equals("guardian")) {
                    list.add(s2);
                }
            }
            for (final String s3 : arena.getPlayers()) {
                if (arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)Bukkit.getPlayer(s3)) != null && arena.getGameScoreboard().getPlayerTeam((OfflinePlayer)Bukkit.getPlayer(s3)).getName().equals("Players")) {
                    final String s4 = list.get(0);
                    arena.getPlots().put(Bukkit.getPlayer(s3).getName(), s4);
                    list.remove(s4);
                    final Location location = new Location(Bukkit.getWorld(arena.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + s4 + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + s4 + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + s4 + ".spawnpoint.z"));
                    location.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + s4 + ".spawnpoint.pitch"));
                    location.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + s4 + ".spawnpoint.yaw"));
                    Bukkit.getPlayer(s3).teleport(location);
                    Bukkit.getPlayer(s3).setFallDistance(0.0f);
                }
                final Iterator<String> iterator4 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_START_DESCRIPTION").iterator();
                while (iterator4.hasNext()) {
                    Bukkit.getPlayer(s3).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator4.next()));
                }
                Bukkit.getPlayer(s3).getInventory().setArmorContents((ItemStack[])null);
                Bukkit.getPlayer(s3).getInventory().clear();
                Bukkit.getPlayer(s3).setExp(0.0f);
                Bukkit.getPlayer(s3).setFireTicks(0);
                Bukkit.getPlayer(s3).setFoodLevel(20);
                Bukkit.getPlayer(s3).setGameMode(GameMode.SURVIVAL);
                Bukkit.getPlayer(s3).setHealth(20.0);
                Bukkit.getPlayer(s3).setLevel(0);
                Bukkit.getPlayer(s3).setAllowFlight(false);
                Bukkit.getPlayer(s3).setFlying(false);
                final Iterator iterator5 = Bukkit.getPlayer(s3).getActivePotionEffects().iterator();
                while (iterator5.hasNext()) {
                    Bukkit.getPlayer(s3).removePotionEffect(iterator5.next().getType());
                }
                this.plugin.getMultiWorld().getKitManager().giveKitItems(Bukkit.getPlayer(s3), arena.getName());
                Bukkit.getPlayer(s3).updateInventory();
                Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s3), 1.0f, 1.0f);
            }
            for (final String s5 : list) {
                if (this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + s5 + ".blocks")) {
                    final Iterator iterator7 = this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots." + s5 + ".blocks").getKeys(false).iterator();
                    while (iterator7.hasNext()) {
                        final String[] split = iterator7.next().split(",");
                        final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                        state.setType(Materials.AIR.getType("block"));
                        state.update(true, false);
                    }
                }
            }
            final Iterator iterator8 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator8.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator8.next());
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
                if (score >= 0 && !arena.getPlots().containsKey(s6)) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + s6), arena.getGameScoreboard())).setScore(score);
                    --score;
                }
            }
            final Iterator<String> iterator10 = arena.getPlayers().iterator();
            while (iterator10.hasNext()) {
                Bukkit.getPlayer((String)iterator10.next()).setScoreboard(arena.getGameScoreboard());
            }
            arena.setGameState(GameState.GAME_STARTING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.GAME_STARTING, arena.getPlots().size()));
            arena.setGameStartTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".game-start-time"));
            arena.setGameStartTimerID(new BukkitRunnable() {
                public void run() {
                    arena.setGameStartTime(arena.getGameStartTime() - 0.1f);
                    arena.setGameStartTime(Float.parseFloat(String.format(Locale.UK, "%.1f", arena.getGameStartTime())));
                    if (arena.getGameStartTime() <= 0.0f) {
                        this.cancel();
                        for (final String s : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showActionBar(Bukkit.getPlayer(s), ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-GAME_START")) + " &a\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c &f0.0 " + Translations.translate("MAIN-SECONDS"));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s), 1.0f, 2.0f);
                        }
                        TimerManager.this.showCaseTimer(arena.getName());
                        return;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-GAME_START")) + " ");
                    final int n = 24;
                    final double n2 = n * (arena.getGameStartTime() / TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".game-start-time"));
                    final double n3 = n - n2;
                    for (int n4 = 0; n4 < n3; ++n4) {
                        sb.append(ChatColor.translateAlternateColorCodes('&', "&a\u258c"));
                    }
                    for (int n5 = 0; n5 < n2; ++n5) {
                        sb.append(ChatColor.translateAlternateColorCodes('&', "&c\u258c"));
                    }
                    sb.append(ChatColor.translateAlternateColorCodes('&', " &f" + arena.getGameStartTime() + " " + Translations.translate("MAIN-SECONDS")));
                    for (final String s2 : arena.getPlayers()) {
                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showActionBar(Bukkit.getPlayer(s2), sb.toString());
                        if (Math.floor(arena.getGameStartTime()) == arena.getGameStartTime()) {
                            Sounds.BLOCK_NOTE_BLOCK_HAT.play(Bukkit.getPlayer(s2), 1.0f, 1.0f);
                        }
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 2L).getTaskId());
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void showCaseTimer(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final ArrayList<String> list = new ArrayList<String>();
            for (final String o : this.plugin.getConfigManager().getConfig("templates.yml").getConfigurationSection("templates").getKeys(false)) {
                if (!arena.getUsedTemplates().contains(o)) {
                    list.add(o);
                }
            }
            if (list.size() == 0) {
                final Iterator<String> iterator2 = arena.getUsedTemplates().iterator();
                while (iterator2.hasNext()) {
                    list.add(iterator2.next());
                }
            }
            if (list.size() > 1) {
                list.remove(arena.getCurrentBuildRawName());
            }
            try {
                arena.setCurrentBuildRawName((String)list.get(new Random().nextInt(list.size())));
                arena.setCurrentBuildDisplayName(this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + arena.getCurrentBuildRawName() + ".display-name"));
                arena.getCurrentBuildBlocks().clear();
                if (!arena.getUsedTemplates().contains(arena.getCurrentBuildRawName())) {
                    arena.getUsedTemplates().add(arena.getCurrentBuildRawName());
                }
                else {
                    arena.getUsedTemplates().remove(arena.getCurrentBuildRawName());
                }
            }
            catch (IllegalArgumentException ex) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Templates can not be found!"));
                this.plugin.getMultiWorld().getArenaManager().endArena(arena.getName());
                return;
            }
            arena.setCurrentRound(arena.getCurrentRound() + 1);
            final Iterator iterator3 = arena.getGameScoreboard().getEntries().iterator();
            while (iterator3.hasNext()) {
                arena.getGameScoreboard().resetScores((String)iterator3.next());
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
            catch (ArrayIndexOutOfBoundsException ex2) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex3) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex4) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex5) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex6) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex7) {}
            try {
                arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                --score;
            }
            catch (ArrayIndexOutOfBoundsException ex8) {}
            for (final String s2 : arena.getPlayers()) {
                if (score >= 0 && !arena.getPlots().containsKey(s2)) {
                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + s2), arena.getGameScoreboard())).setScore(score);
                    --score;
                }
            }
            for (final String s3 : arena.getPlayers()) {
                if (arena.getPlots().containsKey(Bukkit.getPlayer(s3).getName())) {
                    Bukkit.getPlayer(s3).getInventory().setArmorContents((ItemStack[])null);
                    Bukkit.getPlayer(s3).getInventory().clear();
                    Bukkit.getPlayer(s3).setExp(0.0f);
                    Bukkit.getPlayer(s3).setFireTicks(0);
                    Bukkit.getPlayer(s3).setFoodLevel(20);
                    Bukkit.getPlayer(s3).setGameMode(GameMode.SURVIVAL);
                    Bukkit.getPlayer(s3).setHealth(20.0);
                    Bukkit.getPlayer(s3).setLevel(0);
                    Bukkit.getPlayer(s3).setAllowFlight(false);
                    Bukkit.getPlayer(s3).setFlying(false);
                    final Iterator iterator6 = Bukkit.getPlayer(s3).getActivePotionEffects().iterator();
                    while (iterator6.hasNext()) {
                        Bukkit.getPlayer(s3).removePotionEffect(iterator6.next().getType());
                    }
                    Bukkit.getPlayer(s3).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-YOU_WILL_RECREATE_BUILD")));
                }
                Bukkit.getPlayer(s3).setScoreboard(arena.getGameScoreboard());
                this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s3), 0, 100, 0, "", "&6" + arena.getCurrentBuildDisplayName());
            }
            this.plugin.getMultiWorld().getGuardianManager().rotateGuardian(0.0f, arena.getName());
            this.plugin.getMultiWorld().getTemplateManager().unloadTemplate("guardian", arena.getName());
            final Iterator<Map.Entry<String, String>> iterator7 = arena.getPlots().entrySet().iterator();
            while (iterator7.hasNext()) {
                this.plugin.getMultiWorld().getTemplateManager().loadTemplate(iterator7.next().getValue(), arena.getName());
            }
            arena.setGameState(GameState.SHOWCASING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.SHOWCASING, arena.getPlots().size()));
            arena.setShowCaseTime(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".showcase-time"));
            arena.setShowCaseTimerID(new BukkitRunnable() {
                public void run() {
                    arena.setShowCaseTime(arena.getShowCaseTime() - 1);
                    if (arena.getShowCaseTime() == 6) {
                        for (final String s : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s), 1.0f, 0.5f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 5) {
                        for (final String s2 : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s2), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s2), 1.0f, 0.5f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 4) {
                        for (final String s3 : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s3), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s3), 1.0f, 0.6f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 3) {
                        for (final String s4 : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s4), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s4), 1.0f, 0.7f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 2) {
                        for (final String s5 : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s5), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s5), 1.0f, 0.8f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 1) {
                        for (final String s6 : arena.getPlayers()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s6), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + arena.getShowCaseTime()));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s6), 1.0f, 0.9f);
                        }
                    }
                    else if (arena.getShowCaseTime() == 0) {
                        this.cancel();
                        for (final String s7 : arena.getPlayers()) {
                            if (arena.getPlots().containsKey(Bukkit.getPlayer(s7).getName())) {
                                Bukkit.getPlayer(s7).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-RECREATE_BUILD")));
                            }
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s7), 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-VIEW_TIME_OVER")));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s7), 1.0f, 1.0f);
                        }
                        TimerManager.this.buildTimer(arena.getName());
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 20L).getTaskId());
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void buildTimer(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            for (final String s2 : arena.getPlayers()) {
                if (arena.getPlots().containsKey(Bukkit.getPlayer(s2).getName())) {
                    Bukkit.getPlayer(s2).getInventory().setArmorContents((ItemStack[])null);
                    Bukkit.getPlayer(s2).getInventory().clear();
                    Bukkit.getPlayer(s2).setExp(0.0f);
                    Bukkit.getPlayer(s2).setFireTicks(0);
                    Bukkit.getPlayer(s2).setFoodLevel(20);
                    Bukkit.getPlayer(s2).setGameMode(GameMode.SURVIVAL);
                    Bukkit.getPlayer(s2).setHealth(20.0);
                    Bukkit.getPlayer(s2).setLevel(0);
                    Bukkit.getPlayer(s2).setAllowFlight(false);
                    Bukkit.getPlayer(s2).setFlying(false);
                    final Iterator iterator2 = Bukkit.getPlayer(s2).getActivePotionEffects().iterator();
                    while (iterator2.hasNext()) {
                        Bukkit.getPlayer(s2).removePotionEffect(iterator2.next().getType());
                    }
                    Bukkit.getPlayer(s2).updateInventory();
                    Sounds.BLOCK_WOOD_BREAK.play(Bukkit.getPlayer(s2), 1.0f, 1.0f);
                    arena.getPlayerPercent().put(Bukkit.getPlayer(s2).getName(), 0);
                }
            }
            final Iterator<Map.Entry<String, String>> iterator3 = arena.getPlots().entrySet().iterator();
            while (iterator3.hasNext()) {
                this.plugin.getMultiWorld().getTemplateManager().unloadTemplate(iterator3.next().getValue(), arena.getName());
            }
            arena.setGameState(GameState.BUILDING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.BUILDING, arena.getPlots().size()));
            arena.setBuildTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".build-time"));
            if (arena.getCurrentRound() > 2) {
                arena.setBuildTimeSubtractor(arena.getBuildTimeSubtractor() + 1);
                arena.setBuildTime(arena.getBuildTime() - arena.getBuildTimeSubtractor());
            }
            arena.setBuildTimerID(new BukkitRunnable() {
                public void run() {
                    arena.setBuildTime(arena.getBuildTime() - 0.1f);
                    arena.setBuildTime(Float.parseFloat(String.format(Locale.UK, "%.1f", arena.getBuildTime())));
                    if (arena.getBuildTime() <= 0.0f) {
                        this.cancel();
                        for (final String s : arena.getPlayers()) {
                            if (arena.getPlots().containsKey(Bukkit.getPlayer(s).getName())) {
                                Bukkit.getPlayer(s).getInventory().setArmorContents((ItemStack[])null);
                                Bukkit.getPlayer(s).getInventory().clear();
                                Bukkit.getPlayer(s).setExp(0.0f);
                                Bukkit.getPlayer(s).setFireTicks(0);
                                Bukkit.getPlayer(s).setFoodLevel(20);
                                Bukkit.getPlayer(s).setGameMode(GameMode.SPECTATOR);
                                Bukkit.getPlayer(s).setHealth(20.0);
                                Bukkit.getPlayer(s).setLevel(0);
                                Bukkit.getPlayer(s).setAllowFlight(true);
                                Bukkit.getPlayer(s).setFlying(true);
                                final Iterator iterator2 = Bukkit.getPlayer(s).getActivePotionEffects().iterator();
                                while (iterator2.hasNext()) {
                                    Bukkit.getPlayer(s).removePotionEffect(iterator2.next().getType());
                                }
                            }
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showActionBar(Bukkit.getPlayer(s), ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-TIME_LEFT")) + " &c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c &f0.0 " + Translations.translate("MAIN-SECONDS"));
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showParticleEffect1(Bukkit.getPlayer(s), Bukkit.getPlayer(s).getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 10, 15, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-TIME_IS_UP")));
                            Sounds.ENTITY_ELDER_GUARDIAN_CURSE.play(Bukkit.getPlayer(s), 0.85f, 1.0f);
                        }
                        TimerManager.this.judgeTimer(arena.getName());
                        return;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-TIME_LEFT")) + " ");
                    final int n = 24;
                    final double n2 = n * (arena.getBuildTime() / (TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".build-time") - arena.getBuildTimeSubtractor()));
                    final double n3 = n - n2;
                    for (int n4 = 0; n4 < n2; ++n4) {
                        sb.append(ChatColor.translateAlternateColorCodes('&', "&a\u258c"));
                    }
                    for (int n5 = 0; n5 < n3; ++n5) {
                        sb.append(ChatColor.translateAlternateColorCodes('&', "&c\u258c"));
                    }
                    sb.append(ChatColor.translateAlternateColorCodes('&', " &f" + arena.getBuildTime() + " " + Translations.translate("MAIN-SECONDS")));
                    final Iterator<String> iterator3 = arena.getPlayers().iterator();
                    while (iterator3.hasNext()) {
                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showActionBar(Bukkit.getPlayer((String)iterator3.next()), sb.toString());
                    }
                    if (Math.floor(arena.getBuildTime()) == arena.getBuildTime()) {
                        if (arena.getBuildTime() == 5.0f) {
                            for (final String s2 : arena.getPlayers()) {
                                TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s2), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", arena.getBuildTime())));
                                Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s2), 1.0f, 0.5f);
                            }
                        }
                        else if (arena.getBuildTime() == 4.0f) {
                            for (final String s3 : arena.getPlayers()) {
                                TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s3), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", arena.getBuildTime())));
                                Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s3), 1.0f, 0.6f);
                            }
                        }
                        else if (arena.getBuildTime() == 3.0f) {
                            for (final String s4 : arena.getPlayers()) {
                                TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s4), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", arena.getBuildTime())));
                                Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s4), 1.0f, 0.7f);
                            }
                        }
                        else if (arena.getBuildTime() == 2.0f) {
                            for (final String s5 : arena.getPlayers()) {
                                TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s5), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", arena.getBuildTime())));
                                Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s5), 1.0f, 0.8f);
                            }
                        }
                        else if (arena.getBuildTime() == 1.0f) {
                            for (final String s6 : arena.getPlayers()) {
                                TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s6), 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", arena.getBuildTime())));
                                Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s6), 1.0f, 0.9f);
                            }
                        }
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 2L).getTaskId());
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void judgeTimer(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            String judgedPlayerName = null;
            int intValue = 101;
            for (final Map.Entry<String, Integer> entry : arena.getPlayerPercent().entrySet()) {
                if (entry.getValue() < intValue) {
                    judgedPlayerName = entry.getKey();
                    intValue = entry.getValue();
                }
            }
            if (intValue == 100) {
                this.guardianIsImpressed(arena.getName());
                return;
            }
            arena.setJudgedPlayerName(judgedPlayerName);
            if (Bukkit.getPlayer(arena.getJudgedPlayerName()) != null) {
                final Player player = Bukkit.getPlayer(arena.getJudgedPlayerName());
                final Iterator<String> iterator2 = this.plugin.getMultiWorld().loserCommands.iterator();
                while (iterator2.hasNext()) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator2.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
                }
                this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
                Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
            }
            this.plugin.getMultiWorld().getTemplateManager().loadTemplate("guardian", arena.getName());
            arena.setGameState(GameState.JUDGING);
            Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.JUDGING, arena.getPlots().size()));
            arena.setJudgeTime((float)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".judge-time"));
            arena.setJudgeTimerID1(new BukkitRunnable() {
                public void run() {
                    arena.setJudgeTime(arena.getJudgeTime() - 0.05f);
                    arena.setJudgeTime(Float.parseFloat(String.format(Locale.UK, "%.2f", arena.getJudgeTime())));
                    if (arena.getJudgeTime() <= 0.0f) {
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
                        Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
                        final Iterator<String> iterator = arena.getPlayers().iterator();
                        while (iterator.hasNext()) {
                            TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer((String)iterator.next()), 0, 20, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-PLAYER_WAS_ELIMINATED").replaceAll("%PLAYER%", arena.getJudgedPlayerName())));
                        }
                        TimerManager.this.plugin.getMultiWorld().getGuardianManager().laserGuardian(true, arena.getName());
                        arena.setJudgeTimerID2(new BukkitRunnable() {
                            public void run() {
                                for (final String s : arena.getPlayers()) {
                                    Sounds.ENTITY_ZOMBIE_VILLAGER_CURE.play(Bukkit.getPlayer(s), 1.0f, 1.0f);
                                    TimerManager.this.plugin.getMultiWorld().getNMSManager().showParticleEffect2(Bukkit.getPlayer(s), arena.getJudgedPlayerArmorStand().getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
                                }
                                arena.getJudgedPlayerArmorStand().remove();
                                TimerManager.this.plugin.getMultiWorld().getGuardianManager().laserGuardian(false, arena.getName());
                                if (TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".area")) {
                                    TimerManager.this.plugin.getMultiWorld().getTemplateManager().explodePlot(arena.getPlots().get(arena.getJudgedPlayerName()), arena.getName());
                                }
                                if (Bukkit.getPlayer(arena.getJudgedPlayerName()) != null) {
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).getInventory().setArmorContents((ItemStack[])null);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).getInventory().clear();
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setExp(0.0f);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setFireTicks(0);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setFoodLevel(20);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setGameMode(GameMode.SURVIVAL);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setHealth(20.0);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setLevel(0);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setAllowFlight(true);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).setFlying(true);
                                    final Iterator<PotionEffect> iterator2 = Bukkit.getPlayer(arena.getJudgedPlayerName()).getActivePotionEffects().iterator();
                                    while (iterator2.hasNext()) {
                                        Bukkit.getPlayer(arena.getJudgedPlayerName()).removePotionEffect(iterator2.next().getType());
                                    }
                                    final ItemStack itemStack = Materials.CLOCK.getItemStack(1);
                                    final ItemMeta itemMeta = itemStack.getItemMeta();
                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                                    itemStack.setItemMeta(itemMeta);
                                    if (TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                                        final ItemStack itemStack2 = Materials.BOOK.getItemStack(1);
                                        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                                        itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                                        itemStack2.setItemMeta(itemMeta2);
                                        Bukkit.getPlayer(arena.getJudgedPlayerName()).getInventory().setItem(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack2);
                                    }
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).getInventory().setItem(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack);
                                    Bukkit.getPlayer(arena.getJudgedPlayerName()).updateInventory();
                                    arena.getGameScoreboard().getTeam("Players").removePlayer((OfflinePlayer)Bukkit.getPlayer(arena.getJudgedPlayerName()));
                                    arena.getGameScoreboard().getTeam("Guardians").addPlayer((OfflinePlayer)Bukkit.getPlayer(arena.getJudgedPlayerName()));
                                    TimerManager.this.plugin.getMultiWorld().getNMSManager().setPlayerVisibility(Bukkit.getPlayer(arena.getJudgedPlayerName()), null, false);
                                }
                                arena.getPlayerPercent().remove(arena.getJudgedPlayerName());
                                arena.getPlots().remove(arena.getJudgedPlayerName());
                                if (arena.getPlots().size() == 1) {
                                    arena.setSecondPlace(arena.getJudgedPlayerName());
                                    arena.setFirstPlace((String)arena.getPlots().keySet().toArray()[0]);
                                    final Iterator<String> iterator3 = arena.getGameScoreboard().getEntries().iterator();
                                    while (iterator3.hasNext()) {
                                        arena.getGameScoreboard().resetScores((String)iterator3.next());
                                    }
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
                                    int score = 7;
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex2) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex3) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex4) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex5) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex6) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                                        --score;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex7) {}
                                    for (final String s2 : arena.getPlayers()) {
                                        if (score >= 0 && !arena.getPlots().containsKey(s2)) {
                                            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + s2), arena.getGameScoreboard())).setScore(score);
                                            --score;
                                        }
                                    }
                                    for (final String s3 : arena.getPlayers()) {
                                        final Iterator<String> iterator6 = TimerManager.this.plugin.getConfigManager().getConfig("messages.yml").getStringList("MULTIWORLD.MAIN-GAME_END_DESCRIPTION").iterator();
                                        while (iterator6.hasNext()) {
                                            Bukkit.getPlayer(s3).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator6.next().replaceAll("%PLAYER1%", arena.getFirstPlace()).replaceAll("%PLAYER2%", arena.getSecondPlace()).replaceAll("%PLAYER3%", arena.getThirdPlace())));
                                        }
                                        Bukkit.getPlayer(s3).setScoreboard(arena.getGameScoreboard());
                                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s3), 10, 130, 20, ChatColor.translateAlternateColorCodes('&', "&e" + arena.getFirstPlace()), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                                        Sounds.ENTITY_PLAYER_LEVELUP.play(Bukkit.getPlayer(s3), 1.0f, 1.0f);
                                        if (Bukkit.getPlayer(s3).getName().equals(arena.getFirstPlace())) {
                                            arena.setJudgeTimerID3(new BukkitRunnable() {
                                                public void run() {
                                                    final Iterator<String> iterator = TimerManager.this.plugin.getMultiWorld().winnerCommands.iterator();
                                                    while (iterator.hasNext()) {
                                                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", Bukkit.getPlayer(s3).getName()));
                                                    }
                                                    TimerManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, Bukkit.getPlayer(s3), 1);
                                                    Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(Bukkit.getPlayer(s3)));
                                                }
                                            }.runTaskLater((Plugin)TimerManager.this.plugin, 5L).getTaskId());
                                        }
                                    }
                                    arena.setJudgeTimerID4(new BukkitRunnable() {
                                        public void run() {
                                            TimerManager.this.plugin.getMultiWorld().getArenaManager().endArena(arena.getName());
                                        }
                                    }.runTaskLater((Plugin)TimerManager.this.plugin, 200L).getTaskId());
                                }
                                else {
                                    if (arena.getPlots().size() == 2) {
                                        arena.setThirdPlace(arena.getJudgedPlayerName());
                                    }
                                    final Iterator<String> iterator7 = arena.getGameScoreboard().getEntries().iterator();
                                    while (iterator7.hasNext()) {
                                        arena.getGameScoreboard().resetScores((String)iterator7.next());
                                    }
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), arena.getGameScoreboard())).setScore(15);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), arena.getGameScoreboard())).setScore(14);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', arena.getCurrentBuildDisplayName()), arena.getGameScoreboard())).setScore(13);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), arena.getGameScoreboard())).setScore(12);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), arena.getGameScoreboard())).setScore(11);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + arena.getCurrentRound()), arena.getGameScoreboard())).setScore(10);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), arena.getGameScoreboard())).setScore(9);
                                    arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), arena.getGameScoreboard())).setScore(8);
                                    int score2 = 7;
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[0]), arena.getGameScoreboard())).setScore(7);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex8) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[1]), arena.getGameScoreboard())).setScore(6);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex9) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[2]), arena.getGameScoreboard())).setScore(5);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex10) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[3]), arena.getGameScoreboard())).setScore(4);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex11) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[4]), arena.getGameScoreboard())).setScore(3);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex12) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[5]), arena.getGameScoreboard())).setScore(2);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex13) {}
                                    try {
                                        arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)arena.getPlots().keySet().toArray()[6]), arena.getGameScoreboard())).setScore(1);
                                        --score2;
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex14) {}
                                    for (final String s4 : arena.getPlayers()) {
                                        if (score2 >= 0 && !arena.getPlots().containsKey(s4)) {
                                            arena.getGameScoreboard().getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + s4), arena.getGameScoreboard())).setScore(score2);
                                            --score2;
                                        }
                                    }
                                    final Iterator<String> iterator9 = arena.getPlayers().iterator();
                                    while (iterator9.hasNext()) {
                                        Bukkit.getPlayer((String)iterator9.next()).setScoreboard(arena.getGameScoreboard());
                                    }
                                    arena.setJudgeTimerID5(new BukkitRunnable() {
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
                                                    final Location location = new Location(Bukkit.getWorld(arena.getName()), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.x"), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.y"), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.z"));
                                                    location.setPitch((float)TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.pitch"));
                                                    location.setYaw((float)TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.yaw"));
                                                    Bukkit.getPlayer(s).teleport(location);
                                                    Bukkit.getPlayer(s).setFallDistance(0.0f);
                                                }
                                            }
                                            TimerManager.this.showCaseTimer(arena.getName());
                                        }
                                    }.runTaskLater((Plugin)TimerManager.this.plugin, 60L).getTaskId());
                                }
                                arena.setJudgedPlayerName(null);
                            }
                        }.runTaskLater((Plugin)TimerManager.this.plugin, 100L).getTaskId());
                    }
                    else {
                        if (Math.floor(arena.getJudgeTime()) == arena.getJudgeTime() && arena.getJudgeTime() == 4.0f) {
                            for (final String s : arena.getPlayers()) {
                                if (arena.getPlots().containsKey(Bukkit.getPlayer(s).getName())) {
                                    if (arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) <= 100 && arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) >= 75) {
                                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&b").replaceAll("%PERCENT%", "" + arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()))));
                                    }
                                    if (arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) <= 74 && arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) >= 50) {
                                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&a").replaceAll("%PERCENT%", "" + arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()))));
                                    }
                                    if (arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) <= 49 && arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) >= 25) {
                                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&e").replaceAll("%PERCENT%", "" + arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()))));
                                    }
                                    if (arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) > 24 || arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()) < 0) {
                                        continue;
                                    }
                                    TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s), 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&c").replaceAll("%PERCENT%", "" + arena.getPlayerPercent().get(Bukkit.getPlayer(s).getName()))));
                                }
                            }
                        }
                        TimerManager.this.plugin.getMultiWorld().getGuardianManager().rotateGuardian(7.5f, arena.getName());
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 1L).getTaskId());
            arena.setJudgeTimerID6(new BukkitRunnable() {
                public void run() {
                    final Iterator<String> iterator = arena.getPlayers().iterator();
                    while (iterator.hasNext()) {
                        TimerManager.this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer((String)iterator.next()), 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-GWEN_IS_JUDGING")));
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 40L).getTaskId());
            this.plugin.getMultiWorld().getSignManager().updateSigns(arena.getName());
        }
    }
    
    public void guardianIsImpressed(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            Bukkit.getScheduler().cancelTask(arena.getGameStartTimerID());
            Bukkit.getScheduler().cancelTask(arena.getShowCaseTimerID());
            Bukkit.getScheduler().cancelTask(arena.getBuildTimerID());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID1());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID2());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID3());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID4());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID5());
            Bukkit.getScheduler().cancelTask(arena.getJudgeTimerID6());
            for (final String s2 : arena.getPlayers()) {
                if (arena.getPlots().containsKey(Bukkit.getPlayer(s2).getName())) {
                    Bukkit.getPlayer(s2).getInventory().setArmorContents((ItemStack[])null);
                    Bukkit.getPlayer(s2).getInventory().clear();
                    Bukkit.getPlayer(s2).setExp(0.0f);
                    Bukkit.getPlayer(s2).setFireTicks(0);
                    Bukkit.getPlayer(s2).setFoodLevel(20);
                    Bukkit.getPlayer(s2).setGameMode(GameMode.SPECTATOR);
                    Bukkit.getPlayer(s2).setHealth(20.0);
                    Bukkit.getPlayer(s2).setLevel(0);
                    Bukkit.getPlayer(s2).setAllowFlight(true);
                    Bukkit.getPlayer(s2).setFlying(true);
                    final Iterator iterator2 = Bukkit.getPlayer(s2).getActivePotionEffects().iterator();
                    while (iterator2.hasNext()) {
                        Bukkit.getPlayer(s2).removePotionEffect(iterator2.next().getType());
                    }
                }
                this.plugin.getMultiWorld().getNMSManager().showParticleEffect1(Bukkit.getPlayer(s2), Bukkit.getPlayer(s2).getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
                this.plugin.getMultiWorld().getNMSManager().showTitle(Bukkit.getPlayer(s2), 0, 100, 0, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-GWEN_IS_IMPRESSED")));
                Sounds.ENTITY_ELDER_GUARDIAN_CURSE.play(Bukkit.getPlayer(s2), 0.85f, 1.0f);
            }
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
                            final Location location = new Location(Bukkit.getWorld(arena.getName()), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.x"), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.y"), TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.z"));
                            location.setPitch((float)TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.pitch"));
                            location.setYaw((float)TimerManager.this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots." + str + ".spawnpoint.yaw"));
                            Bukkit.getPlayer(s).teleport(location);
                            Bukkit.getPlayer(s).setFallDistance(0.0f);
                        }
                    }
                    TimerManager.this.showCaseTimer(arena.getName());
                }
            }.runTaskLater((Plugin)this.plugin, 60L);
        }
    }
    
    public void cooldownTimer(final String s, final String s2) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            new BukkitRunnable() {
                public void run() {
                    if (arena.getPlayers().contains(s2)) {
                        final float float1 = Float.parseFloat(String.format(Locale.UK, "%.1f", arena.getPlayersDoubleJumpCooldowned().get(s2) - 0.1f));
                        arena.getPlayersDoubleJumpCooldowned().put(s2, float1);
                        if (float1 <= 0.0f) {
                            this.cancel();
                            arena.getPlayersDoubleJumpCooldowned().remove(s2);
                        }
                    }
                    else {
                        this.cancel();
                        arena.getPlayersDoubleJumpCooldowned().remove(s2);
                    }
                }
            }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
        }
    }
    
    public Location getCenter1(final Location location, final Location location2) {
        final double min = Math.min(location.getX(), location2.getX());
        final double min2 = Math.min(location.getY(), location2.getY());
        final double min3 = Math.min(location.getZ(), location2.getZ());
        return new Location(location.getWorld(), min + (Math.max(location.getX(), location2.getX()) - min) / 2.0, min2 + (Math.max(location.getY(), location2.getY()) - min2) / 2.0, min3 + (Math.max(location.getZ(), location2.getZ()) - min3) / 2.0);
    }
    
    public Location getCenter2(final Location location, final Location location2) {
        final double min = Math.min(location.getX(), location2.getX());
        final double min2 = Math.min(location.getY(), location2.getY());
        final double min3 = Math.min(location.getZ(), location2.getZ());
        return new Location(location.getWorld(), min + (Math.max(location.getX(), location2.getX()) - min) / 2.0, min2, min3 + (Math.max(location.getZ(), location2.getZ()) - min3) / 2.0);
    }
    
    public boolean playerIsInsideAsSpectator(final Location location, final Location location2, final Location location3) {
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
    
    public String timeString(final int i) {
        String s;
        if (i >= 60.0) {
            s = Math.floor(i / 60.0 * 10.0) / 10.0 + " " + Translations.translate("MAIN-MINUTES");
        }
        else {
            s = i + " " + Translations.translate("MAIN-SECONDS");
        }
        return s;
    }
}
