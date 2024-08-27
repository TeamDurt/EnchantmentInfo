package team.durt.enchantmentinfo.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;
import team.durt.enchantmentinfo.gui.group.InfoHolder;
import team.durt.enchantmentinfo.gui.tooltip.ItemTooltip;
import team.durt.enchantmentinfo.gui.tooltip.LineGroupTooltip;
import team.durt.enchantmentinfo.gui.tooltip.ParentTooltip;
import team.durt.enchantmentinfo.gui.tooltip.SwitcherTooltip;
import team.durt.enchantmentinfo.gui.tooltip.enchantment_name.EnchantmentNameTooltip;
import team.durt.enchantmentinfo.gui.tooltip.enchantment_name.HeadEnchantmentNameTooltip;
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

    public static ParentTooltip infoToTooltip(List<HeadGroup.PairGroup> pairGroups) {
        return new ParentTooltip(
                pairGroups.stream()
                        .map(HeadGroup.PairGroup::toTooltip)
                        .collect(Collectors.toList())
        ).setGap(2);
    }

    public static ParentTooltip pairToTooltip(HeadGroup.PairGroup pairGroup) {
        ParentTooltip parent = new ParentTooltip().setGap(2);

        ClientTooltipComponent headTooltip = pairGroup.getHead().toTooltip();
        if (pairGroup.getHead().shouldBeHighlightedInTooltip()) {
            if (headTooltip != null) {
                BlueLineTooltip blueLine = new BlueLineTooltip(headTooltip.getHeight());
                headTooltip = new LineGroupTooltip(blueLine, headTooltip); // under blue line
            }
        }
        parent.addChild(headTooltip);

        ParentTooltip info = pairGroup.getTail().toTooltip();

        if (info != null && !info.getChildList().isEmpty()) parent.addChild(info.setGap(2));

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

        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight());
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

    public static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        ParentTooltip parent = collectChildTooltips(enchantables);
        return parent == null ? null : parent.setGap(2);
    }

    public static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        return parseEnchantmentNamesList(enchantmentInstances, false);
    }

    public static ParentTooltip parseHeadEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        return parseEnchantmentNamesList(enchantmentInstances, true);
    }

    public static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances, boolean head) {
        if (enchantmentInstances.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip().setGap(2);
        enchantmentInstances.stream()
                .map(head ? TooltipHelper::parseHeadEnchantmentName : TooltipHelper::parseEnchantmentName)
                .forEach(parent::addChild);

        return parent;
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }

    private static HeadEnchantmentNameTooltip parseHeadEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new HeadEnchantmentNameTooltip(enchantmentInstance);
    }

    public static LineGroupTooltip parseCoolItems(InfoGroup.CoolItems coolItems) {
        return parseItemsDefinition(coolItems, true);
    }

    public static LineGroupTooltip parseNotCoolItems(InfoGroup.NotCoolItems notCoolItems) {
        return parseItemsDefinition(notCoolItems, false);
    }

    public static LineGroupTooltip parseItemsDefinition(InfoGroup<? extends InfoGroup<?>> definitionGroup, boolean allowed) {
        if (definitionGroup == null) return null;

        ParentTooltip parent = collectChildTooltips(definitionGroup);
        if (parent == null) return null;
        parent.setOrientation(ParentTooltip.Orientation.HORIZONTAL).setGap(2);

        int height = parent.getHeight();
        ColoredLineTooltip coloredLine = allowed ?
                new GreenLineTooltip(height) :
                new RedLineTooltip(height);

        return new LineGroupTooltip(coloredLine, parent);
    }

    public static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null || categories.isEmpty()) return null;

        List<EnchantmentCategoryTooltip> tooltips = categories
                .getChildList()
                .stream()
                .map(EnchantmentCategoryTooltip::new)
                .collect(Collectors.toList());

        return new ParentTooltip(tooltips, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    public static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;

        ParentTooltip parent = collectChildTooltips(itemGroups);
        if (parent == null) return null;
        return parent
                .setOrientation(ParentTooltip.Orientation.HORIZONTAL)
                .setGap(2);
    }

    public static SwitcherTooltip parseItemGroup(InfoGroup.Items items) {
        SwitcherTooltip switcher = new SwitcherTooltip();
        items.getChildList()
                .stream()
                .map(ItemTooltip::new)
                .forEach(switcher::addChild);
        return switcher.getChildList().isEmpty() ? null : switcher;
    }

    public static ParentTooltip parseHeadEnchantmentsGroup(HeadGroup.HeadEnchantmentsGroup headEnchantmentsGroup) {
        return parseHeadEnchantmentNamesList(headEnchantmentsGroup.getEnchantments());
    }

    public static ParentTooltip collectChildTooltips(InfoGroup<? extends InfoGroup<?>> infoGroup) {
        if (infoGroup.isEmpty()) return null;
        ParentTooltip parent = new ParentTooltip();
        infoGroup.getChildList()
                .stream()
                .map(InfoHolder::toTooltip)
                .forEach(parent::addChild);
        return parent.getChildList().isEmpty() ? null : parent;
    }

    public static void addShiftMessage(List<Component> components) {
        boolean shiftPressed = Screen.hasShiftDown();
        addShiftMessage(components, !shiftPressed);
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
