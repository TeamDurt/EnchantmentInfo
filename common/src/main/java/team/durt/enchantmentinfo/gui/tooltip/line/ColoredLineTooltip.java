package team.durt.enchantmentinfo.gui.tooltip.line;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.NotNull;

public class ColoredLineTooltip implements ClientTooltipComponent {
    int color;
    int height;
    int width = 2;

    public ColoredLineTooltip(int color, int height) {
        this.color = color;
        this.height = height;
    }

    public int getColor() {
        return color;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return width;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(x, y, x + getWidth(font), y + getHeight(), getColor());
    }
}
