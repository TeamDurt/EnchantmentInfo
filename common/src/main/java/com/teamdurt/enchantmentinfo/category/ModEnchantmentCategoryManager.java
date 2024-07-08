package com.teamdurt.enchantmentinfo.category;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.*;

public class ModEnchantmentCategoryManager {
    private static final List<EnchantmentCategory> FORBIDDEN_CATEGORIES = ImmutableList.of(EnchantmentCategory.ARMOR, EnchantmentCategory.DIGGER, EnchantmentCategory.WEARABLE, EnchantmentCategory.VANISHABLE);
    private static ModEnchantmentCategoryManager instance;
    private final Set<ModEnchantmentCategory> categories = new HashSet<>();

    private ModEnchantmentCategoryManager() {}

    public static synchronized ModEnchantmentCategoryManager getInstance() {
        if (instance == null) {
            instance = new ModEnchantmentCategoryManager();
        }
        return instance;
    }

    public void addCategory(ModEnchantmentCategory category) {
        categories.add(category);
    }

    public Set<ModEnchantmentCategory> getCategories() {
        return categories;
    }

    public ModEnchantmentCategory getCategory(String name) {
        return categories.stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void populateCategories() {
        Arrays.stream(EnchantmentCategory.values())
                .filter(category -> !FORBIDDEN_CATEGORIES.contains(category))
                .map(vanillaCategory -> new ModEnchantmentCategory(vanillaCategory.name().toLowerCase(), vanillaCategory::canEnchant))
                .forEach(this::addCategory);

        addCategory(new ModEnchantmentCategory("pickaxe", item -> item instanceof PickaxeItem));
        addCategory(new ModEnchantmentCategory("axe", item -> item instanceof AxeItem));
        addCategory(new ModEnchantmentCategory("shovel", item -> item instanceof ShovelItem));
        addCategory(new ModEnchantmentCategory("hoe", item -> item instanceof HoeItem));
    }
}