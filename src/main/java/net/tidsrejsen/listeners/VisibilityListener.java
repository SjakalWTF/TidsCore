package net.tidsrejsen.listeners;

import net.tidsrejsen.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class VisibilityListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Main.getInstance().getVisibilityHandler().update(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        String token = event.getLastToken();
        var completions = event.getTabCompletions();
        completions.clear();
        for (Player target : Main.getInstance().getServer().getOnlinePlayers()) {
            if (Main.getInstance().getVisibilityHandler().treatAsOnline(target, event.getPlayer())
                    && StringUtils.startsWithIgnoreCase(target.getName(), token)) {
                completions.add(target.getName());
            }
        }
    }
}