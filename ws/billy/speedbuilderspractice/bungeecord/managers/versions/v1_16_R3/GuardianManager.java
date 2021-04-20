 

package ws.billy.speedbuilderspractice.bungeecord.managers.versions.v1_16_R3;

import java.util.Iterator;
import net.minecraft.server.v1_16_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_16_R3.DataWatcher;
import org.bukkit.entity.ArmorStand;
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
                GuardianManager.this.plugin.getBungeeCord().guardian = location.getWorld().spawnEntity(location, EntityType.ELDER_GUARDIAN);
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setCustomNameVisible(true);
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setAI(false);
                ((ElderGuardian)GuardianManager.this.plugin.getBungeeCord().guardian).setSilent(true);
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }
    
    @Override
    public void rotateGuardian(final float n) {
        final Location location = ((ElderGuardian)this.plugin.getBungeeCord().guardian).getLocation();
        if (n == 7.5f) {
            location.setYaw(location.getYaw() + 7.5f);
        }
        else {
            location.setYaw(0.0f);
        }
        ((ElderGuardian)this.plugin.getBungeeCord().guardian).teleport(location);
    }
    
    @Override
    public void laserGuardian(final boolean b) {
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
    }
}
