package net.tidsrejsen.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent titleComp = new ChatComponentText(title);
        IChatBaseComponent subComp = new ChatComponentText(subtitle);

        // Først times pakken (fade in/out osv)
        PacketPlayOutTitle times = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, titleComp, fadeIn, stay, fadeOut);

        // Title pakke med hovedtitel
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComp);

        // Subtitle pakke med undertitel
        PacketPlayOutTitle subPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subComp);

        // Send pakkerne til spilleren
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(times);
        craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
        craftPlayer.getHandle().playerConnection.sendPacket(subPacket);
    }
}
