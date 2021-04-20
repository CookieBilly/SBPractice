

package ws.billy.speedbuilderspractice.bungeecord;

import java.util.AbstractMap;
import com.google.common.base.Splitter;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Objective;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.GameStateChangeEvent;
import ws.billy.speedbuilderspractice.utils.Translations;
import ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_8_R3.WorldListener;
import ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_8_R3.PlayerListener;
import ws.billy.speedbuilderspractice.bungeecord.listeners.SetupListener2;
import ws.billy.speedbuilderspractice.bungeecord.listeners.SetupListener1;
import org.bukkit.event.Listener;
import ws.billy.speedbuilderspractice.bungeecord.listeners.ServerListener;
import org.bukkit.command.CommandExecutor;
import ws.billy.speedbuilderspractice.bungeecord.commands.SBCommand;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import java.util.Random;
import java.io.FilenameFilter;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.utils.GameState;
import java.util.HashMap;
import java.util.ArrayList;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scoreboard.Scoreboard;
import ws.billy.speedbuilderspractice.bungeecord.managers.TimerManager;
import ws.billy.speedbuilderspractice.bungeecord.managers.TemplateManager;
import ws.billy.speedbuilderspractice.bungeecord.managers.NMSManager;
import ws.billy.speedbuilderspractice.bungeecord.managers.KitManager;
import ws.billy.speedbuilderspractice.bungeecord.managers.GuardianManager;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class BungeeCord
{
    public SpeedBuilders plugin;
    public String currentMap;
    public GuardianManager guardianManager;
    public KitManager kitManager;
    public NMSManager nmsManager;
    public TemplateManager templateManager;
    public TimerManager timerManager;
    public Scoreboard gameScoreboard;
    public ArmorStand judgedPlayerArmorStand;
    public ArrayList<String> unusedTemplates;
    public ArrayList<String> usedTemplates;
    public HashMap<Integer, String> currentBuildBlocks;
    public HashMap<String, Float> playersDoubleJumpCooldowned;
    public HashMap<String, Integer> playerPercent;
    public HashMap<String, Scoreboard> playerStartScoreboard;
    public HashMap<String, String> playersKit;
    public HashMap<String, String> plots;
    public int buildTimeSubtractor;
    public int currentRound;
    public int maxPlayers;
    public Object guardian;
    public String currentBuildDisplayName;
    public String currentBuildRawName;
    public String judgedPlayerName;
    public String firstPlace;
    public String secondPlace;
    public String thirdPlace;
    public GameState gameState;
    public HashMap<String, String> setup;
    public Location location1;
    public Location location2;
    public Location location3;
    public HashMap<String, List<Block>> blocks;
    public List<String> winnerCommands;
    public List<String> loserCommands;
    
    public BungeeCord() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
        this.judgedPlayerArmorStand = null;
        this.unusedTemplates = new ArrayList<String>();
        this.usedTemplates = new ArrayList<String>();
        this.currentBuildBlocks = new HashMap<Integer, String>();
        this.playersDoubleJumpCooldowned = new HashMap<String, Float>();
        this.playerPercent = new HashMap<String, Integer>();
        this.playerStartScoreboard = new HashMap<String, Scoreboard>();
        this.playersKit = new HashMap<String, String>();
        this.plots = new HashMap<String, String>();
        this.buildTimeSubtractor = 0;
        this.currentRound = 0;
        this.maxPlayers = 0;
        this.guardian = null;
        this.currentBuildDisplayName = null;
        this.currentBuildRawName = null;
        this.judgedPlayerName = null;
        this.firstPlace = null;
        this.secondPlace = null;
        this.thirdPlace = null;
        this.setup = new HashMap<String, String>();
        this.location1 = null;
        this.location2 = null;
        this.location3 = null;
        this.blocks = new HashMap<String, List<Block>>();
        this.winnerCommands = new ArrayList<String>();
        this.loserCommands = new ArrayList<String>();
    }
    
    public void onEnable() {
        final File file = new File("plugins/" + this.plugin.getDescription().getName());
        if (!file.exists()) {
            file.mkdirs();
        }
        this.plugin.getConfigManager().loadConfig("lobby.yml");
        this.plugin.getConfigManager().loadConfig("maps.yml");
        this.plugin.getConfigManager().loadConfig("messages.yml");
        this.plugin.getConfigManager().loadConfig("templates.yml");
        this.plugin.getConfigManager().loadConfig("spigot.yml", new File("."));
        final File file2 = new File("plugins/" + this.plugin.getDescription().getName() + "/maps");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        final String[] list = file2.list(new FilenameFilter() {
            @Override
            public boolean accept(final File parent, final String child) {
                return new File(parent, child).isDirectory();
            }
        });
        final ArrayList<String> list2 = new ArrayList<String>();
        try {
            final String[] array = list;
            for (int length = array.length, i = 0; i < length; ++i) {
                list2.add(array[i]);
            }
            if (list2.size() > 1 && list2.contains(this.plugin.getConfigManager().getConfig("lobby.yml").getString("previous-map"))) {
                list2.remove(this.plugin.getConfigManager().getConfig("lobby.yml").getString("previous-map"));
            }
            this.currentMap = list2.get(new Random().nextInt(list2.size()));
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            Bukkit.getLogger().info("");
            Bukkit.getLogger().severe("[SpeedBuilders] Maps for SpeedBuilders plugin can not be found!");
            Bukkit.getLogger().severe("[SpeedBuilders] Follow this instruction to fix this problem:");
            Bukkit.getLogger().severe("[SpeedBuilders] Copy and paste your maps to the plugin's maps folder (../plugins/SpeedBuilders/maps),");
            Bukkit.getLogger().severe("[SpeedBuilders] and to the server's root folder.");
            Bukkit.getLogger().info("");
            Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin);
            return;
        }
        Bukkit.createWorld(new WorldCreator(this.currentMap).generator((ChunkGenerator)new VoidGenerator()));
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this.plugin, "BungeeCord");
        Bukkit.getPluginCommand("speedbuilders").setExecutor((CommandExecutor)new SBCommand());
        Bukkit.getPluginManager().registerEvents((Listener)new ServerListener(), (Plugin)this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new SetupListener1(), (Plugin)this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new SetupListener2(), (Plugin)this.plugin);
        this.kitManager = new KitManager();
        this.timerManager = new TimerManager();
        final String serverVersion = this.plugin.serverVersion;
        switch (serverVersion) {
            case "v1_8_R3": {
                Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_8_R3.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_8_R3.NMSManager();
                this.templateManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_8_R3.TemplateManager();
                break;
            }
            case "v1_9_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_9_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_9_R1.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R1.NMSManager();
                this.templateManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R1.TemplateManager();
                break;
            }
            case "v1_9_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_9_R2.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_9_R2.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R2.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R2.NMSManager();
                this.templateManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_9_R2.TemplateManager();
                break;
            }
            case "v1_10_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_10_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_10_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_10_R1.NMSManager();
                this.templateManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_10_R1.TemplateManager();
                break;
            }
            case "v1_11_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_11_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_11_R1.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_11_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_11_R1.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_12_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.versions.v1_12_R1.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_12_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_12_R1.NMSManager();
                this.templateManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_12_R1.TemplateManager();
                break;
            }
            case "v1_13_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_13_R1.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_13_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_13_R2.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_13_R2.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_14_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_14_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_14_R1.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_15_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_15_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_15_R1.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R1.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R2.GuardianManager();
                this.nmsManager = new NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R3": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.bungeecord.listeners.WorldListener(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R3.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R3.NMSManager();
                this.templateManager = new TemplateManager();
                break;
            }
            default: {
                Bukkit.getLogger().info("[SpeedBuilders] Unsupported version: " + this.plugin.serverVersion);
                Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin);
                break;
            }
        }
        this.plugin.getStatsManager().openConnection();
        for (final String s : this.plugin.getConfigManager().getConfig("messages.yml").getConfigurationSection("BUNGEECORD").getKeys(false)) {
            Translations.messages.put(s, this.plugin.getConfigManager().getConfig("messages.yml").getString("BUNGEECORD." + s));
        }
        this.unusedTemplates.clear();
        this.usedTemplates.clear();
        this.playersDoubleJumpCooldowned.clear();
        this.playersKit.clear();
        this.playerPercent.clear();
        this.plots.clear();
        this.playerStartScoreboard.clear();
        this.buildTimeSubtractor = 0;
        this.currentRound = 0;
        this.maxPlayers = 0;
        this.currentBuildDisplayName = Translations.translate("MAIN-NONE");
        this.currentBuildRawName = Translations.translate("MAIN-NONE");
        this.firstPlace = Translations.translate("MAIN-NONE");
        this.secondPlace = Translations.translate("MAIN-NONE");
        this.thirdPlace = Translations.translate("MAIN-NONE");
        this.gameState = GameState.WAITING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, Bukkit.getOnlinePlayers().size()));
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("winner-commands.enabled")) {
            this.winnerCommands = (List<String>)this.plugin.getConfigManager().getConfig("config.yml").getStringList("winner-commands.commands");
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("loser-commands.enabled")) {
            this.loserCommands = (List<String>)this.plugin.getConfigManager().getConfig("config.yml").getStringList("loser-commands.commands");
        }
        try {
            for (final String str : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + this.currentMap + ".plots").getKeys(false)) {
                if (this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".spawnpoint") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".area") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".laser-beam") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".build-area") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".blocks")) {
                    ++this.maxPlayers;
                }
            }
        }
        catch (NullPointerException ex2) {}
        if (this.maxPlayers == 0) {
            this.maxPlayers = 1;
        }
        this.gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective registerNewObjective = this.gameScoreboard.registerNewObjective("SpeedBuilders", "dummy");
        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-SPEEDBUILDERS")));
        registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        final Team registerNewTeam = this.gameScoreboard.registerNewTeam("Players");
        final Team registerNewTeam2 = this.gameScoreboard.registerNewTeam("Guardians");
        registerNewTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
        registerNewTeam2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
        new BukkitRunnable() {
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        if (BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                            final Location location = new Location(Bukkit.getWorld(BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                            location.setPitch((float)BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                            location.setYaw((float)BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                            player.teleport(location);
                        }
                        else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                        }
                        BungeeCord.this.gameScoreboard.getTeam("Players").addPlayer((OfflinePlayer)player);
                        BungeeCord.this.kitManager.setKit(player, "None");
                        player.getInventory().setArmorContents((ItemStack[])null);
                        player.getInventory().clear();
                        player.setAllowFlight(false);
                        player.setExp(0.0f);
                        player.setFireTicks(0);
                        player.setFlying(false);
                        player.setFoodLevel(20);
                        player.setGameMode(GameMode.valueOf(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                        player.setHealth(20.0);
                        player.setLevel(0);
                        final Iterator iterator2 = player.getActivePotionEffects().iterator();
                        while (iterator2.hasNext()) {
                            player.removePotionEffect(iterator2.next().getType());
                        }
                        final ItemStack itemStack = Materials.CLOCK.getItemStack(1);
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                        itemStack.setItemMeta(itemMeta);
                        if (BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                            final ItemStack itemStack2 = Materials.BOOK.getItemStack(1);
                            final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                            itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                            itemStack2.setItemMeta(itemMeta2);
                            player.getInventory().setItem(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack2);
                        }
                        player.getInventory().setItem(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack);
                        player.updateInventory();
                        if (BungeeCord.this.playerStartScoreboard.get(player.getName()) != null) {
                            final Scoreboard scoreboard = BungeeCord.this.playerStartScoreboard.get(player.getName());
                            final Objective objective = scoreboard.getObjective("SpeedBuilders");
                            final Iterator iterator3 = scoreboard.getEntries().iterator();
                            while (iterator3.hasNext()) {
                                scoreboard.resetScores((String)iterator3.next());
                            }
                            if (BungeeCord.this.gameState == GameState.WAITING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (BungeeCord.this.gameState == GameState.STARTING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", BungeeCord.this.timerManager.timeString(BungeeCord.this.timerManager.getStartTime()))));
                            }
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + BungeeCord.this.maxPlayers), scoreboard)).setScore(4);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + BungeeCord.this.kitManager.getKit(player).toUpperCase())), scoreboard)).setScore(1);
                            player.setScoreboard(scoreboard);
                        }
                        else {
                            final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                            final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                            if (BungeeCord.this.gameState == GameState.WAITING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (BungeeCord.this.gameState == GameState.STARTING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", BungeeCord.this.timerManager.timeString(BungeeCord.this.timerManager.getStartTime()))));
                            }
                            registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + BungeeCord.this.maxPlayers), newScoreboard)).setScore(4);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + BungeeCord.this.kitManager.getKit(player).toUpperCase())), newScoreboard)).setScore(1);
                            player.setScoreboard(newScoreboard);
                            BungeeCord.this.playerStartScoreboard.put(player.getName(), newScoreboard);
                        }
                    }
                    if (Bukkit.getOnlinePlayers().size() >= BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.needed-players")) {
                        BungeeCord.this.timerManager.startTimer();
                    }
                }
                BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").set("previous-map", (Object)BungeeCord.this.currentMap);
                new BukkitRunnable() {
                    public void run() {
                        BungeeCord.this.plugin.getConfigManager().saveConfig("lobby.yml");
                    }
                }.runTaskAsynchronously((Plugin)BungeeCord.this.plugin);
            }
        }.runTaskLater((Plugin)this.plugin, 2L);
    }
    
    public void onDisable() {
    }
    
    public GuardianManager getGuardianManager() {
        return this.guardianManager;
    }
    
    public KitManager getKitManager() {
        return this.kitManager;
    }
    
    public NMSManager getNMSManager() {
        return this.nmsManager;
    }
    
    public TemplateManager getTemplateManager() {
        return this.templateManager;
    }
    
    public TimerManager getTimerManager() {
        return this.timerManager;
    }
    
    public void reload() {
        final File file = new File("plugins/" + this.plugin.getDescription().getName() + "/maps");
        if (!file.exists()) {
            file.mkdirs();
        }
        final String[] list = file.list(new FilenameFilter() {
            @Override
            public boolean accept(final File parent, final String child) {
                return new File(parent, child).isDirectory();
            }
        });
        final ArrayList<String> list2 = new ArrayList<String>();
        try {
            final String[] array = list;
            for (int length = array.length, i = 0; i < length; ++i) {
                list2.add(array[i]);
            }
            if (list2.size() > 1 && list2.contains(this.plugin.getConfigManager().getConfig("lobby.yml").getString("previous-map"))) {
                list2.remove(this.plugin.getConfigManager().getConfig("lobby.yml").getString("previous-map"));
            }
            this.currentMap = list2.get(new Random().nextInt(list2.size()));
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            Bukkit.getLogger().info("");
            Bukkit.getLogger().severe("[SpeedBuilders] Maps for SpeedBuilders plugin can not be found!");
            Bukkit.getLogger().severe("[SpeedBuilders] Follow this instruction to fix this problem:");
            Bukkit.getLogger().severe("[SpeedBuilders] Copy and paste your maps to the plugin's maps folder (../plugins/SpeedBuilders/maps),");
            Bukkit.getLogger().severe("[SpeedBuilders] and to the server's root folder.");
            Bukkit.getLogger().info("");
            Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin);
            return;
        }
        this.unusedTemplates.clear();
        this.usedTemplates.clear();
        this.playersDoubleJumpCooldowned.clear();
        this.playersKit.clear();
        this.playerPercent.clear();
        this.plots.clear();
        this.playerStartScoreboard.clear();
        this.buildTimeSubtractor = 0;
        this.currentRound = 0;
        this.maxPlayers = 0;
        this.currentBuildDisplayName = Translations.translate("MAIN-NONE");
        this.currentBuildRawName = Translations.translate("MAIN-NONE");
        this.firstPlace = Translations.translate("MAIN-NONE");
        this.secondPlace = Translations.translate("MAIN-NONE");
        this.thirdPlace = Translations.translate("MAIN-NONE");
        this.gameState = GameState.WAITING;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(GameState.WAITING, Bukkit.getOnlinePlayers().size()));
        try {
            for (final String str : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + this.currentMap + ".plots").getKeys(false)) {
                if (this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".spawnpoint") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".area") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".laser-beam") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".build-area") && this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + this.currentMap + ".plots." + str + ".blocks")) {
                    ++this.maxPlayers;
                }
            }
        }
        catch (NullPointerException ex2) {}
        if (this.maxPlayers == 0) {
            this.maxPlayers = 1;
        }
        this.gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective registerNewObjective = this.gameScoreboard.registerNewObjective("SpeedBuilders", "dummy");
        registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-SPEEDBUILDERS")));
        registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        final Team registerNewTeam = this.gameScoreboard.registerNewTeam("Players");
        final Team registerNewTeam2 = this.gameScoreboard.registerNewTeam("Guardians");
        registerNewTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
        registerNewTeam2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e"));
        new BukkitRunnable() {
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        BungeeCord.this.plugin.getBungeeCord().getNMSManager().setPlayerVisibility(player, null, true);
                        if (BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                            final Location location = new Location(Bukkit.getWorld(BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                            location.setPitch((float)BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                            location.setYaw((float)BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                            player.teleport(location);
                        }
                        else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                        }
                        BungeeCord.this.gameScoreboard.getTeam("Players").addPlayer((OfflinePlayer)player);
                        BungeeCord.this.kitManager.setKit(player, "None");
                        player.getInventory().setArmorContents((ItemStack[])null);
                        player.getInventory().clear();
                        player.setAllowFlight(false);
                        player.setExp(0.0f);
                        player.setFireTicks(0);
                        player.setFlying(false);
                        player.setFoodLevel(20);
                        player.setGameMode(GameMode.valueOf(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                        player.setHealth(20.0);
                        player.setLevel(0);
                        final Iterator iterator2 = player.getActivePotionEffects().iterator();
                        while (iterator2.hasNext()) {
                            player.removePotionEffect(iterator2.next().getType());
                        }
                        final ItemStack itemStack = Materials.CLOCK.getItemStack(1);
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")));
                        itemStack.setItemMeta(itemMeta);
                        if (BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
                            final ItemStack itemStack2 = Materials.BOOK.getItemStack(1);
                            final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                            itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-STATS_ITEM")));
                            itemStack2.setItemMeta(itemMeta2);
                            player.getInventory().setItem(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("stats-item-slot") - 1, itemStack2);
                        }
                        player.getInventory().setItem(BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("lobby-item-slot") - 1, itemStack);
                        player.updateInventory();
                        if (BungeeCord.this.playerStartScoreboard.get(player.getName()) != null) {
                            final Scoreboard scoreboard = BungeeCord.this.playerStartScoreboard.get(player.getName());
                            final Objective objective = scoreboard.getObjective("SpeedBuilders");
                            final Iterator iterator3 = scoreboard.getEntries().iterator();
                            while (iterator3.hasNext()) {
                                scoreboard.resetScores((String)iterator3.next());
                            }
                            if (BungeeCord.this.gameState == GameState.WAITING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (BungeeCord.this.gameState == GameState.STARTING) {
                                objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", BungeeCord.this.timerManager.timeString(BungeeCord.this.timerManager.getStartTime()))));
                            }
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), scoreboard)).setScore(6);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), scoreboard)).setScore(5);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + BungeeCord.this.maxPlayers), scoreboard)).setScore(4);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), scoreboard)).setScore(3);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), scoreboard)).setScore(2);
                            objective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + BungeeCord.this.kitManager.getKit(player).toUpperCase())), scoreboard)).setScore(1);
                            player.setScoreboard(scoreboard);
                        }
                        else {
                            final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                            final Objective registerNewObjective = newScoreboard.registerNewObjective("SpeedBuilders", "dummy");
                            if (BungeeCord.this.gameState == GameState.WAITING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-WAITING_FOR_PLAYERS")));
                            }
                            if (BungeeCord.this.gameState == GameState.STARTING) {
                                registerNewObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-STARTING_IN").replaceAll("%TIME%", BungeeCord.this.timerManager.timeString(BungeeCord.this.timerManager.getStartTime()))));
                            }
                            registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&1"), newScoreboard)).setScore(6);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-PLAYERS")), newScoreboard)).setScore(5);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Bukkit.getOnlinePlayers().size() + "/" + BungeeCord.this.maxPlayers), newScoreboard)).setScore(4);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', "&2"), newScoreboard)).setScore(3);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("SBOARD-KIT")), newScoreboard)).setScore(2);
                            registerNewObjective.getScore(BungeeCord.this.scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + BungeeCord.this.kitManager.getKit(player).toUpperCase())), newScoreboard)).setScore(1);
                            player.setScoreboard(newScoreboard);
                            BungeeCord.this.playerStartScoreboard.put(player.getName(), newScoreboard);
                        }
                    }
                    if (Bukkit.getOnlinePlayers().size() >= BungeeCord.this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.needed-players")) {
                        BungeeCord.this.timerManager.startTimer();
                    }
                }
                BungeeCord.this.plugin.getConfigManager().getConfig("lobby.yml").set("previous-map", (Object)BungeeCord.this.currentMap);
                new BukkitRunnable() {
                    public void run() {
                        BungeeCord.this.plugin.getConfigManager().saveConfig("lobby.yml");
                    }
                }.runTaskAsynchronously((Plugin)BungeeCord.this.plugin);
            }
        }.runTaskLater((Plugin)this.plugin, 2L);
    }
    
    public String scoreboardScore(final String s, final Scoreboard scoreboard) {
        if (s.length() <= 16) {
            return s;
        }
        final Team registerNewTeam = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        final Iterator<String> iterator = Splitter.fixedLength(16).split((CharSequence)s).iterator();
        registerNewTeam.setPrefix((String)iterator.next());
        final String value = iterator.next();
        if (s.length() > 32) {
            registerNewTeam.setSuffix((String)iterator.next());
        }
        final AbstractMap.SimpleEntry simpleEntry = new AbstractMap.SimpleEntry<Team, String>(registerNewTeam, value);
        final String s2 = simpleEntry.getValue();
        if (simpleEntry.getKey() != null) {
            simpleEntry.getKey().addEntry(s2);
        }
        return s2;
    }
}
