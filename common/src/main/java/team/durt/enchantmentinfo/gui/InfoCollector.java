package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;

import java.util.ArrayList;
import java.util.List;

import static team.durt.enchantmentinfo.gui.Group.*;

public class InfoCollector {
    static EnchantmentDataManager enchantmentDataManager = EnchantmentDataManager.getInstance();
    static ModEnchantmentCategoryManager enchantmentCategoryManager = ModEnchantmentCategoryManager.getInstance();

    public static List<PairGroup> simplify(List<PairGroup> groups) {
        //todo grouping logic here

        return groups;
    }

    public static List<PairGroup> getGroupedInfo(List<EnchantmentInstance> enchantmentInstanceList) {
        List<PairGroup> groups = new ArrayList<>();
        for (EnchantmentInstance enchantmentInstance : enchantmentInstanceList) {
            groups.add(getGroupedInfo(enchantmentInstance));
        }
        return groups;
    }

    public static PairGroup getGroupedInfo(EnchantmentInstance enchantmentInstance) {
        Enchantment enchantment = enchantmentInstance.enchantment;
        InfoGroup.IncompatibleEnchantments incompatibleEnchantments = parseIncompatibleEnchantments(enchantment);
        InfoGroup.Enchantables enchantables = parseEnchantableItems(enchantment);
        return new PairGroup(
                new HeadGroup(enchantmentInstance),
                (InfoGroup.All) new InfoGroup.All() //todo cast = not cool
                        .addChild(incompatibleEnchantments)
                        .addChild(enchantables)
        );
    }

    private static InfoGroup.IncompatibleEnchantments parseIncompatibleEnchantments(Enchantment enchantment) {
        List<Enchantment> incompatibleEnchantments = enchantmentDataManager.getIncompatibleEnchantments(enchantment);
        if (incompatibleEnchantments.isEmpty()) return null;

        InfoGroup.IncompatibleEnchantments group = new InfoGroup.IncompatibleEnchantments();
        group.setChildList(incompatibleEnchantments);

        return group;
    }

    private static InfoGroup.Enchantables parseEnchantableItems(Enchantment enchantment) {
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
}
