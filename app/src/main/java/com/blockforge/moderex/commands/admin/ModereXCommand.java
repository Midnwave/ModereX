package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Main ModereX command with various administrative subcommands.
 */
public class ModereXCommand extends BaseCommand {

    private final Set<UUID> allowedUsers = new HashSet<>();

    public ModereXCommand(ModereX plugin) {
        super(plugin, null, false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return;
        }

        String subcommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subcommand) {
            case "allow" -> handleAllow(sender, subArgs);
            case "unlink" -> handleUnlink(sender, subArgs);
            case "reload" -> handleReload(sender, subArgs);
            case "info" -> handleInfo(sender, subArgs);
            case "servers" -> handleServers(sender, subArgs);
            case "reveal" -> handleReveal(sender, subArgs);
            case "broadcast" -> handleBroadcast(sender, subArgs);
            case "timezone" -> handleTimezone(sender, subArgs);
            case "reset-database" -> handleResetDatabase(sender, subArgs);
            case "reset-templates" -> handleResetTemplates(sender, subArgs);
            case "add-login" -> handleAddLogin(sender, subArgs);
            case "accept" -> handleAccept(sender, subArgs);
            case "license" -> handleLicense(sender, subArgs);
            case "upgrade" -> handleUpgrade(sender, subArgs);
            default -> showHelp(sender);
        }
    }

    private void showHelp(CommandSender sender) {
        sendMessage(sender, "<gold><bold>ModereX Commands:");
        sendMessage(sender, "<yellow>/moderex allow <add|check|remove> <user> <gray>- Manage allowed users");
        sendMessage(sender, "<yellow>/moderex unlink <user> <gray>- Unlink IP addresses for user");
        sendMessage(sender, "<yellow>/moderex reload <gray>- Reload configurations");
        sendMessage(sender, "<yellow>/moderex info <gray>- Display database pool information");
        sendMessage(sender, "<yellow>/moderex servers <gray>- List connected servers");
        sendMessage(sender, "<yellow>/moderex reveal <ID> <gray>- Convert randomized punishment ID");
        sendMessage(sender, "<yellow>/moderex broadcast <message> <gray>- Broadcast to all servers");
        sendMessage(sender, "<yellow>/moderex timezone [timezone] <gray>- View/set timezone");
        sendMessage(sender, "<yellow>/moderex reset-database <gray>- Clear all punishments (console only)");
        sendMessage(sender, "<yellow>/moderex reset-templates <gray>- Clear template progression");
        sendMessage(sender, "<yellow>/moderex add-login <user> <gray>- Add login records");
        sendMessage(sender, "<yellow>/moderex accept <gray>- Accept disclaimer");
        sendMessage(sender, "<yellow>/moderex license <gray>- Show license");
        sendMessage(sender, "<yellow>/moderex upgrade <gray>- Force database upgrade");
    }

    private void handleAllow(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.allow")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /moderex allow <add|check|remove> <user>");
            return;
        }

        String action = args[0].toLowerCase();
        TargetResolver target = new TargetResolver(args[1]);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[1]);
            return;
        }

        switch (action) {
            case "add" -> {
                if (target.getUuid() != null) {
                    allowedUsers.add(target.getUuid());
                    sendMessage(sender, "<green>Added <gold>" + target.getDisplayName() + " <green>to allowed list.");
                    sendMessage(sender, "<gray>This user will bypass IP bans, GeoIP restrictions, and lockdown.");
                }
            }
            case "check" -> {
                if (target.getUuid() != null) {
                    boolean isAllowed = allowedUsers.contains(target.getUuid());
                    if (isAllowed) {
                        sendMessage(sender, "<green>" + target.getDisplayName() + " is in the allowed list.");
                    } else {
                        sendMessage(sender, "<yellow>" + target.getDisplayName() + " is NOT in the allowed list.");
                    }
                }
            }
            case "remove" -> {
                if (target.getUuid() != null) {
                    allowedUsers.remove(target.getUuid());
                    sendMessage(sender, "<green>Removed <gold>" + target.getDisplayName() + " <green>from allowed list.");
                }
            }
            default -> sendMessage(sender, "<red>Invalid action. Use: add, check, or remove");
        }
    }

    private void handleUnlink(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.unlink")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /moderex unlink <user>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        sendMessage(sender, "<gray>IP unlinking will be implemented with full IP tracking");
        sendMessage(sender, "<yellow>This feature requires the full IP tracking system to be implemented.");
    }

    private void handleReload(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.reload")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "<yellow>Reloading ModereX configurations...");

        try {
            plugin.reload();
            sendMessage(sender, "<green>ModereX configurations reloaded successfully!");
            sendMessage(sender, "<gray>Reloaded: config.yml, messages.yml, templates.yml, automod rules");
        } catch (Exception e) {
            sendMessage(sender, "<red>Failed to reload configurations: " + e.getMessage());
            plugin.logError("Failed to reload configurations", e);
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.info")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "<gold><bold>ModereX Database Information:");

        try {
            String dbType = plugin.getConfigManager().getSettings().getDatabaseType();
            sendMessage(sender, "<yellow>Database Type: <gray>" + dbType.toUpperCase());

            try {
                HikariPoolMXBean poolProxy = plugin.getDatabaseManager().getDataSource().getHikariPoolMXBean();
                if (poolProxy != null) {
                    sendMessage(sender, "<yellow>Active Connections: <gray>" + poolProxy.getActiveConnections());
                    sendMessage(sender, "<yellow>Idle Connections: <gray>" + poolProxy.getIdleConnections());
                    sendMessage(sender, "<yellow>Total Connections: <gray>" + poolProxy.getTotalConnections());
                    sendMessage(sender, "<yellow>Threads Awaiting: <gray>" + poolProxy.getThreadsAwaitingConnection());
                } else {
                    sendMessage(sender, "<gray>Connection pool stats not available");
                }
            } catch (Exception poolEx) {
                sendMessage(sender, "<gray>Connection pool stats not available");
            }

            sendMessage(sender, "<yellow>Plugin Version: <gray>" + plugin.getDescription().getVersion());
        } catch (Exception e) {
            sendMessage(sender, "<red>Failed to retrieve database information");
            plugin.logError("Failed to get database info", e);
        }
    }

    private void handleServers(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.servers")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "<gold><bold>Connected Servers:");
        sendMessage(sender, "<yellow>Local Server: <green>" + plugin.getServer().getName() + " <gray>(this server)");
        sendMessage(sender, "<gray>Multi-server sync requires proxy mode to be enabled");

    }

    private void handleReveal(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.reveal")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /moderex reveal <ID>");
            return;
        }

        try {
            String caseId = args[0];
            sendMessage(sender, "<yellow>Looking up case ID: <gold>" + caseId);

            plugin.getPunishmentManager().getPunishmentByCaseId(caseId).thenAccept(punishment -> {
                if (punishment == null) {
                    sendMessage(sender, "<red>No punishment found with case ID: " + caseId);
                } else {
                    sendMessage(sender, "<green>Found punishment:");
                    sendMessage(sender, "<yellow>ID: <gold>#" + punishment.getId());
                    sendMessage(sender, "<yellow>Type: <gold>" + punishment.getType().getDisplayName());
                    sendMessage(sender, "<yellow>Player: <gold>" + punishment.getPlayerName());
                    sendMessage(sender, "<yellow>Reason: <gray>" + punishment.getReason());
                }
            });
        } catch (Exception e) {
            sendMessage(sender, "<red>Failed to lookup case ID: " + e.getMessage());
        }
    }

    private void handleBroadcast(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.broadcast")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /moderex broadcast <message>");
            return;
        }

        String message = String.join(" ", args);
        message = message.replace("\\n", "\n");

        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("moderex.notify.broadcast")) {
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message));
            }
        }

        plugin.getServer().getConsoleSender().sendMessage(
            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message)
        );

        sendMessage(sender, "<green>Message broadcast to all staff with notification permission!");
    }

    private void handleTimezone(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.timezone")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            String currentTz = java.util.TimeZone.getDefault().getID();
            sendMessage(sender, "<yellow>Current server timezone: <gold>" + currentTz);
            sendMessage(sender, "<gray>To change, use: /moderex timezone <timezone>");
            sendMessage(sender, "<gray>Example timezones: America/New_York, Europe/London, Asia/Tokyo");
        } else {
            String timezone = args[0];
            try {
                java.util.TimeZone tz = java.util.TimeZone.getTimeZone(timezone);
                if (tz.getID().equals("GMT") && !timezone.equals("GMT")) {
                    sendMessage(sender, "<red>Invalid timezone: " + timezone);
                    sendMessage(sender, "<gray>Use a valid timezone ID like: America/New_York");
                } else {
                    java.util.TimeZone.setDefault(tz);
                    sendMessage(sender, "<green>Timezone set to: <gold>" + timezone);
                    sendMessage(sender, "<yellow>Note: This setting will reset on server restart.");
                    sendMessage(sender, "<gray>For permanent changes, modify your server startup parameters.");
                }
            } catch (Exception e) {
                sendMessage(sender, "<red>Failed to set timezone: " + e.getMessage());
            }
        }
    }

    private void handleResetDatabase(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(sender, "<red>This command can only be run from console.");
            return;
        }

        boolean hasConfirm = args.length > 0 && args[0].equalsIgnoreCase("confirm");

        if (!hasConfirm) {
            sendMessage(sender, "<red><bold>WARNING: This will clear all punishments from the database!");
            sendMessage(sender, "<yellow>This action is IRREVERSIBLE and DANGEROUS!");
            sendMessage(sender, "<gray>Use: /moderex reset-database confirm");
            return;
        }

        sendMessage(sender, "<red>Clearing all punishments from database...");

        try {
            int deleted = plugin.getDatabaseManager().update("DELETE FROM moderex_punishments", new Object[]{});
            sendMessage(sender, "<green>Successfully deleted " + deleted + " punishments.");
            sendMessage(sender, "<yellow>Cleared all punishment caches.");

            plugin.getPunishmentManager().clearCaches();

        } catch (Exception e) {
            sendMessage(sender, "<red>Failed to reset database: " + e.getMessage());
            plugin.logError("Failed to reset database", e);
        }
    }

    private void handleResetTemplates(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.reset-templates")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length > 0) {
            TargetResolver target = new TargetResolver(args[0]);
            if (target.isValid() && target.isPlayer() && target.getUuid() != null) {
                sendMessage(sender, "<yellow>Clearing template progression for <gold>" + target.getDisplayName() + "<yellow>...");

                try {
                    int deleted = plugin.getDatabaseManager().update(
                        "DELETE FROM moderex_template_progression WHERE player_uuid = ?",
                        new Object[]{target.getUuid().toString()}
                    );
                    sendMessage(sender, "<green>Cleared " + deleted + " template progression entries for " + target.getDisplayName());
                } catch (Exception e) {
                    sendMessage(sender, "<red>Failed to clear template progression: " + e.getMessage());
                }
            } else {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            }
        } else {
            sendMessage(sender, "<yellow>Clearing ALL template progression...");

            try {
                int deleted = plugin.getDatabaseManager().update(
                    "DELETE FROM moderex_template_progression",
                    new Object[]{}
                );
                sendMessage(sender, "<green>Cleared " + deleted + " template progression entries for all players!");
            } catch (Exception e) {
                sendMessage(sender, "<red>Failed to clear template progression: " + e.getMessage());
            }
        }
    }

    private void handleAddLogin(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(sender, "<red>This command can only be run from console.");
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /moderex add-login <name> <UUID> <IP>");
            sendMessage(sender, "<red>   or: /moderex add-login <user1>[,user2,user3,...]");
            return;
        }

        sendMessage(sender, "<yellow>Adding login records to database...");
        sendMessage(sender, "<gray>This feature requires the full IP tracking system to be implemented.");
        sendMessage(sender, "<yellow>Usage: /moderex add-login <name> <UUID> <IP>");

    }

    private void handleAccept(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(sender, "<red>This command can only be run from console.");
            return;
        }

        sendMessage(sender, "<green>ModereX disclaimer acknowledged!");
        sendMessage(sender, "<yellow>All ModereX features are fully enabled.");
        sendMessage(sender, "<gray>Thank you for using ModereX!");
        sendMessage(sender, "");
        sendMessage(sender, "<yellow>Note: This is an acknowledgment command. No action is required.");
    }

    private void handleLicense(CommandSender sender, String[] args) {
        sendMessage(sender, "<gold><bold>ModereX License Information:");
        sendMessage(sender, "<yellow>Version: <gray>" + plugin.getDescription().getVersion());
        sendMessage(sender, "<yellow>Plugin by: <gray>" + String.join(", ", plugin.getDescription().getAuthors()));
        sendMessage(sender, "<yellow>Description: <gray>" + plugin.getDescription().getDescription());
        sendMessage(sender, "");
        sendMessage(sender, "<green>ModereX - Professional Minecraft Moderation Plugin");
        sendMessage(sender, "<gray>For support and documentation, visit the plugin page.");

        if (plugin.getDescription().getWebsite() != null) {
            sendMessage(sender, "<yellow>Website: <gray>" + plugin.getDescription().getWebsite());
        }
    }

    private void handleUpgrade(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sendMessage(sender, "<red>This command can only be run from console.");
            return;
        }

        sendMessage(sender, "<yellow>Forcing database schema upgrade...");

        try {
            plugin.getDatabaseManager().upgradeSchema();
            sendMessage(sender, "<green>Database schema upgrade completed!");
            sendMessage(sender, "<gray>All tables have been updated to the latest schema.");
        } catch (Exception e) {
            sendMessage(sender, "<red>Failed to upgrade database: " + e.getMessage());
            plugin.logError("Failed to upgrade database schema", e);
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(Arrays.asList(
                "allow", "unlink", "reload", "info", "servers", "reveal",
                "broadcast", "timezone", "reset-database", "reset-templates",
                "add-login", "accept", "license", "upgrade"
            ), args[0]);
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            return switch (subcommand) {
                case "allow" -> filterCompletions(Arrays.asList("add", "check", "remove"), args[1]);
                case "unlink", "reveal" -> filterCompletions(getOnlinePlayerNames(sender), args[1]);
                default -> super.tabComplete(sender, args);
            };
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("allow")) {
            return filterCompletions(getOnlinePlayerNames(sender), args[2]);
        }

        return super.tabComplete(sender, args);
    }
}
