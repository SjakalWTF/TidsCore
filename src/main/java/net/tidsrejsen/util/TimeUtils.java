package net.tidsrejsen.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatIntoMMSS(int secs) {
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs == 0) return "0 sekunder";

        long days = TimeUnit.SECONDS.toDays(secs);
        long hours = TimeUnit.SECONDS.toHours(secs) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(secs) % 60;
        long seconds = secs % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" dage, ");
        if (hours > 0) sb.append(hours).append(" timer, ");
        if (minutes > 0) sb.append(minutes).append(" minutter, ");
        if (seconds > 0) sb.append(seconds).append(" sekunder");

        return sb.toString().replaceAll(", $", "");
    }
}