package team.durt.enchantmentinfo.gui.group;

import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.gui.TooltipHelper;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;

import java.util.List;

public abstract class HeadGroup implements InfoHolder {
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
    }

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

        @Override
        public ParentTooltip toTooltip() {
            return TooltipHelper.pairToTooltip(this);
        }
    }
}
