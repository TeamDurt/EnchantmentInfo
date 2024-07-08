package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import com.teamdurt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.world.item.enchantment.Enchantment;

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