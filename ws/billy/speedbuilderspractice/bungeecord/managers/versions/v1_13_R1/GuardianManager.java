 

package ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_13_R1;

import org.bukkit.craftbukkit.v1_13_R1.entity.CraftGuardian;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import net.minecraft.server.v1_13_R1.DamageSource;
import net.minecraft.server.v1_13_R1.World;
import net.minecraft.server.v1_13_R1.EntityGuardianElder;
import java.util.Iterator;
import net.minecraft.server.v1_13_R1.Packet;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityMetadata;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_13_R1.DataWatcher;
import org.bukkit.entity.ArmorStand;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftElderGuardian;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.ElderGuardian;
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
                GuardianManager.this.plugin.getBungeeCord().guardian = CustomGuardianElder.spawnGuardian(location);
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomNameVisible(true);
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }
    
    @Override
    public void rotateGuardian(final float n) {
        final CraftElderGuardian craftElderGuardian = (CraftElderGuardian)this.plugin.getBungeeCord().guardian;
        final NBTTagCompound nbtTagCompound = new NBTTagCompound();
        craftElderGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 0);
        craftElderGuardian.getHandle().f(nbtTagCompound);
        final Location location = ((ElderGuardian)this.plugin.getBungeeCord().guardian).getLocation();
        if (n == 7.5f) {
            location.setYaw(location.getYaw() + 7.5f);
        }
        else {
            location.setYaw(0.0f);
        }
        ((ElderGuardian)this.plugin.getBungeeCord().guardian).teleport(location);
        craftElderGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 1);
        craftElderGuardian.getHandle().f(nbtTagCompound);
    }
    
    @Override
    public void laserGuardian(final boolean b) {
        final CraftElderGuardian craftElderGuardian = (CraftElderGuardian)this.plugin.getBungeeCord().guardian;
        final NBTTagCompound nbtTagCompound = new NBTTagCompound();
        craftElderGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 0);
        craftElderGuardian.getHandle().f(nbtTagCompound);
        if (b) {
            (this.plugin.getBungeeCord().judgedPlayerArmorStand = (ArmorStand)((ElderGuardian)this.plugin.getBungeeCord().guardian).getWorld().spawn(new Location(((ElderGuardian)this.plugin.getBungeeCord().guardian).getWorld(), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.x"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.y"), (double)this.plugin.getConfigManager().getConfig("maps.yml").getInt("maps." + this.plugin.getBungeeCord().currentMap + ".plots." + this.plugin.getBungeeCord().plots.get(this.plugin.getBungeeCord().judgedPlayerName) + ".laser-beam.z")), (Class)ArmorStand.class)).setGravity(false);
            this.plugin.getBungeeCord().judgedPlayerArmorStand.setVisible(false);
            final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(((ElderGuardian)this.plugin.getBungeeCord().guardian).getEntityId(), (DataWatcher)this.plugin.getBungeeCord().getNMSManager().setGuardianTarget((Guardian)this.plugin.getBungeeCord().guardian, this.plugin.getBungeeCord().judgedPlayerArmorStand.getEntityId()), false);
            final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
            while (iterator.hasNext()) {
                ((CraftPlayer)iterator.next()).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata);
            }
        }
        else {
            final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata2 = new PacketPlayOutEntityMetadata(((ElderGuardian)this.plugin.getBungeeCord().guardian).getEntityId(), (DataWatcher)this.plugin.getBungeeCord().getNMSManager().setGuardianTarget((Guardian)this.plugin.getBungeeCord().guardian, 0), false);
            final Iterator<Player> iterator2 = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                ((CraftPlayer)iterator2.next()).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityMetadata2);
            }
        }
        craftElderGuardian.getHandle().c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 1);
        craftElderGuardian.getHandle().f(nbtTagCompound);
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
            craftWorld.getHandle().addEntity((net.minecraft.server.v1_13_R1.Entity)customGuardianElder, CreatureSpawnEvent.SpawnReason.CUSTOM);
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
