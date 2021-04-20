 

package ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R1;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.InventoryView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ws.billy.speedbuilderspractice.utils.Sounds;
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
import org.bukkit.Effect;
import org.bukkit.block.Skull;
import org.bukkit.block.Bed;
import org.bukkit.block.Banner;
import org.bukkit.event.block.Action;
import ws.billy.speedbuilderspractice.utils.Materials;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private SpeedBuilders plugin;
    
    public PlayerListener() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        if (Arena.arenaObjects.contains(this.plugin.getMultiWorld().getArenaManager().getArena(player.getWorld().getName()))) {
            if (this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.world") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.x") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.y") && this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn.z")) {
                final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
                location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
                location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
                player.teleport(location);
            }
            else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
            }
            if (this.plugin.getMultiWorld().playerTempHealth.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempFoodLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempExp.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempLevel.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempGameMode.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempArmor.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempItems.containsKey(player.getName()) && this.plugin.getMultiWorld().playerTempEffects.containsKey(player.getName())) {
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
                final Iterator iterator = player.getActivePotionEffects().iterator();
                while (iterator.hasNext()) {
                    player.removePotionEffect(iterator.next().getType());
                }
                this.plugin.getMultiWorld().loadTempInfo(player);
                player.updateInventory();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null) {
            this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final Action action = playerInteractEvent.getAction();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null) {
            if (arena3.getGameState() == GameState.WAITING) {
                if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                    }
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                        this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
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
            else if (arena3.getGameState() == GameState.STARTING) {
                if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                    }
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                        this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
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
            else if (arena3.getGameState() == GameState.SHOWCASING) {
                if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                    }
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                        this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
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
            else if (arena3.getGameState() == GameState.BUILDING) {
                if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                    }
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                        this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
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
                if (arena3.getPlots().containsKey(player.getName()) && arena3.getPlayerPercent().containsKey(player.getName()) && arena3.getPlayerPercent().get(player.getName()) < 100 && action == Action.LEFT_CLICK_BLOCK) {
                    playerInteractEvent.setCancelled(true);
                    final Block clickedBlock = playerInteractEvent.getClickedBlock();
                    if (this.isBlockInside(clickedBlock.getLocation(), new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.z1")), new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.x2"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.y2"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.z2")))) {
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
                        this.plugin.getMultiWorld().getNMSManager().updateBlockConnections(clickedBlock);
                        this.plugin.getMultiWorld().getTemplateManager().check(arena3.getPlots().get(player.getName()), player, arena3.getName());
                    }
                }
            }
            else if (arena3.getGameState() == GameState.JUDGING) {
                if (player.getItemInHand().getType() == Materials.CLOCK.getType("item") && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-LOBBY_ITEM")))) {
                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                    }
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        playerInteractEvent.setCancelled(true);
                        this.plugin.getMultiWorld().getArenaManager().removePlayer(player, arena3.getName());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PLAYER_QUIT").replaceAll("%PLAYER%", player.getName())));
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
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent playerMoveEvent) {
        final Player player = playerMoveEvent.getPlayer();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null) {
            if (arena3.getGameState() == GameState.GAME_STARTING) {
                if (arena3.getPlots().containsKey(player.getName()) && (playerMoveEvent.getTo().getX() != playerMoveEvent.getFrom().getX() || playerMoveEvent.getTo().getZ() != playerMoveEvent.getFrom().getZ())) {
                    final Location from = playerMoveEvent.getFrom();
                    from.setPitch(playerMoveEvent.getTo().getPitch());
                    from.setYaw(playerMoveEvent.getTo().getYaw());
                    playerMoveEvent.setTo(from);
                }
            }
            else if (arena3.getGameState() == GameState.SHOWCASING) {
                if (arena3.getPlots().containsKey(player.getName()) && !this.isPlayerInsideAsPlayer(playerMoveEvent.getTo(), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.x1"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.y1"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.z1")), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.x2"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.y2"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.z2")))) {
                    final String str = arena3.getPlots().get(player.getName());
                    final Location to = new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str + ".spawnpoint.z"));
                    to.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str + ".spawnpoint.pitch"));
                    to.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str + ".spawnpoint.yaw"));
                    playerMoveEvent.setTo(to);
                    player.setFallDistance(0.0f);
                    this.plugin.getMultiWorld().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_CANNOT_LEAVE")));
                }
            }
            else if (arena3.getGameState() == GameState.BUILDING && arena3.getPlots().containsKey(player.getName())) {
                if (!this.isPlayerInsideAsPlayer(playerMoveEvent.getTo(), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.x1"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.y1"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.z1")), new Location(player.getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.x2"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.y2"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".area.z2")))) {
                    final String str2 = arena3.getPlots().get(player.getName());
                    final Location to2 = new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str2 + ".spawnpoint.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str2 + ".spawnpoint.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str2 + ".spawnpoint.z"));
                    to2.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str2 + ".spawnpoint.pitch"));
                    to2.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + str2 + ".spawnpoint.yaw"));
                    playerMoveEvent.setTo(to2);
                    player.setFallDistance(0.0f);
                    this.plugin.getMultiWorld().getNMSManager().showTitle(player, 0, 30, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-YOU_CANNOT_LEAVE")));
                }
                if (!arena3.getPlayersDoubleJumpCooldowned().containsKey(player.getName())) {
                    player.setAllowFlight(true);
                }
                else {
                    player.setAllowFlight(false);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        final Player player = playerTeleportEvent.getPlayer();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        if (arena != null && playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            playerTeleportEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            final Player player = (Player)entityDamageEvent.getEntity();
            Arena arena = null;
            for (final Arena arena2 : Arena.arenaObjects) {
                if (arena2.getPlayers().contains(player.getName())) {
                    arena = arena2;
                }
            }
            if (arena != null) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(final FoodLevelChangeEvent foodLevelChangeEvent) {
        if (foodLevelChangeEvent.getEntity() instanceof Player) {
            final Player player = (Player)foodLevelChangeEvent.getEntity();
            Arena arena = null;
            for (final Arena arena2 : Arena.arenaObjects) {
                if (arena2.getPlayers().contains(player.getName())) {
                    arena = arena2;
                }
            }
            if (arena != null) {
                foodLevelChangeEvent.setFoodLevel(20);
                foodLevelChangeEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerToggleFlight(final PlayerToggleFlightEvent playerToggleFlightEvent) {
        final Player player = playerToggleFlightEvent.getPlayer();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null && arena3.getGameState() == GameState.BUILDING && !arena3.getPlayersDoubleJumpCooldowned().containsKey(player.getName()) && arena3.getPlots().containsKey(player.getName())) {
            playerToggleFlightEvent.setCancelled(true);
            try {
                NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_SURVIVALFLY);
            }
            catch (NoClassDefFoundError noClassDefFoundError) {}
            player.setVelocity(new Vector(0, 1, 0).multiply(1.05));
            Sounds.ENTITY_BLAZE_SHOOT.play(player, 1.0f, 1.0f);
            arena3.getPlayersDoubleJumpCooldowned().put(player.getName(), 1.5f);
            this.plugin.getMultiWorld().getTimerManager().cooldownTimer(arena3.getName(), player.getName());
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
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null) {
            if (currentItem != null) {
                if (view.getTitle().startsWith(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY")))) {
                    inventoryClickEvent.setCancelled(true);
                    player.updateInventory();
                }
                else if (player.hasPermission("sb.command.setup")) {
                    if (arena3.getGameState() != GameState.WAITING && arena3.getGameState() != GameState.BUILDING) {
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
                else if (arena3.getGameState() != GameState.BUILDING) {
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
        else if (view.getTitle().startsWith(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY"))) && currentItem != null) {
            inventoryClickEvent.setCancelled(true);
            player.updateInventory();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent playerDropItemEvent) {
        final Player player = playerDropItemEvent.getPlayer();
        final ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null && itemStack != null) {
            if (player.hasPermission("sb.command.setup")) {
                if (arena3.getGameState() != GameState.WAITING) {
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
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        if (arena != null) {
            blockBreakEvent.getBlock().getRelative(BlockFace.UP).getState().update();
            blockBreakEvent.getBlock().getState().update();
            blockBreakEvent.getBlock().getRelative(BlockFace.DOWN).getState().update();
            blockBreakEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent blockPlaceEvent) {
        final Player player = blockPlaceEvent.getPlayer();
        Arena arena = null;
        for (final Arena arena2 : Arena.arenaObjects) {
            if (arena2.getPlayers().contains(player.getName())) {
                arena = arena2;
            }
        }
        final Arena arena3 = arena;
        if (arena3 != null) {
            if (arena3.getGameState() == GameState.BUILDING) {
                if (this.isBlockInside(blockPlaceEvent.getBlock().getLocation(), new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.z1")), new Location(Bukkit.getWorld(arena3.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.x2"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.y2"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena3.getName() + ".plots." + arena3.getPlots().get(player.getName()) + ".build-area.z2")))) {
                    new BukkitRunnable() {
                        public void run() {
                            PlayerListener.this.plugin.getMultiWorld().getTemplateManager().check(arena3.getPlots().get(player.getName()), player, arena3.getName());
                        }
                    }.runTaskLater((Plugin)this.plugin, 1L);
                }
                else {
                    blockPlaceEvent.setCancelled(true);
                }
            }
            else {
                blockPlaceEvent.getBlock().getRelative(BlockFace.UP).getState().update();
                blockPlaceEvent.getBlock().getState().update();
                blockPlaceEvent.getBlock().getRelative(BlockFace.DOWN).getState().update();
                blockPlaceEvent.setCancelled(true);
            }
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
