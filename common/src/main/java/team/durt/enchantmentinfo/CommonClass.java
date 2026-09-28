package team.durt.enchantmentinfo;

import net.minecraft.core.RegistryAccess;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.api.compatibility.EnchantmentsCompatibilityManager;
import team.durt.enchantmentinfo.api.enchantment_data.EnchantmentDataManager;
import team.durt.enchantmentinfo.platform.Services;

public class CommonClass {

    public static void initMain() {
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("hi world!");
        }
    }

    public static void initClient() {
        ModEnchantmentCategoryManager.getInstance().populateCategories();
    }

    /**
     * Enchantments are data driven and depend on item tags, so all the data is collected
     * every time client receives registries and tags from server.
     *
     * @see team.durt.enchantmentinfo.mixin.TagCollectorMixin
     */
    public static void initTagDependent(RegistryAccess registryAccess) {
        int startTime = (int) System.currentTimeMillis();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities(registryAccess);
        EnchantmentDataManager.getInstance().populateIncompatibleEnchantments(registryAccess);
        EnchantmentDataManager.getInstance().populateEnchantmentCategories(registryAccess);
        EnchantmentDataManager.getInstance().populateItemGroups(registryAccess);

        Constants.LOG.info("EnchantmentInfo initialization took " + ((int) System.currentTimeMillis() - startTime) + "ms");
    }
}