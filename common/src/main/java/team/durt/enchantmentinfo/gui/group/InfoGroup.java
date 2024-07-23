package team.durt.enchantmentinfo.gui.group;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.Parent;
import team.durt.enchantmentinfo.gui.TooltipHelper;
import team.durt.enchantmentinfo.gui.tooltip.LineGroupTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;
import team.durt.enchantmentinfo.gui.tooltip.SwitcherTooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class InfoGroup<T> implements Parent<T>, InfoHolder {
    List<T> content = new ArrayList<>();

    @Override
    public InfoGroup<T> addChild(T child) {
        if (child == null) return this;
        content.add(child);
        return this;
    }

    @Override
    public InfoGroup<T> setChildList(List<T> childList) {
        content = childList;
        return this;
    }

    @Override
    public List<T> getChildList() {
        return content;
    }

    public static class All extends InfoGroup<InfoGroup<?>> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.collectChildTooltips(this);
        }
    }

    public static class IncompatibleEnchantments extends InfoGroup<Enchantment> {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseIncompatibleEnchantments(this);
        }
    }

    public static class Enchantables extends InfoGroup<InfoGroup<?>> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseEnchantables(this);
        }
    }

    public static class CoolItems extends InfoGroup<InfoGroup<?>> {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseCoolItems(this);
        }
    }

    public static class NotCoolItems extends InfoGroup<InfoGroup<?>> {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseNotCoolItems(this);
        }
    }

    public static class Categories extends InfoGroup<ModEnchantmentCategory> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseCategories(this);
        }
    }

    public static class ItemGroups extends InfoGroup<InfoGroup.Items> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseItemGroups(this);
        }
    }

    public static class Items extends InfoGroup<Item> {
        @Override
        public SwitcherTooltip toTooltip() {
            return TooltipHelper.parseItemGroup(this);
        }
    }
}
