package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

public class ParentTooltip implements ClientTooltipComponent, Parent<ClientTooltipComponent> {
    List<ClientTooltipComponent> childTooltips;
    Orientation orientation;
    int gap;

    int spaceBefore = 0;
    int spaceAfter = 0;

    public ParentTooltip() {
        this(Orientation.VERTICAL);
    }

    public ParentTooltip(Orientation orientation) {
        this(orientation, 0);
    }

    public ParentTooltip(Orientation orientation, int gap) {
        this(Lists.newArrayList(), orientation, gap);
    }

    public ParentTooltip(List<ClientTooltipComponent> childTooltips, Orientation orientation, int gap) {
        setChildList(childTooltips);
        this.orientation = orientation;
        this.gap = gap;
    }

    public ClientTooltipComponent addChild(@Nullable ClientTooltipComponent tooltip) {
        if (tooltip == null) return this;
        this.childTooltips.add(tooltip);
        return this;
    }

    public void setChildList(List<ClientTooltipComponent> childList) {
        this.childTooltips = childList;
    }

    public List<ClientTooltipComponent> getChildList() {
        return this.childTooltips;
    }

    public ClientTooltipComponent addChild(Component component) {
        return addChild(new ClientTextTooltip(component.getVisualOrderText()));
    }

    public void setSpaceBefore(int gap) {
        this.spaceBefore = gap;
    }

    public void setSpaceAfter(int gap) {
        this.spaceAfter = gap;
    }

    @Override
    public int getHeight() {
        int height = 0;
        if (orientation == Orientation.VERTICAL) {
            height += spaceBefore;
            for (ClientTooltipComponent childTooltip : childTooltips) {
                height += childTooltip.getHeight() + gap;
            }
            height += spaceAfter;
            if (!childTooltips.isEmpty()) height -= gap;
        } else {
            for (ClientTooltipComponent childTooltip : childTooltips) {
                height = Math.max(childTooltip.getHeight(), height);
            }
        }
        return height;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int width = 0;
        if (orientation == Orientation.HORIZONTAL) {
            width += spaceBefore;
            for (ClientTooltipComponent childComponent : childTooltips) {
                width += childComponent.getWidth(font) + gap;
            }
            width += spaceAfter;
            if (!childTooltips.isEmpty()) width -= gap;
        } else {
            for (ClientTooltipComponent childComponent : childTooltips) {
                width = Math.max(childComponent.getWidth(font), width);
            }
        }
        return width;
    }

    @Override
    public void renderText(@NotNull Font font, int x, int y, @NotNull Matrix4f matrix4f, MultiBufferSource.@NotNull BufferSource bufferSource) {
        int newX = x;
        int newY = y;
        for (ClientTooltipComponent childTooltip : childTooltips) {
           childTooltip.renderText(font, newX, newY, matrix4f, bufferSource);
           if (orientation == Orientation.HORIZONTAL) {
               newX += childTooltip.getWidth(font) + gap;
           } else {
               newY += childTooltip.getHeight() + gap;
           }
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        int newX = x;
        int newY = y;
        if (orientation == Orientation.VERTICAL) {
            newY += spaceBefore;
        } else {
            newX += spaceBefore;
        }
        for (ClientTooltipComponent childTooltip : childTooltips) {
            childTooltip.renderImage(font, newX, newY, guiGraphics);
            if (orientation == Orientation.HORIZONTAL) {
                newX += childTooltip.getWidth(font) + gap;
            } else {
                newY += childTooltip.getHeight() + gap;
            }
        }
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}
