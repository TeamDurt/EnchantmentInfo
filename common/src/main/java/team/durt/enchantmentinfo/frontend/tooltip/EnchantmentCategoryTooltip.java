package team.durt.enchantmentinfo.frontend.tooltip;

import team.durt.enchantmentinfo.category.ModEnchantmentCategory;

public class EnchantmentCategoryTooltip extends TexturedTooltip {

    public EnchantmentCategoryTooltip(ModEnchantmentCategory category) {
        super(category.getTexture(), 16, 16);
    }
}
