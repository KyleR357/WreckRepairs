package me.clutchmasterftw.wreckrepairs;

import me.clutchmasterftw.wreckrepairs.commands.WreckRepairsCommand;
import me.clutchmasterftw.wreckrepairs.events.OnAnvilAction;
import me.clutchmasterftw.wreckrepairs.events.OnAnvilPlaceOrDestroy;
import me.clutchmasterftw.wreckrepairs.events.OnPlayerDisconnect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class WreckRepairs extends JavaPlugin {
    public static WreckRepairs getPlugin() {
        return plugin;
    }

    private static WreckRepairs plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();

        Logger logger = this.getLogger();
        logger.info("WreckRepairs has been enabled!");

        getCommand("wreckrepairs").setExecutor(new WreckRepairsCommand(logger));
        Bukkit.getServer().getPluginManager().registerEvents(new OnAnvilPlaceOrDestroy(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnAnvilAction(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerDisconnect(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof WreckRepairsGUIHolder && player.getOpenInventory().getTopInventory().getItem(4) != null) {
                ItemStack item = player.getOpenInventory().getTopInventory().getItem(4);
//
//                player.closeInventory();
                boolean foundSlot = false;
                for(int i = 0; i < 36; i++) {
                    if(player.getInventory().getItem(i) == null) {
                        player.getInventory().setItem(i, item);
                        foundSlot = true;
                        break;
                    }
                }
                if(!foundSlot) {
                    //Could not find a valid slot in the players inventory for the item. Proceeding by dropping the item on the ground.
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }

                player.closeInventory();
            }
        }
    }
}
