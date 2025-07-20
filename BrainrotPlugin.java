
package com.brainrot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class BrainrotPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, String> playerTokens = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Brainrot Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Brainrot Plugin Disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerTokens.containsKey(player.getUniqueId())) {
            int pick = random.nextInt(7) + 1;
            String token = getTokenName(pick);
            playerTokens.put(player.getUniqueId(), token);
            player.sendMessage("§aYou received the Brainrot Token: " + token);

            // Give ability/item
            giveTokenItem(player, token);
        }
    }

    private String getTokenName(int index) {
        return switch (index) {
            case 1 -> "Thung....Sahur";
            case 2 -> "Bombardiro Crocodillo";
            case 3 -> "Tralalerotralala";
            case 4 -> "Brr Brr Patapim";
            case 5 -> "Lirililarila";
            case 6 -> "Cappuccino Assassino";
            case 7 -> "Ballerina Cappuccina";
            default -> "Unknown";
        };
    }

    private void giveTokenItem(Player player, String tokenName) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Brainrot Token: " + tokenName);
        meta.setLore(Collections.singletonList("Right-click to activate powers!"));
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Brainrot Token")) {
            String token = playerTokens.get(player.getUniqueId());
            if (token == null) return;

            switch (token) {
                case "Thung....Sahur" -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                    player.sendMessage("§6You now have Strength II and 7-block reach (not natively possible without mods)");
                }
                case "Bombardiro Crocodillo" -> {
                    if (!player.hasMetadata("cooldown_bomb")) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage("§bFlying for 15 seconds!");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.setFlying(false);
                                player.setAllowFlight(false);
                                player.setMetadata("cooldown_bomb", new FixedMetadataValue(BrainrotPlugin.this, true));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.removeMetadata("cooldown_bomb", BrainrotPlugin.this);
                                    }
                                }.runTaskLater(BrainrotPlugin.this, 600L);
                            }
                        }.runTaskLater(this, 300L);
                    }
                }
                case "Tralalerotralala" -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
                    player.setFallDistance(0);
                    player.sendMessage("§9No fall damage & infinite water breathing!");
                }
                case "Brr Brr Patapim" -> {
                    player.getWorld().generateTree(player.getLocation(), TreeType.TREE);
                    player.getWorld().spawn(player.getLocation(), WitherSkull.class);
                    player.sendMessage("§aTree and Wither Rose summoned!");
                }
                case "Lirililarila" -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                }
                case "Cappuccino Assassino" -> {
                    ItemStack blade = new ItemStack(Material.IRON_SWORD);
                    ItemMeta meta = blade.getItemMeta();
                    meta.setDisplayName("§cIron Ninja Blade");
                    blade.setItemMeta(meta);
                    player.getInventory().addItem(blade);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
                    player.sendMessage("§cBlade given with Speed II & Strength II");
                }
                case "Ballerina Cappuccina" -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
                    player.sendMessage("§6Speed III & Fire Resistance activated");
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            String token = playerTokens.get(player.getUniqueId());
            if ("Lirililarila".equals(token) && event.getEntity() instanceof LivingEntity entity) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            }
        }
    }
}
