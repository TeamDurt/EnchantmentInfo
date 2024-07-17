package team.durt.enchantmentinfo.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;

import java.util.Iterator;
import java.util.Optional;


@Mixin(MappedRegistry.class)
public abstract class MixinMappedRegistry<T> {
    @Shadow public abstract Iterator<T> iterator();

    @Shadow @Final private HolderLookup.RegistryLookup<T> lookup;

    @Inject(method = "bindTags", at = @At(value = "TAIL"))
    private void bindTags(CallbackInfo ci) {
        Optional<Holder.Reference<T>> first = this.lookup.listElements().findFirst();
        first.ifPresent(holder -> {
            if (holder.value() instanceof Item) {
                EnchantmentDataManager.getInstance().populateItemGroups();
            }
        });
    }
}
