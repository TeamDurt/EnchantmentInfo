package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InfoCollector {
    public static ArrayList<InfoParser> infoParsers = new ArrayList<>(List.of(
            InfoCollector::parseIncompatibleEnchantments,
            InfoCollector::parseEnchantableItems
    ));

    static EnchantmentDataManager enchantmentDataManager = EnchantmentDataManager.getInstance();
    static ModEnchantmentCategoryManager enchantmentCategoryManager = ModEnchantmentCategoryManager.getInstance();

    public static List<HeadGroup.PairGroup> getInfo(List<EnchantmentInstance> enchantmentInstanceList) {
        return simplify(getRawInfo(enchantmentInstanceList));
    }

    public static List<HeadGroup.PairGroup> simplify(List<HeadGroup.PairGroup> groups) {
        //todo grouping logic here

        return groups;
    }

    public static List<HeadGroup.PairGroup> getRawInfo(List<EnchantmentInstance> enchantmentInstanceList) {
        return enchantmentInstanceList
                .stream()
                .map(InfoCollector::getRawInfo)
                .collect(Collectors.toList());
    }

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
        List<Enchantment> incompatibleEnchantments = enchantmentDataManager
                .getIncompatibleEnchantments(enchantmentInstance.enchantment);

        if (incompatibleEnchantments.isEmpty()) return null;

        InfoGroup.IncompatibleEnchantments group = new InfoGroup.IncompatibleEnchantments();
        group.setChildList(incompatibleEnchantments);

        return group;
    }

    private static InfoGroup.Enchantables parseEnchantableItems(EnchantmentInstance enchantmentInstance) {
        Enchantment enchantment = enchantmentInstance.enchantment;

        List<ModEnchantmentCategory> categories = enchantmentDataManager.getEnchantmentCategories(enchantment);
        List<List<Item>> included = enchantmentDataManager.getIncludedItemGroups(enchantment);
        List<List<Item>> excluded = enchantmentDataManager.getExcludedItemGroups(enchantment);
        if (categories.isEmpty() && included.isEmpty() && excluded.isEmpty()) return null;

        InfoGroup.Enchantables group = new InfoGroup.Enchantables();

        //add allowed categories and items elements
        group.addChild(parseCoolItems(categories, included));
        //add abandoned items from allowed categories
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
            //skipping anything else, breakable basically matches any other category
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

    public interface InfoParser {
        InfoGroup<?> parse(EnchantmentInstance enchantmentInstance);
    }
}
