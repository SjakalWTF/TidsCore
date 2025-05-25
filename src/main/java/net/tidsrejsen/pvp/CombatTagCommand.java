package net.tidsrejsen.pvp;

import net.tidsrejsen.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CombatTagCommand implements CommandExecutor {
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a){
        if(!(s instanceof Player)){ s.sendMessage("Kan ikke bruges fra console."); return true; }
        Player p=(Player)s;
        if(a.length>0 && a[0].equalsIgnoreCase("admin")){
            if(!PermissionsEx.getUser(p).has("combatlog.admin")){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7[&6Tids&eRejsen&7] &cDu har ikke tilladelse."));
                return true;
            }
            boolean adm = Main.getInstance()
                    .getCombatManager().toggleAdmin(p.getUniqueId());
            p.sendMessage(adm
                    ? ChatColor.GREEN+"Admin-tilstand er nu aktiveret."
                    : ChatColor.RED  +"Admin-tilstand er nu deaktiveret.");
            return true;
        }
        int t = Main.getInstance().getCombatManager()
                .getCombatTime(p.getUniqueId());
        if(t>0) p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lCOMBAT &8» &7Du er i kamp i &c"+t+"s"));
        else    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&a&lCOMBAT &8» &7Du er ikke i kamp."));
        return true;
    }
}
