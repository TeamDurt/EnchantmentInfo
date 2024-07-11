package com.teamdurt.enchantmentinfo.frontend;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeComponent implements Component {
    MutableComponent emptyComponent = Component.empty();

    TooltipComponentHolder tooltipComponentHolder;

    public FakeComponent(ClientTooltipComponent tooltipComponent) {
        this.tooltipComponentHolder = new TooltipComponentHolder(tooltipComponent);
    }

    @Override
    public @NotNull Style getStyle() {
        return emptyComponent.getStyle();
    }

    @Override
    public @NotNull ComponentContents getContents() {
        return emptyComponent.getContents();
    }

    @Override
    public @NotNull List<Component> getSiblings() {
        return emptyComponent.getSiblings();
    }

    @Override
    public @NotNull FormattedCharSequence getVisualOrderText() {
        return tooltipComponentHolder;
    }

    public static class TooltipComponentHolder implements FormattedCharSequence {
        FormattedCharSequence emptySequence = FormattedCharSequence.EMPTY;

        ClientTooltipComponent tooltipComponent;

        public TooltipComponentHolder(ClientTooltipComponent tooltipComponent) {
             this.tooltipComponent = tooltipComponent;
        }

        public ClientTooltipComponent getTooltipComponent() {
            return tooltipComponent;
        }

        @Override
        public boolean accept(@NotNull FormattedCharSink var1) {
            return emptySequence.accept(var1);
        }
    }

    public static List<FakeComponent> tooltipsToComponents(List<ClientTooltipComponent> tooltipComponents) {
        List<FakeComponent> fakeComponents = Lists.newArrayList();
        for (ClientTooltipComponent tooltipComponent : tooltipComponents) {
            fakeComponents.add(new FakeComponent(tooltipComponent));
        }
        return fakeComponents;
    }
}
