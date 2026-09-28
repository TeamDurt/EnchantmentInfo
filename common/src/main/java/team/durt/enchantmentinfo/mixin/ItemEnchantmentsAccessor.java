package team.durt.enchantmentinfo.mixin;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEnchantments.class)
public interface ItemEnchantmentsAccessor {
    @Accessor
    boolean isShowInTooltip();
}
