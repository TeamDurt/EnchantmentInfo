package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.ItemEnchantmentCompatibilityManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    public EnchantmentInfo(IEventBus eventBus) {
        CommonClass.init();

        eventBus.addListener(this::commonSetup);

        Constants.LOG.info("Enchantment Info initialized");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        ItemEnchantmentCompatibilityManager.getInstance().populateCompatibilities();
    }
}