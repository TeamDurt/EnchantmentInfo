package team.durt.enchantmentinfo.platform;

import team.durt.enchantmentinfo.platform.services.IPlatformHelper;
import team.durt.enchantmentinfo.platform.services.IRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.stream.Stream;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public Stream<Item> getRegisteredItems() {
        return BuiltInRegistries.ITEM.stream();
    }

    @Override
    public Stream<Enchantment> getRegisteredEnchantments() {
        return BuiltInRegistries.ENCHANTMENT.stream();
    }

    @Override
    public Stream<TagKey<Item>> getRegisteredItemTags() {
        return BuiltInRegistries.ITEM.getTagNames();
    }
}
