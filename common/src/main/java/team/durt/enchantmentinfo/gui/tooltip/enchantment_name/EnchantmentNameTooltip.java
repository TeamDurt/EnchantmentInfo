package team.durt.enchantmentinfo.gui.tooltip.enchantment_name;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantmentNameTooltip extends ClientTextTooltip {
    EnchantmentInstance enchantmentInstance;

    public EnchantmentNameTooltip(Enchantment enchantment) {
        this(new EnchantmentInstance(enchantment, 0));
    }

    public EnchantmentNameTooltip(EnchantmentInstance enchantmentInstance) {
        this(getEnchantmentName(enchantmentInstance).getVisualOrderText(), enchantmentInstance);
    }

    protected EnchantmentNameTooltip(FormattedCharSequence text, EnchantmentInstance enchantmentInstance) {
        super(text);
        this.enchantmentInstance = enchantmentInstance;
    }

    @Override
    public int getHeight() {
        return super.getHeight() - 2; // 8 for better math, any letter's height is basically 8
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

    public static MutableComponent getEnchantmentName(EnchantmentInstance enchantmentInstance) {
        Enchantment enchantment = enchantmentInstance.enchantment;
        int level = enchantmentInstance.level;

        if (level == 0) {
            // from Enchantment#getFullname(int)
            MutableComponent name = Component.translatable(enchantment.getDescriptionId());
            if (enchantment.isCurse()) {
                name.withStyle(ChatFormatting.RED);
            } else {
                name.withStyle(ChatFormatting.GRAY);
            }
            return name;
        }

        return (MutableComponent) enchantment.getFullname(level);
    }
}
