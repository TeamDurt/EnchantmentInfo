package team.durt.enchantmentinfo.gui.tooltip.texture;

import team.durt.enchantmentinfo.api.category.ModEnchantmentCategory;

public class EnchantmentCategoryTooltip extends TexturedTooltip {
    ModEnchantmentCategory category;

    public EnchantmentCategoryTooltip(ModEnchantmentCategory category) {
        super(category.getTexture(), 16, 16);
        this.category = category;
    }
}
