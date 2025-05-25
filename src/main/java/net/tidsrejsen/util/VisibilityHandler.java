package net.tidsrejsen.util;

import net.tidsrejsen.Main;
import net.tidsrejsen.listeners.VisibilityListener;
import net.tidsrejsen.util.action.OverrideAction;
import net.tidsrejsen.util.action.VisibilityAction;
import net.tidsrejsen.util.provider.OverrideProvider;
import net.tidsrejsen.util.provider.VisibilityProvider;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class VisibilityHandler {
    private final Map<String, VisibilityProvider> handlers = new LinkedHashMap<>();
    private final Map<String, OverrideProvider> overrideHandlers = new LinkedHashMap<>();

    public VisibilityHandler() {
        Main.getInstance().getServer().getPluginManager()
                .registerEvents(new VisibilityListener(), Main.getInstance());
    }

    public void registerHandler(String id, VisibilityProvider handler) {
        handlers.put(id, handler);
    }

    public void registerOverride(String id, OverrideProvider handler) {
        overrideHandlers.put(id, handler);
    }

    public void update(Player player) {
        updateAllTo(player);
        updateToAll(player);
    }

    public void updateAllTo(Player viewer) {
        for (Player target : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) viewer.hidePlayer(target);
            else viewer.showPlayer(target);
        }
    }

    public void updateToAll(Player target) {
        for (Player viewer : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!shouldSee(target, viewer)) viewer.hidePlayer(target);
            else viewer.showPlayer(target);
        }
    }

    public boolean treatAsOnline(Player target, Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible");
    }

    private boolean shouldSee(Player target, Player viewer) {
        Iterator<VisibilityProvider> visIter = handlers.values().iterator();
        if (visIter.hasNext()) {
            VisibilityProvider vp = visIter.next();
            Iterator<OverrideProvider> overIter = overrideHandlers.values().iterator();
            if (overIter.hasNext()) {
                OverrideProvider op = overIter.next();
                return op.getAction(target, viewer) == OverrideAction.SHOW;
            }
            return vp.getAction(target, viewer) == VisibilityAction.NEUTRAL;
        }
        return true;
    }
}
