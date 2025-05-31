package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import net.tidsrejsen.player.PlayerData;
import net.tidsrejsen.util.EventOptimizer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final Main plugin;

    public PlayerQuitListener(Main plugin) {
        this.plugin = plugin;
    }

    static {
        EventOptimizer.monitorEvent(PlayerQuitEvent.class, new PlayerQuitListener());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        EventOptimizer.recordEventCall(PlayerQuitEvent.class);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        long sessionTime = 0;

        if (data != null) {
            sessionTime = (System.currentTimeMillis() - data.getLastLoginTime()) / 1000;
            data.addPlayTime(sessionTime);
        }

        plugin.getPlayerDataManager().removePlayerData(player.getUniqueId());
        plugin.getLogger().info(player.getName() + " forlod efter at spille i " + sessionTime + " sekunder");
    }
}