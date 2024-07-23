package team.durt.enchantmentinfo.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.Group.InfoGroup;
import team.durt.enchantmentinfo.gui.tooltip.*;
import team.durt.enchantmentinfo.gui.tooltip.line.GreenLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.RedLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {
    public static LineGroupTooltip parseIncompatibleEnchantments(InfoGroup.IncompatibleEnchantments infoGroup) {
        if (infoGroup == null) return null;

        List<EnchantmentInstance> instances = new ArrayList<>();
        for (Enchantment enchantment : infoGroup.content) {
            instances.add(new EnchantmentInstance(enchantment, 0));
        }

        ParentTooltip incompatibleEnchantmentsList = parseEnchantmentNamesList(instances);
        if (incompatibleEnchantmentsList == null) return null;

        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight() - 2);
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

    public static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.VERTICAL, 2);
        for (InfoGroup<?> group : enchantables.content) {
            parent.addChild(group.toTooltip());
        }

        return parent;
    }

    public static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        if (enchantmentInstances.isEmpty()) return null;
        List<EnchantmentNameTooltip> tooltips = new ArrayList<>();
        for (EnchantmentInstance instance : enchantmentInstances) {
            tooltips.add(parseEnchantmentName(instance));
        }
        return new ParentTooltip(tooltips, ParentTooltip.Orientation.VERTICAL, 0);
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }

    public static LineGroupTooltip parseCoolItems(InfoGroup.CoolItems coolItems) {
        if (coolItems == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup<?> group : coolItems.content) {
            parent.addChild(group.toTooltip());
        }

        GreenLineTooltip greenLine = new GreenLineTooltip(parent.getHeight());
        return new LineGroupTooltip(greenLine, parent);
    }

    //todo ⬆ these are similar ⬇

    public static LineGroupTooltip parseNotCoolItems(InfoGroup.NotCoolItems notCoolItems) {
        if (notCoolItems == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup<?> group : notCoolItems.content) {
            parent.addChild(group.toTooltip());
        }

        RedLineTooltip redLine = new RedLineTooltip(parent.getHeight());
        return new LineGroupTooltip(redLine, parent);
    }

    public static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null) return null;

        List<EnchantmentCategoryTooltip> tooltips = new ArrayList<>();
        for (ModEnchantmentCategory category : categories.content) {
            tooltips.add(new EnchantmentCategoryTooltip(category));
        }

        return new ParentTooltip(tooltips, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    public static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup.Items items : itemGroups.content) {
            parent.addChild(items.toTooltip());
        }

        return parent;
    }

    public static SwitcherTooltip parseItemGroup(InfoGroup.Items items) {
        SwitcherTooltip switcher = new SwitcherTooltip();
        for (Item item : items.content) {
            switcher.addChild(new ItemTooltip(item));
        }
        return switcher;
    }
}
