package net.tidsrejsen.command;

import net.tidsrejsen.Main;
import net.tidsrejsen.player.PlayerData;
import net.tidsrejsen.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand extends BaseCommand {
    public PlayTimeCommand() {
        super("playtime", "tids.command.playtime", "pt", "spilletid");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cKun spillere kan bruge denne kommando!");
            return;
        }

        Player player = (Player) sender;
        PlayerData data = Main.getInstance().getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (data == null) {
            player.sendMessage("§cDine data indlæses...");
            return;
        }

        String playTime = TimeUtils.formatIntoDetailedString((int) (data.getTotalPlayTime()));
        player.sendMessage("§aDu har spillet §e" + playTime + "§a på serveren!");
    }
}