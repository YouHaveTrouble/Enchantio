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
public class BeheadingEnchant extends EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:beheading");

    public final double chanceToDropHeadPerLevel;

    private BeheadingEnchant(
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
        super(
                KEY,
                Component.translatable("enchantio.enchant.beheading", "Beheading"),
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
        this.chanceToDropHeadPerLevel = chanceToDropHeadPerLevel;
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
            EnchantioConfig.ENCHANTS.put(BeheadingEnchant.KEY, beheadingEnchant);
        }

        return beheadingEnchant;
    }

}
