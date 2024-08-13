package me.clutchmasterftw.wreckrepairs.commands;

import me.clutchmasterftw.wreckrepairs.WreckRepairsAnvil;
import me.clutchmasterftw.wreckrepairs.events.OnAnvilAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class WreckRepairsCommand implements CommandExecutor {
    private final Logger logger;

    public WreckRepairsCommand(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0].equals("give") && sender.hasPermission("wreckrepairs.give")) {
            if(args.length < 2) {
                // args[1] is empty
                if(sender instanceof Player) {
                    // A player executed this command
                    sender.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": Please provide an online player as another parameter. Example: /wreckrepairs give {player}.");
                } else {
                    // The console executed this command
                    logger.info("WreckRepairs: Please provide an online player as another parameter. Example: /wreckrepairs give {player}.");
                }
            } else {
                Player sentPlayer = Bukkit.getServer().getPlayer(args[1]);

                if(sender instanceof Player) {
                    // A player executed this command
                    sender.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": Gave " + args[1] + " a WreckRepairs anvil.");
                } else {
                    // The console executed this command
                    logger.info("WreckRepairs: Gave " + args[1] + " a WreckRepairs anvil.");
                }

                ItemStack anvil = WreckRepairsAnvil.giveWreckRepairsAnvil();

                // Give the WreckRepairs anvil to the player
                if (sentPlayer.getInventory().firstEmpty() != -1) {
                    sentPlayer.getInventory().addItem(anvil);
                    sentPlayer.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": You've received a WreckRepairs anvil.");
                } else {
                    sentPlayer.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": Unable to give you a WreckRepairs anvil.");
                }
            }
        } else {
            if((sender instanceof Player && args[0].equals("open")) && sender.hasPermission("wreckrepairs.access")) {
                sender.sendMessage("Opened WreckRepairs anvil menu.");
                OnAnvilAction.openGUI((Player) sender);
            } else {
                if (sender instanceof Player) {
                    // A player executed this command
                    if (!sender.hasPermission("wreckrepairs.give")) {
                        sender.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": " + ChatColor.RED + "You don't have permission to access this command.");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "WreckRepairs" + ChatColor.RESET + ": Unknown Command. Use /wreckrepairs [open/give] [player (in a give use case)].");
                    }
                } else {
                    // The console executed this command
                    logger.info("WreckRepairs: Unknown Command. Use /wreckrepairs [open/give] [player (in a give use case)].");
                }
            }
        }

        return true;
    }
}
