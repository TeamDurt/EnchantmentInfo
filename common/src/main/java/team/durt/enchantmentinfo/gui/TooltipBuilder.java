package team.durt.enchantmentinfo.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.apache.commons.compress.utils.Lists;
import team.durt.enchantmentinfo.gui.Group.HeadGroup;
import team.durt.enchantmentinfo.gui.Group.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.*;
import team.durt.enchantmentinfo.gui.tooltip.line.BlueLineTooltip;

import java.util.List;

public class TooltipBuilder {

    private static final Component shiftKeyComponent = Component
            .literal("Shift")
            .withStyle(ChatFormatting.GRAY);

    private static final Component holdShiftComponent = Component
            .translatable(
                    "enchantmentinfo.holdShift",
                    shiftKeyComponent
            ).withStyle(ChatFormatting.DARK_GRAY);

    private static final Component releaseShiftComponent = Component
            .translatable(
                    "enchantmentinfo.releaseShift",
                    shiftKeyComponent
            ).withStyle(ChatFormatting.DARK_GRAY);


    public static void build(List<Component> components, ListTag enchantmentTags) {
        boolean shiftPressed = Screen.hasShiftDown();

        if (shiftPressed) {
            //custom tooltips
            addCustomTooltips(components, enchantmentTags);
        } else {
            //default enchantment names
            ItemStack.appendEnchantmentNames(components, enchantmentTags);
        }

        //hold or release shift message
        addShiftMessage(components, !shiftPressed);
    }

    private static void addCustomTooltips(List<Component> components, ListTag enchantmentTags) {
        List<EnchantmentInstance> enchantments = getEnchantmentsFromTag(enchantmentTags);

        //collecting raw info about enchantments in form enchantment-info groups
        List<PairGroup> pairGroups = InfoCollector.getGroupedInfo(enchantments);
        //grouping similar entries to make all info simpler (placeholder, not done yet)
        List<PairGroup> simplifiedGroups = InfoCollector.simplify(pairGroups);
        //transforming all info to tooltip components
        List<ClientTooltipComponent> tooltips = pairsToTooltips(simplifiedGroups);

        //adding tooltips to components list using FakeComponent as tooltip holder, so it matches the list type
        components.addAll(FakeComponent.tooltipsToComponents(tooltips));
    }

    private static List<ClientTooltipComponent> pairsToTooltips(List<PairGroup> pairGroups) {
        List<ClientTooltipComponent> tooltips = Lists.newArrayList();

        for (PairGroup pairGroup : pairGroups) {
            tooltips.add(pairToTooltip(pairGroup));
        }

        return tooltips;
    }

    private static ParentTooltip pairToTooltip(PairGroup pairGroup) {
        ParentTooltip parent = new ParentTooltip();

        if (pairGroup.head instanceof HeadGroup group) {
            ParentTooltip headNames = parseHeadGroup(group);
            parent.addChild(headNames);
        } else if (pairGroup.head instanceof PairGroup group) {
            ParentTooltip headTooltip = pairToTooltip(group);
            parent.addChild(new LineGroupTooltip(new BlueLineTooltip(headTooltip.getHeight()), headTooltip)); //under blue line
        }

        ParentTooltip info = pairGroup.tail.toTooltip();
        info.setSpaceAfter(2);

        if (!info.getChildList().isEmpty()) parent.addChild(info);

        //todo return null ?
        return parent;
    }

    private static ParentTooltip parseHeadGroup(HeadGroup headGroup) {
        return TooltipHelper.parseEnchantmentNamesList(headGroup.enchantments);
    }

    private static void addShiftMessage(List<Component> components, boolean shouldHold) {
        if (shouldHold) {
            addHoldShiftMessage(components);
        } else {
            addReleaseShiftMessage(components);
        }
    }

    private static void addHoldShiftMessage(List<Component> components) {
        components.add(holdShiftComponent);
    }

    private static void addReleaseShiftMessage(List<Component> components) {
        components.add(releaseShiftComponent);
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
