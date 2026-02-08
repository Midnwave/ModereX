package com.blockforge.moderex.evidence;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TargetResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Manages in-game evidence selection sessions for staff members.
 * Handles the chat-based UI for Java players selecting activity log entries
 * as evidence for punishments.
 */
public class EvidenceSelectionManager {

    private final ModereX plugin;
    private final Map<UUID, EvidenceSelectionSession> activeSessions = new ConcurrentHashMap<>();
    private int cleanupTaskId = -1;

    // Activity types that can be used as evidence
    private static final List<ActivityType> EVIDENCE_TYPES = List.of(
            ActivityType.CHAT,
            ActivityType.COMMAND,
            ActivityType.AUTOMOD_TRIGGER,
            ActivityType.ANTICHEAT_ALERT,
            ActivityType.SIGN,
            ActivityType.ANVIL_RENAME
    );

    public EvidenceSelectionManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the manager and start cleanup task.
     */
    public void initialize() {
        // Run cleanup every 30 seconds
        cleanupTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, this::cleanupExpiredSessions, 600L, 600L
        ).getTaskId();

        plugin.getLogger().info("Evidence selection manager initialized");
    }

    /**
     * Shutdown the manager.
     */
    public void shutdown() {
        if (cleanupTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(cleanupTaskId);
            cleanupTaskId = -1;
        }

        // Cancel all active sessions
        for (EvidenceSelectionSession session : activeSessions.values()) {
            if (session.isActive()) {
                session.cancel();
            }
        }
        activeSessions.clear();
    }

    /**
     * Check if a player has an active evidence selection session.
     */
    public boolean hasActiveSession(UUID playerUuid) {
        EvidenceSelectionSession session = activeSessions.get(playerUuid);
        return session != null && session.isActive();
    }

    /**
     * Get the active session for a player.
     */
    public EvidenceSelectionSession getSession(UUID playerUuid) {
        EvidenceSelectionSession session = activeSessions.get(playerUuid);
        if (session != null && !session.isActive()) {
            activeSessions.remove(playerUuid);
            return null;
        }
        return session;
    }

    /**
     * Start a new evidence selection session for a staff member.
     * Fetches activity logs for the target player and presents them in chat.
     *
     * @param staff The staff member player
     * @param target The target of the punishment
     * @param punishmentType BAN, MUTE, WARN, or KICK
     * @param duration Duration in milliseconds (-1 for permanent)
     * @param reason Punishment reason
     * @param callback Called when session completes or is cancelled
     * @return true if session started successfully
     */
    public boolean startSession(Player staff, TargetResolver target, String punishmentType,
                                 long duration, String reason,
                                 Consumer<EvidenceSelectionSession.EvidenceSelectionResult> callback) {
        // Cancel any existing session
        EvidenceSelectionSession existing = activeSessions.get(staff.getUniqueId());
        if (existing != null && existing.isActive()) {
            existing.cancel();
        }

        // Fetch activity logs for the target
        if (!plugin.getActivityLogManager().isEnabled()) {
            plugin.logDebug("[EvidenceSelection] Activity log disabled, skipping evidence selection");
            // Execute callback immediately with no evidence
            callback.accept(new EvidenceSelectionSession.EvidenceSelectionResult(null, true));
            return false;
        }

        // Get recent activity logs (last 24 hours, max 20 entries)
        long since = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        List<ActivityLogEntry> entries = plugin.getActivityLogManager().getEntries(
                target.getUuid(), EVIDENCE_TYPES, since, 1, 20
        );

        if (entries.isEmpty()) {
            // No entries to show
            sendNoEntriesMessage(staff, target);
            callback.accept(new EvidenceSelectionSession.EvidenceSelectionResult(null, true));
            return false;
        }

        // Create session
        EvidenceSelectionSession session = new EvidenceSelectionSession(
                staff.getUniqueId(), staff.getName(), target,
                punishmentType, duration, reason, entries, callback
        );

        activeSessions.put(staff.getUniqueId(), session);

        // Check if player is Bedrock (via Geyser/Floodgate) - use GUI instead of chat
        boolean isBedrock = isBedrockPlayer(staff);
        if (isBedrock) {
            // Open GUI for Bedrock players
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getGuiManager().open(staff,
                        new com.blockforge.moderex.gui.EvidenceSelectionGui(plugin, session));
            });
        } else {
            // Display chat-based UI for Java players
            displayEvidenceUI(staff, session);
        }

        return true;
    }

    /**
     * Check if a player is a Bedrock player via Geyser/Floodgate.
     */
    private boolean isBedrockPlayer(Player player) {
        try {
            // Check via Floodgate API if available
            if (plugin.getServer().getPluginManager().getPlugin("floodgate") != null) {
                Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                Object apiInstance = floodgateApiClass.getMethod("getInstance").invoke(null);
                Boolean isFloodgate = (Boolean) floodgateApiClass.getMethod("isFloodgatePlayer", java.util.UUID.class)
                        .invoke(apiInstance, player.getUniqueId());
                if (isFloodgate != null && isFloodgate) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Floodgate not available or API call failed
            plugin.logDebug("[EvidenceSelection] Floodgate check failed: " + e.getMessage());
        }

        // Fallback: Check for Bedrock username prefix (commonly ".")
        String name = player.getName();
        if (name.startsWith(".") || name.startsWith("*")) {
            return true;
        }

        return false;
    }

    /**
     * Handle chat input from a player with an active session.
     * @return true if the input was consumed by the evidence system
     */
    public boolean handleChatInput(Player player, String message) {
        EvidenceSelectionSession session = getSession(player.getUniqueId());
        if (session == null) {
            return false;
        }

        String input = message.trim().toLowerCase();

        // Handle special commands
        if (input.equals("confirm") || input.equals("done") || input.equals("ok")) {
            confirmSession(player, session);
            return true;
        }

        if (input.equals("cancel") || input.equals("abort")) {
            cancelSession(player, session);
            return true;
        }

        if (input.equals("skip") || input.equals("none")) {
            skipSession(player, session);
            return true;
        }

        if (input.equals("clear") || input.equals("reset")) {
            clearSelections(player, session);
            return true;
        }

        if (input.equals("help") || input.equals("?")) {
            showHelp(player);
            return true;
        }

        if (input.equals("list") || input.equals("show")) {
            displayEvidenceUI(player, session);
            return true;
        }

        // Try to parse as numbers (e.g., "1,3,5" or "1 3 5" or "+2" or "-4")
        if (input.startsWith("+") || input.startsWith("-")) {
            handleSingleToggle(player, session, input);
            return true;
        }

        // Try parsing as comma/space separated numbers
        if (input.matches("[0-9,\\s]+")) {
            List<Integer> toggled = session.selectMultiple(input);
            if (!toggled.isEmpty()) {
                displaySelectionUpdate(player, session, toggled);
            } else {
                Msg.send(player, Component.text("Invalid number(s). Use numbers 1-" + session.getAvailableEntries().size(), NamedTextColor.RED));
            }
            return true;
        }

        return false; // Not consumed, let other handlers process
    }

    /**
     * Handle click events from the evidence UI.
     */
    public void handleClick(Player player, String action) {
        EvidenceSelectionSession session = getSession(player.getUniqueId());
        if (session == null) {
            return;
        }

        if (action.startsWith("toggle:")) {
            try {
                int num = Integer.parseInt(action.substring(7));
                if (session.toggleByNumber(num)) {
                    displayEvidenceUI(player, session);
                } else {
                    Msg.send(player, Component.text("Cannot select more (max " + EvidenceSelectionSession.MAX_SELECTIONS + ")", NamedTextColor.RED));
                }
            } catch (NumberFormatException ignored) {
            }
        } else if (action.equals("confirm")) {
            confirmSession(player, session);
        } else if (action.equals("cancel")) {
            cancelSession(player, session);
        } else if (action.equals("skip")) {
            skipSession(player, session);
        }
    }

    private void handleSingleToggle(Player player, EvidenceSelectionSession session, String input) {
        boolean isAdd = input.startsWith("+");
        try {
            int num = Integer.parseInt(input.substring(1).trim());
            if (num < 1 || num > session.getAvailableEntries().size()) {
                Msg.send(player, Component.text("Invalid number. Use 1-" + session.getAvailableEntries().size(), NamedTextColor.RED));
                return;
            }

            ActivityLogEntry entry = session.getAvailableEntries().get(num - 1);
            boolean success;
            if (isAdd) {
                success = session.selectEntry(entry.getId());
                if (!success) {
                    Msg.send(player, Component.text("Cannot select more (max " + EvidenceSelectionSession.MAX_SELECTIONS + ")", NamedTextColor.RED));
                    return;
                }
            } else {
                success = session.deselectEntry(entry.getId());
            }

            if (success) {
                displaySelectionUpdate(player, session, List.of(num));
            }
        } catch (NumberFormatException e) {
            Msg.send(player, Component.text("Invalid format. Use +N to add or -N to remove", NamedTextColor.RED));
        }
    }

    private void confirmSession(Player player, EvidenceSelectionSession session) {
        if (!session.hasSelections()) {
            Msg.send(player, Component.text("No evidence selected. Type ", NamedTextColor.YELLOW)
                    .append(Component.text("skip", NamedTextColor.GOLD))
                    .append(Component.text(" to proceed without evidence, or select entries first.", NamedTextColor.YELLOW)));
            return;
        }

        activeSessions.remove(player.getUniqueId());
        session.confirm();

        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("✓ ", NamedTextColor.GREEN)
                .append(Component.text("Evidence attached! Executing punishment...", NamedTextColor.GREEN)));
    }

    private void cancelSession(Player player, EvidenceSelectionSession session) {
        activeSessions.remove(player.getUniqueId());
        session.cancel();

        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("✗ ", NamedTextColor.RED)
                .append(Component.text("Evidence selection cancelled. Punishment aborted.", NamedTextColor.GRAY)));
    }

    private void skipSession(Player player, EvidenceSelectionSession session) {
        activeSessions.remove(player.getUniqueId());
        session.skip();

        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("→ ", NamedTextColor.YELLOW)
                .append(Component.text("Skipped evidence. Executing punishment...", NamedTextColor.GRAY)));
    }

    private void clearSelections(Player player, EvidenceSelectionSession session) {
        for (Long id : new ArrayList<>(session.getSelectedEntryIds())) {
            session.deselectEntry(id);
        }
        Msg.send(player, Component.text("Cleared all selections.", NamedTextColor.GRAY));
        displayEvidenceUI(player, session);
    }

    private void showHelp(Player player) {
        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("═══ Evidence Selection Help ═══", NamedTextColor.GOLD));
        Msg.send(player, Component.text("• Type numbers to toggle: ", NamedTextColor.GRAY)
                .append(Component.text("1,3,5", NamedTextColor.WHITE)));
        Msg.send(player, Component.text("• Add entry: ", NamedTextColor.GRAY)
                .append(Component.text("+2", NamedTextColor.GREEN)));
        Msg.send(player, Component.text("• Remove entry: ", NamedTextColor.GRAY)
                .append(Component.text("-2", NamedTextColor.RED)));
        Msg.send(player, Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text("confirm", NamedTextColor.GREEN))
                .append(Component.text(" - Attach evidence and punish", NamedTextColor.GRAY)));
        Msg.send(player, Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text("skip", NamedTextColor.YELLOW))
                .append(Component.text(" - Punish without evidence", NamedTextColor.GRAY)));
        Msg.send(player, Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text("cancel", NamedTextColor.RED))
                .append(Component.text(" - Abort punishment", NamedTextColor.GRAY)));
        Msg.send(player, Component.text("• ", NamedTextColor.GRAY)
                .append(Component.text("list", NamedTextColor.AQUA))
                .append(Component.text(" - Show entries again", NamedTextColor.GRAY)));
        Msg.send(player, Component.text("════════════════════════════════", NamedTextColor.GOLD));
    }

    /**
     * Display the evidence selection UI in chat.
     */
    private void displayEvidenceUI(Player player, EvidenceSelectionSession session) {
        List<ActivityLogEntry> entries = session.getAvailableEntries();
        String targetName = session.getTarget().getDisplayName();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        Msg.send(player, Component.text("  Attach Evidence to Punishment", NamedTextColor.GOLD, TextDecoration.BOLD));
        Msg.send(player, Component.text("  Target: ", NamedTextColor.GRAY)
                .append(Component.text(targetName, NamedTextColor.WHITE))
                .append(Component.text(" | Type: ", NamedTextColor.GRAY))
                .append(Component.text(session.getPunishmentType(), NamedTextColor.RED)));
        Msg.send(player, Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
        Msg.send(player, Component.text(""));

        // Show max selection count
        int remaining = EvidenceSelectionSession.MAX_SELECTIONS - session.getSelectionCount();
        Msg.send(player, Component.text("  Selected: ", NamedTextColor.GRAY)
                .append(Component.text(session.getSelectionCount() + "/" + EvidenceSelectionSession.MAX_SELECTIONS,
                        session.getSelectionCount() > 0 ? NamedTextColor.GREEN : NamedTextColor.WHITE))
                .append(Component.text(" (max " + EvidenceSelectionSession.MAX_SELECTIONS + ")", NamedTextColor.DARK_GRAY)));
        Msg.send(player, Component.text(""));

        // Display entries
        for (int i = 0; i < entries.size(); i++) {
            ActivityLogEntry entry = entries.get(i);
            int displayNum = i + 1;
            boolean selected = session.isSelected(entry.getId());

            // Build the entry line
            TextComponent.Builder line = Component.text();

            // Selection indicator and number
            if (selected) {
                line.append(Component.text("  [✓] ", NamedTextColor.GREEN));
            } else {
                line.append(Component.text("  [ ] ", NamedTextColor.DARK_GRAY));
            }

            // Number (clickable)
            Component numberComp = Component.text(displayNum + ". ", selected ? NamedTextColor.GREEN : NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.runCommand("/mx:evidence toggle:" + displayNum))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to " + (selected ? "deselect" : "select"))));
            line.append(numberComp);

            // Type badge
            String typeName = entry.getTypeDisplayName();
            NamedTextColor typeColor = getTypeColor(entry.getType());
            line.append(Component.text("[" + typeName + "] ", typeColor));

            // Content (truncated if too long)
            String content = entry.getContent();
            if (content.length() > 40) {
                content = content.substring(0, 37) + "...";
            }

            // Apply formatting based on selection
            Component contentComp;
            if (selected) {
                contentComp = Component.text(content, NamedTextColor.WHITE, TextDecoration.UNDERLINED);
            } else {
                contentComp = Component.text(content, NamedTextColor.GRAY);
            }

            line.append(contentComp);

            // Timestamp
            line.append(Component.text(" [" + sdf.format(new Date(entry.getTimestamp())) + "]", NamedTextColor.DARK_GRAY));

            // Add hover with full content
            Component fullLine = line.build().hoverEvent(HoverEvent.showText(
                    Component.text(entry.getTypeDisplayName(), typeColor)
                            .append(Component.text("\n" + entry.getContent(), NamedTextColor.WHITE))
                            .append(Component.text("\n\nClick to " + (selected ? "deselect" : "select"), NamedTextColor.GRAY))
            )).clickEvent(ClickEvent.runCommand("/mx:evidence toggle:" + displayNum));

            Msg.send(player, fullLine);
        }

        Msg.send(player, Component.text(""));

        // Action buttons
        TextComponent.Builder actions = Component.text();
        actions.append(Component.text("  "));

        // Confirm button (only if has selections)
        if (session.hasSelections()) {
            actions.append(Component.text("[Confirm]", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/mx:evidence confirm"))
                    .hoverEvent(HoverEvent.showText(Component.text("Attach " + session.getSelectionCount() + " entries as evidence"))));
            actions.append(Component.text("  "));
        }

        // Skip button
        actions.append(Component.text("[Skip]", NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/mx:evidence skip"))
                .hoverEvent(HoverEvent.showText(Component.text("Proceed without evidence"))));
        actions.append(Component.text("  "));

        // Cancel button
        actions.append(Component.text("[Cancel]", NamedTextColor.RED)
                .clickEvent(ClickEvent.runCommand("/mx:evidence cancel"))
                .hoverEvent(HoverEvent.showText(Component.text("Abort punishment"))));

        Msg.send(player, actions.build());

        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("  Type numbers (1,3,5) or click to select. Type ", NamedTextColor.DARK_GRAY)
                .append(Component.text("help", NamedTextColor.GRAY)
                        .clickEvent(ClickEvent.runCommand("/mx:evidence help")))
                .append(Component.text(" for more options.", NamedTextColor.DARK_GRAY)));
        Msg.send(player, Component.text("═══════════════════════════════════════", NamedTextColor.GOLD));
    }

    private void displaySelectionUpdate(Player player, EvidenceSelectionSession session, List<Integer> toggledNumbers) {
        StringBuilder toggled = new StringBuilder();
        for (int i = 0; i < toggledNumbers.size(); i++) {
            if (i > 0) toggled.append(", ");
            int num = toggledNumbers.get(i);
            boolean isNowSelected = session.isSelectedByNumber(num);
            toggled.append(isNowSelected ? "+" : "-").append(num);
        }

        Msg.send(player, Component.text("Toggled: ", NamedTextColor.GRAY)
                .append(Component.text(toggled.toString(), NamedTextColor.YELLOW))
                .append(Component.text(" | Selected: " + session.getSelectionCount() + "/" + EvidenceSelectionSession.MAX_SELECTIONS, NamedTextColor.DARK_GRAY)));
    }

    private void sendNoEntriesMessage(Player player, TargetResolver target) {
        Msg.send(player, Component.text(""));
        Msg.send(player, Component.text("ℹ ", NamedTextColor.GRAY)
                .append(Component.text("No recent activity found for ", NamedTextColor.GRAY))
                .append(Component.text(target.getDisplayName(), NamedTextColor.WHITE))
                .append(Component.text(". Proceeding without evidence.", NamedTextColor.GRAY)));
    }

    private NamedTextColor getTypeColor(ActivityType type) {
        return switch (type) {
            case CHAT -> NamedTextColor.AQUA;
            case COMMAND -> NamedTextColor.YELLOW;
            case AUTOMOD_TRIGGER -> NamedTextColor.GOLD;
            case ANTICHEAT_ALERT -> NamedTextColor.RED;
            case SIGN, ANVIL_RENAME -> NamedTextColor.LIGHT_PURPLE;
            default -> NamedTextColor.GRAY;
        };
    }

    private void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> {
            EvidenceSelectionSession session = entry.getValue();
            if (session.isExpired()) {
                session.cancel();
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    Msg.send(player, Component.text("Evidence selection timed out. Punishment cancelled.", NamedTextColor.RED));
                }
                return true;
            }
            return !session.isActive();
        });
    }
}
