

package ws.billy.speedbuilderspractice.multiworld.managers;

import ws.billy.speedbuilderspractice.multiworld.Arena;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class KitManager
{
    private SpeedBuilders plugin;
    
    public KitManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public String getKit(final Player player, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null && arena.getPlayersKit().containsKey(player.getName())) {
            return arena.getPlayersKit().get(player.getName());
        }
        return null;
    }
    
    public void setKit(final Player player, final String value, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena != null) {
            arena.getPlayersKit().put(player.getName(), value);
        }
    }
    
    public void giveKitItems(final Player player, final String s) {
        final Arena arena = this.plugin.getMultiWorld().getArenaManager().getArena(s);
        if (arena == null || arena.getPlayersKit().get(player.getName()).equals("None")) {}
    }
}
