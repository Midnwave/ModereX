package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.punishment.PlayerHistoryGui;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * /history <user> [type] [page] [--gui]
 * Alias: /hist
 *
 * Shows punishment history for a player.
 * Optional type filter: bans, mutes, warnings, kicks, ipbans, ipmutes, all
 * Optional page number for pagination
 * Optional --gui flag to open GUI instead of chat
 */
public class HistoryCommand extends BaseCommand {

    private static final int ITEMS_PER_PAGE = 8;

    public HistoryCommand(ModereX plugin) {
        super(plugin, "moderex.history", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /history <user> [type] [page] [--gui|--chat]");
            sendMessage(sender, "<gray>Types: all, bans, mutes, warnings, kicks, ipbans, ipmutes");
            sendMessage(sender, "<gray>Flags: <white>--gui <gray>- Show in GUI, <white>--chat <gray>- Force chat output");
            return;
        }

        // Parse arguments and flags
        String targetName = null;
        String type = "all";
        int page = 1;
        boolean useGui = false;
        boolean useChat = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("--gui") || arg.equalsIgnoreCase("-g")) {
                useGui = true;
            } else if (arg.equalsIgnoreCase("--chat")) {
                useChat = true;
            } else if (targetName == null) {
                targetName = arg;
            } else if (isValidType(arg.toLowerCase())) {
                type = arg.toLowerCase();
            } else {
                try {
                    page = Integer.parseInt(arg);
                    if (page < 1) page = 1;
                } catch (NumberFormatException ignored) {
                    // Invalid page, ignore
                }
            }
        }

        if (targetName == null) {
            sendMessage(sender, "<red>Please specify a player.");
            return;
        }

        TargetResolver target = new TargetResolver(targetName);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        // Auto-detect Bedrock/Geyser players: default to GUI unless --chat is specified
        if (!useGui && !useChat && sender instanceof Player player) {
            if (plugin.getHookManager() != null && plugin.getHookManager().getGeyserHook() != null
                    && plugin.getHookManager().getGeyserHook().isBedrockPlayer(player)) {
                useGui = true;
            }
        }

        // GUI mode requires player
        if (useGui && !(sender instanceof Player)) {
            sendMessage(sender, "<red>GUI mode requires a player.");
            useGui = false;
        }

        // If GUI mode, open the GUI
        if (useGui && sender instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                PlayerHistoryGui gui = new PlayerHistoryGui(plugin, Bukkit.getOfflinePlayer(target.getUuid()));
                gui.build();
                plugin.getGuiManager().open(player, gui);
            });
            return;
        }

        // Parse type filter
        List<PunishmentType> typeFilter = parseTypeFilter(type);

        // Filter by permission - only show history types the user has permission for
        List<PunishmentType> permittedTypes = getPermittedTypes(sender);
        if (permittedTypes.isEmpty()) {
            sendMessage(sender, "<red>You don't have permission to view any punishment history.");
            return;
        }

        final int finalPage = page;
        final String finalType = type;
        final String displayName = target.getDisplayName();

        plugin.getPunishmentManager().getHistory(target.getUuid(), null).thenAccept(allHistory -> {
            // Filter by type if not "all"
            List<Punishment> history;
            if (typeFilter != null) {
                // Intersect requested types with permitted types
                List<PunishmentType> effectiveFilter = typeFilter.stream()
                        .filter(permittedTypes::contains)
                        .toList();
                if (effectiveFilter.isEmpty()) {
                    sendMessage(sender, "<red>You don't have permission to view " + finalType + " history.");
                    return;
                }
                history = allHistory.stream()
                        .filter(p -> effectiveFilter.contains(p.getType()))
                        .toList();
            } else {
                // Show all permitted types
                history = allHistory.stream()
                        .filter(p -> permittedTypes.contains(p.getType()))
                        .toList();
            }

            if (history.isEmpty()) {
                sendMessage(sender, MessageKey.HISTORY_HEADER,
                        "player", displayName,
                        "filter", finalType);
                sendMessage(sender, MessageKey.HISTORY_EMPTY, "player", displayName);
                return;
            }

            // Calculate pagination
            int totalItems = history.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int currentPage = Math.min(finalPage, totalPages);
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

            // Display header
            Msg.send(sender, TextUtil.parse(""));
            Msg.send(sender, TextUtil.parse("<dark_gray>┌──────────────────┐"));
            Msg.send(sender, TextUtil.parse("<dark_gray>│ <white>Punishment History: <yellow>" + displayName +
                    " <dark_gray>(<white>" + totalItems + " total<dark_gray>)"));
            if (!finalType.equals("all")) {
                Msg.send(sender, TextUtil.parse("<dark_gray>│ <gray>Filter: <white>" + finalType));
            }
            Msg.send(sender, TextUtil.parse("<dark_gray>├──────────────────┤"));

            // Display entries for current page
            for (int i = startIndex; i < endIndex; i++) {
                Punishment p = history.get(i);
                String statusColor = p.isActive() && !p.isExpired() ? "<green>" :
                                    p.isExpired() ? "<yellow>" : "<red>";
                String statusText = p.isActive() && !p.isExpired() ? "Active" :
                                   p.isExpired() ? "Expired" : "Revoked";
                String typeColor = getTypeColor(p.getType());
                String duration = p.isPermanent() ? "<red>Permanent" :
                        "<white>" + DurationParser.format(p.getExpiresAt() - p.getCreatedAt());

                // Clickable entry
                if (sender instanceof Player) {
                    Component entry = TextUtil.parse("<dark_gray>│ " + typeColor + p.getType().name() +
                            " <dark_gray>- <white>" + p.getCaseId() + " " + statusColor + "[" + statusText + "]")
                        .clickEvent(ClickEvent.runCommand("/viewpunishment " + p.getCaseId()))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse(
                            "<gray>Reason: <white>" + (p.getReason() != null ? p.getReason() : "None") + "\n" +
                            "<gray>Staff: <white>" + p.getStaffName() + "\n" +
                            "<gray>Duration: " + duration + "\n" +
                            "<gray>Date: <white>" + TimeUtil.formatDateTime(p.getCreatedAt()) + "\n" +
                            "\n<yellow>Click to view details")));
                    Msg.send(sender, entry);
                } else {
                    Msg.send(sender, TextUtil.parse("<dark_gray>│ " + typeColor + p.getType().name() +
                            " <dark_gray>- <white>" + p.getCaseId() + " " + statusColor + "[" + statusText + "]" +
                            " <dark_gray>- <gray>" + p.getStaffName()));
                }
            }

            Msg.send(sender, TextUtil.parse("<dark_gray>└──────────────────┘"));

            // Pagination footer with clickable navigation
            if (sender instanceof Player && totalPages > 1) {
                Component pagination = TextUtil.parse("<dark_gray>  ");

                // Previous button
                if (currentPage > 1) {
                    pagination = pagination.append(TextUtil.parse("<gray>[<aqua>« Prev<gray>]")
                        .clickEvent(ClickEvent.runCommand("/history " + displayName + " " + finalType + " " + (currentPage - 1)))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Go to page " + (currentPage - 1)))));
                } else {
                    pagination = pagination.append(TextUtil.parse("<dark_gray>[« Prev]"));
                }

                pagination = pagination.append(TextUtil.parse(" <gray>Page <white>" + currentPage + "<gray>/<white>" + totalPages + " "));

                // Next button
                if (currentPage < totalPages) {
                    pagination = pagination.append(TextUtil.parse("<gray>[<aqua>Next »<gray>]")
                        .clickEvent(ClickEvent.runCommand("/history " + displayName + " " + finalType + " " + (currentPage + 1)))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Go to page " + (currentPage + 1)))));
                } else {
                    pagination = pagination.append(TextUtil.parse("<dark_gray>[Next »]"));
                }

                // GUI button
                pagination = pagination.append(TextUtil.parse(" "))
                    .append(TextUtil.parse("<gray>[<yellow>GUI<gray>]")
                        .clickEvent(ClickEvent.runCommand("/history " + displayName + " --gui"))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View in GUI"))));

                Msg.send(sender, pagination);
            } else if (totalPages > 1) {
                sendMessage(sender, "<gray>Page " + currentPage + "/" + totalPages + " - Use /history " + displayName + " " + finalType + " <page>");
            }
            Msg.send(sender, TextUtil.parse(""));
        });
    }

    private List<PunishmentType> parseTypeFilter(String type) {
        if (type.equals("all")) {
            return null; // No filter
        }
        return switch (type) {
            case "bans" -> List.of(PunishmentType.BAN);
            case "mutes" -> List.of(PunishmentType.MUTE);
            case "warnings", "warns" -> List.of(PunishmentType.WARN);
            case "kicks" -> List.of(PunishmentType.KICK);
            case "ipbans" -> List.of(PunishmentType.IPBAN);
            case "ipmutes" -> List.of(PunishmentType.IPMUTE);
            case "ip" -> List.of(PunishmentType.IPBAN, PunishmentType.IPMUTE); // All IP-based
            default -> null;
        };
    }

    private String getTypeColor(PunishmentType type) {
        return switch (type) {
            case BAN -> "<red>";
            case IPBAN -> "<dark_red>";
            case MUTE -> "<gold>";
            case IPMUTE -> "<dark_purple>";
            case KICK -> "<yellow>";
            case WARN -> "<aqua>";
        };
    }

    private boolean isValidType(String type) {
        return Arrays.asList("all", "bans", "mutes", "warnings", "warns", "kicks", "ipbans", "ipmutes", "ip").contains(type);
    }

    /**
     * Get list of punishment types the sender has permission to view history for.
     * Checks for specific permissions like moderex.history.bans, moderex.history.warns, etc.
     * Falls back to moderex.history.* for all types.
     */
    private List<PunishmentType> getPermittedTypes(CommandSender sender) {
        List<PunishmentType> permitted = new ArrayList<>();

        // Check for wildcard permission first
        if (PermissionUtil.hasPermission(sender, "moderex.history.*")) {
            return Arrays.asList(PunishmentType.values());
        }

        // Check individual permissions
        if (PermissionUtil.hasPermission(sender, "moderex.history.bans") || PermissionUtil.hasPermission(sender, "moderex.checkban")) {
            permitted.add(PunishmentType.BAN);
        }
        if (PermissionUtil.hasPermission(sender, "moderex.history.mutes") || PermissionUtil.hasPermission(sender, "moderex.checkmute")) {
            permitted.add(PunishmentType.MUTE);
        }
        if (PermissionUtil.hasPermission(sender, "moderex.history.warns") || PermissionUtil.hasPermission(sender, "moderex.checkwarn")) {
            permitted.add(PunishmentType.WARN);
        }
        if (PermissionUtil.hasPermission(sender, "moderex.history.kicks")) {
            permitted.add(PunishmentType.KICK);
        }
        if (PermissionUtil.hasPermission(sender, "moderex.history.ipbans")) {
            permitted.add(PunishmentType.IPBAN);
        }
        if (PermissionUtil.hasPermission(sender, "moderex.history.ipmutes")) {
            permitted.add(PunishmentType.IPMUTE);
        }

        // Legacy: if they have base moderex.history permission, give them all
        if (permitted.isEmpty() && PermissionUtil.hasPermission(sender, "moderex.history")) {
            return Arrays.asList(PunishmentType.values());
        }

        return permitted;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            // Only show types the player has permission for
            List<String> completions = new ArrayList<>();
            List<PunishmentType> permitted = getPermittedTypes(sender);

            // Always show "all" if they have any permission
            if (!permitted.isEmpty()) {
                completions.add("all");
            }
            if (permitted.contains(PunishmentType.BAN)) {
                completions.add("bans");
            }
            if (permitted.contains(PunishmentType.MUTE)) {
                completions.add("mutes");
            }
            if (permitted.contains(PunishmentType.WARN)) {
                completions.add("warnings");
            }
            if (permitted.contains(PunishmentType.KICK)) {
                completions.add("kicks");
            }
            if (permitted.contains(PunishmentType.IPBAN)) {
                completions.add("ipbans");
            }
            if (permitted.contains(PunishmentType.IPMUTE)) {
                completions.add("ipmutes");
            }
            completions.add("--gui");
            completions.add("--chat");
            return filterCompletions(completions, args[1]);
        }
        if (args.length == 3) {
            List<String> completions = new ArrayList<>();
            completions.add("1");
            completions.add("2");
            completions.add("3");
            completions.add("--gui");
            completions.add("--chat");
            return filterCompletions(completions, args[2]);
        }
        if (args.length == 4) {
            return filterCompletions(List.of("--gui", "--chat"), args[3]);
        }
        return super.tabComplete(sender, args);
    }
}
