package team.durt.enchantmentinfo.gui.tooltip.texture;

import team.durt.enchantmentinfo.category.ModEnchantmentCategory;

public class EnchantmentCategoryTooltip extends TexturedTooltip {

    public EnchantmentCategoryTooltip(ModEnchantmentCategory category) {
        super(category.getTexture(), 16, 16);
    }
}
