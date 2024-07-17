package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemTooltip implements ClientTooltipComponent {
    ItemStack itemStack;

    public ItemTooltip(Item item) {
        this(new ItemStack(item));
    }

    public ItemTooltip(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return 16;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        guiGraphics.renderItem(itemStack, x, y);
    }
}
