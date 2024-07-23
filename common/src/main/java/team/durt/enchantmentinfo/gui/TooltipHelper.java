package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;
import team.durt.enchantmentinfo.gui.tooltip.*;
import team.durt.enchantmentinfo.gui.tooltip.line.ColoredLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.GreenLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.RedLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {
    public static LineGroupTooltip parseIncompatibleEnchantments(InfoGroup.IncompatibleEnchantments infoGroup) {
        if (infoGroup == null) return null;

        List<EnchantmentInstance> instances = new ArrayList<>();
        for (Enchantment enchantment : infoGroup.getChildList()) {
            instances.add(new EnchantmentInstance(enchantment, 0));
        }

        ParentTooltip incompatibleEnchantmentsList = parseEnchantmentNamesList(instances);
        if (incompatibleEnchantmentsList == null) return null;

        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight() - 2);
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

    public static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        return collectChildTooltips(enchantables).setGap(2);
    }

    public static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        if (enchantmentInstances.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip();
        for (EnchantmentInstance instance : enchantmentInstances) {
            parent.addChild(parseEnchantmentName(instance));
        }

        return parent;
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }

    public static LineGroupTooltip parseCoolItems(InfoGroup.CoolItems coolItems) {
        return parseItemsDefinition(coolItems, true);
    }

    public static LineGroupTooltip parseNotCoolItems(InfoGroup.NotCoolItems notCoolItems) {
        return parseItemsDefinition(notCoolItems, false);
    }

    public static LineGroupTooltip parseItemsDefinition(InfoGroup<? extends InfoGroup<?>> definitionGroup, boolean allowed) {
        if (definitionGroup == null) return null;

        ParentTooltip parent = collectChildTooltips(definitionGroup)
                .setOrientation(ParentTooltip.Orientation.HORIZONTAL)
                .setGap(2);

        int height = parent.getHeight();
        ColoredLineTooltip coloredLine = allowed ?
                new GreenLineTooltip(height) :
                new RedLineTooltip(height);

        return new LineGroupTooltip(coloredLine, parent);
    }

    public static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null) return null;

        List<EnchantmentCategoryTooltip> tooltips = new ArrayList<>();
        for (ModEnchantmentCategory category : categories.getChildList()) {
            tooltips.add(new EnchantmentCategoryTooltip(category));
        }

        return new ParentTooltip(tooltips, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    public static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;

        return collectChildTooltips(itemGroups)
                .setOrientation(ParentTooltip.Orientation.HORIZONTAL)
                .setGap(2);
    }

    public static SwitcherTooltip parseItemGroup(InfoGroup.Items items) {
        SwitcherTooltip switcher = new SwitcherTooltip();
        for (Item item : items.getChildList()) {
            switcher.addChild(new ItemTooltip(item));
        }
        return switcher;
    }

    public static ParentTooltip collectChildTooltips(InfoGroup<? extends InfoGroup<?>> infoGroup) {
        ParentTooltip parent = new ParentTooltip();
        for (InfoGroup<?> group : infoGroup.getChildList()) {
            parent.addChild(group.toTooltip());
        }
        return parent;
    }

    public static ParentTooltip parseHeadEnchantmentsGroup(HeadGroup.HeadEnchantmentsGroup headEnchantmentsGroup) {
        return parseEnchantmentNamesList(headEnchantmentsGroup.getEnchantments());
    }
}
