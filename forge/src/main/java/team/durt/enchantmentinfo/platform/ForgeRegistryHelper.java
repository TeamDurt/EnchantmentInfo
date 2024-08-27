package team.durt.enchantmentinfo.platform;

import team.durt.enchantmentinfo.platform.services.IRegistryHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.stream.Stream;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public Stream<Item> getRegisteredItems() {
        return ForgeRegistries.ITEMS.getValues().stream();
    }

    @Override
    public Stream<Enchantment> getRegisteredEnchantments() {
        return ForgeRegistries.ENCHANTMENTS.getValues().stream();
    }

    @Override
    public Stream<TagKey<Item>> getRegisteredItemTags() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTagNames();
    }
}