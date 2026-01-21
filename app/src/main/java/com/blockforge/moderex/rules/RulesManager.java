package com.blockforge.moderex.rules;

import com.blockforge.moderex.ModereX;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages server rules that players must follow.
 * Rules can be viewed in-game with /rules and edited via GUI or web panel.
 */
public class RulesManager {

    private final ModereX plugin;
    private final List<Rule> rules = new CopyOnWriteArrayList<>();

    public RulesManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        createTable();
        loadRules();

        // If no rules exist, create defaults
        if (rules.isEmpty()) {
            createDefaultRules();
        }
    }

    private void createTable() {
        try {
            plugin.getDatabaseManager().update("""
                CREATE TABLE IF NOT EXISTS moderex_rules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    rule_order INTEGER NOT NULL DEFAULT 0,
                    title VARCHAR(255) NOT NULL,
                    description TEXT,
                    category VARCHAR(64),
                    enabled BOOLEAN DEFAULT TRUE,
                    created_at BIGINT,
                    updated_at BIGINT
                )
            """);
        } catch (SQLException e) {
            plugin.logError("Failed to create rules table", e);
        }
    }

    private void loadRules() {
        rules.clear();
        try {
            plugin.getDatabaseManager().query("""
                SELECT * FROM moderex_rules ORDER BY rule_order ASC
            """, rs -> {
                while (rs.next()) {
                    Rule rule = new Rule();
                    rule.setId(rs.getInt("id"));
                    rule.setOrder(rs.getInt("rule_order"));
                    rule.setTitle(rs.getString("title"));
                    rule.setDescription(rs.getString("description"));
                    rule.setCategory(rs.getString("category"));
                    rule.setEnabled(rs.getBoolean("enabled"));
                    rule.setCreatedAt(rs.getLong("created_at"));
                    rule.setUpdatedAt(rs.getLong("updated_at"));
                    rules.add(rule);
                }
                return null;
            });
            plugin.getLogger().info("Loaded " + rules.size() + " server rules.");
        } catch (SQLException e) {
            plugin.logError("Failed to load rules", e);
        }
    }

    private void createDefaultRules() {
        List<Rule> defaults = List.of(
            new Rule(1, "Be Respectful", "Treat all players with respect. No harassment, bullying, or discrimination.", "General"),
            new Rule(2, "No Cheating", "Using hacks, mods, or exploits that give unfair advantages is prohibited.", "Gameplay"),
            new Rule(3, "No Griefing", "Do not destroy or damage other players' builds without permission.", "Gameplay"),
            new Rule(4, "No Spam", "Avoid excessive messages, caps, or advertising in chat.", "Chat"),
            new Rule(5, "Listen to Staff", "Follow instructions from moderators and admins.", "General"),
            new Rule(6, "No Inappropriate Content", "Keep content family-friendly. No NSFW or offensive material.", "Chat"),
            new Rule(7, "English in Global Chat", "Use English in global chat. Other languages in private messages.", "Chat"),
            new Rule(8, "Report Issues", "Report bugs, exploits, or rule breakers to staff.", "General")
        );

        for (Rule rule : defaults) {
            saveRule(rule);
        }

        plugin.getLogger().info("Created " + defaults.size() + " default server rules.");
    }

    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }

    public List<Rule> getEnabledRules() {
        return rules.stream()
                .filter(Rule::isEnabled)
                .sorted(Comparator.comparingInt(Rule::getOrder))
                .toList();
    }

    public Rule getRule(int id) {
        return rules.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public CompletableFuture<Rule> saveRule(Rule rule) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (rule.getId() == 0) {
                    // Insert new rule - get the generated ID
                    var conn = plugin.getDatabaseManager().getDataSource().getConnection();
                    try (var stmt = conn.prepareStatement("""
                        INSERT INTO moderex_rules (rule_order, title, description, category, enabled, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setInt(1, rule.getOrder());
                        stmt.setString(2, rule.getTitle());
                        stmt.setString(3, rule.getDescription());
                        stmt.setString(4, rule.getCategory());
                        stmt.setBoolean(5, rule.isEnabled());
                        stmt.setLong(6, rule.getCreatedAt());
                        stmt.setLong(7, rule.getUpdatedAt());
                        stmt.executeUpdate();

                        var rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            rule.setId(rs.getInt(1));
                        }
                    }
                    conn.close();
                    rules.add(rule);
                } else {
                    // Update existing rule
                    plugin.getDatabaseManager().update("""
                        UPDATE moderex_rules SET rule_order = ?, title = ?, description = ?, category = ?,
                        enabled = ?, updated_at = ? WHERE id = ?
                    """, rule.getOrder(), rule.getTitle(), rule.getDescription(),
                        rule.getCategory(), rule.isEnabled(), System.currentTimeMillis(), rule.getId());

                    // Update in cache
                    rules.removeIf(r -> r.getId() == rule.getId());
                    rules.add(rule);
                }

                // Sort rules by order
                rules.sort(Comparator.comparingInt(Rule::getOrder));

                // Broadcast update to web panel
                broadcastRulesUpdate();

                return rule;
            } catch (SQLException e) {
                plugin.logError("Failed to save rule", e);
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteRule(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getDatabaseManager().update("DELETE FROM moderex_rules WHERE id = ?", id);
                rules.removeIf(r -> r.getId() == id);
                broadcastRulesUpdate();
                return true;
            } catch (SQLException e) {
                plugin.logError("Failed to delete rule", e);
                return false;
            }
        });
    }

    public CompletableFuture<Void> reorderRules(List<Integer> orderedIds) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < orderedIds.size(); i++) {
                    int id = orderedIds.get(i);
                    int newOrder = i + 1;
                    plugin.getDatabaseManager().update(
                            "UPDATE moderex_rules SET rule_order = ?, updated_at = ? WHERE id = ?",
                            newOrder, System.currentTimeMillis(), id
                    );

                    Rule rule = getRule(id);
                    if (rule != null) {
                        rule.setOrder(newOrder);
                    }
                }
                rules.sort(Comparator.comparingInt(Rule::getOrder));
                broadcastRulesUpdate();
            } catch (SQLException e) {
                plugin.logError("Failed to reorder rules", e);
            }
        });
    }

    private void broadcastRulesUpdate() {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastRulesUpdate(getRules());
        }
    }

    public void reload() {
        loadRules();
    }
}
