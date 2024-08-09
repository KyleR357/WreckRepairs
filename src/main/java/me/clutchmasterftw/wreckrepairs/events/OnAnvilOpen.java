package me.clutchmasterftw.wreckrepairs.events;

import dev.lone.itemsadder.api.CustomStack;
import me.clutchmasterftw.wreckrepairs.WreckRepairs;
import me.clutchmasterftw.wreckrepairs.WreckRepairsGUIHolder;
import me.clutchmasterftw.wreckrepairs.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class OnAnvilOpen implements Listener {
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

//        ItemStack item = new ItemStack(Material.WOODEN_SHOVEL);
//        inventory.setItem(0, item);

        //{Count:1b,id:"minecraft:player_head",tag:{SkullOwner:{Id:[I;67411088,-739686879,-1666252800,178950440],Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE4MDc5ZDU4NDc0MTZhYmY0NGU4YzJmZWMyY2NkNDRmMDhkNzM2Y2E4ZTUxZjk1YTQzNmQ4NWY2NDNmYmMifX19"}]}},display:{Name:'{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"blue","text":"Yellow Question Mark"}],"text":""}'}}}


//        ItemStack helpHead = Utilities.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE4MDc5ZDU4NDc0MTZhYmY0NGU4YzJmZWMyY2NkNDRmMDhkNzM2Y2E4ZTUxZjk1YTQzNmQ4NWY2NDNmYmMifX19");
//        ItemMeta helpHeadMeta = helpHead.getItemMeta();
//
//        helpHeadMeta.setDisplayName("§9Yellow Question Mark");
//        List<String> helpHeadLore = new ArrayList<String>();
//        helpHeadLore.add("This is a");
//        helpHeadLore.add("TEST :)");
//
//        helpHeadMeta.setLore(helpHeadLore);
//
//        helpHead.setItemMeta(helpHeadMeta);

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
            if(clickedSlot == 4) {
                //Raw item slot
                player.sendMessage("YOU CLICKED SLOT 5 IN A WRECKREPAIRS ANVIL!!!");
                //Remember to import hastepotion btw
                ItemStack rawItem = e.getCursor();
                if(rawItem != null) {
                    if(itemHasDurability(rawItem)) {
                        int rawItemDamage;
                        if(isCustomItem(rawItem)) {
                            //Custom item
                            int rawItemDurability = getItemDurability(rawItem);
                            int rawItemMaxDurability = getItemMaxDurability(rawItem);
                            rawItemDamage = rawItemMaxDurability - rawItemDurability;

                            player.sendMessage("This item needs " + ChatColor.DARK_RED + ChatColor.BOLD + String.valueOf(rawItemDamage) + ChatColor.RESET + " durability.");
                        } else {
                            //Vanilla item
                            rawItemDamage = getItemDurability(rawItem);

                            player.sendMessage("This item needs " + ChatColor.DARK_RED + ChatColor.BOLD + String.valueOf(rawItemDamage) + ChatColor.RESET + " durability.");
                        }
                        expNeeded = rawItemDamage / 3;
                        if(expNeeded == 0 && rawItemDamage > 0) {
                            expNeeded = 1;
                        }
                        if(expNeeded > 0) {
                            //Item is damaged
                            ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE, (expToLevels(expNeeded)[0] > 0 ? expToLevels(expNeeded)[0] : 1));
                            ItemMeta expBottleMeta = expBottle.getItemMeta();
                            expBottleMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Experience Required");
                            ArrayList<String> expBottleLore = new ArrayList<String>();
                            expBottleLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "Repairing this item will cost " + ChatColor.DARK_GREEN + ChatColor.BOLD + String.valueOf(expNeeded) + ChatColor.RESET + ChatColor.WHITE + " EXP");
                            expBottleLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "(approx. " + ChatColor.DARK_GREEN + ChatColor.BOLD + String.valueOf(expToLevels(expNeeded)[0] > 0 ? expToLevels(expNeeded)[0] : 1) + ChatColor.RESET + ChatColor.WHITE + " level" + (expToLevels(expNeeded)[0] != 1 ? "s" : "") + ").");
                            expBottleMeta.setLore(expBottleLore);
                            expBottle.setItemMeta(expBottleMeta);
                            inventory.setItem(1, expBottle);

                            if(player.getTotalExperience() >= expNeeded) {
                                ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                                ItemMeta acceptMeta = accept.getItemMeta();
                                acceptMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Repair");
                                ArrayList<String> acceptLore = new ArrayList<String>();
                                acceptLore.add("Repair this item with the specified");
                                acceptLore.add("amount of EXP required.");
                                acceptMeta.setLore(acceptLore);
                                accept.setItemMeta(acceptMeta);
                                inventory.setItem(7, accept);
                            } else {
                                ItemStack deny = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                                ItemMeta denyMeta = deny.getItemMeta();
                                denyMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Not Enough Experience");
                                ArrayList<String> denyLore = new ArrayList<String>();
                                denyLore.add("You don't have enough EXP to repair");
                                denyLore.add("this item.");
                                denyMeta.setLore(denyLore);
                                deny.setItemMeta(denyMeta);
                                inventory.setItem(7, deny);
                            }
                        }
                    } else {
                        //Item is invalid (doesn't have durability)
                        player.sendMessage("Invalid item to repair");
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
                    int rawItemDamage;
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



                    int remainingEXP = player.getTotalExperience() - expNeeded;
                    player.setExp(10);
                }
            } else {
                //Not raw item slot
                if(clickedSlot < 9) {
                    //Any other item slot
                    e.setCancelled(true);
                    //Need to check here after the item has been placed to make slot 8 (7) to a shift clickable (and clickable) slot. Additionally, if the player exits the anvil with their item in it (not taking it out), the item should drop onto the ground or be placed back into their inventory
                } else {
                    ClickType typeOfClick = e.getClick();
                    if(typeOfClick == ClickType.SHIFT_LEFT) {
                        player.sendMessage("There was a shift click.");

                        e.setCancelled(true);
                    }
                }
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

    public int[] expToLevels(int exp) {
        //Levels 1-15: +2 (starts from 7)
        //Levels 16-30: +5 (16->17)
        //Levels 31+: +9 (31->32)
        int levels = 0;
        int remaining = 0;
        while(exp > 0) {
            if(levels == 0) {
                if(exp < 7) {
                    remaining = exp;
                    break;
                } else {
                    exp -= 7;
                    levels++;
                }
            } else if(levels <= 16) {
                if(exp >= 7 + (levels * 2)) {
                    exp -= 7 + (levels * 2);
                    levels++;
                } else {
                    remaining = exp;
                    break;
                }
            } else if(levels <= 31) {
                if(exp >= 37 + ((levels - 16) * 5)) {
                    exp -= 37 + ((levels - 16) * 5);
                    levels++;
                } else {
                    remaining = exp;
                    break;
                }
            } else {
                if(exp >= 112 + ((levels - 31) * 9)) {
                    exp -= 112 + ((levels - 31) * 9);
                    levels++;
                } else {
                    remaining = exp;
                    break;
                }
            }
        }
        if(exp == 0 && levels == 0) {
            levels++;
        }

        return new int[]{levels, remaining};
    }
}
