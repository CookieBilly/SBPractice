 

package ws.billy.speedbuilderspractice.bungeecord.managers;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class KitManager
{
    public SpeedBuilders plugin;
    
    public KitManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    public String getKit(final Player player) {
        if (this.plugin.getBungeeCord().playersKit.containsKey(player.getName())) {
            return this.plugin.getBungeeCord().playersKit.get(player.getName());
        }
        return null;
    }
    
    public void setKit(final Player player, final String value) {
        this.plugin.getBungeeCord().playersKit.put(player.getName(), value);
    }
    
    public void giveKitItems(final Player player) {
        if (this.plugin.getBungeeCord().playersKit.get(player.getName()).equals("None")) {}
    }
}
