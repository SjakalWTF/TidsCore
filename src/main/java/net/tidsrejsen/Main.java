package net.tidsrejsen;

import lombok.Getter;
import net.tidsrejsen.combat.CombatListener;
import net.tidsrejsen.combat.CombatManager;
import net.tidsrejsen.command.CommandHandler;
import net.tidsrejsen.listeners.*;
import net.tidsrejsen.command.CombatTagCommand;
import net.tidsrejsen.command.PvPCommand;
import net.tidsrejsen.player.PlayerDataManager;
import net.tidsrejsen.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import static net.tidsrejsen.Main.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private CombatManager combatManager;
    @Getter private PlayerDataManager playerDataManager;
    @Getter private CommandHandler commandHandler;
    @Getter private VisibilityHandler visibilityHandler;
    @Getter private PermissionsEx permissionsEx;

    private Set<UUID> firstJoinPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;

        // Initialize core components
        combatManager = new CombatManager();
        visibilityHandler = new VisibilityHandler();
        playerDataManager = new PlayerDataManager(this);
        commandHandler = new CommandHandler(this);

        // Check for PermissionEx
        if (Bukkit.getPluginManager().getPlugin("PermissionEx") != null) {
            permissionsEx = (PermissionsEx) Bukkit.getPluginManager().getPlugin("PermissionEx");
            getLogger().info("PermissionEx enabled");

        }

        // Register event listeners
        registerListeners();

        // Register commands
        registerCommands();

        getLogger().info("TidsCore enabled");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SignInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(combatManager), this);
        Bukkit.getPluginManager().registerEvents(new MovementListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
    }

    private void registerCommands() {
        // Register commands via new handler
        commandHandler.registerCommand(new PvPCommand());
        commandHandler.registerCommand(new CombatTagCommand());

    }

    @Override
    public void onDisable() {
        // Save all player data
        playerDataManager.saveAll();

        // Clear combat data
        combatManager.clearAllCombatData();

        getLogger().info("TidsCore disabled");
    }

    public Set<UUID> getFirstJoinPlayers() {
        return firstJoinPlayers;
    }

    public boolean hasPermission(Player player, String permission) {
        if (permissionsEx != null) {
            return permissionsEx.getUser(player).has(permission);
        }
        return player.hasPermission(permission);
    }
}