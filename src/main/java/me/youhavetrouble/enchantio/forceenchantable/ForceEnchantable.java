package me.youhavetrouble.enchantio.forceenchantable;


import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ForceEnchantable implements Listener {

    public final Map<ItemType, Integer> enchantableness;
    public final boolean enabled;

    public ForceEnchantable(ConfigurationSection config) {
        ConfigurationSection section = EnchantioConfig.getConfigSection(config, "force-enchantable");

        enabled = EnchantioConfig.getBoolean(section, "enabled", false);

        Map<ItemType, Integer> enchantableness = new HashMap<>();

        ConfigurationSection pairs = EnchantioConfig.getConfigSection(section, "items");
        for (String key : pairs.getKeys(false)) {
            ItemType type = Registry.ITEM.get(Key.key(key));
            if (type == null) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to add force-enchantable item {}. Invalid item type. Skipping.", key);
                continue;
            }
            int value = pairs.getInt(key);
            if (value <= 0) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to add force-enchantable item {}. Invalid value {}. Skipping.", key, value);
                continue;
            }
            enchantableness.put(type, value);
        }

        this.enchantableness = Collections.unmodifiableMap(enchantableness);
        // TODO decide if items should just globally get enchantable component or only give it to items actively in
        //  enchant table item slot
    }

}
