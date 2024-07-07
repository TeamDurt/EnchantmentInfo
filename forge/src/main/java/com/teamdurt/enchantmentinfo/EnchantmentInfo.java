package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.ItemEnchantmentCompatibilityManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    
    public EnchantmentInfo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CommonClass.init();

        modEventBus.addListener(this::commonSetup);

        Constants.LOG.info("Enchantment Info initialized");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModEnchantmentCategoryManager.getInstance().populateCategories();
        EnchantmentsCompatibilityManager.getInstance().populateCompatibilities();
        ItemEnchantmentCompatibilityManager.getInstance().populateCompatibilities();
    }
}