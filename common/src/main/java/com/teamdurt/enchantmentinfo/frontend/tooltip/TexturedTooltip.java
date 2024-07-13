package com.teamdurt.enchantmentinfo.frontend.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TexturedTooltip implements ClientTooltipComponent {
    ResourceLocation texture;
    int width;
    int height;

    public TexturedTooltip(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
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
        int width = getWidth(font);
        int height = getHeight();
        guiGraphics.blit(texture, x, y, 0, 0, width, height, width, height);
    }
}
