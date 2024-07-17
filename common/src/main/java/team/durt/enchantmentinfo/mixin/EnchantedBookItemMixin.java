package team.durt.enchantmentinfo.mixin;

import team.durt.enchantmentinfo.gui.TooltipBuilder;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookItemMixin {
    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
    private void onGetTooltipLines(List<Component> components, ListTag enchantmentTags) {
        TooltipBuilder.build(components, enchantmentTags);
    }
}
