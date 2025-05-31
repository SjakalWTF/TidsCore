package net.tidsrejsen.util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class EventOptimizer {
    private static final Map<Class<? extends Event>, Map<Listener, Integer>> eventUsage = new HashMap<>();

    public static void monitorEvent(Class<? extends Event> eventClass, Listener listener) {
        eventUsage.computeIfAbsent(eventClass, k -> new HashMap<>())
                .put(listener, 0);
    }

    public static void recordEventCall(Class<? extends Event> eventClass) {
        Map<Listener, Integer> listeners = eventUsage.get(eventClass);
        if (listeners != null) {
            listeners.replaceAll((listener, count) -> count + 1);
        }
    }

    public static void optimizeEvents() {
        for (Map.Entry<Class<? extends Event>, Map<Listener, Integer>> entry : eventUsage.entrySet()) {
            Class<? extends Event> eventClass = entry.getKey();
            for (Map.Entry<Listener, Integer> listenerEntry : entry.getValue().entrySet()) {
                if (listenerEntry.getValue() < 5) { // Hvis mindre end 5 kald i timen
                    HandlerList.unregisterAll(listenerEntry.getKey());
                    System.out.println("[Optimering] Afmeldte ubrugt listener for " + eventClass.getSimpleName());
                }
            }
        }
    }
}