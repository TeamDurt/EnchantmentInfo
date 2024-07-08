package com.teamdurt.enchantmentinfo;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    
    public EnchantmentInfo() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CommonClass.initMain();

        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CommonClass.initClient();
    }
}