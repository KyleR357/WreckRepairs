package me.clutchmasterftw.wreckrepairs.events;

import dev.lone.itemsadder.api.CustomStack;
import me.clutchmasterftw.wreckrepairs.WreckRepairs;
import me.clutchmasterftw.wreckrepairs.WreckRepairsGUIHolder;
import me.clutchmasterftw.wreckrepairs.utilities.Experience;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OnAnvilAction implements Listener {
    private final FileConfiguration file = WreckRepairs.getPlugin().getConfig();

    @EventHandler
    public void onAnvilOpen(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if(block.getType() != Material.ANVIL) return; //Checks if block is of type ANVIL

        Location location = block.getLocation();

        List<Map<String, Integer>> anvilLocations = (List<Map<String, Integer>>) file.getList("anvil-locations", new ArrayList<>());

        for (Map<String, Integer> locationMap : anvilLocations) {
            int x = locationMap.get("x");
            int y = locationMap.get("y");
            int z = locationMap.get("z");

            Location locationFromMap = new Location(Bukkit.getWorld("world"), x, y, z);

            if (location.equals(locationFromMap)) {
                e.setCancelled(true);

                openGUI(e.getPlayer());

                break;
            }
        }
    }

    public static void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(new WreckRepairsGUIHolder(null), 9, "Repair Items");

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        List<Integer> placeholderSlots = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
        for(int slot:placeholderSlots) {
            inventory.setItem(slot, glass);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        if(inventory.getHolder() instanceof WreckRepairsGUIHolder) {
            int clickedSlot = e.getRawSlot();
            int expNeeded;
            Player player = (Player) e.getWhoClicked();
            ClickType typeOfClick = e.getClick();
            if(clickedSlot == 4 && typeOfClick != ClickType.SHIFT_LEFT) {
                //Raw item slot
                ItemStack rawItem = e.getCursor();
                if(rawItem != null) {
                    if(itemHasDurability(rawItem)) {
                        int rawItemDamage;
                        if(isCustomItem(rawItem)) {
                            //Custom item
                            int rawItemDurability = getItemDurability(rawItem);
                            int rawItemMaxDurability = getItemMaxDurability(rawItem);
                            rawItemDamage = rawItemMaxDurability - rawItemDurability;

//                            player.sendMessage("This item needs " + ChatColor.DARK_RED + ChatColor.BOLD + String.valueOf(rawItemDamage) + ChatColor.RESET + " durability.");
                        } else {
                            //Vanilla item
                            rawItemDamage = getItemDurability(rawItem);

//                            player.sendMessage("This item needs " + ChatColor.DARK_RED + ChatColor.BOLD + String.valueOf(rawItemDamage) + ChatColor.RESET + " durability.");
                        }
                        expNeeded = rawItemDamage / 3;
                        if(expNeeded == 0 && rawItemDamage > 0) {
                            expNeeded = 1;
                        }
                        if(expNeeded > 0) {
                            //Item is damaged
//                            ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE, (expToLevels(expNeeded)[0] > 0 ? expToLevels(expNeeded)[0] : 1));
                            int levelsRequired = ((int) Experience.getLevelFromExp(Experience.getExp(player))) - ((int) Experience.getLevelFromExp(Experience.getExp(player) - expNeeded));
                            ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE, (levelsRequired > 0 ? levelsRequired : 1));
                            ItemMeta expBottleMeta = expBottle.getItemMeta();
                            expBottleMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Experience Required");
                            ArrayList<String> expBottleLore = new ArrayList<String>();
                            expBottleLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Repairing this item will cost " + ChatColor.DARK_GREEN + ChatColor.BOLD + String.valueOf(expNeeded) + ChatColor.RESET + ChatColor.WHITE + " EXP");
//                            expBottleLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "(approx. " + ChatColor.DARK_GREEN + ChatColor.BOLD + String.valueOf(expToLevels(expNeeded)[0] > 0 ? expToLevels(expNeeded)[0] : 1) + ChatColor.RESET + ChatColor.WHITE + " level" + (expToLevels(expNeeded)[0] != 1 ? "s" : "") + ").");
                            expBottleLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "(approx. " + ChatColor.DARK_GREEN + ChatColor.BOLD + (levelsRequired > 0 ? levelsRequired : "<1") + ChatColor.RESET + ChatColor.WHITE + " level" + (levelsRequired != 1 ? "s" : "") + ").");
                            expBottleMeta.setLore(expBottleLore);
                            expBottle.setItemMeta(expBottleMeta);
                            inventory.setItem(1, expBottle);

                            int totalEXP = Experience.getExp(player);

                            if(totalEXP >= expNeeded) {
                                ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                                ItemMeta acceptMeta = accept.getItemMeta();
                                acceptMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Repair");
                                ArrayList<String> acceptLore = new ArrayList<String>();
                                acceptLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Repair this item with the specified");
                                acceptLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "amount of EXP required.");
                                acceptMeta.setLore(acceptLore);
                                accept.setItemMeta(acceptMeta);
                                inventory.setItem(7, accept);
                            } else {
                                ItemStack deny = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                                ItemMeta denyMeta = deny.getItemMeta();
                                denyMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Not Enough Experience");
                                ArrayList<String> denyLore = new ArrayList<String>();
                                denyLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "You don't have enough EXP to repair");
                                denyLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "this item.");
                                denyMeta.setLore(denyLore);
                                deny.setItemMeta(denyMeta);
                                inventory.setItem(7, deny);
                            }
                        }
                    } else {
                        //Item is invalid (doesn't have durability)
//                        player.sendMessage("Invalid item to repair");
                        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                        inventory.setItem(1, glass);
                        inventory.setItem(7, glass);
                    }
                }
            } else if(clickedSlot == 7)  {
                //Check if slot contains confirmation
                e.setCancelled(true);
                ItemStack slot = inventory.getItem(7);
                Material slotType = slot.getType();
                if(slotType == Material.LIME_STAINED_GLASS_PANE) {
                    //The below code is copied from above and COULD be optimized, I'm just lazy :')
                    ItemStack rawItem = inventory.getItem(4);
                    int rawItemDamage;//x
                    if(isCustomItem(rawItem)) {
                        //Custom item
                        int rawItemDurability = getItemDurability(rawItem);
                        int rawItemMaxDurability = getItemMaxDurability(rawItem);
                        rawItemDamage = rawItemMaxDurability - rawItemDurability;
                    } else {
                        //Vanilla item
                        rawItemDamage = getItemDurability(rawItem);
                    }
                    expNeeded = rawItemDamage / 3;
                    if(expNeeded == 0 && rawItemDamage > 0) {
                        expNeeded = 1;
                    }


                    Experience.changeExp(player, -expNeeded);

                    //Set the item to its repaired state
                    ItemStack repairedItem = repairItem(rawItem);
                    inventory.setItem(4, repairedItem);

                    //Turn everything inside of the inventory back into the default glass
                    ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    List<Integer> placeholderSlots = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
                    for(int inventorySlot:placeholderSlots) {
                        inventory.setItem(inventorySlot, glass);
                    }

                    //Play anvil repair sound
                    double randomNumber = Math.random();
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, (float) randomNumber);

//                    player.sendMessage("rawItemDamage = " + String.valueOf(rawItemDamage));
//                    player.sendMessage("level = " + String.valueOf(expToLevels(remainingEXP)[0]) + "remaining" + String.valueOf(expToLevels(remainingEXP)[1]));
//                    player.sendMessage("expNeeded = " + String.valueOf(expNeeded));
//                    player.sendMessage("remainingEXP = " + String.valueOf(remainingEXP));
                }
            } else {
                //Not raw item slot
                if(clickedSlot < 9 && clickedSlot != 4) {
                    //Any other item slot
                    e.setCancelled(true);
                    //Need to check here after the item has been placed to make slot 8 (7) to a shift clickable (and clickable) slot. Additionally, if the player exits the anvil with their item in it (not taking it out), the item should drop onto the ground or be placed back into their inventory
                } else {
                    if(typeOfClick == ClickType.SHIFT_LEFT) {
//                        player.sendMessage("There was a shift click.");

                        if(clickedSlot == 4 && inventory.getItem(4) != null) {
                            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                            List<Integer> placeholderSlots = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
                            for(int slot:placeholderSlots) {
                                inventory.setItem(slot, glass);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }

        InventoryAction action = e.getAction();
        if (action == InventoryAction.COLLECT_TO_CURSOR && e.getSlot() == 4) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            List<Integer> placeholderSlots = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8);
            for(int slot:placeholderSlots) {
                inventory.setItem(slot, glass);
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Inventory inventory = e.getInventory();
        if(inventory.getHolder() instanceof WreckRepairsGUIHolder) {
            boolean inInventory = true;
            for(int i = 0; i < 9; i++) {
                if (e.getInventorySlots().contains(i)) {
                    inInventory = false;
                }
            }

            if(!inInventory) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if(inventory.getHolder() instanceof WreckRepairsGUIHolder && inventory.getItem(4) != null) {
            Player player = (Player) e.getPlayer();

            ItemStack item = inventory.getItem(4);
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
        }
    }

    public boolean isCustomItem(ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null;
    }

    public boolean itemHasDurability(ItemStack item) {
        return item.getType().getMaxDurability() > 0;
    }

    public int getItemDurability(ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        int durability;
        if(customStack == null) {
            ItemMeta meta = item.getItemMeta();
            durability = ((Damageable) meta).getDamage();
        } else {
            durability = customStack.getDurability();
        }
        return durability;
    }

    public int getItemMaxDurability(ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        int maxDurability;
        if(customStack == null) {
            maxDurability = item.getType().getMaxDurability();
        } else {
            maxDurability = customStack.getMaxDurability();
        }
        return maxDurability;
    }

    public ItemStack repairItem(ItemStack item) {
        CustomStack customStack = CustomStack.byItemStack(item);
        if(customStack == null) {
            ItemMeta meta = item.getItemMeta();
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(0);

            item.setItemMeta(meta);

            return item;
        } else {
            int maxDurability = getItemMaxDurability(item);
            customStack.setDurability(maxDurability);

            return customStack.getItemStack();
        }
    }
}