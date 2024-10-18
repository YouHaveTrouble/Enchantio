package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import me.youhavetrouble.enchantio.enchants.TelepathyEnchant;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;


public class TelepathyListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onTelepathyTool(BlockDropItemEvent event) {
        Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment telepathy = registry.get(TelepathyEnchant.KEY);
        if (telepathy == null) return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!tool.containsEnchantment(telepathy)) return;

        for (Item item : event.getItems()) {
            item.teleport(event.getPlayer(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            item.setPickupDelay(0);
        }

    }

}
