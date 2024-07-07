package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.ItemEnchantmentCompatibilityManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import net.fabricmc.api.ModInitializer;

public class EnchantmentInfo implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        ItemEnchantmentCompatibilityManager.getInstance().populateCompatibilities();

        Constants.LOG.info("Enchantment Info initialized");
    }
}
