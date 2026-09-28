package team.durt.enchantmentinfo.mixin;

import net.minecraft.client.multiplayer.TagCollector;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.durt.enchantmentinfo.CommonClass;

/**
 * Client receives registries and tags from server both on joining and after {@code /reload}.
 * Forge does not fire its tags event on joining, so it is handled here for all loaders.
 */
@Mixin(TagCollector.class)
public class TagCollectorMixin {
    @Inject(method = "updateTags", at = @At("TAIL"))
    private void onUpdateTags(RegistryAccess registryAccess, boolean isMemoryConnection, CallbackInfo ci) {
        CommonClass.initTagDependent(registryAccess);
    }
}
