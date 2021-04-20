 

package ws.billy.speedbuilderspractice.multiworld.managers;

import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;
import org.bukkit.block.data.BlockData;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;
import org.bukkit.inventory.ItemStack;
import ws.billy.speedbuilderspractice.utils.Materials;
import java.util.Map;
import java.util.Iterator;
import org.bukkit.block.BlockState;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.PlayerPerfectEvent;
import ws.billy.speedbuilderspractice.utils.StatsType;
import ws.billy.speedbuilderspractice.utils.Sounds;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class TemplateManager
{
    public SpeedBuilders plugin;
    
    public TemplateManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public void check(final String s, final Player obj, final String s2) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s2);
        if (arena != null) {
            final HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
            final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x2"));
            final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y2"));
            final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z2"));
            final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x2"));
            final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y2"));
            final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z2"));
            int n = 0;
            for (int i = min; i <= max; ++i) {
                for (int j = min2; j <= max2; ++j) {
                    for (int k = min3; k <= max3; ++k) {
                        if (j != min2) {
                            final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(i, j, k).getState();
                            if (!state.getType().toString().equals("AIR") && state.getBlockData().getAsString().equals(arena.getCurrentBuildBlocks().get(n))) {
                                hashMap.put(n, state.getBlockData().getAsString());
                            }
                        }
                        ++n;
                    }
                }
            }
            final int l = 100 * hashMap.size() / arena.getCurrentBuildBlocks().size();
            if (l == 100) {
                final float f = Math.round(10.0f * (this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".build-time") - arena.getBuildTimeSubtractor() - arena.getBuildTime())) / 10.0f;
                for (final String s3 : arena.getPlayers()) {
                    Bukkit.getPlayer(s3).sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PERFECT_BUILD").replaceAll("%PLAYER%", obj.getName()).replaceAll("%TIME%", f + " " + Translations.translate("MAIN-SECONDS"))));
                    Sounds.BLOCK_NOTE_BLOCK_PLING.play(Bukkit.getPlayer(s3), 1.0f, 1.0f);
                    if (Bukkit.getPlayer(s3).equals(obj)) {
                        Sounds.ENTITY_PLAYER_LEVELUP.play(obj, 1.0f, 1.0f);
                        this.plugin.getMultiWorld().getNMSManager().showTitle(obj, 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-PERFECT_MATCH")));
                        arena.getPlayerPercent().put(obj.getName(), 100);
                        this.plugin.getStatsManager().incrementStat(StatsType.PBUILDS, obj, 1);
                        Bukkit.getPluginManager().callEvent((Event)new PlayerPerfectEvent(obj, f));
                    }
                }
                if ((int)Collections.min((Collection<?>)arena.getPlayerPercent().values()) >= 100) {
                    this.plugin.getMultiWorld().getTimerManager().guardianIsImpressed(arena.getName());
                }
            }
            else {
                arena.getPlayerPercent().put(obj.getName(), l);
            }
        }
    }
    
    public void loadTemplate(final String s, final String s2) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s2);
        if (arena != null) {
            if (s.equalsIgnoreCase("guardian")) {
                final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x2"));
                final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y2"));
                final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z2"));
                final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x2"));
                final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y2"));
                final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z2"));
                int i = 0;
                for (int j = min; j <= max; ++j) {
                    for (int k = min2; k <= max2; ++k) {
                        for (int l = min3; l <= max3; ++l) {
                            final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(j, k, l).getState();
                            state.setBlockData(Bukkit.createBlockData(this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + arena.getCurrentBuildRawName() + ".blocks." + i)));
                            state.update(true, false);
                            ++i;
                        }
                    }
                }
            }
            else {
                final HashMap<Integer, String> m = new HashMap<Integer, String>();
                final int min4 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x2"));
                final int min5 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y2"));
                final int min6 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z2"));
                final int max4 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.x2"));
                final int max5 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.y2"));
                final int max6 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".build-area.z2"));
                int n = 0;
                for (int n2 = min4; n2 <= max4; ++n2) {
                    for (int n3 = min5; n3 <= max5; ++n3) {
                        for (int n4 = min6; n4 <= max6; ++n4) {
                            final BlockState state2 = Bukkit.getWorld(arena.getName()).getBlockAt(n2, n3, n4).getState();
                            state2.setBlockData(Bukkit.createBlockData(this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + arena.getCurrentBuildRawName() + ".blocks." + n)));
                            state2.update(true, false);
                            if (n3 != min5 && !state2.getType().toString().equals("AIR")) {
                                m.put(n, state2.getBlockData().getAsString());
                            }
                            ++n;
                        }
                    }
                }
                if (arena.getCurrentBuildBlocks().isEmpty()) {
                    arena.getCurrentBuildBlocks().putAll(m);
                }
            }
        }
    }
    
    public void unloadTemplate(final String str, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            if (str.equalsIgnoreCase("guardian")) {
                for (final String str2 : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots.guardian.blocks").getKeys(false)) {
                    final String[] split = str2.split(",");
                    final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                    state.setBlockData(Bukkit.createBlockData(this.plugin.getConfigManager().getConfig("arenas.yml").getString("arenas." + arena.getName() + ".plots.guardian.blocks." + str2)));
                    state.update(true, false);
                }
            }
            else {
                Player player = null;
                for (final Map.Entry<String, String> entry : arena.getPlots().entrySet()) {
                    if (entry.getValue().equalsIgnoreCase(str)) {
                        player = Bukkit.getPlayer((String)entry.getKey());
                        break;
                    }
                }
                final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.x2"));
                final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.y2"));
                final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.z2"));
                final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.x2"));
                final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.y2"));
                final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + str + ".build-area.z2"));
                for (int i = min; i <= max; ++i) {
                    for (int j = min2; j <= max2; ++j) {
                        for (int k = min3; k <= max3; ++k) {
                            if (j != min2) {
                                final BlockState state2 = Bukkit.getWorld(arena.getName()).getBlockAt(i, j, k).getState();
                                final BlockData blockData = state2.getBlockData();
                                if (blockData.getMaterial() != Materials.AIR.getType("block")) {
                                    if (blockData.getMaterial() == Materials.WALL_TORCH.getType("block")) {
                                        player.getInventory().addItem(new ItemStack[] { Materials.TORCH.getItemStack(1) });
                                    }
                                    else if (blockData instanceof Slab) {
                                        if (((Slab)blockData).getType() == Slab.Type.DOUBLE) {
                                            player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.getMaterial(blockData.getMaterial().toString()), 2) });
                                        }
                                        else {
                                            player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.getMaterial(blockData.getMaterial().toString())) });
                                        }
                                    }
                                    else {
                                        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.getMaterial(blockData.getMaterial().toString())) });
                                    }
                                    state2.setType(Materials.AIR.getType("block"));
                                    state2.update(true, false);
                                    this.plugin.getMultiWorld().getNMSManager().updateBlockConnections(state2.getBlock());
                                }
                            }
                        }
                    }
                }
                final Location center = this.getCenter(new Location(Bukkit.getWorld(arena.getName()), (double)min, (double)min2, (double)min3), new Location(Bukkit.getWorld(arena.getName()), (double)max, (double)max2, (double)max3));
                center.getWorld().playEffect(center, Effect.STEP_SOUND, (Object)Materials.OAK_LOG.getType("block"), (int)Materials.OAK_LOG.getData("block"));
            }
        }
    }
    
    public void explodePlot(final String s, final String s2) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s2);
        if (arena != null) {
            final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.x2"));
            final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.y2"));
            final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.z2"));
            final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.x2"));
            final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.y2"));
            final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s + ".area.z2"));
            int n = 0;
            for (int i = min; i <= max; ++i) {
                for (int j = min2; j <= max2; ++j) {
                    for (int k = min3; k <= max3; ++k) {
                        final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(i, j, k).getState();
                        if (n % 7 == 0 && state.getType() != Materials.AIR.getType("block")) {
                            final FallingBlock spawnFallingBlock = state.getWorld().spawnFallingBlock(state.getLocation().add(0.0, 1.0, 0.0), state.getBlockData());
                            spawnFallingBlock.setDropItem(false);
                            spawnFallingBlock.setVelocity(new Vector(-0.15f + (float)(Math.random() * 0.44999999999999996), 1.0f, 0.15f + (float)(Math.random() * -0.44999999999999996)));
                        }
                        state.setType(Materials.AIR.getType("block"));
                        state.update(true, false);
                        ++n;
                    }
                }
            }
        }
    }
    
    public void resetPlots(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null && this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots")) {
            for (final String str : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots").getKeys(false)) {
                if (this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + arena.getName() + ".plots." + str + ".blocks")) {
                    for (final String str2 : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots." + str + ".blocks").getKeys(false)) {
                        final String[] split = str2.split(",");
                        final BlockState state = Bukkit.getWorld(arena.getName()).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                        state.setBlockData(Bukkit.createBlockData(this.plugin.getConfigManager().getConfig("arenas.yml").getString("arenas." + arena.getName() + ".plots." + str + ".blocks." + str2)));
                        state.update(true, false);
                    }
                }
            }
        }
    }
    
    public void savePlots(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            for (final String s2 : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas." + arena.getName() + ".plots").getKeys(false)) {
                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".plots." + s2 + ".blocks", (Object)"");
                if (s2.equalsIgnoreCase("guardian")) {
                    final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x2"));
                    final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y2"));
                    final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z2"));
                    final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.x2"));
                    final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.y2"));
                    final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots.guardian.template-area.z2"));
                    for (int i = min; i <= max; ++i) {
                        for (int j = min2; j <= max2; ++j) {
                            for (int k = min3; k <= max3; ++k) {
                                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".plots.guardian.blocks." + i + "," + j + "," + k, (Object)Bukkit.getWorld(arena.getName()).getBlockAt(i, j, k).getState().getBlockData().getAsString().replaceFirst("minecraft:", ""));
                            }
                        }
                    }
                }
                else {
                    final int min4 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.x2"));
                    final int min5 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.y2"));
                    final int min6 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.z2"));
                    final int max4 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.x2"));
                    final int max5 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.y2"));
                    final int max6 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + s2 + ".area.z2"));
                    for (int l = min4; l <= max4; ++l) {
                        for (int m = min5; m <= max5; ++m) {
                            for (int i2 = min6; i2 <= max6; ++i2) {
                                this.plugin.getConfigManager().getConfig("arenas.yml").set("arenas." + arena.getName() + ".plots." + s2 + ".blocks." + l + "," + m + "," + i2, (Object)Bukkit.getWorld(arena.getName()).getBlockAt(l, m, i2).getState().getBlockData().getAsString().replaceFirst("minecraft:", ""));
                            }
                        }
                    }
                }
            }
            new BukkitRunnable() {
                public void run() {
                    TemplateManager.this.plugin.getConfigManager().saveConfig("arenas.yml");
                }
            }.runTaskAsynchronously((Plugin)this.plugin);
        }
    }
    
    public void saveTemplate(final String str, final String s, final List<Block> list) {
        this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + str, (Object)"");
        this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + str + ".display-name", (Object)s);
        int i = 0;
        final Iterator<Block> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + str + ".blocks." + i, (Object)iterator.next().getState().getBlockData().getAsString().replaceFirst("minecraft:", ""));
            ++i;
        }
        new BukkitRunnable() {
            public void run() {
                TemplateManager.this.plugin.getConfigManager().saveConfig("templates.yml");
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
    
    public Location getCenter(final Location location, final Location location2) {
        final double min = Math.min(location.getX(), location2.getX());
        final double min2 = Math.min(location.getY(), location2.getY());
        final double min3 = Math.min(location.getZ(), location2.getZ());
        return new Location(location.getWorld(), min + (Math.max(location.getX(), location2.getX()) - min) / 2.0, min2 + (Math.max(location.getY(), location2.getY()) - min2) / 2.0, min3 + (Math.max(location.getZ(), location2.getZ()) - min3) / 2.0);
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
}
