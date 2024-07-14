package com.teamdurt.enchantmentinfo.frontend;

import com.mojang.datafixers.util.Pair;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategory;
import com.teamdurt.enchantmentinfo.category.ModEnchantmentCategoryManager;
import com.teamdurt.enchantmentinfo.enchantment_data.EnchantmentDataManager;
import com.teamdurt.enchantmentinfo.frontend.tooltip.EnchantmentCategoryTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.ItemTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.line.GreenLineTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.ParentTooltip;
import com.teamdurt.enchantmentinfo.frontend.tooltip.line.RedLineTooltip;
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
    static ModEnchantmentCategoryManager enchantmentCategoryManager = ModEnchantmentCategoryManager.getInstance();

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
        List<ClientTooltipComponent> tooltips = Lists.newArrayList();

        List<Pair<Enchantment, Integer>> enchantments = getEnchantmentsFromTag(enchantmentTags);

        for (Pair<Enchantment, Integer> enchantmentWithLevel : enchantments) {
            Enchantment enchantment = enchantmentWithLevel.getFirst();

            //parent tooltip
            ParentTooltip mainParent = new ParentTooltip();

            //enchantment name
            mainParent.addChild(getEnchantmentName(enchantmentWithLevel));
            //incompatible enchantments
            mainParent.addChild(parseIncompatibleEnchantments(enchantment));
            //matching items
            mainParent.addChild(parseEnchantableItems(enchantment));

            //adding the whole thing to tooltips
            tooltips.add(mainParent);
        }

        //adding tooltips to components list using FakeComponent as tooltip holder, so it matches the list type
        components.addAll(FakeComponent.tooltipsToComponents(tooltips));
    }

    private static ClientTooltipComponent parseIncompatibleEnchantments(Enchantment enchantment) {
        List<Enchantment> incompatibleEnchantments = enchantmentDataManager.getIncompatibleEnchantments(enchantment);

        if (incompatibleEnchantments.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        ParentTooltip incompatiblesList = new ParentTooltip();
        for (Enchantment incompatibleEnchantment : incompatibleEnchantments) {
            incompatiblesList.addChild(
                    getEnchantmentName(incompatibleEnchantment)
            );
        }

        RedLineTooltip redLine = new RedLineTooltip(incompatiblesList.getHeight() - 3);
        parent.addChild(redLine);
        parent.addChild(incompatiblesList);

        return parent;
    }

    private static ClientTooltipComponent parseEnchantableItems(Enchantment enchantment) {
        List<ModEnchantmentCategory> categories = enchantmentDataManager.getEnchantmentCategories(enchantment);
        List<List<Item>> included = enchantmentDataManager.getIncludedItemGroups(enchantment);
        List<List<Item>> excluded = enchantmentDataManager.getExcludedItemGroups(enchantment);
        if (categories.isEmpty() && included.isEmpty() && excluded.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.VERTICAL, 2);

        //add allowed categories and items elements
        parent.addChild(parseCoolItems(categories, included));
        //add abandoned items from allowed categories
        parent.addChild(parseNotCoolItems(excluded));

        parent.setSpaceAfter(2);

        return parent;
    }

    private static ParentTooltip parseCoolItems(List<ModEnchantmentCategory> categories, List<List<Item>> included) {
        ParentTooltip categoryTooltips = parseEnchantmentCategories(categories);
        ParentTooltip includedItemsTooltips = itemGroupsToTooltip(included);

        if (categoryTooltips == null && includedItemsTooltips == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        ParentTooltip categoriesAndItems = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        //add categories
        categoriesAndItems.addChild(categoryTooltips);
        //add included
        categoriesAndItems.addChild(includedItemsTooltips);

        GreenLineTooltip greenLine = new GreenLineTooltip(categoriesAndItems.getHeight());

        parent.addChild(greenLine);
        parent.addChild(categoriesAndItems);

        return parent;
    }

    private static ParentTooltip parseNotCoolItems(List<List<Item>> excluded) {
        ParentTooltip excludedItemsTooltips = itemGroupsToTooltip(excluded);

        if (excludedItemsTooltips == null) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        RedLineTooltip redLine = new RedLineTooltip(excludedItemsTooltips.getHeight());

        parent.addChild(redLine);
        parent.addChild(excludedItemsTooltips);

        return parent;
    }

    private static ParentTooltip parseEnchantmentCategories(List<ModEnchantmentCategory> categories) {
        if (categories.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);

        ModEnchantmentCategory breakable = enchantmentCategoryManager.getCategory("breakable");
        if (categories.contains(breakable)) {
            parent.addChild(new EnchantmentCategoryTooltip(breakable));
            return parent;
            //skipping anything else, breakable basically matches any other category
        }

        for (ModEnchantmentCategory category : categories) {
            parent.addChild(new EnchantmentCategoryTooltip(category));
        }

        return parent;
    }

    private static ParentTooltip itemGroupsToTooltip(List<List<Item>> itemGroups) {
        if (itemGroups.isEmpty()) return null;

        ParentTooltip parent = new ParentTooltip(ParentTooltip.Orientation.HORIZONTAL, 2);
        for (List<Item> itemGroup : itemGroups) {
            for (Item item : itemGroup) {
                parent.addChild(new ItemTooltip(item));
            }
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
