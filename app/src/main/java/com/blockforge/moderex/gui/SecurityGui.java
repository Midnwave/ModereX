package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;

/**
 * In-game security settings GUI for ModereX staff.
 * Shows security status, trusted devices, IP reputation, raid protection settings.
 */
public class SecurityGui extends BaseGui {

    public SecurityGui(ModereX plugin) {
        super(plugin, "<gradient:#ef4444:#f97316>Security Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);

        // Row 2: Security Overview
        // Slot 10: Raid Protection status
        boolean raidEnabled = plugin.getConfigManager().getSettings().isRaidProtectionEnabled();
        setItem(10, createItem(
                raidEnabled ? Material.SHIELD : Material.WOODEN_SWORD,
                raidEnabled ? "<green>Raid Protection: ON" : "<red>Raid Protection: OFF",
                "<gray>Protects against join floods",
                "<gray>and bot attacks.",
                "",
                raidEnabled ? "<yellow>Click to disable" : "<yellow>Click to enable"
        ), () -> {
            var settings = plugin.getConfigManager().getSettings();
            boolean current = settings.isRaidProtectionEnabled();
            settings.setRaidProtectionEnabled(!current);
            plugin.getConfigManager().saveConfig();
            refresh();
        });

        // Slot 12: Proxy/VPN Detection
        boolean proxyEnabled = plugin.getConfigManager().getSettings().isProxyEnabled();
        setItem(12, createItem(
                Material.COMPASS,
                "<gold>Proxy/VPN Detection",
                "<gray>Screens new player IPs",
                "<gray>for VPNs and proxies.",
                "",
                proxyEnabled ? "<green>Active" : "<red>Not configured"
        ));

        // Slot 14: Bot Detection
        setItem(14, createItem(
                Material.IRON_GOLEM_SPAWN_EGG,
                "<aqua>Bot Detection",
                "<gray>Automatically detects and",
                "<gray>blocks bot join attempts.",
                "",
                "<green>Active"
        ));

        // Slot 16: Web Panel Security
        setItem(16, createItem(
                Material.IRON_DOOR,
                "<light_purple>Web Panel Auth",
                "<gray>Manages web panel tokens",
                "<gray>and trusted devices.",
                "",
                "<yellow>Click to manage"
        ), () -> {
            openGui(new WebSecurityGui(plugin));
        });

        // Row 3: Active Threat Info
        setItem(19, createItem(
                Material.PAPER,
                "<white>Active Sessions",
                "<gray>View connected web panel",
                "<gray>sessions and devices."
        ));

        // Slot 21: Gateway Security
        boolean gatewayConnected = plugin.getGatewayClient() != null && plugin.getGatewayClient().isConnected();
        setItem(21, createItem(
                gatewayConnected ? Material.END_CRYSTAL : Material.GRAY_DYE,
                gatewayConnected ? "<green>Gateway: Connected" : "<red>Gateway: Disconnected",
                "<gray>Secure tunnel between",
                "<gray>server and web panel.",
                "",
                gatewayConnected ? "<green>TLS encrypted" : "<yellow>Enable in config"
        ));

        // Slot 23: Permission Overview
        setItem(23, createItem(
                Material.WRITTEN_BOOK,
                "<yellow>Permission Nodes",
                "<gray>Quick reference for",
                "<gray>all ModereX permissions.",
                "",
                "<yellow>Click to view"
        ), () -> {
            openGui(new PermissionListGui(plugin));
        });

        // Slot 25: Audit Log
        setItem(25, createItem(
                Material.WRITABLE_BOOK,
                "<gold>Audit Log",
                "<gray>View recent staff actions",
                "<gray>and configuration changes.",
                "",
                "<yellow>Click to view"
        ));

        // Row 4: Quick Actions
        // Slot 29: Toggle web panel
        boolean webPanelEnabled = plugin.getConfigManager().getSettings().isWebPanelEnabled();
        setItem(29, createItem(
                Material.REDSTONE_TORCH,
                webPanelEnabled ? "<green>Web Panel: Enabled" : "<red>Web Panel: Disabled",
                "<gray>Toggle the embedded web panel",
                "<gray>on or off.",
                "",
                webPanelEnabled ? "<yellow>Click to disable" : "<yellow>Click to enable"
        ), () -> {
            var settings = plugin.getConfigManager().getSettings();
            boolean current = settings.isWebPanelEnabled();
            settings.setWebPanelEnabled(!current);
            plugin.getConfigManager().saveConfig();
            if (viewer != null) {
                if (!current) {
                    Msg.send(viewer, TextUtil.parse("<green>Web panel enabled. Restart or reload to apply."));
                } else {
                    Msg.send(viewer, TextUtil.parse("<red>Web panel disabled. Restart or reload to apply."));
                }
            }
            refresh();
        });

        // Slot 31: Revoke player's web tokens
        setItem(31, createItem(
                Material.NAME_TAG,
                "<aqua>Revoke Tokens",
                "<gray>Revoke a player's web panel",
                "<gray>authentication token.",
                "",
                "<yellow>Click to enter player name"
        ), () -> {
            if (viewer != null) {
                viewer.closeInventory();
                promptInput("<yellow>Enter the player name whose token to revoke (or 'cancel'):", input -> {
                    if (!"cancel".equalsIgnoreCase(input.trim())) {
                        var target = plugin.getServer().getOfflinePlayer(input.trim());
                        if (target.hasPlayedBefore() || target.isOnline()) {
                            boolean revoked = plugin.getWebAuthManager().revokePermanentToken(target.getUniqueId());
                            plugin.getWebAuthManager().invalidateAllSessions(target.getUniqueId());
                            if (revoked) {
                                Msg.send(viewer, TextUtil.parse("<red>Revoked token and sessions for " + input.trim() + "."));
                            } else {
                                Msg.send(viewer, TextUtil.parse("<yellow>No token found for " + input.trim() + ", but sessions were invalidated."));
                            }
                        } else {
                            Msg.send(viewer, TextUtil.parse("<red>Player not found: " + input.trim()));
                        }
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getGuiManager().open(viewer, new SecurityGui(plugin));
                    });
                });
            }
        });

        // Slot 33: Force disconnect all web sessions
        setItem(33, createItem(
                Material.ENDER_PEARL,
                "<light_purple>Force Disconnect All",
                "<gray>Disconnect all active",
                "<gray>web panel sessions.",
                "",
                "<yellow>Click to disconnect"
        ), () -> {
            if (viewer != null) {
                for (var onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    plugin.getWebAuthManager().invalidateAllSessions(onlinePlayer.getUniqueId());
                    plugin.getWebAuthManager().disconnectPlayerSessions(onlinePlayer.getUniqueId());
                }
                Msg.send(viewer, TextUtil.parse("<red>All active web panel sessions disconnected."));
                refresh();
            }
        });

        // Bottom row: navigation
        setItem(49, createBackButton(), () -> {
            openGui(new MainMenuGui(plugin));
        });

        fillEmpty(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Sub-GUI for web panel token/device management.
     */
    static class WebSecurityGui extends BaseGui {

        public WebSecurityGui(ModereX plugin) {
            super(plugin, "<gradient:#ef4444:#f97316>Web Panel Security</gradient>", 4);
        }

        @Override
        protected void populate() {
            fillBorder(Material.RED_STAINED_GLASS_PANE);

            setItem(10, createItem(
                    Material.IRON_DOOR,
                    "<gold>Session Management",
                    "<gray>Active temporary sessions",
                    "<gray>and permanent tokens."
            ));

            setItem(12, createItem(
                    Material.CLOCK,
                    "<yellow>Session Timeout",
                    "<gray>Temporary sessions expire",
                    "<gray>after 30 minutes."
            ));

            setItem(14, createItem(
                    Material.GOLDEN_APPLE,
                    "<green>Token Duration",
                    "<gray>Permanent tokens last 90 days.",
                    "<gray>Generated via /mx link."
            ));

            setItem(16, createItem(
                    Material.TRIPWIRE_HOOK,
                    "<red>Rate Limiting",
                    "<gray>5 failed attempts = 15 min lockout.",
                    "<gray>CAPTCHA after 3 attempts."
            ));

            // Back button
            setItem(31, createItem(Material.ARROW, "<yellow>Go Back", "<gray>Return to security"), () -> {
                openGui(new SecurityGui(plugin));
            });

            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);
        }
    }

    /**
     * Sub-GUI listing all permission nodes.
     */
    static class PermissionListGui extends BaseGui {

        private int page = 0;
        private static final int PER_PAGE = 21;
        private static final String[][] PERMISSIONS = {
                {"moderex.staff", "Base staff access"},
                {"moderex.admin", "Admin-level access"},
                {"moderex.webpanel", "Web panel access"},
                {"moderex.command.ban", "Ban/unban players"},
                {"moderex.command.mute", "Mute/unmute players"},
                {"moderex.command.kick", "Kick players"},
                {"moderex.command.warn", "Warn players"},
                {"moderex.command.ipban", "IP-ban players"},
                {"moderex.command.vanish", "Toggle vanish mode"},
                {"moderex.command.staffchat", "Use staff chat"},
                {"moderex.command.modlog", "View moderation logs"},
                {"moderex.command.admin", "Configure automod/settings"},
                {"moderex.command.punish", "Open punishment GUI"},
                {"moderex.notify.anticheat", "Receive anticheat alerts"},
                {"moderex.notify.punishment", "Receive punishment alerts"},
                {"moderex.ai.bypass", "Bypass AI moderation"},
                {"moderex.ai.manage", "Configure AI moderation"},
                {"moderex.security", "Access security settings"},
                {"moderex.rules.manage", "Manage server rules"},
                {"moderex.admin.debug", "Debug tools"},
        };

        public PermissionListGui(ModereX plugin) {
            super(plugin, "<yellow>Permission Nodes</yellow>", 6);
        }

        @Override
        protected void populate() {
            fillBorder(Material.YELLOW_STAINED_GLASS_PANE);

            int totalPages = Math.max(1, (int) Math.ceil((double) PERMISSIONS.length / PER_PAGE));
            if (page >= totalPages) page = totalPages - 1;

            int start = page * PER_PAGE;
            int end = Math.min(start + PER_PAGE, PERMISSIONS.length);

            int slot = 10;
            for (int i = start; i < end; i++) {
                // Skip border slots
                if (slot % 9 == 0) slot++;
                if (slot % 9 == 8) slot += 2;
                if (slot >= 45) break;

                String perm = PERMISSIONS[i][0];
                String desc = PERMISSIONS[i][1];
                boolean hasIt = viewer != null && viewer.hasPermission(perm);

                setItem(slot, createItem(
                        hasIt ? Material.LIME_DYE : Material.GRAY_DYE,
                        (hasIt ? "<green>" : "<red>") + perm,
                        "<gray>" + desc,
                        "",
                        hasIt ? "<green>You have this permission" : "<red>You don't have this permission"
                ));
                slot++;
            }

            // Pagination
            if (page > 0) {
                setItem(48, createPreviousButton(page + 1), () -> {
                    page--;
                    refresh();
                });
            }
            if (page < totalPages - 1) {
                setItem(50, createNextButton(page + 1, totalPages), () -> {
                    page++;
                    refresh();
                });
            }

            // Back
            setItem(45, createItem(Material.ARROW, "<yellow>Go Back", "<gray>Return to security"), () -> {
                openGui(new SecurityGui(plugin));
            });

            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);
        }
    }
}
