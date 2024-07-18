package team.durt.enchantmentinfo.gui.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantmentNameTooltip extends ClientTextTooltip {
    EnchantmentInstance enchantmentInstance;

    public EnchantmentNameTooltip(Enchantment enchantment) {
        this(new EnchantmentInstance(enchantment, 0));
    }

    public EnchantmentNameTooltip(EnchantmentInstance enchantmentInstance) {
        super((enchantmentInstance.level == 0 ?
                getEnchantmentName(enchantmentInstance.enchantment) :
                getEnchantmentName(enchantmentInstance)
        ).getVisualOrderText());

        this.enchantmentInstance = enchantmentInstance;
    }

    public EnchantmentInstance getEnchantmentInstance() {
        return enchantmentInstance;
    }

    public Enchantment getEnchantment() {
        return enchantmentInstance.enchantment;
    }

    public int getLevel() {
        return enchantmentInstance.level;
    }

    /**
     *  from {@link Enchantment#getFullname(int)}
     */
    private static Component getEnchantmentName(Enchantment enchantment) {
        MutableComponent name = Component.translatable(enchantment.getDescriptionId());
        if (enchantment.isCurse()) {
            name.withStyle(ChatFormatting.RED);
        } else {
            name.withStyle(ChatFormatting.GRAY);
        }
        return name;
    }

    private static Component getEnchantmentName(EnchantmentInstance enchantmentInstance) {
        Enchantment enchantment = enchantmentInstance.enchantment;
        int level = enchantmentInstance.level;
        return enchantment.getFullname(level);
    }
}
