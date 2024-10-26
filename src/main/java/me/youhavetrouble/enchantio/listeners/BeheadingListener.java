package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.BeheadingEnchant;
import me.youhavetrouble.enchantio.enchants.EnchantioEnchant;
import me.youhavetrouble.enchantio.events.EntityBeheadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;


@SuppressWarnings("UnstableApiUsage")
public class BeheadingListener implements Listener {

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment beheading = registry.get(BeheadingEnchant.KEY);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBeheading(EntityDeathEvent event) {
        if (beheading == null) return;
        EnchantioEnchant enchant = EnchantioConfig.ENCHANTS.get(BeheadingEnchant.KEY);
        if (!(enchant instanceof BeheadingEnchant beheadingEnchant)) return;
        if (event.getEntity().getKiller() == null) return;
        if (event.getDamageSource().isIndirect()) return;
        Entity killer = event.getDamageSource().getCausingEntity();
        if (killer == null) return;
        if (!(killer instanceof InventoryHolder inventoryHolder)) return;
        if (!(inventoryHolder.getInventory() instanceof EntityEquipment entityEquipment)) return;

        ItemStack weapon = entityEquipment.getItemInMainHand();

        if (!weapon.containsEnchantment(beheading)) return;

        double chance = weapon.getEnchantmentLevel(beheading) * beheadingEnchant.getChanceToDropHeadPerLevel();

        if (ThreadLocalRandom.current().nextDouble() > chance) return;

        ItemStack head = null;

        switch (event.getEntity().getType()) {
            case ZOMBIE -> {
                if (listContainsItemType(event.getDrops(), Material.ZOMBIE_HEAD)) return;
                head = new ItemStack(org.bukkit.Material.ZOMBIE_HEAD);
            }
            case PIGLIN -> {
                if (listContainsItemType(event.getDrops(), Material.PIGLIN_HEAD)) return;
                head = new ItemStack(org.bukkit.Material.PIGLIN_HEAD);
            }
            case WITHER_SKELETON -> {
                if (listContainsItemType(event.getDrops(), Material.WITHER_SKELETON_SKULL)) return;
                head = new ItemStack(org.bukkit.Material.WITHER_SKELETON_SKULL);
            }
            case SKELETON -> {
                if (listContainsItemType(event.getDrops(), Material.SKELETON_SKULL)) return;
                head = new ItemStack(org.bukkit.Material.SKELETON_SKULL);
            }
            case CREEPER -> {
                if (listContainsItemType(event.getDrops(), Material.CREEPER_HEAD)) return;
                head = new ItemStack(org.bukkit.Material.CREEPER_HEAD);
            }
            case ENDER_DRAGON -> {
                if (listContainsItemType(event.getDrops(), Material.DRAGON_HEAD)) return;
                head = new ItemStack(org.bukkit.Material.DRAGON_HEAD);
            }
            case PLAYER -> {
                if (listContainsItemType(event.getDrops(), Material.PLAYER_HEAD)) return;
                Player player = (Player) event.getEntity();
                head = getPlayerHead(player);
            }
        }

        if (head == null) return;
        EntityBeheadEvent beheadEvent = new EntityBeheadEvent(event.getEntity(), head);
        Bukkit.getPluginManager().callEvent(beheadEvent);
        if (beheadEvent.isCancelled()) return;
        event.getDrops().add(beheadEvent.getHeadToDrop());

    }

    private boolean listContainsItemType(@NotNull Iterable<ItemStack> list, @NotNull Material type) {
        for (ItemStack item : list) {
            if (type.equals(item.getType())) return true;
        }
        return false;
    }

    private ItemStack getPlayerHead(@NotNull Player player) {
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        return head;
    }

}
