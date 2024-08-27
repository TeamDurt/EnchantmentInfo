package team.durt.enchantmentinfo.mixin;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.durt.enchantmentinfo.gui.TooltipBuilder;
import team.durt.enchantmentinfo.gui.TooltipHelper;

import java.util.List;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookItemMixin {
    @Shadow
    public static ListTag getEnchantments(ItemStack $$0) {
        return null;
    }

    @Inject(
            method = "appendHoverText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onAppendEnchantmentNamesBefore(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo ci) {
        // custom tooltips, canceling method if added to avoid default enchantment names
        if (TooltipBuilder.build(components, getEnchantments(itemStack))) {
            // release shift message
            TooltipHelper.addShiftMessage(components);
            ci.cancel();
        }
    }

    @Inject(
            method = "appendHoverText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onAppendEnchantmentNamesAfter(ItemStack itemStack, Level level, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo ci) {
        // hold shift message
        TooltipHelper.addShiftMessage(components);
    }
}
