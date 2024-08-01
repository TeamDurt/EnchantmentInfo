package team.durt.enchantmentinfo.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.apache.commons.compress.utils.Lists;
import team.durt.enchantmentinfo.Constants;
import team.durt.enchantmentinfo.gui.group.HeadGroup.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;

import java.util.List;

public class TooltipBuilder {
    static Exception lastException = null;

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
            try {
                // custom tooltips
                addCustomTooltips(components, enchantmentTags);
            } catch (Exception e) {
                // just in case something goes wrong,
                // we don't want players to experience game crash only because of some little mistake.
                // planned to be removed on release
                onException(components, enchantmentTags, e);
            }
        } else {
            // default enchantment names
            ItemStack.appendEnchantmentNames(components, enchantmentTags);
        }

        // hold or release shift message
        TooltipHelper.addShiftMessage(components, !shiftPressed);
    }

    private static void addCustomTooltips(List<Component> components, ListTag enchantmentTags) {
        List<EnchantmentInstance> enchantments = getEnchantmentsFromTag(enchantmentTags);

        // collecting info grouped by similar parts
        List<PairGroup> info = InfoCollector.getInfo(enchantments);
        // transforming all info to tooltip component
        ParentTooltip tooltip = TooltipHelper.infoToTooltip(info).setSpaceAfter(2);

        // adding tooltip to components list using FakeComponent as tooltip holder, so it matches the list type
        components.add(new FakeComponent(tooltip));
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

    private static void onException(List<Component> components, ListTag enchantmentTags, Exception e) {
        for (int i = 1; i < 6; i++) {
            components.add(Component.translatable("enchantmentinfo.crash" + i).withStyle(ChatFormatting.RED));
        }
        components.add(Component.literal(e.toString()).withStyle(ChatFormatting.RED));
        if (lastException == null || !e.toString().equals(lastException.toString())) {
            lastException = e;
            for (EnchantmentInstance instance : getEnchantmentsFromTag(enchantmentTags)) {
                Constants.LOG.error(instance.enchantment.getDescriptionId() + " " + instance.level);
            }
            Constants.LOG.error("Something went wrong on getting Enchantment Info", e);
        }
    }
}
