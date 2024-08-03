package team.durt.enchantmentinfo.gui.tooltip.line;

import team.durt.enchantmentinfo.gui.ColorManager;

public class RedLineTooltip extends ColoredLineTooltip {
    public RedLineTooltip(int height) {
        super(ColorManager.getInstance().getRed(), height);
    }
}
