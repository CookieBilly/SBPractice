 

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_14_R1;

import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Bed;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import net.minecraft.server.v1_14_R1.DataWatcherObject;
import net.minecraft.server.v1_14_R1.DataWatcherRegistry;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import net.minecraft.server.v1_14_R1.DataWatcher;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.Particle;
import org.bukkit.Location;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class NMSManager implements ws.billy.speedbuilderspractice.multiworld.managers.NMSManager
{
    public SpeedBuilders plugin;
    
    public NMSManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @Override
    public void showActionBar(final Player player, final String s) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', s)).create());
    }
    
    @Override
    public void showParticleEffect1(final Player player, final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        player.spawnParticle(Particle.MOB_APPEARANCE, location, n5, (double)n, (double)n2, (double)n3, (double)n4);
    }
    
    @Override
    public void showParticleEffect2(final Player player, final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        player.spawnParticle(Particle.EXPLOSION_HUGE, location, n5, (double)n, (double)n2, (double)n3, (double)n4);
    }
    
    @Override
    public void showTitle(final Player player, final int n, final int n2, final int n3, final String s, final String s2) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent)null, n, n2, n3));
        if (s != null) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', s) + "\"}")));
        }
        if (s2 != null) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', s2) + "\"}")));
        }
    }
    
    @Override
    public DataWatcher setGuardianTarget(final Guardian guardian, final int i) {
        final DataWatcher dataWatcher = ((CraftLivingEntity)guardian).getHandle().getDataWatcher();
        dataWatcher.set(new DataWatcherObject(15, DataWatcherRegistry.b), (Object)i);
        return dataWatcher;
    }
    
    @Override
    public void setPlayerVisibility(final Player player, final Player player2, final boolean b) {
        if (b) {
            if (player.hasMetadata("invisible")) {
                if (player2 != null) {
                    player2.showPlayer((Plugin)this.plugin, player);
                    player.removeMetadata("invisible", (Plugin)this.plugin);
                }
                else {
                    for (final Player player3 : Bukkit.getOnlinePlayers()) {
                        if (!player3.equals(player)) {
                            player3.showPlayer((Plugin)this.plugin, player);
                        }
                    }
                    player.removeMetadata("invisible", (Plugin)this.plugin);
                }
            }
        }
        else if (player2 != null) {
            player2.hidePlayer((Plugin)this.plugin, player);
            player.setMetadata("invisible", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)"true"));
        }
        else {
            for (final Player player4 : Bukkit.getOnlinePlayers()) {
                if (!player4.equals(player)) {
                    player4.hidePlayer((Plugin)this.plugin, player);
                }
            }
            player.setMetadata("invisible", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)"true"));
        }
    }
    
    @Override
    public void updateBlockConnections(final Block block) {
        if (block.getRelative(BlockFace.DOWN).getBlockData() instanceof Bisected) {
            if (block.getRelative(BlockFace.DOWN).getBlockData() instanceof Stairs) {
                return;
            }
            if (((Bisected)block.getRelative(BlockFace.DOWN).getState().getBlockData()).getHalf() == Bisected.Half.BOTTOM) {
                final BlockState state = block.getRelative(BlockFace.DOWN).getState();
                state.setType(Materials.AIR.getType("block"));
                state.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.UP).getBlockData() instanceof Bisected) {
            if (block.getRelative(BlockFace.UP).getBlockData() instanceof Stairs) {
                return;
            }
            if (((Bisected)block.getRelative(BlockFace.UP).getState().getBlockData()).getHalf() == Bisected.Half.TOP) {
                final BlockState state2 = block.getRelative(BlockFace.UP).getState();
                state2.setType(Materials.AIR.getType("block"));
                state2.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.EAST).getState().getBlockData() instanceof Bed) {
            final Bed bed = (Bed)block.getRelative(BlockFace.EAST).getState().getBlockData();
            if (bed.getFacing() == BlockFace.EAST) {
                if (bed.getPart() == Bed.Part.HEAD) {
                    final BlockState state3 = block.getRelative(BlockFace.EAST).getState();
                    state3.setType(Materials.AIR.getType("block"));
                    state3.update(true, false);
                }
            }
            else if (bed.getFacing() == BlockFace.EAST.getOppositeFace() && bed.getPart() == Bed.Part.FOOT) {
                final BlockState state4 = block.getRelative(BlockFace.EAST).getState();
                state4.setType(Materials.AIR.getType("block"));
                state4.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.NORTH).getState().getBlockData() instanceof Bed) {
            final Bed bed2 = (Bed)block.getRelative(BlockFace.NORTH).getState().getBlockData();
            if (bed2.getFacing() == BlockFace.NORTH) {
                if (bed2.getPart() == Bed.Part.HEAD) {
                    final BlockState state5 = block.getRelative(BlockFace.NORTH).getState();
                    state5.setType(Materials.AIR.getType("block"));
                    state5.update(true, false);
                }
            }
            else if (bed2.getFacing() == BlockFace.NORTH.getOppositeFace() && bed2.getPart() == Bed.Part.FOOT) {
                final BlockState state6 = block.getRelative(BlockFace.NORTH).getState();
                state6.setType(Materials.AIR.getType("block"));
                state6.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.SOUTH).getState().getBlockData() instanceof Bed) {
            final Bed bed3 = (Bed)block.getRelative(BlockFace.SOUTH).getState().getBlockData();
            if (bed3.getFacing() == BlockFace.SOUTH) {
                if (bed3.getPart() == Bed.Part.HEAD) {
                    final BlockState state7 = block.getRelative(BlockFace.SOUTH).getState();
                    state7.setType(Materials.AIR.getType("block"));
                    state7.update(true, false);
                }
            }
            else if (bed3.getFacing() == BlockFace.SOUTH.getOppositeFace() && bed3.getPart() == Bed.Part.FOOT) {
                final BlockState state8 = block.getRelative(BlockFace.SOUTH).getState();
                state8.setType(Materials.AIR.getType("block"));
                state8.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.WEST).getState().getBlockData() instanceof Bed) {
            final Bed bed4 = (Bed)block.getRelative(BlockFace.WEST).getState().getBlockData();
            if (bed4.getFacing() == BlockFace.WEST) {
                if (bed4.getPart() == Bed.Part.HEAD) {
                    final BlockState state9 = block.getRelative(BlockFace.WEST).getState();
                    state9.setType(Materials.AIR.getType("block"));
                    state9.update(true, false);
                }
            }
            else if (bed4.getFacing() == BlockFace.WEST.getOppositeFace() && bed4.getPart() == Bed.Part.FOOT) {
                final BlockState state10 = block.getRelative(BlockFace.WEST).getState();
                state10.setType(Materials.AIR.getType("block"));
                state10.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.EAST).getState().getBlockData() instanceof MultipleFacing) {
            final BlockState state11 = block.getRelative(BlockFace.EAST).getState();
            final MultipleFacing blockData = (MultipleFacing)state11.getBlockData();
            blockData.setFace(BlockFace.WEST, false);
            state11.setBlockData((BlockData)blockData);
            state11.update(true, false);
        }
        if (block.getRelative(BlockFace.NORTH).getState().getBlockData() instanceof MultipleFacing) {
            final BlockState state12 = block.getRelative(BlockFace.NORTH).getState();
            final MultipleFacing blockData2 = (MultipleFacing)state12.getBlockData();
            blockData2.setFace(BlockFace.SOUTH, false);
            state12.setBlockData((BlockData)blockData2);
            state12.update(true, false);
        }
        if (block.getRelative(BlockFace.SOUTH).getState().getBlockData() instanceof MultipleFacing) {
            final BlockState state13 = block.getRelative(BlockFace.SOUTH).getState();
            final MultipleFacing blockData3 = (MultipleFacing)state13.getBlockData();
            blockData3.setFace(BlockFace.NORTH, false);
            state13.setBlockData((BlockData)blockData3);
            state13.update(true, false);
        }
        if (block.getRelative(BlockFace.WEST).getState().getBlockData() instanceof MultipleFacing) {
            final BlockState state14 = block.getRelative(BlockFace.WEST).getState();
            final MultipleFacing blockData4 = (MultipleFacing)state14.getBlockData();
            blockData4.setFace(BlockFace.EAST, false);
            state14.setBlockData((BlockData)blockData4);
            state14.update(true, false);
        }
    }
}
