package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import team.durt.enchantmentinfo.gui.Parent;

import java.util.ArrayList;
import java.util.List;

public class SwitcherTooltip implements ClientTooltipComponent, Parent<ClientTooltipComponent> {
    List<ClientTooltipComponent> tooltips;
    int interval;
    static final int defaultInterval = 1000;

    public SwitcherTooltip() {
        this(defaultInterval);
    }

    public SwitcherTooltip(int intervalMillis) {
        this(Lists.newArrayList(), intervalMillis);
    }

    public SwitcherTooltip(List<? extends ClientTooltipComponent> tooltips) {
        this(tooltips, defaultInterval);
    }

    public SwitcherTooltip(List<? extends ClientTooltipComponent> tooltips, int intervalMillis) {
        this.tooltips = new ArrayList<>(tooltips);
        this.interval = intervalMillis;
    }

    @Override
    public int getHeight() {
        ClientTooltipComponent tooltip = currentTooltip();
        if (tooltip == null) return 0;
        return tooltip.getHeight();
    }

    @Override
    public int getWidth(@NotNull Font font) {
        ClientTooltipComponent tooltip = currentTooltip();
        if (tooltip == null) return 0;
        return tooltip.getWidth(font);
    }

    @Override
    public void renderText(@NotNull Font font, int x, int y, @NotNull Matrix4f matrix4f, MultiBufferSource.@NotNull BufferSource bufferSource) {
        ClientTooltipComponent tooltip = currentTooltip();
        if (tooltip == null) return;
        tooltip.renderText(font, x, y, matrix4f, bufferSource);
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        ClientTooltipComponent tooltip = currentTooltip();
        if (tooltip == null) return;
        tooltip.renderImage(font, x, y, guiGraphics);
    }

    int currentTooltipIndex() {
        if (tooltips.isEmpty()) return -1;
        long l = System.currentTimeMillis() / interval;
        return (int) (l % tooltips.size());
    }

    @Nullable
    ClientTooltipComponent currentTooltip() {
        int index = currentTooltipIndex();
        if (index < 0) return null;
        return tooltips.get(index);
    }

    public SwitcherTooltip addChild(@Nullable ClientTooltipComponent child) {
        if (child != null) tooltips.add(child);
        return this;
    }

    public SwitcherTooltip setChildList(List<ClientTooltipComponent> childList) {
        this.tooltips = childList;
        return this;
    }

    public List<ClientTooltipComponent> getChildList() {
        return this.tooltips;
    }
}
