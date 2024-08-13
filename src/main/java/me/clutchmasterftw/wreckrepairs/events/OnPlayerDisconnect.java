package me.clutchmasterftw.wreckrepairs.events;

import me.clutchmasterftw.wreckrepairs.WreckRepairsGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OnPlayerDisconnect implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        Inventory inventory = player.getInventory();
        if(inventory.getHolder() instanceof WreckRepairsGUIHolder && inventory.getItem(4) != null) {

            ItemStack item = inventory.getItem(4);
            boolean foundSlot = false;
            for(int i = 0; i < 36; i++) {
                if(player.getInventory().getItem(i) == null) {
                    inventory.setItem(i, item);
                    foundSlot = true;
                    break;
                }
            }
            if(!foundSlot) {
                //Could not find a valid slot in the players inventory for the item. Proceeding by dropping the item on the ground.
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }
}
