package com.teamdurt.enchantmentinfo.enchantment_data;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;

public class EnchantmentDataManager {
    private static EnchantmentDataManager instance;
    private final Map<Enchantment, List<Enchantment>> incompatibleEnchantments = new HashMap<>();
    private final Map<Enchantment, List<ModEnchantmentCategory>> enchantmentCategories = new HashMap<>();
    private final Map<Enchantment, List<List<Item>>> enchantmentIncludedItemGroups = new HashMap<>();
    private final Map<Enchantment, List<List<Item>>> enchantmentExcludedItemGroups = new HashMap<>();

    private EnchantmentDataManager() {}

    public static synchronized EnchantmentDataManager getInstance() {
        if (instance == null) {
            instance = new EnchantmentDataManager();
        }
        return instance;
    }

    public void addIncompatibleEnchantments(Enchantment enchantment, List<Enchantment> incompatibleEnchantments) {
        this.incompatibleEnchantments.put(enchantment, incompatibleEnchantments);
    }

    public List<Enchantment> getIncompatibleEnchantments(Enchantment enchantment) {
        return this.incompatibleEnchantments.get(enchantment);
    }

    private void populateIncompatibleEnchantments() {
        EnchantmentsCompatibilityManager manager = EnchantmentsCompatibilityManager.getInstance();
        for (Enchantment enchantment1 : BuiltInRegistries.ENCHANTMENT.stream().toList()) {
            ArrayList<Enchantment> incompatibleEnchantments = new ArrayList<>();
            for (Enchantment enchantment2 : BuiltInRegistries.ENCHANTMENT.stream().toList()) {
                if (enchantment1.equals(enchantment2)) continue;
                if (!manager.isCompatible(enchantment1, enchantment2)) incompatibleEnchantments.add(enchantment2);
            }
            addIncompatibleEnchantments(enchantment1, incompatibleEnchantments);
        }
    }

    public void addEnchantmentCategory(Enchantment enchantment, ModEnchantmentCategory category) {
        if (!this.enchantmentCategories.containsKey(enchantment)) {
            this.enchantmentCategories.put(enchantment, new ArrayList<>());
        }
        this.enchantmentCategories.get(enchantment).add(category);
    }

    public List<ModEnchantmentCategory> getEnchantmentCategories(Enchantment enchantment) {
        return this.enchantmentCategories.get(enchantment);
    }

    private void populateEnchantmentCategories() {
        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT.stream().toList()) {
            for (ModEnchantmentCategory category : ModEnchantmentCategoryManager.getInstance().getCategories()) {
                ArrayList<Item> categoryItems = new ArrayList<>();
                BuiltInRegistries.ITEM.stream()
                        .filter(category::canEnchant)
                        .forEach(categoryItems::add);
                if (categoryItems.stream().filter(item -> enchantment.canEnchant(new ItemStack(item))).count() > categoryItems.size() / 2) {
                    addEnchantmentCategory(enchantment, category);
                }
            }
        }
    }

    private List<List<Item>> groupItemsByTags(List<Item> items) {
        ArrayList<ArrayList<Item>> groups = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagNames()
                .map(tagKey -> items.stream()
                                .filter(item -> new ItemStack(item).is(tagKey)))
                .filter(stream -> stream.count() > 1)
                .toList()
                .forEach(stream -> groups.add(new ArrayList<>(stream.toList())));
        return new ArrayList<>();
    }

    public void populateData() {
        populateIncompatibleEnchantments();
        populateEnchantmentCategories();
    }
}
