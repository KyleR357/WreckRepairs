package me.clutchmasterftw.wreckrepairs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class WreckRepairsAnvil {
    public static ItemStack giveWreckRepairsAnvil() {
        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta meta = anvil.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "WreckRepairs Anvil");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_GRAY + "Placing this anvil down will spawn a custom WreckRepairs Anvil.");
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();

        data.set(new NamespacedKey(WreckRepairs.getPlugin(), "isWreckRepairAnvil"), PersistentDataType.BOOLEAN, true);

        anvil.setItemMeta(meta);
        return anvil;
    }

    public static void generateAnvilGUI(Player player) {
        player.sendMessage("Hello, you opened a WreckRepair Anvil! Awesome :)");
    }
}
