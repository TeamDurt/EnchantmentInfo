package team.durt.enchantmentinfo.gui.group;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface InfoHolder {
    /**
     * This method is used to transform {@link InfoGroup} to {@link ClientTooltipComponent Tooltip}.
     *
     * @see team.durt.enchantmentinfo.gui.TooltipHelper#infoToTooltip(List)
     */
    @Nullable
    ClientTooltipComponent toTooltip();
}