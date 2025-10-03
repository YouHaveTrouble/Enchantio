package me.youhavetrouble.enchantio;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import me.youhavetrouble.enchantio.enchants.EnchantioEnchant;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
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

        logger.info("Registering supported item tags");
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler((event) -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                logger.info("Registering item tag {}", enchant.getTagForSupportedItems().key());
                event.registrar().addToTag(
                        ItemTypeTagKeys.create(enchant.getTagForSupportedItems().key()),
                        enchant.getSupportedItems()
                );
            }
        }));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                logger.info("Registering enchantment {}", enchant.getKey());
                event.registry().register(TypedKey.create(RegistryKey.ENCHANTMENT, enchant.getKey()), enchantment -> {
                    enchantment.description(enchant.getDescription());
                    enchantment.anvilCost(enchant.getAnvilCost());
                    enchantment.maxLevel(enchant.getMaxLevel());
                    enchantment.weight(enchant.getWeight());
                    enchantment.minimumCost(enchant.getMinimumCost());
                    enchantment.maximumCost(enchant.getMaximumCost());
                    enchantment.activeSlots(enchant.getActiveSlots());
                    enchantment.supportedItems(event.getOrCreateTag(enchant.getTagForSupportedItems()));
                });
            }
        }));

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler((event) -> {
            for (EnchantioEnchant enchant : enchantioEnchants) {
                enchant.getEnchantTagKeys().forEach(enchantmentTagKey -> {
                    event.registrar().addToTag(enchantmentTagKey, Set.of(enchant.getTagEntry()));
                });
            }
        }));

    }

}
