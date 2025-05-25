package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import net.tidsrejsen.combat.CombatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {

    private final CombatManager manager;

    public CombatListener(CombatManager manager) {
        this.manager = manager;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!(e.getDamager() instanceof Player attacker)) return;

        if (!manager.isPvpEnabled()) {
            e.setCancelled(true);
            attacker.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8» &7PvP er ikke aktiveret."));
            return;
        }

        manager.tag(attacker, victim);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        int time = manager.getCombatTime(p.getUniqueId());
        if (time > 0) {
            p.setHealth(0);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lCOMBAT &8» &c" + p.getName() + " &7blev dræbt for at logge ud i kamp!"));
        }
        manager.clearCombatDataOnShutdown();
    }
}
