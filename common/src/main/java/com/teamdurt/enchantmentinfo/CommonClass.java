package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import com.teamdurt.enchantmentinfo.compatibility.ItemEnchantmentCompatibilityManager;
import com.teamdurt.enchantmentinfo.platform.Services;

public class CommonClass {

    public static void initMain() {
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to enchantmentinfo");
        }
    }

    public static void initClient() {
        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        ItemEnchantmentCompatibilityManager.getInstance().populateCompatibilities();
    }
}