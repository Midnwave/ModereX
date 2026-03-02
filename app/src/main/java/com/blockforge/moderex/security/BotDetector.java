package com.blockforge.moderex.security;

import com.blockforge.moderex.ModereX;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Detects bot-like joining patterns to identify raid attacks.
 * Tracks join velocity, name patterns, and connection characteristics.
 */
public class BotDetector {

    private final ModereX plugin;

    // Join velocity tracking
    private final List<Long> recentJoinTimestamps = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<Long>> perIpJoinTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Integer> subnetJoinCounts = new ConcurrentHashMap<>();

    // Suspicious name patterns
    private static final List<String> BOT_NAME_PATTERNS = List.of(
            "^[A-Z][a-z]+\\d{4,}$",      // Name + 4+ digits: Player12345
            "^[a-z]{3,5}\\d{5,}$",        // Short name + 5+ digits: abc12345
            "^\\w{1,3}_\\w{1,3}_\\d+$",   // Pattern: a_b_123
            "^Bot_?\\d+$",                 // Bot123 or Bot_123
            "^[A-Z]{2,4}\\d{6,}$"         // Initials + 6+ digits: AB123456
    );

    public BotDetector(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Analyze a join event and return a suspicion score (0-100).
     */
    public int analyzeJoin(String playerName, String ip) {
        int score = 0;

        // Check name patterns
        score += analyzeNamePatterns(playerName);

        // Check join velocity
        score += analyzeJoinVelocity(ip);

        // Record this join
        long now = System.currentTimeMillis();
        recentJoinTimestamps.add(now);
        perIpJoinTimestamps.computeIfAbsent(ip, k -> Collections.synchronizedList(new ArrayList<>())).add(now);

        // Track /24 subnet
        String subnet = getSubnet(ip);
        subnetJoinCounts.merge(subnet, 1, Integer::sum);

        // Clean old data periodically
        if (recentJoinTimestamps.size() > 1000) {
            cleanOldData();
        }

        return Math.min(100, score);
    }

    private int analyzeNamePatterns(String name) {
        int score = 0;

        // Check against known bot patterns
        for (String pattern : BOT_NAME_PATTERNS) {
            if (name.matches(pattern)) {
                score += 30;
                break;
            }
        }

        // Very short names
        if (name.length() <= 3) {
            score += 10;
        }

        // All numbers in name (e.g., player name is mostly digits)
        long digitCount = name.chars().filter(Character::isDigit).count();
        if (digitCount > name.length() * 0.6) {
            score += 15;
        }

        // Sequential characters (abc, 123)
        if (hasSequentialChars(name)) {
            score += 10;
        }

        return score;
    }

    private int analyzeJoinVelocity(String ip) {
        int score = 0;
        long now = System.currentTimeMillis();
        long windowMs = 60000; // 1 minute window

        // Global join velocity
        long recentCount = recentJoinTimestamps.stream()
                .filter(t -> now - t < windowMs)
                .count();
        if (recentCount > 20) score += 20;  // 20+ joins/min is suspicious
        if (recentCount > 50) score += 30;  // 50+ joins/min is very suspicious

        // Per-IP velocity
        List<Long> ipJoins = perIpJoinTimestamps.get(ip);
        if (ipJoins != null) {
            long ipRecentCount = ipJoins.stream()
                    .filter(t -> now - t < windowMs)
                    .count();
            if (ipRecentCount > 3) score += 25;  // 3+ joins from same IP in 1 min
            if (ipRecentCount > 5) score += 25;  // 5+ is almost certainly a bot
        }

        // Subnet velocity
        String subnet = getSubnet(ip);
        int subnetCount = subnetJoinCounts.getOrDefault(subnet, 0);
        if (subnetCount > 10) score += 15;

        return score;
    }

    private boolean hasSequentialChars(String name) {
        for (int i = 0; i < name.length() - 2; i++) {
            char a = name.charAt(i);
            char b = name.charAt(i + 1);
            char c = name.charAt(i + 2);
            if (b == a + 1 && c == b + 1) return true;
        }
        return false;
    }

    /**
     * Get the /24 subnet (first 3 octets) of an IPv4 address.
     */
    private String getSubnet(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length >= 3) {
            return parts[0] + "." + parts[1] + "." + parts[2];
        }
        return ip;
    }

    /**
     * Get global join velocity (joins per minute).
     */
    public int getJoinVelocity() {
        long now = System.currentTimeMillis();
        return (int) recentJoinTimestamps.stream()
                .filter(t -> now - t < 60000)
                .count();
    }

    private void cleanOldData() {
        long cutoff = System.currentTimeMillis() - 300000; // 5 minutes
        recentJoinTimestamps.removeIf(t -> t < cutoff);
        perIpJoinTimestamps.values().forEach(list -> list.removeIf(t -> t < cutoff));
        perIpJoinTimestamps.entrySet().removeIf(e -> e.getValue().isEmpty());

        // Reset subnet counts periodically
        if (subnetJoinCounts.size() > 500) {
            subnetJoinCounts.clear();
        }
    }

    /**
     * Reset all detection state (e.g., after raid ends).
     */
    public void reset() {
        recentJoinTimestamps.clear();
        perIpJoinTimestamps.clear();
        subnetJoinCounts.clear();
    }
}
