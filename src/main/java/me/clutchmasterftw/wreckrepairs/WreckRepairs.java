package me.clutchmasterftw.wreckrepairs;

import me.clutchmasterftw.wreckrepairs.commands.WreckRepairsCommand;
import me.clutchmasterftw.wreckrepairs.events.OnAnvilOpen;
import me.clutchmasterftw.wreckrepairs.events.OnAnvilPlaceOrDestroy;
import org.bukkit.Bukkit;
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
        Bukkit.getServer().getPluginManager().registerEvents(new OnAnvilOpen(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
