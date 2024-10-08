package team.durt.enchantmentinfo.gui.group;

import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.gui.TooltipHelper;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;

import java.util.List;

public abstract class HeadGroup implements InfoHolder {
    public boolean shouldBeHighlightedInTooltip() {
        return true;
    }

    /**
     * Contains List of {@link EnchantmentInstance} and used to be stored in {@link PairGroup}.
     *
     * @see PairGroup
     * @see team.durt.enchantmentinfo.gui.InfoCollector#getRawInfo(EnchantmentInstance)
     */
    public static class HeadEnchantmentsGroup extends HeadGroup {
        List<EnchantmentInstance> enchantments;

        public HeadEnchantmentsGroup(EnchantmentInstance... enchantments) {
            this.enchantments = List.of(enchantments);
        }

        public List<EnchantmentInstance> getEnchantments() {
            return enchantments;
        }

        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.parseHeadEnchantmentsGroup(this);
        }

        @Override
        public boolean shouldBeHighlightedInTooltip() {
            return enchantments.size() > 1;
        }
    }

    /**
     * Contains a Pair of Head and Tail Groups.
     * Head Group can be either another {@link PairGroup} or {@link HeadEnchantmentsGroup}.
     * Tail Group is {@link InfoGroup.All} that contains Information about all {@link HeadEnchantmentsGroup HeadEnchantmentsGroups} in Head Group,
     * it means that this info is valid for all {@link HeadEnchantmentsGroup HeadEnchantmentsGroups} that can be found
     * going through all {@link PairGroup PairGroups} in Head Group.
     *
     * @see HeadEnchantmentsGroup
     * @see team.durt.enchantmentinfo.gui.InfoCollector#getInfo(List)
     */
    public static class PairGroup extends HeadGroup {
        HeadGroup head;
        InfoGroup.All tail;

        public PairGroup(HeadGroup head, InfoGroup.All tail) {
            this.head = head;
            this.tail = tail;
        }

        public HeadGroup getHead() {
            return head;
        }

        public InfoGroup.All getTail() {
            return tail;
        }

        public void extract(InfoGroup.All info) {
            getTail().extract(info);
        }

        public InfoGroup.All getSimilarInfo(InfoGroup.All info) {
            return getTail().getSimilar(info);
        }

        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.pairToTooltip(this);
        }
    }

    public static class HeadPairListGroup extends HeadGroup {
        List<PairGroup> heads;

        public HeadPairListGroup(List<PairGroup> heads) {
            this.heads = heads;
        }

        public List<PairGroup> getHeads() {
            return heads;
        }

        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.infoToTooltip(getHeads());
        }
    }
}
