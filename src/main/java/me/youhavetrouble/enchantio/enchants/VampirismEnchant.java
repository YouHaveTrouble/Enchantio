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
public class VampirismEnchant extends EnchantioEnchant {

    public static final Key KEY = Key.key("enchantio:vampirism_curse");

    public VampirismEnchant(
            int anvilCost,
            int weight,
            EnchantmentRegistryEntry.EnchantmentCost minimumCost,
            EnchantmentRegistryEntry.EnchantmentCost maximumCost,
            Collection<TagKey<Enchantment>> enchantTagKeys,
            Collection<TagKey<Enchantment>> exclusiveTagKeys,
            Collection<TagEntry<ItemType>> supportedItemTags,
            Collection<EquipmentSlotGroup> activeSlots
    ) {
        super(
                KEY,
                Component.translatable("enchantio.enchant.vampirism_curse", "Curse of Vampirism"),
                anvilCost,
                1,
                weight,
                minimumCost,
                maximumCost,
                activeSlots,
                supportedItemTags,
                enchantTagKeys,
                exclusiveTagKeys
        );
    }

    public static VampirismEnchant create(ConfigurationSection configurationSection) {
        VampirismEnchant vampirismEnchant = new VampirismEnchant(
                EnchantioConfig.getInt(configurationSection, "anvilCost", 1),
                EnchantioConfig.getInt(configurationSection, "weight", 2),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "minimumCost.base", 0),
                        EnchantioConfig.getInt(configurationSection, "minimumCost.additionalPerLevel", 3)
                ),
                EnchantmentRegistryEntry.EnchantmentCost.of(
                        EnchantioConfig.getInt(configurationSection, "maximumCost.base", 30),
                        EnchantioConfig.getInt(configurationSection, "maximumCost.additionalPerLevel", 1)
                ),
                EnchantioConfig.getEnchantmentTagKeysFromList(EnchantioConfig.getStringList(
                        configurationSection,
                        "enchantmentTags",
                        List.of("#in_enchanting_table", "#curse")
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
                                "#minecraft:enchantable/armor"
                        )
                )),
                EnchantioConfig.getEquipmentSlotGroups(EnchantioConfig.getStringList(
                        configurationSection,
                        "activeSlots",
                        List.of(
                                "ARMOR"
                        )
                ))
        );

        if (EnchantioConfig.getBoolean(configurationSection, "enabled", true)) {
            EnchantioConfig.ENCHANTS.put(VampirismEnchant.KEY, vampirismEnchant);
        }

        return vampirismEnchant;
    }

}
