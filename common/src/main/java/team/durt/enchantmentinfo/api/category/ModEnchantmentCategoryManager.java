package team.durt.enchantmentinfo.api.category;

import net.minecraft.tags.ItemTags;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModEnchantmentCategoryManager {
    private static ModEnchantmentCategoryManager instance;
    private final Set<ModEnchantmentCategory> categories = new LinkedHashSet<>();

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
        return Collections.unmodifiableSet(new LinkedHashSet<>(categories));
    }

    public ModEnchantmentCategory getCategory(String name) {
        return categories.stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void populateCategories() {
        /* Vanilla categories */
        addCategory(new ModEnchantmentCategory("armor_head", ItemTags.HEAD_ARMOR_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("armor_chest", ItemTags.CHEST_ARMOR_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("armor_legs", ItemTags.LEG_ARMOR_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("armor_feet", ItemTags.FOOT_ARMOR_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("weapon", ItemTags.SWORD_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("fishing_rod", ItemTags.FISHING_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("trident", ItemTags.TRIDENT_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("breakable", ItemTags.DURABILITY_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("bow", ItemTags.BOW_ENCHANTABLE));
        addCategory(new ModEnchantmentCategory("crossbow", ItemTags.CROSSBOW_ENCHANTABLE));

        /* Custom categories */
        addCategory(new ModEnchantmentCategory("pickaxe", ItemTags.PICKAXES));
        addCategory(new ModEnchantmentCategory("axe", ItemTags.AXES));
        addCategory(new ModEnchantmentCategory("shovel", ItemTags.SHOVELS));
        addCategory(new ModEnchantmentCategory("hoe", ItemTags.HOES));
    }
}