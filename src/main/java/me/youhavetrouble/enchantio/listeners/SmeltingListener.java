package me.youhavetrouble.enchantio.listeners;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.youhavetrouble.enchantio.enchants.SmeltingEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmeltingListener implements Listener {

    private final Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Enchantment smelting = registry.get(SmeltingEnchant.KEY);
    private final Map<ItemStack, ItemStack> smeltingCache = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSmeltingEnchantSmelt(BlockDropItemEvent event) {
        if (smelting == null) return;
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (!tool.containsEnchantment(smelting)) return;

        BlockState block = event.getBlockState();
        if (block instanceof BlockInventoryHolder) return;

        for (Item item : event.getItems()) {
            int amount = item.getItemStack().getAmount();
            ItemStack smeltedItem = getSmeltedItem(item.getItemStack());
            if (smeltedItem == null) continue;
            item.setItemStack(smeltedItem.asQuantity(amount));
        }
    }

    /**
     * Gets the smelted item from the given item stack. If item stack is not smeltable, returns the item stack itself.
     */
    private ItemStack getSmeltedItem(@NotNull ItemStack itemStack) {
        ItemStack singleItem = itemStack.asOne();
        if (smeltingCache.containsKey(singleItem)) return smeltingCache.get(singleItem);

        for (@NotNull Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (!(recipe instanceof FurnaceRecipe furnaceRecipe)) continue;
            if (!furnaceRecipe.getInputChoice().test(singleItem)) continue;
            ItemStack result = furnaceRecipe.getResult();
            smeltingCache.put(singleItem, result);
            return result;
        }
        smeltingCache.put(singleItem, null);
        return null;
    }

}
