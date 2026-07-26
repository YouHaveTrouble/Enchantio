package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.Enchantio;
import me.youhavetrouble.enchantio.enchants.VampirismEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class VampirismListener implements Listener {

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment vampirism = registry.get(VampirismEnchant.KEY);

    public VampirismListener() {
        if (vampirism == null) return;
        Enchantio enchantio = Enchantio.getPlugin(Enchantio.class);
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(enchantio, (task) -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean hasVampirism = Enchantio.getSumOfEnchantLevels(player.getEquipment(), vampirism) != 0;
                if (!hasVampirism) continue;
                player.getScheduler().execute(enchantio, () -> {
                    Location abovePlayer = player.getLocation().add(0, player.getEyeHeight() + 0.5, 0);
                    Block block = player.getWorld().getBlockAt(abovePlayer);
                    byte light = block.getLightFromSky();
                    if (light < 15) return;
                    int fireTicks = player.getFireTicks();

                    // Prefent fire animation from disappearing on the client
                    if (player.getFireTicks() < 20) {
                        fireTicks = 25;
                    }

                    player.setFireTicks(Math.max(fireTicks, player.getMaxFireTicks()));
                }, () -> {}, 0);
            }

        }, 1, 20);
    }

}
