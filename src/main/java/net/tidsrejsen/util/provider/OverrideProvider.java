package net.tidsrejsen.util.provider;

import org.bukkit.entity.Player;
import net.tidsrejsen.util.action.OverrideAction;

public interface OverrideProvider {
    OverrideAction getAction(Player target, Player viewer);
}