 

package ws.billy.speedbuilderspractice.utils;

import java.util.ConcurrentModificationException;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.TreeMap;
import org.bukkit.Bukkit;
import java.util.Map;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class ConfigManager
{
    private SpeedBuilders plugin;
    private Map<String, Configuration> configurations;
    
    public ConfigManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
        this.configurations = new TreeMap<String, Configuration>(String.CASE_INSENSITIVE_ORDER);
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }
    }
    
    public YamlConfiguration getConfig(final String s) {
        return this.configurations.get(s).get();
    }
    
    public void loadConfig(final String s) {
        this.loadConfig(s, this.plugin.getDataFolder());
    }
    
    public void loadConfig(final String child, final File parent) {
        final File file = new File(parent, child);
        if (!file.exists()) {
            try {
                file.createNewFile();
                final InputStream resource = this.plugin.getResource(child);
                if (resource != null) {
                    final FileOutputStream fileOutputStream = new FileOutputStream(file);
                    final byte[] array = new byte[1024];
                    int read;
                    while ((read = resource.read(array)) > 0) {
                        fileOutputStream.write(array, 0, read);
                    }
                    fileOutputStream.close();
                    resource.close();
                }
            }
            catch (IOException ex) {}
        }
        final Configuration configuration = new Configuration(file);
        configuration.load();
        this.configurations.put(child, configuration);
    }
    
    public void reloadConfig(final String s) {
        this.configurations.get(s).load();
    }
    
    public void saveConfig(final String s) {
        this.configurations.get(s).save();
    }
    
    private static class Configuration
    {
        private File file;
        private YamlConfiguration yamlConfiguration;
        
        public Configuration(final File file) {
            this.file = file;
            this.yamlConfiguration = new YamlConfiguration();
        }
        
        public YamlConfiguration get() {
            return this.yamlConfiguration;
        }
        
        public void load() {
            try {
                this.yamlConfiguration.load(this.file);
            }
            catch (InvalidConfigurationException ex) {}
            catch (IOException ex2) {}
        }
        
        public void save() {
            try {
                this.yamlConfiguration.save(this.file);
            }
            catch (ConcurrentModificationException ex) {}
            catch (IOException ex2) {}
            catch (NullPointerException ex3) {}
        }
    }
}
