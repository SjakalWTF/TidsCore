package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import net.tidsrejsen.nametag.NametagManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayerData(player.getUniqueId());

        // Opdater nametag hvis PermissionEx er tilgængelig
        if (plugin.getPermissionsEx() != null) {
            NametagManager.updateNametag(player);
        }

        plugin.getLogger().info(player.getName() + " joined the server");
    }
}