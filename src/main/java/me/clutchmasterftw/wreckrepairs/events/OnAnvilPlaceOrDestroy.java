package me.clutchmasterftw.wreckrepairs.events;

import me.clutchmasterftw.wreckrepairs.WreckRepairs;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnAnvilPlaceOrDestroy implements Listener {
    private final FileConfiguration file = WreckRepairs.getPlugin().getConfig();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack blockItem = e.getItemInHand();
        ItemMeta meta = blockItem.getItemMeta();
        PersistentDataContainer itemData = meta.getPersistentDataContainer();

        if(blockItem.getType() == Material.ANVIL && itemData.has(new NamespacedKey(WreckRepairs.getPlugin(), "isWreckRepairAnvil"))) {
            Player player = e.getPlayer();
            player.sendMessage("You've placed a " + ChatColor.AQUA + "WreckRepairs Anvil" + ChatColor.RESET + ".");

            //Insert the persistent data back into the block via the config.yml, since it was cleared upon placing the block.
            Block blockPlaced = e.getBlockPlaced();
            int x = blockPlaced.getX();
            int y = blockPlaced.getY();
            int z = blockPlaced.getZ();

            Map<String, Integer> locationMap = new HashMap<>();
            locationMap.put("x", x);
            locationMap.put("y", y);
            locationMap.put("z", z);

            List<Map<String, Integer>> anvilLocations = (List<Map<String, Integer>>) file.getList("anvil-locations", new ArrayList<>());
            anvilLocations.add(locationMap);

            file.set("anvil-locations", anvilLocations);
            WreckRepairs.getPlugin().saveConfig();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if(block.getType() != Material.ANVIL) return;

        Location location = block.getLocation();

        List<Map<String, Integer>> anvilLocations = (List<Map<String, Integer>>) file.getList("anvil-locations", new ArrayList<>());

        int i = 0;
        for (Map<String, Integer> locationMap : anvilLocations) {
            int x = locationMap.get("x");
            int y = locationMap.get("y");
            int z = locationMap.get("z");

            Location locationFromMap = new Location(Bukkit.getWorld("world"), x, y, z);

            if (location.equals(locationFromMap)) {
                e.getPlayer().sendMessage("You've destroyed a WreckRepairs Anvil.");
                anvilLocations.remove(i);

                file.set("anvil-locations", anvilLocations);
                WreckRepairs.getPlugin().saveConfig();

                break;
            }
            i++;
        }
    }
}
