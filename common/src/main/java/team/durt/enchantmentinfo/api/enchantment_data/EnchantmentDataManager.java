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
import java.util.function.Function;

/**
 * Collects data of each Enchantment only when it is requested for the first time,
 * since collecting it for all Enchantments at once takes too long.
 */
public class EnchantmentDataManager {
    private static EnchantmentDataManager instance;
    private List<Holder.Reference<Enchantment>> enchantments = List.of();
    private List<Item> items = List.of();
    private List<TagKey<Item>> itemTags = List.of();
    private final Map<ModEnchantmentCategory, List<Item>> categoryItems = new HashMap<>();
    private final Map<Holder<Enchantment>, List<Holder<Enchantment>>> incompatibleEnchantments = new HashMap<>();
    private final Map<Holder<Enchantment>, List<ModEnchantmentCategory>> enchantmentCategories = new HashMap<>();
    private final Map<Holder<Enchantment>, ItemGroups> enchantmentItemGroups = new HashMap<>();

    private EnchantmentDataManager() {}

    public static synchronized EnchantmentDataManager getInstance() {
        if (instance == null) {
            instance = new EnchantmentDataManager();
        }
        return instance;
    }

    public List<Holder<Enchantment>> getIncompatibleEnchantments(Holder<Enchantment> enchantment) {
        return List.copyOf(getOrCollect(incompatibleEnchantments, enchantment, this::collectIncompatibleEnchantments, List.of()));
    }

    public List<ModEnchantmentCategory> getEnchantmentCategories(Holder<Enchantment> enchantment) {
        return List.copyOf(getOrCollect(enchantmentCategories, enchantment, this::collectEnchantmentCategories, List.of()));
    }

    public List<List<Item>> getIncludedItemGroups(Holder<Enchantment> enchantment) {
        return List.copyOf(getItemGroups(enchantment).included());
    }

    public List<List<Item>> getExcludedItemGroups(Holder<Enchantment> enchantment) {
        return List.copyOf(getItemGroups(enchantment).excluded());
    }

    /**
     * Forgets all collected data, so it is collected again from given registries on request.
     */
    public void reload(RegistryAccess registryAccess) {
        Registry<Item> itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
        this.enchantments = registryAccess.registryOrThrow(Registries.ENCHANTMENT).holders().toList();
        this.items = itemRegistry.stream().toList();
        this.itemTags = itemRegistry.getTagNames().toList();
        this.categoryItems.clear();
        this.incompatibleEnchantments.clear();
        this.enchantmentCategories.clear();
        this.enchantmentItemGroups.clear();
    }

    private <T> T getOrCollect(Map<Holder<Enchantment>, T> data, Holder<Enchantment> enchantment, Function<Holder<Enchantment>, T> collector, T empty) {
        T value = data.get(enchantment);
        if (value != null) return value;
        // Enchantment from other registries, nothing to collect for it
        if (!enchantments.contains(enchantment)) return empty;

        value = collector.apply(enchantment);
        data.put(enchantment, value);
        return value;
    }

    private List<Holder<Enchantment>> collectIncompatibleEnchantments(Holder<Enchantment> enchantment1) {
        EnchantmentsCompatibilityManager manager = EnchantmentsCompatibilityManager.getInstance();
        List<Holder<Enchantment>> incompatibleEnchantments = new ArrayList<>();
        enchantments.forEach(enchantment2 -> {
            if (!enchantment1.equals(enchantment2) && !manager.isCompatible(enchantment1, enchantment2)) {
                incompatibleEnchantments.add(enchantment2);
            }
        });
        return incompatibleEnchantments;
    }

    private List<ModEnchantmentCategory> collectEnchantmentCategories(Holder<Enchantment> enchantment) {
        List<ModEnchantmentCategory> enchantmentCategories = new ArrayList<>();
        ModEnchantmentCategoryManager.getInstance().getCategories().forEach(category -> {
            List<Item> categoryItems = getCategoryItems(category);

            long enchantedItemCount = categoryItems.stream()
                    .filter(item -> enchantment.value().canEnchant(new ItemStack(item)))
                    .count();

            if (enchantedItemCount > categoryItems.size() / 2) {
                enchantmentCategories.add(category);
            }
        });
        return enchantmentCategories;
    }

    private List<Item> getCategoryItems(ModEnchantmentCategory category) {
        return categoryItems.computeIfAbsent(category, key -> items.stream()
                .filter(key::canEnchant)
                .toList());
    }

    private ItemGroups getItemGroups(Holder<Enchantment> enchantment) {
        return getOrCollect(enchantmentItemGroups, enchantment, this::collectItemGroups, ItemGroups.EMPTY);
    }

    private ItemGroups collectItemGroups(Holder<Enchantment> enchantment) {
        List<ModEnchantmentCategory> enchantmentCategories = getEnchantmentCategories(enchantment);
        List<Item> includedItems = new ArrayList<>();
        List<Item> excludedItems = new ArrayList<>();

        items.forEach(item -> {
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

        return new ItemGroups(groupItemsByTags(includedItems, itemTags), groupItemsByTags(excludedItems, itemTags));
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

    private record ItemGroups(List<List<Item>> included, List<List<Item>> excluded) {
        static final ItemGroups EMPTY = new ItemGroups(List.of(), List.of());
    }
}
