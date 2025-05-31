package net.tidsrejsen.player;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private long firstJoinTime;
    private long lastLoginTime;
    private long totalPlayTime;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public void load() {
        File file = getDataFile();
        if (!file.exists()) {
            firstJoinTime = System.currentTimeMillis();
            lastLoginTime = System.currentTimeMillis();
            totalPlayTime = 0;
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.firstJoinTime = config.getLong("firstJoin", System.currentTimeMillis());
        this.lastLoginTime = config.getLong("lastLogin", System.currentTimeMillis());
        this.totalPlayTime = config.getLong("playTime", 0);
    }

    public void save() {
        File file = getDataFile();
        YamlConfiguration config = new YamlConfiguration();

        config.set("firstJoin", firstJoinTime);
        config.set("lastLogin", lastLoginTime);
        config.set("playTime", totalPlayTime);

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getDataFile() {
        return new File("plugins/TidsCore/playerdata/" + uuid + ".yml");
    }

    // Getters & Setters
    public long getFirstJoinTime() { return firstJoinTime; }
    public long getLastLoginTime() { return lastLoginTime; }
    public long getTotalPlayTime() { return totalPlayTime; }

    public void updateLoginTime() {
        this.lastLoginTime = System.currentTimeMillis();
    }

    public void addPlayTime(long seconds) {
        this.totalPlayTime += seconds;
    }
}