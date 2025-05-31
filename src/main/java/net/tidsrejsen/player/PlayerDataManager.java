package net.tidsrejsen.player;

import net.tidsrejsen.Main;
import net.tidsrejsen.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final Main plugin;
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadPlayerData(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData data = new PlayerData(uuid);
            data.load();
            data.updateLoginTime(); // Opdater login-tid
            cache.put(uuid, data);
        });
    }

    public void savePlayerData(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData data = cache.get(uuid);
            if (data != null) {
                data.save();
            }
        });
    }

    public PlayerData getPlayerData(UUID uuid) {
        return cache.get(uuid);
    }

    public void removePlayerData(UUID uuid) {
        savePlayerData(uuid);
        cache.remove(uuid);
    }

    public void saveAll() {
        cache.forEach((uuid, data) -> data.save());
        cache.clear();
    }
}