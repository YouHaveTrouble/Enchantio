package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.EnchantioEnchant;
import me.youhavetrouble.enchantio.enchants.TunnellingEnchant;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("UnstableApiUsage")
public class TunnellingListener implements Listener {

    private final Enchantment tunnelling = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(TunnellingEnchant.KEY);
    private final NamespacedKey tunnellingKey = new NamespacedKey("enchantio", "tunnelling");
    private final NamespacedKey tunnellingBlockFaceKey = new NamespacedKey("enchantio", "tunellingblockface");


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTunnelBlockSideCheck(PlayerInteractEvent event) {
        if (tunnelling == null) return;
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (player.isSneaking() || GameMode.CREATIVE.equals(player.getGameMode())) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.containsEnchantment(tunnelling)) return;
        event.getPlayer().getPersistentDataContainer().set(tunnellingBlockFaceKey, PersistentDataType.STRING, event.getBlockFace().toString());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTunnelBlockBreak(BlockBreakEvent event) {
        if (tunnelling == null) return;
        Player player = event.getPlayer();
        if (GameMode.CREATIVE.equals(player.getGameMode())) return;
        EnchantioEnchant enchant = EnchantioConfig.ENCHANTS.get(TunnellingEnchant.KEY);
        if (!(enchant instanceof TunnellingEnchant tunnellingEnchant)) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!item.containsEnchantment(tunnelling)) return;
        Block block = event.getBlock();
        // TODO register a block tag and flatten it to check the block against it
        Registry.BLOCK.getTagValues(BlockTypeTagKeys.create(tunnellingKey)).contains(block.getType().asBlockType());



    }

}
