package net.tidsrejsen.util;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBarUtil {
    public static void sendActionBar(Player player, String message) {
        String colored = ChatColor.translateAlternateColorCodes('&', message);
        IChatBaseComponent component = new ChatComponentText(colored);
        PacketPlayOutChat packet = new PacketPlayOutChat(component, (byte) 2); // (byte) 2 = actionbar
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
