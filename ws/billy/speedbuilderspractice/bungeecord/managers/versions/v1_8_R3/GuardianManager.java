

package ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_8_R3;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.EntityTypes;
import java.util.Map;
import java.util.ArrayList;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import java.util.Iterator;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.DataWatcher;
import org.bukkit.entity.ArmorStand;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftGuardian;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class GuardianManager implements ws.billy.speedbuilderspractice.bungeecord.managers.GuardianManager
{
    private final SpeedBuilders plugin;
    
    public GuardianManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @Override
    public void spawnGuardian() {
        final Location location = new Location(Bukkit.getWorld(this.plugin.getBungeeCord().currentMap), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.spawnpoint.x"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.spawnpoint.y"), this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.spawnpoint.z"));
        location.setPitch((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.spawnpoint.pitch"));
        location.setYaw((float)this.plugin.getConfigManager().getConfig("maps.yml").getDouble("maps." + this.plugin.getBungeeCord().currentMap + ".plots.guardian.spawnpoint.yaw"));
        final WorldCreator worldCreator = new WorldCreator(this.plugin.getBungeeCord().currentMap);
        worldCreator.generator((ChunkGenerator)new VoidGenerator());
        Bukkit.createWorld(worldCreator);
        location.getChunk().load();
        for (final Entity entity : location.getChunk().getEntities()) {
            if (entity.getType() != EntityType.PLAYER) {
                entity.remove();
            }
        }
        new BukkitRunnable() {
            public void run() {
                GuardianManager.this.registerEntity("Guardian", 68, EntityGuardian.class, CustomGuardianElder.class);
                GuardianManager.this.plugin.getBungeeCord().guardian = CustomGuardianElder.spawnGuardian(location);
                ((Guardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                ((Guardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomNameVisible(true);
                GuardianManager.this.registerEntity("Guardian", 68, EntityGuardian.class, EntityGuardian.class);
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }
    
    @Override
    public void rotateGuardian(final float n) {
        final CraftGuardian craftGuardian = (CraftGuardian)this.plugin.getBungeeCord().guardian;
        final NBTTagCompound nbtTagCompound = new NBTTagCompound();
        craftGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 0);
        craftGuardian.getHandle().f(nbtTagCompound);
        final Location location = ((Guardian)this.plugin.getBungeeCord().guardian).getLocation();
        if (n == 7.5f) {
            location.setYaw(location.getYaw() + 7.5f);
        }
        else {
            location.setYaw(0.0f);
        }
        ((Guardian)this.plugin.getBungeeCord().guardian).teleport(location);
        craftGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 1);
        craftGuardian.getHandle().f(nbtTagCompound);
    }
    
    @Override
    public void laserGuardian(final boolean b) {
        final CraftGuardian craftGuardian = (CraftGuardian)this.plugin.getBungeeCord().guardian;
        final NBTTagCompound nbtTagCompound = new NBTTagCompound();
        craftGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 0);
        craftGuardian.getHandle().f(nbtTagCompound);
        if (b) {
            (this.plugin.getBungeeCord().judgedPlayerArmorStand = (ArmorStand)((Guardian)this.plugin.getBungeeCord().guardian).getWorld().spawn(new Location(((Guardian)this.plugin.getBungeeCord().guardian).getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.x"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.y"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.z")), (Class)ArmorStand.class)).setGravity(false);
            this.plugin.getBungeeCord().judgedPlayerArmorStand.setVisible(false);
            final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(((Guardian)this.plugin.getBungeeCord().guardian).getEntityId(), (DataWatcher)this.plugin.getBungeeCord().getNMSManager().setGuardianTarget((Guardian)this.plugin.getBungeeCord().guardian, this.plugin.getBungeeCord().judgedPlayerArmorStand.getEntityId()), false);
            final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
            while (iterator.hasNext()) {
                ((CraftPlayer)iterator.next()).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata);
            }
        }
        else {
            final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata2 = new PacketPlayOutEntityMetadata(((Guardian)this.plugin.getBungeeCord().guardian).getEntityId(), (DataWatcher)this.plugin.getBungeeCord().getNMSManager().setGuardianTarget((Guardian)this.plugin.getBungeeCord().guardian, 0), false);
            final Iterator<Player> iterator2 = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                ((CraftPlayer)iterator2.next()).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata2);
            }
        }
        craftGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 1);
        craftGuardian.getHandle().f(nbtTagCompound);
    }
    
    private void registerEntity(final String s, final int i, final Class<? extends EntityInsentient> clazz, final Class<? extends EntityInsentient> clazz2) {
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
        public CustomGuardianElder(final CraftWorld craftWorld) {
            super((World)craftWorld.getHandle());
        }
        
        public boolean damageEntity(final DamageSource damageSource, final float n) {
            return false;
        }
        
        public static Guardian spawnGuardian(final Location location) {
            final CraftWorld craftWorld = (CraftWorld)location.getWorld();
            final CustomGuardianElder customGuardianElder = new CustomGuardianElder(craftWorld);
            customGuardianElder.setPosition(location.getX(), location.getY(), location.getZ());
            customGuardianElder.aK = location.getYaw();
            customGuardianElder.yaw = location.getYaw();
            customGuardianElder.pitch = location.getPitch();
            ((CraftLivingEntity)customGuardianElder.getBukkitEntity()).setRemoveWhenFarAway(false);
            craftWorld.getHandle().addEntity((net.minecraft.server.v1_8_R3.Entity)customGuardianElder, CreatureSpawnEvent.SpawnReason.CUSTOM);
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
