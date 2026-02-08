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
 * Removes an active mute. Can target by player name, UUID, or punishment ID.
 */
public class UnmuteCommand extends BaseCommand {

    public UnmuteCommand(ModereX plugin) {
        super(plugin, "moderex.unmute", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /unmute <player|id> [reason] [flags]");
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

        if (target.isPunishmentId()) {
            Long punishmentId = target.getPunishmentId();
            plugin.getPunishmentManager().getPunishmentById(punishmentId).thenAccept(punishment -> {
                if (punishment == null) {
                    sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(punishmentId));
                    return;
                }

                if (punishment.getType() != PunishmentType.MUTE) {
                    sendMessage(sender, "<red>Punishment #" + punishmentId + " is not a mute.");
                    return;
                }

                if (!punishment.isActive()) {
                    sendMessage(sender, "<red>Mute #" + punishmentId + " is already inactive.");
                    return;
                }

                plugin.getPunishmentManager().removePunishment(punishment.getPlayerUuid(), PunishmentType.MUTE, staffUuid, staffName, reason)
                        .thenAccept(success -> {
                            if (success) {
                                sendMessage(sender, MessageKey.UNMUTE_SUCCESS, "player", punishment.getPlayerName());
                            } else {
                                sendMessage(sender, "<red>Failed to remove mute #" + punishmentId);
                            }
                        });
            });
            return;
        }

        if (target.isPlayer() && target.getUuid() != null) {
            UUID targetUuid = target.getUuid();
            String displayName = target.getDisplayName();

            if (!plugin.getPunishmentManager().isMuted(targetUuid)) {
                sendMessage(sender, MessageKey.UNMUTE_NOT_MUTED, "player", displayName);
                return;
            }

            plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.MUTE, staffUuid, staffName, reason)
                    .thenAccept(success -> {
                        if (success) {
                            sendMessage(sender, MessageKey.UNMUTE_SUCCESS, "player", displayName);
                        } else {
                            sendMessage(sender, "<red>Failed to unmute player.");
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
            return filterCompletions(getOnlinePlayerNames(sender), currentArg);
        }
        return super.tabComplete(sender, args);
    }
}
