package me.youhavetrouble.enchantio;

import me.youhavetrouble.enchantio.enchants.*;
import me.youhavetrouble.enchantio.listeners.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public final class Enchantio extends JavaPlugin {

    @Override
    public void onEnable() {
        if (EnchantioConfig.ENCHANTS.containsKey(SoulboundEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new SoulboundListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(TelepathyEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new TelepathyListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(ReplantingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new ReplantingListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(ExecutionerEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new ExecutionerListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(BeheadingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new BeheadingListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(SmeltingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new SmeltingListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(AirbagEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new AirbagListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(HomecomingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new HomecomingListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(CloakingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new CloakingListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(VolleyEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new VolleyListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(WardEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new WardListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(TunnellingEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new TunnellingListener(), this);
        }

        if (EnchantioConfig.ENCHANTS.containsKey(PanicEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new PanicListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(VampirismEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new VampirismListener(), this);
        }
        if (EnchantioConfig.ENCHANTS.containsKey(InsomniaEnchant.KEY)) {
            getServer().getPluginManager().registerEvents(new InsomniaListener(), this);
        }
    }

    @Override
    public void onDisable() {
        if (getServer().isStopping()) return;
        getLogger().severe("Enchantio is being disabled without a server shutdown. Server will be shut down to prevent issues.");
        getServer().shutdown();
    }

    /**
     * Returns highest enchantment level of the given enchantment on the given equipment.
     * @param equipment The equipment to check for enchantments.
     * @param enchantment The enchantment to check for.
     * @return The highest level of the enchantment in the equipment.
     */
    public static int getHighestEnchantLevel(
            @NotNull EntityEquipment equipment,
            @NotNull Enchantment enchantment
    ) {
        int highestLevel = 0;
        Set<EquipmentSlotGroup> equipmentSlotGroups = enchantment.getActiveSlotGroups();

        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.FEET)) {
            ItemStack boots = equipment.getBoots();
            if (boots != null) {
                highestLevel = Math.max(highestLevel, boots.getEnchantmentLevel(enchantment));
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.LEGS)) {
            ItemStack leggings = equipment.getLeggings();
            if (leggings != null) {
                highestLevel = Math.max(highestLevel, leggings.getEnchantmentLevel(enchantment));
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.CHEST)) {
            ItemStack chestplate = equipment.getChestplate();
            if (chestplate != null) {
                highestLevel = Math.max(highestLevel, chestplate.getEnchantmentLevel(enchantment));
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.HEAD)) {
            ItemStack helmet = equipment.getHelmet();
            if (helmet != null) {
                highestLevel = Math.max(highestLevel, helmet.getEnchantmentLevel(enchantment));
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.MAINHAND)) {
            highestLevel = Math.max(highestLevel, equipment.getItemInMainHand().getEnchantmentLevel(enchantment));
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.OFFHAND)) {
            highestLevel = Math.max(highestLevel, equipment.getItemInOffHand().getEnchantmentLevel(enchantment));
        }

        return highestLevel;
    }

    /**
     * Returns the sum of enchantment levels of the given enchantment on the given equipment.
     * @param equipment The equipment to check for enchantments.
     * @param enchantment The enchantment to check for.
     * @return Sum of enchantment levels of the enchantment in the equipment.
     */
    public static int getSumOfEnchantLevels(
            @NotNull EntityEquipment equipment,
            @NotNull Enchantment enchantment
    ) {
        int level = 0;
        Set<EquipmentSlotGroup> equipmentSlotGroups = enchantment.getActiveSlotGroups();

        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.FEET)) {
            ItemStack boots = equipment.getBoots();
            if (boots != null) {
                level += boots.getEnchantmentLevel(enchantment);
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.LEGS)) {
            ItemStack leggings = equipment.getLeggings();
            if (leggings != null) {
                level += leggings.getEnchantmentLevel(enchantment);
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.CHEST)) {
            ItemStack chestplate = equipment.getChestplate();
            if (chestplate != null) {
                level += chestplate.getEnchantmentLevel(enchantment);
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.ARMOR) || equipmentSlotGroups.contains(EquipmentSlotGroup.HEAD)) {
            ItemStack helmet = equipment.getHelmet();
            if (helmet != null) {
                level += helmet.getEnchantmentLevel(enchantment);
            }
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.MAINHAND)) {
            ItemStack mainHand = equipment.getItemInMainHand();
            level += mainHand.getEnchantmentLevel(enchantment);
        }
        if (equipmentSlotGroups.contains(EquipmentSlotGroup.ANY) || equipmentSlotGroups.contains(EquipmentSlotGroup.HAND) || equipmentSlotGroups.contains(EquipmentSlotGroup.OFFHAND)) {
            ItemStack offHand = equipment.getItemInOffHand();
            level += offHand.getEnchantmentLevel(enchantment);
        }

        return level;
    }
}
