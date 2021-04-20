 

package ws.billy.speedbuilderspractice.bungeecord.listeners;

import ws.billy.speedbuilderspractice.utils.GameState;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class ServerListener implements Listener
{
    private SpeedBuilders plugin;
    
    public ServerListener() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(final ServerListPingEvent serverListPingEvent) {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("bungeecord.custom-motd.enabled")) {
            serverListPingEvent.setMotd(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfig("config.yml").getString("bungeecord.custom-motd.message").replaceAll("%GAMESTATE%", Translations.translate("GAMESTATE-" + this.plugin.getBungeeCord().gameState.toString()))));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent playerLoginEvent) {
        if (Bukkit.getOnlinePlayers().size() + 1 > this.plugin.getBungeeCord().maxPlayers) {
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.translateAlternateColorCodes('&', Translations.translate("KICK-THE_SERVER_IS_FULL")));
        }
        else if (this.plugin.getBungeeCord().gameState != GameState.WAITING && this.plugin.getBungeeCord().gameState != GameState.STARTING) {
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', Translations.translate("KICK-GAME_RUNNING")));
        }
    }
}
