package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StaffSettingsGui extends BaseGui {

    private SettingsTab currentTab = SettingsTab.PERSONAL;
    private StaffSettings settings;

    public StaffSettingsGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Staff Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        // Load settings
        settings = plugin.getStaffSettingsManager().getSettings(viewer);

        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Tab buttons
        setItem(2, createTabButton(SettingsTab.PERSONAL, Material.PLAYER_HEAD), () -> {
            currentTab = SettingsTab.PERSONAL;
            refresh();
        });

        setItem(4, createTabButton(SettingsTab.ALERTS, Material.BELL), () -> {
            currentTab = SettingsTab.ALERTS;
            refresh();
        });

        setItem(6, createTabButton(SettingsTab.PLUGIN, Material.COMMAND_BLOCK), () -> {
            if (viewer.hasPermission("moderex.admin")) {
                currentTab = SettingsTab.PLUGIN;
                refresh();
            } else {
                viewer.sendMessage(TextUtil.parse("<red>You don't have permission to access plugin settings!"));
            }
        });

        // Render current tab content
        switch (currentTab) {
            case PERSONAL -> renderPersonalSettings();
            case ALERTS -> renderAlertSettings();
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

    private enum SettingsTab {
        PERSONAL("Personal Settings", "<gray>Your personal preferences"),
        ALERTS("Alert Settings", "<gray>Configure notifications"),
        PLUGIN("Plugin Settings", "<gray>Server-wide configuration");

        final String displayName;
        final String description;

        SettingsTab(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }
}
