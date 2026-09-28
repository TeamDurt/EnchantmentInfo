package team.durt.enchantmentinfo.api.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class ModEnchantmentCategory {
    private final String name;
    private final Predicate<Item> canEnchant;

    public ModEnchantmentCategory(String name, Predicate<Item> canEnchant) {
        this.name = name;
        this.canEnchant = canEnchant;
    }

    public ModEnchantmentCategory(String name, TagKey<Item> tag) {
        this(name, item -> new ItemStack(item).is(tag));
    }

    public String getName() {
        return name;
    }

    public boolean canEnchant(Item item) {
        return this.canEnchant.test(item);
    }

    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath("enchantmentinfo", "textures/tooltip/category/" + this.getName() + ".png");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModEnchantmentCategory that = (ModEnchantmentCategory) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}