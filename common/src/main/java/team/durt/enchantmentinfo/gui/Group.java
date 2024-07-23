package team.durt.enchantmentinfo.gui;

import team.durt.enchantmentinfo.gui.tooltip.EnchantmentNameTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ItemTooltip;
import team.durt.enchantmentinfo.gui.tooltip.Parent;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

import java.util.ArrayList;
import java.util.List;

public class Group {
    GroupType type;

    public Group(GroupType type) {
        this.type = type;
    }

    public enum GroupType {
        PAIR,
        HEAD
    }

    public static class HeadGroup extends Group {
        List<EnchantmentNameTooltip> enchantments;

        public HeadGroup(EnchantmentNameTooltip... enchantments) {
            super(GroupType.HEAD);
            this.enchantments = List.of(enchantments);
        }
    }

    public static class PairGroup extends Group {
        Group head;

        InfoGroup.All tail;
        public PairGroup(Group head, InfoGroup.All tail) {
            super(GroupType.PAIR);
            this.head = head;
            this.tail = tail;
        }
    }

    public static class InfoGroup<T> implements Parent<T> {
        Type type;
        List<T> content = new ArrayList<>();

        public InfoGroup(Type type) {
            this.type = type;
        }

        public boolean is(Type type) {
            return this.type == type;
        }

        @Override
        public InfoGroup<T> addChild(T child) {
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

        public enum Type {
            ALL, //INCOMPATIBLE_ENCHANTMENTS + ENCHANTABLES + CATEGORIES
            INCOMPATIBLE_ENCHANTMENTS, //list of EnchantmentNameTooltips
            ENCHANTABLES, //CATEGORIES + (IN+)COMPATIBLE_ITEM_GROUPS
            CATEGORIES, //list of EnchantmentCategoryTooltips
            COMPATIBLE_ITEM_GROUPS, //list of ITEMS
            INCOMPATIBLE_ITEM_GROUPS, //list of ITEMS
            ITEMS; //list of ItemTooltips
        }
        //todo make info groups scalable via optional parenting and etc

        public static class All extends InfoGroup<InfoGroup<?>> {
            private InfoGroup.IncompatibleEnchantments incompatibleEnchantments;
            private InfoGroup.Enchantables enchantables;

            public All() {
                super(Type.ALL);
            }

            public All setIncompatibleEnchantments(IncompatibleEnchantments incompatibleEnchantments) {
                content.remove(this.incompatibleEnchantments);
                this.incompatibleEnchantments = incompatibleEnchantments;
                content.add(this.incompatibleEnchantments);
                return this;
            }

            public IncompatibleEnchantments getIncompatibleEnchantments() {
                return incompatibleEnchantments;
            }

            public All setEnchantables(Enchantables enchantables) {
                content.remove(this.enchantables);
                this.enchantables = enchantables;
                content.add(this.enchantables);
                return this;
            }

            public Enchantables getEnchantables() {
                return enchantables;
            }
        }

        public static class IncompatibleEnchantments extends InfoGroup<EnchantmentNameTooltip> {

            public IncompatibleEnchantments() {
                super(Type.INCOMPATIBLE_ENCHANTMENTS);
            }

            @Override //todo cast everywhere ?
            public IncompatibleEnchantments setChildList(List<EnchantmentNameTooltip> childList) {
                return (IncompatibleEnchantments) super.setChildList(childList);
            }
        }

        public static class Enchantables extends InfoGroup<InfoGroup<?>> {
            private InfoGroup.Categories categories;
            private InfoGroup.CompatibleItemGroups compatibleItemGroups;
            private InfoGroup.IncompatibleItemGroups incompatibleItemGroups;

            public Enchantables() {
                super(Type.ENCHANTABLES);
            }

            public Enchantables setCategories(Categories categories) {
                content.remove(this.categories);
                this.categories = categories;
                content.add(this.categories);
                return this;
            }

            public Categories getCategories() {
                return categories;
            }

            public Enchantables setCompatibleItemGroups(CompatibleItemGroups compatibleItemGroups) {
                content.remove(this.compatibleItemGroups);
                this.compatibleItemGroups = compatibleItemGroups;
                content.add(this.compatibleItemGroups);
                return this;
            }

            public CompatibleItemGroups getCompatibleItemGroups() {
                return compatibleItemGroups;
            }

            public Enchantables setIncompatibleItemGroups(IncompatibleItemGroups incompatibleItemGroups) {
                content.remove(this.incompatibleItemGroups);
                this.incompatibleItemGroups = incompatibleItemGroups;
                content.add(this.incompatibleItemGroups);
                return this;
            }

            public IncompatibleItemGroups getIncompatibleItemGroups() {
                return incompatibleItemGroups;
            }

            @Override //todo cast everywhere ?
            public Enchantables setChildList(List<InfoGroup<?>> childList) {
                return (Enchantables) super.setChildList(childList);
            }
        }

        public static class Categories extends InfoGroup<EnchantmentCategoryTooltip> {

            public Categories() {
                super(Type.CATEGORIES);
            }
        }

        public static class CompatibleItemGroups extends InfoGroup<InfoGroup.Items> {

            public CompatibleItemGroups() {
                super(Type.COMPATIBLE_ITEM_GROUPS);
            }
        }

        public static class IncompatibleItemGroups extends InfoGroup<InfoGroup.Items> {

            public IncompatibleItemGroups() {
                super(Type.INCOMPATIBLE_ITEM_GROUPS);
            }
        }

        public static class Items extends InfoGroup<ItemTooltip> {

            public Items() {
                super(Type.ITEMS);
            }
        }
    }
}
