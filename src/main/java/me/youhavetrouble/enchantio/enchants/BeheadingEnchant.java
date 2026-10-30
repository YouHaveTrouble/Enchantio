package me.youhavetrouble.enchantio.enchants;

import io.papermc.paper.registry.RegistryKey;
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
public class BeheadingEnchant implements EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:beheading");

    private final int anvilCost, weight, maxLevel;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
    private final double chanceToDropHeadPerLevel;
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Set<TagKey<Enchantment>> exclusiveTagKeys = new HashSet<>();
    private final Set<EquipmentSlotGroup> activeSlots = new HashSet<>();

    public BeheadingEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots,
            int maxLevel,
            double chanceToDropHeadPerLevel
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.supportedItemTags.addAll(supportedItemTags);
        this.maxLevel = maxLevel;
        this.chanceToDropHeadPerLevel = chanceToDropHeadPerLevel;
        this.activeSlots.addAll(activeSlots);
        this.enchantTagKeys.addAll(enchantTagKeys);
        this.exclusiveTagKeys.addAll(exclusiveTagKeys);
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("enchantio.enchant.beheading", "Beheading");
    }

    @Override
    public int getAnvilCost() {
        return anvilCost;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    public double getChanceToDropHeadPerLevel() {
        return chanceToDropHeadPerLevel;
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
    public @NotNull TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key("enchantio:beheading_enchantable"));
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
    public @NotNull Set<TagKey<Enchantment>> getExclusiveTagKeys() {
        return Collections.unmodifiableSet(exclusiveTagKeys);
    }

    public static BeheadingEnchant create(ConfigurationSection configurationSection) {
        BeheadingEnchant beheadingEnchant = new BeheadingEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 1),
                EnchantioConfig.getInt(configurationSection, "weight", 1),
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
                                "#minecraft:axes"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "MAINHAND"
                        )
                )),
                EnchantioConfig.getInt(configurationSection, "maxLevel", 1),
                EnchantioConfig.getDouble(configurationSection, "chanceToDropHeadPerLevel", 0.1)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            ENCHANTS.put(BeheadingEnchant.KEY, beheadingEnchant);
        }

        return beheadingEnchant;
    }

}
