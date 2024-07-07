package com.teamdurt.enchantmentinfo;

import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class CommonClass {

    public static void init() {

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to enchantmentinfo");
        }
    }
}