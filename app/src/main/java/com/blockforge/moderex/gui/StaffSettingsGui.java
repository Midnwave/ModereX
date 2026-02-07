package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.staff.StaffSettings.CommandAlertLevel;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Redesigned Staff Settings GUI - Single page, all settings visible.
 *
 * Layout (6 rows):
 * Row 0: Header with player info
 * Row 1: Punishment alerts (Ban, Kick, Mute, Warn, Pardon)
 * Row 2: Other alerts (Automod, Anticheat, Commands, Join/Leave, Nickname)
 * Row 3: Additional settings (Lag alerts, Watchlist, Staff Chat)
 * Row 4: UI Settings (Sounds, Chat alerts, Action bar, etc.)
 * Row 5: Navigation (Save, Close)
 */
public class StaffSettingsGui extends BaseGui {

    private StaffSettings settings;

    // Permission constants
    private static final String PERM_ALERTS_BAN = "moderex.alerts.ban";
    private static final String PERM_ALERTS_KICK = "moderex.alerts.kick";
    private static final String PERM_ALERTS_MUTE = "moderex.alerts.mute";
    private static final String PERM_ALERTS_WARN = "moderex.alerts.warn";
    private static final String PERM_ALERTS_PARDON = "moderex.alerts.pardon";
    private static final String PERM_ALERTS_AUTOMOD = "moderex.alerts.automod";
    private static final String PERM_ALERTS_ANTICHEAT = "moderex.alerts.anticheat";
    private static final String PERM_ALERTS_COMMANDS = "moderex.alerts.commands";
    private static final String PERM_ALERTS_JOINLEAVE = "moderex.alerts.joinleave";
    private static final String PERM_ALERTS_NICKNAME = "moderex.alerts.nickname";
    private static final String PERM_ALERTS_LAG = "moderex.alerts.lag";
    private static final String PERM_ALERTS_WATCHLIST = "moderex.alerts.watchlist";
    private static final String PERM_ALERTS_STAFFCHAT = "moderex.alerts.staffchat";
    private static final String PERM_ALERTS_ALL = "moderex.alerts.*";
    private static final String PERM_STAFF = "moderex.staff";

    public StaffSettingsGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Staff Alert Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        // Load settings
        settings = plugin.getStaffSettingsManager().getSettings(viewer);

        // Fill with dark background
        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

        // === ROW 0: Header ===
        setItem(4, createItem(Material.BELL, "<gradient:#a855f7:#ec4899>Alert Configuration</gradient>",
                "<gray>Configure your personal alert settings",
                "",
                "<white>Staff: <yellow>" + viewer.getName(),
                "",
                "<dark_gray>Settings sync to database automatically"));

