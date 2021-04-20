

package ws.billy.speedbuilderspractice.multiworld.managers.versions.v1_16_R1;

import java.util.Iterator;
import net.minecraft.server.v1_16_R1.Packet;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;
import org.bukkit.entity.Guardian;
import net.minecraft.server.v1_16_R1.DataWatcher;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EntityType;
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
                    arena.setGuardian(location.getWorld().spawnEntity(location, EntityType.ELDER_GUARDIAN));
                    ((ElderGuardian)arena.getGuardian()).setCustomName(ChatColor.translateAlternateColorCodes('&', Translations.translate("MAIN-GWEN_THE_GUARDIAN")));
                    ((ElderGuardian)arena.getGuardian()).setCustomNameVisible(true);
                    ((ElderGuardian)arena.getGuardian()).setAI(false);
                    ((ElderGuardian)arena.getGuardian()).setSilent(true);
                }
            }.runTaskLater((Plugin)this.plugin, 5L);
        }
    }
    
    @Override
    public void rotateGuardian(final float n, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            final Location location = ((ElderGuardian)arena.getGuardian()).getLocation();
            if (n == 7.5f) {
                location.setYaw(location.getYaw() + 7.5f);
            }
            else {
                location.setYaw(0.0f);
            }
            ((ElderGuardian)arena.getGuardian()).teleport(location);
        }
    }
    
    @Override
    public void laserGuardian(final boolean b, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
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
        }
    }
}
