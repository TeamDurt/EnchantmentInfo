package team.durt.enchantmentinfo.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import team.durt.enchantmentinfo.gui.FakeComponent;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @ModifyVariable(
            method = "renderTooltipInternal",
            ordinal = 0,
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            argsOnly = true
    )
    private List<ClientTooltipComponent> parseTooltips(List<ClientTooltipComponent> list) {
        List<ClientTooltipComponent> parsedTooltips = Lists.newArrayList();
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
