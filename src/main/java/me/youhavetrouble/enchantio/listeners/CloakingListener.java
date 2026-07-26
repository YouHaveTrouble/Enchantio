package me.youhavetrouble.enchantio.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.EnchantioConfig;
import me.youhavetrouble.enchantio.enchants.CloakingEnchant;
import me.youhavetrouble.enchantio.enchants.ExecutionerEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class CloakingListener implements Listener {

    private final HashMap<UUID, Integer> ticksSinceLastMovement = new HashMap<>();

    private final CloakingEnchant cloakingEnchant = (CloakingEnchant) EnchantioConfig.ENCHANTS.get(CloakingEnchant.KEY);
    private final PotionEffect cloakingEffect = new PotionEffect(PotionEffectType.INVISIBILITY,  3, 0, false, false, false);

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment cloaking = registry.get(CloakingEnchant.KEY);

    public CloakingListener() {
        if (cloaking == null) return;
        Enchantio enchantio = Enchantio.getPlugin(Enchantio.class);
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(enchantio, (task) -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.isSneaking()) {
                    ticksSinceLastMovement.put(player.getUniqueId(), 0);
                    continue;
                }
                int cloakingLevel = Enchantio.getSumOfEnchantLevels(player.getEquipment(), cloaking);
                if (cloakingLevel == 0) continue;
                ticksSinceLastMovement.computeIfPresent(player.getUniqueId(), (uuid, ticks) -> ticks + 1);
                if (ticksSinceLastMovement.getOrDefault(player.getUniqueId(), 0) < cloakingEnchant.getTicksToActivate()) continue;
                player.getScheduler().execute(enchantio, () -> player.addPotionEffect(cloakingEffect), () -> {}, 1);
            }
        }, 1, 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ticksSinceLastMovement.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ticksSinceLastMovement.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        ticksSinceLastMovement.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJump(PlayerJumpEvent event) {
        ticksSinceLastMovement.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ticksSinceLastMovement.put(event.getPlayer().getUniqueId(), 0);
    }





}
