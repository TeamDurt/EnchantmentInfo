package team.durt.enchantmentinfo.api.enchantment_data;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.api.compatibility.EnchantmentsCompatibilityManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;

public class EnchantmentDataManager {
    private static EnchantmentDataManager instance;
    private final Map<Holder<Enchantment>, List<Holder<Enchantment>>> incompatibleEnchantments = new HashMap<>();
    private final Map<Holder<Enchantment>, List<ModEnchantmentCategory>> enchantmentCategories = new HashMap<>();
    private final Map<Holder<Enchantment>, List<List<Item>>> enchantmentIncludedItemGroups = new HashMap<>();
    private final Map<Holder<Enchantment>, List<List<Item>>> enchantmentExcludedItemGroups = new HashMap<>();

    private EnchantmentDataManager() {}

    public static synchronized EnchantmentDataManager getInstance() {
        if (instance == null) {
            instance = new EnchantmentDataManager();
        }
        return instance;
    }

    public List<Holder<Enchantment>> getIncompatibleEnchantments(Holder<Enchantment> enchantment) {
        return List.copyOf(incompatibleEnchantments.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<ModEnchantmentCategory> getEnchantmentCategories(Holder<Enchantment> enchantment) {
        return List.copyOf(enchantmentCategories.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<List<Item>> getIncludedItemGroups(Holder<Enchantment> enchantment) {
        return List.copyOf(enchantmentIncludedItemGroups.getOrDefault(enchantment, Collections.emptyList()));
    }

    public List<List<Item>> getExcludedItemGroups(Holder<Enchantment> enchantment) {
        return List.copyOf(enchantmentExcludedItemGroups.getOrDefault(enchantment, Collections.emptyList()));
    }

    public void populateIncompatibleEnchantments(RegistryAccess registryAccess) {
        this.incompatibleEnchantments.clear();
        EnchantmentsCompatibilityManager manager = EnchantmentsCompatibilityManager.getInstance();
        List<Holder.Reference<Enchantment>> enchantments = getEnchantments(registryAccess);
        enchantments.forEach(enchantment1 -> {
            List<Holder<Enchantment>> incompatibleEnchantments = new ArrayList<>();
            enchantments.forEach(enchantment2 -> {
                if (!enchantment1.equals(enchantment2) && !manager.isCompatible(enchantment1, enchantment2)) {
                    incompatibleEnchantments.add(enchantment2);
                }
            });
            this.incompatibleEnchantments.put(enchantment1, incompatibleEnchantments);
        });
    }

    public void populateEnchantmentCategories(RegistryAccess registryAccess) {
        this.enchantmentCategories.clear();
        List<Holder.Reference<Enchantment>> enchantments = getEnchantments(registryAccess);
        Registry<Item> itemRegistry = getItems(registryAccess);
        ModEnchantmentCategoryManager.getInstance().getCategories().forEach(category -> {
            List<Item> categoryItems = itemRegistry.stream()
                    .filter(category::canEnchant)
                    .toList();

            enchantments.forEach(enchantment -> {
                long enchantedItemCount = categoryItems.stream()
                        .filter(item -> enchantment.value().canEnchant(new ItemStack(item)))
                        .count();

                if (enchantedItemCount > categoryItems.size() / 2) {
                    this.enchantmentCategories
                            .computeIfAbsent(enchantment, k -> new ArrayList<>())
                            .add(category);
                }
            });
        });
    }

    public void populateItemGroups(RegistryAccess registryAccess) {
        this.enchantmentIncludedItemGroups.clear();
        this.enchantmentExcludedItemGroups.clear();
        Registry<Item> itemRegistry = getItems(registryAccess);
        List<TagKey<Item>> itemTags = itemRegistry.getTagNames().toList();
        getEnchantments(registryAccess).forEach(enchantment -> {
            List<ModEnchantmentCategory> enchantmentCategories = getEnchantmentCategories(enchantment);
            List<Item> includedItems = new ArrayList<>();
            List<Item> excludedItems = new ArrayList<>();

            itemRegistry.forEach(item -> {
                ItemStack itemStack = new ItemStack(item);
                if (enchantment.value().canEnchant(itemStack)) {
                    if (enchantmentCategories.stream().noneMatch(category -> category.canEnchant(item))) {
                        includedItems.add(item);
                    }
                } else {
                    if (enchantmentCategories.stream().anyMatch(category -> category.canEnchant(item))) {
                        excludedItems.add(item);
                    }
                }
            });

            this.enchantmentIncludedItemGroups.put(enchantment, groupItemsByTags(includedItems, itemTags));
            this.enchantmentExcludedItemGroups.put(enchantment, groupItemsByTags(excludedItems, itemTags));
        });
    }

    public static List<List<Item>> groupItemsByTags(List<Item> items, List<TagKey<Item>> tags) {
        List<Item> input = new ArrayList<>(items);
        List<List<Item>> groups = new ArrayList<>();
        tags.forEach(tagKey -> {
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

    private static List<Holder.Reference<Enchantment>> getEnchantments(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.ENCHANTMENT).holders().toList();
    }

    private static Registry<Item> getItems(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(Registries.ITEM);
    }
}
