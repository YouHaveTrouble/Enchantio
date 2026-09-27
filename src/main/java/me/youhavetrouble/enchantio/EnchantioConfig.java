package me.youhavetrouble.enchantio;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import me.youhavetrouble.enchantio.enchants.*;
import net.kyori.adventure.key.Key;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class EnchantioConfig {

    public static final Map<Key, EnchantioEnchant> ENCHANTS = new HashMap<>();
    private static boolean initialized = false;

    protected static void init(Path filePath) throws IOException {
        if (initialized) {
            return;
        }
        initialized = true;
        File file = filePath.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        File configFile = new File(filePath.toFile(), "config.yml");
        configFile.createNewFile();

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection enchantsSection = getConfigSection(configuration, "enchants");
        migrateEnchantTags(enchantsSection);

        ConfigurationSection soulboundSection = getConfigSection(enchantsSection, "soulbound");
        SoulboundEnchant.create(soulboundSection);

        ConfigurationSection telepathySection = getConfigSection(enchantsSection, "telepathy");
        TelepathyEnchant.create(telepathySection);

        ConfigurationSection replantingSection = getConfigSection(enchantsSection, "replanting");
        ReplantingEnchant.create(replantingSection);

        ConfigurationSection executionerSection = getConfigSection(enchantsSection, "executioner");
        ExecutionerEnchant.create(executionerSection);

        ConfigurationSection beheadingSection = getConfigSection(enchantsSection, "beheading");
        BeheadingEnchant.create(beheadingSection);

        ConfigurationSection smeltingSection = getConfigSection(enchantsSection, "smelting");
        SmeltingEnchant.create(smeltingSection);

        ConfigurationSection airbagSection = getConfigSection(enchantsSection, "airbag");
        AirbagEnchant.create(airbagSection);

        ConfigurationSection homecomingSection = getConfigSection(enchantsSection, "homecoming");
        HomecomingEnchant.create(homecomingSection);

        ConfigurationSection cloakingSection = getConfigSection(enchantsSection, "cloaking");
        CloakingEnchant.create(cloakingSection);

        ConfigurationSection volleySection = getConfigSection(enchantsSection, "volley");
        VolleyEnchant.create(volleySection);

        ConfigurationSection wardSection = getConfigSection(enchantsSection, "ward");
        WardEnchant.create(wardSection);

        ConfigurationSection tunnellingSection = getConfigSection(enchantsSection, "tunnelling");
        TunnellingEnchant.create(tunnellingSection);

        ConfigurationSection cursesSection = getConfigSection(configuration, "curses");
        migrateEnchantTags(cursesSection);

        ConfigurationSection panicSection = getConfigSection(cursesSection, "panic");
        PanicEnchant.create(panicSection);

        ConfigurationSection vampirismSection = getConfigSection(cursesSection, "vampirism");
        VampirismEnchant.create(vampirismSection);

        ConfigurationSection insomniaSection = getConfigSection(cursesSection, "insomnia");
        InsomniaEnchant.create(insomniaSection);

        configuration.save(configFile);
    }

    public static List<String> getStringList(ConfigurationSection section, String key, List<String> defaultValue) {
        List<String> list = section.contains(key) ? section.getStringList(key) : null;
        if (list == null) {
            section.set(key, defaultValue);
            return defaultValue;
        }
        return list;
    }

    public static String getString(ConfigurationSection section, String key, String defaultValue) {
        String value = section.contains(key) ? section.getString(key) : null;
        if (value == null) {
            section.set(key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static int getInt(ConfigurationSection section, String key, int defaultValue) {
        int value = section.contains(key) ? section.getInt(key) : -1;
        if (value == -1) {
            section.set(key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static double getDouble(ConfigurationSection section, String key, double defaultValue) {
        double value = section.contains(key) ? section.getDouble(key) : -1;
        if (value == -1) {
            section.set(key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static boolean getBoolean(ConfigurationSection section, String key, boolean defaultValue) {
        if (!section.contains(key)) {
            section.set(key, defaultValue);
            return defaultValue;
        }
        return section.getBoolean(key);
    }

    public static void migrateEnchantTags(@NotNull ConfigurationSection section) {
        for (String sectionKey : section.getKeys(false)) {
            ConfigurationSection enchantSection = section.getConfigurationSection(sectionKey);
            if (enchantSection == null) continue;
            if (!enchantSection.isSet("canGetFromEnchantingTable") || enchantSection.isSet("enchantmentTags")) return;
            boolean canGetFromEnchantingTable = section.getBoolean("canGetFromEnchantingTable", true);
            section.set("enchantmentTags", canGetFromEnchantingTable ? List.of("#in_enchanting_table") : List.of());
        }
    }

    public static Set<EquipmentSlotGroup> getEquipmentSlotGroups(@NotNull List<String> slots) {
        Set<EquipmentSlotGroup> equipmentSlotGroups = new HashSet<>();
        for (String slot : slots) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.getByName(slot);
            if (equipmentSlotGroup == null) {
                continue;
            }
            equipmentSlotGroups.add(equipmentSlotGroup);
        }
        return equipmentSlotGroups;
    }

    public static Set<TagEntry<ItemType>> getItemTagEntriesFromList(@NotNull List<String> tags) {
        Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();
        for (String itemTag : tags) {
            if (itemTag == null) continue;
            if (itemTag.startsWith("#")) {
                itemTag = itemTag.substring(1);
                try {
                    Key key = Key.key(itemTag);
                    TagKey<ItemType> tagKey = ItemTypeTagKeys.create(key);
                    TagEntry<ItemType> tagEntry = TagEntry.tagEntry(tagKey);
                    supportedItemTags.add(tagEntry);
                } catch (IllegalArgumentException e) {
                }
                continue;
            }
            try {
                Key key = Key.key(itemTag);
                TypedKey<ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, key);
                TagEntry<ItemType> tagEntry = TagEntry.valueEntry(typedKey);
                supportedItemTags.add(tagEntry);
            } catch (IllegalArgumentException | NullPointerException e) {
            }
        }
        return supportedItemTags;
    }

    public static Set<TagKey<Enchantment>> getEnchantmentTagKeysFromList(@NotNull List<String> tags) {
        Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
        for (String enchantmentTag : tags) {
            if (enchantmentTag == null) continue;
            if (enchantmentTag.startsWith("#")) {
                enchantmentTag = enchantmentTag.substring(1);
                try {
                    Key key = Key.key(enchantmentTag);
                    TagKey<Enchantment> tagKey = EnchantmentTagKeys.create(key);
                    enchantTagKeys.add(tagKey);
                } catch (IllegalArgumentException ignored) {
                }
                continue;
            }
            try {
                Key key = Key.key(enchantmentTag);
                TypedKey<Enchantment> typedKey = TypedKey.create(RegistryKey.ENCHANTMENT, key);
                TagKey<Enchantment> tagKey = EnchantmentTagKeys.create(key);
                enchantTagKeys.add(tagKey);
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }
        return enchantTagKeys;
    }

    public static Set<TagEntry<BlockType>> getBlockTagKeysFromList(@NotNull List<String> tags) {
        Set<TagEntry<BlockType>> blockTagKeys = new HashSet<>();
        for (String blockTag : tags) {
            if (blockTag == null) continue;
            if (blockTag.startsWith("#")) {
                blockTag = blockTag.substring(1);
                try {
                    Key key = Key.key(blockTag);
                    TagKey<BlockType> tagKey = BlockTypeTagKeys.create(key);
                    TagEntry<BlockType> tagEntry = TagEntry.tagEntry(tagKey);
                    blockTagKeys.add(tagEntry);
                } catch (IllegalArgumentException e) {
                }
                continue;
            }
            try {
                Key key = Key.key(blockTag);
                TypedKey<BlockType> typedKey = TypedKey.create(RegistryKey.BLOCK, key);
                TagEntry<BlockType> tagEntry = TagEntry.valueEntry(typedKey);
                blockTagKeys.add(tagEntry);
            } catch (IllegalArgumentException | NullPointerException e) {
            }
        }
        return blockTagKeys;
    }

    public static ConfigurationSection getConfigSection(ConfigurationSection section, String key) {
        ConfigurationSection value = section.getConfigurationSection(key);
        if (value == null) {
            value = section.createSection(key);
        }
        return value;
    }

}
