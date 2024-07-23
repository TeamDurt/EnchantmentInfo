package team.durt.enchantmentinfo.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.apache.commons.compress.utils.Lists;
import team.durt.enchantmentinfo.category.ModEnchantmentCategory;
import team.durt.enchantmentinfo.gui.Group.HeadGroup;
import team.durt.enchantmentinfo.gui.Group.InfoGroup;
import team.durt.enchantmentinfo.gui.Group.PairGroup;
import team.durt.enchantmentinfo.gui.tooltip.*;
import team.durt.enchantmentinfo.gui.tooltip.line.BlueLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.GreenLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.line.RedLineTooltip;
import team.durt.enchantmentinfo.gui.tooltip.texture.EnchantmentCategoryTooltip;

import java.util.ArrayList;
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
        return parseEnchantmentNamesList(headGroup.enchantments);
    }

    public static LineGroupTooltip parseIncompatibleEnchantments(InfoGroup.IncompatibleEnchantments infoGroup) {
        if (infoGroup == null) return null;

        List<EnchantmentInstance> instances = new ArrayList<>();
        for (Enchantment enchantment : infoGroup.content) {
            instances.add(new EnchantmentInstance(enchantment, 0));
        }

        ParentTooltip incompatibleEnchantmentsList = parseEnchantmentNamesList(instances);
        if (incompatibleEnchantmentsList == null) return null;

        RedLineTooltip redLine = new RedLineTooltip(incompatibleEnchantmentsList.getHeight() - 2);
        return new LineGroupTooltip(redLine, incompatibleEnchantmentsList);
    }

     public static ParentTooltip parseEnchantables(InfoGroup.Enchantables enchantables) {
        if (enchantables == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.VERTICAL, 2);
        for (InfoGroup<?> group : enchantables.content) {
            parent.addChild(group.toTooltip());
        }

        return parent;
    }

    private static ParentTooltip parseEnchantmentNamesList(List<EnchantmentInstance> enchantmentInstances) {
        if (enchantmentInstances.isEmpty()) return null;
        List<EnchantmentNameTooltip> tooltips = new ArrayList<>();
        for (EnchantmentInstance instance : enchantmentInstances) {
            tooltips.add(parseEnchantmentName(instance));
        }
        return new ParentTooltip(tooltips, ParentTooltip.Orientation.VERTICAL, 0);
    }

    private static EnchantmentNameTooltip parseEnchantmentName(EnchantmentInstance enchantmentInstance) {
        return new EnchantmentNameTooltip(enchantmentInstance);
    }


    public static LineGroupTooltip parseCoolItems(InfoGroup.CoolItems coolItems) {
        if (coolItems == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup<?> group : coolItems.content) {
            parent.addChild(group.toTooltip());
        }

        GreenLineTooltip greenLine = new GreenLineTooltip(parent.getHeight());
        return new LineGroupTooltip(greenLine, parent);
    }

    //todo ⬆ these are similar ⬇

    public static LineGroupTooltip parseNotCoolItems(InfoGroup.NotCoolItems notCoolItems) {
        if (notCoolItems == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup<?> group : notCoolItems.content) {
            parent.addChild(group.toTooltip());
        }

        RedLineTooltip redLine = new RedLineTooltip(parent.getHeight());
        return new LineGroupTooltip(redLine, parent);
    }

    public static ParentTooltip parseCategories(InfoGroup.Categories categories) {
        if (categories == null) return null;

        List<EnchantmentCategoryTooltip> tooltips = new ArrayList<>();
        for (ModEnchantmentCategory category : categories.content) {
            tooltips.add(new EnchantmentCategoryTooltip(category));
        }

        return new ParentTooltip(tooltips, ParentTooltip.Orientation.HORIZONTAL, 2);
    }

    public static ParentTooltip parseItemGroups(InfoGroup<InfoGroup.Items> itemGroups) {
        if (itemGroups == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (InfoGroup.Items items : itemGroups.content) {
            parent.addChild(items.toTooltip());
        }

        return parent;
    }

    public static SwitcherTooltip parseItemGroup(InfoGroup.Items items) {
        SwitcherTooltip switcher = new SwitcherTooltip();
        for (Item item : items.content) {
            switcher.addChild(new ItemTooltip(item));
        }
        return switcher;
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
