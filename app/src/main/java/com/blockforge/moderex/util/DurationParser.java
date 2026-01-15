package com.blockforge.moderex.util;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(\\d+)(mo|w|d|h|m|s)",
            Pattern.CASE_INSENSITIVE
    );

    private static final long SECONDS_PER_MINUTE = 60;
    private static final long SECONDS_PER_HOUR = 3600;
    private static final long SECONDS_PER_DAY = 86400;
    private static final long SECONDS_PER_WEEK = 604800;
    private static final long SECONDS_PER_MONTH = 2592000; // 30 days

    private DurationParser() {
    }

    public static long parse(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        String trimmed = input.trim().toLowerCase();

        // Check for permanent duration
        if (trimmed.equals("permanent") || trimmed.equals("perm") || trimmed.equals("forever") || trimmed.equals("-1")) {
            return -1;
        }

        // Try to parse as a simple number (assumes seconds)
        try {
            long seconds = Long.parseLong(trimmed);
            return seconds * 1000;
        } catch (NumberFormatException ignored) {
        }

        // Parse complex duration string
        Matcher matcher = DURATION_PATTERN.matcher(trimmed);
        long totalSeconds = 0;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            totalSeconds += switch (unit) {
                case "mo" -> value * SECONDS_PER_MONTH;
                case "w" -> value * SECONDS_PER_WEEK;
                case "d" -> value * SECONDS_PER_DAY;
                case "h" -> value * SECONDS_PER_HOUR;
                case "m" -> value * SECONDS_PER_MINUTE;
                case "s" -> value;
                default -> 0;
            };
        }

        return found ? totalSeconds * 1000 : 0;
    }

    public static long parseToSeconds(String input) {
        long millis = parse(input);
        if (millis == -1) return -1;
        return millis / 1000;
    }

    public static boolean isPermanent(String input) {
        return parse(input) == -1;
    }

    public static boolean isValidDuration(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String trimmed = input.trim().toLowerCase();

        if (trimmed.equals("permanent") || trimmed.equals("perm") || trimmed.equals("forever")) {
            return true;
        }

        try {
            Long.parseLong(trimmed);
            return true;
        } catch (NumberFormatException ignored) {
        }

        return DURATION_PATTERN.matcher(trimmed).find();
    }

    public static String format(long millis) {
        return format(millis, false);
    }

    public static String format(long millis, boolean shortFormat) {
        if (millis == -1) {
            return "Permanent";
        }

        if (millis == 0) {
            return shortFormat ? "0s" : "0 seconds";
        }

        long seconds = millis / 1000;
        StringBuilder sb = new StringBuilder();

        long months = seconds / SECONDS_PER_MONTH;
        seconds %= SECONDS_PER_MONTH;

        long weeks = seconds / SECONDS_PER_WEEK;
        seconds %= SECONDS_PER_WEEK;

        long days = seconds / SECONDS_PER_DAY;
        seconds %= SECONDS_PER_DAY;

        long hours = seconds / SECONDS_PER_HOUR;
        seconds %= SECONDS_PER_HOUR;

        long minutes = seconds / SECONDS_PER_MINUTE;
        seconds %= SECONDS_PER_MINUTE;

        if (shortFormat) {
            if (months > 0) sb.append(months).append("mo ");
            if (weeks > 0) sb.append(weeks).append("w ");
            if (days > 0) sb.append(days).append("d ");
            if (hours > 0) sb.append(hours).append("h ");
            if (minutes > 0) sb.append(minutes).append("m ");
            if (seconds > 0) sb.append(seconds).append("s");
        } else {
            if (months > 0) sb.append(months).append(months == 1 ? " month " : " months ");
            if (weeks > 0) sb.append(weeks).append(weeks == 1 ? " week " : " weeks ");
            if (days > 0) sb.append(days).append(days == 1 ? " day " : " days ");
            if (hours > 0) sb.append(hours).append(hours == 1 ? " hour " : " hours ");
            if (minutes > 0) sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
            if (seconds > 0) sb.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return sb.toString().trim();
    }

    public static String formatRemaining(long endTimestamp) {
        if (endTimestamp == -1) {
            return "Permanent";
        }

        long remaining = endTimestamp - System.currentTimeMillis();
        if (remaining <= 0) {
            return "Expired";
        }

        return format(remaining);
    }

    public static String formatRemainingShort(long endTimestamp) {
        if (endTimestamp == -1) {
            return "Permanent";
        }

        long remaining = endTimestamp - System.currentTimeMillis();
        if (remaining <= 0) {
            return "Expired";
        }

        return format(remaining, true);
    }

    public static long calculateEndTime(long durationMillis) {
        if (durationMillis == -1) {
            return -1;
        }
        return System.currentTimeMillis() + durationMillis;
    }

    public static boolean isExpired(long endTimestamp) {
        if (endTimestamp == -1) {
            return false; // Permanent never expires
        }
        return System.currentTimeMillis() >= endTimestamp;
    }
}
