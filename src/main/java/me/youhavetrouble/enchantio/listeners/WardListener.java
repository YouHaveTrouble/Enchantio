package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.*;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class WardListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment ward = registry.get(WardEnchant.KEY);

    private final NamespacedKey wardKey = new NamespacedKey(WardEnchant.KEY.namespace(), WardEnchant.KEY.value());

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageWithWard(EntityDamageByEntityEvent event) {
        if (ward == null) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;
        ItemStack item = EnchantioEnchant.findFirstWithEnchant(equipment, ward);
        if (item == null) return;
        if (!(EnchantioConfig.ENCHANTS.get(WardEnchant.KEY) instanceof WardEnchant wardEnchant)) return;
        if (entity instanceof HumanEntity humanEntity) {
            if (humanEntity.getCooldown(item) > 0) return;
            if (wardEnchant.cooldownTicks > 0) {
                humanEntity.setCooldown(item, wardEnchant.cooldownTicks);
            }
        } else {
            if (wardEnchant.cooldownTicks > 0) {
                // non-human entities don't support cooldowns, so simulate it with a timestamp
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                Long lastWard = pdc.get(wardKey, PersistentDataType.LONG);
                if (lastWard != null && Instant.now().toEpochMilli() - lastWard < 50L * wardEnchant.cooldownTicks) return;
                pdc.set(wardKey, PersistentDataType.LONG, Instant.now().toEpochMilli());
            }
        }
        item.damage((int) Math.ceil(event.getFinalDamage()), entity);
        entity.getWorld().playSound(entity, wardEnchant.blockSound, SoundCategory.MASTER, 1, 1);
        event.setDamage(0);
    }

}
