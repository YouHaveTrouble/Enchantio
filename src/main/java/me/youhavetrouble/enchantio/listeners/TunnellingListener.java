package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("UnstableApiUsage")
public class TunnellingListener implements Listener {

    private final static Set<Block> blockBreakSkips = new HashSet<>();
    private final static Map<UUID, BlockBreakData> blockBreakData = new ConcurrentHashMap<>();
    private final Enchantment tunnelling = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(TunnellingEnchant.KEY);
    private final TunnellingEnchant tunnellingEnchant;

    public TunnellingListener() {
        if (EnchantioConfig.ENCHANTS.get(TunnellingEnchant.KEY) instanceof TunnellingEnchant enchant) {
            this.tunnellingEnchant = enchant;
        } else {
            throw new RuntimeException("Tunnelling enchantment is not registered, but something tried to create a listener for it. Don't do that.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!tunnellingEnchant.shouldVisualizeBreaking()) return;
        event.getPlayer().getScheduler().runAtFixedRate(Enchantio.getPlugin(Enchantio.class), (task) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                task.cancel();
                return;
            }
            BlockBreakData data = blockBreakData.get(uuid);
            if (data == null) return;
            long elapsedTicks = player.getWorld().getFullTime() - data.startedBreakingAtTick();
            float breakSpeed = data.block.getBreakSpeed(player);
            float ticksNeeded = 1.0f / breakSpeed;
            float progress = Math.min(elapsedTicks / ticksNeeded, 1.0f);
            if (progress >= 1.0f) return;
            updateBlockBreakProgress(player, data, progress);
        }, null, 1, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTunnelBlockStartBreak(BlockDamageEvent event) {
        if (tunnelling == null) return;
        Player player = event.getPlayer();
        if (GameMode.CREATIVE.equals(player.getGameMode())) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) return;
        int enchantLevel = item.getEnchantmentLevel(tunnelling);
        if (enchantLevel == -1) return;
        if (!tunnellingEnchant.shouldVisualizeBreaking()) return;
        if (blockBreakData.containsKey(player.getUniqueId())) return;
        Block block = event.getBlock();
        Set<Block> blocksToBreak = getSquare(block, event.getBlockFace(), tunnellingEnchant.getBlocksPerLevel() * enchantLevel);
        blockBreakData.put(player.getUniqueId(), new BlockBreakData(block, block.getWorld().getFullTime(), event.getBlockFace(), blocksToBreak));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTunnelBlockStopBreak(BlockDamageAbortEvent event) {
        BlockBreakData data = blockBreakData.remove(event.getPlayer().getUniqueId());
        if (data == null) return;
        updateBlockBreakProgress(event.getPlayer(), data, 0f);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        blockBreakData.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTpToAnotherWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        BlockBreakData data = blockBreakData.get(player.getUniqueId());
        if (data == null) return;
        if (data.block.getWorld().getUID().equals(player.getWorld().getUID())) return;
        blockBreakData.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTunnelBlockBreak(BlockBreakEvent event) {
        if (tunnelling == null) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockBreakData data = blockBreakData.remove(player.getUniqueId());
        if (blockBreakSkips.contains(block)) return;
        if (data != null) updateBlockBreakProgress(player, data, 0f);
        if (GameMode.CREATIVE.equals(player.getGameMode())) return;
        if (player.isSneaking()) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        int enchantLevel = item.getEnchantmentLevel(tunnelling);
        if (enchantLevel <= 0) return;
        BlockType blockType = block.getType().asBlockType();
        if (!Registry.BLOCK.getTagValues(BlockTypeTagKeys.create(TunnellingEnchant.AFFECTED_BLOCKS_KEY)).contains(blockType)) return;
        if (data == null) return;
        Set<Block> blocksToBreak = getSquare(block, data.blockFace, tunnellingEnchant.getBlocksPerLevel() * enchantLevel);
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

    private void updateBlockBreakProgress(Player player, BlockBreakData data, float progress) {
        if (!tunnellingEnchant.shouldVisualizeBreaking()) return;
        int id = Integer.MIN_VALUE;
        for (Block block : data.blocksToSyncDamage) {
            player.sendBlockDamage(block.getLocation(), progress, id++);
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

    private record BlockBreakData(Block block, long startedBreakingAtTick, BlockFace blockFace, Set<Block> blocksToSyncDamage) {}

}
