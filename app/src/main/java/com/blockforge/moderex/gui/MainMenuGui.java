package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.punishment.PunishPlayerGui;
import com.blockforge.moderex.gui.ReplayGui;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MainMenuGui extends BaseGui {

    public MainMenuGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>ModereX</gradient> <dark_gray>- Main Menu", 6);
    }

    @Override
    protected void populate() {
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Title decoration
        setItem(4, createItem(Material.NETHER_STAR, "<gradient:#a855f7:#ec4899><bold>ModereX</bold></gradient>",
                "<gray>Version " + plugin.getDescription().getVersion(),
                "",
                "<white>Advanced Moderation Plugin",
                "<gray>Select an option below"));

        // ======== Row 2: Quick Actions ========

        // Online Players - Select player to punish
        setItem(19, createOnlinePlayersItem(), () -> openGui(new OnlinePlayersGui(plugin)));

        // Recent Punishments
        setItem(21, createItem(Material.WRITABLE_BOOK, "<gold>Recent Punishments",
                "<gray>View recent moderation actions",
                "",
                "<yellow>Click to view"), () -> openGui(new ModLogGui(plugin)));

        // Watchlist
        int watchlistSize = plugin.getWatchlistManager().getWatchedPlayers().size();
        setItem(23, createItem(Material.SPYGLASS, "<aqua>Watchlist",
                "<gray>View players being monitored",
                "",
                "<white>Players watched: <yellow>" + watchlistSize,
                "",
                "<yellow>Click: <white>View watched players",
                "<yellow>Shift-click: <white>All players view"), clickType -> {
            if (clickType.isShiftClick()) {
                openGui(new AllPlayersWatchlistGui(plugin));
            } else {
                openGui(new WatchlistGui(plugin));
            }
        });

        // Staff Online
        setItem(25, createStaffOnlineItem());

        // ======== Row 3: Moderation Tools ========

        // Ban Management
        setItem(28, createItem(Material.BARRIER, "<red>Ban Management",
                "<gray>Manage player bans",
                "",
                "<white>Active Bans: <yellow>" + getActiveBanCount(),
                "",
                "<yellow>Click to manage"), () -> {
            sendMessage("<yellow>Ban Management GUI coming soon!");
        });

        // Mute Management
        setItem(30, createItem(Material.PAPER, "<gold>Mute Management",
                "<gray>Manage player mutes",
                "",
                "<white>Active Mutes: <yellow>" + getActiveMuteCount(),
                "",
                "<yellow>Click to manage"), () -> {
            sendMessage("<yellow>Mute Management GUI coming soon!");
        });

        // Warning Management
        setItem(32, createItem(Material.BOOK, "<yellow>Warning Management",
                "<gray>Manage player warnings",
                "",
                "<yellow>Click to manage"), () -> {
            sendMessage("<yellow>Warning Management GUI coming soon!");
        });

        // IP Bans
        setItem(34, createItem(Material.IRON_BARS, "<dark_red>IP Bans",
                "<gray>Manage IP address bans",
                "",
                "<yellow>Click to manage"), () -> {
            sendMessage("<yellow>IP Ban Management GUI coming soon!");
        });

        // Templates
        if (viewer.hasPermission("moderex.template.view")) {
            setItem(35, createItem(Material.WRITABLE_BOOK, "<light_purple>Templates",
                    "<gray>Manage punishment templates",
                    "",
                    "<white>Quick-apply presets for punishments",
                    "",
                    "<yellow>Click to manage"), () -> openGui(new TemplateGui(plugin)));
        }

        // ======== Row 4: Admin Tools ========

        // Replays
        if (viewer.hasPermission("moderex.command.replay")) {
            setItem(37, createItem(Material.JUKEBOX, "<gradient:#a855f7:#ec4899>Replays</gradient>",
                    "<gray>View recorded player sessions",
                    "",
                    "<white>Browse anticheat alerts, watchlist recordings",
                    "<white>and manual staff recordings",
                    "",
                    "<yellow>Click to browse"), () -> openGui(new ReplayGui(plugin)));
        }

        // Chat Control
        if (viewer.hasPermission("moderex.command.admin")) {
            setItem(38, createChatControlItem(), () -> openGui(new ChatControlGui(plugin)));

            // Automod Settings
            setItem(40, createItem(Material.COMPARATOR, "<light_purple>Automod Settings",
                    "<gray>Configure automatic moderation",
                    "",
                    "<yellow>Click to configure"), () -> {
                sendMessage("<yellow>Automod GUI coming soon!");
            });

            // Server Settings
            setItem(42, createItem(Material.COMMAND_BLOCK, "<blue>Staff Settings",
                    "<gray>Configure personal & plugin settings",
                    "",
                    "<yellow>Click to configure"), () -> openGui(new StaffSettingsGui(plugin)));
        }

        // ======== Row 5: Information ========

        // Statistics
        setItem(47, createItem(Material.EMERALD, "<green>Statistics",
                "<gray>View moderation statistics",
                "",
                "<yellow>Click to view"), () -> {
            sendMessage("<yellow>Statistics GUI coming soon!");
        });

        // Web Panel
        if (viewer.hasPermission("moderex.webpanel") && plugin.getConfigManager().getSettings().isWebPanelEnabled()) {
            setItem(49, createItem(Material.END_PORTAL_FRAME, "<light_purple>Web Panel",
                    "<gray>Connect to the web panel",
                    "",
                    "<white>Use <yellow>/mx connect <white>in chat",
                    "<gray>to get your connection code",
                    "",
                    "<yellow>Click for info"), () -> {
                close();
                viewer.performCommand("mx connect");
            });
        }

        // Help
        setItem(51, createItem(Material.KNOWLEDGE_BOOK, "<aqua>Help",
                "<gray>View available commands",
                "",
                "<yellow>Click to view"), () -> {
            close();
            viewer.performCommand("mx help");
        });

        // Close button
        setItem(53, createCloseButton(), this::close);
    }

    private ItemStack createOnlinePlayersItem() {
        int online = Bukkit.getOnlinePlayers().size();
        return createItem(Material.PLAYER_HEAD, "<green>Online Players",
                "<gray>Select a player to moderate",
                "",
                "<white>Currently Online: <yellow>" + online,
                "",
                "<yellow>Click to select player");
    }

    private ItemStack createStaffOnlineItem() {
        List<String> staffNames = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("moderex.command.punish")) {
                String name = p.getName();
                if (plugin.getVanishManager().isVanished(p)) {
                    name = "<gray><strikethrough>" + name + "</strikethrough>";
                }
                staffNames.add("<gray>- <white>" + name);
            }
        }

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Staff members currently online");
        lore.add("");
        if (staffNames.isEmpty()) {
            lore.add("<red>No staff online");
        } else {
            lore.add("<white>Staff Online: <yellow>" + staffNames.size());
            lore.add("");
            lore.addAll(staffNames.subList(0, Math.min(staffNames.size(), 10)));
            if (staffNames.size() > 10) {
                lore.add("<gray>...and " + (staffNames.size() - 10) + " more");
            }
        }

        return createItem(Material.TOTEM_OF_UNDYING, "<gold>Staff Online", lore);
    }

    private ItemStack createChatControlItem() {
        boolean chatEnabled = plugin.getConfigManager().getSettings().isChatEnabled();
        int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Control server chat settings");
        lore.add("");
        lore.add("<white>Chat: " + (chatEnabled ? "<green>Enabled" : "<red>Disabled"));
        lore.add("<white>Slowmode: " + (slowmode > 0 ? "<yellow>" + slowmode + "s" : "<gray>Off"));
        lore.add("");
        lore.add("<yellow>Click to manage");

        return createItem(Material.OAK_SIGN, "<yellow>Chat Control", lore);
    }

    private int getActiveBanCount() {
        // Count is calculated asynchronously, return cached or 0
        return 0;
    }

    private int getActiveMuteCount() {
        // Count is calculated asynchronously, return cached or 0
        return 0;
    }

    private void sendMessage(String message) {
        viewer.sendMessage(TextUtil.parse(message));
    }

    public static class OnlinePlayersGui extends PaginatedGui<Player> {

        public OnlinePlayersGui(ModereX plugin) {
            super(plugin, "<green>Online Players", 6);
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

            // Check for active punishments
            if (plugin.getPunishmentManager().isMuted(player.getUniqueId())) {
                lore.add("<red>Currently Muted");
            }
            if (plugin.getWatchlistManager().isWatched(player.getUniqueId())) {
                lore.add("<yellow>On Watchlist");
            }

            lore.add("");
            lore.add("<yellow>Click to moderate");

            meta.lore(lore.stream().map(TextUtil::parse).toList());
            head.setItemMeta(meta);

            setItem(slot, head, () -> openGui(new PunishPlayerGui(plugin, player)));
        }

        @Override
        protected void addNavigationButtons() {
            super.addNavigationButtons();
            // Add back button to main menu
            setItem(45, createBackButton(), () -> openGui(new MainMenuGui(plugin)));
        }
    }

    public static class ChatControlGui extends BaseGui {

        public ChatControlGui(ModereX plugin) {
            super(plugin, "<yellow>Chat Control", 3);
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            boolean chatEnabled = plugin.getConfigManager().getSettings().isChatEnabled();
            int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();

            // Toggle Chat
            setItem(10, createItem(
                    chatEnabled ? Material.LIME_DYE : Material.RED_DYE,
                    chatEnabled ? "<green>Chat Enabled" : "<red>Chat Disabled",
                    "<gray>Click to toggle chat",
                    "",
                    "<yellow>Click to " + (chatEnabled ? "disable" : "enable")
            ), () -> {
                plugin.getConfigManager().getSettings().setChatEnabled(!chatEnabled);
                broadcastChatStatusToPanel();
                refresh();
            });

            // Slowmode Control
            setItem(12, createItem(Material.CLOCK, "<gold>Slowmode: " + (slowmode > 0 ? slowmode + "s" : "Off"),
                    "<gray>Click to cycle slowmode",
                    "",
                    "<yellow>Left-click: +5s",
                    "<yellow>Right-click: -5s",
                    "<yellow>Shift-click: Reset"
            ), clickType -> {
                int current = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
                if (clickType.isShiftClick()) {
                    plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(0);
                } else if (clickType.isLeftClick()) {
                    plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(Math.min(300, current + 5));
                } else if (clickType.isRightClick()) {
                    plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(Math.max(0, current - 5));
                }
                broadcastChatStatusToPanel();
                refresh();
            });

            // Clear Chat
            setItem(14, createItem(Material.TNT, "<red>Clear Chat",
                    "<gray>Clear chat for all players",
                    "",
                    "<red>Click to clear"
            ), () -> {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    for (int i = 0; i < 100; i++) {
                        p.sendMessage("");
                    }
                    p.sendMessage(TextUtil.parse("<gray>Chat was cleared by <white>" + viewer.getName()));
                }
                viewer.sendMessage(TextUtil.parse("<green>Chat cleared!"));
            });

            // Lock Chat (disable + message)
            setItem(16, createItem(Material.CHAIN, "<dark_red>Lock Chat",
                    "<gray>Disable chat and notify players",
                    "",
                    "<yellow>Click to lock"
            ), () -> {
                plugin.getConfigManager().getSettings().setChatEnabled(false);
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    p.sendMessage(TextUtil.parse("<red><bold>Chat has been locked by staff."));
                }
                broadcastChatStatusToPanel();
                refresh();
            });

            // Back button
            setItem(22, createBackButton(), () -> openGui(new MainMenuGui(plugin)));
        }

        private void broadcastChatStatusToPanel() {
            if (plugin.getWebPanelServer() != null) {
                plugin.getWebPanelServer().broadcastChatStatus();
            }
        }
    }
}
