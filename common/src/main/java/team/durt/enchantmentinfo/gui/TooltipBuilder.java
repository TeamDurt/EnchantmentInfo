package team.durt.enchantmentinfo.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.apache.commons.compress.utils.Lists;
import team.durt.enchantmentinfo.gui.group.HeadGroup.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;

import java.util.List;
import java.util.stream.Collectors;

public class TooltipBuilder {
    /**
     * Takes List of Components and adds {@link net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent Tooltips} to it
     * that are represents information about Enchantments in given {@link ListTag}
     *
     * @see team.durt.enchantmentinfo.mixin.EnchantedBookItemMixin
     * @see FakeComponent
     */
    public static void build(List<Component> components, ListTag enchantmentTags) {
        boolean shiftPressed = Screen.hasShiftDown();

        if (shiftPressed) {
            // custom tooltips
            addCustomTooltips(components, enchantmentTags);
        } else {
            // default enchantment names
            ItemStack.appendEnchantmentNames(components, enchantmentTags);
        }

        // hold or release shift message
        TooltipHelper.addShiftMessage(components, !shiftPressed);
    }

    private static void addCustomTooltips(List<Component> components, ListTag enchantmentTags) {
        List<EnchantmentInstance> enchantments = getEnchantmentsFromTag(enchantmentTags);

        // collecting info grouped by similar parts //todo (placeholder, not done yet)
        List<PairGroup> info = InfoCollector.getInfo(enchantments);
        // transforming all info to tooltip components
        List<ParentTooltip> tooltips = infoToTooltips(info);

        // adding tooltips to components list using FakeComponent as tooltip holder, so it matches the list type
        components.addAll(FakeComponent.tooltipsToComponents(tooltips));
    }

    private static List<ParentTooltip> infoToTooltips(List<PairGroup> pairGroups) {
        return pairGroups
                .stream()
                .map(PairGroup::toTooltip)
                .collect(Collectors.toList());
    }

    private static List<EnchantmentInstance> getEnchantmentsFromTag(ListTag enchantmentTag) {
        List<EnchantmentInstance> enchantments = Lists.newArrayList();
        for (int i = 0; i < enchantmentTag.size(); i++) {
            CompoundTag compoundTag = enchantmentTag.getCompound(i);
            BuiltInRegistries.ENCHANTMENT
                    .getOptional(EnchantmentHelper.getEnchantmentId(compoundTag))
                    .ifPresent(enchantment -> enchantments.add(
                            new EnchantmentInstance(
                                    enchantment,
                                    EnchantmentHelper.getEnchantmentLevel(compoundTag)
                            )
                    ));
        }
        return enchantments;
    }
}
