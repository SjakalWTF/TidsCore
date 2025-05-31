package net.tidsrejsen.command;

import net.tidsrejsen.Main;
import net.tidsrejsen.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPCommand extends BaseCommand {

    public PvPCommand() {
        super("pvp", "pvp.admin", "pvptoggle");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Kan kun bruges af spillere.");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lSERVER &8&l» &7Brug &8/&7pvp <on/off>"));
            return;
        }

        boolean enable = args[0].equalsIgnoreCase("on");
        Main.getInstance().getCombatManager().setPvP(enable);

        String status = enable ? "&aaktiveret" : "&cdeaktiveret";
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lSERVER &8&l» &7PvP er nu " + status + "&7."));
    }
}