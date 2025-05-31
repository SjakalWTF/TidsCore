package net.tidsrejsen.command;

import net.tidsrejsen.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    private final String name;
    private final String permission;
    private final String[] aliases;

    public BaseCommand(String name) {
        this(name, null);
    }

    public BaseCommand(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() { return name; }
    public String getPermission() { return permission; }
    public String[] getAliases() { return aliases; }

    public boolean hasPermission(CommandSender sender) {
        if (permission == null) return true;
        if (sender instanceof Player) {
            return Main.getInstance().hasPermission((Player) sender, permission);
        }
        return sender.hasPermission(permission);
    }
}