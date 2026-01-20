package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * /unban <player|id> [reason] [flags]
 *
 * Removes an active ban. Can target by player name, UUID, or punishment ID.
 */
public class UnbanCommand extends BaseCommand {

    public UnbanCommand(ModereX plugin) {
        super(plugin, "moderex.unban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /unban <player|id> [reason] [flags]");
            return;
        }

        TargetResolver target = new TargetResolver(regularArgs.get(0));

        if (!target.isValid()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        String reason = regularArgs.size() > 1 ? flagParser.joinArgs(1) : null;

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        // Handle by punishment ID
        if (target.isPunishmentId()) {
            Long punishmentId = target.getPunishmentId();
            plugin.getPunishmentManager().getPunishmentById(punishmentId).thenAccept(punishment -> {
                if (punishment == null) {
                    sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(punishmentId));
                    return;
                }

                if (punishment.getType() != PunishmentType.BAN) {
                    sendMessage(sender, "<red>Punishment #" + punishmentId + " is not a ban.");
                    return;
                }

                if (!punishment.isActive()) {
                    sendMessage(sender, "<red>Ban #" + punishmentId + " is already inactive.");
                    return;
                }

                plugin.getPunishmentManager().removePunishment(punishment.getPlayerUuid(), PunishmentType.BAN, staffUuid, staffName, reason)
                        .thenAccept(success -> {
                            if (success) {
                                sendMessage(sender, MessageKey.UNBAN_SUCCESS, "player", punishment.getPlayerName());
                            } else {
                                sendMessage(sender, "<red>Failed to remove ban #" + punishmentId);
                            }
                        });
            });
            return;
        }

        // Handle by player
        if (target.isPlayer() && target.getUuid() != null) {
            UUID targetUuid = target.getUuid();
            String displayName = target.getDisplayName();

            if (!plugin.getPunishmentManager().isBanned(targetUuid)) {
                sendMessage(sender, MessageKey.UNBAN_NOT_BANNED, "player", displayName);
                return;
            }

            plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.BAN, staffUuid, staffName, reason)
                    .thenAccept(success -> {
                        if (success) {
                            sendMessage(sender, MessageKey.UNBAN_SUCCESS, "player", displayName);
                        } else {
                            sendMessage(sender, "<red>Failed to unban player.");
                        }
                    });
        } else {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();
        int regularArgIndex = regularArgs.size();

        String currentArg = args.length > 0 ? args[args.length - 1] : "";
        if (currentArg.startsWith("-")) {
            return filterCompletions(Arrays.asList("-s", "-S", "--hide"), currentArg);
        }

        if (regularArgIndex == 1) {
            // Tab completion for banned players (using online players for now)
            return filterCompletions(getOnlinePlayerNames(sender), currentArg);
        }
        return super.tabComplete(sender, args);
    }
}
