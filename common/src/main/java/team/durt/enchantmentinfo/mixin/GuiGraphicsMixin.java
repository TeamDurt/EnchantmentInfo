package team.durt.enchantmentinfo.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import team.durt.enchantmentinfo.gui.FakeComponent;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    /**
     * Modifies tooltips before they are passed to renderTooltipInternal,
     * so they are right even if other mod renders tooltips on its own there.
     */
    @ModifyArg(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"
            ),
            index = 1
    )
    private List<ClientTooltipComponent> parseTooltips(List<ClientTooltipComponent> list) {
        List<ClientTooltipComponent> parsedTooltips = new ArrayList<>();
        for (ClientTooltipComponent tooltip : list) {
            if (tooltip instanceof ClientTextTooltip textTooltip) {
                if (((ClientTextTooltipAccessor) textTooltip).getText() instanceof FakeComponent.TooltipComponentHolder tooltipComponentHolder) {
                    tooltip = tooltipComponentHolder.getTooltipComponent();
                }
            }
            parsedTooltips.add(tooltip);
        }
        return parsedTooltips;
    }
}
