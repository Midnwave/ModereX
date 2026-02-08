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
 * GUI for viewing a player's automod trigger history.
 */
public class AutomodHistoryGui extends PaginatedGui<ActivityLogEntry> {

    private final OfflinePlayer target;
    private List<ActivityLogEntry> cachedEntries = new ArrayList<>();

    public AutomodHistoryGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<light_purple>Automod History: <white>" + target.getName(), 6);
        this.target = target;
        loadEntries();
    }

    private void loadEntries() {
        // Load automod entries asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<ActivityLogEntry> entries = plugin.getActivityLogManager().getEntries(
                    target.getUniqueId(),
                    List.of(ActivityType.AUTOMOD_TRIGGER),
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
        setItem(49, createItem(Material.COMPARATOR,
                "<light_purple>Automod Triggers",
                "<gray>Player: <white>" + target.getName(),
                "<gray>Total: <white>" + cachedEntries.size() + " triggers",
                "",
                "<dark_gray>Shows when automod rules",
                "<dark_gray>were triggered by this player."));
    }

    private ItemStack createEntryItem(ActivityLogEntry entry) {
        String ruleName = entry.getExtra() != null ? entry.getExtra() : "Unknown Rule";
        String message = entry.getContent() != null ? entry.getContent() : "";
        String timestamp = TimeUtil.formatDateTime(entry.getTimestamp());
        String relativeTime = formatRelativeTime(entry.getTimestamp());

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Rule: <light_purple>" + ruleName);
        lore.add("<gray>Time: <white>" + relativeTime);
        lore.add("");

        // Message (truncated if too long)
        if (message.length() > 40) {
            lore.add("<gray>Message:");
            // Split into multiple lines
            for (int i = 0; i < message.length(); i += 40) {
                int end = Math.min(i + 40, message.length());
                lore.add("<white>" + message.substring(i, end));
            }
        } else {
            lore.add("<gray>Message: <white>" + message);
        }

        lore.add("");
        lore.add("<dark_gray>" + timestamp);

        return createItem(Material.COMPARATOR, "<light_purple>" + ruleName, lore.toArray(new String[0]));
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
