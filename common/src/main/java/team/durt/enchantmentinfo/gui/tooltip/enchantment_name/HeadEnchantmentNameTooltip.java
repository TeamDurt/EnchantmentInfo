package team.durt.enchantmentinfo.gui.tooltip.enchantment_name;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.gui.tooltip.line.BlueLineTooltip;

public class HeadEnchantmentNameTooltip extends EnchantmentNameTooltip {
    public HeadEnchantmentNameTooltip(EnchantmentInstance enchantmentInstance) {
        super(getEnchantmentName(enchantmentInstance).getVisualOrderText(), enchantmentInstance);
    }

    public static MutableComponent getEnchantmentName(EnchantmentInstance enchantmentInstance) {
        MutableComponent enchantmentName = EnchantmentNameTooltip.getEnchantmentName(enchantmentInstance);
        if (enchantmentInstance.enchantment.isCurse()) return enchantmentName;
        return enchantmentName.withColor(BlueLineTooltip.BLUE_COLOR);
    }
}
