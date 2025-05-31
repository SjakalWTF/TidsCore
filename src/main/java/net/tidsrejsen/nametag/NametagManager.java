package net.tidsrejsen.nametag;

import net.tidsrejsen.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NametagManager {
    private static Scoreboard scoreboard;

    static {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public static void updateNametag(Player player) {
        PermissionUser user = PermissionsEx.getUser(player);
        String prefix = user.getPrefix() != null ? ChatColor.translateAlternateColorCodes('&', user.getPrefix()) : "";
        String group = user.getGroups()[0].getName();

        Team team = scoreboard.getTeam(group) != null ?
                scoreboard.getTeam(group) :
                scoreboard.registerNewTeam(group);

        team.setPrefix(prefix);
        team.addEntry(player.getName());
    }

    public static void updateAllNametags() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateNametag(player);
        }
    }
}