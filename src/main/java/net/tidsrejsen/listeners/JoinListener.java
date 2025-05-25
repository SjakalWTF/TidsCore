package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import net.tidsrejsen.util.ActionBarUtil;
import net.tidsrejsen.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JoinListener implements Listener {
    private final ConcurrentHashMap<UUID, Boolean> firstJoin = new ConcurrentHashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Main.getInstance().getCombatManager().setPvP(true);

        boolean isFirst = firstJoin.putIfAbsent(p.getUniqueId(), true) == null;

        if (isFirst) {
            // Skjul alle spillere for denne spiller, fordi det er første gang
            for (Player online : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
                if (!online.equals(p)) {
                    p.hidePlayer(online);
                }
            }



            new BukkitRunnable() {
                int step = 0;

                @Override
                public void run() {
                    switch (step++) {
                        case 0:
                            TitleUtil.sendTitle(p,
                                    ChatColor.translateAlternateColorCodes('&', "&6&lVelkommen &etil &6&lTids&eRejsen, &6" + p.getName() + "!"),
                                    ChatColor.YELLOW + "Udforsk og rejs igennem tid og sted",
                                    10, 100, 10);
                            break;
                        case 1:
                            ActionBarUtil.sendActionBar(p, "&e&nHeld og lykke!");
                            p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                            break;
                        case 2:
                            ActionBarUtil.sendActionBar(p, "&emc.&6&lTids&eRejsen.net");
                            break;
                        case 3:
                            new BukkitRunnable() {
                                int times = 5; // antal gange vi sender beskeden (hver sekund)

                                @Override
                                public void run() {
                                    if (times <= 0) {
                                        cancel();
                                        return;
                                    }
                                    ActionBarUtil.sendActionBar(p, "&CDu skal gå over til &4&lTELEFON BOX");
                                    times--;
                                }
                            }.runTaskTimer(Main.getInstance(), 0L, 20L);
                            break;
                    }
                }
            }.runTaskTimer(Main.getInstance(), 20L, 60L);

        } else {
            // Hvis ikke første gang: kald visibility handler for at vise spillere som normalt
            Main.getInstance().getVisibilityHandler().update(p);

            new BukkitRunnable() {
                int c = 0;

                @Override
                public void run() {
                    if (c < 3) {
                        ActionBarUtil.sendActionBar(p, "&6&lVelkommen &7» &etilbage til &6&lTids&eRejsen, &6" + p.getName() + "!");
                    } else cancel();
                    c++;
                }
            }.runTaskTimer(Main.getInstance(), 20L, 20L);
        }

        // Combat reset
        if (Main.getInstance().getCombatManager().getCombatTime(p.getUniqueId()) > 0) {
            Main.getInstance().getCombatManager().clearCombatDataOnShutdown();
            ActionBarUtil.sendActionBar(p, "&a&lCOMBAT &8» &aDin combat er restart");
        }
    }
}
