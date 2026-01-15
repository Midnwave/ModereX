package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ModLogCommand extends BaseCommand {

    private static final int ENTRIES_PER_PAGE = 10;

    public ModLogCommand(ModereX plugin) {
        super(plugin, "moderex.command.modlog", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /modlog <player> [filter] [page]");
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

        String filter = "all";
        int page = 1;

        if (args.length >= 2) {
            if (isFilter(args[1])) {
                filter = args[1].toLowerCase();
                if (args.length >= 3) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        final String finalFilter = filter;
        final int finalPage = Math.max(1, page);

        plugin.getPunishmentManager().getPunishments(targetUuid).thenAccept(punishments -> {
            List<Punishment> filtered = punishments;
            if (!finalFilter.equals("all")) {
                PunishmentType filterType = PunishmentType.fromString(finalFilter);
                if (filterType != null) {
                    filtered = punishments.stream()
                            .filter(p -> p.getType() == filterType)
                            .toList();
                }
            }

            int totalPages = (int) Math.ceil((double) filtered.size() / ENTRIES_PER_PAGE);
            int startIndex = (finalPage - 1) * ENTRIES_PER_PAGE;
            int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, filtered.size());

            final List<Punishment> pagedResults = filtered.subList(
                    Math.min(startIndex, filtered.size()),
                    Math.min(endIndex, filtered.size())
            );
            final int finalTotalPages = Math.max(1, totalPages);
            final int totalCount = filtered.size();

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.MODLOG_HEADER,
                        "player", displayName));

                if (pagedResults.isEmpty()) {
                    sender.sendMessage(plugin.getLanguageManager().get(MessageKey.MODLOG_EMPTY,
                            "player", displayName));
                } else {
                    for (Punishment p : pagedResults) {
                        sender.sendMessage(formatEntry(p));
                    }
                }

                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.MODLOG_FOOTER,
                        "page", String.valueOf(finalPage),
                        "total", String.valueOf(finalTotalPages)));
            });
        });
    }

    private boolean isFilter(String arg) {
        return arg.equalsIgnoreCase("all") ||
                arg.equalsIgnoreCase("bans") ||
                arg.equalsIgnoreCase("mutes") ||
                arg.equalsIgnoreCase("kicks") ||
                arg.equalsIgnoreCase("warns") ||
                PunishmentType.fromString(arg) != null;
    }

    private Component formatEntry(Punishment p) {
        NamedTextColor typeColor = switch (p.getType()) {
            case BAN, IPBAN -> NamedTextColor.DARK_RED;
            case MUTE -> NamedTextColor.GOLD;
            case KICK -> NamedTextColor.RED;
            case WARN -> NamedTextColor.YELLOW;
        };

        String duration = p.getType().hasDuration() ?
                DurationParser.format(p.getExpiresAt() == -1 ? -1 : p.getExpiresAt() - p.getCreatedAt()) :
                "N/A";

        String status = p.isActive() ? (p.isExpired() ? "Expired" : "Active") : "Removed";

        return Component.text("[" + TimeUtil.formatDate(p.getCreatedAt()) + "] ", NamedTextColor.GRAY)
                .append(Component.text(p.getType().getDisplayName(), typeColor))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(p.getReason(), NamedTextColor.WHITE))
                .append(Component.text(" (by ", NamedTextColor.GRAY))
                .append(Component.text(p.getStaffName(), NamedTextColor.YELLOW))
                .append(Component.text(") - " + duration + " [" + status + "]", NamedTextColor.GRAY));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("all", "bans", "mutes", "kicks", "warns"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}
