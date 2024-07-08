package com.teamdurt.enchantmentinfo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {
    public EnchantmentInfo(IEventBus eventBus) {
        CommonClass.initMain();

        eventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            CommonClass.initClient();
        }
    }
}