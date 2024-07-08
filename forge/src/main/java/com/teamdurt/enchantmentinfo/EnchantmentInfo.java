package com.teamdurt.enchantmentinfo;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    
    public EnchantmentInfo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CommonClass.initMain();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            CommonClass.initClient();
        }
    }
}