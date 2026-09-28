package team.durt.enchantmentinfo.api.compatibility;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.List;
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

    public void addCompatibility(Holder<Enchantment> enchantment1, Holder<Enchantment> enchantment2, boolean compatible) {
        compatibilityMap.put(new EnchantmentPair(enchantment1, enchantment2), compatible);
    }

    public boolean isCompatible(Holder<Enchantment> enchantment1, Holder<Enchantment> enchantment2) {
        return compatibilityMap.getOrDefault(new EnchantmentPair(enchantment1, enchantment2), false);
    }

    public void populateCompatibilities(RegistryAccess registryAccess) {
        compatibilityMap.clear();
        List<Holder.Reference<Enchantment>> enchantments = registryAccess.registryOrThrow(Registries.ENCHANTMENT).holders().toList();
        enchantments.forEach(enchantment1 ->
                enchantments.forEach(enchantment2 -> {
                    if (!enchantment1.equals(enchantment2)) {
                        addCompatibility(enchantment1, enchantment2, Enchantment.areCompatible(enchantment1, enchantment2));
                    }
                }));
    }

    private static class EnchantmentPair {
        private final Holder<Enchantment> enchantment1;
        private final Holder<Enchantment> enchantment2;

        public EnchantmentPair(Holder<Enchantment> enchantment1, Holder<Enchantment> enchantment2) {
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
