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

@SuppressWarnings("UnstableApiUsage")
public class TunnellingEnchant extends EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio", "tunnelling");

    public static final Key AFFECTED_BLOCKS_KEY = Key.key("enchantio", "affected_by_tunnelling");

    public final int  blocksPerLevel;
    public final boolean visualizeBreaking;
    public final Map<Key, Set<TagEntry<BlockType>>> affectedBlockTags;


    private TunnellingEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Map<Key, Set<TagEntry<BlockType>>> affectedBlockTags,
            int maxLevel,
            int blocksPerLevel,
            boolean visualizeBreaking
    ) {
        super(
                KEY,
                Component.translatable("enchantio.enchant.tunnelling", "Tunnelling"),
                anvilCost,
                maxLevel,
                weight,
                minimumCost,
                maximumCost,
                Set.of(EquipmentSlotGroup.MAINHAND),
                supportedItemTags,
                enchantTagKeys,
                exclusiveTagKeys
        );
        this.affectedBlockTags = Collections.unmodifiableMap(affectedBlockTags);
        this.blocksPerLevel = blocksPerLevel;
        this.visualizeBreaking = visualizeBreaking;
    }

    @Override
    public @NotNull Map<Key, Set<TagEntry<BlockType>>> getBlockTagsToRegister() {
        return affectedBlockTags;
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
                EnchantioConfig.getEnchantmentTagKeysFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "exclusiveTags",
                        List.of()
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
                                        "minecraft:end_stone",
                                        "minecraft:cobblestone",
                                        "minecraft:cobbled_deepslate"
                                )
                        ))
                ),
                EnchantioConfig.getInt(configurationSection, "maxLevel", 1),
                EnchantioConfig.getInt(configurationSection, "blocksPerLevel", 1),
                EnchantioConfig.getBoolean(configurationSection, "visualizeBreaking", true)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            EnchantioConfig.ENCHANTS.put(TunnellingEnchant.KEY, tunnellingEnchant);
        }

        return tunnellingEnchant;
    }

}
