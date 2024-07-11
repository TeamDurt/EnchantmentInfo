package com.teamdurt.enchantmentinfo.mixin;

import com.teamdurt.enchantmentinfo.frontend.FakeComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {
    @Inject(method = "create(Lnet/minecraft/util/FormattedCharSequence;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void onCreate(FormattedCharSequence formattedCharSequence, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (formattedCharSequence instanceof FakeComponent.TooltipComponentHolder tooltipComponentHolder) {
            cir.setReturnValue(tooltipComponentHolder.getTooltipComponent());
        }
    }
}
