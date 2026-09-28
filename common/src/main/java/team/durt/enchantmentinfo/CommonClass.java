package team.durt.enchantmentinfo;

import net.minecraft.core.RegistryAccess;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.api.compatibility.EnchantmentsCompatibilityManager;
import team.durt.enchantmentinfo.api.enchantment_data.EnchantmentDataManager;

public class CommonClass {

    public static void initClient() {
        ModEnchantmentCategoryManager.getInstance().populateCategories();
    }

    /**
     * Enchantments are data driven and depend on item tags, so all the data is reset
     * every time client receives registries and tags from server.
     *
     * @see team.durt.enchantmentinfo.mixin.TagCollectorMixin
     */
    public static void initTagDependent(RegistryAccess registryAccess) {
        long startTime = System.currentTimeMillis();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities(registryAccess);
        EnchantmentDataManager.getInstance().reload(registryAccess);

        Constants.LOG.info("EnchantmentInfo initialization took " + (System.currentTimeMillis() - startTime) + "ms");
    }
}