package me.youhavetrouble.enchantio.enchants;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.youhavetrouble.enchantio.EnchantioConfig.ENCHANTS;

@SuppressWarnings("UnstableApiUsage")
public class CloakingEnchant implements EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:cloaking");

    private final int anvilCost, weight, ticksToActivate;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags;
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Set<EquipmentSlotGroup> activeSlots = new HashSet<>();

    public CloakingEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            boolean canGetFromEnchantingTable,
            Set<TagEntry<ItemType>> supportedItemTags,
            Set<EquipmentSlotGroup> activeSlots,
            int ticksToActivate
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.supportedItemTags = supportedItemTags;
        this.ticksToActivate = ticksToActivate;
        this.activeSlots.addAll(activeSlots);
        if (canGetFromEnchantingTable) {
            enchantTagKeys.add(EnchantmentTagKeys.IN_ENCHANTING_TABLE);
        }
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("enchantio.enchant.cloaking", "Cloaking");
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

    @Override
    public @NotNull Set<TagKey<Enchantment>> getEnchantTagKeys() {
        return Collections.unmodifiableSet(enchantTagKeys);
    }

    public int getTicksToActivate() {
        return ticksToActivate;
    }

    public static CloakingEnchant create(ConfigurationSection configurationSection) {
        CloakingEnchant cloakingEnchant = new CloakingEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 1),
                EnchantioConfig.getInt(configurationSection, "weight", 10),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "minimumCost.base", 25),
                        EnchantioConfig.getInt(configurationSection, "minimumCost.additionalPerLevel", 1)
                ),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "maximumCost.base", 65),
                        EnchantioConfig.getInt(configurationSection, "maximumCost.additionalPerLevel", 1)
                ),
                EnchantioConfig.getBoolean(configurationSection, "canGetFromEnchantingTable", true),
                EnchantioConfig.getTagsFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "supportedItemTags",
                        List.of(
                                "#minecraft:leg_armor"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "LEGS"
                        )
                )),
                EnchantioConfig.getInt(configurationSection, "ticksToActivate", 30)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            ENCHANTS.put(CloakingEnchant.KEY, cloakingEnchant);
        }

        return cloakingEnchant;
    }

}