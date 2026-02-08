package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CmdBlacklistCommand extends BaseCommand {

    // Cached command list for tab completion (TTL: 30 seconds)
    private static List<String> cachedCommands = null;
    private static long cacheTimestamp = 0;
    private static final long CACHE_TTL = 30_000; // 30 seconds

    // ModereX commands that should be protected when config option is false
    private static final Set<String> MODEREX_COMMANDS = Set.of(
        "moderex", "mx", "ban", "mute", "warn", "kick", "tempban", "tempmute",
        "ipban", "ipmute", "unban", "unmute", "unwarn", "check", "checkban",
        "checkmute", "checkwarn", "history", "staffhistory", "warnings",
        "banlist", "mutelist", "warnlist", "viewpunishment", "dupeip",
        "iphistory", "ipreport", "geoip", "lastuuid", "namehistory",
        "automodhistory", "amhistory", "automodlog", "nickhistory",
        "nicknamehistory", "nicklog", "lockdown", "prunehistory",
        "staffrollback", "punish", "modlog", "staffchat",
        "sc", "staffhelp", "watchlist", "wl", "vanish", "v", "cmdblacklist",
        "cmdunblacklist", "cmdhistory", "log", "seen", "disguise", "disguisename",
        "disguiseskin", "rules", "replay", "mban", "munban", "mmute", "munmute",
        "mwarn", "munwarn", "mkick"
    );

    public CmdBlacklistCommand(ModereX plugin) {
        super(plugin, "moderex.cmdblacklist", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /cmdblacklist <player> <command> [duration]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        String cmdArg = args[1].toLowerCase();
        if (cmdArg.startsWith("/")) {
            cmdArg = cmdArg.substring(1);
        }
        final String command = cmdArg;

        // Check if command is protected (core server commands)
        List<String> protectedCommands = plugin.getConfigManager().getSettings().getCmdBlacklistProtectedCommands();
        if (protectedCommands.stream().anyMatch(c -> c.equalsIgnoreCase(command))) {
            sendMessage(sender, "<red>The command <yellow>/" + command + "<red> is a protected system command and cannot be blacklisted.");
            return;
        }

        // Check if command is a ModereX command and if that's allowed (OPs bypass this check)
        boolean isOp = sender.isOp();
        if (!isOp && MODEREX_COMMANDS.contains(command) && !plugin.getConfigManager().getSettings().isCmdBlacklistAllowModerexCommands()) {
            sendMessage(sender, "<red>Blacklisting ModereX commands is disabled by server configuration.");
            sendMessage(sender, "<gray>This restriction is in place to prevent staff from being locked out of moderation tools.");
            return;
        }

        long duration = -1;
        if (args.length >= 3 && DurationParser.isValidDuration(args[2])) {
            duration = DurationParser.parse(args[2]);
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        long expiresAt = duration == -1 ? -1 : System.currentTimeMillis() + duration;

        final String finalCommand = command;
        final long finalExpiresAt = expiresAt;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_command_blacklist
                        (player_uuid, command, staff_uuid, staff_name, created_at, expires_at, reason, server)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        targetUuid.toString(),
                        finalCommand,
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        System.currentTimeMillis(),
                        finalExpiresAt,
                        "Blacklisted by " + staffName,
                        plugin.getServer().getName()
                );

                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, MessageKey.CMD_BLACKLIST_SUCCESS,
                                "command", "/" + finalCommand,
                                "player", displayName));
            } catch (SQLException e) {
                plugin.logError("Failed to blacklist command", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to blacklist command."));
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            // Load registered commands with caching
            List<String> commands = getRegisteredCommands();
            if (commands == null || commands.isEmpty()) {
                // Fallback to loading message if cache isn't ready
                return List.of("Loading...");
            }
            return filterCompletions(commands, args[1]);
        }
        if (args.length == 3) {
            return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "permanent"), args[2]);
        }
        return super.tabComplete(sender, args);
    }

    /**
     * Get all registered commands from Bukkit's command map, with caching.
     * Returns command names in "plugin:command" format.
     */
    private List<String> getRegisteredCommands() {
        long now = System.currentTimeMillis();
        if (cachedCommands != null && (now - cacheTimestamp) < CACHE_TTL) {
            return cachedCommands;
        }

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ?> knownCommands = (Map<String, ?>) knownCommandsField.get(commandMap);

            List<String> commands = new ArrayList<>();
            for (String name : knownCommands.keySet()) {
                if (!name.contains(":")) {
                    commands.add(name);
                }
            }
            commands.sort(String::compareToIgnoreCase);

            cachedCommands = commands;
            cacheTimestamp = now;
            return commands;
        } catch (Exception e) {
            // Fallback: return null so caller shows loading
            return cachedCommands;
        }
    }
}
