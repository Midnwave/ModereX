package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.player.PlayerProfile;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.Collection;
import java.util.stream.Collectors;

public class AllPlayersWatchlistGui extends PaginatedGui<PlayerProfile> {

    private List<PlayerProfile> cachedPlayers = new ArrayList<>();
    private FilterMode filterMode = FilterMode.ALL;
    private String searchQuery = null;
    private SortMode sortMode = SortMode.LAST_SEEN;

    public AllPlayersWatchlistGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>All Players</gradient> <gray>- Watchlist Manager", 6);
        loadPlayers();
    }

    private void loadPlayers() {
        Collection<PlayerProfile> profiles = plugin.getPlayerProfileManager().getAllProfiles();
        var stream = profiles.stream();

        // Apply filter
        switch (filterMode) {
            case ALL -> {} // No filtering
            case WATCHED -> stream = stream.filter(p ->
                    plugin.getWatchlistManager().isWatched(p.getUuid()));
            case ONLINE -> stream = stream.filter(p ->
                    Bukkit.getPlayer(p.getUuid()) != null);
            case OFFLINE -> stream = stream.filter(p ->
                    Bukkit.getPlayer(p.getUuid()) == null);
            case PUNISHED -> stream = stream.filter(p ->
                    plugin.getPunishmentManager().hasActivePunishment(p.getUuid()));
        }

        // Apply search
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String query = searchQuery.toLowerCase();
            stream = stream.filter(p ->
                    p.getUsername().toLowerCase().contains(query) ||
                    p.getUuid().toString().toLowerCase().contains(query));
        }

        // Apply sort
        stream = switch (sortMode) {
            case LAST_SEEN -> stream.sorted((a, b) -> Long.compare(b.getLastJoin(), a.getLastJoin()));
            case FIRST_JOINED -> stream.sorted((a, b) -> Long.compare(a.getFirstJoin(), b.getFirstJoin()));
            case NAME -> stream.sorted(Comparator.comparing(PlayerProfile::getUsername, String.CASE_INSENSITIVE_ORDER));
            case WATCHED_FIRST -> stream.sorted((a, b) -> {
                boolean aWatched = plugin.getWatchlistManager().isWatched(a.getUuid());
                boolean bWatched = plugin.getWatchlistManager().isWatched(b.getUuid());
                if (aWatched != bWatched) return aWatched ? -1 : 1;
                return Long.compare(b.getLastJoin(), a.getLastJoin());
            });
        };

        this.cachedPlayers = stream.collect(Collectors.toList());

        if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
            plugin.getServer().getScheduler().runTask(plugin, this::refresh);
        }
    }

    @Override
    protected List<PlayerProfile> getItems() {
        return cachedPlayers;
    }

    @Override
    protected void renderItem(int slot, PlayerProfile profile) {
        ItemStack item = createPlayerItem(profile);
        setItem(slot, item, clickType -> {
            if (clickType.isRightClick()) {
                // Toggle watchlist
                toggleWatchlist(profile);
            } else if (clickType.isShiftClick()) {
                // Quick punish
                OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getUuid());
                if (player.isOnline()) {
                    openGui(new com.blockforge.moderex.gui.punishment.PunishPlayerGui(plugin, player.getPlayer()));
                } else {
                    openGui(new WatchlistGui.OfflinePlayerGui(plugin, profile.getUuid(), profile.getUsername()));
                }
            } else {
                // View player details
                showPlayerDetails(profile);
            }
        });
    }

    @Override
    protected void populate() {
        super.populate();

        // Filter buttons
        setItem(0, createFilterButton(FilterMode.ALL, "All Players", Material.CHEST), () -> {
            filterMode = FilterMode.ALL;
            loadPlayers();
        });

        setItem(1, createFilterButton(FilterMode.WATCHED, "Watched Only", Material.SPYGLASS), () -> {
            filterMode = FilterMode.WATCHED;
            loadPlayers();
        });

        setItem(2, createFilterButton(FilterMode.ONLINE, "Online Only", Material.LIME_WOOL), () -> {
            filterMode = FilterMode.ONLINE;
            loadPlayers();
        });

        setItem(3, createFilterButton(FilterMode.OFFLINE, "Offline Only", Material.RED_WOOL), () -> {
            filterMode = FilterMode.OFFLINE;
            loadPlayers();
        });

        setItem(4, createFilterButton(FilterMode.PUNISHED, "Punished", Material.BARRIER), () -> {
            filterMode = FilterMode.PUNISHED;
            loadPlayers();
        });

        // Search button
        setItem(5, createItem(Material.OAK_SIGN, "<aqua>Search Player",
                "<gray>Search by name or UUID",
                "",
                searchQuery != null ? "<white>Current: <yellow>" + searchQuery : "<gray>No search active",
                "",
                "<yellow>Left-click: Search",
                "<red>Right-click: Clear"), clickType -> {
            if (clickType.isRightClick()) {
                searchQuery = null;
                loadPlayers();
            } else {
                promptSearch();
            }
        });

        // Sort button
        setItem(6, createItem(Material.HOPPER, "<gold>Sort: " + sortMode.displayName,
                "<gray>Change sort order",
                "",
                "<yellow>Click to cycle"), () -> {
            sortMode = SortMode.values()[(sortMode.ordinal() + 1) % SortMode.values().length];
            loadPlayers();
        });

        // Bulk actions (admin only)
        if (viewer.hasPermission("moderex.admin")) {
            setItem(7, createItem(Material.COMMAND_BLOCK, "<red>Bulk Actions",
                    "<gray>Mass watchlist management",
                    "",
                    "<yellow>Click for options"), () -> openGui(new BulkActionsGui(plugin)));
        }

        // Stats
        int watchedCount = (int) cachedPlayers.stream()
                .filter(p -> plugin.getWatchlistManager().isWatched(p.getUuid()))
                .count();
        setItem(8, createItem(Material.BOOK, "<white>Statistics",
                "<gray>Total players: <white>" + cachedPlayers.size(),
                "<gray>Watched: <yellow>" + watchedCount,
                "<gray>Filter: <white>" + filterMode.displayName,
                "<gray>Sort: <white>" + sortMode.displayName));
    }

    private ItemStack createFilterButton(FilterMode mode, String label, Material icon) {
        boolean selected = (filterMode == mode);
        return createItem(
                selected ? Material.LIME_DYE : icon,
                (selected ? "<green>" : "<gray>") + label,
                selected ? "<green>Currently viewing" : "<yellow>Click to filter"
        );
    }

    private ItemStack createPlayerItem(PlayerProfile profile) {
        boolean isOnline = Bukkit.getPlayer(profile.getUuid()) != null;
        boolean isWatched = plugin.getWatchlistManager().isWatched(profile.getUuid());
        boolean hasPunishment = plugin.getPunishmentManager().hasActivePunishment(profile.getUuid());

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getUuid());
            meta.setOwningPlayer(player);
        } catch (Exception ignored) {}

        // Build name with status indicators
        StringBuilder nameBuilder = new StringBuilder();
        if (isWatched) nameBuilder.append("<yellow>★ ");
        if (isOnline) nameBuilder.append("<green>");
        else nameBuilder.append("<gray>");
        nameBuilder.append(profile.getUsername());
        if (hasPunishment) nameBuilder.append(" <red>⚠");

        meta.displayName(TextUtil.parse(nameBuilder.toString()));

        List<String> lore = new ArrayList<>();
        lore.add("<gray>UUID: <white>" + profile.getUuid().toString().substring(0, 8) + "...");
        lore.add("");

        // Status indicators
        lore.add(isOnline ? "<green>● Online" : "<red>● Offline");
        if (isWatched) {
            lore.add("<yellow>★ On Watchlist");
        }
        if (hasPunishment) {
            lore.add("<red>⚠ Has Active Punishment");
        }

        // Timestamps
        lore.add("");
        lore.add("<gray>First joined: <white>" + formatTimeAgo(profile.getFirstJoin()));
        lore.add("<gray>Last seen: <white>" + formatTimeAgo(profile.getLastJoin()));

        lore.add("");
        lore.add("<yellow>Left-click: <white>View details");
        lore.add(isWatched ?
                "<red>Right-click: <white>Remove from watchlist" :
                "<green>Right-click: <white>Add to watchlist");
        lore.add("<aqua>Shift-click: <white>Punish player");

        meta.lore(lore.stream().map(TextUtil::parse).toList());
        head.setItemMeta(meta);

        return head;
    }

    private void toggleWatchlist(PlayerProfile profile) {
        UUID uuid = profile.getUuid();
        boolean isWatched = plugin.getWatchlistManager().isWatched(uuid);

        if (isWatched) {
            plugin.getWatchlistManager().removeFromWatchlist(uuid);
            viewer.sendMessage(TextUtil.parse("<yellow>Removed <white>" + profile.getUsername() + "<yellow> from watchlist."));
        } else {
            plugin.getWatchlistManager().addToWatchlist(
                    uuid,
                    profile.getUsername(),
                    viewer.getUniqueId(),
                    viewer.getName(),
                    "Added via All Players GUI"
            );
            viewer.sendMessage(TextUtil.parse("<green>Added <white>" + profile.getUsername() + "<green> to watchlist."));
        }

        refresh();
    }

    private void showPlayerDetails(PlayerProfile profile) {
        viewer.sendMessage(TextUtil.parse(""));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse("<yellow><bold>" + profile.getUsername()));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse("<gray>UUID: <white>" + profile.getUuid()));
        viewer.sendMessage(TextUtil.parse("<gray>First Join: <white>" + formatTimeAgo(profile.getFirstJoin())));
        viewer.sendMessage(TextUtil.parse("<gray>Last Seen: <white>" + formatTimeAgo(profile.getLastJoin())));

        if (profile.getIpAddress() != null) {
            viewer.sendMessage(TextUtil.parse("<gray>Last IP: <white>" + profile.getIpAddress()));
        }
        if (profile.getLastServer() != null) {
            viewer.sendMessage(TextUtil.parse("<gray>Last Server: <white>" + profile.getLastServer()));
        }

        boolean isWatched = plugin.getWatchlistManager().isWatched(profile.getUuid());
        viewer.sendMessage(TextUtil.parse("<gray>Watchlist: " + (isWatched ? "<yellow>Yes" : "<gray>No")));

        boolean hasPunishment = plugin.getPunishmentManager().hasActivePunishment(profile.getUuid());
        viewer.sendMessage(TextUtil.parse("<gray>Active Punishment: " + (hasPunishment ? "<red>Yes" : "<green>No")));

        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
    }

    private void promptSearch() {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter player name or UUID to search:"));
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
                        } else {
                            searchQuery = input;
                        }

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            AllPlayersWatchlistGui gui = new AllPlayersWatchlistGui(plugin);
                            gui.searchQuery = searchQuery;
                            gui.filterMode = filterMode;
                            gui.sortMode = sortMode;
                            gui.loadPlayers();
                            plugin.getGuiManager().open(viewer, gui);
                        });

                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    private String formatTimeAgo(long timestamp) {
        if (timestamp <= 0) return "Unknown";

        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 30) {
            return (days / 30) + " month" + (days / 30 == 1 ? "" : "s") + " ago";
        } else if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return "Just now";
        }
    }

    @Override
    protected void addNavigationButtons() {
        super.addNavigationButtons();
        setItem(45, createBackButton(), () -> openGui(new MainMenuGui(plugin)));
    }

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }

    private enum FilterMode {
        ALL("All Players"),
        WATCHED("Watched Only"),
        ONLINE("Online Only"),
        OFFLINE("Offline Only"),
        PUNISHED("Has Punishment");

        final String displayName;

        FilterMode(String displayName) {
            this.displayName = displayName;
        }
    }

    private enum SortMode {
        LAST_SEEN("Last Seen"),
        FIRST_JOINED("First Joined"),
        NAME("Name"),
        WATCHED_FIRST("Watched First");

        final String displayName;

        SortMode(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class BulkActionsGui extends BaseGui {

        public BulkActionsGui(ModereX plugin) {
            super(plugin, "<red>Bulk Watchlist Actions", 3);
        }

        @Override
        protected void populate() {
            fillBorder(Material.RED_STAINED_GLASS_PANE);

            // Clear all watchlist
            setItem(11, createItem(Material.TNT, "<dark_red>Clear Entire Watchlist",
                    "<gray>Remove ALL players from watchlist",
                    "",
                    "<red>This cannot be undone!",
                    "",
                    "<yellow>Click to clear"), () -> {
                int count = plugin.getWatchlistManager().getWatchedPlayers().size();
                for (UUID uuid : new ArrayList<>(plugin.getWatchlistManager().getWatchedPlayers())) {
                    plugin.getWatchlistManager().removeFromWatchlist(uuid);
                }
                viewer.sendMessage(TextUtil.parse("<red>Cleared " + count + " players from watchlist."));
                close();
            });

            // Add all online to watchlist
            setItem(13, createItem(Material.SPYGLASS, "<yellow>Watch All Online",
                    "<gray>Add all currently online players",
                    "<gray>to the watchlist",
                    "",
                    "<yellow>Click to add"), () -> {
                int count = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!plugin.getWatchlistManager().isWatched(p.getUniqueId())) {
                        plugin.getWatchlistManager().addToWatchlist(
                                p.getUniqueId(),
                                p.getName(),
                                viewer.getUniqueId(),
                                viewer.getName(),
                                "Bulk add - all online"
                        );
                        count++;
                    }
                }
                viewer.sendMessage(TextUtil.parse("<green>Added " + count + " players to watchlist."));
                close();
            });

            // Export watchlist
            setItem(15, createItem(Material.WRITABLE_BOOK, "<aqua>Export Watchlist",
                    "<gray>Export watchlist to console",
                    "",
                    "<yellow>Click to export"), () -> {
                Set<UUID> watched = plugin.getWatchlistManager().getWatchedPlayers();
                plugin.getLogger().info("=== Watchlist Export ===");
                for (UUID uuid : watched) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    plugin.getLogger().info(uuid + " - " + (p.getName() != null ? p.getName() : "Unknown"));
                }
                plugin.getLogger().info("Total: " + watched.size() + " players");
                viewer.sendMessage(TextUtil.parse("<green>Watchlist exported to console."));
                close();
            });

            // Back button
            setItem(22, createBackButton(), () -> openGui(new AllPlayersWatchlistGui(plugin)));
        }
    }
}
