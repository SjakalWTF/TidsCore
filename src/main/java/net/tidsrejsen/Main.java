package net.tidsrejsen;

import lombok.Getter;
import net.tidsrejsen.combat.CombatManager;
import net.tidsrejsen.listeners.CombatListener;
import net.tidsrejsen.listeners.JoinListener;
import net.tidsrejsen.listeners.MovementListener;
import net.tidsrejsen.listeners.SignInteractListener;
import net.tidsrejsen.pvp.CombatTagCommand;
import net.tidsrejsen.pvp.PvPCommand;
import net.tidsrejsen.util.VisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private CombatManager combatManager;

    // Felt til firstJoin spillere
    private Set<UUID> firstJoinPlayers = new HashSet<>();

    // Felt til VisibilityHandler - husk at importere og lave denne klasse
    private VisibilityHandler visibilityHandler;

    // Getter til visibilityHandler
    public VisibilityHandler getVisibilityHandler() {
        return visibilityHandler;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Initialiserer combatManager og visibilityHandler
        combatManager = new CombatManager();
        visibilityHandler = new VisibilityHandler();


        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new SignInteractListener(), this);

        Bukkit.getPluginManager().registerEvents(new CombatListener(combatManager), this);
        Bukkit.getPluginManager().registerEvents(new MovementListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        // Register commands
        getCommand("pvp").setExecutor(new PvPCommand());
        getCommand("combattag").setExecutor(new CombatTagCommand());

        getLogger().info("TidsCore enabled");
    }

    @Override
    public void onDisable() {
        combatManager.clearCombatDataOnShutdown();
        getLogger().info("TidsCore disabled");
    }

    // Getter til firstJoinPlayers, hvis nødvendigt udenfor
    public Set<UUID> getFirstJoinPlayers() {
        return firstJoinPlayers;
    }
}
