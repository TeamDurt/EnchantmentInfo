package team.durt.enchantmentinfo.gui.group;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.InfoCollector;
import team.durt.enchantmentinfo.gui.Parent;
import team.durt.enchantmentinfo.gui.TooltipHelper;
import team.durt.enchantmentinfo.gui.tooltip.LineGroupTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;
import team.durt.enchantmentinfo.gui.tooltip.SwitcherTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to store information for further transformation to tooltips.
 *
 * @see InfoHolder#toTooltip()
 * @see InfoCollector#getInfo(List)
 */
public abstract class InfoGroup<T> implements Parent<T>, InfoHolder {
    /**
     * List of stored information.
     */
    List<T> content = new ArrayList<>();

    /**
     * Removes from content List entries that are contained is target's content List.
     * Intended to work with {@link InfoGroup InfoGroups} as members of content Lists too.
     *
     * @param infoToExtract Group which content will be removed from this group.
     */
    @SuppressWarnings("unchecked")
    public <R> void extract(InfoGroup<T> infoToExtract) {
        for (T entry : infoToExtract.getChildList()) {
            if (entry instanceof InfoGroup<?> g) {
                // casting for compiler to know that entryGroup and group are of the exact same class,
                // we can assert that they are from getGroupWithSameClass()
                InfoGroup<R> entryGroup = (InfoGroup<R>) g;
                InfoGroup<R> group = (InfoGroup<R>) getGroupWithSameClass(this.getChildList(), entryGroup);
                if (group == null) continue;
                // giving extract() method object of same class as group
                group.extract(entryGroup);
                if (group.isEmpty()) {
                    // we can assert that group is instance of T from getGroupWithSameClass()
                    this.getChildList().remove((T) group);
                }
            } else {
                this.getChildList().remove(entry);
            }
        }
    }

    /**
     * Should return new InfoGroup of this subclass
     * that contains info dedicated for both this group and given one
     *
     * @param info another instance of this subclass,
     *             may be not, but unexpected behavior will appear
     * @see #getSimilarContent(List)
     */
    public abstract InfoGroup<T> getSimilar(InfoGroup<T> info);

    /**
     * Returns List that contains every member of given List that is contained in this object's content.
     * Intended to be used in {@link #getSimilar(InfoGroup)}.
     */
    public List<T> getSimilarContent(List<T> info) {
        return info.stream()
                .filter(entry -> content.contains(entry))
                .collect(Collectors.toList());
    }

    /**
     * Returns member from given List that is of the exact same class as given InfoGroup.
     * Note that calculations are depend on {@link Object#getClass()} and {@link Object#equals(Object)} methods.
     *
     * @param groupsList   List of InfoGroups that may contain group of class same as classExample
     * @param classExample Instance of InfoGroup subclass needed to be compared with groupsList
     * @return First member of groupsList that is of the exact same class as classExample, or null if not found.
     */
    private static InfoGroup<?> getGroupWithSameClass(List<?> groupsList, InfoGroup<?> classExample) {
        for (Object o : groupsList) { // Object instead of casting list
            if (o instanceof InfoGroup<?> g) {
                if (g.getClass().equals(classExample.getClass())) {
                    return g;
                }
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.getChildList().isEmpty();
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

    public abstract static class InfoGroupsContainer extends InfoGroup<InfoGroup<?>> {
        /**
         * Same as its super method, but fixed to work with {@link InfoGroup InfoGroups} as content members.
         *
         * @see #getSimilarContentInternal(List)
         */
        @Override
        public List<InfoGroup<?>> getSimilarContent(List<InfoGroup<?>> infoFromSameGroup) {
            // separate method for introducing type parameter
            return getSimilarContentInternal(infoFromSameGroup);
        }

        @SuppressWarnings("unchecked")
        private <R> List<InfoGroup<?>> getSimilarContentInternal(List<InfoGroup<?>> infoFromSameGroup) {
            List<InfoGroup<?>> groups = new ArrayList<>();
            for (InfoGroup<?> g : infoFromSameGroup) {
                InfoGroup<R> group = (InfoGroup<R>) g;
                InfoGroup<R> ourInfo = (InfoGroup<R>) getGroupWithSameClass(this.getChildList(), group);
                // casting for compiler to know that group and ourInfo are of the exact same class,
                // we can assert that they are from getGroupWithSameClass()
                if (ourInfo == null) continue;
                // giving getSimilar() method object of same class as ourInfo
                InfoGroup<R> subSimilar = ourInfo.getSimilar(group);
                if (subSimilar.isEmpty()) continue;
                groups.add(subSimilar);
            }
            return groups;
        }
    }

    public static class All extends InfoGroupsContainer {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.collectChildTooltips(this);
        }

        @Override
        public All getSimilar(InfoGroup<InfoGroup<?>> info) {
            return (All) new All().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class IncompatibleEnchantments extends InfoGroup<Enchantment> {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseIncompatibleEnchantments(this);
        }

        @Override
        public IncompatibleEnchantments getSimilar(InfoGroup<Enchantment> info) {
            return (IncompatibleEnchantments) new IncompatibleEnchantments().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class Enchantables extends InfoGroupsContainer {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseEnchantables(this);
        }

        @Override
        public Enchantables getSimilar(InfoGroup<InfoGroup<?>> info) {
            return (Enchantables) new Enchantables().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class CoolItems extends InfoGroupsContainer {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseCoolItems(this);
        }

        @Override
        public CoolItems getSimilar(InfoGroup<InfoGroup<?>> info) {
            return (CoolItems) new CoolItems().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class NotCoolItems extends InfoGroupsContainer {
        @Override
        public LineGroupTooltip toTooltip() {
            return TooltipHelper.parseNotCoolItems(this);
        }

        @Override
        public NotCoolItems getSimilar(InfoGroup<InfoGroup<?>> info) {
            return (NotCoolItems) new NotCoolItems().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class Categories extends InfoGroup<ModEnchantmentCategory> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseCategories(this);
        }

        @Override
        public Categories getSimilar(InfoGroup<ModEnchantmentCategory> info) {
            return (Categories) new Categories().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }

    public static class ItemGroups extends InfoGroup<Items> {
        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseItemGroups(this);
        }

        @Override
        public ItemGroups getSimilar(InfoGroup<Items> info) {
            return (ItemGroups) new ItemGroups().setChildList(this.getSimilarContent(info.getChildList()));
        }

        @Override
        public List<Items> getSimilarContent(List<Items> info) {
            List<Items> similarContent = new ArrayList<>();
            for (Items our : this.getChildList()) {
                Items similar = new Items();
                for (Items their : info) {
                    Items subSimilar = our.getSimilar(their);
                    if (!subSimilar.isEmpty()) {
                        subSimilar.getChildList().forEach(similar::addChild);
                    }
                }
                if (!similar.isEmpty()) {
                    similarContent.add(similar);
                }
            }
            return similarContent;
        }
    }

    public static class Items extends InfoGroup<Item> {
        @Override
        public SwitcherTooltip toTooltip() {
            return TooltipHelper.parseItemGroup(this);
        }

        @Override
        public Items getSimilar(InfoGroup<Item> info) {
            return (Items) new Items().setChildList(this.getSimilarContent(info.getChildList()));
        }
    }
}
