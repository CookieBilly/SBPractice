 

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_13_R1;

import org.bukkit.craftbukkit.v1_13_R1.entity.CraftGuardian;
import net.minecraft.server.v1_13_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import net.minecraft.server.v1_13_R1.DamageSource;
import net.minecraft.server.v1_13_R1.World;
import net.minecraft.server.v1_13_R1.EntityGuardianElder;
import java.util.Iterator;
import net.minecraft.server.v1_13_R1.Packet;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityMetadata;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_13_R1.DataWatcher;
import org.bukkit.entity.ArmorStand;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftElderGuardian;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.ElderGuardian;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class GuardianManager implements ws.billy.speedbuilderspractice.multiworld.managers.GuardianManager
{
    private final SpeedBuilders plugin;
    
    public GuardianManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @Override
    public void spawnGuardian(final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final Location location = new Location(Bukkit.getWorld(arena.getName()), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots.guardian.spawnpoint.x"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots.guardian.spawnpoint.y"), this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots.guardian.spawnpoint.z"));
            location.setPitch((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots.guardian.spawnpoint.pitch"));
            location.setYaw((float)this.plugin.getConfigManager().getConfig("arenas.yml").getDouble("arenas." + arena.getName() + ".plots.guardian.spawnpoint.yaw"));
            final WorldCreator worldCreator = new WorldCreator(arena.getName());
            worldCreator.generator((ChunkGenerator)new VoidGenerator());
            Bukkit.createWorld(worldCreator);
            location.getChunk().load();
            new BukkitRunnable() {
                public void run() {
                    arena.setGuardian(CustomGuardianElder.spawnGuardian(location));
                    ((ElderGuardian)arena.getGuardian()).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                    ((ElderGuardian)arena.getGuardian()).setCustomNameVisible(true);
                }
            }.runTaskLater((Plugin)this.plugin, 5L);
        }
    }
    
    @Override
    public void rotateGuardian(final float n, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final CraftElderGuardian craftElderGuardian = (CraftElderGuardian)arena.getGuardian();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftElderGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 0);
            craftElderGuardian.getHandle().f(nbtTagCompound);
            final Location location = ((ElderGuardian)arena.getGuardian()).getLocation();
            if (n == 7.5f) {
                location.setYaw(location.getYaw() + 7.5f);
            }
            else {
                location.setYaw(0.0f);
            }
            ((ElderGuardian)arena.getGuardian()).teleport(location);
            craftElderGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 1);
            craftElderGuardian.getHandle().f(nbtTagCompound);
        }
    }
    
    @Override
    public void laserGuardian(final boolean b, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final CraftElderGuardian craftElderGuardian = (CraftElderGuardian)arena.getGuardian();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftElderGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 0);
            craftElderGuardian.getHandle().f(nbtTagCompound);
            if (b) {
                arena.setJudgedPlayerArmorStand((ArmorStand)((ElderGuardian)arena.getGuardian()).getWorld().spawn(new Location(((ElderGuardian)arena.getGuardian()).getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.x"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.y"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.z")), (Class)ArmorStand.class));
                arena.getJudgedPlayerArmorStand().setGravity(false);
                arena.getJudgedPlayerArmorStand().setVisible(false);
                final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(((ElderGuardian)arena.getGuardian()).getEntityId(), (DataWatcher)this.plugin.getMultiWorld().getNMSManager().setGuardianTarget((Guardian)arena.getGuardian(), arena.getJudgedPlayerArmorStand().getEntityId()), false);
                final Iterator<String> iterator = arena.getPlayers().iterator();
                while (iterator.hasNext()) {
                    ((CraftPlayer)Bukkit.getPlayer((String)iterator.next())).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata);
                }
            }
            else {
                final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata2 = new PacketPlayOutEntityMetadata(((ElderGuardian)arena.getGuardian()).getEntityId(), (DataWatcher)this.plugin.getMultiWorld().getNMSManager().setGuardianTarget((Guardian)arena.getGuardian(), 0), false);
                final Iterator<String> iterator2 = arena.getPlayers().iterator();
                while (iterator2.hasNext()) {
                    ((CraftPlayer)Bukkit.getPlayer((String)iterator2.next())).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata2);
                }
            }
            craftElderGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 1);
            craftElderGuardian.getHandle().f(nbtTagCompound);
        }
    }
    
    private static class CustomGuardianElder extends EntityGuardianElder
    {
        public CustomGuardianElder(final World world) {
            super(world);
        }
        
        public boolean damageEntity(final DamageSource damageSource, final float n) {
            return false;
        }
        
        public static ElderGuardian spawnGuardian(final Location location) {
            final CraftWorld craftWorld = (CraftWorld)location.getWorld();
            final CustomGuardianElder customGuardianElder = new CustomGuardianElder((World)craftWorld.getHandle());
            customGuardianElder.setPosition(location.getX(), location.getY(), location.getZ());
            customGuardianElder.aP = location.getYaw();
            customGuardianElder.yaw = location.getYaw();
            customGuardianElder.pitch = location.getPitch();
            ((CraftLivingEntity)customGuardianElder.getBukkitEntity()).setRemoveWhenFarAway(false);
            craftWorld.getHandle().addEntity((Entity)customGuardianElder, CreatureSpawnEvent.SpawnReason.CUSTOM);
            removeSounds(customGuardianElder);
            removeAI(customGuardianElder);
            return (ElderGuardian)customGuardianElder.getBukkitEntity();
        }
        
        private static void removeSounds(final CustomGuardianElder customGuardianElder) {
            final CraftGuardian craftGuardian = (CraftGuardian)customGuardianElder.getBukkitEntity();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("Silent", 1);
            craftGuardian.getHandle().f(nbtTagCompound);
        }
        
        private static void removeAI(final CustomGuardianElder customGuardianElder) {
            final CraftGuardian craftGuardian = (CraftGuardian)customGuardianElder.getBukkitEntity();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 1);
            craftGuardian.getHandle().f(nbtTagCompound);
        }
    }
}
