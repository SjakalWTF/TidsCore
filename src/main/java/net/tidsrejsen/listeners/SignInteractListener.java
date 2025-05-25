// File: src/main/java/net/tidsrejsen/listeners/SignInteractListener.java
package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignInteractListener implements Listener {

    private static final String SIGN_IDENTIFIER = ChatColor.stripColor("Kom i gang!");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material type = block.getType();
        if (type != Material.SIGN_POST && type != Material.WALL_SIGN) return;

        Sign sign = (Sign) block.getState();

        String line = ChatColor.stripColor(sign.getLine(0)).trim();

        if (!line.equalsIgnoreCase(SIGN_IDENTIFIER)) return;

        Player player = event.getPlayer();

        // Vis alle spillere for spilleren
        for (Player online : player.getServer().getOnlinePlayers()) {
            if (!online.equals(player)) {
                player.showPlayer(online);
            }
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[&6Tids&eRejsen&7] &aDu kan nu se andre spillere."));
    }
}
