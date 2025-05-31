package net.tidsrejsen.command;

import net.tidsrejsen.Main;
import net.tidsrejsen.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CombatTagCommand extends BaseCommand {

    public CombatTagCommand() {
        super("combattag", "combatlog.use", "ct", "combat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Kan ikke bruges fra console.");
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
            if (!hasAdminPermission(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7[&6Tids&eRejsen&7] &cDu har ikke tilladelse."));
                return;
            }
            handleAdminToggle(player);
            return;
        }

        displayCombatStatus(player);
    }

    private boolean hasAdminPermission(Player player) {
        return Main.getInstance().hasPermission(player, "combatlog.admin");
    }

    private void handleAdminToggle(Player player) {
        boolean adminMode = Main.getInstance().getCombatManager()
                .toggleAdmin(player.getUniqueId());

        if (adminMode) {
            Main.getInstance().getCombatManager().clearCombatData(player.getUniqueId());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lCOMBAT &8» &7Admin tilstand er nu &aAktiveret&7."));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lCOMBAT &8» &7Admin tilstand er nu &cDeaktiveret&7."));
        }
    }

    private void displayCombatStatus(Player player) {
        int combatTime = Main.getInstance().getCombatManager()
                .getCombatTime(player.getUniqueId());

        if (combatTime > 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lCOMBAT &8» &7Du er i kamp i &c" + combatTime + "s"));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lCOMBAT &8» &7Du er ikke i kamp."));
        }
    }
}