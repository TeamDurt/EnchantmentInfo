package team.durt.enchantmentinfo.api.category;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.HashSet;
import java.util.Set;

public class ModEnchantmentCategoryManager {
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
        return Set.copyOf(categories);
    }

    public ModEnchantmentCategory getCategory(String name) {
        return categories.stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void populateCategories() {
        /* Vanilla categories */
        addCategory(new ModEnchantmentCategory("armor_feet", EnchantmentCategory.ARMOR_FEET::canEnchant));
        addCategory(new ModEnchantmentCategory("armor_legs", EnchantmentCategory.ARMOR_LEGS::canEnchant));
        addCategory(new ModEnchantmentCategory("armor_chest", EnchantmentCategory.ARMOR_CHEST::canEnchant));
        addCategory(new ModEnchantmentCategory("armor_head", EnchantmentCategory.ARMOR_HEAD::canEnchant));
        addCategory(new ModEnchantmentCategory("weapon", EnchantmentCategory.WEAPON::canEnchant));
        addCategory(new ModEnchantmentCategory("fishing_rod", EnchantmentCategory.FISHING_ROD::canEnchant));
        addCategory(new ModEnchantmentCategory("trident", EnchantmentCategory.TRIDENT::canEnchant));
        addCategory(new ModEnchantmentCategory("breakable", EnchantmentCategory.BREAKABLE::canEnchant));
        addCategory(new ModEnchantmentCategory("bow", EnchantmentCategory.BOW::canEnchant));
        addCategory(new ModEnchantmentCategory("crossbow", EnchantmentCategory.CROSSBOW::canEnchant));

        /* Custom categories */
        addCategory(new ModEnchantmentCategory("pickaxe", item -> item instanceof PickaxeItem));
        addCategory(new ModEnchantmentCategory("axe", item -> item instanceof AxeItem));
        addCategory(new ModEnchantmentCategory("shovel", item -> item instanceof ShovelItem));
        addCategory(new ModEnchantmentCategory("hoe", item -> item instanceof HoeItem));
    }
}