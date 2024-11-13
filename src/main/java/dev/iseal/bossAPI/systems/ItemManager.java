package dev.iseal.bossAPI.systems;

import dev.iseal.bossAPI.systems.attacks.AttackManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemManager {

    private static ItemManager instance;
    public static ItemManager getInstance() {
        if (instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }

    private ArrayList<ItemStack> items = new ArrayList<>();

    public void registerItems() {
        items.clear();
        AttackManager.getInstance().getAttacks().forEach(attack -> items.add(attack.getAttackItem()));
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }
}
