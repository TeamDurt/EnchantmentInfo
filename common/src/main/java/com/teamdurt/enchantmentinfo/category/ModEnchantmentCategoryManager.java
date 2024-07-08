package com.teamdurt.enchantmentinfo.category;

import com.google.common.collect.ImmutableList;
import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.*;

public class ModEnchantmentCategoryManager {
    private static final List<EnchantmentCategory> FORBIDDEN_CATEGORIES = ImmutableList.of(EnchantmentCategory.ARMOR, EnchantmentCategory.DIGGER, EnchantmentCategory.WEARABLE, EnchantmentCategory.VANISHABLE);
    private static ModEnchantmentCategoryManager instance;
    private final HashSet<ModEnchantmentCategory> categories = new HashSet<>();
    private final HashMap<ModEnchantmentCategory, List<Item>> categoryItems = new HashMap<>();

    private ModEnchantmentCategoryManager() {}

    public static synchronized ModEnchantmentCategoryManager getInstance() {
        if (instance == null) {
            instance = new ModEnchantmentCategoryManager();
        }
        return instance;
    }

    public void addCategory(ModEnchantmentCategory category) {
        categories.add(category);
        Services.REGISTRY.getRegisteredItems().forEach(item -> {
            if (category.canEnchant(item)) addItemToCategory(item, category);
        });
    }

    public HashSet<ModEnchantmentCategory> getCategories() {
        return categories;
    }

    public ModEnchantmentCategory getCategory(String name) {
        return categories.stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void addItemToCategory(Item item, ModEnchantmentCategory category) {
        if (!categoryItems.containsKey(category)) {
            categoryItems.put(category, new ArrayList<>(Collections.singletonList(item)));
        } else {
            categoryItems.get(category).add(item);
        }
    }

    public List<Item> getCategoryItems(ModEnchantmentCategory category) {
        return categoryItems.get(category);
    }

    public void populateCategories() {
        Arrays.stream(EnchantmentCategory.values())
                .filter(category -> !FORBIDDEN_CATEGORIES.contains(category))
                .forEach(vanillaCategory -> {
                    ModEnchantmentCategory category = new ModEnchantmentCategory(vanillaCategory.name().toLowerCase(), vanillaCategory::canEnchant);
                    addCategory(category);
                });
        addCategory(new ModEnchantmentCategory("pickaxe", item -> item instanceof PickaxeItem));
        addCategory(new ModEnchantmentCategory("axe", item -> item instanceof AxeItem));
        addCategory(new ModEnchantmentCategory("shovel", item -> item instanceof ShovelItem));
        addCategory(new ModEnchantmentCategory("hoe", item -> item instanceof HoeItem));
    }
}
