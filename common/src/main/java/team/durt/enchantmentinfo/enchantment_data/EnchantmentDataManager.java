package team.durt.enchantmentinfo.enchantment_data;

import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.compatibility.EnchantmentsCompatibilityManager;
import team.durt.enchantmentinfo.platform.Services;
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

    public List<Enchantment> getIncompatibleEnchantments(Enchantment enchantment) {
        return List.copyOf(incompatibleEnchantments.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<ModEnchantmentCategory> getEnchantmentCategories(Enchantment enchantment) {
        return List.copyOf(enchantmentCategories.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<List<Item>> getIncludedItemGroups(Enchantment enchantment) {
        return List.copyOf(enchantmentIncludedItemGroups.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<List<Item>> getExcludedItemGroups(Enchantment enchantment) {
        return List.copyOf(enchantmentExcludedItemGroups.getOrDefault(enchantment, Collections.emptyList()));
    }

    public void populateIncompatibleEnchantments() {
        EnchantmentsCompatibilityManager manager = EnchantmentsCompatibilityManager.getInstance();
        Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment1 -> {
            List<Enchantment> incompatibleEnchantments = new ArrayList<>();
            Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment2 -> {
                if (!enchantment1.equals(enchantment2) && !manager.isCompatible(enchantment1, enchantment2)) {
                    incompatibleEnchantments.add(enchantment2);
                }
            });
            this.incompatibleEnchantments.put(enchantment1, incompatibleEnchantments);
        });
    }

    public void populateEnchantmentCategories() {
        Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment -> {
            ModEnchantmentCategoryManager.getInstance().getCategories().forEach(category -> {
                List<Item> categoryItems = Services.REGISTRY.getRegisteredItems()
                        .filter(category::canEnchant)
                        .toList();

                long enchantedItemCount = categoryItems.stream()
                        .filter(item -> enchantment.canEnchant(new ItemStack(item)))
                        .count();

                if (enchantedItemCount > categoryItems.size() / 2) {
                    this.enchantmentCategories
                            .computeIfAbsent(enchantment, k -> new ArrayList<>())
                            .add(category);
                }
            });
        });
    }

    public void populateItemGroups() {
        Services.REGISTRY.getRegisteredEnchantments().forEach(enchantment -> {
            List<ModEnchantmentCategory> enchantmentCategories = getEnchantmentCategories(enchantment);
            List<Item> includedItems = new ArrayList<>();
            List<Item> excludedItems = new ArrayList<>();

            Services.REGISTRY.getRegisteredItems().forEach(item -> {
                ItemStack itemStack = new ItemStack(item);
                if (enchantment.canEnchant(itemStack)) {
                    if (enchantmentCategories.stream().noneMatch(category -> category.canEnchant(item))) {
                        includedItems.add(item);
                    }
                } else {
                    if (enchantmentCategories.stream().anyMatch(category -> category.canEnchant(item))) {
                        excludedItems.add(item);
                    }
                }
            });

            this.enchantmentIncludedItemGroups.put(enchantment, groupItemsByTags(includedItems));
            this.enchantmentExcludedItemGroups.put(enchantment, groupItemsByTags(excludedItems));
        });
    }

    public static List<List<Item>> groupItemsByTags(List<Item> items) {
        List<Item> input = new ArrayList<>(items);
        List<List<Item>> groups = new ArrayList<>();
        Services.REGISTRY.getRegisteredItemTags().forEach(tagKey -> {
            List<Item> taggedItems = items.stream()
                    .filter(item -> new ItemStack(item).is(tagKey))
                    .toList();

            if (taggedItems.size() > 1) {
                groups.add(new ArrayList<>(taggedItems));
            }
        });

        List<List<Item>> result = new ArrayList<>();
        while (!groups.isEmpty()) {
            List<Item> biggestGroup = groups.stream().max(Comparator.comparingInt(List::size)).orElse(null);
            result.add(List.copyOf(biggestGroup));
            groups.remove(biggestGroup);
            input.removeAll(biggestGroup);
            groups.forEach(group -> group.removeAll(biggestGroup));
            groups.removeIf(List::isEmpty);
        }

        input.forEach(item -> result.add(Collections.singletonList(item)));
        return List.copyOf(result);
    }
}
