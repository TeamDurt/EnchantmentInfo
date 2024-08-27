package team.durt.enchantmentinfo.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.durt.enchantmentinfo.CommonClass;

@Mixin(value = net.minecraftforge.registries.ForgeRegistry.class, remap = false)
public class MixinForgeRegistry<V> {
    @Shadow private V defaultValue;

    @Inject(method = "onBindTags", at = @At(value = "TAIL"))
    private void initEIItemGroups(CallbackInfo ci) {
        if (this.defaultValue instanceof Item) {
            CommonClass.initTagDependent();
        }
    }
}
