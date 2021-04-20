

package ws.billy.speedbuilderspractice.utils;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.ChatColor;
import java.sql.ResultSet;
import org.bukkit.entity.Player;
import java.io.IOException;
import org.bukkit.plugin.Plugin;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.File;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import java.sql.Connection;
import ws.billy.speedbuilderspractice.SpeedBuilders;

public class StatsManager
{
    public SpeedBuilders plugin;
    public Connection connection;
    
    public StatsManager() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
        this.connection = null;
    }
    
    public void openConnection() {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("sqlite")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final File obj = new File(StatsManager.this.plugin.getDataFolder(), "stats.db");
                            if (!obj.exists()) {
                                obj.createNewFile();
                            }
                            Class.forName("org.sqlite.JDBC");
                            StatsManager.this.connection = DriverManager.getConnection("jdbc:sqlite:" + obj);
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `speedbuilders` (`uuid` varchar(36) NOT NULL, `username` varchar(16) NOT NULL, `wins` int(16) NOT NULL, `pbuilds` int(16) NOT NULL, `losses` int(16) NOT NULL, PRIMARY KEY (`uuid`));");
                            statement.close();
                            new BukkitRunnable() {
                                public void run() {
                                    try {
                                        final Statement statement = StatsManager.this.connection.createStatement();
                                        statement.executeQuery("SELECT 1 FROM `speedbuilders`");
                                        statement.close();
                                    }
                                    catch (SQLException ex) {}
                                }
                            }.runTaskTimerAsynchronously((Plugin)StatsManager.this.plugin, 600L, 600L);
                        }
                        catch (ClassNotFoundException | IOException | SQLException ex) {
                            final Throwable t;
                            t.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
            else if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("mysql")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final String string = StatsManager.this.plugin.getConfigManager().getConfig("config.yml").getString("mysql.host");
                            final int int1 = StatsManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("mysql.port");
                            final String string2 = StatsManager.this.plugin.getConfigManager().getConfig("config.yml").getString("mysql.database");
                            final String string3 = StatsManager.this.plugin.getConfigManager().getConfig("config.yml").getString("mysql.username");
                            final String string4 = StatsManager.this.plugin.getConfigManager().getConfig("config.yml").getString("mysql.password");
                            Class.forName("com.mysql.jdbc.Driver");
                            StatsManager.this.connection = DriverManager.getConnection("jdbc:mysql://" + string + ":" + int1 + "/" + string2, string3, string4);
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `speedbuilders` (`uuid` varchar(36) NOT NULL, `username` varchar(16) NOT NULL, `wins` int(16) NOT NULL, `pbuilds` int(16) NOT NULL, `losses` int(16) NOT NULL, UNIQUE KEY `uuid` (`uuid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                            statement.close();
                            new BukkitRunnable() {
                                public void run() {
                                    try {
                                        final Statement statement = StatsManager.this.connection.createStatement();
                                        statement.executeQuery("SELECT 1 FROM `speedbuilders`");
                                        statement.close();
                                    }
                                    catch (SQLException ex) {}
                                }
                            }.runTaskTimerAsynchronously((Plugin)StatsManager.this.plugin, 600L, 600L);
                        }
                        catch (ClassNotFoundException | SQLException ex) {
                            final Throwable t;
                            t.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
        }
    }
    
    public int getStats(final StatsType statsType, final Player player) {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("sqlite")) {
                try {
                    int int1 = 0;
                    final Statement statement = this.connection.createStatement();
                    final ResultSet executeQuery = statement.executeQuery("SELECT * FROM `speedbuilders` WHERE `uuid`='" + player.getUniqueId().toString() + "'");
                    while (executeQuery.next()) {
                        int1 = executeQuery.getInt(statsType.toString().toLowerCase());
                    }
                    statement.close();
                    return int1;
                }
                catch (SQLException ex) {
                    return 0;
                }
            }
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("mysql")) {
                try {
                    int int2 = 0;
                    final Statement statement2 = this.connection.createStatement();
                    final ResultSet executeQuery2 = statement2.executeQuery("SELECT * FROM `speedbuilders` WHERE `uuid`='" + player.getUniqueId().toString() + "'");
                    while (executeQuery2.next()) {
                        int2 = executeQuery2.getInt(statsType.toString().toLowerCase());
                    }
                    statement2.close();
                    return int2;
                }
                catch (SQLException ex2) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    public void showStats(final Player player, final Player player2) {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("sqlite")) {
                final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 9, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8" + Translations.translate("STATS")));
                final ItemStack itemStack = new ItemStack(Material.DIAMOND);
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-WINS") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore = new ArrayList<String>();
                lore.add("");
                itemMeta.setLore((List)lore);
                itemStack.setItemMeta(itemMeta);
                final ItemStack itemStack2 = new ItemStack(Material.BRICK);
                final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-PBUILDS") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore2 = new ArrayList<String>();
                lore2.add("");
                itemMeta2.setLore((List)lore2);
                itemStack2.setItemMeta(itemMeta2);
                final ItemStack itemStack3 = new ItemStack(Material.COAL);
                final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-LOSSES") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore3 = new ArrayList<String>();
                lore3.add("");
                itemMeta3.setLore((List)lore3);
                itemStack3.setItemMeta(itemMeta3);
                inventory.setItem(1, itemStack);
                inventory.setItem(4, itemStack2);
                inventory.setItem(7, itemStack3);
                new BukkitRunnable() {
                    public void run() {
                        final ItemStack itemStack = new ItemStack(Material.DIAMOND);
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-WINS") + StatsManager.this.getStats(StatsType.WINS, player2)));
                        final ArrayList<String> lore = new ArrayList<String>();
                        lore.add("");
                        itemMeta.setLore((List)lore);
                        itemStack.setItemMeta(itemMeta);
                        final ItemStack itemStack2 = new ItemStack(Material.BRICK);
                        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                        itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-PBUILDS") + StatsManager.this.getStats(StatsType.PBUILDS, player2)));
                        final ArrayList<String> lore2 = new ArrayList<String>();
                        lore2.add("");
                        itemMeta2.setLore((List)lore2);
                        itemStack2.setItemMeta(itemMeta2);
                        final ItemStack itemStack3 = new ItemStack(Material.COAL);
                        final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                        itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-LOSSES") + StatsManager.this.getStats(StatsType.LOSSES, player2)));
                        final ArrayList<String> lore3 = new ArrayList<String>();
                        lore3.add("");
                        itemMeta3.setLore((List)lore3);
                        itemStack3.setItemMeta(itemMeta3);
                        inventory.setItem(1, itemStack);
                        inventory.setItem(4, itemStack2);
                        inventory.setItem(7, itemStack3);
                        player.updateInventory();
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.openInventory(inventory);
            }
            else if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("mysql")) {
                final Inventory inventory2 = Bukkit.createInventory((InventoryHolder)player, 9, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8" + Translations.translate("STATS")));
                final ItemStack itemStack4 = new ItemStack(Material.DIAMOND);
                final ItemMeta itemMeta4 = itemStack4.getItemMeta();
                itemMeta4.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-WINS") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore4 = new ArrayList<String>();
                lore4.add("");
                itemMeta4.setLore((List)lore4);
                itemStack4.setItemMeta(itemMeta4);
                final ItemStack itemStack5 = new ItemStack(Material.BRICK);
                final ItemMeta itemMeta5 = itemStack5.getItemMeta();
                itemMeta5.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-PBUILDS") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore5 = new ArrayList<String>();
                lore5.add("");
                itemMeta5.setLore((List)lore5);
                itemStack5.setItemMeta(itemMeta5);
                final ItemStack itemStack6 = new ItemStack(Material.COAL);
                final ItemMeta itemMeta6 = itemStack6.getItemMeta();
                itemMeta6.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-LOSSES") + Translations.translate("STATS-LOADING")));
                final ArrayList<String> lore6 = new ArrayList<String>();
                lore6.add("");
                itemMeta6.setLore((List)lore6);
                itemStack6.setItemMeta(itemMeta6);
                inventory2.setItem(1, itemStack4);
                inventory2.setItem(4, itemStack5);
                inventory2.setItem(7, itemStack6);
                new BukkitRunnable() {
                    public void run() {
                        final ItemStack itemStack = new ItemStack(Material.DIAMOND);
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-WINS") + StatsManager.this.getStats(StatsType.WINS, player2)));
                        final ArrayList<String> lore = new ArrayList<String>();
                        lore.add("");
                        itemMeta.setLore((List)lore);
                        itemStack.setItemMeta(itemMeta);
                        final ItemStack itemStack2 = new ItemStack(Material.BRICK);
                        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                        itemMeta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-PBUILDS") + StatsManager.this.getStats(StatsType.PBUILDS, player2)));
                        final ArrayList<String> lore2 = new ArrayList<String>();
                        lore2.add("");
                        itemMeta2.setLore((List)lore2);
                        itemStack2.setItemMeta(itemMeta2);
                        final ItemStack itemStack3 = new ItemStack(Material.COAL);
                        final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                        itemMeta3.setDisplayName(ChatColor.translateAlternateColorCodes('&', Translations.translate("STATS-LOSSES") + StatsManager.this.getStats(StatsType.LOSSES, player2)));
                        final ArrayList<String> lore3 = new ArrayList<String>();
                        lore3.add("");
                        itemMeta3.setLore((List)lore3);
                        itemStack3.setItemMeta(itemMeta3);
                        inventory2.setItem(1, itemStack);
                        inventory2.setItem(4, itemStack2);
                        inventory2.setItem(7, itemStack3);
                        player.updateInventory();
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
                player.openInventory(inventory2);
            }
        }
    }
    
    public void setValue(final StatsType statsType, final Player player, final int n) {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("sqlite")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("INSERT OR IGNORE INTO `speedbuilders` (`uuid`, `username`, `wins`, `pbuilds`, `losses`) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + StatsManager.this.getStats(StatsType.WINS, player) + "', '" + StatsManager.this.getStats(StatsType.PBUILDS, player) + "', '" + StatsManager.this.getStats(StatsType.LOSSES, player) + "');");
                            statement.executeUpdate("UPDATE `speedbuilders` SET `" + statsType.toString().toLowerCase() + "` = '" + n + "' WHERE `uuid` ='" + player.getUniqueId().toString() + "';");
                            statement.close();
                        }
                        catch (SQLException ex) {}
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
            else if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("mysql")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("INSERT IGNORE INTO `speedbuilders` (`uuid`, `username`, `wins`, `pbuilds`, `losses`) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + StatsManager.this.getStats(StatsType.WINS, player) + "', '" + StatsManager.this.getStats(StatsType.PBUILDS, player) + "', '" + StatsManager.this.getStats(StatsType.LOSSES, player) + "');");
                            statement.executeUpdate("UPDATE `speedbuilders` SET `" + statsType.toString().toLowerCase() + "` = '" + n + "' WHERE `uuid` ='" + player.getUniqueId().toString() + "';");
                            statement.close();
                        }
                        catch (SQLException ex) {}
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
        }
    }
    
    public void incrementStat(final StatsType statsType, final Player player, final int n) {
        if (this.plugin.getConfigManager().getConfig("config.yml").getBoolean("stats.enabled")) {
            if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("sqlite")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("INSERT OR IGNORE INTO `speedbuilders` (`uuid`, `username`, `wins`, `pbuilds`, `losses`) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + StatsManager.this.getStats(StatsType.WINS, player) + "', '" + StatsManager.this.getStats(StatsType.PBUILDS, player) + "', '" + StatsManager.this.getStats(StatsType.LOSSES, player) + "');");
                            statement.executeUpdate("UPDATE `speedbuilders` SET `" + statsType.toString().toLowerCase() + "` = '" + (StatsManager.this.getStats(statsType, player) + n) + "' WHERE `uuid` ='" + player.getUniqueId().toString() + "';");
                            statement.close();
                        }
                        catch (SQLException ex) {}
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
            else if (this.plugin.getConfigManager().getConfig("config.yml").getString("stats.system-type").equalsIgnoreCase("mysql")) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            final Statement statement = StatsManager.this.connection.createStatement();
                            statement.executeUpdate("INSERT IGNORE INTO `speedbuilders` (`uuid`, `username`, `wins`, `pbuilds`, `losses`) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + StatsManager.this.getStats(StatsType.WINS, player) + "', '" + StatsManager.this.getStats(StatsType.PBUILDS, player) + "', '" + StatsManager.this.getStats(StatsType.LOSSES, player) + "');");
                            statement.executeUpdate("UPDATE `speedbuilders` SET `" + statsType.toString().toLowerCase() + "` = '" + (StatsManager.this.getStats(statsType, player) + n) + "' WHERE `uuid` ='" + player.getUniqueId().toString() + "';");
                            statement.close();
                        }
                        catch (SQLException ex) {}
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
}
