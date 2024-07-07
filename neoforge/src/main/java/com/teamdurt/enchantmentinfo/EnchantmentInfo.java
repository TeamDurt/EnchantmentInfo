package com.teamdurt.enchantmentinfo;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class EnchantmentInfo {

    public EnchantmentInfo(IEventBus eventBus) {
        CommonClass.init();
    }
}