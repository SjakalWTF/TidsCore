package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementListener implements Listener {
    private final Map<UUID, Location> lastLoc = new HashMap<>();
    private final Map<UUID, Long> lastWarning = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!Main.getInstance().getCombatManager().isPvpEnabled() &&
                isRestricted(e.getBlockPlaced().getType())) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8&l» &7Du kan ikke placere blocks, når PvP er slukket."));
        }
    }

    private boolean isRestricted(Material m) {
        return m == Material.FIRE || m == Material.CACTUS || m == Material.TNT || m == Material.LAVA;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        int t = Main.getInstance().getCombatManager().getCombatTime(uuid);

        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null || from.distanceSquared(to) == 0) return;

        // Opdater sidste sikre position kun hvis spilleren bevæger sig
        if (!lastLoc.containsKey(uuid) || lastLoc.get(uuid).distanceSquared(from) > 0.5) {
            lastLoc.put(uuid, from.clone());
        }

        // Check om spilleren er i sikker zone
        Block b = to.clone().subtract(0, 1, 0).getBlock();
        byte d = b.getData();
        boolean safe = (b.getType() == Material.WOOL && d == 14) ||
                (b.getType() == Material.CARPET && d == 14) ||
                b.getType() == Material.GLOWSTONE ||
                (b.getType() == Material.CLAY && d == 14);

        if (t > 0 && safe) {
            Location prev = lastLoc.get(uuid);
            if (prev != null) {
                Location knockBack = prev.clone().add(to.getDirection().multiply(-0.75));
                knockBack.setYaw(p.getLocation().getYaw());
                knockBack.setPitch(p.getLocation().getPitch());
                p.teleport(knockBack);
            }

            long now = System.currentTimeMillis();
            if (!lastWarning.containsKey(uuid) || now - lastWarning.get(uuid) > 1500) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lCOMBAT &8» &7Du kan ikke søge sikkerhed i kamp!"));
                p.playSound(p.getLocation(), Sound.ANVIL_LAND, 0.3f, 1.2f);
                lastWarning.put(uuid, now);
            }

            e.setCancelled(true);
        }
    }
}
