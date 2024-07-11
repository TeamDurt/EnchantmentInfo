package com.teamdurt.enchantmentinfo.frontend;

import com.mojang.datafixers.util.Pair;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;
import com.teamdurt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import com.teamdurt.enchantmentinfo.frontend.tooltip.line.GreenLineTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.ParentTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.line.RedLineTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.TexturedTooltip;
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
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class TooltipBuilder {
    static EnchantmentDataManager enchantmentDataManager = EnchantmentDataManager.getInstance();

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
            //custom tooltip components
            addCustomComponents(components, enchantmentTags);
        } else {
            //default enchantment names
            ItemStack.appendEnchantmentNames(components, enchantmentTags);
        }

        //hold or release shift message
        addShiftMessage(components, !shiftPressed);
    }

    private static void addCustomComponents(List<Component> components, ListTag enchantmentTags) {
        List<ClientTooltipComponent> tooltips = Lists.newArrayList();

        List<Pair<Enchantment, Integer>> enchantments = getEnchantmentsFromTag(enchantmentTags);

        for (Pair<Enchantment, Integer> enchantmentWithLevel : enchantments) {
            //parent tooltip
            ParentTooltip mainParent = new ParentTooltip();

            //enchantment name
            mainParent.addChild(getEnchantmentName(enchantmentWithLevel));
            //incompatible enchantments
            mainParent.addChild(parseIncompatibleEnchantments(enchantmentWithLevel));

            //adding the whole thing to tooltips
            tooltips.add(mainParent);
        }

        components.addAll(FakeComponent.tooltipsToComponents(tooltips));
    }

    private static ClientTooltipComponent parseIncompatibleEnchantments(Pair<Enchantment, Integer> enchantmentWithLevel) {
        List<Enchantment> incompatibleEnchantments = enchantmentDataManager.getIncompatibleEnchantments(enchantmentWithLevel.getFirst());

        if (incompatibleEnchantments.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        ParentTooltip incompatiblesList = new ParentTooltip();
        for (Enchantment enchantment : incompatibleEnchantments) {
            incompatiblesList.addChild(
                    getEnchantmentName(enchantment)
            );
        }

        RedLineTooltip redLine = new RedLineTooltip(incompatiblesList.getHeight() - 3);
        parent.addChild(redLine);
        parent.addChild(incompatiblesList);

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

    private static List<Pair<Enchantment, Integer>> getEnchantmentsFromTag(ListTag enchantmentTag) {
        List<Pair<Enchantment, Integer>> enchantments = Lists.newArrayList();
        for (int i = 0; i < enchantmentTag.size(); i++) {
            CompoundTag compoundTag = enchantmentTag.getCompound(i);
            BuiltInRegistries.ENCHANTMENT
                    .getOptional(EnchantmentHelper.getEnchantmentId(compoundTag))
                    .ifPresent(enchantment -> enchantments.add(
                            Pair.of(
                                    enchantment,
                                    EnchantmentHelper.getEnchantmentLevel(compoundTag)
                            )
                    ));
        }
        return enchantments;
    }

    private static Component getEnchantmentName(Enchantment enchantment) {
        return Component.translatable(enchantment.getDescriptionId());
    }

    private static Component getEnchantmentName(Pair<Enchantment, Integer> enchantmentWithLevel) {
        Enchantment enchantment = enchantmentWithLevel.getFirst();
        Integer level = enchantmentWithLevel.getSecond();
        return enchantment.getFullname(level);
    }
}
