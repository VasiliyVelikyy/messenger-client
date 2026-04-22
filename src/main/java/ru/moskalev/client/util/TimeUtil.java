package ru.moskalev.client.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimeUtil {

    public static String formatTimestamp(long timestamp) {
        LocalTime time = Instant
                .ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
}
