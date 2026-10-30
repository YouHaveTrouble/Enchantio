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
public class TelepathyEnchant implements EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:telepathy");

    private final int anvilCost, weight;
    private final EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private final EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private final Set<TagKey<Enchantment>> exclusiveTagKeys = new HashSet<>();
    private final Set<EquipmentSlotGroup> activeSlots = new HashSet<>();
    private final boolean onlyUserCanPickupItems;

    public TelepathyEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots,
            boolean onlyUserCanPickupItems
    ) {
        this.anvilCost = anvilCost;
        this.weight = weight;
        this.minimumCost = minimumCost;
        this.maximumCost = maximumCost;
        this.supportedItemTags.addAll(supportedItemTags);
        this.activeSlots.addAll(activeSlots);
        this.enchantTagKeys.addAll(enchantTagKeys);
        this.exclusiveTagKeys.addAll(exclusiveTagKeys);
        this.onlyUserCanPickupItems = onlyUserCanPickupItems;
    }

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("enchantio.enchant.telepathy", "Telepathy");
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

    @Override
    public @NotNull Set<TagKey<Enchantment>> getExclusiveTagKeys() {
        return Collections.unmodifiableSet(exclusiveTagKeys);
    }

    public boolean isOnlyUserCanPickupItems() {
        return onlyUserCanPickupItems;
    }

    public static TelepathyEnchant create(ConfigurationSection configurationSection) {
        TelepathyEnchant telepathyEnchant = new TelepathyEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 1),
                EnchantioConfig.getInt(configurationSection, "weight", 5),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "minimumCost.base", 15),
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
                                "#minecraft:enchantable/mining"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "MAINHAND"
                        )
                )),
                EnchantioConfig.getBoolean(configurationSection, "onlyUserCanPickupItems", false)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            ENCHANTS.put(TelepathyEnchant.KEY, telepathyEnchant);
        }

        return telepathyEnchant;
    }

}
