package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /kick <player> [reason] [flags]
 *
 * Kicks a player from the server.
 */
public class KickCommand extends PunishmentCommandBase {

    public KickCommand(ModereX plugin) {
        super(plugin, "moderex.kick", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /kick <player> [reason] [flags]");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null || flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        // Kick doesn't support IP, modify, or delete flags
        if (!checkFlagPermissions(sender, flagParser, false, false, false)) {
            return;
        }

        // For kicks, target must be online
        Player target = Bukkit.getPlayer(regularArgs.get(0));

        if (target == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        String reason = regularArgs.size() > 1 ? flagParser.joinArgs(1) : "No reason specified";

        PunishmentContext context = PunishmentContext.builder(sender)
                .target(new TargetResolver(target.getName()))
                .flags(flagParser)
                .reason(reason)
                .build();

        executeKick(context, target);
    }

    private void executeKick(PunishmentContext context, Player target) {
        String reason = context.getReason();

        plugin.getPunishmentManager().kick(
                target.getUniqueId(),
                target.getName(),
                context.getExecutorUuid(),
                context.getExecutorName(),
                reason
        ).thenAccept(punishment -> {
            sendMessage(context.getSender(), MessageKey.KICK_SUCCESS,
                    "player", target.getName());

            broadcastPunishment(context, target.getName(), reason);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String reason) {
        if (context.isHidden()) {
            return;
        }

        if (context.isExtraSilent()) {
            plugin.getLogger().info(String.format("%s kicked %s. Reason: %s",
                    context.getExecutorName(), playerName, reason));
            return;
        }

        if (context.isSilent()) {
            return;
        }

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(MessageKey.KICK_BROADCAST,
                    "staff", context.getExecutorName(),
                    "player", playerName,
                    "reason", reason);

            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.notify.punishments")) {
                    player.sendMessage(message);
                }
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
