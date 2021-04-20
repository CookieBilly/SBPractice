

package ws.billy.speedbuilderspractice.multiworld;

import org.bukkit.scoreboard.Team;
import java.util.AbstractMap;
import com.google.common.base.Splitter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Iterator;
import ws.billy.speedbuilderspractice.utils.Translations;
import ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_8_R3.WorldListener;
import ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_8_R3.PlayerListener;
import ws.billy.speedbuilderspractice.multiworld.listeners.SetupListener2;
import org.bukkit.event.Listener;
import ws.billy.speedbuilderspractice.multiworld.listeners.SetupListener1;
import org.bukkit.command.CommandExecutor;
import ws.billy.speedbuilderspractice.multiworld.commands.SBCommand;
import org.bukkit.plugin.Plugin;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;
import java.util.HashMap;
import java.util.ArrayList;
import ws.billy.speedbuilderspractice.multiworld.managers.TimerManager;
import ws.billy.speedbuilderspractice.multiworld.managers.TemplateManager;
import ws.billy.speedbuilderspractice.multiworld.managers.SignManager;
import ws.billy.speedbuilderspractice.multiworld.managers.NMSManager;
import ws.billy.speedbuilderspractice.multiworld.managers.KitManager;
import ws.billy.speedbuilderspractice.multiworld.managers.GuardianManager;
import ws.billy.speedbuilderspractice.multiworld.managers.ArenaManager;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class MultiWorld
{
    public SpeedBuilders plugin;
    public ArenaManager arenaManager;
    public GuardianManager guardianManager;
    public KitManager kitManager;
    public NMSManager nmsManager;
    public SignManager signManager;
    public TemplateManager templateManager;
    public TimerManager timerManager;
    public ArrayList<String> playersSignCooldowned;
    public HashMap<String, Double> playerTempHealth;
    public HashMap<String, Integer> playerTempFoodLevel;
    public HashMap<String, Float> playerTempExp;
    public HashMap<String, Integer> playerTempLevel;
    public HashMap<String, GameMode> playerTempGameMode;
    public HashMap<String, ItemStack[]> playerTempArmor;
    public HashMap<String, ItemStack[]> playerTempItems;
    public HashMap<String, Collection<PotionEffect>> playerTempEffects;
    public HashMap<String, String> setup;
    public Location location1;
    public Location location3;
    public Location location2;
    public HashMap<String, List<Block>> blocks;
    public List<String> winnerCommands;
    public List<String> loserCommands;
    
    public MultiWorld() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
        this.playersSignCooldowned = new ArrayList<String>();
        this.playerTempHealth = new HashMap<String, Double>();
        this.playerTempFoodLevel = new HashMap<String, Integer>();
        this.playerTempExp = new HashMap<String, Float>();
        this.playerTempLevel = new HashMap<String, Integer>();
        this.playerTempGameMode = new HashMap<String, GameMode>();
        this.playerTempArmor = new HashMap<String, ItemStack[]>();
        this.playerTempItems = new HashMap<String, ItemStack[]>();
        this.playerTempEffects = new HashMap<String, Collection<PotionEffect>>();
        this.setup = new HashMap<String, String>();
        this.location1 = null;
        this.location3 = null;
        this.location2 = null;
        this.blocks = new HashMap<String, List<Block>>();
        this.winnerCommands = new ArrayList<String>();
        this.loserCommands = new ArrayList<String>();
    }
    
    public void onEnable() {
        final File file = new File("plugins/" + this.plugin.getDescription().getName());
        if (!file.exists()) {
            file.mkdirs();
        }
        this.plugin.getConfigManager().loadConfig("arenas.yml");
        this.plugin.getConfigManager().loadConfig("lobby.yml");
        this.plugin.getConfigManager().loadConfig("messages.yml");
        this.plugin.getConfigManager().loadConfig("signs.yml");
        this.plugin.getConfigManager().loadConfig("templates.yml");
        this.plugin.getConfigManager().loadConfig("spigot.yml", new File("."));
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this.plugin, "BungeeCord");
        Bukkit.getPluginCommand("speedbuilders").setExecutor((CommandExecutor)new SBCommand());
        Bukkit.getPluginManager().registerEvents((Listener)new SetupListener1(), (Plugin)this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new SetupListener2(), (Plugin)this.plugin);
        this.arenaManager = new ArenaManager();
        this.kitManager = new KitManager();
        this.timerManager = new TimerManager();
        final String serverVersion = this.plugin.serverVersion;
        switch (serverVersion) {
            case "v1_8_R3": {
                Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_8_R3.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_8_R3.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_8_R3.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_8_R3.SignManager();
                this.templateManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_8_R3.TemplateManager();
                break;
            }
            case "v1_9_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R1.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1.SignManager();
                this.templateManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1.TemplateManager();
                break;
            }
            case "v1_9_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R2.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R2.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R2.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R2.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R2.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R2.SignManager();
                this.templateManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R2.TemplateManager();
                break;
            }
            case "v1_10_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_10_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1.SignManager();
                this.templateManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1.TemplateManager();
                break;
            }
            case "v1_11_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_11_R1.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_11_R1.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1.SignManager();
                this.templateManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1.TemplateManager();
                break;
            }
            case "v1_12_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_12_R1.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_12_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_12_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_12_R1.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_13_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R1.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_13_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R2.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R2.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R2.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R2.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_14_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_14_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_14_R1.GuardianManager();
                this.nmsManager = new NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_14_R1.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_15_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_15_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_15_R1.NMSManager();
                this.signManager = new SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R1": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R1.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R1.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R1.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R1.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R2": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R2.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R2.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R2.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R2.SignManager();
                this.templateManager = new TemplateManager();
                break;
            }
            case "v1_16_R3": {
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.PlayerListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.listeners.WorldListener(), (Plugin)this.plugin);
                Bukkit.getPluginManager().registerEvents((Listener)new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R3.SignManager(), (Plugin)this.plugin);
                this.guardianManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R3.GuardianManager();
                this.nmsManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R3.NMSManager();
                this.signManager = new ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R3.SignManager();
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
        for (final String s : this.plugin.getConfigManager().getConfig("messages.yml").getConfigurationSection("MULTIWORLD").getKeys(false)) {
            Translations.messages.put(s, this.plugin.getConfigManager().getConfig("messages.yml").getString("MULTIWORLD." + s));
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("winner-commands.enabled")) {
            this.winnerCommands = (List<String>)this.plugin.getConfigManager().getConfig("config.yml").getStringList("winner-commands.commands");
        }
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("loser-commands.enabled")) {
            this.loserCommands = (List<String>)this.plugin.getConfigManager().getConfig("config.yml").getStringList("loser-commands.commands");
        }
        this.arenaManager.loadArenas();
    }
    
    public void onDisable() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (this.playerTempHealth.containsKey(player.getName()) && this.playerTempFoodLevel.containsKey(player.getName()) && this.playerTempExp.containsKey(player.getName()) && this.playerTempLevel.containsKey(player.getName()) && this.playerTempGameMode.containsKey(player.getName()) && this.playerTempArmor.containsKey(player.getName()) && this.playerTempItems.containsKey(player.getName()) && this.playerTempEffects.containsKey(player.getName())) {
                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                    final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                    location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                    location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                    player.teleport(location);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
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
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                final Iterator iterator2 = player.getActivePotionEffects().iterator();
                while (iterator2.hasNext()) {
                    player.removePotionEffect(iterator2.next().getType());
                }
                this.loadTempInfo(player);
                player.updateInventory();
            }
        }
    }
    
    public ArenaManager getArenaManager() {
        return this.arenaManager;
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
    
    public SignManager getSignManager() {
        return this.signManager;
    }
    
    public TemplateManager getTemplateManager() {
        return this.templateManager;
    }
    
    public TimerManager getTimerManager() {
        return this.timerManager;
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
    
    public void saveTempInfo(final Player player) {
        this.playerTempHealth.put(player.getName(), player.getHealth());
        this.playerTempFoodLevel.put(player.getName(), player.getFoodLevel());
        this.playerTempExp.put(player.getName(), player.getExp());
        this.playerTempLevel.put(player.getName(), player.getLevel());
        this.playerTempGameMode.put(player.getName(), player.getGameMode());
        this.playerTempArmor.put(player.getName(), player.getInventory().getArmorContents());
        this.playerTempItems.put(player.getName(), player.getInventory().getContents());
        this.playerTempEffects.put(player.getName(), player.getActivePotionEffects());
    }
    
    public void loadTempInfo(final Player player) {
        player.setHealth((double)this.playerTempHealth.get(player.getName()));
        player.setFoodLevel((int)this.playerTempFoodLevel.get(player.getName()));
        player.setExp((float)this.playerTempExp.get(player.getName()));
        player.setLevel((int)this.playerTempLevel.get(player.getName()));
        player.setGameMode((GameMode)this.playerTempGameMode.get(player.getName()));
        player.getInventory().setArmorContents((ItemStack[])this.playerTempArmor.get(player.getName()));
        player.getInventory().setContents((ItemStack[])this.playerTempItems.get(player.getName()));
        player.addPotionEffects((Collection)this.playerTempEffects.get(player.getName()));
        this.playerTempHealth.remove(player.getName());
        this.playerTempFoodLevel.remove(player.getName());
        this.playerTempExp.remove(player.getName());
        this.playerTempLevel.remove(player.getName());
        this.playerTempGameMode.remove(player.getName());
        this.playerTempArmor.remove(player.getName());
        this.playerTempItems.remove(player.getName());
        this.playerTempEffects.remove(player.getName());
    }
}
