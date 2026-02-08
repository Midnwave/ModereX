package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI for viewing a player's nickname change history.
 */
public class NickHistoryGui extends PaginatedGui<ActivityLogEntry> {

    private final OfflinePlayer target;
    private List<ActivityLogEntry> cachedEntries = new ArrayList<>();

    public NickHistoryGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<gold>Nickname History: <white>" + target.getName(), 6);
        this.target = target;
        loadEntries();
    }

    private void loadEntries() {
        // Load nickname entries asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<ActivityLogEntry> entries = plugin.getActivityLogManager().getEntries(
                    target.getUniqueId(),
                    List.of(ActivityType.NICKNAME_CHANGE),
                    0,
                    System.currentTimeMillis(),
                    1,
                    500 // Load up to 500 entries
            );
            this.cachedEntries = entries;

            // Refresh GUI on main thread if viewer is still viewing
            if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
                plugin.getServer().getScheduler().runTask(plugin, this::refresh);
            }
        });
    }

    @Override
    protected List<ActivityLogEntry> getItems() {
        return cachedEntries;
    }

    @Override
    protected void renderItem(int slot, ActivityLogEntry entry) {
        ItemStack item = createEntryItem(entry);
        setItem(slot, item, clickType -> {
            // Could add click functionality here if needed
        });
    }

    @Override
    protected void populate() {
        super.populate();

        // Back button in bottom row
        setItem(45, createBackButton(), this::close);

        // Info item
        setItem(49, createItem(Material.NAME_TAG,
                "<gold>Nickname History",
                "<gray>Player: <white>" + target.getName(),
                "<gray>Total: <white>" + cachedEntries.size() + " changes",
                "",
                "<dark_gray>Shows past nickname changes",
                "<dark_gray>for this player."));
    }

    private ItemStack createEntryItem(ActivityLogEntry entry) {
        String newNick = entry.getContent() != null ? entry.getContent() : "(cleared)";
        String oldNick = entry.getExtra() != null ? entry.getExtra() : "(none)";
        String timestamp = TimeUtil.formatDateTime(entry.getTimestamp());
        String relativeTime = formatRelativeTime(entry.getTimestamp());

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Time: <white>" + relativeTime);
        lore.add("");

        // New nickname (show with colors if applicable)
        lore.add("<gray>New nickname:");
        lore.add("<gold>" + newNick);
        lore.add("");

        // Old nickname
        lore.add("<gray>Previous nickname:");
        lore.add("<gold>" + oldNick);

        lore.add("");
        lore.add("<dark_gray>" + timestamp);

        // Display title - try to show the nick but keep it reasonable
        String displayTitle = newNick;
        if (displayTitle.length() > 32) {
            displayTitle = displayTitle.substring(0, 29) + "...";
        }

        return createItem(Material.NAME_TAG, "<gold>" + displayTitle, lore.toArray(new String[0]));
    }

    private String formatRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        double seconds = diff / 1000.0;
        if (seconds < 60) return String.format("%.0fs ago", seconds);
        double minutes = seconds / 60.0;
        if (minutes < 60) return String.format("%.0fm ago", minutes);
        double hours = minutes / 60.0;
        if (hours < 24) return String.format("%.1fh ago", hours);
        double days = hours / 24.0;
        if (days < 7) return String.format("%.1fd ago", days);
        double weeks = days / 7.0;
        return String.format("%.1fw ago", weeks);
    }
}
