package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.staff.StaffSettings.JoinLeaveLevel;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StaffSettingsGui extends BaseGui {

    private SettingsTab currentTab = SettingsTab.NOTIFICATIONS;
    private StaffSettings settings;

    public StaffSettingsGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Staff Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        // Load settings
        settings = plugin.getStaffSettingsManager().getSettings(viewer);

        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Tab buttons (5 tabs)
        setItem(1, createTabButton(SettingsTab.NOTIFICATIONS, Material.BELL), () -> {
            currentTab = SettingsTab.NOTIFICATIONS;
            refresh();
        });

        setItem(2, createTabButton(SettingsTab.ALERTS, Material.COMPARATOR), () -> {
            currentTab = SettingsTab.ALERTS;
            refresh();
        });

        setItem(3, createTabButton(SettingsTab.ANTICHEAT, Material.IRON_SWORD), () -> {
            currentTab = SettingsTab.ANTICHEAT;
            refresh();
        });

        setItem(5, createTabButton(SettingsTab.PERSONAL, Material.PLAYER_HEAD), () -> {
            currentTab = SettingsTab.PERSONAL;
            refresh();
        });

        setItem(7, createTabButton(SettingsTab.PLUGIN, Material.COMMAND_BLOCK), () -> {
            if (viewer.hasPermission("moderex.admin")) {
                currentTab = SettingsTab.PLUGIN;
                refresh();
            } else {
                viewer.sendMessage(TextUtil.parse("<red>You don't have permission to access plugin settings!"));
            }
        });

        // Render current tab content
        switch (currentTab) {
            case NOTIFICATIONS -> renderNotificationSettings();
            case ALERTS -> renderAlertSettings();
            case ANTICHEAT -> renderAnticheatSettings();
            case PERSONAL -> renderPersonalSettings();
            case PLUGIN -> renderPluginSettings();
        }

        // Save button
        setItem(49, createItem(Material.LIME_CONCRETE, "<green>Save Settings",
                "<gray>Save your current settings",
                "",
                "<yellow>Click to save"), () -> {
            plugin.getStaffSettingsManager().saveSettings(settings);
            viewer.sendMessage(TextUtil.parse("<green>Settings saved successfully!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        });

        // Close button
        setItem(53, createCloseButton(), this::close);
    }

    private ItemStack createTabButton(SettingsTab tab, Material icon) {
        boolean selected = (currentTab == tab);

        List<String> lore = new ArrayList<>();
        lore.add(tab.description);
        lore.add("");
        lore.add(selected ? "<green>Currently viewing" : "<yellow>Click to view");

        if (tab == SettingsTab.PLUGIN && !viewer.hasPermission("moderex.admin")) {
            lore.add("<red>Requires admin permission");
        }

        return createItem(
                selected ? Material.LIME_STAINED_GLASS_PANE : icon,
                (selected ? "<green>" : "<gray>") + tab.displayName,
                lore
        );
    }

    // ========== Notification Settings Tab ==========

    private void renderNotificationSettings() {
        // Join/Leave Messages
        setItem(10, createItem(Material.OAK_DOOR, "<aqua>Join/Leave Messages",
                "<gray>Control player connection messages"));

        setItem(19, createJoinLeaveLevelButton("Join/Leave Messages",
                settings.getJoinLeaveMessages(), Material.OAK_DOOR), () -> {
            settings.setJoinLeaveMessages(settings.getJoinLeaveMessages().next());
            refresh();
        });

        // Moderation Actions
        setItem(12, createItem(Material.IRON_AXE, "<gold>Moderation Actions",
                "<gray>See when staff take actions"));

        setItem(21, createToggle("Moderation Actions",
                settings.isModerationActionsEnabled(),
                "See staff moderation actions",
                Material.IRON_AXE), () -> {
            settings.setModerationActionsEnabled(!settings.isModerationActionsEnabled());
            refresh();
        });

        // Punishment Notifications
        setItem(14, createItem(Material.BARRIER, "<red>Punishment Alerts",
                "<gray>Get notified about punishments"));

        setItem(23, createAlertLevelButton("Punishment Alerts",
                settings.getPunishmentAlerts(), Material.BARRIER), () -> {
            settings.setPunishmentAlerts(settings.getPunishmentAlerts().next());
            refresh();
        });

        // Staff Chat
        setItem(16, createItem(Material.DIAMOND, "<light_purple>Staff Chat",
                "<gray>Staff communication settings"));

        setItem(25, createToggle("Staff Chat",
                settings.isStaffChatEnabled(),
                "Receive staff chat messages",
                Material.DIAMOND), () -> {
            settings.setStaffChatEnabled(!settings.isStaffChatEnabled());
            refresh();
        });

        // Watchlist Join Alerts
        setItem(28, createToggle("Watchlist Join",
                settings.isWatchlistJoinAlerts(),
                "Alert when watched players join",
                Material.SPYGLASS), () -> {
            settings.setWatchlistJoinAlerts(!settings.isWatchlistJoinAlerts());
            refresh();
        });

        // Watchlist Quit Alerts
        setItem(29, createToggle("Watchlist Quit",
                settings.isWatchlistQuitAlerts(),
                "Alert when watched players leave",
                Material.ENDER_EYE), () -> {
            settings.setWatchlistQuitAlerts(!settings.isWatchlistQuitAlerts());
            refresh();
        });

        // Watchlist Activity Alerts
        setItem(30, createToggle("Watchlist Activity",
                settings.isWatchlistActivityAlerts(),
                "Alert on watched player activity",
                Material.TRIPWIRE_HOOK), () -> {
            settings.setWatchlistActivityAlerts(!settings.isWatchlistActivityAlerts());
            refresh();
        });

        // Staff Chat Sound
        setItem(34, createToggle("Chat Sound",
                settings.isStaffChatSound(),
                "Play sound on staff chat",
                Material.BELL), () -> {
            settings.setStaffChatSound(!settings.isStaffChatSound());
            refresh();
        });
    }

    // ========== Anticheat Settings Tab ==========

    private void renderAnticheatSettings() {
        // Global Anticheat Toggle
        setItem(10, createItem(Material.IRON_SWORD, "<red>Anticheat Alerts",
                "<gray>Global anticheat notification settings"));

        setItem(19, createAlertLevelButton("Anticheat Alerts",
                settings.getAnticheatAlerts(), Material.IRON_SWORD), () -> {
            settings.setAnticheatAlerts(settings.getAnticheatAlerts().next());
            refresh();
        });

        // Minimum VL
        setItem(20, createItem(Material.EXPERIENCE_BOTTLE,
                "<yellow>Min VL: <white>" + settings.getAnticheatMinVL(),
                "<gray>Minimum violation level to show",
                "",
                "<yellow>Left-click: +5",
                "<yellow>Right-click: -5",
                "<yellow>Shift-click: Reset to 10"), clickType -> {
            if (clickType.isShiftClick()) {
                settings.setAnticheatMinVL(10);
            } else if (clickType.isLeftClick()) {
                settings.setAnticheatMinVL(Math.min(100, settings.getAnticheatMinVL() + 5));
            } else if (clickType.isRightClick()) {
                settings.setAnticheatMinVL(Math.max(0, settings.getAnticheatMinVL() - 5));
            }
            refresh();
        });

        // Per-anticheat settings
        List<String> enabledACs = plugin.getAnticheatManager().getEnabledAnticheats();
        int slot = 28;

        for (String acName : enabledACs) {
            if (slot > 34) break;

            StaffSettings.AnticheatPreference pref = settings.getAnticheatPreference(acName);
            boolean enabled = pref.isEnabled();

            setItem(slot, createItem(
                    enabled ? Material.LIME_DYE : Material.RED_DYE,
                    (enabled ? "<green>" : "<red>") + acName,
                    "<gray>Toggle alerts from " + acName,
                    "",
                    enabled ? "<green>✓ Enabled" : "<red>✗ Disabled",
                    "",
                    "<yellow>Click to toggle"
            ), () -> {
                pref.setEnabled(!pref.isEnabled());
                refresh();
            });

            slot++;
        }

        if (enabledACs.isEmpty()) {
            setItem(31, createItem(Material.GRAY_DYE, "<gray>No Anticheats",
                    "<gray>No anticheat plugins detected"));
        }

        // Info
        setItem(22, createItem(Material.BOOK, "<aqua>Anticheat Info",
                "<gray>Configure per-anticheat settings",
                "",
                "<gray>Detected: <white>" + enabledACs.size(),
                "<gray>Check the automod GUI for",
                "<gray>per-check configurations"));
    }

    // ========== Personal Settings Tab ==========

    private void renderPersonalSettings() {
        // Staff Chat Toggle
        setItem(19, createToggle("Staff Chat",
                settings.isStaffChatEnabled(),
                "Receive and send staff chat messages",
                Material.DIAMOND), () -> {
            settings.setStaffChatEnabled(!settings.isStaffChatEnabled());
            refresh();
        });

        // Staff Chat Sound
        setItem(20, createToggle("Staff Chat Sound",
                settings.isStaffChatSound(),
                "Play sound on staff chat messages",
                Material.BELL), () -> {
            settings.setStaffChatSound(!settings.isStaffChatSound());
            refresh();
        });

        // Sound Enabled
        setItem(21, createToggle("Alert Sounds",
                settings.isSoundEnabled(),
                "Play sounds for alerts",
                Material.NOTE_BLOCK), () -> {
            settings.setSoundEnabled(!settings.isSoundEnabled());
            refresh();
        });

        // Compact Mode
        setItem(22, createToggle("Compact Mode",
                settings.isCompactMode(),
                "Show alerts in compact format",
                Material.BOOK), () -> {
            settings.setCompactMode(!settings.isCompactMode());
            refresh();
        });

        // Chat Alerts
        setItem(28, createToggle("Chat Alerts",
                settings.isChatAlerts(),
                "Show alerts in chat",
                Material.PAPER), () -> {
            settings.setChatAlerts(!settings.isChatAlerts());
            refresh();
        });

        // Action Bar Alerts
        setItem(29, createToggle("Action Bar Alerts",
                settings.isActionBarAlerts(),
                "Show quick alerts in action bar",
                Material.NAME_TAG), () -> {
            settings.setActionBarAlerts(!settings.isActionBarAlerts());
            refresh();
        });

        // Boss Bar Alerts
        setItem(30, createToggle("Boss Bar Alerts",
                settings.isBossBarAlerts(),
                "Show important alerts as boss bars",
                Material.DRAGON_HEAD), () -> {
            settings.setBossBarAlerts(!settings.isBossBarAlerts());
            refresh();
        });

        // Vanish Settings Header
        setItem(23, createItem(Material.POTION, "<aqua>Vanish Settings",
                "<gray>Configure vanish behavior"));

        // Auto Vanish on Join
        setItem(32, createToggle("Auto Vanish",
                settings.isAutoVanishOnJoin(),
                "Automatically vanish when joining",
                Material.ENDER_EYE), () -> {
            settings.setAutoVanishOnJoin(!settings.isAutoVanishOnJoin());
            refresh();
        });

        // Vanish Night Vision
        setItem(33, createToggle("Vanish Night Vision",
                settings.isVanishNightVision(),
                "Apply night vision while vanished",
                Material.GOLDEN_CARROT), () -> {
            settings.setVanishNightVision(!settings.isVanishNightVision());
            refresh();
        });

        // Vanish Show Self
        setItem(34, createToggle("Show Self in Vanish",
                settings.isVanishShowSelf(),
                "Show particle effects to yourself",
                Material.BLAZE_POWDER), () -> {
            settings.setVanishShowSelf(!settings.isVanishShowSelf());
            refresh();
        });
    }

    // ========== Alert Settings Tab ==========

    private void renderAlertSettings() {
        // Punishment Alerts Section
        setItem(10, createItem(Material.BARRIER, "<red>Punishment Alerts",
                "<gray>Configure punishment notifications"));

        setItem(19, createAlertLevelButton("Warn Alerts",
                settings.getWarnAlerts(), Material.BOOK), () -> {
            settings.setWarnAlerts(settings.getWarnAlerts().next());
            refresh();
        });

        setItem(20, createAlertLevelButton("Mute Alerts",
                settings.getMuteAlerts(), Material.PAPER), () -> {
            settings.setMuteAlerts(settings.getMuteAlerts().next());
            refresh();
        });

        setItem(21, createAlertLevelButton("Ban Alerts",
                settings.getBanAlerts(), Material.BARRIER), () -> {
            settings.setBanAlerts(settings.getBanAlerts().next());
            refresh();
        });

        setItem(22, createAlertLevelButton("Kick Alerts",
                settings.getKickAlerts(), Material.LEATHER_BOOTS), () -> {
            settings.setKickAlerts(settings.getKickAlerts().next());
            refresh();
        });

        setItem(23, createAlertLevelButton("Pardon Alerts",
                settings.getPardonAlerts(), Material.EMERALD), () -> {
            settings.setPardonAlerts(settings.getPardonAlerts().next());
            refresh();
        });

        // Automod Alerts Section
        setItem(12, createItem(Material.COMPARATOR, "<gold>Automod Alerts",
                "<gray>Configure automod notifications"));

        setItem(28, createAlertLevelButton("Automod Alerts",
                settings.getAutomodAlerts(), Material.COMPARATOR), () -> {
            settings.setAutomodAlerts(settings.getAutomodAlerts().next());
            refresh();
        });

        setItem(29, createAlertLevelButton("Spam Alerts",
                settings.getSpamAlerts(), Material.REPEATER), () -> {
            settings.setSpamAlerts(settings.getSpamAlerts().next());
            refresh();
        });

        setItem(30, createAlertLevelButton("Filter Alerts",
                settings.getFilterAlerts(), Material.HOPPER), () -> {
            settings.setFilterAlerts(settings.getFilterAlerts().next());
            refresh();
        });

        // Anticheat Alerts Section
        setItem(14, createItem(Material.IRON_SWORD, "<aqua>Anticheat Alerts",
                "<gray>Configure anticheat notifications"));

        setItem(32, createAlertLevelButton("Anticheat Alerts",
                settings.getAnticheatAlerts(), Material.IRON_SWORD), () -> {
            settings.setAnticheatAlerts(settings.getAnticheatAlerts().next());
            refresh();
        });

        setItem(33, createItem(Material.EXPERIENCE_BOTTLE,
                "<yellow>Min VL: <white>" + settings.getAnticheatMinVL(),
                "<gray>Minimum violation level to show",
                "",
                "<yellow>Left-click: +5",
                "<yellow>Right-click: -5",
                "<yellow>Shift-click: Reset to 10"), clickType -> {
            if (clickType.isShiftClick()) {
                settings.setAnticheatMinVL(10);
            } else if (clickType.isLeftClick()) {
                settings.setAnticheatMinVL(Math.min(100, settings.getAnticheatMinVL() + 5));
            } else if (clickType.isRightClick()) {
                settings.setAnticheatMinVL(Math.max(0, settings.getAnticheatMinVL() - 5));
            }
            refresh();
        });

        // Watchlist Alerts Section
        setItem(16, createItem(Material.SPYGLASS, "<light_purple>Watchlist Alerts",
                "<gray>Configure watchlist notifications"));

        setItem(37, createToggle("Join Alerts",
                settings.isWatchlistJoinAlerts(),
                "Alert when watched players join",
                Material.OAK_DOOR), () -> {
            settings.setWatchlistJoinAlerts(!settings.isWatchlistJoinAlerts());
            refresh();
        });

        setItem(38, createToggle("Quit Alerts",
                settings.isWatchlistQuitAlerts(),
                "Alert when watched players leave",
                Material.IRON_DOOR), () -> {
            settings.setWatchlistQuitAlerts(!settings.isWatchlistQuitAlerts());
            refresh();
        });

        setItem(39, createToggle("Activity Alerts",
                settings.isWatchlistActivityAlerts(),
                "Alert on watched player activity",
                Material.TRIPWIRE_HOOK), () -> {
            settings.setWatchlistActivityAlerts(!settings.isWatchlistActivityAlerts());
            refresh();
        });

        // Command & PM Alerts
        setItem(41, createAlertLevelButton("Command Alerts",
                settings.getCommandAlerts(), Material.COMMAND_BLOCK), () -> {
            settings.setCommandAlerts(settings.getCommandAlerts().next());
            refresh();
        });

        setItem(42, createAlertLevelButton("PM Alerts",
                settings.getPrivateMessageAlerts(), Material.WRITABLE_BOOK), () -> {
            settings.setPrivateMessageAlerts(settings.getPrivateMessageAlerts().next());
            refresh();
        });

        setItem(43, createToggle("Blacklist Cmds",
                settings.isShowBlacklistedCommands(),
                "Show blacklisted command attempts",
                Material.STRUCTURE_VOID), () -> {
            settings.setShowBlacklistedCommands(!settings.isShowBlacklistedCommands());
            refresh();
        });
    }

    // ========== Plugin Settings Tab ==========

    private void renderPluginSettings() {
        if (!viewer.hasPermission("moderex.admin")) {
            setItem(22, createItem(Material.BARRIER, "<red>Access Denied",
                    "<gray>You need admin permission to view this"));
            return;
        }

        // Chat Control
        boolean chatEnabled = plugin.getConfigManager().getSettings().isChatEnabled();
        setItem(19, createToggle("Global Chat",
                chatEnabled,
                "Enable/disable server chat",
                Material.OAK_SIGN), () -> {
            plugin.getConfigManager().getSettings().setChatEnabled(!chatEnabled);
            if (plugin.getWebPanelServer() != null) {
                plugin.getWebPanelServer().broadcastChatStatus();
            }
            refresh();
        });

        // Slowmode
        int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
        setItem(20, createItem(Material.CLOCK,
                "<gold>Slowmode: " + (slowmode > 0 ? slowmode + "s" : "Off"),
                "<gray>Chat cooldown between messages",
                "",
                "<yellow>Left-click: +5s",
                "<yellow>Right-click: -5s",
                "<yellow>Shift-click: Reset"), clickType -> {
            int current = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
            if (clickType.isShiftClick()) {
                plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(0);
            } else if (clickType.isLeftClick()) {
                plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(Math.min(300, current + 5));
            } else if (clickType.isRightClick()) {
                plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(Math.max(0, current - 5));
            }
            if (plugin.getWebPanelServer() != null) {
                plugin.getWebPanelServer().broadcastChatStatus();
            }
            refresh();
        });

        // Debug Mode
        boolean debugMode = plugin.getConfigManager().getSettings().isDebugMode();
        setItem(21, createToggle("Debug Mode",
                debugMode,
                "Enable debug logging",
                Material.COMMAND_BLOCK), () -> {
            plugin.getConfigManager().getSettings().setDebugMode(!debugMode);
            refresh();
        });

        // Replay Settings
        setItem(28, createToggle("Replay System",
                plugin.getReplayManager().isEnabled(),
                "Enable replay recording system",
                Material.JUKEBOX), () -> {
            plugin.getReplayManager().setEnabled(!plugin.getReplayManager().isEnabled());
            refresh();
        });

        setItem(29, createToggle("AC Recording",
                plugin.getReplayManager().isRecordOnAnticheatAlert(),
                "Record on anticheat alerts",
                Material.IRON_SWORD), () -> {
            plugin.getReplayManager().setRecordOnAnticheatAlert(!plugin.getReplayManager().isRecordOnAnticheatAlert());
            refresh();
        });

        setItem(30, createToggle("Watchlist Record",
                plugin.getReplayManager().isRecordWatchlistPlayers(),
                "Auto-record watchlist players",
                Material.SPYGLASS), () -> {
            plugin.getReplayManager().setRecordWatchlistPlayers(!plugin.getReplayManager().isRecordWatchlistPlayers());
            refresh();
        });

        // Web Panel Status
        boolean webPanelEnabled = plugin.getConfigManager().getSettings().isWebPanelEnabled();
        int webPanelPort = plugin.getConfigManager().getSettings().getWebPanelPort();
        setItem(32, createItem(Material.END_PORTAL_FRAME,
                webPanelEnabled ? "<green>Web Panel: Active" : "<red>Web Panel: Disabled",
                "<gray>Port: <white>" + webPanelPort,
                "",
                "<gray>Requires restart to change"));

        // Proxy Mode
        boolean proxyEnabled = plugin.getConfigManager().getSettings().isProxyEnabled();
        setItem(33, createItem(Material.ENDER_PEARL,
                proxyEnabled ? "<green>Proxy Mode: Active" : "<gray>Proxy Mode: Off",
                "<gray>BungeeCord/Velocity support",
                "",
                "<gray>Requires restart to change"));

        // Reload Button
        setItem(43, createItem(Material.REDSTONE, "<yellow>Reload Config",
                "<gray>Reload configuration files",
                "",
                "<yellow>Click to reload"), () -> {
            viewer.sendMessage(TextUtil.parse("<yellow>Reloading ModereX..."));
            plugin.reload();
            viewer.sendMessage(TextUtil.parse("<green>ModereX reloaded!"));
            refresh();
        });
    }

    // ========== Helper Methods ==========

    private ItemStack createToggle(String name, boolean enabled, String description, Material icon) {
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

    private ItemStack createAlertLevelButton(String name, AlertLevel level, Material icon) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + level.getDescription());
        lore.add("");
        lore.add("<white>Current: " + level.getColor() + level.getDisplayName());
        lore.add("");
        lore.add("<yellow>Click to cycle");

        return createItem(icon, level.getColor() + name, lore);
    }

    private ItemStack createJoinLeaveLevelButton(String name, JoinLeaveLevel level, Material icon) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + level.getDescription());
        lore.add("");
        lore.add("<white>Current: " + level.getColor() + level.getDisplayName());
        lore.add("");
        lore.add("<yellow>Click to cycle");

        return createItem(icon, level.getColor() + name, lore);
    }

    private enum SettingsTab {
        NOTIFICATIONS("Notifications", "<gray>Join/Leave and alert settings"),
        ALERTS("Alert Types", "<gray>Configure alert types"),
        ANTICHEAT("Anticheat", "<gray>Per-check settings"),
        PERSONAL("Personal", "<gray>Your preferences"),
        PLUGIN("Plugin", "<gray>Server-wide config");

        final String displayName;
        final String description;

        SettingsTab(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }
}
