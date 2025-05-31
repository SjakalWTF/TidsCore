package net.tidsrejsen.command;

import net.tidsrejsen.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final Map<String, BaseCommand> commands = new HashMap<>();

    public CommandHandler(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(BaseCommand command) {
        commands.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false;

        BaseCommand command = commands.get(args[0].toLowerCase());
        if (command == null) return false;

        if (!command.hasPermission(sender)) {
            sender.sendMessage("§cIngen adgang!");
            return true;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        command.execute(sender, newArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String commandName : commands.keySet()) {
                if (commandName.startsWith(args[0].toLowerCase())) {
                    completions.add(commandName);
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}