package com.teamdurt.enchantmentinfo.enchantment_data;

import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import com.teamdurt.enchantmentinfo.platform.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    private void addIncompatibleEnchantments(Enchantment enchantment, List<Enchantment> incompatibleEnchantments) {
        this.incompatibleEnchantments.put(enchantment, incompatibleEnchantments);
    }

    public List<Enchantment> getIncompatibleEnchantments(Enchantment enchantment) {
        return this.incompatibleEnchantments.get(enchantment);
    }

    private void populateIncompatibleEnchantments() {
        EnchantmentsCompatibilityManager manager = EnchantmentsCompatibilityManager.getInstance();
        for (Enchantment enchantment1 : Services.REGISTRY.getRegisteredEnchantments().toList()) {
            ArrayList<Enchantment> incompatibleEnchantments = new ArrayList<>();
            for (Enchantment enchantment2 : Services.REGISTRY.getRegisteredEnchantments().toList()) {
                if (enchantment1.equals(enchantment2)) continue;
                if (!manager.isCompatible(enchantment1, enchantment2)) incompatibleEnchantments.add(enchantment2);
            }
            addIncompatibleEnchantments(enchantment1, incompatibleEnchantments);
        }
    }

    private void addEnchantmentCategory(Enchantment enchantment, ModEnchantmentCategory category) {
        if (!this.enchantmentCategories.containsKey(enchantment)) {
            this.enchantmentCategories.put(enchantment, new ArrayList<>());
        }
        this.enchantmentCategories.get(enchantment).add(category);
    }

    public List<ModEnchantmentCategory> getEnchantmentCategories(Enchantment enchantment) {
        return this.enchantmentCategories.get(enchantment);
    }

    private void populateEnchantmentCategories() {
        for (Enchantment enchantment : Services.REGISTRY.getRegisteredEnchantments().toList()) {
            for (ModEnchantmentCategory category : ModEnchantmentCategoryManager.getInstance().getCategories()) {
                ArrayList<Item> categoryItems = new ArrayList<>();
                Services.REGISTRY.getRegisteredItems()
                        .filter(category::canEnchant)
                        .forEach(categoryItems::add);
                if (categoryItems.stream().filter(item -> enchantment.canEnchant(new ItemStack(item))).count() > categoryItems.size() / 2) {
                    addEnchantmentCategory(enchantment, category);
                }
            }
        }
    }

    private void addIncludedItemGroups(Enchantment enchantment, List<Item> items) {
        this.enchantmentIncludedItemGroups.put(enchantment, groupItemsByTags(items));
    }

    public List<List<Item>> getIncludedItemGroups(Enchantment enchantment) {
        return this.enchantmentIncludedItemGroups.get(enchantment);
    }

    private void addExcludedItemGroups(Enchantment enchantment, List<Item> items) {
        this.enchantmentExcludedItemGroups.put(enchantment, groupItemsByTags(items));
    }

    public List<List<Item>> getExcludedItemGroups(Enchantment enchantment) {
        return this.enchantmentExcludedItemGroups.get(enchantment);
    }

    private void populateItemGroups() {
        for (Enchantment enchantment : Services.REGISTRY.getRegisteredEnchantments().toList()) {
            ArrayList<Item> includedItems = new ArrayList<>();
            ArrayList<Item> excludedItems = new ArrayList<>();
            Services.REGISTRY.getRegisteredItems()
                    .filter(item -> enchantment.canEnchant(new ItemStack(item)))
                    .filter(item -> getEnchantmentCategories(enchantment).stream().noneMatch(category -> category.canEnchant(item)))
                    .forEach(includedItems::add);
            Services.REGISTRY.getRegisteredItems()
                    .filter(item -> !enchantment.canEnchant(new ItemStack(item)))
                    .filter(item -> getEnchantmentCategories(enchantment).stream().anyMatch(category -> category.canEnchant(item)))
                    .forEach(excludedItems::add);
            addIncludedItemGroups(enchantment, includedItems);
            addExcludedItemGroups(enchantment, excludedItems);
        }
    }

    public void populateData() {
        populateIncompatibleEnchantments();
        populateEnchantmentCategories();
        populateItemGroups();
    }

    public static List<List<Item>> groupItemsByTags(List<Item> items) {
        ArrayList<Item> input = new ArrayList<>(items);
        ArrayList<ArrayList<Item>> groups = new ArrayList<>();
        Services.REGISTRY.getRegisteredItemTags()
                .map(tagKey -> items.stream()
                        .filter(item -> new ItemStack(item).is(tagKey)))
                .filter(stream -> stream.count() > 1)
                .toList()
                .forEach(stream -> groups.add(new ArrayList<>(stream.toList())));
        ArrayList<List<Item>> result = new ArrayList<>();
        while (!groups.isEmpty()) {
            ArrayList<Item> biggestGroup = groups.stream().max(Comparator.comparingInt(List::size)).orElse(null);
            result.add(biggestGroup);
            groups.remove(biggestGroup);
            input.removeAll(biggestGroup);
            groups.forEach(group -> group.removeAll(biggestGroup));
        }
        input.forEach(item -> result.add(Collections.singletonList(item)));
        return result;
    }
}
