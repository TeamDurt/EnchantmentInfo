package team.durt.enchantmentinfo.compatibility;

import team.durt.enchantmentinfo.platform.Services;
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
        Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment1 ->
                Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment2 -> {
                    if (!enchantment1.equals(enchantment2)) {
                        addCompatibility(enchantment1, enchantment2, enchantment1.isCompatibleWith(enchantment2));
                    }
                }));
    }

    private static class EnchantmentPair {
        private final Enchantment enchantment1;
        private final Enchantment enchantment2;

        public EnchantmentPair(Enchantment enchantment1, Enchantment enchantment2) {
            if (enchantment1.hashCode() > enchantment2.hashCode() ||
                    (enchantment1.hashCode() == enchantment2.hashCode() && enchantment1.toString().compareTo(enchantment2.toString()) > 0)) {
                this.enchantment1 = enchantment2;
                this.enchantment2 = enchantment1;
            } else {
                this.enchantment1 = enchantment1;
                this.enchantment2 = enchantment2;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EnchantmentPair)) return false;
            EnchantmentPair that = (EnchantmentPair) o;
            return Objects.equals(enchantment1, that.enchantment1) && Objects.equals(enchantment2, that.enchantment2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enchantment1, enchantment2);
        }
    }
}
