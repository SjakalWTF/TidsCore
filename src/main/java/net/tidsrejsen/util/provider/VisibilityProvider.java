// File: net/tidsrejsen/util/provider/VisibilityProvider.java
package net.tidsrejsen.util.provider;

import org.bukkit.entity.Player;
import net.tidsrejsen.util.action.VisibilityAction;

/**
 * Determines default visibility between two players.
 */
public interface VisibilityProvider {
    /**
     * @return VisibilityAction.NEUTRAL to show, VisibilityAction.HIDE to hide.
     */
    VisibilityAction getAction(Player target, Player viewer);
}
