

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_10_R1;

import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;
import ws.billy.speedbuilderspractice.utils.Materials;
import net.minecraft.server.v1_10_R1.BlockPosition;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.material.Door;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import net.minecraft.server.v1_10_R1.DataWatcherObject;
import net.minecraft.server.v1_10_R1.DataWatcherRegistry;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import net.minecraft.server.v1_10_R1.DataWatcher;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_10_R1.EnumParticle;
import org.bukkit.Location;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
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
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', s) + "\"}"), (byte)2));
    }
    
    @Override
    public void showParticleEffect1(final Player player, final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutWorldParticles(EnumParticle.MOB_APPEARANCE, true, (float)location.getX(), (float)location.getY(), (float)location.getZ(), n, n2, n3, n4, n5, new int[0]));
    }
    
    @Override
    public void showParticleEffect2(final Player player, final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_HUGE, true, (float)location.getX(), (float)location.getY(), (float)location.getZ(), n, n2, n3, n4, n5, new int[0]));
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
        dataWatcher.set(new DataWatcherObject(13, DataWatcherRegistry.b), (Object)i);
        return dataWatcher;
    }
    
    @Override
    public void setPlayerVisibility(final Player player, final Player player2, final boolean b) {
        if (b) {
            if (player.hasMetadata("invisible")) {
                if (player2 != null) {
                    player2.showPlayer(player);
                    player.removeMetadata("invisible", (Plugin)this.plugin);
                }
                else {
                    for (final Player player3 : Bukkit.getOnlinePlayers()) {
                        if (!player3.equals(player)) {
                            player3.showPlayer(player);
                        }
                    }
                    player.removeMetadata("invisible", (Plugin)this.plugin);
                }
            }
        }
        else if (player2 != null) {
            player2.hidePlayer(player);
            player.setMetadata("invisible", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)"true"));
        }
        else {
            for (final Player player4 : Bukkit.getOnlinePlayers()) {
                if (!player4.equals(player)) {
                    player4.hidePlayer(player);
                }
            }
            player.setMetadata("invisible", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)"true"));
        }
    }
    
    @Override
    public void updateBlockConnections(final Block block) {
        if (block.getRelative(BlockFace.DOWN).getState().getType().toString().equals("DOUBLE_PLANT") || block.getRelative(BlockFace.DOWN).getState().getData() instanceof Door) {
            final BlockState state = block.getRelative(BlockFace.DOWN).getState();
            if (((CraftWorld)state.getWorld()).getHandle().getType(new BlockPosition(state.getX(), state.getY(), state.getZ())).toString().contains("half=lower")) {
                state.setType(Materials.AIR.getType("block"));
                state.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.UP).getState().getType().toString().equals("DOUBLE_PLANT") || block.getRelative(BlockFace.UP).getState().getData() instanceof Door) {
            final BlockState state2 = block.getRelative(BlockFace.UP).getState();
            if (((CraftWorld)state2.getWorld()).getHandle().getType(new BlockPosition(state2.getX(), state2.getY(), state2.getZ())).toString().contains("half=upper")) {
                state2.setType(Materials.AIR.getType("block"));
                state2.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.EAST).getState().getData() instanceof Bed) {
            final Bed bed = (Bed)block.getRelative(BlockFace.EAST).getState().getData();
            if (bed.getFacing() == BlockFace.EAST) {
                if (bed.isHeadOfBed()) {
                    final BlockState state3 = block.getRelative(BlockFace.EAST).getState();
                    state3.setType(Materials.AIR.getType("block"));
                    state3.update(true, false);
                }
            }
            else if (bed.getFacing() == BlockFace.EAST.getOppositeFace() && !bed.isHeadOfBed()) {
                final BlockState state4 = block.getRelative(BlockFace.EAST).getState();
                state4.setType(Materials.AIR.getType("block"));
                state4.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.NORTH).getState().getData() instanceof Bed) {
            final Bed bed2 = (Bed)block.getRelative(BlockFace.NORTH).getState().getData();
            if (bed2.getFacing() == BlockFace.NORTH) {
                if (bed2.isHeadOfBed()) {
                    final BlockState state5 = block.getRelative(BlockFace.NORTH).getState();
                    state5.setType(Materials.AIR.getType("block"));
                    state5.update(true, false);
                }
            }
            else if (bed2.getFacing() == BlockFace.NORTH.getOppositeFace() && !bed2.isHeadOfBed()) {
                final BlockState state6 = block.getRelative(BlockFace.NORTH).getState();
                state6.setType(Materials.AIR.getType("block"));
                state6.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.SOUTH).getState().getData() instanceof Bed) {
            final Bed bed3 = (Bed)block.getRelative(BlockFace.SOUTH).getState().getData();
            if (bed3.getFacing() == BlockFace.SOUTH) {
                if (bed3.isHeadOfBed()) {
                    final BlockState state7 = block.getRelative(BlockFace.SOUTH).getState();
                    state7.setType(Materials.AIR.getType("block"));
                    state7.update(true, false);
                }
            }
            else if (bed3.getFacing() == BlockFace.SOUTH.getOppositeFace() && !bed3.isHeadOfBed()) {
                final BlockState state8 = block.getRelative(BlockFace.SOUTH).getState();
                state8.setType(Materials.AIR.getType("block"));
                state8.update(true, false);
            }
        }
        if (block.getRelative(BlockFace.WEST).getState().getData() instanceof Bed) {
            final Bed bed4 = (Bed)block.getRelative(BlockFace.WEST).getState().getData();
            if (bed4.getFacing() == BlockFace.WEST) {
                if (bed4.isHeadOfBed()) {
                    final BlockState state9 = block.getRelative(BlockFace.WEST).getState();
                    state9.setType(Materials.AIR.getType("block"));
                    state9.update(true, false);
                }
            }
            else if (bed4.getFacing() == BlockFace.WEST.getOppositeFace() && !bed4.isHeadOfBed()) {
                final BlockState state10 = block.getRelative(BlockFace.WEST).getState();
                state10.setType(Materials.AIR.getType("block"));
                state10.update(true, false);
            }
        }
    }
}
