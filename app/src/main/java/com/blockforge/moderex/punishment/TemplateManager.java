package com.blockforge.moderex.punishment;

import com.blockforge.moderex.ModereX;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TemplateManager {

    private final ModereX plugin;
    private final Map<String, PunishmentTemplate> templates = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TemplateManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void load() {
        templates.clear();

        // Create default templates if none exist
        createDefaultTemplates();

        // Load from database
        try {
            plugin.getDatabaseManager().query(
                    "SELECT * FROM moderex_templates WHERE active = TRUE ORDER BY priority ASC, name ASC",
                    rs -> {
                        while (rs.next()) {
                            PunishmentTemplate template = new PunishmentTemplate();
                            template.setId(rs.getString("id"));
                            template.setName(rs.getString("name"));
                            template.setType(PunishmentType.valueOf(rs.getString("type")));
                            template.setDuration(rs.getString("duration"));
                            template.setReason(rs.getString("reason"));
                            template.setCategory(rs.getString("category"));
                            template.setPriority(rs.getInt("priority"));
                            template.setActive(rs.getBoolean("active"));
                            template.setCreatedBy(rs.getString("created_by") != null
                                    ? UUID.fromString(rs.getString("created_by")) : null);
                            template.setCreatedByName(rs.getString("created_by_name"));
                            template.setCreatedAt(rs.getLong("created_at"));
                            template.setUpdatedAt(rs.getLong("updated_at"));

                            templates.put(template.getId(), template);
                        }
                        return null;
                    }
            );
        } catch (SQLException e) {
            plugin.logError("Failed to load templates from database", e);
        }

        plugin.getLogger().info("Loaded " + templates.size() + " punishment templates");
    }

    private void createDefaultTemplates() {
        try {
            // Check if templates exist
            Long count = plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) FROM moderex_templates",
                    rs -> rs.next() ? rs.getLong(1) : 0L
            );

            if (count != null && count == 0) {
                // Create defaults
                List<PunishmentTemplate> defaults = Arrays.asList(
                        createTemplate("First Offense Warning", PunishmentType.WARN, "", "First offense warning - please review the rules", "General", 0),
                        createTemplate("Chat Spam", PunishmentType.MUTE, "30m", "Excessive chat spam", "Chat", 1),
                        createTemplate("Toxic Language", PunishmentType.MUTE, "1h", "Use of toxic/inappropriate language", "Chat", 2),
                        createTemplate("Advertising", PunishmentType.MUTE, "1d", "Advertising other servers or content", "Chat", 3),
                        createTemplate("Minor Hacking", PunishmentType.BAN, "7d", "Use of unauthorized modifications", "Cheating", 4),
                        createTemplate("Major Hacking", PunishmentType.BAN, "30d", "Serious use of hacked client", "Cheating", 5),
                        createTemplate("Permanent Ban", PunishmentType.BAN, "permanent", "Permanent ban - severe rule violation", "Cheating", 6),
                        createTemplate("Harassment", PunishmentType.BAN, "14d", "Harassment of other players", "Behavior", 7),
                        createTemplate("AFK Kick", PunishmentType.KICK, "", "AFK timeout", "General", 8),
                        createTemplate("Rule Violation Kick", PunishmentType.KICK, "", "Kicked for rule violation", "General", 9),
                        createTemplate("Alt Account", PunishmentType.IPBAN, "permanent", "Ban evasion using alternate account", "Evasion", 10)
                );

                for (PunishmentTemplate template : defaults) {
                    template.setCreatedByName("System");
                    saveTemplate(template);
                }

                plugin.getLogger().info("Created " + defaults.size() + " default punishment templates");
            }
        } catch (SQLException e) {
            plugin.logError("Failed to create default templates", e);
        }
    }

    private PunishmentTemplate createTemplate(String name, PunishmentType type, String duration, String reason, String category, int priority) {
        PunishmentTemplate template = new PunishmentTemplate(name, type, duration, reason);
        template.setCategory(category);
        template.setPriority(priority);
        return template;
    }

    public void saveTemplate(PunishmentTemplate template) {
        try {
            plugin.getDatabaseManager().update(
                    """
                    INSERT INTO moderex_templates (id, name, type, duration, reason, category, priority, active, created_by, created_by_name, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(id) DO UPDATE SET
                        name = excluded.name,
                        type = excluded.type,
                        duration = excluded.duration,
                        reason = excluded.reason,
                        category = excluded.category,
                        priority = excluded.priority,
                        active = excluded.active,
                        updated_at = excluded.updated_at
                    """,
                    template.getId(),
                    template.getName(),
                    template.getType().name(),
                    template.getDuration(),
                    template.getReason(),
                    template.getCategory(),
                    template.getPriority(),
                    template.isActive(),
                    template.getCreatedBy() != null ? template.getCreatedBy().toString() : null,
                    template.getCreatedByName(),
                    template.getCreatedAt(),
                    template.getUpdatedAt()
            );

            templates.put(template.getId(), template);
        } catch (SQLException e) {
            plugin.logError("Failed to save template " + template.getName(), e);
        }
    }

    public boolean deleteTemplate(String id) {
        try {
            plugin.getDatabaseManager().update(
                    "UPDATE moderex_templates SET active = FALSE WHERE id = ?",
                    id
            );
            templates.remove(id);
            return true;
        } catch (SQLException e) {
            plugin.logError("Failed to delete template " + id, e);
            return false;
        }
    }

    public PunishmentTemplate getTemplate(String id) {
        return templates.get(id);
    }

    public PunishmentTemplate getTemplateByName(String name) {
        return templates.values().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<PunishmentTemplate> getAllTemplates() {
        return templates.values().stream()
                .filter(PunishmentTemplate::isActive)
                .sorted(Comparator.comparingInt(PunishmentTemplate::getPriority)
                        .thenComparing(PunishmentTemplate::getName))
                .collect(Collectors.toList());
    }

    public List<PunishmentTemplate> getTemplatesByType(PunishmentType type) {
        return templates.values().stream()
                .filter(t -> t.isActive() && t.getType() == type)
                .sorted(Comparator.comparingInt(PunishmentTemplate::getPriority)
                        .thenComparing(PunishmentTemplate::getName))
                .collect(Collectors.toList());
    }

    public List<PunishmentTemplate> getTemplatesByCategory(String category) {
        return templates.values().stream()
                .filter(t -> t.isActive() && category.equals(t.getCategory()))
                .sorted(Comparator.comparingInt(PunishmentTemplate::getPriority)
                        .thenComparing(PunishmentTemplate::getName))
                .collect(Collectors.toList());
    }

    public Set<String> getCategories() {
        return templates.values().stream()
                .filter(PunishmentTemplate::isActive)
                .map(PunishmentTemplate::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public List<PunishmentTemplate> searchTemplates(String query) {
        String lowerQuery = query.toLowerCase();
        return templates.values().stream()
                .filter(t -> t.isActive() &&
                        (t.getName().toLowerCase().contains(lowerQuery) ||
                                (t.getReason() != null && t.getReason().toLowerCase().contains(lowerQuery))))
                .sorted(Comparator.comparingInt(PunishmentTemplate::getPriority))
                .collect(Collectors.toList());
    }

    public String exportToJson() {
        JsonArray array = new JsonArray();
        for (PunishmentTemplate template : getAllTemplates()) {
            array.add(template.toJson());
        }
        return gson.toJson(array);
    }

    public int importFromJson(String json) {
        JsonArray array = gson.fromJson(json, JsonArray.class);
        int imported = 0;

        for (var element : array) {
            try {
                PunishmentTemplate template = PunishmentTemplate.fromJson(element.getAsJsonObject());
                // Generate new ID to avoid conflicts
                template.setId(UUID.randomUUID().toString().substring(0, 8));
                saveTemplate(template);
                imported++;
            } catch (Exception e) {
                plugin.logDebug("Failed to import template: " + e.getMessage());
            }
        }

        return imported;
    }
}
