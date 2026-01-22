package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * View detailed information about a punishment by its case ID.
 * Usage: /viewpunishment <caseId>
 * Example: /viewpunishment MX-A1B2C3
 */
public class ViewPunishmentCommand extends BaseCommand {

    public ViewPunishmentCommand(ModereX plugin) {
        super(plugin, "moderex.command.viewpunishment", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /viewpunishment <caseId>");
            sendMessage(sender, "<gray>Example: /viewpunishment MX-A1B2C3");
            return;
        }

        String caseId = args[0].toUpperCase();

        // Validate case ID format (should be MX-XXXXXX)
        if (!caseId.matches("MX-[A-Z0-9]+")) {
            sendMessage(sender, "<red>Invalid case ID format. Expected format: MX-XXXXXX");
            return;
        }

        sendMessage(sender, "<gray>Looking up punishment " + caseId + "...");

        plugin.getPunishmentManager().getPunishmentByCaseId(caseId).thenAccept(punishment -> {
            if (punishment == null) {
                sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", caseId);
                return;
            }

            displayPunishment(sender, punishment);
        });
    }

    private void displayPunishment(CommandSender sender, Punishment p) {
        String statusColor;
        String statusText;

        if (p.isActive() && !p.isExpired()) {
            statusColor = "<green>";
            statusText = "Active";
        } else if (p.isExpired()) {
            statusColor = "<yellow>";
            statusText = "Expired";
        } else {
            statusColor = "<red>";
            statusText = "Revoked";
        }

        String typeColor = switch (p.getType()) {
            case MUTE, IPMUTE -> "<gold>";
            case BAN, IPBAN -> "<red>";
            case KICK -> "<yellow>";
            case WARN -> "<aqua>";
        };

        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════════════"));
        sender.sendMessage(TextUtil.parse("<white>     Punishment Details: <yellow>" + p.getCaseId()));
        sender.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════════════"));
        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>Type: " + typeColor + p.getType().name()));
        sender.sendMessage(TextUtil.parse("<gray>Status: " + statusColor + statusText));
        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>Player: <white>" + p.getPlayerName()));
        sender.sendMessage(TextUtil.parse("<gray>UUID: <dark_gray>" + p.getPlayerUuid()));
        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>Reason: <white>" + p.getReason()));
        sender.sendMessage(TextUtil.parse("<gray>Staff: <white>" + p.getStaffName()));
        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>Created: <white>" + TimeUtil.formatFull(p.getCreatedAt())));

        if (p.isPermanent()) {
            sender.sendMessage(TextUtil.parse("<gray>Duration: <red>Permanent"));
        } else {
            long duration = p.getExpiresAt() - p.getCreatedAt();
            sender.sendMessage(TextUtil.parse("<gray>Duration: <white>" + DurationParser.format(duration)));
            sender.sendMessage(TextUtil.parse("<gray>Expires: <white>" + TimeUtil.formatFull(p.getExpiresAt())));

            if (p.isActive() && !p.isExpired()) {
                sender.sendMessage(TextUtil.parse("<gray>Remaining: <white>" + DurationParser.formatRemaining(p.getExpiresAt())));
            }
        }

        if (p.getIpAddress() != null && !p.getIpAddress().isEmpty()) {
            sender.sendMessage(TextUtil.parse(""));
            sender.sendMessage(TextUtil.parse("<gray>IP Address: <white>" + p.getIpAddress()));
        }

        if (p.getServer() != null && !p.getServer().isEmpty()) {
            sender.sendMessage(TextUtil.parse("<gray>Server: <white>" + p.getServer()));
        }

        if (p.isRemoved()) {
            sender.sendMessage(TextUtil.parse(""));
            sender.sendMessage(TextUtil.parse("<red>─── Revoked ───"));
            sender.sendMessage(TextUtil.parse("<gray>Revoked by: <white>" + p.getRemovedByName()));
            sender.sendMessage(TextUtil.parse("<gray>Revoked at: <white>" + TimeUtil.formatFull(p.getRemovedAt())));
            if (p.getRemovedReason() != null && !p.getRemovedReason().isEmpty()) {
                sender.sendMessage(TextUtil.parse("<gray>Revoke reason: <white>" + p.getRemovedReason()));
            }
        }

        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════════════"));
        sender.sendMessage(TextUtil.parse(""));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Get recent punishment case IDs for suggestions
            List<String> caseIds = plugin.getPunishmentManager().getRecentCaseIds(50);
            if (caseIds.isEmpty()) {
                return filterCompletions(List.of("MX-"), args[0]);
            }
            return filterCompletions(caseIds, args[0]);
        }
        return Collections.emptyList();
    }
}
