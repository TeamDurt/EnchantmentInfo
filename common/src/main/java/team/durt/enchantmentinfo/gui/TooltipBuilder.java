package team.durt.enchantmentinfo.gui;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import team.durt.enchantmentinfo.Constants;
import team.durt.enchantmentinfo.gui.group.HeadGroup.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TooltipBuilder {
    static Exception lastException = null;

    /**
     * Takes Tooltip Adder and adds {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent Tooltips} to it
     * that are represents information about Enchantments in given {@link ItemEnchantments}
     *
     * @param registries used to sort Enchantments the same way as vanilla does
     * @return true if added custom tooltips, false otherwise
     * @see team.durt.enchantmentinfo.mixin.ItemStackMixin
     * @see FakeComponent
     */
    public static boolean build(Consumer<Component> tooltipAdder, ItemEnchantments itemEnchantments, @Nullable HolderLookup.Provider registries) {
        boolean shiftPressed = Screen.hasShiftDown();

        if (shiftPressed) {
            List<EnchantmentInstance> enchantments = getEnchantments(itemEnchantments, registries);
            try {
                // custom tooltips
                addCustomTooltips(tooltipAdder, enchantments);
            } catch (Exception e) {
                // just in case something goes wrong,
                // we don't want players to experience game crash only because of some little mistake.
                // planned to be removed on release
                onException(tooltipAdder, enchantments, e);
            }
        } else {
            return false;
        }

        return true;
    }

    private static void addCustomTooltips(Consumer<Component> tooltipAdder, List<EnchantmentInstance> enchantments) {
        // collecting info grouped by similar parts
        List<PairGroup> info = InfoCollector.getInfo(enchantments);
        // transforming all info to tooltip component
        ParentTooltip tooltip = TooltipHelper.infoToTooltip(info).setSpaceAfter(2);

        // adding tooltip using FakeComponent as tooltip holder, so it matches the Component type
        tooltipAdder.accept(new FakeComponent(tooltip));
    }

    /**
     * Returns Enchantments in the same order as they are shown in vanilla tooltip.
     *
     * @see ItemEnchantments#addToTooltip
     */
    private static List<EnchantmentInstance> getEnchantments(ItemEnchantments itemEnchantments, @Nullable HolderLookup.Provider registries) {
        HolderSet<Enchantment> tooltipOrder = getTooltipOrder(registries);
        List<EnchantmentInstance> enchantments = new ArrayList<>();
        for (Holder<Enchantment> enchantment : tooltipOrder) {
            int level = itemEnchantments.getLevel(enchantment);
            if (level > 0) {
                enchantments.add(new EnchantmentInstance(enchantment, level));
            }
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
            if (!tooltipOrder.contains(entry.getKey())) {
                enchantments.add(new EnchantmentInstance(entry.getKey(), entry.getIntValue()));
            }
        }
        return enchantments;
    }

    private static HolderSet<Enchantment> getTooltipOrder(@Nullable HolderLookup.Provider registries) {
        if (registries == null) return HolderSet.empty();
        Optional<HolderSet.Named<Enchantment>> tooltipOrder = registries
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(EnchantmentTags.TOOLTIP_ORDER);
        return tooltipOrder.isPresent() ? tooltipOrder.get() : HolderSet.empty();
    }

    private static void onException(Consumer<Component> tooltipAdder, List<EnchantmentInstance> enchantments, Exception e) {
        for (int i = 1; i < 6; i++) {
            tooltipAdder.accept(Component.translatable("enchantmentinfo.crash" + i).withStyle(ChatFormatting.RED));
        }
        tooltipAdder.accept(Component.literal(e.toString()).withStyle(ChatFormatting.RED));
        if (lastException == null || !e.toString().equals(lastException.toString())) {
            lastException = e;
            for (EnchantmentInstance instance : enchantments) {
                Constants.LOG.error(instance.enchantment.getRegisteredName() + " " + instance.level);
            }
            Constants.LOG.error("Something went wrong on getting Enchantment Info", e);
        }
    }
}
