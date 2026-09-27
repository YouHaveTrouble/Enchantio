package me.youhavetrouble.enchantio.enchants;

import com.google.common.collect.ImmutableMap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public interface EnchantioEnchant {

    @NotNull
    Key getKey();

    @NotNull
    Component getDescription();

    int getAnvilCost();

    int getMaxLevel();

    int getWeight();

    @NotNull
    EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    @NotNull
    EnchantmentRegistryEntry.EnchantmentCost getMaximumCost();

    @NotNull
    Iterable<EquipmentSlotGroup> getActiveSlots();

    @NotNull
    Set<TagEntry<ItemType>> getSupportedItems();

    @NotNull
    Set<TagKey<Enchantment>> getEnchantTagKeys();

    @NotNull
    default TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key( getKey().asString() + "_enchantable"));
    }

    @NotNull
    default TagEntry<Enchantment> getTagEntry() {
        return TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, getKey()));
    }

    /**
     * Get block tags to register to use for this enchantment and what tags/blocks it should contain
     */
    @NotNull
    default Map<Key, Set<TagEntry<BlockType>>> getBlockTagsToRegister() {
        return ImmutableMap.of();
    }

    static @Nullable ItemStack findFirstWithEnchant(
            @NotNull EntityEquipment equipment,
            @NotNull Enchantment enchantment
    ) {

        Set<EquipmentSlotGroup> equipmentSlotGroups = enchantment.getActiveSlotGroups();

        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.MAINHAND)) {
            if (equipment.getItemInMainHand().getEnchantmentLevel(enchantment) > 0) return equipment.getItemInMainHand();
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.OFFHAND)) {
            if (equipment.getItemInOffHand().getEnchantmentLevel(enchantment) > 0) return equipment.getItemInOffHand();
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.HEAD)) {
            ItemStack helmet = equipment.getHelmet();
            if (helmet != null && helmet.getEnchantmentLevel(enchantment) > 0) return helmet;
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.CHEST)) {
            ItemStack chestplate = equipment.getChestplate();
            if (chestplate != null && chestplate.getEnchantmentLevel(enchantment) > 0) return chestplate;
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.LEGS)) {
            ItemStack leggings = equipment.getLeggings();
            if (leggings != null && leggings.getEnchantmentLevel(enchantment) > 0) return leggings;

        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.FEET)) {
            ItemStack boots = equipment.getBoots();
            if (boots != null && boots.getEnchantmentLevel(enchantment) > 0) return boots;
        }
        return null;
    }

}
