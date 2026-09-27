package me.youhavetrouble.enchantio.enchants;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import me.youhavetrouble.enchantio.EnchantioConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.youhavetrouble.enchantio.EnchantioConfig.ENCHANTS;

@SuppressWarnings("UnstableApiUsage")
public class TunnellingEnchant implements EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio", "tunnelling");

    public static final Key AFFECTED_BLOCKS_KEY = Key.key("enchantio", "affected_by_tunnelling");

    private final int anvilCost, weight, maxLevel, blocksPerLevel;
    private final boolean canBreakToolIncompatibleBlocks;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Map<Key, Set<TagEntry<BlockType>>> affectedBlockTags = new HashMap<>();

    private TunnellingEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Map<Key, Set<TagEntry<BlockType>>> affectedBlockTags,
            int maxLevel,
            int blocksPerLevel,
            boolean canBreakToolIncompatibleBlocks
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.maxLevel = maxLevel;
        this.blocksPerLevel = blocksPerLevel;
        this.canBreakToolIncompatibleBlocks = canBreakToolIncompatibleBlocks;
        this.supportedItemTags.addAll(supportedItemTags);
        this.enchantTagKeys.addAll(enchantTagKeys);
        this.affectedBlockTags.putAll(affectedBlockTags);
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("enchantio.enchant.tunnelling", "Tunnelling");
    }

    @Override
    public int getAnvilCost() {
        return anvilCost;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
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
        return Set.of(EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public @NotNull Set<TagEntry<ItemType>> getSupportedItems() {
        return supportedItemTags;
    }

    @Override
    public @NotNull Set<TagKey<Enchantment>> getEnchantTagKeys() {
        return Collections.unmodifiableSet(enchantTagKeys);
    }

    @Override
    public @NotNull Map<Key, Set<TagEntry<BlockType>>> getBlockTagsToRegister() {
        return affectedBlockTags;
    }

    public int getBlocksPerLevel() {
        return blocksPerLevel;
    }

    public boolean canBreakToolIncompatibleBlocks() {
        return canBreakToolIncompatibleBlocks;
    }

    public static TunnellingEnchant create(ConfigurationSection configurationSection) {
        TunnellingEnchant tunnellingEnchant = new TunnellingEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 1),
                EnchantioConfig.getInt(configurationSection, "weight", 10),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "minimumCost.base", 40),
                        EnchantioConfig.getInt(configurationSection, "minimumCost.additionalPerLevel", 3)
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
                EnchantioConfig.getItemTagEntriesFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "supportedItemTags",
                        List.of(
                                "#minecraft:enchantable/mining"
                        )
                )),
                Map.of(
                        AFFECTED_BLOCKS_KEY,
                        EnchantioConfig.getBlockTagKeysFromList(EnchantioConfig.getStringList(
                                configurationSection,
                                "affectedBlockTags",
                                List.of(
                                        "#minecraft:base_stone_overworld",
                                        "#minecraft:base_stone_nether",
                                        "#minecraft:ores",
                                        "#minecraft:terracotta",
                                        "#minecraft:glazed_terracotta",
                                        "#minecraft:concrete",
                                        "#minecraft:stone_bricks",
                                        "minecraft:end_stone"
                                )
                        ))
                ),
                EnchantioConfig.getInt(configurationSection, "maxLevel", 1),
                EnchantioConfig.getInt(configurationSection, "blocksPerLevel", 1),
                EnchantioConfig.getBoolean(configurationSection, "canBreakToolIncompatibleBlocks", false)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            ENCHANTS.put(TunnellingEnchant.KEY, tunnellingEnchant);
        }

        return tunnellingEnchant;
    }

}
