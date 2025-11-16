package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.TelepathyEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TelepathyListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment telepathy = registry.get(TelepathyEnchant.KEY);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onTelepathyTool(BlockDropItemEvent event) {
        if (telepathy == null) return;
        if (!(EnchantioConfig.ENCHANTS.get(TelepathyEnchant.KEY) instanceof TelepathyEnchant telepathyEnchant)) return;
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!tool.containsEnchantment(telepathy)) return;
        for (Item item : event.getItems()) {
            item.setPickupDelay(0);
            if (!telepathyEnchant.isOnlyUserCanPickupItems()) continue;
            item.setOwner(event.getPlayer().getUniqueId());
        }

        // If there's ever a performance problem here, it's the following
        Bukkit.getScheduler().runTask(Enchantio.getPlugin(Enchantio.class), () -> event.getItems().forEach((item) -> {
            if (item == null || item.isDead()) return;
            item.teleport(event.getPlayer(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }));
    }

}
