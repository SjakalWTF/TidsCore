package net.tidsrejsen.combat;

import net.tidsrejsen.Main;
import net.tidsrejsen.util.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CombatListener implements Listener {

    private final CombatManager manager;

    public CombatListener(CombatManager manager) {
        this.manager = manager;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || event.getDamage() <= 0) return;

        if (!(event.getEntity() instanceof Player victim)) return;

        if (event.getDamager() instanceof Player attacker) {
            handlePlayerAttack(victim, attacker);
        } else if (event.getDamager() instanceof Projectile projectile &&
                projectile.getShooter() instanceof Player shooter) {
            handleProjectileAttack(victim, shooter);
        }
    }

    private void handlePlayerAttack(Player victim, Player attacker) {
        if (!manager.isPvpEnabled()) {
            attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8» &7PvP er ikke aktiveret."));
            return;
        }

        manager.tag(attacker, victim);

        attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du angreb &c" + victim.getName() + "&7 og er nu i kamp!"));
        victim.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du blev angrebet af &c" + attacker.getName() + "&7 og er nu i kamp!"));
    }

    private void handleProjectileAttack(Player victim, Player shooter) {
        if (!manager.isPvpEnabled()) {
            shooter.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8» &7PvP er ikke aktiveret."));
            return;
        }

        manager.tag(shooter, victim);

        shooter.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du skød &c" + victim.getName() + "&7 og er nu i kamp!"));
        victim.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du blev skudt af &c" + shooter.getName() + "&7 og er nu i kamp!"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (manager.getCombatTime(uuid) > 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lCOMBAT &8» &c" + player.getName() + " &7loggede ud under kamp!"));
            player.getWorld().strikeLightningEffect(player.getLocation());
            manager.clearCombatData(uuid);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        UUID victimUuid = victim.getUniqueId();

        if (manager.getCombatTime(victimUuid) > 0) {
            manager.clearCombatData(victimUuid);
            Player killer = victim.getKiller();

            String deathMessage;
            if (killer != null) {
                deathMessage = ChatColor.translateAlternateColorCodes('&',
                        "&c&lCOMBAT &8» &7Du blev dræbt af &c" + killer.getName() + "&7.");
                handleKillerRewards(killer, victim);
            } else {
                deathMessage = ChatColor.translateAlternateColorCodes('&',
                        "&c&lCOMBAT &8» &7Du døde under kamp.");
            }

            ActionBarUtil.sendActionBar(victim, deathMessage);
            victim.sendMessage(deathMessage);
        }
    }

    private void handleKillerRewards(Player killer, Player victim) {
        String killerMsg = ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du har dræbt &c" + victim.getName() + "&7.");
        killer.sendMessage(killerMsg);

        // Give killer rewards
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 2);
        ItemMeta meta = steak.getItemMeta();
        meta.setDisplayName(ChatColor.RED + victim.getName() + "'s kød");
        steak.setItemMeta(meta);
        killer.getInventory().addItem(steak);

        // Heal killer
        killer.setHealth(Math.min(killer.getHealth() + 6.0, killer.getMaxHealth()));

        // Give speed effect
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
    }
}