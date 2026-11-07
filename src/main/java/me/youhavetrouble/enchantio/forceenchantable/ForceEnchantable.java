package me.youhavetrouble.enchantio.forceenchantable;


import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class ForceEnchantable implements Listener {

    private final Map<String, Integer> rawEnchantableness = new HashMap<>();
    private Map<ItemType, Integer> enchantableness = new HashMap<>();
    public final boolean enabled;

    public ForceEnchantable(ConfigurationSection config) {
        ConfigurationSection section = EnchantioConfig.getConfigSection(config, "force-enchantable");

        enabled = EnchantioConfig.getBoolean(section, "enabled", false);

        ConfigurationSection pairs = EnchantioConfig.getConfigSection(section, "items",
                Map.of(
                        "totem_of_undying", 1,
                        "elytra", 1
                )
        );

        for (String key : pairs.getKeys(false)) {
            try {
                rawEnchantableness.put(key, pairs.getInt(key));
            } catch (IllegalArgumentException e) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to parse integer from force-enchantable entry {}. Invalid value. Skipping.", key);
            }
        }
    }

    public void init() {
        if (rawEnchantableness.size() == enchantableness.size()) return;
        for (Map.Entry<String, Integer> entry : rawEnchantableness.entrySet()) {
            String key = entry.getKey();
            ItemType type;
            try {
                type = Registry.ITEM.get(Key.key(key));
            } catch (IllegalArgumentException e) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to add force-enchantable item {}. Invalid item key. Skipping.", key);
                continue;
            }
            if (type == null) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to add force-enchantable item {}. Invalid item type. Skipping.", key);
                continue;
            }
            int value = entry.getValue();
            if (value <= 0) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().warn("Failed to add force-enchantable item {}. Invalid value {}. Skipping.", key, value);
                continue;
            }
            enchantableness.put(type, value);
            if (EnchantioConfig.DEBUG) {
                Enchantio.getPlugin(Enchantio.class).getSLF4JLogger().info("Overriding enchantability of {} to {}", key, value);
            }
        }

        this.enchantableness = Collections.unmodifiableMap(enchantableness);
    }

    private void applyEnchantable(ItemStack itemStack) {
        if (itemStack == null) return;
        if (!enchantableness.containsKey(itemStack.getType().asItemType())) return;
        int value = enchantableness.get(itemStack.getType().asItemType());
        if (value < 0) {
            itemStack.unsetData(DataComponentTypes.ENCHANTABLE);
            return;
        }
        itemStack.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(value));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnchantableInInventory(InventoryOpenEvent event) {
        for (ItemStack itemStack : event.getInventory().getStorageContents()) {
            applyEnchantable(itemStack);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnchantablePlayerJoin(PlayerJoinEvent event) {
        for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
            applyEnchantable(itemStack);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnchantableItemDrop(ItemSpawnEvent event) {
        applyEnchantable(event.getEntity().getItemStack());
    }

}
