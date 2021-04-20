 

package ws.billy.speedbuilderspractice.multiworld.listeners.versions.v1_9_R1;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.Effect;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class WorldListener implements Listener
{
    private SpeedBuilders plugin;
    
    public WorldListener() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockCanBuild(final BlockCanBuildEvent blockCanBuildEvent) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(blockCanBuildEvent.getBlock().getWorld().getName());
        if (arena != null && arena.getGameState() == GameState.BUILDING) {
            final Location add = blockCanBuildEvent.getBlock().getLocation().add(0.5, 1.0, 0.5);
            for (final Entity entity : add.getWorld().getNearbyEntities(add, 0.5, 1.0, 0.5)) {
                if (entity instanceof Player && !arena.getPlots().containsKey(entity.getName())) {
                    blockCanBuildEvent.setBuildable(true);
                    break;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(final CreatureSpawnEvent creatureSpawnEvent) {
        if (this.plugin.getMultiWorld().getArenaManager().getArena(creatureSpawnEvent.getEntity().getWorld().getName()) != null && creatureSpawnEvent.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            creatureSpawnEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlock(final EntityChangeBlockEvent entityChangeBlockEvent) {
        if (this.plugin.getMultiWorld().getArenaManager().getArena(entityChangeBlockEvent.getEntity().getWorld().getName()) != null && entityChangeBlockEvent.getEntityType() == EntityType.FALLING_BLOCK) {
            entityChangeBlockEvent.getEntity().getWorld().playEffect(entityChangeBlockEvent.getEntity().getLocation(), Effect.STEP_SOUND, (Object)entityChangeBlockEvent.getTo());
            entityChangeBlockEvent.getEntity().remove();
            entityChangeBlockEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(final BlockPhysicsEvent blockPhysicsEvent) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(blockPhysicsEvent.getBlock().getWorld().getName());
        if (arena != null && (arena.getGameState() == GameState.GAME_STARTING || arena.getGameState() == GameState.SHOWCASING || arena.getGameState() == GameState.BUILDING || arena.getGameState() == GameState.JUDGING)) {
            blockPhysicsEvent.setCancelled(true);
        }
    }
}
