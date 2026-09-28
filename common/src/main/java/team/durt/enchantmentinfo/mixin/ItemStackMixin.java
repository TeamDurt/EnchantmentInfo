package team.durt.enchantmentinfo.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.durt.enchantmentinfo.gui.TooltipBuilder;
import team.durt.enchantmentinfo.gui.TooltipHelper;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(
            method = "addToTooltip",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddToTooltipHead(DataComponentType<?> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, CallbackInfo ci) {
        ItemEnchantments enchantments = enchantmentinfo$getShownStoredEnchantments(component);
        if (enchantments == null) return;

        // custom tooltips, canceling method if added to avoid default enchantment names
        if (TooltipBuilder.build(tooltipAdder, enchantments, context.registries())) {
            // release shift message
            TooltipHelper.addShiftMessage(tooltipAdder);
            ci.cancel();
        }
    }

    @Inject(
            method = "addToTooltip",
            at = @At("TAIL")
    )
    private void onAddToTooltipTail(DataComponentType<?> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (enchantmentinfo$getShownStoredEnchantments(component) == null) return;

        // hold shift message
        TooltipHelper.addShiftMessage(tooltipAdder);
    }

    /**
     * Returns Enchantments stored in Enchanted Book if vanilla shows them in rendered tooltip, null otherwise.
     */
    @Unique
    private @Nullable ItemEnchantments enchantmentinfo$getShownStoredEnchantments(DataComponentType<?> component) {
        if (component != DataComponents.STORED_ENCHANTMENTS) return null;
        // tooltips are also collected off-thread for creative search, they should stay vanilla
        if (!Minecraft.getInstance().isSameThread()) return null;

        ItemEnchantments enchantments = ((ItemStack) (Object) this).get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return null;
        if (!((ItemEnchantmentsAccessor) enchantments).isShowInTooltip()) return null;

        return enchantments;
    }
}
