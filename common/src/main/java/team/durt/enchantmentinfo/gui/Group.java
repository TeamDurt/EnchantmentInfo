package team.durt.enchantmentinfo.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.tooltip.LineGroupTooltip;
import team.durt.enchantmentinfo.gui.tooltip.Parent;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;
import team.durt.enchantmentinfo.gui.tooltip.SwitcherTooltip;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public static class HeadGroup extends Group {
        List<EnchantmentInstance> enchantments;

        public HeadGroup(EnchantmentInstance... enchantments) {
            this.enchantments = List.of(enchantments);
        }
    }

    public static class PairGroup extends Group {
        Group head;

        InfoGroup.All tail;
        public PairGroup(Group head, InfoGroup.All tail) {
            this.head = head;
            this.tail = tail;
        }
    }

    public static class InfoGroup<T> implements Parent<T>, InfoHolder {
        List<T> content = new ArrayList<>();

        @Override
        public ClientTooltipComponent toTooltip() {
            return null; //todo some logic here ?
        }

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

        //todo make info groups scalable via optional parenting and etc

        public static class All extends InfoGroup<InfoGroup<?>> {
            @Override
            public ParentTooltip toTooltip() {
                ParentTooltip parent = new ParentTooltip();
                for (InfoGroup<?> group : content) {
                    parent.addChild(group.toTooltip());
                }
                return parent;
            }
        }

        public static class IncompatibleEnchantments extends InfoGroup<Enchantment> {
            @Override
            public LineGroupTooltip toTooltip() {
                return TooltipBuilder.parseIncompatibleEnchantments(this);
            }
        }

        public static class Enchantables extends InfoGroup<InfoGroup<?>> {
            @Override
            public ParentTooltip toTooltip() {
                return TooltipBuilder.parseEnchantables(this);
//                ParentTooltip parent = new ParentTooltip();
//                for (InfoGroup<?> group : content) {
//                    parent.addChild(group.toTooltip());
//                }
//                return parent;
            } //copy from All, todo simple ?
        }

        public static class CoolItems extends InfoGroup<InfoGroup<?>> {
            @Override
            public LineGroupTooltip toTooltip() {
                return TooltipBuilder.parseCoolItems(this);
            }
        }

        public static class NotCoolItems extends InfoGroup<InfoGroup<?>> {
            @Override
            public LineGroupTooltip toTooltip() {
                return TooltipBuilder.parseNotCoolItems(this);
            }
        }

        public static class Categories extends InfoGroup<ModEnchantmentCategory> {
            @Override
            public ParentTooltip toTooltip() {
                return TooltipBuilder.parseCategories(this);
            }
        }

        public static class ItemGroups extends InfoGroup<InfoGroup.Items> {
            @Override
            public ParentTooltip toTooltip() {
                return TooltipBuilder.parseItemGroups(this);
            }
        }

        public static class Items extends InfoGroup<Item> {
            @Override
            public SwitcherTooltip toTooltip() {
                return TooltipBuilder.parseItemGroup(this);
            }
        }

    }
    public interface InfoHolder {
        ClientTooltipComponent toTooltip();
    }
}
