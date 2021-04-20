 

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_11_R1;

import org.bukkit.World;
import org.bukkit.scoreboard.Objective;
import java.util.Iterator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import java.util.Random;
import java.util.ArrayList;
import java.io.FilenameFilter;
import java.io.File;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ws.billy.speedbuilderspractice.utils.Materials;
import ws.billy.speedbuilderspractice.utils.GameState;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class SignManager implements Listener, ws.billy.speedbuilderspractice.multiworld.managers.SignManager
{
    private SpeedBuilders plugin;
    
    public SignManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignBreak(final BlockBreakEvent blockBreakEvent) {
        final Player player = blockBreakEvent.getPlayer();
        if (blockBreakEvent.getBlock().getState() instanceof Sign) {
            final Sign sign = (Sign)blockBreakEvent.getBlock().getState();
            if (sign.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                if (player.hasPermission("sb.command.setup")) {
                    if (this.plugin.getConfigManager().getConfig("signs.yml").contains("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ()) && sign.getData() instanceof org.bukkit.material.Sign) {
                        final BlockState state = blockBreakEvent.getBlock().getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace()).getState();
                        final Material material = Material.getMaterial(this.plugin.getConfigManager().getConfig("signs.yml").getString("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-type"));
                        final byte rawData = (byte)this.plugin.getConfigManager().getConfig("signs.yml").getInt("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-data");
                        state.setType(material);
                        state.setRawData(rawData);
                        state.update(true, false);
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ(), (Object)null);
                        new BukkitRunnable() {
                            public void run() {
                                SignManager.this.plugin.getConfigManager().saveConfig("signs.yml");
                            }
                        }.runTaskAsynchronously((Plugin)this.plugin);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7SpeedBuilders sign is successfully removed!"));
                    }
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
                    blockBreakEvent.setCancelled(true);
                }
            }
        }
        else {
            final Block block = blockBreakEvent.getBlock();
            if (block.getRelative(BlockFace.UP).getState().getData() instanceof org.bukkit.material.Sign) {
                if (((Sign)block.getRelative(BlockFace.UP).getState()).getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                    blockBreakEvent.setCancelled(true);
                }
            }
            else if (block.getRelative(BlockFace.NORTH).getState().getData() instanceof org.bukkit.material.Sign) {
                final Sign sign2 = (Sign)block.getRelative(BlockFace.NORTH).getState();
                if (((org.bukkit.material.Sign)sign2.getData()).getAttachedFace() == block.getRelative(BlockFace.NORTH).getFace(block) && sign2.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                    blockBreakEvent.setCancelled(true);
                }
            }
            else if (block.getRelative(BlockFace.SOUTH).getState().getData() instanceof org.bukkit.material.Sign) {
                final Sign sign3 = (Sign)block.getRelative(BlockFace.SOUTH).getState();
                if (((org.bukkit.material.Sign)sign3.getData()).getAttachedFace() == block.getRelative(BlockFace.SOUTH).getFace(block) && sign3.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                    blockBreakEvent.setCancelled(true);
                }
            }
            else if (block.getRelative(BlockFace.EAST).getState().getData() instanceof org.bukkit.material.Sign) {
                final Sign sign4 = (Sign)block.getRelative(BlockFace.EAST).getState();
                if (((org.bukkit.material.Sign)sign4.getData()).getAttachedFace() == block.getRelative(BlockFace.EAST).getFace(block) && sign4.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                    blockBreakEvent.setCancelled(true);
                }
            }
            else if (block.getRelative(BlockFace.WEST).getState().getData() instanceof org.bukkit.material.Sign) {
                final Sign sign5 = (Sign)block.getRelative(BlockFace.WEST).getState();
                if (((org.bukkit.material.Sign)sign5.getData()).getAttachedFace() == block.getRelative(BlockFace.WEST).getFace(block) && sign5.getLine(0).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                    blockBreakEvent.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(final SignChangeEvent signChangeEvent) {
        final Player player = signChangeEvent.getPlayer();
        if (signChangeEvent.getLine(0).equalsIgnoreCase("[SB]") || signChangeEvent.getLine(0).equalsIgnoreCase("[SpeedBuilders]")) {
            if (player.hasPermission("sb.command.setup")) {
                final Sign sign = (Sign)signChangeEvent.getBlock().getState();
                signChangeEvent.setLine(0, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")));
                if (signChangeEvent.getLine(1).equalsIgnoreCase("[AutoJoin]")) {
                    if (sign.getData() instanceof org.bukkit.material.Sign) {
                        final BlockState state = sign.getBlock().getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace()).getState();
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".world", (Object)sign.getWorld().getName());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-type", (Object)state.getType().toString());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-data", (Object)state.getRawData());
                        new BukkitRunnable() {
                            public void run() {
                                SignManager.this.plugin.getConfigManager().saveConfig("signs.yml");
                            }
                        }.runTaskAsynchronously((Plugin)this.plugin);
                        signChangeEvent.setLine(1, ChatColor.translateAlternateColorCodes('&', "&4[AutoJoin]"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7SpeedBuilders sign is successfully created!"));
                    }
                }
                else if (signChangeEvent.getLine(1).equalsIgnoreCase("[Leave]")) {
                    if (sign.getData() instanceof org.bukkit.material.Sign) {
                        final BlockState state2 = sign.getBlock().getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace()).getState();
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".world", (Object)sign.getWorld().getName());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-type", (Object)state2.getType().toString());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-data", (Object)state2.getRawData());
                        new BukkitRunnable() {
                            public void run() {
                                SignManager.this.plugin.getConfigManager().saveConfig("signs.yml");
                            }
                        }.runTaskAsynchronously((Plugin)this.plugin);
                        signChangeEvent.setLine(1, ChatColor.translateAlternateColorCodes('&', "&5[Leave]"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7SpeedBuilders sign is successfully created!"));
                    }
                }
                else if (signChangeEvent.getLine(1).equalsIgnoreCase("[Stats]")) {
                    if (sign.getData() instanceof org.bukkit.material.Sign) {
                        final BlockState state3 = sign.getBlock().getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace()).getState();
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".world", (Object)sign.getWorld().getName());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-type", (Object)state3.getType().toString());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-data", (Object)state3.getRawData());
                        new BukkitRunnable() {
                            public void run() {
                                SignManager.this.plugin.getConfigManager().saveConfig("signs.yml");
                            }
                        }.runTaskAsynchronously((Plugin)this.plugin);
                        signChangeEvent.setLine(1, ChatColor.translateAlternateColorCodes('&', "&2[Stats]"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7SpeedBuilders sign is successfully created!"));
                    }
                }
                else if (Arena.arenaObjects.contains(this.plugin.getMultiWorld().getArenaManager().getArena(signChangeEvent.getLine(1)))) {
                    if (sign.getData() instanceof org.bukkit.material.Sign) {
                        final BlockState state4 = sign.getBlock().getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace()).getState();
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".arena", (Object)signChangeEvent.getLine(1));
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".world", (Object)sign.getWorld().getName());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-type", (Object)state4.getType().toString());
                        this.plugin.getConfigManager().getConfig("signs.yml").set("signs." + sign.getBlock().getX() + "," + sign.getBlock().getY() + "," + sign.getBlock().getZ() + ".previous-data", (Object)state4.getRawData());
                        new BukkitRunnable() {
                            public void run() {
                                SignManager.this.plugin.getConfigManager().saveConfig("signs.yml");
                            }
                        }.runTaskAsynchronously((Plugin)this.plugin);
                        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(signChangeEvent.getLine(1));
                        signChangeEvent.setLine(1, arena.getName());
                        if (arena.getGameState() == GameState.WAITING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-WAITING")));
                            state4.setType(Materials.LIME_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.LIME_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        else if (arena.getGameState() == GameState.STARTING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-STARTING")));
                            state4.setType(Materials.ORANGE_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.ORANGE_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        else if (arena.getGameState() == GameState.GAME_STARTING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-GAME_STARTING")));
                            state4.setType(Materials.RED_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        else if (arena.getGameState() == GameState.SHOWCASING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-SHOWCASING")));
                            state4.setType(Materials.RED_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        else if (arena.getGameState() == GameState.BUILDING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-BUILDING")));
                            state4.setType(Materials.RED_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        else if (arena.getGameState() == GameState.JUDGING) {
                            signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-JUDGING")));
                            state4.setType(Materials.RED_TERRACOTTA.getType("block"));
                            state4.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                            state4.update(true, false);
                        }
                        signChangeEvent.setLine(3, arena.getPlayers().size() + "/" + arena.getMaxPlayers());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7SpeedBuilders sign is successfully created!"));
                    }
                }
                else {
                    signChangeEvent.setLine(1, ChatColor.translateAlternateColorCodes('&', "&cArena does"));
                    signChangeEvent.setLine(2, ChatColor.translateAlternateColorCodes('&', "&cnot exist"));
                }
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_PERMISSION")));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getClickedBlock().getState() instanceof Sign) {
            final Sign sign = (Sign)playerInteractEvent.getClickedBlock().getState();
            if (sign.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")))) {
                if (sign.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&4[AutoJoin]"))) {
                    if (!this.plugin.getMultiWorld().playersSignCooldowned.contains(player.getName())) {
                        this.plugin.getMultiWorld().playersSignCooldowned.add(player.getName());
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
                                final String[] array = list;
                                for (int length = array.length, i = 0; i < length; ++i) {
                                    if (array[i].equals(arena.getName())) {
                                        b3 = true;
                                        break;
                                    }
                                }
                                if (b3 && (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING)) {
                                    if (arena.getPlayers().size() < arena.getMaxPlayers()) {
                                        b2 = true;
                                        this.plugin.getMultiWorld().saveTempInfo(player);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena.getName());
                                        this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                                        break;
                                    }
                                    if (player.hasPermission("sb.server.joinfullgame")) {
                                        this.plugin.getMultiWorld().saveTempInfo(player);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena.getName());
                                        final ArrayList<Player> list2 = new ArrayList<Player>();
                                        for (final String s : arena.getPlayers()) {
                                            if (Bukkit.getPlayer(s) != player && !Bukkit.getPlayer(s).hasPermission("sb.server.joinfullgame")) {
                                                list2.add(Bukkit.getPlayer(s));
                                            }
                                        }
                                        if (!list2.isEmpty()) {
                                            final Player player2 = list2.get(new Random().nextInt(list2.size()));
                                            arena.getPlayers().remove(player2.getName());
                                            if (arena.getGameState() == GameState.WAITING) {
                                                for (final String s2 : arena.getPlayers()) {
                                                    if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName()) != null) {
                                                        final Scoreboard scoreboard = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s2).getName());
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
                                                for (final String s3 : arena.getPlayers()) {
                                                    if (arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName()) != null) {
                                                        final Scoreboard scoreboard2 = arena.getPlayerStartScoreboard().get(Bukkit.getPlayer(s3).getName());
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
                                this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                            }
                            else {
                                this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYING")));
                            this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-WAIT_BEFORE_CLICKING_AGAIN")));
                    }
                }
                else if (sign.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&5[Leave]"))) {
                    if (!this.plugin.getMultiWorld().playersSignCooldowned.contains(player.getName())) {
                        this.plugin.getMultiWorld().playersSignCooldowned.add(player.getName());
                        boolean b4 = false;
                        for (final Arena arena2 : Arena.arenaObjects) {
                            if (arena2.getPlayers().contains(player.getName())) {
                                b4 = true;
                                this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena2.getName());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
                                this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                                break;
                            }
                        }
                        if (!b4) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NOT_PLAYING")));
                            this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-WAIT_BEFORE_CLICKING_AGAIN")));
                    }
                }
                else if (sign.getLine(1).equals(ChatColor.translateAlternateColorCodes('&', "&2[Stats]"))) {
                    if (!this.plugin.getMultiWorld().playersSignCooldowned.contains(player.getName())) {
                        this.plugin.getStatsManager().showStats(player, player);
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-WAIT_BEFORE_CLICKING_AGAIN")));
                    }
                }
                else if (Arena.arenaObjects.contains(this.plugin.getMultiWorld().getArenaManager().getArena(sign.getLine(1)))) {
                    if (!this.plugin.getMultiWorld().playersSignCooldowned.contains(player.getName())) {
                        this.plugin.getMultiWorld().playersSignCooldowned.add(player.getName());
                        boolean b5 = false;
                        final Iterator<Arena> iterator11 = Arena.arenaObjects.iterator();
                        while (iterator11.hasNext()) {
                            if (iterator11.next().getPlayers().contains(player.getName())) {
                                b5 = true;
                                break;
                            }
                        }
                        if (!b5) {
                            final Arena arena3 = this.plugin.getMultiWorld().getArenaManager().getArena(sign.getLine(1));
                            final String[] list3 = new File(".").list(new FilenameFilter() {
                                @Override
                                public boolean accept(final File parent, final String child) {
                                    return new File(parent, child).isDirectory();
                                }
                            });
                            boolean b6 = false;
                            final String[] array2 = list3;
                            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                                if (array2[j].equals(arena3.getName())) {
                                    b6 = true;
                                    break;
                                }
                            }
                            if (b6) {
                                if (arena3.getGameState() == GameState.WAITING || arena3.getGameState() == GameState.STARTING) {
                                    if (arena3.getPlayers().size() < arena3.getMaxPlayers()) {
                                        this.plugin.getMultiWorld().saveTempInfo(player);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena3.getName());
                                        this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                                    }
                                    else if (player.hasPermission("sb.server.joinfullgame")) {
                                        this.plugin.getMultiWorld().saveTempInfo(player);
                                        this.plugin.getMultiWorld().getArenaManager().addPlayer(player, arena3.getName());
                                        final ArrayList<Player> list4 = new ArrayList<Player>();
                                        for (final String s4 : arena3.getPlayers()) {
                                            if (Bukkit.getPlayer(s4) != player && !Bukkit.getPlayer(s4).hasPermission("sb.server.joinfullgame")) {
                                                list4.add(Bukkit.getPlayer(s4));
                                            }
                                        }
                                        if (!list4.isEmpty()) {
                                            final Player player3 = list4.get(new Random().nextInt(list4.size()));
                                            arena3.getPlayers().remove(player3.getName());
                                            if (arena3.getGameState() == GameState.WAITING) {
                                                for (final String s5 : arena3.getPlayers()) {
                                                    if (arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s5).getName()) != null) {
                                                        final Scoreboard scoreboard3 = arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s5).getName());
                                                        final Objective objective3 = scoreboard3.getObjective("SpeedBuilders");
                                                        final Iterator iterator14 = scoreboard3.getEntries().iterator();
                                                        while (iterator14.hasNext()) {
                                                            scoreboard3.resetScores((String)iterator14.next());
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
                                                        objective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s5), arena3.getName()).toUpperCase())), scoreboard3)).setScore(1);
                                                        Bukkit.getPlayer(s5).setScoreboard(scoreboard3);
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
                                                        registerNewObjective3.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s5), arena3.getName()).toUpperCase())), newScoreboard3)).setScore(1);
                                                        Bukkit.getPlayer(s5).setScoreboard(newScoreboard3);
                                                        arena3.getPlayerStartScoreboard().put(Bukkit.getPlayer(s5).getName(), newScoreboard3);
                                                    }
                                                }
                                                arena3.getGameScoreboard().getPlayerTeam((OfflinePlayer)player3).removePlayer((OfflinePlayer)player3);
                                                this.plugin.getMultiWorld().getKitManager().setKit(player3, null, arena3.getName());
                                                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                    final Location location3 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                    location3.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                    location3.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                    player3.teleport(location3);
                                                }
                                                else {
                                                    player3.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                                }
                                                player3.getInventory().setArmorContents((ItemStack[])null);
                                                player3.getInventory().clear();
                                                player3.setAllowFlight(false);
                                                player3.setExp(0.0f);
                                                player3.setFireTicks(0);
                                                player3.setFlying(false);
                                                player3.setFoodLevel(20);
                                                player3.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                                player3.setHealth(20.0);
                                                player3.setLevel(0);
                                                final Iterator iterator15 = player3.getActivePotionEffects().iterator();
                                                while (iterator15.hasNext()) {
                                                    player3.removePotionEffect(iterator15.next().getType());
                                                }
                                                if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player3.getName())) {
                                                    this.plugin.getMultiWorld().loadTempInfo(player3);
                                                }
                                                player3.updateInventory();
                                            }
                                            else if (arena3.getGameState() == GameState.STARTING) {
                                                for (final String s6 : arena3.getPlayers()) {
                                                    if (arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s6).getName()) != null) {
                                                        final Scoreboard scoreboard4 = arena3.getPlayerStartScoreboard().get(Bukkit.getPlayer(s6).getName());
                                                        final Objective objective4 = scoreboard4.getObjective("SpeedBuilders");
                                                        final Iterator iterator17 = scoreboard4.getEntries().iterator();
                                                        while (iterator17.hasNext()) {
                                                            scoreboard4.resetScores((String)iterator17.next());
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
                                                        objective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s6), arena3.getName()).toUpperCase())), scoreboard4)).setScore(1);
                                                        Bukkit.getPlayer(s6).setScoreboard(scoreboard4);
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
                                                        registerNewObjective4.getScore(this.plugin.getMultiWorld().scoreboardScore(ChatColor.translateAlternateColorCodes('&', Translations.translate("KITS-" + this.plugin.getMultiWorld().getKitManager().getKit(Bukkit.getPlayer(s6), arena3.getName()).toUpperCase())), newScoreboard4)).setScore(1);
                                                        Bukkit.getPlayer(s6).setScoreboard(newScoreboard4);
                                                        arena3.getPlayerStartScoreboard().put(Bukkit.getPlayer(s6).getName(), newScoreboard4);
                                                    }
                                                }
                                                arena3.getGameScoreboard().getPlayerTeam((OfflinePlayer)player3).removePlayer((OfflinePlayer)player3);
                                                this.plugin.getMultiWorld().getKitManager().setKit(player3, null, arena3.getName());
                                                if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                                                    final Location location4 = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                                                    location4.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                                                    location4.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                                                    player3.teleport(location4);
                                                }
                                                else {
                                                    player3.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                                                }
                                                player3.getInventory().setArmorContents((ItemStack[])null);
                                                player3.getInventory().clear();
                                                player3.setAllowFlight(false);
                                                player3.setExp(0.0f);
                                                player3.setFireTicks(0);
                                                player3.setFlying(false);
                                                player3.setFoodLevel(20);
                                                player3.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
                                                player3.setHealth(20.0);
                                                player3.setLevel(0);
                                                final Iterator iterator18 = player3.getActivePotionEffects().iterator();
                                                while (iterator18.hasNext()) {
                                                    player3.removePotionEffect(iterator18.next().getType());
                                                }
                                                if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player3.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player3.getName())) {
                                                    this.plugin.getMultiWorld().loadTempInfo(player3);
                                                }
                                                player3.updateInventory();
                                            }
                                        }
                                        list4.clear();
                                    }
                                    else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-ARENA_IS_FULL")));
                                        this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                                    }
                                }
                                else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-GAME_RUNNING")));
                                    this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                                }
                            }
                            else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&cWorld folder can not be found for that arena!"));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&cShut down the server and upload the world to this server!"));
                                this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-PLAYING")));
                            this.plugin.getMultiWorld().playersSignCooldowned.remove(player.getName());
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-WAIT_BEFORE_CLICKING_AGAIN")));
                    }
                }
            }
        }
    }
    
    public void updateSigns(final String anObject) {
        final Iterator<String> iterator = this.plugin.getConfigManager().getConfig("signs.yml").getConfigurationSection("signs").getKeys(false).iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split(",");
            if (this.plugin.getConfigManager().getConfig("signs.yml").contains("signs." + split[0] + "," + split[1] + "," + split[2]) && this.plugin.getConfigManager().getConfig("signs.yml").getString("signs." + split[0] + "," + split[1] + "," + split[2] + ".arena").equals(anObject)) {
                final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(this.plugin.getConfigManager().getConfig("signs.yml").getString("signs." + split[0] + "," + split[1] + "," + split[2] + ".arena"));
                final World world = Bukkit.getWorld(this.plugin.getConfigManager().getConfig("signs.yml").getString("signs." + split[0] + "," + split[1] + "," + split[2] + ".world"));
                final Location location = new Location(world, (double)Integer.parseInt(split[0]), (double)Integer.parseInt(split[1]), (double)Integer.parseInt(split[2]));
                location.getChunk().load();
                final Block block = world.getBlockAt(location);
                final BlockState state = world.getBlockAt(location).getState();
                if (!(state.getData() instanceof org.bukkit.material.Sign)) {
                    continue;
                }
                final Sign sign = (Sign)state;
                final org.bukkit.material.Sign sign2 = (org.bukkit.material.Sign)state.getData();
                sign.setLine(0, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-SIGN")));
                sign.setLine(1, arena.getName());
                if (arena.getGameState() == GameState.WAITING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-WAITING")));
                    final BlockState state2 = block.getRelative(sign2.getAttachedFace()).getState();
                    state2.setType(Materials.LIME_TERRACOTTA.getType("block"));
                    state2.setRawData(Materials.LIME_TERRACOTTA.getData("block"));
                    state2.update(true, false);
                }
                else if (arena.getGameState() == GameState.STARTING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-STARTING")));
                    final BlockState state3 = block.getRelative(sign2.getAttachedFace()).getState();
                    state3.setType(Materials.ORANGE_TERRACOTTA.getType("block"));
                    state3.setRawData(Materials.ORANGE_TERRACOTTA.getData("block"));
                    state3.update(true, false);
                }
                else if (arena.getGameState() == GameState.GAME_STARTING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-GAME_STARTING")));
                    final BlockState state4 = block.getRelative(sign2.getAttachedFace()).getState();
                    state4.setType(Materials.RED_TERRACOTTA.getType("block"));
                    state4.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                    state4.update(true, false);
                }
                else if (arena.getGameState() == GameState.SHOWCASING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-SHOWCASING")));
                    final BlockState state5 = block.getRelative(sign2.getAttachedFace()).getState();
                    state5.setType(Materials.RED_TERRACOTTA.getType("block"));
                    state5.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                    state5.update(true, false);
                }
                else if (arena.getGameState() == GameState.BUILDING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-BUILDING")));
                    final BlockState state6 = block.getRelative(sign2.getAttachedFace()).getState();
                    state6.setType(Materials.RED_TERRACOTTA.getType("block"));
                    state6.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                    state6.update(true, false);
                }
                else if (arena.getGameState() == GameState.JUDGING) {
                    sign.setLine(2, ChatColor.translateAlternateColorCodes('&', Translations.translate("GAMESTATE-JUDGING")));
                    final BlockState state7 = block.getRelative(sign2.getAttachedFace()).getState();
                    state7.setType(Materials.RED_TERRACOTTA.getType("block"));
                    state7.setRawData(Materials.RED_TERRACOTTA.getData("block"));
                    state7.update(true, false);
                }
                sign.setLine(3, arena.getPlayers().size() + "/" + arena.getMaxPlayers());
                sign.update(true, false);
            }
        }
    }
}
