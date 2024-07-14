package com.teamdurt.enchantmentinfo.frontend.tooltip;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;

public class EnchantmentCategoryTooltip extends TexturedTooltip {

    public EnchantmentCategoryTooltip(ModEnchantmentCategory category) {
        super(category.getTexture(), 16, 16);
    }
}
