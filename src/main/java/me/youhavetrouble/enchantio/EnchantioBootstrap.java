package me.youhavetrouble.enchantio;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.tag.TagEntry;
import me.youhavetrouble.enchantio.enchants.EnchantioEnchant;
import net.kyori.adventure.key.Key;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class EnchantioBootstrap implements PluginBootstrap {

    private final Logger logger = LoggerFactory.getLogger("Enchantio");

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        try {
            EnchantioConfig.init(context.getDataDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Collection<EnchantioEnchant> enchantioEnchants = EnchantioConfig.ENCHANTS.values();

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler((event) -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                if (EnchantioConfig.DEBUG) logger.info("Registering item tag {}", enchant.getTagForSupportedItems().key());
                event.registrar().addToTag(
                        ItemTypeTagKeys.create(enchant.getTagForSupportedItems().key()),
                        enchant.supportedItems
                );
            }
        }));

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.BLOCK).newHandler((event) -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                for (Map.Entry<Key, Set<TagEntry<BlockType>>> entry : enchant.getBlockTagsToRegister().entrySet()) {
                    if (EnchantioConfig.DEBUG) logger.info("Registering block tag {}", entry.getKey());
                    event.registrar().addToTag(
                            BlockTypeTagKeys.create(entry.getKey()),
                            entry.getValue()
                    );
                }
            }
        }));

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler((event) -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                for (var tagKey : enchant.enchantTagKeys) {
                    if (EnchantioConfig.DEBUG) logger.info("Adding enchantment {} to tag {}", enchant.key, tagKey.key());
                    event.registrar().addToTag(
                            tagKey,
                            Set.of(enchant.getTagEntry())
                    );
                }

                Set<TagEntry<Enchantment>> entries = new HashSet<>();
                for (var e : enchant.exclusiveTagKeys) {
                    entries.add(TagEntry.tagEntry(e));
                }

                if (EnchantioConfig.DEBUG) logger.info("Registering enchantment tag {}", enchant.getTagForExclusiveEnchants().key());
                event.registrar().addToTag(
                        EnchantmentTagKeys.create(enchant.getTagForExclusiveEnchants().key()),
                        entries
                );
            }
        }));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                if (EnchantioConfig.DEBUG) logger.info("Registering enchantment {}", enchant.key);
                event.registry().register(TypedKey.create(RegistryKey.ENCHANTMENT, enchant.key), enchantment -> {
                    enchantment.description(enchant.description);
                    enchantment.anvilCost(enchant.anvilCost);
                    enchantment.maxLevel(enchant.maxLevel);
                    enchantment.weight(enchant.weight);
                    enchantment.minimumCost(enchant.minimumCost);
                    enchantment.maximumCost(enchant.maximumCost);
                    enchantment.activeSlots(enchant.activeSlots);
                    enchantment.supportedItems(event.getOrCreateTag(enchant.getTagForSupportedItems()));
                    enchantment.exclusiveWith(event.getOrCreateTag(enchant.getTagForExclusiveEnchants()));
                });
            }
        }));

    }

}
