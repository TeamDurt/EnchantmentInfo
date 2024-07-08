package com.teamdurt.enchantmentinfo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    public EnchantmentInfo(IEventBus eventBus) {
        CommonClass.initMain();

        eventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CommonClass.initClient();
    }
}