package net.tidsrejsen.pvp;

import net.tidsrejsen.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PvPCommand implements CommandExecutor {
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a){
        if(!(s instanceof Player)){ s.sendMessage("Kun spillere."); return true; }
        Player p=(Player)s;
        if(!PermissionsEx.getUser(p).has("pvp.admin")){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lSERVER &8&l» &7Du har ikke adgang til det!"));
            return true;
        }
        if(a.length<1){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lSERVER &8&l» &7Brug &8/&7pvp <on/off>"));
            return true;
        }
        boolean on=a[0].equalsIgnoreCase("on");
        Main.getInstance().getCombatManager().setPvP(on);
        String msg = on
                ? "&4&lSERVER &8&l» &7Du &aaktiverede &7PvP."
                : "&4&lSERVER &8&l» &7Du &cdeaktiverede &7PvP!";
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
        return true;
    }
}
