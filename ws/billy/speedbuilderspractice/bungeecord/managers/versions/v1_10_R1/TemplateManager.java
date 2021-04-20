 

package ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_10_R1;

import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;
import org.bukkit.Effect;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import java.util.Iterator;
import org.bukkit.block.BlockState;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.event.Event;
import ws.billy.speedbuilderspractice.api.events.PlayerPerfectEvent;
import ws.billy.speedbuilderspractice.utils.StatsType;
import ws.billy.speedbuilderspractice.utils.Sounds;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.block.Skull;
import org.bukkit.block.Banner;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class TemplateManager extends ws.billy.speedbuilderspractice.bungeecord.managers.TemplateManager
{
    public SpeedBuilders plugin;
    
    public TemplateManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @Override
    public void check(final String s, final Player obj) {
        final HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        final int min = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x2"));
        final int min2 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y2"));
        final int min3 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z2"));
        final int max = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x2"));
        final int max2 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y2"));
        final int max3 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z2"));
        int n = 1;
        for (int i = min; i <= max; ++i) {
            for (int j = min2; j <= max2; ++j) {
                for (int k = min3; k <= max3; ++k) {
                    if (j != min2) {
                        final BlockState state = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(i, j, k).getState();
                        if (!state.getType().toString().equals("AIR")) {
                            if (state.getType().toString().equals("STANDING_BANNER") || state.getType().toString().equals("WALL_BANNER")) {
                                if ((state.getType().toString() + ":" + (15 - ((Banner)state).getBaseColor().ordinal()) + ":" + state.getRawData()).equals(this.plugin.getBungeeCord().currentBuildBlocks.get(n))) {
                                    hashMap.put(n, state.getType().toString() + ":" + (15 - ((Banner)state).getBaseColor().ordinal()) + ":" + state.getRawData());
                                }
                            }
                            else if (state.getType().toString().equals("BED_BLOCK")) {
                                if ((state.getType().toString() + ":0:" + state.getRawData()).equals(this.plugin.getBungeeCord().currentBuildBlocks.get(n))) {
                                    hashMap.put(n, state.getType().toString() + ":0:" + state.getRawData());
                                }
                            }
                            else if (state.getType().toString().equals("SKULL")) {
                                if ((state.getType().toString() + ":" + ((Skull)state).getSkullType().ordinal() + ":" + state.getRawData() + ":" + ((Skull)state).getRotation().toString()).equals(this.plugin.getBungeeCord().currentBuildBlocks.get(n))) {
                                    hashMap.put(n, state.getType().toString() + ":" + ((Skull)state).getSkullType().ordinal() + ":" + state.getRawData() + ":" + ((Skull)state).getRotation().toString());
                                }
                            }
                            else if ((state.getType().toString() + ":" + state.getRawData()).equals(this.plugin.getBungeeCord().currentBuildBlocks.get(n))) {
                                hashMap.put(n, state.getType().toString() + ":" + state.getRawData());
                            }
                        }
                    }
                    ++n;
                }
            }
        }
        final int l = 100 * hashMap.size() / this.plugin.getBungeeCord().currentBuildBlocks.size();
        if (l == 100) {
            final float f = Math.round(10.0f * (this.plugin.getConfigManager().getConfig("config.yml").getInt("bungeecord.build-time") - this.plugin.getBungeeCord().buildTimeSubtractor - this.plugin.getBungeeCord().getTimerManager().getBuildTime())) / 10.0f;
            for (final Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("MAIN-PERFECT_BUILD").replaceAll("%PLAYER%", obj.getName()).replaceAll("%TIME%", f + " " + Translations.translate("MAIN-SECONDS"))));
                Sounds.BLOCK_NOTE_BLOCK_PLING.play(player, 1.0f, 1.0f);
                if (player.equals(obj)) {
                    Sounds.ENTITY_PLAYER_LEVELUP.play(obj, 1.0f, 1.0f);
                    this.plugin.getBungeeCord().getNMSManager().showTitle(obj, 0, 40, 10, "", ChatColor.translateAlternateColorCodes('&', Translations.translate("TITLE-PERFECT_MATCH")));
                    this.plugin.getBungeeCord().playerPercent.put(obj.getName(), 100);
                    this.plugin.getStatsManager().incrementStat(StatsType.PBUILDS, obj, 1);
                    Bukkit.getPluginManager().callEvent((Event)new PlayerPerfectEvent(obj, f));
                }
            }
            if (Collections.min((Collection<? extends Integer>)this.plugin.getBungeeCord().playerPercent.values()) >= 100) {
                this.plugin.getBungeeCord().getTimerManager().guardianIsImpressed();
            }
        }
        else {
            this.plugin.getBungeeCord().playerPercent.put(obj.getName(), l);
        }
    }
    
    @Override
    public void loadTemplate(final String s) {
        if (s.equalsIgnoreCase("guardian")) {
            final int min = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.x2"));
            final int min2 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.y2"));
            final int min3 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.z2"));
            final int max = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.x2"));
            final int max2 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.y2"));
            final int max3 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.template-area.z2"));
            int i = 1;
            for (int j = min; j <= max; ++j) {
                for (int k = min2; k <= max2; ++k) {
                    for (int l = min3; l <= max3; ++l) {
                        final String string = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i + ".type");
                        if (string.equals("STANDING_BANNER") || string.equals("WALL_BANNER")) {
                            final String[] split = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i + ".data").split(":");
                            final int int1 = Integer.parseInt(split[0]);
                            final byte byte1 = Byte.parseByte(split[1]);
                            final BlockState state = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            state.setType(Material.getMaterial(string));
                            state.setRawData(byte1);
                            state.update(true, false);
                            final Banner banner = (Banner)Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            if (int1 == 0) {
                                banner.setBaseColor(DyeColor.WHITE);
                            }
                            else if (int1 == 1) {
                                banner.setBaseColor(DyeColor.ORANGE);
                            }
                            else if (int1 == 2) {
                                banner.setBaseColor(DyeColor.MAGENTA);
                            }
                            else if (int1 == 3) {
                                banner.setBaseColor(DyeColor.LIGHT_BLUE);
                            }
                            else if (int1 == 4) {
                                banner.setBaseColor(DyeColor.YELLOW);
                            }
                            else if (int1 == 5) {
                                banner.setBaseColor(DyeColor.LIME);
                            }
                            else if (int1 == 6) {
                                banner.setBaseColor(DyeColor.PINK);
                            }
                            else if (int1 == 7) {
                                banner.setBaseColor(DyeColor.GRAY);
                            }
                            else if (int1 == 8) {
                                banner.setBaseColor(DyeColor.LIGHT_GRAY);
                            }
                            else if (int1 == 9) {
                                banner.setBaseColor(DyeColor.CYAN);
                            }
                            else if (int1 == 10) {
                                banner.setBaseColor(DyeColor.PURPLE);
                            }
                            else if (int1 == 11) {
                                banner.setBaseColor(DyeColor.BLUE);
                            }
                            else if (int1 == 12) {
                                banner.setBaseColor(DyeColor.BROWN);
                            }
                            else if (int1 == 13) {
                                banner.setBaseColor(DyeColor.GREEN);
                            }
                            else if (int1 == 14) {
                                banner.setBaseColor(DyeColor.RED);
                            }
                            else if (int1 == 15) {
                                banner.setBaseColor(DyeColor.BLACK);
                            }
                            banner.update(true, false);
                        }
                        else if (string.equals("BED_BLOCK")) {
                            final byte byte2 = Byte.parseByte(this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i + ".data").split(":")[1]);
                            final BlockState state2 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            state2.setType(Material.getMaterial(string));
                            state2.setRawData(byte2);
                            state2.update(true, false);
                        }
                        else if (string.equals("SKULL")) {
                            final String[] split2 = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i + ".data").split(":");
                            final int int2 = Integer.parseInt(split2[0]);
                            final byte byte3 = Byte.parseByte(split2[1]);
                            final String s2 = split2[2];
                            final BlockState state3 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            state3.setType(Material.getMaterial(string));
                            state3.setRawData(byte3);
                            state3.update(true, false);
                            final Skull skull = (Skull)Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            skull.setRotation(BlockFace.valueOf(s2));
                            if (int2 == 0) {
                                skull.setSkullType(SkullType.SKELETON);
                            }
                            else if (int2 == 1) {
                                skull.setSkullType(SkullType.WITHER);
                            }
                            else if (int2 == 2) {
                                skull.setSkullType(SkullType.ZOMBIE);
                            }
                            else if (int2 == 3) {
                                skull.setSkullType(SkullType.PLAYER);
                            }
                            else if (int2 == 4) {
                                skull.setSkullType(SkullType.CREEPER);
                            }
                            skull.update(true, false);
                        }
                        else {
                            final BlockState state4 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(j, k, l).getState();
                            state4.setType(Material.getMaterial(string));
                            state4.setRawData((byte)this.plugin.getConfigManager().getConfig("templates.yml").getInt("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i + ".data"));
                            state4.update(true, false);
                        }
                        ++i;
                    }
                }
            }
        }
        else {
            final HashMap<Integer, String> m = new HashMap<Integer, String>();
            final int min4 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x2"));
            final int min5 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y2"));
            final int min6 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z2"));
            final int max4 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.x2"));
            final int max5 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.y2"));
            final int max6 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".build-area.z2"));
            int i2 = 1;
            for (int n = min4; n <= max4; ++n) {
                for (int n2 = min5; n2 <= max5; ++n2) {
                    for (int n3 = min6; n3 <= max6; ++n3) {
                        final String string2 = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i2 + ".type");
                        if (string2.equals("STANDING_BANNER") || string2.equals("WALL_BANNER")) {
                            final String[] split3 = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i2 + ".data").split(":");
                            final int int3 = Integer.parseInt(split3[0]);
                            final byte byte4 = Byte.parseByte(split3[1]);
                            final BlockState state5 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            state5.setType(Material.getMaterial(string2));
                            state5.setRawData(byte4);
                            state5.update(true, false);
                            final Banner banner2 = (Banner)Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            if (int3 == 0) {
                                banner2.setBaseColor(DyeColor.WHITE);
                            }
                            else if (int3 == 1) {
                                banner2.setBaseColor(DyeColor.ORANGE);
                            }
                            else if (int3 == 2) {
                                banner2.setBaseColor(DyeColor.MAGENTA);
                            }
                            else if (int3 == 3) {
                                banner2.setBaseColor(DyeColor.LIGHT_BLUE);
                            }
                            else if (int3 == 4) {
                                banner2.setBaseColor(DyeColor.YELLOW);
                            }
                            else if (int3 == 5) {
                                banner2.setBaseColor(DyeColor.LIME);
                            }
                            else if (int3 == 6) {
                                banner2.setBaseColor(DyeColor.PINK);
                            }
                            else if (int3 == 7) {
                                banner2.setBaseColor(DyeColor.GRAY);
                            }
                            else if (int3 == 8) {
                                banner2.setBaseColor(DyeColor.LIGHT_GRAY);
                            }
                            else if (int3 == 9) {
                                banner2.setBaseColor(DyeColor.CYAN);
                            }
                            else if (int3 == 10) {
                                banner2.setBaseColor(DyeColor.PURPLE);
                            }
                            else if (int3 == 11) {
                                banner2.setBaseColor(DyeColor.BLUE);
                            }
                            else if (int3 == 12) {
                                banner2.setBaseColor(DyeColor.BROWN);
                            }
                            else if (int3 == 13) {
                                banner2.setBaseColor(DyeColor.GREEN);
                            }
                            else if (int3 == 14) {
                                banner2.setBaseColor(DyeColor.RED);
                            }
                            else if (int3 == 15) {
                                banner2.setBaseColor(DyeColor.BLACK);
                            }
                            banner2.update(true, false);
                            if (n2 != min5 && !state5.getType().toString().equals("AIR")) {
                                m.put(i2, string2 + ":" + int3 + ":" + byte4);
                            }
                        }
                        else if (string2.equals("BED_BLOCK")) {
                            final byte byte5 = Byte.parseByte(this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i2 + ".data").split(":")[1]);
                            final BlockState state6 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            state6.setType(Material.getMaterial(string2));
                            state6.setRawData(byte5);
                            state6.update(true, false);
                            if (n2 != min5 && !state6.getType().toString().equals("AIR")) {
                                m.put(i2, string2 + ":0:" + byte5);
                            }
                        }
                        else if (string2.equals("SKULL")) {
                            final String[] split4 = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i2 + ".data").split(":");
                            final int int4 = Integer.parseInt(split4[0]);
                            final byte byte6 = Byte.parseByte(split4[1]);
                            final String str = split4[2];
                            final BlockState state7 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            state7.setType(Material.getMaterial(string2));
                            state7.setRawData(byte6);
                            state7.update(true, false);
                            final Skull skull2 = (Skull)Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            skull2.setRotation(BlockFace.valueOf(str));
                            if (int4 == 0) {
                                skull2.setSkullType(SkullType.SKELETON);
                            }
                            else if (int4 == 1) {
                                skull2.setSkullType(SkullType.WITHER);
                            }
                            else if (int4 == 2) {
                                skull2.setSkullType(SkullType.ZOMBIE);
                            }
                            else if (int4 == 3) {
                                skull2.setSkullType(SkullType.PLAYER);
                            }
                            else if (int4 == 4) {
                                skull2.setSkullType(SkullType.CREEPER);
                            }
                            skull2.update(true, false);
                            if (n2 != min5 && !state7.getType().toString().equals("AIR")) {
                                m.put(i2, string2 + ":" + int4 + ":" + byte6 + ":" + str);
                            }
                        }
                        else {
                            final String string3 = this.plugin.getConfigManager().getConfig("templates.yml").getString("templates." + this.plugin.getBungeeCord().currentBuildRawName + ".blocks." + i2 + ".data");
                            final BlockState state8 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(n, n2, n3).getState();
                            state8.setType(Material.getMaterial(string2));
                            state8.setRawData(Byte.parseByte(string3));
                            state8.update(true, false);
                            if (n2 != min5 && !state8.getType().toString().equals("AIR")) {
                                m.put(i2, string2 + ":" + string3);
                            }
                        }
                        ++i2;
                    }
                }
            }
            if (this.plugin.getBungeeCord().currentBuildBlocks.isEmpty()) {
                this.plugin.getBungeeCord().currentBuildBlocks.putAll(m);
            }
        }
    }
    
    @Override
    public void unloadTemplate(final String str) {
        if (str.equalsIgnoreCase("guardian")) {
            for (final String s : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.blocks").getKeys(false)) {
                final String[] split = s.split(",");
                final String string = this.plugin.getConfigManager().getConfig("maps.yml").getString("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.blocks." + s + ".type");
                final BlockState state = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                state.setType(Material.getMaterial(string));
                state.setRawData((byte)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.blocks." + s + ".data"));
                state.update(true, false);
            }
        }
        else {
            Player player = null;
            for (final Map.Entry<String, String> entry : this.plugin.getBungeeCord().plots.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(str)) {
                    player = Bukkit.getPlayer((String)entry.getKey());
                    break;
                }
            }
            final int min = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.x2"));
            final int min2 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.y2"));
            final int min3 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.z2"));
            final int max = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.x2"));
            final int max2 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.y2"));
            final int max3 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + str + ".build-area.z2"));
            for (int i = min; i <= max; ++i) {
                for (int j = min2; j <= max2; ++j) {
                    for (int k = min3; k <= max3; ++k) {
                        if (j != min2) {
                            final BlockState state2 = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(i, j, k).getState();
                            final String string2 = state2.getType().toString();
                            final byte rawData = state2.getRawData();
                            if (!string2.equals("AIR")) {
                                if (string2.contains("DOUBLE_SLAB") || string2.contains("DOUBLE_STEP") || string2.contains("DOUBLE_STONE_SLAB2")) {
                                    player.getInventory().addItem(new ItemStack[] { Materials.fromString(string2 + ":" + rawData).getItemStack(2) });
                                }
                                else if (string2.equals("STANDING_BANNER") || string2.equals("WALL_BANNER")) {
                                    player.getInventory().addItem(new ItemStack[] { Materials.fromString("BANNER:" + (15 - ((Banner)state2).getBaseColor().ordinal())).getItemStack(1) });
                                }
                                else if (string2.equals("BED_BLOCK")) {
                                    player.getInventory().addItem(new ItemStack[] { Materials.fromString("BED:0").getItemStack(1) });
                                }
                                else if (string2.equals("SKULL")) {
                                    player.getInventory().addItem(new ItemStack[] { Materials.fromString("SKULL_ITEM:" + ((Skull)state2).getSkullType().ordinal()).getItemStack(1) });
                                }
                                else {
                                    player.getInventory().addItem(new ItemStack[] { Materials.fromString(string2 + ":" + rawData).getItemStack(1) });
                                }
                                state2.setType(Materials.AIR.getType("block"));
                                state2.update(true, false);
                                this.plugin.getBungeeCord().getNMSManager().updateBlockConnections(state2.getBlock());
                            }
                        }
                    }
                }
            }
            final Location center = this.getCenter(new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), (double)min, (double)min2, (double)min3), new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), (double)max, (double)max2, (double)max3));
            center.getWorld().playEffect(center, Effect.STEP_SOUND, (Object)Materials.OAK_LOG.getType("block"), (int)Materials.OAK_LOG.getData("block"));
        }
    }
    
    @Override
    public void explodePlot(final String s) {
        final int min = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.x2"));
        final int min2 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.y2"));
        final int min3 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.z2"));
        final int max = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.x2"));
        final int max2 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.y2"));
        final int max3 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + s + ".area.z2"));
        int n = 0;
        for (int i = min; i <= max; ++i) {
            for (int j = min2; j <= max2; ++j) {
                for (int k = min3; k <= max3; ++k) {
                    final BlockState state = Bukkit.getWorld(this.plugin.getBungeeCord().currentMap).getBlockAt(i, j, k).getState();
                    if (state.getType() != Materials.AIR.getType("block")) {
                        if (n % 7 == 0) {
                            final FallingBlock spawnFallingBlock = state.getWorld().spawnFallingBlock(state.getLocation().add(0.0, 1.0, 0.0), state.getType(), state.getRawData());
                            spawnFallingBlock.setDropItem(false);
                            spawnFallingBlock.setVelocity(new Vector(-0.15f + (float)(Math.random() * 0.44999999999999996), 1.0f, 0.15f + (float)(Math.random() * -0.44999999999999996)));
                        }
                        state.setType(Materials.AIR.getType("block"));
                        state.update(true, false);
                    }
                    ++n;
                }
            }
        }
    }
    
    @Override
    public void resetPlots(final String s) {
        if (this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + s + ".plots")) {
            for (final String s2 : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + s + ".plots").getKeys(false)) {
                if (this.plugin.getConfigManager().getConfig("maps.yml").contains("maps." + s + ".plots." + s2 + ".blocks")) {
                    for (final String s3 : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + s + ".plots." + s2 + ".blocks").getKeys(false)) {
                        final String[] split = s3.split(",");
                        final String string = this.plugin.getConfigManager().getConfig("maps.yml").getString("maps." + s + ".plots." + s2 + ".blocks." + s3 + ".type");
                        final BlockState state = Bukkit.getWorld(s).getBlockAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])).getState();
                        state.setType(Material.getMaterial(string));
                        state.setRawData((byte)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + s2 + ".blocks." + s3 + ".data"));
                        state.update(true, false);
                    }
                }
            }
        }
    }
    
    @Override
    public void savePlots(final String s) {
        for (final String str : this.plugin.getConfigManager().getConfig("maps.yml").getConfigurationSection("maps." + s + ".plots").getKeys(false)) {
            this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks", (Object)"");
            if (str.equalsIgnoreCase("guardian")) {
                final int min = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.x2"));
                final int min2 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.y2"));
                final int min3 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.z2"));
                final int max = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.x2"));
                final int max2 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.y2"));
                final int max3 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots.guardian.template-area.z2"));
                for (int i = min; i <= max; ++i) {
                    for (int j = min2; j <= max2; ++j) {
                        for (int k = min3; k <= max3; ++k) {
                            final BlockState state = Bukkit.getWorld(s).getBlockAt(i, j, k).getState();
                            if (state.getType().toString().equals("STANDING_BANNER") || state.getType().toString().equals("WALL_BANNER")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".type", (Object)state.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".data", (Object)(15 - ((Banner)state).getBaseColor().ordinal() + ":" + state.getRawData()));
                            }
                            else if (state.getType().toString().equals("BED_BLOCK")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".type", (Object)state.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".data", (Object)("0:" + state.getRawData()));
                            }
                            else if (state.getType().toString().equals("SKULL")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".type", (Object)state.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".data", (Object)(((Skull)state).getSkullType().ordinal() + ":" + state.getRawData() + ":" + ((Skull)state).getRotation().toString()));
                            }
                            else {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".type", (Object)state.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots.guardian.blocks." + i + "," + j + "," + k + ".data", (Object)state.getRawData());
                            }
                        }
                    }
                }
            }
            else {
                final int min4 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.x2"));
                final int min5 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.y2"));
                final int min6 = Math.min(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.z2"));
                final int max4 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.x1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.x2"));
                final int max5 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.y1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.y2"));
                final int max6 = Math.max(this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.z1"), this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + s + ".plots." + str + ".area.z2"));
                for (int l = min4; l <= max4; ++l) {
                    for (int n = min5; n <= max5; ++n) {
                        for (int n2 = min6; n2 <= max6; ++n2) {
                            final BlockState state2 = Bukkit.getWorld(s).getBlockAt(l, n, n2).getState();
                            if (state2.getType().toString().equals("STANDING_BANNER") || state2.getType().toString().equals("WALL_BANNER")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".type", (Object)state2.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".data", (Object)(15 - ((Banner)state2).getBaseColor().ordinal() + ":" + state2.getRawData()));
                            }
                            else if (state2.getType().toString().equals("BED_BLOCK")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".type", (Object)state2.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".data", (Object)("0:" + state2.getRawData()));
                            }
                            else if (state2.getType().toString().equals("SKULL")) {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".type", (Object)state2.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".data", (Object)(((Skull)state2).getSkullType().ordinal() + ":" + state2.getRawData() + ":" + ((Skull)state2).getRotation().toString()));
                            }
                            else {
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".type", (Object)state2.getType().toString());
                                this.plugin.getConfigManager().getConfig("maps.yml").set("maps." + s + ".plots." + str + ".blocks." + l + "," + n + "," + n2 + ".data", (Object)state2.getRawData());
                            }
                        }
                    }
                }
            }
        }
        new BukkitRunnable() {
            public void run() {
                TemplateManager.this.plugin.getConfigManager().saveConfig("maps.yml");
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
    
    @Override
    public void saveTemplate(final String s, final String s2, final List<Block> list) {
        this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s, (Object)"");
        this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".display-name", (Object)s2);
        int n = 1;
        final Iterator<Block> iterator = list.iterator();
        while (iterator.hasNext()) {
            final BlockState state = iterator.next().getState();
            if (state.getType().toString().equals("STANDING_BANNER") || state.getType().toString().equals("WALL_BANNER")) {
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".type", (Object)state.getType().toString());
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".data", (Object)(15 - ((Banner)state).getBaseColor().ordinal() + ":" + state.getRawData()));
            }
            else if (state.getType().toString().equals("BED_BLOCK")) {
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".type", (Object)state.getType().toString());
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".data", (Object)("0:" + state.getRawData()));
            }
            else if (state.getType().toString().equals("SKULL")) {
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".type", (Object)state.getType().toString());
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".data", (Object)(((Skull)state).getSkullType().ordinal() + ":" + state.getRawData() + ":" + ((Skull)state).getRotation().toString()));
            }
            else {
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".type", (Object)state.getType().toString());
                this.plugin.getConfigManager().getConfig("templates.yml").set("templates." + s + ".blocks." + n + ".data", (Object)state.getRawData());
            }
            ++n;
        }
        new BukkitRunnable() {
            public void run() {
                TemplateManager.this.plugin.getConfigManager().saveConfig("templates.yml");
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
    }
}
