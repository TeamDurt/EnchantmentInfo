package team.durt.enchantmentinfo;

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
        int startTime = (int) System.currentTimeMillis();
        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        EnchantmentDataManager.getInstance().populateIncompatibleEnchantments();
        EnchantmentDataManager.getInstance().populateEnchantmentCategories();

        Constants.LOG.info("EnchantmentInfo initialization took " + ((int) System.currentTimeMillis() - startTime) + "ms");
    }

    public static void initTagDependent() {
        EnchantmentDataManager.getInstance().populateItemGroups();
    }
}