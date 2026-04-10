package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.VolleyEnchant;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class VolleyListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment volley = registry.get(VolleyEnchant.KEY);

    private final Random random = ThreadLocalRandom.current();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVolley(ProjectileLaunchEvent event) {
        if (volley == null) return;
        Entity projectileEntity = event.getEntity();
        if (CreatureSpawnEvent.SpawnReason.ENCHANTMENT.equals(projectileEntity.getEntitySpawnReason())) return;
        if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) return;
        EntityEquipment equipment = shooter.getEquipment();
        if (equipment == null) return;
        ItemStack bow = equipment.getItemInMainHand();
        int level = bow.getEnchantmentLevel(volley);
        if (level <= 0) return;
        if (!(EnchantioConfig.ENCHANTS.get(VolleyEnchant.KEY) instanceof VolleyEnchant volleyEnchant)) return;
        double spread = volleyEnchant.getSpread();
        if (projectileEntity instanceof Arrow arrow) {
            for (int i = 0; i < level * volleyEnchant.getAdditionalArrowsPerLevel(); i++) {
                Vector velocity = arrow.getVelocity();
                double spreadX = (random.nextDouble() - 0.5) * spread;
                double spreadY = (random.nextDouble() - 0.5) * spread;
                double spreadZ = (random.nextDouble() - 0.5) * spread;
                Vector newVelocity = velocity.clone().add(new Vector(spreadX, spreadY, spreadZ));
                shooter.getWorld().spawn(arrow.getLocation(), Arrow.class, spawnedArrow -> {
                    spawnedArrow.setVelocity(newVelocity);
                    spawnedArrow.getPersistentDataContainer().set(volley.getKey(), PersistentDataType.BOOLEAN, true);
                    spawnedArrow.setCritical(arrow.isCritical());
                    spawnedArrow.setShooter(arrow.getShooter());
                    spawnedArrow.setHasLeftShooter(arrow.hasLeftShooter());
                    spawnedArrow.setBasePotionType(arrow.getBasePotionType());
                    spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    arrow.getCustomEffects().forEach(effect -> spawnedArrow.addCustomEffect(effect, false));
                }, CreatureSpawnEvent.SpawnReason.ENCHANTMENT);
            }
            return;
        }
        if (projectileEntity instanceof SpectralArrow arrow) {
            for (int i = 0; i < level; i++) {
                Vector velocity = arrow.getVelocity();
                double spreadX = (random.nextDouble() - 0.5) * spread;
                double spreadY = (random.nextDouble() - 0.5) * spread;
                double spreadZ = (random.nextDouble() - 0.5) * spread;
                Vector newVelocity = velocity.clone().add(new Vector(spreadX, spreadY, spreadZ));
                shooter.getWorld().spawn(arrow.getLocation(), SpectralArrow.class, spawnedArrow -> {
                    spawnedArrow.setVelocity(newVelocity);
                    spawnedArrow.getPersistentDataContainer().set(volley.getKey(), PersistentDataType.BOOLEAN, true);
                    spawnedArrow.setCritical(arrow.isCritical());
                    spawnedArrow.setShooter(arrow.getShooter());
                    spawnedArrow.setHasLeftShooter(arrow.hasLeftShooter());
                    spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }, CreatureSpawnEvent.SpawnReason.ENCHANTMENT);
            }
            return;
        }

    }

}
