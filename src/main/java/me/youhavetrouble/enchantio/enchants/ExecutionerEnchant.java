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


import java.util.Collection;
import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public class ExecutionerEnchant extends EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:executioner");

    public final double damageMultiplierPerLevel, maxDamageHpThreshold;

    private ExecutionerEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots,
            int maxLevel,
            double damageMultiplierPerLevel,
            double maxDamageHpThreshold
    ) {
        super(
                KEY,
                Component.translatable("enchantio.enchant.executioner","Executioner"),
                anvilCost,
                maxLevel,
                weight,
                minimumCost,
                maximumCost,
                activeSlots,
                supportedItemTags,
                enchantTagKeys,
                exclusiveTagKeys
        );
        this.damageMultiplierPerLevel = damageMultiplierPerLevel;
        this.maxDamageHpThreshold = maxDamageHpThreshold;
    }

    public static ExecutionerEnchant create(ConfigurationSection configurationSection) {
        ExecutionerEnchant executionerEnchant = new ExecutionerEnchant(
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
                        List.of("#in_enchanting_table", "#minecraft:exclusive_set/damage")
                )),
                EnchantioConfig.getEnchantmentTagKeysFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "exclusiveTags",
                        List.of("#minecraft:exclusive_set/damage")
                )),
                EnchantioConfig.getItemTagEntriesFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "supportedItemTags",
                        List.of(
                                "#minecraft:enchantable/weapon"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "MAINHAND"
                        )
                )),
                EnchantioConfig.getInt(configurationSection, "maxLevel", 5),
                EnchantioConfig.getDouble(configurationSection, "damageMultiplierPerLevel", 0.05),
                EnchantioConfig.getDouble(configurationSection, "maxDamageHpThreshold", 0.25)
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            EnchantioConfig.ENCHANTS.put(ExecutionerEnchant.KEY, executionerEnchant);
        }

        return executionerEnchant;
    }

}
