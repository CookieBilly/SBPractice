

package ws.billy.speedbuilderspractice.bungeecord.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.speedbuilderspractice.api.events.PlayerWinEvent;
import ws.billy.speedbuilderspractice.api.events.PlayerLoseEvent;
import ws.billy.speedbuilderspractice.utils.StatsType;
import org.bukkit.command.CommandSender;
import ws.billy.speedbuilderspractice.bungeecord.BungeeCord;
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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.GameStateChangeEvent;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class TimerManager
{
    public SpeedBuilders plugin;
    public int startTime;
    public int startTimerID;
    public float gameStartTime;
    public int gameStartTimerID;
    public int showCaseTime;
    public int showCaseTimerID;
    public float buildTime;
    public int buildTimerID;
    public float judgeTime;
    public int judgeTimerID1;
    public int judgeTimerID2;
    public int judgeTimerID3;
    public int judgeTimerID4;
    public int judgeTimerID5;
    public int judgeTimerID6;
    
    public TimerManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public void startTimer() {
        this.plugin.getBungeeCord().gameState = GameState.STARTING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.STARTING, Bukkit.getOnlinePlayers().size()));
        this.startTime = this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.start-time");
        this.startTimerID = new BukkitRunnable() {
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (TimerManager.this.plugin.getBungeeCord().playerStartScoreboard.get(player.getName()) != null) {
                        final Scoreboard scoreboard = TimerManager.this.plugin.getBungeeCord().playerStartScoreboard.get(player.getName());
                        final Objective objective = scoreboard.getObjective("SpeedBuilders");
                        final Iterator iterator2 = scoreboard.getEntries().iterator();
                        while (iterator2.hasNext()) {
                            scoreboard.resetScores((String)iterator2.next());
                        }
                        if (TimerManager.this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                        }
                        if (TimerManager.this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", TimerManager.this.plugin.getBungeeCord().getTimerManager().timeString(TimerManager.this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                        }
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + TimerManager.this.plugin.getBungeeCord().maxPlayers), scoreboard)).setScore(4);
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                        objective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + TimerManager.this.plugin.getBungeeCord().getKitManager().getKit(player).toUpperCase())), scoreboard)).setScore(1);
                        player.setScoreboard(scoreboard);
                    }
                    else {
                        final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                        if (TimerManager.this.plugin.getBungeeCord().gameState == GameState.WAITING) {
                            registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                        }
                        if (TimerManager.this.plugin.getBungeeCord().gameState == GameState.STARTING) {
                            registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", TimerManager.this.plugin.getBungeeCord().getTimerManager().timeString(TimerManager.this.plugin.getBungeeCord().getTimerManager().getStartTime()))));
                        }
                        registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + TimerManager.this.plugin.getBungeeCord().maxPlayers), newScoreboard)).setScore(4);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                        registerNewObjective.getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + TimerManager.this.plugin.getBungeeCord().getKitManager().getKit(player).toUpperCase())), newScoreboard)).setScore(1);
                        player.setScoreboard(newScoreboard);
                        TimerManager.this.plugin.getBungeeCord().playerStartScoreboard.put(player.getName(), newScoreboard);
                    }
                }
                if (TimerManager.this.startTime == 0) {
                    this.cancel();
                    TimerManager.this.startTime = TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.start-time");
                    TimerManager.this.gameStartTimer();
                    return;
                }
                final TimerManager this$0 = TimerManager.this;
                --this$0.startTime;
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L).getTaskId();
    }
    
    public void gameStartTimer() {
        for (final Entity entity : Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getEntities()) {
            if (entity.getType() != EntityType.PLAYER) {
                entity.remove();
            }
        }
        this.plugin.getBungeeCord().getGuardianManager().spawnGuardian();
        this.plugin.getBungeeCord().getTemplateManager().resetPlots(this.plugin.getBungeeCord().currentMap);
        final ArrayList<String> list = new ArrayList<String>();
        for (final String s : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + this.plugin.getBungeeCord().currentMap + ".plots").getKeys(false)) {
            if (!s.equals("guardian")) {
                list.add(s);
            }
        }
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player) != null && this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player).getName().equals("Players")) {
                final String s2 = list.get(0);
                this.plugin.getBungeeCord().plots.put(player.getName(), s2);
                list.remove(s2);
                final Location location = new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s2 + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s2 + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s2 + ".spawnpoint.z"));
                location.setPitch((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s2 + ".spawnpoint.pitch"));
                location.setYaw((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s2 + ".spawnpoint.yaw"));
                player.teleport(location);
                player.setFallDistance(0.0f);
            }
            final Iterator<String> iterator4 = this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_START_DESCRIPTION").iterator();
            while (iterator4.hasNext()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator4.next()));
            }
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator5 = player.getActivePotionEffects().iterator();
            while (iterator5.hasNext()) {
                player.removePotionEffect(iterator5.next().getType());
            }
            this.plugin.getBungeeCord().getKitManager().giveKitItems(player);
            player.updateInventory();
            Sounds.ENTITY_PLAYER_LEVELUP.play(player, 1.0f, 1.0f);
        }
        for (final String s3 : list) {
            if (this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s3 + ".blocks")) {
                final Iterator iterator7 = this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s3 + ".blocks").getKeys(false).iterator();
                while (iterator7.hasNext()) {
                    final String[] split = iterator7.next().split(",");
                    final BlockState state = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                    state.setType(Materials.AIR.getType("item"));
                    state.update(true, false);
                }
            }
        }
        final Iterator<String> iterator8 = this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
        while (iterator8.hasNext()) {
            this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator8.next());
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
        for (final Player player2 : Bukkit.getOnlinePlayers()) {
            if (score >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player2.getName())) {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player2.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score);
                --score;
            }
        }
        final Iterator<Player> iterator10 = Bukkit.getOnlinePlayers().iterator();
        while (iterator10.hasNext()) {
            iterator10.next().setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
        }
        this.plugin.getBungeeCord().gameState = GameState.GAME_STARTING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.GAME_STARTING, this.plugin.getBungeeCord().plots.size()));
        this.gameStartTime = (float)this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.game-start-time");
        this.gameStartTimerID = new BukkitRunnable() {
            public void run() {
                TimerManager.this.gameStartTime -= 0.1f;
                TimerManager.this.gameStartTime = Float.parseFloat(String.format(Locale.UK, "%.1f", TimerManager.this.gameStartTime));
                if (TimerManager.this.gameStartTime <= 0.0f) {
                    this.cancel();
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showActionBar(player, ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-GAME_START")) + " &a\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c &f0.0 " + Translations.translate("MAIN-SECONDS"));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player, 1.0f, 2.0f);
                    }
                    TimerManager.this.showCaseTimer();
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-GAME_START")) + " ");
                final int n = 24;
                final double n2 = n * (TimerManager.this.gameStartTime / (float)TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getDouble("bungeecord.game-start-time"));
                final double n3 = n - n2;
                for (int n4 = 0; n4 < n3; ++n4) {
                    sb.append(ChatColor.translateAlternateColorCodes('&', "&a\u258c"));
                }
                for (int n5 = 0; n5 < n2; ++n5) {
                    sb.append(ChatColor.translateAlternateColorCodes('&', "&c\u258c"));
                }
                sb.append(ChatColor.translateAlternateColorCodes('&', " &f" + TimerManager.this.gameStartTime + " " + Translations.translate("MAIN-SECONDS")));
                for (final Player player2 : Bukkit.getOnlinePlayers()) {
                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showActionBar(player2, sb.toString());
                    if (Math.floor(TimerManager.this.gameStartTime) == TimerManager.this.gameStartTime) {
                        Sounds.BLOCK_NOTE_BLOCK_HAT.play(player2, 1.0f, 1.0f);
                    }
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L).getTaskId();
    }
    
    public void showCaseTimer() {
        final ArrayList<String> list = new ArrayList<String>();
        for (final String o : this.plugin.getConfigManager().getConfig("templates.yml").getConfigurationSection("templates").getKeys(false)) {
            if (!this.plugin.getBungeeCord().usedTemplates.contains(o)) {
                list.add(o);
            }
        }
        if (list.size() == 0) {
            final Iterator<String> iterator2 = this.plugin.getBungeeCord().usedTemplates.iterator();
            while (iterator2.hasNext()) {
                list.add(iterator2.next());
            }
        }
        if (list.size() > 1) {
            list.remove(this.plugin.getBungeeCord().currentBuildRawName);
        }
        try {
            this.plugin.getBungeeCord().currentBuildRawName = (String)list.get(new Random().nextInt(list.size()));
            this.plugin.getBungeeCord().currentBuildDisplayName = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".display-name");
            this.plugin.getBungeeCord().currentBuildBlocks.clear();
            if (!this.plugin.getBungeeCord().usedTemplates.contains(this.plugin.getBungeeCord().currentBuildRawName)) {
                this.plugin.getBungeeCord().usedTemplates.add(this.plugin.getBungeeCord().currentBuildRawName);
            }
            else {
                this.plugin.getBungeeCord().usedTemplates.remove(this.plugin.getBungeeCord().currentBuildRawName);
            }
        }
        catch (IllegalArgumentException ex) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Templates can not be found!"));
            this.stop();
            return;
        }
        final BungeeCord bungeeCord = this.plugin.getBungeeCord();
        ++bungeeCord.currentRound;
        final Iterator<String> iterator3 = (Iterator<String>)this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
        while (iterator3.hasNext()) {
            this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator3.next());
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
        catch (ArrayIndexOutOfBoundsException ex2) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[1]), this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex3) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[2]), this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex4) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[3]), this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex5) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[4]), this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex6) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[5]), this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex7) {}
        try {
            this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)this.plugin.getBungeeCord().plots.keySet().toArray()[6]), this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
            --score;
        }
        catch (ArrayIndexOutOfBoundsException ex8) {}
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (score >= 0 && !this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
                this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player.getName()), this.plugin.getBungeeCord().gameScoreboard)).setScore(score);
                --score;
            }
        }
        for (final Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getBungeeCord().plots.containsKey(player2.getName())) {
                player2.getInventory().setArmorContents((ItemStack[])null);
                player2.getInventory().clear();
                player2.setExp(0.0f);
                player2.setFireTicks(0);
                player2.setFoodLevel(20);
                player2.setGameMode(GameMode.SURVIVAL);
                player2.setHealth(20.0);
                player2.setLevel(0);
                player2.setAllowFlight(false);
                player2.setFlying(false);
                final Iterator iterator6 = player2.getActivePotionEffects().iterator();
                while (iterator6.hasNext()) {
                    player2.removePotionEffect(iterator6.next().getType());
                }
                player2.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-YOU_WILL_RECREATE_BUILD")));
            }
            player2.setScoreboard(this.plugin.getBungeeCord().gameScoreboard);
            this.plugin.getBungeeCord().getNMSManager().showTitle(player2, 0, 100, 0, "", "&6" + this.plugin.getBungeeCord().currentBuildDisplayName);
        }
        this.plugin.getBungeeCord().getGuardianManager().rotateGuardian(0.0f);
        this.plugin.getBungeeCord().getTemplateManager().unloadTemplate("guardian");
        final Iterator<Map.Entry<String, String>> iterator7 = this.plugin.getBungeeCord().plots.entrySet().iterator();
        while (iterator7.hasNext()) {
            this.plugin.getBungeeCord().getTemplateManager().loadTemplate(iterator7.next().getValue());
        }
        this.plugin.getBungeeCord().gameState = GameState.SHOWCASING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.SHOWCASING, this.plugin.getBungeeCord().plots.size()));
        this.showCaseTime = this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.showcase-time");
        this.showCaseTimerID = new BukkitRunnable() {
            public void run() {
                final TimerManager this$0 = TimerManager.this;
                --this$0.showCaseTime;
                if (TimerManager.this.showCaseTime == 6) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player, 1.0f, 0.5f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 5) {
                    for (final Player player2 : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player2, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player2, 1.0f, 0.5f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 4) {
                    for (final Player player3 : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player3, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player3, 1.0f, 0.6f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 3) {
                    for (final Player player4 : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player4, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player4, 1.0f, 0.7f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 2) {
                    for (final Player player5 : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player5, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player5, 1.0f, 0.8f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 1) {
                    for (final Player player6 : Bukkit.getOnlinePlayers()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player6, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + TimerManager.this.showCaseTime));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player6, 1.0f, 0.9f);
                    }
                }
                else if (TimerManager.this.showCaseTime == 0) {
                    this.cancel();
                    for (final Player player7 : Bukkit.getOnlinePlayers()) {
                        if (TimerManager.this.plugin.getBungeeCord().plots.containsKey(player7.getName())) {
                            player7.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-RECREATE_BUILD")));
                        }
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player7, 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-VIEW_TIME_OVER")));
                        Sounds.BLOCK_NOTE_BLOCK_PLING.play(player7, 1.0f, 1.0f);
                    }
                    TimerManager.this.buildTimer();
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L).getTaskId();
    }
    
    public void buildTimer() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
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
                player.updateInventory();
                Sounds.BLOCK_WOOD_BREAK.play(player, 1.0f, 1.0f);
                this.plugin.getBungeeCord().playerPercent.put(player.getName(), 0);
            }
        }
        final Iterator<Map.Entry<String, String>> iterator3 = this.plugin.getBungeeCord().plots.entrySet().iterator();
        while (iterator3.hasNext()) {
            this.plugin.getBungeeCord().getTemplateManager().unloadTemplate(iterator3.next().getValue());
        }
        this.plugin.getBungeeCord().gameState = GameState.BUILDING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.BUILDING, this.plugin.getBungeeCord().plots.size()));
        this.buildTime = (float)this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.build-time");
        if (this.plugin.getBungeeCord().currentRound > 2) {
            final BungeeCord bungeeCord = this.plugin.getBungeeCord();
            ++bungeeCord.buildTimeSubtractor;
            this.buildTime -= this.plugin.getBungeeCord().buildTimeSubtractor;
        }
        this.buildTimerID = new BukkitRunnable() {
            public void run() {
                TimerManager.this.buildTime -= 0.1f;
                TimerManager.this.buildTime = Float.parseFloat(String.format(Locale.UK, "%.1f", TimerManager.this.buildTime));
                if (TimerManager.this.buildTime <= 0.0f) {
                    this.cancel();
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (TimerManager.this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
                            player.getInventory().setArmorContents((ItemStack[])null);
                            player.getInventory().clear();
                            player.setExp(0.0f);
                            player.setFireTicks(0);
                            player.setFoodLevel(20);
                            player.setGameMode(GameMode.SPECTATOR);
                            player.setHealth(20.0);
                            player.setLevel(0);
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            final Iterator iterator2 = player.getActivePotionEffects().iterator();
                            while (iterator2.hasNext()) {
                                player.removePotionEffect(iterator2.next().getType());
                            }
                        }
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showActionBar(player, ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-TIME_LEFT")) + " &c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c\u258c &f0.0 " + Translations.translate("MAIN-SECONDS"));
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showParticleEffect1(player, player.getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 10, 15, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-TIME_IS_UP")));
                        Sounds.ENTITY_ELDER_GUARDIAN_CURSE.play(player, 0.85f, 1.0f);
                    }
                    TimerManager.this.judgeTimer();
                    return;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.translateAlternateColorCodes('&', Translations.translate("ABAR-TIME_LEFT")) + " ");
                final int n = 24;
                final double n2 = n * (TimerManager.this.buildTime / (TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.build-time") - TimerManager.this.plugin.getBungeeCord().buildTimeSubtractor));
                final double n3 = n - n2;
                for (int n4 = 0; n4 < n2; ++n4) {
                    sb.append(ChatColor.translateAlternateColorCodes('&', "&a\u258c"));
                }
                for (int n5 = 0; n5 < n3; ++n5) {
                    sb.append(ChatColor.translateAlternateColorCodes('&', "&c\u258c"));
                }
                sb.append(ChatColor.translateAlternateColorCodes('&', " &f" + TimerManager.this.buildTime + " " + Translations.translate("MAIN-SECONDS")));
                final Iterator<Player> iterator3 = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
                while (iterator3.hasNext()) {
                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showActionBar(iterator3.next(), sb.toString());
                }
                if (Math.floor(TimerManager.this.buildTime) == TimerManager.this.buildTime) {
                    if (TimerManager.this.buildTime == 5.0f) {
                        for (final Player player2 : Bukkit.getOnlinePlayers()) {
                            TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player2, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", TimerManager.this.buildTime)));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(player2, 1.0f, 0.5f);
                        }
                    }
                    else if (TimerManager.this.buildTime == 4.0f) {
                        for (final Player player3 : Bukkit.getOnlinePlayers()) {
                            TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player3, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", TimerManager.this.buildTime)));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(player3, 1.0f, 0.6f);
                        }
                    }
                    else if (TimerManager.this.buildTime == 3.0f) {
                        for (final Player player4 : Bukkit.getOnlinePlayers()) {
                            TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player4, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", TimerManager.this.buildTime)));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(player4, 1.0f, 0.7f);
                        }
                    }
                    else if (TimerManager.this.buildTime == 2.0f) {
                        for (final Player player5 : Bukkit.getOnlinePlayers()) {
                            TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player5, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", TimerManager.this.buildTime)));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(player5, 1.0f, 0.8f);
                        }
                    }
                    else if (TimerManager.this.buildTime == 1.0f) {
                        for (final Player player6 : Bukkit.getOnlinePlayers()) {
                            TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player6, 0, 40, 0, "", ChatColor.translateAlternateColorCodes('&', "&a" + String.format(Locale.UK, "%.0f", TimerManager.this.buildTime)));
                            Sounds.BLOCK_NOTE_BLOCK_PLING.play(player6, 1.0f, 0.9f);
                        }
                    }
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L).getTaskId();
    }
    
    public void judgeTimer() {
        String judgedPlayerName = null;
        int intValue = 101;
        for (final Map.Entry<String, Integer> entry : this.plugin.getBungeeCord().playerPercent.entrySet()) {
            if (entry.getValue() < intValue) {
                judgedPlayerName = entry.getKey();
                intValue = entry.getValue();
            }
        }
        if (intValue == 100) {
            this.plugin.getBungeeCord().getTimerManager().guardianIsImpressed();
            return;
        }
        this.plugin.getBungeeCord().judgedPlayerName = judgedPlayerName;
        if (Bukkit.getPlayer(this.plugin.getBungeeCord().judgedPlayerName) != null) {
            final Player player = Bukkit.getPlayer(this.plugin.getBungeeCord().judgedPlayerName);
            final Iterator<String> iterator2 = this.plugin.getBungeeCord().loserCommands.iterator();
            while (iterator2.hasNext()) {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator2.next().replaceFirst("/", "").replaceAll("%PLAYER%", player.getName()));
            }
            this.plugin.getStatsManager().incrementStat(StatsType.LOSSES, player, 1);
            Bukkit.getPluginManager().callEvent((Event)new PlayerLoseEvent(player));
        }
        this.plugin.getBungeeCord().getTemplateManager().loadTemplate("guardian");
        this.plugin.getBungeeCord().gameState = GameState.JUDGING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.JUDGING, this.plugin.getBungeeCord().plots.size()));
        this.judgeTime = (float)this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.judge-time");
        this.judgeTimerID1 = new BukkitRunnable() {
            public void run() {
                TimerManager.this.judgeTime -= 0.05f;
                TimerManager.this.judgeTime = Float.parseFloat(String.format(Locale.UK, "%.2f", TimerManager.this.judgeTime));
                if (TimerManager.this.judgeTime <= 0.0f) {
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID1);
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID2);
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID3);
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID4);
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID5);
                    Bukkit.getScheduler().cancelTask(TimerManager.this.judgeTimerID6);
                    final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
                    while (iterator.hasNext()) {
                        TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(iterator.next(), 0, 20, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-PLAYER_WAS_ELIMINATED").replaceAll("%PLAYER%", TimerManager.this.plugin.getBungeeCord().judgedPlayerName)));
                    }
                    TimerManager.this.plugin.getBungeeCord().getGuardianManager().laserGuardian(true);
                    TimerManager.this.judgeTimerID2 = new BukkitRunnable() {
                        public void run() {
                            for (final Player player : Bukkit.getOnlinePlayers()) {
                                Sounds.ENTITY_ZOMBIE_VILLAGER_CURE.play(player, 1.0f, 1.0f);
                                TimerManager.this.plugin.getBungeeCord().getNMSManager().showParticleEffect2(player, TimerManager.this.plugin.getBungeeCord().judgedPlayerArmorStand.getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
                            }
                            TimerManager.this.plugin.getBungeeCord().judgedPlayerArmorStand.remove();
                            TimerManager.this.plugin.getBungeeCord().getGuardianManager().laserGuardian(false);
                            if (TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + TimerManager.this.plugin.getBungeeCord().plots.get(TimerManager.this.plugin.getBungeeCord().judgedPlayerName) + ".area")) {
                                TimerManager.this.plugin.getBungeeCord().getTemplateManager().explodePlot(TimerManager.this.plugin.getBungeeCord().plots.get(TimerManager.this.plugin.getBungeeCord().judgedPlayerName));
                            }
                            if (Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName) != null) {
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).getInventory().setArmorContents((ItemStack[])null);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).getInventory().clear();
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setExp(0.0f);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setFireTicks(0);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setFoodLevel(20);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setGameMode(GameMode.SURVIVAL);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setHealth(20.0);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setLevel(0);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setAllowFlight(true);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).setFlying(true);
                                final Iterator<PotionEffect> iterator2 = Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).getActivePotionEffects().iterator();
                                while (iterator2.hasNext()) {
                                    Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).removePotionEffect(iterator2.next().getType());
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
                                    Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).getInventory().setItem(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack2);
                                }
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).getInventory().setItem(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack);
                                Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName).updateInventory();
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getTeam("Players").removePlayer((OfflinePlayer)Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName));
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getTeam("Guardians").addPlayer((OfflinePlayer)Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName));
                                TimerManager.this.plugin.getBungeeCord().getNMSManager().setPlayerVisibility(Bukkit.getPlayer(TimerManager.this.plugin.getBungeeCord().judgedPlayerName), null, false);
                            }
                            TimerManager.this.plugin.getBungeeCord().playerPercent.remove(TimerManager.this.plugin.getBungeeCord().judgedPlayerName);
                            TimerManager.this.plugin.getBungeeCord().plots.remove(TimerManager.this.plugin.getBungeeCord().judgedPlayerName);
                            if (TimerManager.this.plugin.getBungeeCord().plots.size() == 1) {
                                TimerManager.this.plugin.getBungeeCord().secondPlace = TimerManager.this.plugin.getBungeeCord().judgedPlayerName;
                                TimerManager.this.plugin.getBungeeCord().firstPlace = (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[0];
                                final Iterator<String> iterator3 = TimerManager.this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
                                while (iterator3.hasNext()) {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator3.next());
                                }
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', TimerManager.this.plugin.getBungeeCord().currentBuildDisplayName), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + TimerManager.this.plugin.getBungeeCord().currentRound), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
                                int score = 7;
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[0]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[1]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex2) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[2]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex3) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[3]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex4) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[4]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex5) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[5]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex6) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[6]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                                    --score;
                                }
                                catch (ArrayIndexOutOfBoundsException ex7) {}
                                for (final Player player2 : Bukkit.getOnlinePlayers()) {
                                    if (score >= 0 && !TimerManager.this.plugin.getBungeeCord().plots.containsKey(player2.getName())) {
                                        TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player2.getName()), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(score);
                                        --score;
                                    }
                                }
                                for (final Player player3 : Bukkit.getOnlinePlayers()) {
                                    final Iterator<String> iterator6 = TimerManager.this.plugin.getConfigManager().getConfig("messages.yml").getStringList("BUNGEECORD.MAIN-GAME_END_DESCRIPTION").iterator();
                                    while (iterator6.hasNext()) {
                                        player3.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + iterator6.next().replaceAll("%PLAYER1%", TimerManager.this.plugin.getBungeeCord().firstPlace).replaceAll("%PLAYER2%", TimerManager.this.plugin.getBungeeCord().secondPlace).replaceAll("%PLAYER3%", TimerManager.this.plugin.getBungeeCord().thirdPlace)));
                                    }
                                    player3.setScoreboard(TimerManager.this.plugin.getBungeeCord().gameScoreboard);
                                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player3, 10, 130, 20, ChatColor.translateAlternateColorCodes('&', "&e" + TimerManager.this.plugin.getBungeeCord().firstPlace), ChatColor.translateAlternateColorCodes('&', "&e" + Translations.translate("TITLE-WON_THE_GAME")));
                                    Sounds.ENTITY_PLAYER_LEVELUP.play(player3, 1.0f, 1.0f);
                                    if (player3.getName().equals(TimerManager.this.plugin.getBungeeCord().firstPlace)) {
                                        TimerManager.this.judgeTimerID3 = new BukkitRunnable() {
                                            public void run() {
                                                final Iterator<String> iterator = TimerManager.this.plugin.getBungeeCord().winnerCommands.iterator();
                                                while (iterator.hasNext()) {
                                                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), iterator.next().replaceFirst("/", "").replaceAll("%PLAYER%", player3.getName()));
                                                }
                                                TimerManager.this.plugin.getStatsManager().incrementStat(StatsType.WINS, player3, 1);
                                                Bukkit.getPluginManager().callEvent((Event)new PlayerWinEvent(player3));
                                            }
                                        }.runTaskLater((Plugin)TimerManager.this.plugin, 5L).getTaskId();
                                    }
                                }
                                TimerManager.this.judgeTimerID4 = new BukkitRunnable() {
                                    public void run() {
                                        TimerManager.this.stop();
                                    }
                                }.runTaskLater((Plugin)TimerManager.this.plugin, 200L).getTaskId();
                            }
                            else {
                                if (TimerManager.this.plugin.getBungeeCord().plots.size() == 2) {
                                    TimerManager.this.plugin.getBungeeCord().thirdPlace = TimerManager.this.plugin.getBungeeCord().judgedPlayerName;
                                }
                                final Iterator<String> iterator7 = TimerManager.this.plugin.getBungeeCord().gameScoreboard.getEntries().iterator();
                                while (iterator7.hasNext()) {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.resetScores((String)iterator7.next());
                                }
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(15);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-BUILD")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(14);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', TimerManager.this.plugin.getBungeeCord().currentBuildDisplayName), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(13);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(12);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-ROUND")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(11);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "" + TimerManager.this.plugin.getBungeeCord().currentRound), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(10);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&3"), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(9);
                                TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(8);
                                int score2 = 7;
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[0]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(7);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex8) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[1]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(6);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex9) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[2]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(5);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex10) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[3]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(4);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex11) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[4]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(3);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex12) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[5]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(2);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex13) {}
                                try {
                                    TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&f" + (String)TimerManager.this.plugin.getBungeeCord().plots.keySet().toArray()[6]), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(1);
                                    --score2;
                                }
                                catch (ArrayIndexOutOfBoundsException ex14) {}
                                for (final Player player4 : Bukkit.getOnlinePlayers()) {
                                    if (score2 >= 0 && !TimerManager.this.plugin.getBungeeCord().plots.containsKey(player4.getName())) {
                                        TimerManager.this.plugin.getBungeeCord().gameScoreboard.getObjective("SpeedBuilders").getScore(TimerManager.this.plugin.getBungeeCord().scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&8" + player4.getName()), TimerManager.this.plugin.getBungeeCord().gameScoreboard)).setScore(score2);
                                        --score2;
                                    }
                                }
                                final Iterator<Player> iterator9 = Bukkit.getOnlinePlayers().iterator();
                                while (iterator9.hasNext()) {
                                    iterator9.next().setScoreboard(TimerManager.this.plugin.getBungeeCord().gameScoreboard);
                                }
                                TimerManager.this.judgeTimerID5 = new BukkitRunnable() {
                                    public void run() {
                                        for (final Player player : Bukkit.getOnlinePlayers()) {
                                            if (TimerManager.this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
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
                                                if (TimerManager.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player) == null || !TimerManager.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player).getName().equals("Players")) {
                                                    continue;
                                                }
                                                final String str = TimerManager.this.plugin.getBungeeCord().plots.get(player.getName());
                                                final Location location = new Location(Bukkit.getWorld(TimerManager.this.plugin.getBungeeCord().currentMap), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.x"), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.y"), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.z"));
                                                location.setPitch((float)TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.pitch"));
                                                location.setYaw((float)TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.yaw"));
                                                player.teleport(location);
                                                player.setFallDistance(0.0f);
                                            }
                                        }
                                        TimerManager.this.showCaseTimer();
                                    }
                                }.runTaskLater((Plugin)TimerManager.this.plugin, 60L).getTaskId();
                            }
                            TimerManager.this.plugin.getBungeeCord().judgedPlayerName = null;
                        }
                    }.runTaskLater((Plugin)TimerManager.this.plugin, 100L).getTaskId();
                }
                else {
                    if (Math.floor(TimerManager.this.judgeTime) == TimerManager.this.judgeTime && TimerManager.this.judgeTime == 4.0f) {
                        for (final Player player : Bukkit.getOnlinePlayers()) {
                            if (TimerManager.this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
                                if (TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) <= 100 && TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) >= 75) {
                                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&b").replaceAll("%PERCENT%", "" + TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()))));
                                }
                                if (TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) <= 74 && TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) >= 50) {
                                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&a").replaceAll("%PERCENT%", "" + TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()))));
                                }
                                if (TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) <= 49 && TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) >= 25) {
                                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&e").replaceAll("%PERCENT%", "" + TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()))));
                                }
                                if (TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) > 24 || TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()) < 0) {
                                    continue;
                                }
                                TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_SCORED_PERCENT").replaceAll("%PERCENT_COLOR%", "&c").replaceAll("%PERCENT%", "" + TimerManager.this.plugin.getBungeeCord().playerPercent.get(player.getName()))));
                            }
                        }
                    }
                    TimerManager.this.plugin.getBungeeCord().getGuardianManager().rotateGuardian(7.5f);
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 1L).getTaskId();
        this.judgeTimerID6 = new BukkitRunnable() {
            public void run() {
                final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
                while (iterator.hasNext()) {
                    TimerManager.this.plugin.getBungeeCord().getNMSManager().showTitle(iterator.next(), 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-GWEN_IS_JUDGING")));
                }
            }
        }.runTaskLater((Plugin)this.plugin, 40L).getTaskId();
    }
    
    public void guardianIsImpressed() {
        Bukkit.getScheduler().cancelTask(this.gameStartTimerID);
        Bukkit.getScheduler().cancelTask(this.showCaseTimerID);
        Bukkit.getScheduler().cancelTask(this.buildTimerID);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID1);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID2);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID3);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID4);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID5);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID6);
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
                player.getInventory().setArmorContents((ItemStack[])null);
                player.getInventory().clear();
                player.setExp(0.0f);
                player.setFireTicks(0);
                player.setFoodLevel(20);
                player.setGameMode(GameMode.SPECTATOR);
                player.setHealth(20.0);
                player.setLevel(0);
                player.setAllowFlight(true);
                player.setFlying(true);
                final Iterator iterator2 = player.getActivePotionEffects().iterator();
                while (iterator2.hasNext()) {
                    player.removePotionEffect(iterator2.next().getType());
                }
            }
            this.plugin.getBungeeCord().getNMSManager().showTitle(player, 0, 100, 0, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-GWEN_IS_IMPRESSED")));
            this.plugin.getBungeeCord().getNMSManager().showParticleEffect1(player, player.getLocation(), 0.0f, 0.0f, 0.0f, 1.0f, 1);
            Sounds.ENTITY_ELDER_GUARDIAN_CURSE.play(player, 0.85f, 1.0f);
        }
        new BukkitRunnable() {
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (TimerManager.this.plugin.getBungeeCord().plots.containsKey(player.getName())) {
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
                        if (TimerManager.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player) == null || !TimerManager.this.plugin.getBungeeCord().gameScoreboard.getPlayerTeam((OfflinePlayer)player).getName().equals("Players")) {
                            continue;
                        }
                        final String str = TimerManager.this.plugin.getBungeeCord().plots.get(player.getName());
                        final Location location = new Location(Bukkit.getWorld(TimerManager.this.plugin.getBungeeCord().currentMap), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.x"), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.y"), TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.z"));
                        location.setPitch((float)TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.pitch"));
                        location.setYaw((float)TimerManager.this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + TimerManager.this.plugin.getBungeeCord().currentMap + ".plots." + str + ".spawnpoint.yaw"));
                        player.teleport(location);
                        player.setFallDistance(0.0f);
                    }
                }
                TimerManager.this.showCaseTimer();
            }
        }.runTaskLater((Plugin)this.plugin, 60L);
    }
    
    public void cooldownTimer(final String s) {
        new BukkitRunnable() {
            public void run() {
                if (Bukkit.getPlayer(s).isOnline()) {
                    final float float1 = Float.parseFloat(String.format(Locale.UK, "%.1f", TimerManager.this.plugin.getBungeeCord().playersDoubleJumpCooldowned.get(s) - 0.1f));
                    TimerManager.this.plugin.getBungeeCord().playersDoubleJumpCooldowned.put(s, float1);
                    if (float1 <= 0.0f) {
                        this.cancel();
                        TimerManager.this.plugin.getBungeeCord().playersDoubleJumpCooldowned.remove(s);
                    }
                }
                else {
                    this.cancel();
                    TimerManager.this.plugin.getBungeeCord().playersDoubleJumpCooldowned.remove(s);
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 2L);
    }
    
    public void stop() {
        Bukkit.getScheduler().cancelTask(this.startTimerID);
        Bukkit.getScheduler().cancelTask(this.gameStartTimerID);
        Bukkit.getScheduler().cancelTask(this.showCaseTimerID);
        Bukkit.getScheduler().cancelTask(this.buildTimerID);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID1);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID2);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID3);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID4);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID5);
        Bukkit.getScheduler().cancelTask(this.judgeTimerID6);
        if (this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.game-ending-mode").equalsIgnoreCase("reload-1")) {
            new BukkitRunnable() {
                public void run() {
                    TimerManager.this.plugin.getBungeeCord().reload();
                }
            }.runTaskLater((Plugin)this.plugin, 10L);
            return;
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.game-ending-mode").equalsIgnoreCase("reload-2")) {
            new BukkitRunnable() {
                public void run() {
                    final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
                    dataOutput.writeUTF("Connect");
                    dataOutput.writeUTF(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().sendPluginMessage((Plugin)TimerManager.this.plugin, "BungeeCord", dataOutput.toByteArray());
                    }
                    new BukkitRunnable() {
                        public void run() {
                            TimerManager.this.plugin.getBungeeCord().reload();
                        }
                    }.runTaskLater((Plugin)TimerManager.this.plugin, 40L);
                }
            }.runTaskLater((Plugin)this.plugin, 10L);
            return;
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.game-ending-mode").equalsIgnoreCase("restart")) {
            new BukkitRunnable() {
                public void run() {
                    final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
                    dataOutput.writeUTF("Connect");
                    dataOutput.writeUTF(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().sendPluginMessage((Plugin)TimerManager.this.plugin, "BungeeCord", dataOutput.toByteArray());
                    }
                    new BukkitRunnable() {
                        public void run() {
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "restart");
                        }
                    }.runTaskLater((Plugin)TimerManager.this.plugin, 40L);
                }
            }.runTaskLater((Plugin)this.plugin, 10L);
            return;
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.game-ending-mode").equalsIgnoreCase("stop")) {
            new BukkitRunnable() {
                public void run() {
                    final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
                    dataOutput.writeUTF("Connect");
                    dataOutput.writeUTF(TimerManager.this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.lobby-server-name"));
                    final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().sendPluginMessage((Plugin)TimerManager.this.plugin, "BungeeCord", dataOutput.toByteArray());
                    }
                    new BukkitRunnable() {
                        public void run() {
                            Bukkit.shutdown();
                        }
                    }.runTaskLater((Plugin)TimerManager.this.plugin, 40L);
                }
            }.runTaskLater((Plugin)this.plugin, 10L);
        }
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
    
    public int getStartTime() {
        return this.startTime;
    }
    
    public int getStartTimerID() {
        return this.startTimerID;
    }
    
    public float getGameStartTime() {
        return this.gameStartTime;
    }
    
    public int getGameStartTimerID() {
        return this.gameStartTimerID;
    }
    
    public int getShowCaseTime() {
        return this.showCaseTime;
    }
    
    public int getShowCaseTimerID() {
        return this.showCaseTimerID;
    }
    
    public float getBuildTime() {
        return this.buildTime;
    }
    
    public int getBuildTimerID() {
        return this.buildTimerID;
    }
    
    public float getJudgeTime() {
        return this.judgeTime;
    }
    
    public int getJudgeTimerID1() {
        return this.judgeTimerID1;
    }
    
    public int getJudgeTimerID2() {
        return this.judgeTimerID2;
    }
    
    public int getJudgeTimerID3() {
        return this.judgeTimerID3;
    }
    
    public int getJudgeTimerID4() {
        return this.judgeTimerID4;
    }
    
    public int getJudgeTimerID5() {
        return this.judgeTimerID5;
    }
    
    public int getJudgeTimerID6() {
        return this.judgeTimerID6;
    }
}
