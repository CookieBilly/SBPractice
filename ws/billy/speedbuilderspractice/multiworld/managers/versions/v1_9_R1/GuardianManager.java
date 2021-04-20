 

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_9_R1;

import net.minecraft.server.v1_9_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.World;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import net.minecraft.server.v1_9_R1.EntityTypes;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.server.v1_9_R1.Packet;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_9_R1.DataWatcher;
import org.bukkit.entity.ArmorStand;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftGuardian;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityGuardian;
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
                    GuardianManager.this.registerEntity("Guardian", 68, (Class<? extends EntityInsentient>)EntityGuardian.class, (Class<? extends EntityInsentient>)CustomGuardianElder.class);
                    arena.setGuardian(CustomGuardianElder.spawnGuardian(location));
                    ((Guardian)arena.getGuardian()).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                    ((Guardian)arena.getGuardian()).setCustomNameVisible(true);
                    GuardianManager.this.registerEntity("Guardian", 68, (Class<? extends EntityInsentient>)EntityGuardian.class, (Class<? extends EntityInsentient>)EntityGuardian.class);
                }
            }.runTaskLater((Plugin)this.plugin, 5L);
        }
    }
    
    @Override
    public void rotateGuardian(final float n, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final CraftGuardian craftGuardian = (CraftGuardian)arena.getGuardian();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 0);
            craftGuardian.getHandle().f(nbtTagCompound);
            final Location location = ((Guardian)arena.getGuardian()).getLocation();
            if (n == 7.5f) {
                location.setYaw(location.getYaw() + 7.5f);
            }
            else {
                location.setYaw(0.0f);
            }
            ((Guardian)arena.getGuardian()).teleport(location);
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 1);
            craftGuardian.getHandle().f(nbtTagCompound);
        }
    }
    
    @Override
    public void laserGuardian(final boolean b, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final CraftGuardian craftGuardian = (CraftGuardian)arena.getGuardian();
            final NBTTagCompound nbtTagCompound = new NBTTagCompound();
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 0);
            craftGuardian.getHandle().f(nbtTagCompound);
            if (b) {
                arena.setJudgedPlayerArmorStand((ArmorStand)((Guardian)arena.getGuardian()).getWorld().spawn(new Location(((Guardian)arena.getGuardian()).getWorld(), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.x"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.y"), (double)this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + arena.getName() + ".plots." + arena.getPlots().get(arena.getJudgedPlayerName()) + ".laser-beam.z")), (Class)ArmorStand.class));
                arena.getJudgedPlayerArmorStand().setGravity(false);
                arena.getJudgedPlayerArmorStand().setVisible(false);
                final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(((Guardian)arena.getGuardian()).getEntityId(), (DataWatcher)this.plugin.getMultiWorld().getNMSManager().setGuardianTarget((Guardian)arena.getGuardian(), arena.getJudgedPlayerArmorStand().getEntityId()), false);
                final Iterator<String> iterator = arena.getPlayers().iterator();
                while (iterator.hasNext()) {
                    ((CraftPlayer)Bukkit.getPlayer((String)iterator.next())).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata);
                }
            }
            else {
                final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata2 = new PacketPlayOutEntityMetadata(((Guardian)arena.getGuardian()).getEntityId(), (DataWatcher)this.plugin.getMultiWorld().getNMSManager().setGuardianTarget((Guardian)arena.getGuardian(), 0), false);
                final Iterator<String> iterator2 = arena.getPlayers().iterator();
                while (iterator2.hasNext()) {
                    ((CraftPlayer)Bukkit.getPlayer((String)iterator2.next())).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata2);
                }
            }
            craftGuardian.getHandle().c(nbtTagCompound);
            nbtTagCompound.setInt("NoAI", 1);
            craftGuardian.getHandle().f(nbtTagCompound);
        }
    }
    
    public void registerEntity(final String s, final int i, final Class<? extends EntityInsentient> clazz, final Class<? extends EntityInsentient> clazz2) {
        try {
            final ArrayList<Map> list = new ArrayList<Map>();
            for (final Field field : EntityTypes.class.getDeclaredFields()) {
                if (field.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    field.setAccessible(true);
                    list.add((Map)field.get(null));
                }
            }
            if (((Map)list.get(2)).containsKey(i)) {
                ((Map)list.get(0)).remove(s);
                ((Map)list.get(2)).remove(i);
            }
            final Method declaredMethod = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(null, clazz2, s, i);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static class CustomGuardianElder extends EntityGuardian
    {
        public CustomGuardianElder(final World world) {
            super(world);
        }
        
        public boolean damageEntity(final DamageSource damageSource, final float n) {
            return false;
        }
        
        public static Guardian spawnGuardian(final Location location) {
            final CraftWorld craftWorld = (CraftWorld)location.getWorld();
            final CustomGuardianElder customGuardianElder = new CustomGuardianElder((World)craftWorld.getHandle());
            customGuardianElder.setPosition(location.getX(), location.getY(), location.getZ());
            customGuardianElder.aO = location.getYaw();
            customGuardianElder.yaw = location.getYaw();
            customGuardianElder.pitch = location.getPitch();
            ((CraftLivingEntity)customGuardianElder.getBukkitEntity()).setRemoveWhenFarAway(false);
            craftWorld.getHandle().addEntity((Entity)customGuardianElder, CreatureSpawnEvent.SpawnReason.CUSTOM);
            removeSounds(customGuardianElder);
            removeAI(customGuardianElder);
            ((CraftGuardian)customGuardianElder.getBukkitEntity()).setElder(true);
            return (Guardian)customGuardianElder.getBukkitEntity();
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
