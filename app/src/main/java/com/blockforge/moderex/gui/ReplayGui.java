package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplayManager.ReplaySessionInfo;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ReplayGui extends PaginatedGui<ReplaySessionInfo> {

    private List<ReplaySessionInfo> cachedReplays = new ArrayList<>();
    private ReplayFilter filter = ReplayFilter.ALL;
    private UUID filterPlayerUuid = null;
    private String filterPlayerName = null;
    private Long filterDateFrom = null;
    private Long filterDateTo = null;

    public ReplayGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Replay Browser</gradient>", 6);
        loadReplays();
    }

    public ReplayGui(ModereX plugin, UUID playerUuid, String playerName) {
        super(plugin, "<gradient:#a855f7:#ec4899>Replays: " + playerName + "</gradient>", 6);
        this.filterPlayerUuid = playerUuid;
        this.filterPlayerName = playerName;
        this.filter = ReplayFilter.PLAYER;
        loadReplays();
    }

    private void loadReplays() {
        CompletableFuture<List<ReplaySessionInfo>> future;

        if (filterPlayerUuid != null) {
            future = plugin.getReplayManager().searchReplaysByPlayer(filterPlayerUuid);
        } else if (filterDateFrom != null && filterDateTo != null) {
            future = plugin.getReplayManager().searchReplaysByDate(filterDateFrom, filterDateTo);
        } else {
            future = plugin.getReplayManager().getSavedReplays();
        }

        future.thenAccept(replays -> {
            // Apply additional filters
            var stream = replays.stream();

            if (filter == ReplayFilter.ANTICHEAT) {
                stream = stream.filter(r -> r.reason() == ReplaySession.RecordingReason.ANTICHEAT_ALERT);
            } else if (filter == ReplayFilter.WATCHLIST) {
                stream = stream.filter(r -> r.reason() == ReplaySession.RecordingReason.WATCHLIST);
            } else if (filter == ReplayFilter.MANUAL) {
                stream = stream.filter(r ->
                    r.reason() == ReplaySession.RecordingReason.MANUAL ||
                    r.reason() == ReplaySession.RecordingReason.STAFF_REQUEST);
            }

            this.cachedReplays = stream.toList();

            if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }

    @Override
    protected List<ReplaySessionInfo> getItems() {
        return cachedReplays;
    }

    @Override
    protected void renderItem(int slot, ReplaySessionInfo replay) {
        ItemStack item = createReplayItem(replay);
        setItem(slot, item, clickType -> {
            if (clickType.isRightClick()) {
                // Show delete confirmation
                showDeleteConfirmation(replay);
            } else {
                // Start playback
                startReplayPlayback(replay);
            }
        });
    }

    @Override
    protected void populate() {
        super.populate();

        // Filter buttons in top row
        setItem(0, createFilterButton(ReplayFilter.ALL, "All Replays", Material.CHEST), () -> {
            filter = ReplayFilter.ALL;
            filterPlayerUuid = null;
            filterPlayerName = null;
            filterDateFrom = null;
            filterDateTo = null;
            loadReplays();
        });

        setItem(1, createFilterButton(ReplayFilter.ANTICHEAT, "Anticheat Alerts", Material.IRON_SWORD), () -> {
            filter = ReplayFilter.ANTICHEAT;
            filterPlayerUuid = null;
            loadReplays();
        });

        setItem(2, createFilterButton(ReplayFilter.WATCHLIST, "Watchlist", Material.SPYGLASS), () -> {
            filter = ReplayFilter.WATCHLIST;
            filterPlayerUuid = null;
            loadReplays();
        });

        setItem(3, createFilterButton(ReplayFilter.MANUAL, "Manual", Material.WRITABLE_BOOK), () -> {
            filter = ReplayFilter.MANUAL;
            filterPlayerUuid = null;
            loadReplays();
        });

        // Search buttons
        setItem(5, createItem(Material.PLAYER_HEAD, "<aqua>Search by Player",
                "<gray>Find replays for a specific player",
                "",
                "<yellow>Click to search"), this::promptPlayerSearch);

        setItem(6, createItem(Material.CLOCK, "<gold>Search by Date",
                "<gray>Find replays within a date range",
                "",
                "<yellow>Click to search"), this::promptDateSearch);

        // Settings button
        setItem(7, createItem(Material.COMPARATOR, "<light_purple>Replay Settings",
                "<gray>Configure replay system",
                "",
                "<white>Enabled: " + (plugin.getReplayManager().isEnabled() ? "<green>Yes" : "<red>No"),
                "<white>Anticheat Recording: " + (plugin.getReplayManager().isRecordOnAnticheatAlert() ? "<green>On" : "<red>Off"),
                "<white>Watchlist Recording: " + (plugin.getReplayManager().isRecordWatchlistPlayers() ? "<green>On" : "<red>Off"),
                "",
                "<yellow>Click to configure"), () -> openGui(new ReplaySettingsGui(plugin)));

        // Recording indicator
        setItem(8, createRecordingStatusItem());
    }

    private ItemStack createFilterButton(ReplayFilter filterType, String label, Material icon) {
        boolean selected = (filter == filterType);
        return createItem(selected ? Material.LIME_DYE : icon,
                (selected ? "<green>" : "<gray>") + label,
                selected ? "<green>Currently viewing" : "<yellow>Click to filter");
    }

    private ItemStack createReplayItem(ReplaySessionInfo replay) {
        Material material = getMaterialForReason(replay.reason());
        String color = getColorForReason(replay.reason());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateFormat.format(new Date(replay.startTime()));

        long durationMs = replay.getDuration();
        String durationStr = formatDuration(durationMs);

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Session: <white>" + replay.sessionId());
        lore.add("<gray>Player: <white>" + replay.primaryName());
        lore.add("<gray>World: <white>" + replay.worldName());
        lore.add("<gray>Reason: " + color + formatReason(replay.reason()));
        lore.add("<gray>Date: <white>" + dateStr);
        lore.add("<gray>Duration: <white>" + durationStr);
        lore.add("<gray>Players: <white>" + replay.playerCount());
        lore.add("");
        lore.add("<yellow>Left-click: <white>Play replay");
        lore.add("<red>Right-click: <white>Delete");

        // Try to create player head
        ItemStack item;
        try {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(replay.primaryUuid());
            meta.setOwningPlayer(player);
            meta.displayName(TextUtil.parse(color + replay.primaryName() + " <gray>- " + formatReason(replay.reason())));
            meta.lore(lore.stream().map(TextUtil::parse).toList());
            item.setItemMeta(meta);
        } catch (Exception e) {
            item = createItem(material,
                    color + replay.primaryName() + " <gray>- " + formatReason(replay.reason()),
                    lore);
        }

        return item;
    }

    private ItemStack createRecordingStatusItem() {
        int activeCount = 0; // Would need method to get count from ReplayManager

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Currently recording players");
        lore.add("");

        if (activeCount == 0) {
            lore.add("<gray>No active recordings");
        } else {
            lore.add("<green>Active recordings: <white>" + activeCount);
        }

        lore.add("");
        lore.add("<yellow>Click to start manual recording");

        return createItem(Material.RED_CONCRETE, "<red>Recording Status", lore);
    }

    private Material getMaterialForReason(ReplaySession.RecordingReason reason) {
        return switch (reason) {
            case ANTICHEAT_ALERT -> Material.IRON_SWORD;
            case WATCHLIST -> Material.SPYGLASS;
            case MANUAL -> Material.WRITABLE_BOOK;
            case STAFF_REQUEST -> Material.GOLDEN_SWORD;
            case AUTOMOD_TRIGGER -> Material.COMPARATOR;
        };
    }

    private String getColorForReason(ReplaySession.RecordingReason reason) {
        return switch (reason) {
            case ANTICHEAT_ALERT -> "<red>";
            case WATCHLIST -> "<aqua>";
            case MANUAL -> "<yellow>";
            case STAFF_REQUEST -> "<gold>";
            case AUTOMOD_TRIGGER -> "<light_purple>";
        };
    }

    private String formatReason(ReplaySession.RecordingReason reason) {
        return switch (reason) {
            case ANTICHEAT_ALERT -> "Anticheat Alert";
            case WATCHLIST -> "Watchlist";
            case MANUAL -> "Manual Recording";
            case STAFF_REQUEST -> "Staff Request";
            case AUTOMOD_TRIGGER -> "Automod Trigger";
        };
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }

    private void startReplayPlayback(ReplaySessionInfo replay) {
        viewer.sendMessage(TextUtil.parse("<yellow>Loading replay " + replay.sessionId() + "..."));
        close();

        plugin.getReplayManager().loadReplay(replay.sessionId()).thenAccept(session -> {
            if (session == null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    viewer.sendMessage(TextUtil.parse("<red>Failed to load replay!"));
                });
                return;
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getReplayManager().startPlayback(viewer, session);
            });
        });
    }

    private void showDeleteConfirmation(ReplaySessionInfo replay) {
        openGui(new ConfirmGui(plugin, "<red>Delete Replay?",
                "Are you sure you want to delete this replay?",
                replay.sessionId(),
                () -> {
                    plugin.getReplayManager().deleteReplay(replay.sessionId()).thenAccept(success -> {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (success) {
                                viewer.sendMessage(TextUtil.parse("<green>Replay deleted successfully!"));
                            } else {
                                viewer.sendMessage(TextUtil.parse("<red>Failed to delete replay!"));
                            }
                            openGui(new ReplayGui(plugin));
                        });
                    });
                },
                () -> openGui(this)
        ));
    }

    private void promptPlayerSearch() {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter the player name to search replays:"));
        viewer.sendMessage(TextUtil.parse("<gray>Type 'cancel' to cancel"));

        new ConversationFactory(plugin)
                .withModality(true)
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("cancel")) {
                            context.getForWhom().sendRawMessage(toLegacy("<red>Cancelled."));
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                openGui(ReplayGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        @SuppressWarnings("deprecation")
                        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            openGui(new ReplayGui(plugin, target.getUniqueId(), target.getName() != null ? target.getName() : input));
                        });

                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    private void promptDateSearch() {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter the date range to search (format: YYYY-MM-DD to YYYY-MM-DD):"));
        viewer.sendMessage(TextUtil.parse("<gray>Example: 2024-01-01 to 2024-01-31"));
        viewer.sendMessage(TextUtil.parse("<gray>Or type 'today' for today, 'week' for last 7 days, 'month' for last 30 days"));
        viewer.sendMessage(TextUtil.parse("<gray>Type 'cancel' to cancel"));

        new ConversationFactory(plugin)
                .withModality(true)
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("cancel")) {
                            context.getForWhom().sendRawMessage(toLegacy("<red>Cancelled."));
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                openGui(ReplayGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        long from, to;
                        long now = System.currentTimeMillis();
                        long dayMs = 24 * 60 * 60 * 1000L;

                        if (input.equalsIgnoreCase("today")) {
                            from = now - dayMs;
                            to = now;
                        } else if (input.equalsIgnoreCase("week")) {
                            from = now - (7 * dayMs);
                            to = now;
                        } else if (input.equalsIgnoreCase("month")) {
                            from = now - (30 * dayMs);
                            to = now;
                        } else {
                            // Try to parse date range
                            try {
                                String[] parts = input.split(" to ");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                from = sdf.parse(parts[0].trim()).getTime();
                                to = sdf.parse(parts[1].trim()).getTime() + dayMs; // End of day
                            } catch (Exception e) {
                                context.getForWhom().sendRawMessage(toLegacy("<red>Invalid date format! Use YYYY-MM-DD to YYYY-MM-DD"));
                                plugin.getServer().getScheduler().runTask(plugin, () -> {
                                    openGui(ReplayGui.this);
                                });
                                return Prompt.END_OF_CONVERSATION;
                            }
                        }

                        final long finalFrom = from;
                        final long finalTo = to;

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            ReplayGui gui = new ReplayGui(plugin);
                            gui.filterDateFrom = finalFrom;
                            gui.filterDateTo = finalTo;
                            gui.loadReplays();
                            openGui(gui);
                        });

                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    @Override
    protected void addNavigationButtons() {
        super.addNavigationButtons();
        // Back to main menu
        setItem(45, createBackButton(), () -> openGui(new MainMenuGui(plugin)));
    }

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }

    private enum ReplayFilter {
        ALL, ANTICHEAT, WATCHLIST, MANUAL, PLAYER, DATE
    }

    public static class ConfirmGui extends BaseGui {
        private final String description;
        private final String details;
        private final Runnable onConfirm;
        private final Runnable onCancel;

        public ConfirmGui(ModereX plugin, String title, String description, String details,
                         Runnable onConfirm, Runnable onCancel) {
            super(plugin, title, 3);
            this.description = description;
            this.details = details;
            this.onConfirm = onConfirm;
            this.onCancel = onCancel;
        }

        @Override
        protected void populate() {
            fillBorder(Material.BLACK_STAINED_GLASS_PANE);

            // Description
            setItem(4, createItem(Material.PAPER, "<yellow>Confirmation",
                    "<white>" + description,
                    "",
                    "<gray>" + details));

            // Confirm button
            setItem(11, createItem(Material.LIME_CONCRETE, "<green>Confirm",
                    "<gray>Click to confirm this action"), () -> {
                close();
                onConfirm.run();
            });

            // Cancel button
            setItem(15, createItem(Material.RED_CONCRETE, "<red>Cancel",
                    "<gray>Click to cancel"), () -> {
                close();
                onCancel.run();
            });
        }
    }

    public static class ReplaySettingsGui extends BaseGui {

        public ReplaySettingsGui(ModereX plugin) {
            super(plugin, "<light_purple>Replay Settings", 4);
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            // Title
            setItem(4, createItem(Material.JUKEBOX, "<gradient:#a855f7:#ec4899>Replay System Settings</gradient>",
                    "<gray>Configure recording behavior"));

            // System enabled toggle
            boolean enabled = plugin.getReplayManager().isEnabled();
            setItem(10, createItem(
                    enabled ? Material.LIME_DYE : Material.RED_DYE,
                    enabled ? "<green>System Enabled" : "<red>System Disabled",
                    "<gray>Toggle the replay system on/off",
                    "",
                    "<yellow>Click to " + (enabled ? "disable" : "enable")
            ), () -> {
                plugin.getReplayManager().setEnabled(!enabled);
                refresh();
            });

            // Anticheat recording toggle
            boolean acRecord = plugin.getReplayManager().isRecordOnAnticheatAlert();
            setItem(12, createItem(
                    acRecord ? Material.LIME_DYE : Material.RED_DYE,
                    acRecord ? "<green>Anticheat Recording: On" : "<red>Anticheat Recording: Off",
                    "<gray>Auto-record when anticheat alerts trigger",
                    "",
                    "<yellow>Click to toggle"
            ), () -> {
                plugin.getReplayManager().setRecordOnAnticheatAlert(!acRecord);
                refresh();
            });

            // Watchlist recording toggle
            boolean wlRecord = plugin.getReplayManager().isRecordWatchlistPlayers();
            setItem(14, createItem(
                    wlRecord ? Material.LIME_DYE : Material.RED_DYE,
                    wlRecord ? "<green>Watchlist Recording: On" : "<red>Watchlist Recording: Off",
                    "<gray>Auto-record players on the watchlist",
                    "",
                    "<yellow>Click to toggle"
            ), () -> {
                plugin.getReplayManager().setRecordWatchlistPlayers(!wlRecord);
                refresh();
            });

            // Nearby player radius
            int radius = plugin.getReplayManager().getNearbyPlayerRadius();
            setItem(16, createItem(Material.ENDER_PEARL, "<aqua>Nearby Radius: " + radius + " blocks",
                    "<gray>Include nearby players in recordings",
                    "",
                    "<yellow>Left-click: +5",
                    "<yellow>Right-click: -5",
                    "<yellow>Shift-click: Reset to 20"
            ), clickType -> {
                int current = plugin.getReplayManager().getNearbyPlayerRadius();
                if (clickType.isShiftClick()) {
                    plugin.getReplayManager().setNearbyPlayerRadius(20);
                } else if (clickType.isLeftClick()) {
                    plugin.getReplayManager().setNearbyPlayerRadius(Math.min(100, current + 5));
                } else if (clickType.isRightClick()) {
                    plugin.getReplayManager().setNearbyPlayerRadius(Math.max(0, current - 5));
                }
                refresh();
            });

            // Max recording duration
            int maxDuration = plugin.getReplayManager().getMaxRecordingDurationSeconds();
            setItem(20, createItem(Material.CLOCK, "<gold>Max Duration: " + (maxDuration / 60) + " minutes",
                    "<gray>Maximum recording length",
                    "",
                    "<yellow>Left-click: +1 min",
                    "<yellow>Right-click: -1 min",
                    "<yellow>Shift-click: Reset to 5 min"
            ), clickType -> {
                int current = plugin.getReplayManager().getMaxRecordingDurationSeconds();
                if (clickType.isShiftClick()) {
                    plugin.getReplayManager().setMaxRecordingDurationSeconds(300);
                } else if (clickType.isLeftClick()) {
                    plugin.getReplayManager().setMaxRecordingDurationSeconds(Math.min(1800, current + 60));
                } else if (clickType.isRightClick()) {
                    plugin.getReplayManager().setMaxRecordingDurationSeconds(Math.max(60, current - 60));
                }
                refresh();
            });

            // Storage info
            setItem(22, createItem(Material.CHEST, "<yellow>Storage Info",
                    "<gray>Replay storage statistics",
                    "",
                    "<white>Max stored: <yellow>1000 replays",
                    "<gray>Oldest replays are auto-deleted"));

            // Manual record button
            setItem(24, createItem(Material.RED_CONCRETE, "<red>Start Manual Recording",
                    "<gray>Select a player to record",
                    "",
                    "<yellow>Click to select player"), () -> {
                openGui(new SelectPlayerToRecordGui(plugin));
            });

            // Back button
            setItem(31, createBackButton(), () -> openGui(new ReplayGui(plugin)));
        }
    }

    public static class SelectPlayerToRecordGui extends PaginatedGui<Player> {

        public SelectPlayerToRecordGui(ModereX plugin) {
            super(plugin, "<red>Select Player to Record", 6);
        }

        @Override
        protected List<Player> getItems() {
            return new ArrayList<>(Bukkit.getOnlinePlayers());
        }

        @Override
        protected void renderItem(int slot, Player player) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            meta.displayName(TextUtil.parse("<yellow>" + player.getName()));

            List<String> lore = new ArrayList<>();
            lore.add("<gray>UUID: <white>" + player.getUniqueId().toString().substring(0, 8) + "...");
            lore.add("");

            // Check if already recording
            // Would need a method in ReplayManager to check this
            lore.add("<yellow>Click to start recording");

            meta.lore(lore.stream().map(TextUtil::parse).toList());
            head.setItemMeta(meta);

            setItem(slot, head, () -> {
                plugin.getReplayManager().startRecording(player, ReplaySession.RecordingReason.STAFF_REQUEST);
                viewer.sendMessage(TextUtil.parse("<green>Started recording " + player.getName() + "!"));
                viewer.sendMessage(TextUtil.parse("<gray>Use /mx replay stop " + player.getName() + " to stop"));
                close();
            });
        }

        @Override
        protected void addNavigationButtons() {
            super.addNavigationButtons();
            setItem(45, createBackButton(), () -> openGui(new ReplaySettingsGui(plugin)));
        }
    }
}
