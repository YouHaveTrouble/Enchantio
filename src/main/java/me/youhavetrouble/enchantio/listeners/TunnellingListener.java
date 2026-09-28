package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.EnchantioEnchant;
import me.youhavetrouble.enchantio.enchants.TunnellingEnchant;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
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
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class TunnellingListener implements Listener {

    private final static Set<Block> blockBreakSkips = new HashSet<>();
    private final Enchantment tunnelling = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(TunnellingEnchant.KEY);
    private final NamespacedKey tunnellingBlockFaceKey = new NamespacedKey("enchantio", "tunellingblockface");


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTunnelBlockSideCheck(PlayerInteractEvent event) {
        if (tunnelling == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (GameMode.CREATIVE.equals(player.getGameMode())) return;
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
        if (player.isSneaking()) return;
        String rawFace = player.getPersistentDataContainer().get(tunnellingBlockFaceKey, PersistentDataType.STRING);
        if (rawFace == null) return;
        EnchantioEnchant enchant = EnchantioConfig.ENCHANTS.get(TunnellingEnchant.KEY);
        if (!(enchant instanceof TunnellingEnchant tunnellingEnchant)) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        int enchantLevel = item.getEnchantmentLevel(tunnelling);
        if (enchantLevel <= 0) return;
        Block block = event.getBlock();
        BlockType blockType = block.getType().asBlockType();
        if (!Registry.BLOCK.getTagValues(BlockTypeTagKeys.create(TunnellingEnchant.AFFECTED_BLOCKS_KEY)).contains(blockType)) return;
        if (blockBreakSkips.contains(block)) return;
        BlockFace blockFace;
        try {
            blockFace = BlockFace.valueOf(rawFace);
        } catch (IllegalArgumentException e) {
            return;
        }
        Set<Block> blocksToBreak = getSquare(block, blockFace, tunnellingEnchant.getBlocksPerLevel() * enchantLevel);
        blockBreakSkips.addAll(blocksToBreak);
        for (Block b : blocksToBreak) {
            if (player.getInventory().getItemInMainHand().isEmpty()) {
                event.setCancelled(true);
                blockBreakSkips.removeAll(blocksToBreak);
                break;
            }
            event.getPlayer().breakBlock(b);
            blockBreakSkips.remove(b);
        }
    }

    private Set<Block> getSquare(Block block, BlockFace face, int range) {
        switch (face) {
            case DOWN, UP -> {
                return getBlocksToTunnel(block.getLocation(), range, 0, range);
            }
            case EAST, WEST -> {
                return getBlocksToTunnel(block.getLocation(), 0, range, range);
            }
            case NORTH, SOUTH -> {
                return getBlocksToTunnel(block.getLocation(), range, range, 0);
            }
        }
        return new HashSet<>();
    }

    /**
     * Get all blocks that will be broken by Tunnelling enchantment
     * @param base location at the centre of selection
     * @return All blocks that will get broken
     */
    private Set<Block> getBlocksToTunnel(Location base, int changeX, int changeY, int changeZ) {
        Set<Block> blocks = new HashSet<>();
        for (int x = (base.getBlockX() - changeX); x <= (base.getBlockX() + changeX); x++) {
            for (int y = (base.getBlockY() - changeY); y <= (base.getBlockY() + changeY); y++) {
                for (int z = (base.getBlockZ() - changeZ); z <= (base.getBlockZ() + changeZ); z++) {
                    Location loc = new Location(base.getWorld(), x, y, z);
                    if (base.equals(loc)) continue; // ignore the base block
                    Block block = loc.getBlock();
                    BlockType blockType = block.getType().asBlockType();
                    if (!Registry.BLOCK.getTagValues(BlockTypeTagKeys.create(TunnellingEnchant.AFFECTED_BLOCKS_KEY)).contains(blockType)) continue;
                    if (block.getType().isAir()) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

}
