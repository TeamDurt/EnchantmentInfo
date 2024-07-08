package com.teamdurt.enchantmentinfo.compatibility;

import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnchantmentsCompatibilityManager {
    private static EnchantmentsCompatibilityManager instance;
    private final Map<EnchantmentPair, Boolean> compatibilityMap = new HashMap<>();

    private EnchantmentsCompatibilityManager() {}

    public static synchronized EnchantmentsCompatibilityManager getInstance() {
        if (instance == null) {
            instance = new EnchantmentsCompatibilityManager();
        }
        return instance;
    }

    public void addCompatibility(Enchantment enchantment1, Enchantment enchantment2, boolean compatible) {
        compatibilityMap.put(new EnchantmentPair(enchantment1, enchantment2), compatible);
    }

    public boolean isCompatible(Enchantment enchantment1, Enchantment enchantment2) {
        return compatibilityMap.getOrDefault(new EnchantmentPair(enchantment1, enchantment2), false);
    }

    public void populateCompatibilities() {
        for (Enchantment enchantment1 : Services.REGISTRY.getRegisteredEnchantments().toList()) {
            for (Enchantment enchantment2 : Services.REGISTRY.getRegisteredEnchantments().toList()) {
                if (enchantment1 == enchantment2) continue;
                addCompatibility(enchantment1, enchantment2, enchantment1.isCompatibleWith(enchantment2));
            }
        }
    }

    private record EnchantmentPair(Enchantment enchantment1, Enchantment enchantment2) {
        public EnchantmentPair {
            if (enchantment1.hashCode() > enchantment2.hashCode()) {
                Enchantment temp = enchantment1;
                enchantment1 = enchantment2;
                enchantment2 = temp;
            } else if (enchantment1.hashCode() == enchantment2.hashCode() && !enchantment1.equals(enchantment2)) {
                if (enchantment1.toString().compareTo(enchantment2.toString()) > 0) {
                    Enchantment temp = enchantment1;
                    enchantment1 = enchantment2;
                    enchantment2 = temp;
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EnchantmentPair that)) return false;
            return enchantment1.equals(that.enchantment1) && enchantment2.equals(that.enchantment2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enchantment1, enchantment2);
        }
    }
}
