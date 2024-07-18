package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import team.durt.enchantmentinfo.gui.tooltip.line.ColoredLineTooltip;

import java.util.List;

public class LineGroupTooltip extends ParentTooltip {
    ParentTooltip content = new ParentTooltip();

    public LineGroupTooltip(ColoredLineTooltip coloredLine, ClientTooltipComponent... tooltipUnderLine) {
        super(Orientation.HORIZONTAL, 2);
        content.setChildList(List.of(tooltipUnderLine));
        super.setChildList(List.of(coloredLine, content));
    }

    @Override
    public ParentTooltip addChild(ClientTooltipComponent child) {
        return content.addChild(child);
    }

    @Override
    public ParentTooltip setChildList(List<ClientTooltipComponent> childList) {
        return content.setChildList(childList);
    }

    @Override
    public List<ClientTooltipComponent> getChildList() {
        return content.getChildList();
    }
}
