package com.teamdurt.enchantmentinfo.frontend;

import com.mojang.datafixers.util.Pair;
import com.teamdurt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
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
