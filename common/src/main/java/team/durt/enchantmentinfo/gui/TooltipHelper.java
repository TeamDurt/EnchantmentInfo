package team.durt.enchantmentinfo.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;
import team.durt.enchantmentinfo.gui.group.InfoHolder;
import team.durt.enchantmentinfo.gui.tooltip.*;
import team.durt.enchantmentinfo.gui.tooltip.line.BlueLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.ColoredLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.GreenLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.RedLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

import java.util.List;
import java.util.stream.Collectors;

public class TooltipHelper {
    private static final Component shiftKeyComponent = Component
            .literal("Shift")
            .withStyle(ChatFormatting.GRAY);
    private static final Component releaseShiftComponent = Component
            .translatable(
                    "enchantmentinfo.releaseShift",
                    shiftKeyComponent
            ).withStyle(ChatFormatting.DARK_GRAY);
    private static final Component holdShiftComponent = Component
            .translatable(
                    "enchantmentinfo.holdShift",
                    shiftKeyComponent
            ).withStyle(ChatFormatting.DARK_GRAY);

    public static ParentTooltip pairToTooltip(HeadGroup.PairGroup pairGroup) {
        ParentTooltip parent = new ParentTooltip();

        if (pairGroup.getHead() instanceof HeadGroup.PairGroup group) {
            ParentTooltip headTooltip = group.toTooltip(); // recursion here
            parent.addChild(
                    new LineGroupTooltip(
                            new BlueLineTooltip(headTooltip.getHeight()),
                            headTooltip
                    )
            ); // under blue line
        } else {
            parent.addChild(pairGroup.getHead().toTooltip());
        }

        ParentTooltip info = pairGroup.getTail().toTooltip();
        info.setSpaceAfter(2);

        if (!info.getChildList().isEmpty()) parent.addChild(info);

        return parent;
    }

    public static LineGroupTooltip parseIncompatibleEnchantments(InfoGroup.IncompatibleEnchantments infoGroup) {
        if (infoGroup == null) return null;

        List<EnchantmentInstance> instances = infoGroup
                .getChildList()
                .stream()
                .map(enchantment -> new EnchantmentInstance(enchantment, 0))
                .collect(Collectors.toList());

        ParentTooltip incompatibleEnchantmentsList = parseEnchantmentNamesList(instances);
        if (incompatibleEnchantmentsList == null) return null;

        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight() - 2);
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

    public static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        return collectChildTooltips(enchantables).setGap(2);
    }

    public static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        if (enchantmentInstances.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip();
        enchantmentInstances.stream()
                .map(TooltipHelper::parseEnchantmentName)
                .forEach(parent::addChild);

        return parent;
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }

    public static LineGroupTooltip parseCoolItems(InfoGroup.CoolItems coolItems) {
        return parseItemsDefinition(coolItems, true);
    }

    public static LineGroupTooltip parseNotCoolItems(InfoGroup.NotCoolItems notCoolItems) {
        return parseItemsDefinition(notCoolItems, false);
    }

    public static LineGroupTooltip parseItemsDefinition(InfoGroup<? extends InfoGroup<?>> definitionGroup, boolean allowed) {
        if (definitionGroup == null) return null;

        ParentTooltip parent = collectChildTooltips(definitionGroup)
                .setOrientation(ParentTooltip.Orientation.HORIZONTAL)
                .setGap(2);

        int height = parent.getHeight();
        ColoredLineTooltip coloredLine = allowed ?
                new GreenLineTooltip(height) :
                new RedLineTooltip(height);

        return new LineGroupTooltip(coloredLine, parent);
    }

    public static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null) return null;

        List<EnchantmentCategoryTooltip> tooltips = categories
                .getChildList()
                .stream()
                .map(EnchantmentCategoryTooltip::new)
                .collect(Collectors.toList());

        return new ParentTooltip(tooltips, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    public static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;

        return collectChildTooltips(itemGroups)
                .setOrientation(ParentTooltip.Orientation.HORIZONTAL)
                .setGap(2);
    }

    public static SwitcherTooltip parseItemGroup(InfoGroup.Items items) {
        SwitcherTooltip switcher = new SwitcherTooltip();
        items.getChildList()
                .stream()
                .map(ItemTooltip::new)
                .forEach(switcher::addChild);
        return switcher;
    }

    public static ParentTooltip parseHeadEnchantmentsGroup(HeadGroup.HeadEnchantmentsGroup headEnchantmentsGroup) {
        return parseEnchantmentNamesList(headEnchantmentsGroup.getEnchantments());
    }

    public static ParentTooltip collectChildTooltips(InfoGroup<? extends InfoGroup<?>> infoGroup) {
        ParentTooltip parent = new ParentTooltip();
        infoGroup.getChildList()
                .stream()
                .map(InfoHolder::toTooltip)
                .forEach(parent::addChild);
        return parent;
    }

    public static void addShiftMessage(List<Component> components, boolean shouldHold) {
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
}
