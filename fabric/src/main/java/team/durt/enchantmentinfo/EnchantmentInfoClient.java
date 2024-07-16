package team.durt.enchantmentinfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

public class EnchantmentInfoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStart);
    }

    private void onClientStart(Minecraft minecraft) {
        CommonClass.initClient();
    }
}
