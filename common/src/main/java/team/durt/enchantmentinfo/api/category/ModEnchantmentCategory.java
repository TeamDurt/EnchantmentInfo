package team.durt.enchantmentinfo.api.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.function.Predicate;

public class ModEnchantmentCategory {
    private final String name;
    private final Predicate<Item> canEnchant;

    public ModEnchantmentCategory(String name, Predicate<Item> canEnchant) {
        this.name = name;
        this.canEnchant = canEnchant;
    }

    public String getName() {
        return name;
    }

    public boolean canEnchant(Item item) {
        return this.canEnchant.test(item);
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation("enchantmentinfo", "textures/tooltip/category/" + this.getName() + ".png");
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