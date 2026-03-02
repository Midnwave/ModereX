package com.blockforge.moderex.rules;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks player acceptance of server rules.
 * Supports chat-based acceptance (Java) and book acceptance (Bedrock).
 */
public class RuleAcceptanceManager {

    private final ModereX plugin;
    private final Set<UUID> pendingAcceptance = ConcurrentHashMap.newKeySet();
    private int currentRulesVersion = 1;

    public RuleAcceptanceManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        createTable();
        calculateRulesVersion();
    }

    private void createTable() {
        try {
            String sql;
            if (plugin.getDatabaseManager().isMySQL()) {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_rule_acceptances (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        rules_version INT NOT NULL,
                        accepted_at BIGINT NOT NULL,
                        method VARCHAR(16) NOT NULL,
                        INDEX idx_uuid (player_uuid),
                        INDEX idx_version (rules_version)
                    )
                """;
            } else {
                sql = """
                    CREATE TABLE IF NOT EXISTS moderex_rule_acceptances (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        player_name VARCHAR(16) NOT NULL,
                        rules_version INTEGER NOT NULL,
                        accepted_at BIGINT NOT NULL,
                        method VARCHAR(16) NOT NULL
                    )
                """;
            }
            plugin.getDatabaseManager().update(sql);

            // Create indexes for SQLite
            if (!plugin.getDatabaseManager().isMySQL()) {
                plugin.getDatabaseManager().update(
                        "CREATE INDEX IF NOT EXISTS idx_ra_uuid ON moderex_rule_acceptances(player_uuid)");
                plugin.getDatabaseManager().update(
                        "CREATE INDEX IF NOT EXISTS idx_ra_version ON moderex_rule_acceptances(rules_version)");
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create rule_acceptances table", e);
        }
    }

    /**
     * Calculate a version hash based on current rules.
     */
    public void calculateRulesVersion() {
        List<Rule> rules = plugin.getRulesManager().getEnabledRules();
        int hash = 0;
        for (Rule rule : rules) {
            hash = 31 * hash + (rule.getTitle() != null ? rule.getTitle().hashCode() : 0);
            hash = 31 * hash + (rule.getDescription() != null ? rule.getDescription().hashCode() : 0);
        }
        currentRulesVersion = Math.abs(hash) % 100000;
    }

    /**
     * Check if a player has accepted the current rules version.
     */
    public CompletableFuture<Boolean> hasAccepted(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                    SELECT COUNT(*) as cnt FROM moderex_rule_acceptances
                    WHERE player_uuid = ? AND rules_version = ?
                """, rs -> {
                    if (rs.next()) {
                        return rs.getInt("cnt") > 0;
                    }
                    return false;
                }, uuid.toString(), currentRulesVersion);
            } catch (SQLException e) {
                plugin.logError("Failed to check rule acceptance", e);
                return true; // Default to accepted on error
            }
        });
    }

    /**
     * Record that a player has accepted the rules.
     */
    public CompletableFuture<Void> recordAcceptance(UUID uuid, String playerName, String method) {
        return CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_rule_acceptances (player_uuid, player_name, rules_version, accepted_at, method)
                    VALUES (?, ?, ?, ?, ?)
                """, uuid.toString(), playerName, currentRulesVersion, System.currentTimeMillis(), method);
                pendingAcceptance.remove(uuid);
            } catch (SQLException e) {
                plugin.logError("Failed to record rule acceptance", e);
            }
        });
    }

    /**
     * Show rules in chat with Accept/Decline buttons (Java Edition fallback).
     */
    public void showRulesInChat(Player player) {
        List<Rule> rules = plugin.getRulesManager().getEnabledRules();
        pendingAcceptance.add(player.getUniqueId());

        Msg.send(player, TextUtil.parse("<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"));
        Msg.send(player, TextUtil.parse("<bold><gradient:#3b82f6:#a855f7>Server Rules</gradient></bold>"));
        Msg.send(player, TextUtil.parse("<gray>Please read and accept the following rules:"));
        Msg.send(player, Component.empty());

        int num = 1;
        for (Rule rule : rules) {
            Component ruleComponent = Component.text(num + ". ", NamedTextColor.GOLD)
                    .append(Component.text(rule.getTitle(), NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text(" - " + rule.getDescription(), NamedTextColor.GRAY));
            Msg.send(player, ruleComponent);
            num++;
        }

        Msg.send(player, Component.empty());

        Component acceptButton = Component.text("[Accept]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept the rules", NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.runCommand("/rules accept"));

        Component declineButton = Component.text("[Decline]", NamedTextColor.RED, TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("Click to decline the rules", NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.runCommand("/rules decline"));

        Msg.send(player, acceptButton.append(Component.text("  ")).append(declineButton));
        Msg.send(player, TextUtil.parse("<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"));
    }

    /**
     * Give rules as a book (Bedrock Edition fallback).
     */
    public void giveRulesBook(Player player) {
        List<Rule> rules = plugin.getRulesManager().getEnabledRules();
        pendingAcceptance.add(player.getUniqueId());

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("Server Rules");
        meta.setAuthor("Server");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);

        // Build pages
        List<Component> pages = new ArrayList<>();
        Component currentPage = Component.text("Server Rules\n\n", NamedTextColor.DARK_BLUE)
                .decoration(TextDecoration.BOLD, true);

        int lineCount = 2;
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            Component ruleLine = Component.text((i + 1) + ". " + rule.getTitle() + "\n", NamedTextColor.BLACK)
                    .decoration(TextDecoration.BOLD, true)
                    .append(Component.text(rule.getDescription() + "\n\n", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.BOLD, false));

            lineCount += 3;
            if (lineCount > 12) {
                pages.add(currentPage);
                currentPage = Component.empty();
                lineCount = 3;
            }
            currentPage = currentPage.append(ruleLine);
        }

        // Add final page with acceptance note
        currentPage = currentPage.append(Component.text("\nClose this book to acknowledge you have read the rules.",
                NamedTextColor.DARK_GREEN));
        pages.add(currentPage);

        meta.pages(pages);
        book.setItemMeta(meta);

        // Give to player
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(book);
        } else {
            player.getWorld().dropItem(player.getLocation(), book);
        }

        Msg.send(player, TextUtil.parse("<green>A rules book has been added to your inventory. Please read it."));
    }

    /**
     * Handle /rules accept command.
     */
    public void handleAccept(Player player) {
        if (!pendingAcceptance.contains(player.getUniqueId())) {
            Msg.send(player, TextUtil.parse("<yellow>You have already accepted the rules."));
            return;
        }

        recordAcceptance(player.getUniqueId(), player.getName(), "chat");
        Msg.send(player, TextUtil.parse("<green>You have accepted the server rules. Welcome!"));
    }

    /**
     * Handle /rules decline command.
     */
    public void handleDecline(Player player) {
        pendingAcceptance.remove(player.getUniqueId());
        Msg.send(player, TextUtil.parse("<red>You have declined the server rules."));
    }

    public boolean isPendingAcceptance(UUID uuid) {
        return pendingAcceptance.contains(uuid);
    }

    public int getCurrentRulesVersion() {
        return currentRulesVersion;
    }

    /**
     * Get acceptance statistics for the web panel.
     */
    public CompletableFuture<List<Map<String, Object>>> getAcceptances(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_rule_acceptances
                    ORDER BY accepted_at DESC LIMIT ?
                """, rs -> {
                    List<Map<String, Object>> results = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("playerUuid", rs.getString("player_uuid"));
                        row.put("playerName", rs.getString("player_name"));
                        row.put("rulesVersion", rs.getInt("rules_version"));
                        row.put("acceptedAt", rs.getLong("accepted_at"));
                        row.put("method", rs.getString("method"));
                        results.add(row);
                    }
                    return results;
                }, limit);
            } catch (SQLException e) {
                plugin.logError("Failed to get rule acceptances", e);
                return List.of();
            }
        });
    }
}
