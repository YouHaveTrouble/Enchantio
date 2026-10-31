package me.youhavetrouble.enchantio.enchants;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public abstract class EnchantioEnchant {

    @NotNull public final Key key;
    @NotNull public final Component description;
    public final int anvilCost, maxLevel,  weight;
    @NotNull
    public final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    @NotNull
    public final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    @NotNull
    public final Set<EquipmentSlotGroup> activeSlots;
    @NotNull public final Set<TagEntry<ItemType>> supportedItems;
    @NotNull public final Set<TagKey<Enchantment>> enchantTagKeys;
    @NotNull public final Set<TagKey<Enchantment>> exclusiveTagKeys;

    public EnchantioEnchant(
            @NotNull Key key,
            @NotNull Component description,
            int anvilCost,
            int maxLevel,
            int weight,
            EnchantmentRegistryEntry.@NonNull EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.@NonNull EnchantmentCost maximumCost,
            @NotNull Collection<EquipmentSlotGroup> activeSlots,
            @NotNull Collection<TagEntry<ItemType>> supportedItems,
            @NotNull Collection<TagKey<Enchantment>> enchantTagKeys,
            @NotNull Collection<TagKey<Enchantment>> exclusiveTagKeys

    ) {
        this.key = key;
        this.description = description;
        this.anvilCost = anvilCost;
        this.maxLevel = maxLevel;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.activeSlots = ImmutableSet.copyOf(activeSlots);
        this.supportedItems = ImmutableSet.copyOf(supportedItems);
        this.enchantTagKeys = ImmutableSet.copyOf(enchantTagKeys);
        this.exclusiveTagKeys = ImmutableSet.copyOf(exclusiveTagKeys);

    }

    @NotNull
    public TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key( key + "_enchantable"));
    }

    public TagKey<Enchantment> getTagForExclusiveEnchants() {
        return TagKey.create(RegistryKey.ENCHANTMENT, Key.key( key + "_exclusive"));
    }

    @NotNull
    public TagEntry<Enchantment> getTagEntry() {
        return TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, key));
    }

    /**
     * Get block tags to register to use for this enchantment and what tags/blocks it should contain
     */
    @NotNull
    public Map<Key, Set<TagEntry<BlockType>>> getBlockTagsToRegister() {
        return ImmutableMap.of();
    }

    public static @Nullable ItemStack findFirstWithEnchant(
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
