package net.tidsrejsen.util;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarUtil {
    private static final Map<UUID, BukkitRunnable> activeBars = new ConcurrentHashMap<>();

    public static void sendActionBar(Player player, String message) {
        cancelActiveBar(player); // Fjern eksisterende

        // Send ny besked
        IChatBaseComponent chat = new ChatComponentText(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        // Auto-fjern efter 3 sekunder
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                sendRawActionBar(player, ""); // Clear
                activeBars.remove(player.getUniqueId());
            }
        };
        task.runTaskLater(Main.getInstance(), 60);
        activeBars.put(player.getUniqueId(), task);
    }
}