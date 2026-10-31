package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.enchants.SoulboundEnchant;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SoulboundListener implements Listener {

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment soulbound = registry.get(SoulboundEnchant.KEY);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onSoulboundEnchantDeath(PlayerDeathEvent event) {
        if (soulbound == null) return;
        for (ItemStack item : event.getPlayer().getInventory()) {
            if (item == null) continue;
            if (!item.containsEnchantment(soulbound)) continue;
            event.getItemsToKeep().add(item);
            event.getDrops().remove(item);
        }
    }

}
