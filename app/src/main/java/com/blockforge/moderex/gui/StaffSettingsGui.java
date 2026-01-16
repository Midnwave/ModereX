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

    // Layout constants for a centered 6-row GUI
    // Row 0: Border
    // Row 1: Tabs (centered)
    // Row 2-4: Content area (slots 19-25, 28-34, 37-43)
    // Row 5: Navigation (save/close)

    public StaffSettingsGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Staff Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        // Load settings
        settings = plugin.getStaffSettingsManager().getSettings(viewer);

        // Fill with dark background
        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

        // Tab row (row 1, centered) - slots 10-16
        setItem(10, createTabButton(SettingsTab.NOTIFICATIONS, Material.BELL), () -> {
            currentTab = SettingsTab.NOTIFICATIONS;
            refresh();
        });

        setItem(11, createTabButton(SettingsTab.ALERTS, Material.COMPARATOR), () -> {
            currentTab = SettingsTab.ALERTS;
            refresh();
        });

        setItem(12, createTabButton(SettingsTab.ANTICHEAT, Material.IRON_SWORD), () -> {
            currentTab = SettingsTab.ANTICHEAT;
            refresh();
        });

        setItem(13, createTabButton(SettingsTab.PERSONAL, Material.ARMOR_STAND), () -> {
            currentTab = SettingsTab.PERSONAL;
            refresh();
        });

        setItem(14, createTabButton(SettingsTab.VANISH, Material.POTION), () -> {
            currentTab = SettingsTab.VANISH;
            refresh();
        });

        setItem(16, createTabButton(SettingsTab.PLUGIN, Material.COMMAND_BLOCK), () -> {
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
            case VANISH -> renderVanishSettings();
            case PLUGIN -> renderPluginSettings();
        }

        // Bottom row navigation
        setItem(45, createItem(Material.ARROW, "<red>Close", "<gray>Close this menu"), this::close);

        setItem(49, createItem(Material.LIME_CONCRETE, "<green>Save Settings",
                "<gray>Save your current settings",
                "",
                "<yellow>Click to save"), () -> {
            plugin.getStaffSettingsManager().saveSettings(settings);
            viewer.sendMessage(TextUtil.parse("<green>Settings saved successfully!"));
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        });

        setItem(53, createItem(Material.BOOK, "<aqua>Help",
                "<gray>Hover over items to see",
                "<gray>what each setting does"));
    }

    private ItemStack createTabButton(SettingsTab tab, Material icon) {
        boolean selected = (currentTab == tab);

        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + tab.description);
        lore.add("");
        lore.add(selected ? "<green>▶ Currently viewing" : "<yellow>Click to view");

        if (tab == SettingsTab.PLUGIN && !viewer.hasPermission("moderex.admin")) {
            lore.add("<red>⚠ Requires admin permission");
        }

        Material displayIcon = selected ? Material.LIME_STAINED_GLASS_PANE : icon;
        String color = selected ? "<green>" : "<white>";

        return createItem(displayIcon, color + tab.displayName, lore);
    }

    // ========== Notification Settings Tab ==========

    private void renderNotificationSettings() {
        // Center content area: slots 20-24, 29-33, 38-42

        // Row 1 - Core notifications
        setItem(20, createJoinLeaveLevelButton("Join/Leave",
                settings.getJoinLeaveMessages(), Material.OAK_DOOR), () -> {
            settings.setJoinLeaveMessages(settings.getJoinLeaveMessages().next());
            refresh();
        });

        setItem(21, createToggle("Mod Actions",
                settings.isModerationActionsEnabled(),
                "See staff moderation actions",
                Material.IRON_AXE), () -> {
            settings.setModerationActionsEnabled(!settings.isModerationActionsEnabled());
            refresh();
        });

        setItem(22, createAlertLevelButton("Punishments",
                settings.getPunishmentAlerts(), Material.BARRIER), () -> {
            settings.setPunishmentAlerts(settings.getPunishmentAlerts().next());
            refresh();
        });

        setItem(23, createToggle("Staff Chat",
                settings.isStaffChatEnabled(),
                "Receive staff chat messages",
                Material.DIAMOND), () -> {
            settings.setStaffChatEnabled(!settings.isStaffChatEnabled());
            refresh();
        });

        setItem(24, createToggle("Chat Sound",
                settings.isStaffChatSound(),
                "Play sound on staff chat",
                Material.BELL), () -> {
            settings.setStaffChatSound(!settings.isStaffChatSound());
            refresh();
        });

        // Row 2 - Watchlist settings
        setItem(29, createToggle("WL Join Alert",
                settings.isWatchlistJoinAlerts(),
                "Alert when watched players join",
                Material.LIME_DYE), () -> {
            settings.setWatchlistJoinAlerts(!settings.isWatchlistJoinAlerts());
            refresh();
        });

        setItem(30, createToggle("WL Quit Alert",
                settings.isWatchlistQuitAlerts(),
                "Alert when watched players leave",
                Material.RED_DYE), () -> {
            settings.setWatchlistQuitAlerts(!settings.isWatchlistQuitAlerts());
            refresh();
        });

        setItem(31, createToggle("WL Activity",
                settings.isWatchlistActivityAlerts(),
                "Alert on watched player activity",
                Material.TRIPWIRE_HOOK), () -> {
            settings.setWatchlistActivityAlerts(!settings.isWatchlistActivityAlerts());
            refresh();
        });

        setItem(32, createAlertLevelButton("Commands",
                settings.getCommandAlerts(), Material.COMMAND_BLOCK), () -> {
            settings.setCommandAlerts(settings.getCommandAlerts().next());
            refresh();
        });

        setItem(33, createAlertLevelButton("Private Msgs",
                settings.getPrivateMessageAlerts(), Material.WRITABLE_BOOK), () -> {
            settings.setPrivateMessageAlerts(settings.getPrivateMessageAlerts().next());
            refresh();
        });
    }

    // ========== Alert Settings Tab ==========

    private void renderAlertSettings() {
        // Punishment alerts - Row 1
        setItem(19, createAlertLevelButton("Warn",
                settings.getWarnAlerts(), Material.PAPER), () -> {
            settings.setWarnAlerts(settings.getWarnAlerts().next());
            refresh();
        });

        setItem(20, createAlertLevelButton("Mute",
                settings.getMuteAlerts(), Material.BARRIER), () -> {
            settings.setMuteAlerts(settings.getMuteAlerts().next());
            refresh();
        });

        setItem(21, createAlertLevelButton("Ban",
                settings.getBanAlerts(), Material.IRON_DOOR), () -> {
            settings.setBanAlerts(settings.getBanAlerts().next());
            refresh();
        });

        setItem(22, createAlertLevelButton("Kick",
                settings.getKickAlerts(), Material.LEATHER_BOOTS), () -> {
            settings.setKickAlerts(settings.getKickAlerts().next());
            refresh();
        });

        setItem(23, createAlertLevelButton("Pardon",
                settings.getPardonAlerts(), Material.EMERALD), () -> {
            settings.setPardonAlerts(settings.getPardonAlerts().next());
            refresh();
        });

        // Automod alerts - Row 2
        setItem(29, createAlertLevelButton("Automod",
                settings.getAutomodAlerts(), Material.COMPARATOR), () -> {
            settings.setAutomodAlerts(settings.getAutomodAlerts().next());
            refresh();
        });

        setItem(30, createAlertLevelButton("Spam",
                settings.getSpamAlerts(), Material.REPEATER), () -> {
            settings.setSpamAlerts(settings.getSpamAlerts().next());
            refresh();
        });

        setItem(31, createAlertLevelButton("Filter",
                settings.getFilterAlerts(), Material.HOPPER), () -> {
            settings.setFilterAlerts(settings.getFilterAlerts().next());
            refresh();
        });

        // Anticheat alerts - Row 2 continued
        setItem(33, createAlertLevelButton("Anticheat",
                settings.getAnticheatAlerts(), Material.IRON_SWORD), () -> {
            settings.setAnticheatAlerts(settings.getAnticheatAlerts().next());
            refresh();
        });

        setItem(34, createItem(Material.EXPERIENCE_BOTTLE,
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

        // Show blacklisted commands - Row 3
        setItem(40, createToggle("Blacklist Cmds",
                settings.isShowBlacklistedCommands(),
                "Show blacklisted command attempts",
                Material.STRUCTURE_VOID), () -> {
            settings.setShowBlacklistedCommands(!settings.isShowBlacklistedCommands());
            refresh();
        });
    }

    // ========== Anticheat Settings Tab ==========

    private void renderAnticheatSettings() {
        // Global settings - Row 1
        setItem(20, createAlertLevelButton("AC Alerts",
                settings.getAnticheatAlerts(), Material.IRON_SWORD), () -> {
            settings.setAnticheatAlerts(settings.getAnticheatAlerts().next());
            refresh();
        });

        setItem(22, createItem(Material.EXPERIENCE_BOTTLE,
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

        setItem(24, createItem(Material.COMPARATOR, "<gold>Anticheat Rules",
                "<gray>Configure check rules,",
                "<gray>thresholds, and auto-punishments",
                "",
                "<yellow>Click to open"), () -> {
            plugin.getGuiManager().open(viewer, new AnticheatRulesGui(plugin));
        });

        // Per-anticheat toggles - Row 2 & 3 (centered)
        List<String> enabledACs = plugin.getAnticheatManager().getEnabledAnticheats();
        int[] slots = {29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
        int slotIndex = 0;

        for (String acName : enabledACs) {
            if (slotIndex >= slots.length) break;

            StaffSettings.AnticheatPreference pref = settings.getAnticheatPreference(acName);
            boolean enabled = pref.isEnabled();

            setItem(slots[slotIndex], createItem(
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

            slotIndex++;
        }

        if (enabledACs.isEmpty()) {
            setItem(31, createItem(Material.GRAY_DYE, "<gray>No Anticheats Detected",
                    "<gray>Install an anticheat plugin"));
        }
    }

    // ========== Personal Settings Tab ==========

    private void renderPersonalSettings() {
        // Display settings - Row 1
        setItem(20, createToggle("Compact Mode",
                settings.isCompactMode(),
                "Show alerts in compact format",
                Material.BOOK), () -> {
            settings.setCompactMode(!settings.isCompactMode());
            refresh();
        });

        setItem(21, createToggle("Alert Sounds",
                settings.isSoundEnabled(),
                "Play sounds for alerts",
                Material.NOTE_BLOCK), () -> {
            settings.setSoundEnabled(!settings.isSoundEnabled());
            refresh();
        });

        setItem(22, createToggle("Chat Alerts",
                settings.isChatAlerts(),
                "Show alerts in chat",
                Material.PAPER), () -> {
            settings.setChatAlerts(!settings.isChatAlerts());
            refresh();
        });

        setItem(23, createToggle("Action Bar",
                settings.isActionBarAlerts(),
                "Show alerts in action bar",
                Material.NAME_TAG), () -> {
            settings.setActionBarAlerts(!settings.isActionBarAlerts());
            refresh();
        });

        setItem(24, createToggle("Boss Bar",
                settings.isBossBarAlerts(),
                "Show alerts as boss bars",
                Material.DRAGON_HEAD), () -> {
            settings.setBossBarAlerts(!settings.isBossBarAlerts());
            refresh();
        });
    }

    // ========== Vanish Settings Tab ==========

    private void renderVanishSettings() {
        // Vanish settings - centered
        setItem(20, createToggle("Auto Vanish",
                settings.isAutoVanishOnJoin(),
                "Auto-vanish when joining server",
                Material.ENDER_EYE), () -> {
            settings.setAutoVanishOnJoin(!settings.isAutoVanishOnJoin());
            refresh();
        });

        setItem(22, createToggle("Night Vision",
                settings.isVanishNightVision(),
                "Get night vision while vanished",
                Material.GOLDEN_CARROT), () -> {
            settings.setVanishNightVision(!settings.isVanishNightVision());
            refresh();
        });

        setItem(24, createToggle("Show Particles",
                settings.isVanishShowSelf(),
                "Show particle effects to yourself",
                Material.BLAZE_POWDER), () -> {
            settings.setVanishShowSelf(!settings.isVanishShowSelf());
            refresh();
        });

        // Info
        setItem(31, createItem(Material.POTION, "<aqua>Vanish Info",
                "<gray>These settings control your",
                "<gray>vanish behavior when using",
                "<gray>/mx vanish or /v"));
    }

    // ========== Plugin Settings Tab ==========

    private void renderPluginSettings() {
        if (!viewer.hasPermission("moderex.admin")) {
            setItem(31, createItem(Material.BARRIER, "<red>Access Denied",
                    "<gray>You need admin permission"));
            return;
        }

        // Chat & Slowmode - Row 1
        boolean chatEnabled = plugin.getConfigManager().getSettings().isChatEnabled();
        setItem(20, createToggle("Global Chat",
                chatEnabled,
                "Enable/disable server chat",
                Material.OAK_SIGN), () -> {
            plugin.getConfigManager().getSettings().setChatEnabled(!chatEnabled);
            if (plugin.getWebPanelServer() != null) {
                plugin.getWebPanelServer().broadcastChatStatus();
            }
            refresh();
        });

        int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
        setItem(22, createItem(Material.CLOCK,
                "<gold>Slowmode: " + (slowmode > 0 ? slowmode + "s" : "Off"),
                "<gray>Chat cooldown between messages",
                "",
                "<yellow>Left-click: +5s",
                "<yellow>Right-click: -5s",
                "<yellow>Shift-click: Disable"), clickType -> {
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

        boolean debugMode = plugin.getConfigManager().getSettings().isDebugMode();
        setItem(24, createToggle("Debug Mode",
                debugMode,
                "Enable debug logging",
                Material.COMMAND_BLOCK), () -> {
            plugin.getConfigManager().getSettings().setDebugMode(!debugMode);
            refresh();
        });

        // Replay settings - Row 2
        setItem(29, createToggle("Replay System",
                plugin.getReplayManager().isEnabled(),
                "Enable replay recording",
                Material.JUKEBOX), () -> {
            plugin.getReplayManager().setEnabled(!plugin.getReplayManager().isEnabled());
            refresh();
        });

        setItem(30, createToggle("AC Recording",
                plugin.getReplayManager().isRecordOnAnticheatAlert(),
                "Record on anticheat alerts",
                Material.IRON_SWORD), () -> {
            plugin.getReplayManager().setRecordOnAnticheatAlert(!plugin.getReplayManager().isRecordOnAnticheatAlert());
            refresh();
        });

        setItem(31, createToggle("WL Recording",
                plugin.getReplayManager().isRecordWatchlistPlayers(),
                "Record watchlist players",
                Material.SPYGLASS), () -> {
            plugin.getReplayManager().setRecordWatchlistPlayers(!plugin.getReplayManager().isRecordWatchlistPlayers());
            refresh();
        });

        // Server info - Row 2 continued
        boolean webPanelEnabled = plugin.getConfigManager().getSettings().isWebPanelEnabled();
        int webPanelPort = plugin.getConfigManager().getSettings().getWebPanelPort();
        setItem(33, createItem(Material.END_PORTAL_FRAME,
                webPanelEnabled ? "<green>Web Panel: Active" : "<red>Web Panel: Off",
                "<gray>Port: <white>" + webPanelPort,
                "",
                "<gray>Requires restart to change"));

        // Reload button - Row 3
        setItem(40, createItem(Material.REDSTONE, "<yellow>Reload Config",
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

        Material displayIcon = switch (level) {
            case EVERYONE -> Material.LIME_DYE;
            case WATCHLIST_ONLY -> Material.YELLOW_DYE;
            case OFF -> Material.RED_DYE;
        };

        return createItem(displayIcon, level.getColor() + name, lore);
    }

    private ItemStack createJoinLeaveLevelButton(String name, JoinLeaveLevel level, Material icon) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + level.getDescription());
        lore.add("");
        lore.add("<white>Current: " + level.getColor() + level.getDisplayName());
        lore.add("");
        lore.add("<yellow>Click to cycle");

        Material displayIcon = switch (level) {
            case ALL -> Material.LIME_DYE;
            case STAFF_ONLY -> Material.YELLOW_DYE;
            case OFF -> Material.RED_DYE;
        };

        return createItem(displayIcon, level.getColor() + name, lore);
    }

    private enum SettingsTab {
        NOTIFICATIONS("Notifications", "Join/Leave and alert settings"),
        ALERTS("Alert Types", "Configure which alerts to see"),
        ANTICHEAT("Anticheat", "Anticheat alert settings"),
        PERSONAL("Display", "Display and UI preferences"),
        VANISH("Vanish", "Vanish behavior settings"),
        PLUGIN("Server", "Server-wide configuration");

        final String displayName;
        final String description;

        SettingsTab(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }
}
