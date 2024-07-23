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
import team.durt.enchantmentinfo.gui.Group.InfoGroup;
import team.durt.enchantmentinfo.gui.Group.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.EnchantmentNameTooltip;
import team.durt.enchantmentinfo.gui.tooltip.LineGroupTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;
import team.durt.enchantmentinfo.gui.tooltip.SwitcherTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.BlueLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.GreenLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.RedLineTooltip;

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

        if (pairGroup.head instanceof HeadGroup headGroup) {
            ParentTooltip headNames = parseHeadGroup(headGroup);
            parent.addChild(headNames);
        } else if (pairGroup.head instanceof PairGroup pairGroup1) {
            ParentTooltip pairHead = pairToTooltip(pairGroup1);
            parent.addChild(new LineGroupTooltip(new BlueLineTooltip(pairHead.getHeight()), pairHead)); //under blue line
        }

        ParentTooltip info = new ParentTooltip();

        info.addChild(parseIncompatibleEnchantments(pairGroup.tail.getIncompatibleEnchantments()));
        info.addChild(parseEnchantables(pairGroup.tail.getEnchantables()));

        info.setSpaceAfter(2);

        if (!info.getChildList().isEmpty()) parent.addChild(info);

        //todo return null ?
        return parent;
    }

    private static ParentTooltip parseHeadGroup(HeadGroup headGroup) {
        return parseEnchantmentNamesList(headGroup.enchantments);
    }

    private static LineGroupTooltip parseIncompatibleEnchantments(InfoGroup.IncompatibleEnchantments infoGroup) {
        if (infoGroup == null) return null;
        ParentTooltip incompatibleEnchantmentsList = parseEnchantmentNamesList(infoGroup.content);
        if (incompatibleEnchantmentsList == null) return null;
        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight() - 2);
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

     private static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        InfoGroup.Categories categories = enchantables.getCategories();
        InfoGroup.CompatibleItemGroups compatibleItemGroups = enchantables.getCompatibleItemGroups();
        InfoGroup.IncompatibleItemGroups incompatibleItemGroups = enchantables.getIncompatibleItemGroups();

        if (categories == null && compatibleItemGroups == null && incompatibleItemGroups == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.VERTICAL, 2);
        parent.addChild(parseCoolItems(categories, compatibleItemGroups));
        parent.addChild(parseNotCoolItems(incompatibleItemGroups));

        return parent;
    }

    private static ParentTooltip parseEnchantmentNamesList(List<EnchantmentNameTooltip> enchantmentNameTooltips) {
        if (enchantmentNameTooltips.isEmpty()) return null;
        return new ParentTooltip(enchantmentNameTooltips, ParentTooltip.Orientation.VERTICAL, 0);
    }

    private static LineGroupTooltip parseCoolItems(InfoGroup.Categories categories, InfoGroup.CompatibleItemGroups compatibleItemGroups) {
        if (categories == null && compatibleItemGroups == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        parent.addChild(parseCategories(categories));
        parent.addChild(parseItemGroups(compatibleItemGroups));

        GreenLineTooltip greenLine = new GreenLineTooltip(parent.getHeight());
        return new LineGroupTooltip(greenLine, parent);
    }

    private static LineGroupTooltip parseNotCoolItems(InfoGroup.IncompatibleItemGroups incompatibleItemGroups) {
        ParentTooltip itemGroupsTooltip = parseItemGroups(incompatibleItemGroups);
        if (itemGroupsTooltip == null) return null;
        RedLineTooltip redLine = new RedLineTooltip(itemGroupsTooltip.getHeight());
        return new LineGroupTooltip(redLine, itemGroupsTooltip);
    }

    private static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null) return null;
        return new ParentTooltip(categories.content, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    private static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;
        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup.Items items : itemGroups.content) {
            SwitcherTooltip switcher = new SwitcherTooltip(items.content);
            parent.addChild(switcher);
        }
        return parent;
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
