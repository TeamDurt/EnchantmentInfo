package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.api.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.api.enchantment_data.EnchantmentDataManager;
import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;

import java.util.*;
import java.util.stream.Collectors;

public class InfoCollector {
    /**
     * This List used to be called in {@link #getRawInfo(List)}.
     * It contains {@link InfoParser InfoParsers} with single method {@link InfoParser#parse(EnchantmentInstance)}
     * that is called for every entry in for loop
     *
     * @see #getRawInfo(EnchantmentInstance)
     * @see InfoParser
     */
    public static ArrayList<InfoParser> infoParsers = new ArrayList<>(List.of(
            InfoCollector::parseIncompatibleEnchantments,
            InfoCollector::parseEnchantableItems
    ));

    static EnchantmentDataManager enchantmentDataManager = EnchantmentDataManager.getInstance();
    static ModEnchantmentCategoryManager enchantmentCategoryManager = ModEnchantmentCategoryManager.getInstance();

    /**
     * Returns List of {@link HeadGroup.PairGroup} in simple form where all similar {@link InfoGroup info} combined.
     *
     * @see HeadGroup.PairGroup
     * @see InfoGroup
     * @see #getRawInfo(EnchantmentInstance)
     * @see #simplify(List)
     */
    public static List<HeadGroup.PairGroup> getInfo(List<EnchantmentInstance> enchantmentInstanceList) {
        return simplify(getRawInfo(enchantmentInstanceList));
    }

    /**
     * Takes List of Raw {@link HeadGroup.PairGroup Pair Groups}
     * and combines all similar {@link InfoGroup Info} together so result looks more simple
     *
     * @see #getRawInfo(EnchantmentInstance)
     * @see HeadGroup.PairGroup
     * @see InfoGroup
     */
    public static List<HeadGroup.PairGroup> simplify(List<HeadGroup.PairGroup> groups) {
        return Grouper.group(groups);
    }

    public static List<HeadGroup.PairGroup> getRawInfo(List<EnchantmentInstance> enchantmentInstanceList) {
        return enchantmentInstanceList
                .stream()
                .map(InfoCollector::getRawInfo)
                .collect(Collectors.toList());
    }

    /**
     * Returns one {@link HeadGroup.PairGroup} for given {@link EnchantmentInstance}.
     * Raw Info basically means that returned {@link HeadGroup.PairGroup} is simplest {@link HeadGroup.PairGroup}
     * that can be gotten from this {@link EnchantmentInstance}, It only contains single Enchantment as {@link HeadGroup.HeadEnchantmentsGroup Head} and
     * all {@link InfoGroup.All Info} for given {@link EnchantmentInstance}.
     *
     * @see HeadGroup.PairGroup
     * @see HeadGroup.HeadEnchantmentsGroup
     * @see InfoGroup
     * @see #simplify(List)
     */
    public static HeadGroup.PairGroup getRawInfo(EnchantmentInstance enchantmentInstance) {
        HeadGroup.HeadEnchantmentsGroup headEnchantments = new HeadGroup.HeadEnchantmentsGroup(enchantmentInstance);

        InfoGroup.All info = new InfoGroup.All();
        infoParsers.stream()
                .map(parser -> parser.parse(enchantmentInstance))
                .forEach(info::addChild);

        return new HeadGroup.PairGroup(headEnchantments, info);
    }

    private static InfoGroup.IncompatibleEnchantments parseIncompatibleEnchantments(
            EnchantmentInstance enchantmentInstance
    ) {
        List<Enchantment> incompatibleEnchantments = new ArrayList<>(
                enchantmentDataManager.getIncompatibleEnchantments(enchantmentInstance.enchantment)
        );

        if (incompatibleEnchantments.isEmpty()) return null;

        InfoGroup.IncompatibleEnchantments group = new InfoGroup.IncompatibleEnchantments();
        group.setChildList(incompatibleEnchantments);

        return group;
    }

    private static InfoGroup.Enchantables parseEnchantableItems(EnchantmentInstance enchantmentInstance) {
        Enchantment enchantment = enchantmentInstance.enchantment;

        List<ModEnchantmentCategory> categories = new ArrayList<>(enchantmentDataManager.getEnchantmentCategories(enchantment));
        List<List<Item>> included = enchantmentDataManager.getIncludedItemGroups(enchantment)
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList()); // mutable copies
        List<List<Item>> excluded = enchantmentDataManager.getExcludedItemGroups(enchantment).
                stream()
                .map(ArrayList::new)
                .collect(Collectors.toList()); // mutable copies
        if (categories.isEmpty() && included.isEmpty() && excluded.isEmpty()) return null;

        InfoGroup.Enchantables group = new InfoGroup.Enchantables();

        // add allowed categories and items
        group.addChild(parseCoolItems(categories, included));
        // add abandoned items from allowed categories
        group.addChild(parseNotCoolItems(excluded));

        return group;
    }

    private static InfoGroup.CoolItems parseCoolItems(List<ModEnchantmentCategory> categories, List<List<Item>> included) {
        InfoGroup.CoolItems coolItems = new InfoGroup.CoolItems();
        coolItems.addChild(parseEnchantmentCategories(categories));
        coolItems.addChild(itemGroupsToInfoGroups(included));
        return coolItems;
    }

    private static InfoGroup.NotCoolItems parseNotCoolItems(List<List<Item>> excluded) {
        return (InfoGroup.NotCoolItems) new InfoGroup.NotCoolItems()
                .addChild(itemGroupsToInfoGroups(excluded));
    }

    private static InfoGroup.Categories parseEnchantmentCategories(List<ModEnchantmentCategory> categories) {
        if (categories.isEmpty()) return null;

        InfoGroup.Categories content = new InfoGroup.Categories();

        ModEnchantmentCategory breakable = enchantmentCategoryManager.getCategory("breakable");
        if (categories.contains(breakable)) {
            content.addChild(breakable);
            return content;
            // skipping anything else, breakable basically matches any other category
        }

        content.setChildList(categories);

        return content;
    }

    private static InfoGroup.ItemGroups itemGroupsToInfoGroups(List<List<Item>> itemGroups) {
        if (itemGroups.isEmpty()) return null;

        InfoGroup.ItemGroups content = new InfoGroup.ItemGroups();
        for (List<Item> itemGroup : itemGroups) {
            InfoGroup.Items items = new InfoGroup.Items();
            items.setChildList(itemGroup);
            content.addChild(items);
        }
        return content;
    }

    /**
     * This Interface is used to be added in {@link #infoParsers} list.
     * A single method {@link #parse(EnchantmentInstance)} is called from all parsers in {@link #getRawInfo(EnchantmentInstance)}.
     * The {@link #parse(EnchantmentInstance)} method takes {@link EnchantmentInstance} which is one of enchantments Enchanted Book have,
     * and returns {@link InfoGroup} that is later added to one {@link InfoGroup.All}.
     *
     * @see #infoParsers
     * @see #getRawInfo(EnchantmentInstance)
     * @see #getRawInfo(List)
     * @see InfoGroup
     */
    public interface InfoParser {
        InfoGroup<?> parse(EnchantmentInstance enchantmentInstance);
    }
}
