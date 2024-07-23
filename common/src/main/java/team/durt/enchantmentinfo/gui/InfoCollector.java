package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.apache.commons.compress.utils.Lists;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import team.durt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import team.durt.enchantmentinfo.gui.tooltip.EnchantmentNameTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ItemTooltip;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

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
                new HeadGroup(parseEnchantmentName(enchantmentInstance)),
                new InfoGroup.All()
                        .setIncompatibleEnchantments(incompatibleEnchantments)
                        .setEnchantables(enchantables)
        );
    }


    private static EnchantmentNameTooltip parseEnchantmentName(Enchantment enchantment) {
        return new EnchantmentNameTooltip(enchantment);
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }

    private static InfoGroup.IncompatibleEnchantments parseIncompatibleEnchantments(Enchantment enchantment) {
        List<Enchantment> incompatibleEnchantments = enchantmentDataManager.getIncompatibleEnchantments(enchantment);
        if (incompatibleEnchantments.isEmpty()) return null;

        InfoGroup.IncompatibleEnchantments group = new InfoGroup.IncompatibleEnchantments();

        for (Enchantment incompatibleEnchantment : incompatibleEnchantments) {
            group.addChild(
                    parseEnchantmentName(incompatibleEnchantment)
            );
        }

        return group;
    }

    private static InfoGroup.Enchantables parseEnchantableItems(Enchantment enchantment) {
        List<ModEnchantmentCategory> categories = enchantmentDataManager.getEnchantmentCategories(enchantment);
        List<List<Item>> included = enchantmentDataManager.getIncludedItemGroups(enchantment);
        List<List<Item>> excluded = enchantmentDataManager.getExcludedItemGroups(enchantment);
        if (categories.isEmpty() && included.isEmpty() && excluded.isEmpty()) return null;

        InfoGroup.Enchantables group = new InfoGroup.Enchantables();

        //add allowed categories and items elements
        group.setCategories(parseEnchantmentCategories(categories));
        group.setCompatibleItemGroups((InfoGroup.CompatibleItemGroups) itemGroupsToTooltip(included, true));
        //add abandoned items from allowed categories
        group.setIncompatibleItemGroups((InfoGroup.IncompatibleItemGroups) itemGroupsToTooltip(excluded, false));

        return group;
    }

    private static InfoGroup.Categories parseEnchantmentCategories(List<ModEnchantmentCategory> categories) {
        if (categories.isEmpty()) return null;

        InfoGroup.Categories content = new InfoGroup.Categories();

        ModEnchantmentCategory breakable = enchantmentCategoryManager.getCategory("breakable");
        if (categories.contains(breakable)) {
            content.addChild(new EnchantmentCategoryTooltip(breakable));
            return content;
            //skipping anything else, breakable basically matches any other category
        }

        for (ModEnchantmentCategory category : categories) {
            content.addChild(new EnchantmentCategoryTooltip(category));
        }

        return content;
    }

    private static InfoGroup<InfoGroup.Items> itemGroupsToTooltip(List<List<Item>> itemGroups, boolean compatible) {
        if (itemGroups.isEmpty()) return null;

        InfoGroup<InfoGroup.Items> content =
                compatible ?
                        new InfoGroup.CompatibleItemGroups() :
                        new InfoGroup.IncompatibleItemGroups();
        for (List<Item> itemGroup : itemGroups) {
            InfoGroup.Items items = new InfoGroup.Items();
            for (Item item : itemGroup) {
                items.addChild(new ItemTooltip(item));
            }
            content.addChild(items);
        }
        return content;
    }
}
