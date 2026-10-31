package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.AirbagEnchant;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;

public class AirbagListener implements Listener {

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment airbag = registry.get(AirbagEnchant.KEY);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onCushionedFall(EntityDamageEvent event) {
        if (airbag == null) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL)) return;

        double damage = event.getDamage();

        EntityEquipment entityEquipment = livingEntity.getEquipment();
        if (entityEquipment == null) return;

        int levels = Enchantio.getSumOfEnchantLevels(entityEquipment, airbag);
        if (levels == 0) return;

        AirbagEnchant airbagEnchant = (AirbagEnchant) EnchantioConfig.ENCHANTS.get(AirbagEnchant.KEY);
        double percentageDamageReduction = Math.min(1, levels * airbagEnchant.damageReductionPerLevel);
        event.setDamage(damage * (1 - percentageDamageReduction));
    }

}
