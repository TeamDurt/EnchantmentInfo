package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return currentTooltip().getHeight();
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return currentTooltip().getWidth(font);
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        currentTooltip().renderImage(font, x, y, guiGraphics);
    }

    int currentTooltipIndex() {
        long l = System.currentTimeMillis() / interval;
        return (int) (l % tooltips.size());
    }

    ClientTooltipComponent currentTooltip() {
        return tooltips.get(currentTooltipIndex());
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
