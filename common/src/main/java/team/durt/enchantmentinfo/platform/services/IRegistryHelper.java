package team.durt.enchantmentinfo.platform.services;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.stream.Stream;

public interface IRegistryHelper {
    Stream<Item> getRegisteredItems();

    Stream<Enchantment> getRegisteredEnchantments();

    Stream<TagKey<Item>> getRegisteredItemTags();
}
