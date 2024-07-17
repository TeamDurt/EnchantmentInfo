package team.durt.enchantmentinfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;

public class EnchantmentInfoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStart);
        CommonLifecycleEvents.TAGS_LOADED.register(this::onTagsLoaded);
    }

    private void onClientStart(Minecraft minecraft) {
        CommonClass.initClient();
    }

    private void onTagsLoaded(RegistryAccess registryAccess, boolean b) {
        CommonClass.initTagDependent();
    }
}
