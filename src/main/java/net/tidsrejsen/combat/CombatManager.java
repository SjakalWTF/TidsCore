package net.tidsrejsen.combat;

import net.tidsrejsen.Main;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {
    private final Map<UUID, Integer> combat = new ConcurrentHashMap<>();
    private final Set<UUID> admin = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private volatile boolean pvpEnabled = true;

    public CombatManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                Main.getInstance(),
                this::runAsyncCountdown,
                20L, 20L
        );
    }

    public void setPvP(boolean val) {
        pvpEnabled = val;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void tag(Player a, Player v) {
        if (!pvpEnabled) {
            a.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8&l» &7PvP er ikke aktiveret."));
            return;
        }
        if (admin.contains(a.getUniqueId()) || admin.contains(v.getUniqueId())) return;

        boolean aWasTagged = combat.containsKey(a.getUniqueId());
        boolean vWasTagged = combat.containsKey(v.getUniqueId());

        combat.put(a.getUniqueId(), 30);
        combat.put(v.getUniqueId(), 30);

        if (!aWasTagged) runSync(() -> sendCombatStart(a));
        if (!vWasTagged) runSync(() -> sendCombatStart(v));
    }

    public int getCombatTime(UUID u) {
        return combat.getOrDefault(u, 0);
    }

    public boolean toggleAdmin(UUID u) {
        if (admin.contains(u)) {
            admin.remove(u);
            return false;
        }
        admin.add(u);
        return true;
    }

    public void clearCombatDataOnShutdown() {
        combat.clear();
        admin.clear();
    }

    private void runAsyncCountdown() {
        for (UUID u : new ArrayList<>(combat.keySet())) {
            if (admin.contains(u)) continue;

            int left = combat.get(u) - 1;
            if (left <= 0) {
                combat.remove(u);
                runSync(() -> notifyCombatEnd(u));
            } else {
                combat.put(u, left);
                runSync(() -> updateActionBar(u, left));
            }
        }
    }

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Main.getInstance(), task);
    }

    private void sendCombatStart(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du er nu i kamp. Log ikke ud!"));
    }

    private void updateActionBar(UUID u, int left) {
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    "&4&lCOMBAT &8» &7Du er i kamp i &c" + left + "s");
            sendActionBar(p, message);
        }
    }

    private void notifyCombatEnd(UUID u) {
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    "&a&lCOMBAT &8» &7Du er ikke længere i kamp.");
            sendActionBar(p, message);
            p.sendMessage(message);
        }
    }

    private void sendActionBar(Player player, String message) {
        IChatBaseComponent chat = new ChatComponentText(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2); // 2 = actionbar
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
