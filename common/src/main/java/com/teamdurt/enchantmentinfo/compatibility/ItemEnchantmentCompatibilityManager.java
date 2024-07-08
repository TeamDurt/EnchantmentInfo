package com.teamdurt.enchantmentinfo.compatibility;

import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEnchantmentCompatibilityManager {
    private static ItemEnchantmentCompatibilityManager instance;
    private final Map<ItemEnchantmentPair, Boolean> compatibilityMap = new HashMap<>();

    private ItemEnchantmentCompatibilityManager() {}

    public static synchronized ItemEnchantmentCompatibilityManager getInstance() {
        if (instance == null) {
            instance = new ItemEnchantmentCompatibilityManager();
        }
        return instance;
    }

    public void addCompatibility(Item item, Enchantment enchantment, boolean compatible) {
        compatibilityMap.put(new ItemEnchantmentPair(item, enchantment), compatible);
    }

    public boolean areCompatible(Item item, Enchantment enchantment) {
        return compatibilityMap.getOrDefault(new ItemEnchantmentPair(item, enchantment), false);
    }

    public List<Item> getCompatibleItems(Enchantment enchantment) {
        return compatibilityMap.entrySet().stream()
                .filter(entry -> entry.getKey().enchantment.equals(enchantment) && entry.getValue())
                .map(entry -> entry.getKey().item)
                .toList();
    }

    public void populateCompatibilities() {
        for (Item item : Services.REGISTRY.getRegisteredItems().toList()) {
            for (Enchantment enchantment : Services.REGISTRY.getRegisteredEnchantments().toList()) {
                addCompatibility(item, enchantment, enchantment.canEnchant(new ItemStack(item)));
            }
        }
    }

    private record ItemEnchantmentPair(Item item, Enchantment enchantment) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemEnchantmentPair that = (ItemEnchantmentPair) o;
            return item.equals(that.item) && enchantment.equals(that.enchantment);
        }
    }
}