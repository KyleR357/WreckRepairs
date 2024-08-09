package me.clutchmasterftw.wreckrepairs;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Inventory;

public class WreckRepairsGUIHolder implements InventoryHolder {
    private final Inventory inventory;

    public WreckRepairsGUIHolder(Inventory inventory) {
        this.inventory =  inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
