package me.youhavetrouble.enchantio.enchants;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import me.youhavetrouble.enchantio.EnchantioConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.youhavetrouble.enchantio.EnchantioConfig.ENCHANTS;

@SuppressWarnings("UnstableApiUsage")
public class WardEnchant implements EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:ward");

    private final int anvilCost, weight, cooldownTicks;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Set<TagKey<Enchantment>> exclusiveTagKeys = new HashSet<>();
    private final Set<EquipmentSlotGroup> activeSlots = new HashSet<>();
    private final String blockSound;

    public WardEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots,
            int cooldownTicks,
            String blockSound
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.supportedItemTags.addAll(supportedItemTags);
        this.activeSlots.addAll(activeSlots);
        this.cooldownTicks = cooldownTicks;
        this.blockSound = blockSound;
        this.enchantTagKeys.addAll(enchantTagKeys);
        this.exclusiveTagKeys.addAll(exclusiveTagKeys);
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("enchantio.enchant.ward", "Ward");
    }

    @Override
    public int getAnvilCost() {
        return anvilCost;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public EnchantmentRegistryEntry.@NotNull EnchantmentCost getMinimumCost() {
        return minimumCost;
    }

    @Override
    public EnchantmentRegistryEntry.@NotNull EnchantmentCost getMaximumCost() {
        return maximumCost;
    }

    @Override
    public @NotNull Iterable<EquipmentSlotGroup> getActiveSlots() {
        return activeSlots;
    }

    @Override
    public @NotNull Set<TagEntry<ItemType>> getSupportedItems() {
        return supportedItemTags;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public @NotNull String getBlockSound() {
        return blockSound;
    }

    @Override
    public @NotNull Set<TagKey<Enchantment>> getEnchantTagKeys() {
        return Collections.unmodifiableSet(enchantTagKeys);
    }

    @Override
    public @NotNull Set<TagKey<Enchantment>> getExclusiveTagKeys() {
        return Collections.unmodifiableSet(exclusiveTagKeys);
    }

    public static WardEnchant create(ConfigurationSection configurationSection) {
        WardEnchant wardEnchant = new WardEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 12),
                EnchantioConfig.getInt(configurationSection, "weight", 2),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "minimumCost.base", 35),
                        EnchantioConfig.getInt(configurationSection, "minimumCost.additionalPerLevel", 1)
                ),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "maximumCost.base", 65),
                        EnchantioConfig.getInt(configurationSection, "maximumCost.additionalPerLevel", 1)
                ),
                EnchantioConfig.getEnchantmentTagKeysFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "enchantmentTags",
                        List.of("#in_enchanting_table")
                )),
                EnchantioConfig.getEnchantmentTagKeysFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "exclusiveTags",
                        List.of()
                )),
                EnchantioConfig.getItemTagEntriesFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "supportedItemTags",
                        List.of(
                                "minecraft:shield"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "OFFHAND"
                        )
                )),
                EnchantioConfig.getInt(configurationSection, "cooldownTicks", 40),
                EnchantioConfig.getString(configurationSection, "blockSound", "minecraft:item.shield.block")
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            ENCHANTS.put(WardEnchant.KEY, wardEnchant);
        }

        return wardEnchant;
    }

}
