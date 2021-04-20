

package ws.billy.speedbuilderspractice;

import java.sql.SQLException;
import org.bukkit.plugin.Plugin;
import ws.billy.speedbuilderspractice.utils.bstats.Metrics;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.utils.StatsManager;
import ws.billy.speedbuilderspractice.utils.ConfigManager;
import ws.billy.speedbuilderspractice.multiworld.MultiWorld;
import ws.billy.speedbuilderspractice.bungeecord.BungeeCord;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedBuilders extends JavaPlugin
{
    public String serverVersion;
    public BungeeCord bungeeCord;
    public MultiWorld multiWorld;
    public ConfigManager configManager;
    public StatsManager statsManager;
    
    public SpeedBuilders() {
        this.serverVersion = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
    }
    
    public void onEnable() {
        loadConfig0();
        Bukkit.getLogger().info("[SpeedBuilders] SpeedBuilders v" + this.getDescription().getVersion() + " is enabled!");
        new Metrics((Plugin)this, 8034);
        this.configManager = new ConfigManager();
        this.statsManager = new StatsManager();
        this.configManager.loadConfig("config.yml");
        if (this.configManager.getConfig("config.yml").getString("plugin.version").equalsIgnoreCase("bungeecord")) {
            (this.bungeeCord = new BungeeCord()).onEnable();
        }
        else if (this.configManager.getConfig("config.yml").getString("plugin.version").equalsIgnoreCase("multiworld")) {
            (this.multiWorld = new MultiWorld()).onEnable();
        }
    }
    
    public void onDisable() {
        if (this.bungeeCord != null || this.multiWorld != null) {
            if (this.configManager.getConfig("config.yml").getString("plugin.version").equalsIgnoreCase("bungeecord")) {
                this.bungeeCord.onDisable();
            }
            else if (this.configManager.getConfig("config.yml").getString("plugin.version").equalsIgnoreCase("multiworld")) {
                this.multiWorld.onDisable();
            }
            if (this.statsManager.getConnection() != null) {
                try {
                    this.statsManager.getConnection().close();
                }
                catch (SQLException ex) {}
            }
        }
        Bukkit.getLogger().info("[SpeedBuilders] SpeedBuilders v" + this.getDescription().getVersion() + " is disabled!");
    }
    
    public BungeeCord getBungeeCord() {
        return this.bungeeCord;
    }
    
    public MultiWorld getMultiWorld() {
        return this.multiWorld;
    }
    
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
    
    public StatsManager getStatsManager() {
        return this.statsManager;
    }
}