        // Decorative corners
        setItem(0, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));
        setItem(8, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        // === ROW 1: Punishment Alerts ===
        // Section label
        setItem(9, createItem(Material.DIAMOND_SWORD, "<red>Punishment Alerts",
                "<gray>Configure punishment notifications"));

        // Ban alerts
        if (hasAlertPermission(PERM_ALERTS_BAN)) {
            setItem(10, createAlertLevelItem("Ban Alerts", settings.getBanAlerts(), Material.IRON_DOOR,
                    "Ban and IP-Ban notifications"), () -> {
                settings.setBanAlerts(settings.getBanAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(10, createNoPermissionItem("Ban Alerts"));
        }

        // Kick alerts
        if (hasAlertPermission(PERM_ALERTS_KICK)) {
            setItem(11, createAlertLevelItem("Kick Alerts", settings.getKickAlerts(), Material.LEATHER_BOOTS,
                    "Player kick notifications"), () -> {
                settings.setKickAlerts(settings.getKickAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(11, createNoPermissionItem("Kick Alerts"));
        }

        // Mute alerts
        if (hasAlertPermission(PERM_ALERTS_MUTE)) {
            setItem(12, createAlertLevelItem("Mute Alerts", settings.getMuteAlerts(), Material.BARRIER,
                    "Mute and IP-Mute notifications"), () -> {
                settings.setMuteAlerts(settings.getMuteAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(12, createNoPermissionItem("Mute Alerts"));
        }

        // Warn alerts
        if (hasAlertPermission(PERM_ALERTS_WARN)) {
            setItem(13, createAlertLevelItem("Warn Alerts", settings.getWarnAlerts(), Material.PAPER,
                    "Warning notifications"), () -> {
                settings.setWarnAlerts(settings.getWarnAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(13, createNoPermissionItem("Warn Alerts"));
        }

        // Pardon alerts (unbans, unmutes, unwarns)
        if (hasAlertPermission(PERM_ALERTS_PARDON)) {
            setItem(14, createAlertLevelItem("Pardon Alerts", settings.getPardonAlerts(), Material.EMERALD,
                    "Unbans, unmutes, unwarns"), () -> {
                settings.setPardonAlerts(settings.getPardonAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(14, createNoPermissionItem("Pardon Alerts"));
        }

        // === ROW 2: Other Alerts ===
        // Section label
        setItem(18, createItem(Material.COMPARATOR, "<gold>Detection Alerts",
                "<gray>Configure detection notifications"));

        // Automod alerts
        if (hasAlertPermission(PERM_ALERTS_AUTOMOD)) {
            setItem(19, createAlertLevelItem("Automod Alerts", settings.getAutomodAlerts(), Material.HOPPER,
                    "Chat filter & spam detection"), () -> {
                settings.setAutomodAlerts(settings.getAutomodAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(19, createNoPermissionItem("Automod Alerts"));
        }

        // Anticheat alerts (opens submenu)
        if (hasAlertPermission(PERM_ALERTS_ANTICHEAT)) {
            setItem(20, createItem(
                    settings.getAnticheatAlerts() == AlertLevel.OFF ? Material.IRON_SWORD : Material.DIAMOND_SWORD,
                    settings.getAnticheatAlerts().getColor() + "Anticheat Alerts",
                    "<gray>Configure anticheat notifications",
                    "",
                    "<white>Level: " + settings.getAnticheatAlerts().getColor() + settings.getAnticheatAlerts().getDisplayName(),
                    "<white>Min VL: <aqua>" + settings.getAnticheatMinVL(),
                    "",
                    "<yellow>Click to configure checks",
                    "<yellow>Shift+click to cycle level"), clickType -> {
                if (clickType.isShiftClick()) {
                    settings.setAnticheatAlerts(settings.getAnticheatAlerts().next());
                    saveAndRefresh();
                } else {
                    plugin.getGuiManager().open(viewer, new AnticheatRulesGui(plugin));
                }
            });
        } else {
            setItem(20, createNoPermissionItem("Anticheat Alerts"));
        }

        // Command alerts
        if (hasAlertPermission(PERM_ALERTS_COMMANDS)) {
            setItem(21, createCommandAlertItem("Command Alerts", settings.getCommandAlerts(), Material.COMMAND_BLOCK,
                    "Player command monitoring"), () -> {
                settings.setCommandAlerts(settings.getCommandAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(21, createNoPermissionItem("Command Alerts"));
        }

        // Join/Leave alerts (in-game only)
        if (hasAlertPermission(PERM_ALERTS_JOINLEAVE)) {
            setItem(22, createAlertLevelItem("Join/Leave Alerts", settings.getJoinLeaveAlerts(), Material.OAK_DOOR,
                    "Player join/leave alerts (in-game only)"), () -> {
                settings.setJoinLeaveAlerts(settings.getJoinLeaveAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(22, createNoPermissionItem("Join/Leave Alerts"));
        }

        // Nickname alerts
        if (hasAlertPermission(PERM_ALERTS_NICKNAME)) {
            setItem(23, createAlertLevelItem("Nickname Alerts", settings.getNicknameAlerts(), Material.NAME_TAG,
                    "Inappropriate nickname detection"), () -> {
                settings.setNicknameAlerts(settings.getNicknameAlerts().next());
                saveAndRefresh();
            });
        } else {
            setItem(23, createNoPermissionItem("Nickname Alerts"));
        }

        // === ROW 3: Additional Alerts ===
        // Section label
        setItem(27, createItem(Material.ENDER_EYE, "<aqua>Additional Alerts",
                "<gray>Other notification settings"));

        // Lag/Server status alerts
        if (hasAlertPermission(PERM_ALERTS_LAG)) {
            setItem(28, createToggleItem("Lag Alerts", settings.isLagAlerts(), Material.CLOCK,
                    "Low TPS & server lag warnings"), () -> {
                settings.setLagAlerts(!settings.isLagAlerts());
                saveAndRefresh();
            });
        } else {
            setItem(28, createNoPermissionItem("Lag Alerts"));
        }

        // Watchlist alerts
        if (hasAlertPermission(PERM_ALERTS_WATCHLIST)) {
            boolean wlEnabled = settings.isWatchlistJoinAlerts() || settings.isWatchlistActivityAlerts();
            setItem(29, createItem(
                    wlEnabled ? Material.LIME_DYE : Material.RED_DYE,
                    (wlEnabled ? "<green>" : "<red>") + "Watchlist Alerts",
                    "<gray>Alerts for watched players",
                    "",
                    "<white>Join: " + (settings.isWatchlistJoinAlerts() ? "<green>ON" : "<red>OFF"),
                    "<white>Quit: " + (settings.isWatchlistQuitAlerts() ? "<green>ON" : "<red>OFF"),
                    "<white>Activity: " + (settings.isWatchlistActivityAlerts() ? "<green>ON" : "<red>OFF"),
                    "",
                    "<yellow>Left-click: Toggle join",
                    "<yellow>Right-click: Toggle activity",
                    "<yellow>Shift+click: Toggle quit"), clickType -> {
                if (clickType.isShiftClick()) {
                    settings.setWatchlistQuitAlerts(!settings.isWatchlistQuitAlerts());
                } else if (clickType.isRightClick()) {
                    settings.setWatchlistActivityAlerts(!settings.isWatchlistActivityAlerts());
                } else {
                    settings.setWatchlistJoinAlerts(!settings.isWatchlistJoinAlerts());
                }
                saveAndRefresh();
            });
        } else {
            setItem(29, createNoPermissionItem("Watchlist Alerts"));
        }

        // Staff Chat
        if (hasAlertPermission(PERM_ALERTS_STAFFCHAT)) {
            setItem(30, createItem(
                    settings.isStaffChatEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                    (settings.isStaffChatEnabled() ? "<green>" : "<red>") + "Staff Chat",
                    "<gray>Receive staff chat messages",
                    "",
                    "<white>Enabled: " + (settings.isStaffChatEnabled() ? "<green>ON" : "<red>OFF"),
                    "<white>Sound: " + (settings.isStaffChatSound() ? "<green>ON" : "<red>OFF"),
                    "",
                    "<yellow>Left-click: Toggle chat",
                    "<yellow>Right-click: Toggle sound"), clickType -> {
                if (clickType.isRightClick()) {
                    settings.setStaffChatSound(!settings.isStaffChatSound());
                } else {
                    settings.setStaffChatEnabled(!settings.isStaffChatEnabled());
                }
                saveAndRefresh();
            });
        } else {
            setItem(30, createNoPermissionItem("Staff Chat"));
        }

        // === ROW 4: UI Settings ===
        // Section label
        setItem(36, createItem(Material.PAINTING, "<light_purple>Display Settings",
                "<gray>How alerts are shown to you"));

        // Sound toggle
        setItem(37, createToggleItem("Alert Sounds", settings.isSoundEnabled(), Material.NOTE_BLOCK,
                "Play sounds for alerts"), () -> {
            settings.setSoundEnabled(!settings.isSoundEnabled());
            saveAndRefresh();
        });

        // Chat alerts toggle
        setItem(38, createToggleItem("Chat Alerts", settings.isChatAlerts(), Material.PAPER,
                "Show alerts in chat"), () -> {
            settings.setChatAlerts(!settings.isChatAlerts());
            saveAndRefresh();
        });

        // Action bar toggle
        setItem(39, createToggleItem("Action Bar", settings.isActionBarAlerts(), Material.NAME_TAG,
                "Show alerts in action bar"), () -> {
            settings.setActionBarAlerts(!settings.isActionBarAlerts());
            saveAndRefresh();
        });

        // Boss bar toggle
        setItem(40, createToggleItem("Boss Bar", settings.isBossBarAlerts(), Material.DRAGON_HEAD,
                "Show alerts as boss bars"), () -> {
            settings.setBossBarAlerts(!settings.isBossBarAlerts());
            saveAndRefresh();
        });

        // Compact mode
        setItem(41, createToggleItem("Compact Mode", settings.isCompactMode(), Material.BOOK,
                "Shorter alert messages"), () -> {
            settings.setCompactMode(!settings.isCompactMode());
            saveAndRefresh();
        });

        // === ROW 5: Navigation ===
        setItem(45, createItem(Material.ARROW, "<red>Close", "<gray>Close this menu"), this::close);

        // Reset to defaults
        setItem(49, createItem(Material.TNT, "<gold>Reset Defaults",
                "<gray>Reset all settings to default",
                "",
                "<red>Shift+click to confirm"), clickType -> {
            if (clickType.isShiftClick()) {
                // Reset settings
                settings = new StaffSettings(viewer.getUniqueId());
                plugin.getStaffSettingsManager().saveSettings(settings);
                Msg.send(viewer, TextUtil.parse("<yellow>Settings reset to defaults!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f);
                refresh();
            }
        });

        // Save button
        setItem(53, createItem(Material.LIME_CONCRETE, "<green>Save & Close",
                "<gray>Save settings and close",
                "",
                "<dark_gray>Settings auto-save on change"), () -> {
            plugin.getStaffSettingsManager().saveSettings(settings);
            Msg.send(viewer, TextUtil.parse("<green>Settings saved!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            close();
        });
    }

    // ========== Helper Methods ==========

    private boolean hasAlertPermission(String permission) {
        // Use PermissionUtil for OP bypass (except moderex.webpanel)
        return PermissionUtil.hasPermission(viewer, PERM_STAFF) &&
               (PermissionUtil.hasPermission(viewer, PERM_ALERTS_ALL) || PermissionUtil.hasPermission(viewer, permission));
    }

    private void saveAndRefresh() {
        plugin.getStaffSettingsManager().saveSettings(settings);
        viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
        refresh();
    }

    private ItemStack createAlertLevelItem(String name, AlertLevel level, Material icon, String description) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + description);
        lore.add("");
        lore.add("<white>Level: " + level.getColor() + level.getDisplayName());
        lore.add("");
        lore.add("<dark_gray>Options:");
        lore.add(level == AlertLevel.EVERYONE ? "<green>▶ " : "<gray>  " + "Everyone");
        lore.add(level == AlertLevel.WATCHLIST_ONLY ? "<yellow>▶ " : "<gray>  " + "Watchlist Only");
        lore.add(level == AlertLevel.OFF ? "<red>▶ " : "<gray>  " + "Off");
        lore.add("");
        lore.add("<yellow>Click to cycle");

        Material displayIcon = switch (level) {
            case EVERYONE -> Material.LIME_DYE;
            case WATCHLIST_ONLY -> Material.YELLOW_DYE;
            case OFF -> Material.RED_DYE;
        };

        return createItem(displayIcon, level.getColor() + name, lore);
    }

    private ItemStack createCommandAlertItem(String name, CommandAlertLevel level, Material icon, String description) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + description);
        lore.add("");
        lore.add("<white>Level: " + level.getColor() + level.getDisplayName());
        lore.add("");
        lore.add("<dark_gray>Options:");
        lore.add(level == CommandAlertLevel.EVERYONE ? "<green>▶ " : "<gray>  " + "Everyone");
        lore.add(level == CommandAlertLevel.WATCHLIST_ONLY ? "<yellow>▶ " : "<gray>  " + "Watchlist Only");
        lore.add(level == CommandAlertLevel.BLACKLISTED_ONLY ? "<gold>▶ " : "<gray>  " + "Blacklisted Only");
        lore.add(level == CommandAlertLevel.OFF ? "<red>▶ " : "<gray>  " + "Off");
        lore.add("");
        lore.add("<yellow>Click to cycle");

        Material displayIcon = switch (level) {
            case EVERYONE -> Material.LIME_DYE;
            case WATCHLIST_ONLY -> Material.YELLOW_DYE;
            case BLACKLISTED_ONLY -> Material.ORANGE_DYE;
            case OFF -> Material.RED_DYE;
        };

        return createItem(displayIcon, level.getColor() + name, lore);
    }

    private ItemStack createToggleItem(String name, boolean enabled, Material icon, String description) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + description);
        lore.add("");
        lore.add(enabled ? "<green>✓ Enabled" : "<red>✗ Disabled");
        lore.add("");
        lore.add("<yellow>Click to toggle");

        return createItem(
                enabled ? Material.LIME_DYE : Material.RED_DYE,
                (enabled ? "<green>" : "<red>") + name,
                lore
        );
    }

    private ItemStack createNoPermissionItem(String name) {
        return createItem(Material.BARRIER, "<dark_gray>" + name,
                "<red>No Permission",
                "",
                "<gray>You don't have permission",
                "<gray>to configure this alert type");
    }
}
