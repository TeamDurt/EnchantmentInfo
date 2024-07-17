package team.durt.enchantmentinfo.frontend.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SwitcherTooltip implements ClientTooltipComponent, Parent<ClientTooltipComponent> {
    List<ClientTooltipComponent> tooltips;
    int interval;

    public SwitcherTooltip() {
        this(1000);
    }

    public SwitcherTooltip(int intervalMillis) {
        this(Lists.newArrayList(), intervalMillis);
    }

    public SwitcherTooltip(List<ClientTooltipComponent> tooltips, int intervalMillis) {
        setChildList(tooltips);
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

    public ClientTooltipComponent addChild(ClientTooltipComponent child) {
        if (child == null) return this;
        this.tooltips.add(child);
        return this;
    }

    public void setChildList(List<ClientTooltipComponent> childList) {
        this.tooltips = childList;
    }

    public List<ClientTooltipComponent> getChildList() {
        return this.tooltips;
    }
}
