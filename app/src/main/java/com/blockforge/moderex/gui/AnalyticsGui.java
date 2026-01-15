package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AnalyticsGui extends BaseGui {

    private Map<PunishmentType, Integer> punishmentCounts = new HashMap<>();
    private int totalPunishments = 0;
    private int activePunishments = 0;
    private List<Punishment> recentPunishments = List.of();
    private boolean loaded = false;

    public AnalyticsGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Server Analytics</gradient>", 6);
        loadData();
    }

    private void loadData() {
        // Load punishment statistics asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                // Get recent punishments
                recentPunishments = plugin.getPunishmentManager().getRecentPunishments(10).join();

                // Count by type
                for (PunishmentType type : PunishmentType.values()) {
                    int count = countPunishmentsByType(type);
                    punishmentCounts.put(type, count);
                    totalPunishments += count;
                }

                // Count active punishments
                activePunishments = countActivePunishments();

                loaded = true;

                // Refresh GUI on main thread
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            } catch (Exception e) {
                plugin.logError("Failed to load analytics data", e);
            }
        });
    }

    private int countPunishmentsByType(PunishmentType type) {
        try {
            Integer count = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_punishments WHERE type = ?",
                    rs -> rs.next() ? rs.getInt("cnt") : 0,
                    type.name()
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int countActivePunishments() {
        try {
            Integer count = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as cnt FROM moderex_punishments WHERE active = TRUE",
                    rs -> rs.next() ? rs.getInt("cnt") : 0
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void populate() {
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        if (!loaded) {
            setItem(22, createItem(Material.CLOCK, "<yellow>Loading...",
                    "<gray>Fetching analytics data..."));
            return;
        }

        // Header
        setItem(4, createItem(Material.COMPARATOR, "<gold><bold>Moderation Analytics</bold>",
                "<gray>Server-wide punishment statistics",
                "",
                "<white>" + totalPunishments + " <gray>total punishments",
                "<green>" + activePunishments + " <gray>currently active"));

        // Punishment type breakdown
        setItem(19, createStatItem(PunishmentType.WARN, Material.BOOK));
        setItem(20, createStatItem(PunishmentType.MUTE, Material.PAPER));
        setItem(21, createStatItem(PunishmentType.KICK, Material.LEATHER_BOOTS));
        setItem(22, createStatItem(PunishmentType.BAN, Material.BARRIER));
        setItem(23, createStatItem(PunishmentType.IPBAN, Material.CHAIN));

        // Recent activity section header
        setItem(31, createItem(Material.CLOCK, "<aqua><bold>Recent Activity</bold>",
                "<gray>Last 10 punishments issued"));

        // Recent punishments
        int slot = 37;
        for (Punishment punishment : recentPunishments) {
            if (slot > 43) break;
            setItem(slot, createPunishmentItem(punishment));
            slot++;
        }

        if (recentPunishments.isEmpty()) {
            setItem(40, createItem(Material.STRUCTURE_VOID, "<gray>No recent punishments",
                    "<dark_gray>Nothing to show"));
        }

        // Server stats
        setItem(10, createItem(Material.PLAYER_HEAD, "<green>Online Players",
                "<white>" + plugin.getServer().getOnlinePlayers().size() + " <gray>/ <white>" +
                        plugin.getServer().getMaxPlayers()));

        setItem(11, createItem(Material.SPYGLASS, "<yellow>Watchlist",
                "<white>" + plugin.getWatchlistManager().getWatchedPlayers().size() + " <gray>players watched"));

        setItem(12, createItem(Material.COMPARATOR, "<gold>Automod Rules",
                "<white>" + plugin.getAutomodManager().getRules().size() + " <gray>active rules"));

        // Refresh button
        setItem(49, createItem(Material.SUNFLOWER, "<yellow>Refresh",
                "<gray>Reload analytics data",
                "",
                "<yellow>Click to refresh"), () -> {
            loaded = false;
            refresh();
            loadData();
        });

        // Back button
        setItem(45, createBackButton(), () -> openGui(new MainMenuGui(plugin)));

        // Close button
        setItem(53, createCloseButton(), this::close);
    }

    private org.bukkit.inventory.ItemStack createStatItem(PunishmentType type, Material icon) {
        int count = punishmentCounts.getOrDefault(type, 0);
        double percentage = totalPunishments > 0 ? (count * 100.0 / totalPunishments) : 0;

        return createItem(icon,
                getTypeColor(type) + type.getDisplayName() + "s",
                "<white>" + count + " <gray>total",
                "<dark_gray>" + String.format("%.1f%%", percentage) + " of all punishments"
        );
    }

    private org.bukkit.inventory.ItemStack createPunishmentItem(Punishment punishment) {
        String timeAgo = formatTimeAgo(punishment.getCreatedAt());

        return createItem(
                getMaterialForType(punishment.getType()),
                getTypeColor(punishment.getType()) + punishment.getPlayerName(),
                "<gray>Type: " + getTypeColor(punishment.getType()) + punishment.getType().getDisplayName(),
                "<gray>Staff: <white>" + punishment.getStaffName(),
                "<gray>Reason: <white>" + truncate(punishment.getReason(), 30),
                "<gray>When: <white>" + timeAgo
        );
    }

    private Material getMaterialForType(PunishmentType type) {
        return switch (type) {
            case WARN -> Material.BOOK;
            case MUTE -> Material.PAPER;
            case KICK -> Material.LEATHER_BOOTS;
            case BAN -> Material.BARRIER;
            case IPBAN -> Material.CHAIN;
        };
    }

    private String getTypeColor(PunishmentType type) {
        return switch (type) {
            case WARN -> "<yellow>";
            case MUTE -> "<gold>";
            case KICK -> "<aqua>";
            case BAN -> "<red>";
            case IPBAN -> "<dark_red>";
        };
    }

    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return seconds + "s ago";
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
