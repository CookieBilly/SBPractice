 

package ws.billy.speedbuilderspractice.multiworld.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.generator.ChunkGenerator;
import ws.billy.speedbuilderspractice.utils.VoidGenerator;
import org.bukkit.WorldCreator;
import java.util.Arrays;
import java.io.File;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.inventory.InventoryType;
import ws.billy.speedbuilderspractice.utils.Materials;
import org.bukkit.ChatColor;
import ws.billy.speedbuilderspractice.utils.Translations;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Bukkit;
import ws.billy.speedbuilderspractice.SpeedBuilders;
import org.bukkit.event.Listener;

public class SetupListener1 implements Listener
{
    private SpeedBuilders plugin;
    
    public SetupListener1() {
        this.plugin = (SpeedBuilders)Bukkit.getPluginManager().getPlugin("SpeedBuilders");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent inventoryClickEvent) {
        final Player player = (Player)inventoryClickEvent.getWhoClicked();
        final InventoryView view = inventoryClickEvent.getView();
        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();
        if (!view.getTitle().equals(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"))) {
            return;
        }
        if (currentItem == null) {
            return;
        }
        if (currentItem.getType() == Materials.PODZOL.getType("item")) {
            inventoryClickEvent.setCancelled(true);
            if (!this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7You have to set spawnpoint location for main lobby!"));
                return;
            }
            final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, InventoryType.CHEST, ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-INVENTORY") + "&8Setup"));
            int n = 0;
            for (final String displayName : this.plugin.getConfigManager().getConfig("arenas.yml").getConfigurationSection("arenas").getKeys(false)) {
                final ItemStack itemStack = Materials.GRASS_BLOCK.getItemStack(1);
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemStack.setItemMeta(itemMeta);
                inventory.setItem(n, itemStack);
                ++n;
            }
            player.openInventory(inventory);
        }
        else if (currentItem.getType() == Materials.GRASS_BLOCK.getType("item")) {
            inventoryClickEvent.setCancelled(true);
            if (!Arrays.asList(new File(this.plugin.getDataFolder() + "/../../").list((parent, child) -> new File(parent, child).isDirectory())).contains(currentItem.getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7World folder can not be found for that arena!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Shut down the server and upload the world to this server!"));
                return;
            }
            if (this.plugin.getMultiWorld().setup.containsValue(currentItem.getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7That arena is already being set up!"));
                return;
            }
            final String displayName2 = currentItem.getItemMeta().getDisplayName();
            final WorldCreator worldCreator = new WorldCreator(displayName2);
            worldCreator.generator((ChunkGenerator)new VoidGenerator());
            Bukkit.createWorld(worldCreator);
            for (final Entity entity : Bukkit.getWorld(displayName2).getEntities()) {
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                }
            }
            final Location spawnLocation = Bukkit.getWorld(displayName2).getSpawnLocation();
            spawnLocation.setY((double)spawnLocation.getWorld().getHighestBlockYAt(spawnLocation));
            player.teleport(spawnLocation);
            this.plugin.getMultiWorld().getTemplateManager().resetPlots(displayName2);
            player.getInventory().clear();
            player.setAllowFlight(true);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(true);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.CREATIVE);
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator3 = player.getActivePotionEffects().iterator();
            while (iterator3.hasNext()) {
                player.removePotionEffect(iterator3.next().getType());
            }
            player.updateInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup &7again to start setting up your arena."));
            this.plugin.getMultiWorld().setup.put(player.getName(), currentItem.getItemMeta().getDisplayName());
        }
        else if (currentItem.getType() == Materials.NETHER_STAR.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack2 = Materials.NETHER_STAR.getItemStack(1);
            final ItemMeta itemMeta2 = itemStack2.getItemMeta();
            itemMeta2.setDisplayName("Set spawnpoint location for main lobby");
            final ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to set spawnpoint location."));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cIt sets the spawnpoint location's &fY-coordinate"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&c1 block &fhigher&c than the clicked block!"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta2.setLore((List)lore);
            itemStack2.setItemMeta(itemMeta2);
            player.getInventory().addItem(new ItemStack[] { itemStack2 });
        }
        else if (currentItem.getType() == Materials.BEACON.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack3 = Materials.BEACON.getItemStack(1);
            final ItemMeta itemMeta3 = itemStack3.getItemMeta();
            itemMeta3.setDisplayName("Set spawnpoint location for arena lobby");
            final ArrayList<String> lore2 = new ArrayList<String>();
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to set spawnpoint location."));
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&cIt sets the spawnpoint location's &fY-coordinate"));
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&c1 block &fhigher&c than the clicked block!"));
            lore2.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta3.setLore((List)lore2);
            itemStack3.setItemMeta(itemMeta3);
            player.getInventory().addItem(new ItemStack[] { itemStack3 });
        }
        else if (currentItem.getType() == Materials.GUARDIAN_SPAWN_EGG.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack4 = Materials.GUARDIAN_SPAWN_EGG.getItemStack(1);
            final ItemMeta itemMeta4 = itemStack4.getItemMeta();
            itemMeta4.setDisplayName("Set spawnpoint location for guardian plot");
            final ArrayList<String> lore3 = new ArrayList<String>();
            lore3.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to set spawnpoint location."));
            lore3.add(ChatColor.translateAlternateColorCodes('&', "&cIt sets the spawnpoint location's &fY-coordinate"));
            lore3.add(ChatColor.translateAlternateColorCodes('&', "&c10 blocks &fhigher&c than the clicked block!"));
            lore3.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta4.setLore((List)lore3);
            itemStack4.setItemMeta(itemMeta4);
            player.getInventory().addItem(new ItemStack[] { itemStack4 });
        }
        else if (currentItem.getType() == Materials.PAPER.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack5 = Materials.PAPER.getItemStack(1);
            final ItemMeta itemMeta5 = itemStack5.getItemMeta();
            itemMeta5.setDisplayName("Set template area positions for guardian plot");
            final ArrayList<String> lore4 = new ArrayList<String>();
            lore4.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to select corner 1 and"));
            lore4.add(ChatColor.translateAlternateColorCodes('&', "&5right-click to select corner 2."));
            lore4.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta5.setLore((List)lore4);
            itemStack5.setItemMeta(itemMeta5);
            player.getInventory().addItem(new ItemStack[] { itemStack5 });
        }
        else if (currentItem.getType() == Materials.WHITE_BED.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack6 = Materials.WHITE_BED.getItemStack(1);
            final ItemMeta itemMeta6 = itemStack6.getItemMeta();
            itemMeta6.setDisplayName("Set spawnpoint location for each player plot");
            final ArrayList<String> lore5 = new ArrayList<String>();
            lore5.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to set spawnpoint location."));
            lore5.add(ChatColor.translateAlternateColorCodes('&', "&cIt sets the spawnpoint location's &fY-coordinate"));
            lore5.add(ChatColor.translateAlternateColorCodes('&', "&c1 block &fhigher&c than the clicked block!"));
            lore5.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta6.setLore((List)lore5);
            itemStack6.setItemMeta(itemMeta6);
            player.getInventory().addItem(new ItemStack[] { itemStack6 });
        }
        else if (currentItem.getType() == Materials.WOODEN_AXE.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack7 = Materials.WOODEN_AXE.getItemStack(1);
            final ItemMeta itemMeta7 = itemStack7.getItemMeta();
            itemMeta7.setDisplayName("Set plot area positions for each player plot");
            final ArrayList<String> lore6 = new ArrayList<String>();
            lore6.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to select corner 1 and"));
            lore6.add(ChatColor.translateAlternateColorCodes('&', "&5right-click to select corner 2."));
            lore6.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta7.setLore((List)lore6);
            itemStack7.setItemMeta(itemMeta7);
            player.getInventory().addItem(new ItemStack[] { itemStack7 });
        }
        else if (currentItem.getType() == Materials.ARMOR_STAND.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack8 = Materials.ARMOR_STAND.getItemStack(1);
            final ItemMeta itemMeta8 = itemStack8.getItemMeta();
            itemMeta8.setDisplayName("Set laser location for each player plot");
            final ArrayList<String> lore7 = new ArrayList<String>();
            lore7.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to set laser location."));
            lore7.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta8.setLore((List)lore7);
            itemStack8.setItemMeta(itemMeta8);
            player.getInventory().addItem(new ItemStack[] { itemStack8 });
        }
        else if (currentItem.getType() == Materials.OAK_FENCE.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final ItemStack itemStack9 = Materials.OAK_FENCE.getItemStack(1);
            final ItemMeta itemMeta9 = itemStack9.getItemMeta();
            itemMeta9.setDisplayName("Set build area positions for each player plot");
            final ArrayList<String> lore8 = new ArrayList<String>();
            lore8.add(ChatColor.translateAlternateColorCodes('&', "&5Left-click to select corner 1 and"));
            lore8.add(ChatColor.translateAlternateColorCodes('&', "&5right-click to select corner 2."));
            lore8.add(ChatColor.translateAlternateColorCodes('&', "&3Setup"));
            itemMeta9.setLore((List)lore8);
            itemStack9.setItemMeta(itemMeta9);
            player.getInventory().addItem(new ItemStack[] { itemStack9 });
        }
        else if (currentItem.getType() == Materials.WRITABLE_BOOK.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            final String str = this.plugin.getMultiWorld().setup.get(player.getName());
            if (!this.plugin.getConfigManager().getConfig("arenas.yml").contains("arenas." + str + ".plots.guardian.template-area")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Location for at least one of the template area positions does not exist."));
                return;
            }
            final ArrayList<Block> value = new ArrayList<Block>();
            final int min = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.x2"));
            final int min2 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.y2"));
            final int min3 = Math.min(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.z2"));
            final int max = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.x1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.x2"));
            final int max2 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.y1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.y2"));
            final int max3 = Math.max(this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.z1"), this.plugin.getConfigManager().getConfig("arenas.yml").getInt("arenas." + str + ".plots.guardian.template-area.z2"));
            for (int i = min; i <= max; ++i) {
                for (int j = min2; j <= max2; ++j) {
                    for (int k = min3; k <= max3; ++k) {
                        value.add(Bukkit.getWorld(str).getBlockAt(i, j, k));
                    }
                }
            }
            this.plugin.getMultiWorld().blocks.put(player.getName(), value);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Blocks for template were successfully saved into cache."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + "&7Type &f/sb setup template <raw> <display> &7to save the template into configuration file."));
        }
        else if (currentItem.getType() == Materials.CLOCK.getType("item")) {
            player.closeInventory();
            player.getInventory().clear();
            if (!this.plugin.getConfigManager().getConfig("lobby.yml").contains("lobby.spawn")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Translations.translate("PREFIX-CHAT") + Translations.translate("ERROR-NO_LOBBY_SPAWNPOINT")));
                return;
            }
            final Location location = new Location(Bukkit.getWorld(this.plugin.getConfigManager().getConfig("lobby.yml").getString("lobby.spawn.world")), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.x"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.y"), this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.z"));
            location.setPitch((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.pitch"));
            location.setYaw((float)this.plugin.getConfigManager().getConfig("lobby.yml").getDouble("lobby.spawn.yaw"));
            player.teleport(location);
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setExp(0.0f);
            player.setFireTicks(0);
            player.setFlying(false);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.valueOf(this.plugin.getConfigManager().getConfig("config.yml").getString("gamemode").toUpperCase()));
            player.setHealth(20.0);
            player.setLevel(0);
            final Iterator iterator4 = player.getActivePotionEffects().iterator();
            while (iterator4.hasNext()) {
                player.removePotionEffect(iterator4.next().getType());
            }
            player.updateInventory();
            final String s = this.plugin.getMultiWorld().setup.get(player.getName());
            this.plugin.getMultiWorld().getTemplateManager().savePlots(s);
            Bukkit.getWorld(s).save();
            this.plugin.getMultiWorld().setup.remove(player.getName());
        }
    }
}
