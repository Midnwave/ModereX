package com.blockforge.moderex.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public final class TimeUtil {

    private static ZoneId zoneId = ZoneId.of("America/Chicago"); // Default CST
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private static DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    private TimeUtil() {
    }

    public static void setTimezone(String timezone) {
        try {
            zoneId = ZoneId.of(timezone);
        } catch (Exception e) {
            // Try as TimeZone ID
            TimeZone tz = TimeZone.getTimeZone(timezone);
            zoneId = tz.toZoneId();
        }
    }

    public static String getTimezone() {
        return zoneId.getId();
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static String formatDate(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), zoneId
        );
        return dateTime.format(dateFormatter);
    }

    public static String formatTime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), zoneId
        );
        return dateTime.format(timeFormatter);
    }

    public static String formatDateTime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), zoneId
        );
        return dateTime.format(dateTimeFormatter);
    }

    public static String formatFull(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), zoneId
        );
        return dateTime.format(fullFormatter);
    }

    public static String format(long timestamp, String pattern) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), zoneId
        );
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatRelative(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        boolean past = diff >= 0;
        diff = Math.abs(diff);

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        String result;
        if (years > 0) {
            result = years + (years == 1 ? " year" : " years");
        } else if (months > 0) {
            result = months + (months == 1 ? " month" : " months");
        } else if (weeks > 0) {
            result = weeks + (weeks == 1 ? " week" : " weeks");
        } else if (days > 0) {
            result = days + (days == 1 ? " day" : " days");
        } else if (hours > 0) {
            result = hours + (hours == 1 ? " hour" : " hours");
        } else if (minutes > 0) {
            result = minutes + (minutes == 1 ? " minute" : " minutes");
        } else if (seconds > 0) {
            result = seconds + (seconds == 1 ? " second" : " seconds");
        } else {
            return "just now";
        }

        return past ? result + " ago" : "in " + result;
    }

    public static long parseDate(String dateStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(
                    dateStr + " 00:00:00",
                    dateTimeFormatter
            );
            return dateTime.atZone(zoneId).toInstant().toEpochMilli();
        } catch (Exception e) {
            return -1;
        }
    }

    public static long parseDateTime(String dateTimeStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
            return dateTime.atZone(zoneId).toInstant().toEpochMilli();
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getStartOfToday() {
        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        return startOfDay.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long getEndOfToday() {
        return getStartOfToday() + (24 * 60 * 60 * 1000) - 1;
    }

    public static boolean isToday(long timestamp) {
        return timestamp >= getStartOfToday() && timestamp <= getEndOfToday();
    }
}
