package team.durt.enchantmentinfo;

import team.durt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import team.durt.enchantmentinfo.platform.Services;

public class CommonClass {

    public static void initMain() {
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to enchantmentinfo");
        }
    }

    public static void initClient() {
        int startTime = (int) System.currentTimeMillis();
        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        EnchantmentDataManager.getInstance().populateData();
        Constants.LOG.info("EnchantmentInfo initialization took " + ((int) System.currentTimeMillis() - startTime) + "ms");
    }
}