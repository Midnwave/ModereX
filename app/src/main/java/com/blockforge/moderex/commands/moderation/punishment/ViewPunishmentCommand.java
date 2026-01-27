package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * View detailed information about a punishment by its case ID.
 * Usage: /viewpunishment <caseId> [--gui]
 * Example: /viewpunishment MX-A1B2C3 --gui
 */
public class ViewPunishmentCommand extends BaseCommand {

    public ViewPunishmentCommand(ModereX plugin) {
        super(plugin, "moderex.command.viewpunishment", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /viewpunishment <caseId> [--gui]");
            sendMessage(sender, "<gray>Example: /viewpunishment MX-A1B2C3");
            sendMessage(sender, "<gray>Flags: <white>--gui <gray>- Show in GUI instead of chat");
            return;
        }

        // Parse flags
        boolean useGui = false;
        String caseId = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("--gui") || arg.equalsIgnoreCase("-g")) {
                useGui = true;
            } else if (caseId == null) {
                caseId = arg.toUpperCase();
            }
        }

        if (caseId == null) {
            sendMessage(sender, "<red>Please specify a case ID.");
            return;
        }

        // Validate case ID format (should be MX-XXXXXX)
        if (!caseId.matches("MX-[A-Z0-9]+")) {
            sendMessage(sender, "<red>Invalid case ID format. Expected format: MX-XXXXXX");
            return;
        }

        // GUI requires player
        if (useGui && !(sender instanceof Player)) {
            sendMessage(sender, "<red>GUI mode requires a player.");
            useGui = false;
        }

        sendMessage(sender, "<gray>Looking up punishment " + caseId + "...");

        final boolean showGui = useGui;
        final String finalCaseId = caseId;
        plugin.getPunishmentManager().getPunishmentByCaseId(finalCaseId).thenAccept(punishment -> {
            if (punishment == null) {
                sendMessage(sender, MessageKey.PUNISHMENT_NOT_FOUND, "id", finalCaseId);
                return;
            }

            if (showGui && sender instanceof Player player) {
                // Open GUI on main thread
                Bukkit.getScheduler().runTask(plugin, () -> openPunishmentGui(player, punishment));
            } else {
                displayPunishment(sender, punishment);
            }
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
        sender.sendMessage(TextUtil.parse("<dark_gray>┌────────────────────────────────────────────┐"));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <white>Punishment Details: <yellow>" + p.getCaseId() + " " + statusColor + "(" + statusText + ")"));
        sender.sendMessage(TextUtil.parse("<dark_gray>├────────────────────────────────────────────┤"));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Type: " + typeColor + p.getType().name()));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Player: <white>" + p.getPlayerName() + " <dark_gray>(" + p.getPlayerUuid().toString().substring(0, 8) + "...)"));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Moderator: <white>" + p.getStaffName()));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Reason: <white>" + (p.getReason() != null ? p.getReason() : "No reason specified")));
        sender.sendMessage(TextUtil.parse("<dark_gray>├────────────────────────────────────────────┤"));
        sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Date: <white>" + TimeUtil.formatDateTime(p.getCreatedAt())));

        if (p.isPermanent()) {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Duration: <red>Permanent"));
        } else {
            long duration = p.getExpiresAt() - p.getCreatedAt();
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Duration: <white>" + DurationParser.format(duration)));
            if (p.isActive() && !p.isExpired()) {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Remaining: <green>" + DurationParser.formatRemaining(p.getExpiresAt())));
            } else {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Expired: <white>" + TimeUtil.formatDateTime(p.getExpiresAt())));
            }
        }

        if (p.getIpAddress() != null && !p.getIpAddress().isEmpty() &&
            sender.hasPermission("moderex.check.ip")) {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>IP Address: <white>" + p.getIpAddress()));
        }

        if (p.getServer() != null && !p.getServer().isEmpty()) {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Server: <white>" + p.getServer()));
        }

        if (p.isRemoved()) {
            sender.sendMessage(TextUtil.parse("<dark_gray>├────────────────────────────────────────────┤"));
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <red>Revoked by: <white>" + p.getRemovedByName()));
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <red>Revoked at: <white>" + TimeUtil.formatDateTime(p.getRemovedAt())));
            if (p.getRemovedReason() != null && !p.getRemovedReason().isEmpty()) {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <red>Reason: <white>" + p.getRemovedReason()));
            }
        }

        sender.sendMessage(TextUtil.parse("<dark_gray>└────────────────────────────────────────────┘"));

        // Add clickable action buttons for players
        if (sender instanceof Player && sender.hasPermission("moderex.history")) {
            Component buttons = TextUtil.parse("<dark_gray>  ")
                .append(TextUtil.parse("<gray>[<aqua>Player History<gray>]")
                    .clickEvent(ClickEvent.runCommand("/history " + p.getPlayerName()))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View " + p.getPlayerName() + "'s history"))))
                .append(TextUtil.parse(" "))
                .append(TextUtil.parse("<gray>[<yellow>GUI<gray>]")
                    .clickEvent(ClickEvent.runCommand("/viewpunishment " + p.getCaseId() + " --gui"))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View in GUI"))));

            if (p.isActive() && !p.isExpired() && canRevoke(sender, p)) {
                buttons = buttons.append(TextUtil.parse(" "))
                    .append(TextUtil.parse("<gray>[<red>Revoke<gray>]")
                        .clickEvent(ClickEvent.suggestCommand(getRevokeCmdForType(p)))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Click to revoke this punishment"))));
            }

            sender.sendMessage(buttons);
        }
        sender.sendMessage(TextUtil.parse(""));
    }

    private boolean canRevoke(CommandSender sender, Punishment p) {
        return switch (p.getType()) {
            case BAN, IPBAN -> sender.hasPermission("moderex.unban");
            case MUTE, IPMUTE -> sender.hasPermission("moderex.unmute");
            case WARN -> sender.hasPermission("moderex.unwarn");
            case KICK -> false;
        };
    }

    private String getRevokeCmdForType(Punishment p) {
        return switch (p.getType()) {
            case BAN, IPBAN -> "/unban " + p.getCaseId() + " ";
            case MUTE, IPMUTE -> "/unmute " + p.getCaseId() + " ";
            case WARN -> "/unwarn " + p.getCaseId() + " ";
            case KICK -> "";
        };
    }

    /**
     * Open a GUI showing punishment details.
     */
    private void openPunishmentGui(Player player, Punishment p) {
        ViewPunishmentGui gui = new ViewPunishmentGui(plugin, p);
        gui.build();
        plugin.getGuiManager().open(player, gui);
    }

    /**
     * Inner GUI class for displaying punishment details.
     */
    private class ViewPunishmentGui extends BaseGui {
        private final Punishment punishment;

        public ViewPunishmentGui(ModereX plugin, Punishment p) {
            super(plugin, "<dark_gray>Case: <yellow>" + p.getCaseId(), 3);
            this.punishment = p;
        }

        @Override
        protected void populate() {
            // Fill background
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            // Player head (slot 10)
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(punishment.getPlayerUuid()));
            headMeta.displayName(TextUtil.parse("<white>" + punishment.getPlayerName()));
            headMeta.lore(Arrays.asList(
                TextUtil.parse("<gray>UUID: <white>" + punishment.getPlayerUuid().toString().substring(0, 13) + "..."),
                TextUtil.parse(""),
                TextUtil.parse("<yellow>Click to view history")
            ));
            head.setItemMeta(headMeta);
            setItem(10, head, click -> {
                viewer.closeInventory();
                viewer.performCommand("history " + punishment.getPlayerName());
            });

            // Punishment info (slot 13)
            Material typeMat = getMaterialForType(punishment.getType());
            String typeColor = getColorForType(punishment.getType());
            String statusColor = punishment.isActive() && !punishment.isExpired() ? "<green>" :
                                 punishment.isExpired() ? "<yellow>" : "<red>";
            String status = punishment.isActive() && !punishment.isExpired() ? "Active" :
                           punishment.isExpired() ? "Expired" : "Revoked";

            List<String> lore = new ArrayList<>();
            lore.add("<gray>Case ID: <white>" + punishment.getCaseId());
            lore.add("<gray>Status: " + statusColor + status);
            lore.add("");
            lore.add("<gray>Reason: <white>" + (punishment.getReason() != null ? punishment.getReason() : "None"));
            lore.add("<gray>Moderator: <white>" + punishment.getStaffName());
            lore.add("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt()));
            lore.add("");

            if (punishment.isPermanent()) {
                lore.add("<gray>Duration: <red>Permanent");
            } else {
                lore.add("<gray>Duration: <white>" + DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt()));
                if (punishment.isActive() && !punishment.isExpired()) {
                    lore.add("<gray>Remaining: <green>" + DurationParser.formatRemaining(punishment.getExpiresAt()));
                }
            }

            if (punishment.isRemoved()) {
                lore.add("");
                lore.add("<red>Revoked by: <white>" + punishment.getRemovedByName());
                lore.add("<red>Reason: <white>" + (punishment.getRemovedReason() != null ? punishment.getRemovedReason() : "None"));
            }

            ItemStack info = createItem(typeMat, typeColor + punishment.getType().name(), lore);
            setItem(13, info);

            // Staff head (slot 16)
            ItemStack staffHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta staffMeta = (SkullMeta) staffHead.getItemMeta();
            if (punishment.getStaffUuid() != null) {
                staffMeta.setOwningPlayer(Bukkit.getOfflinePlayer(punishment.getStaffUuid()));
            }
            staffMeta.displayName(TextUtil.parse("<gold>Moderator: <white>" + punishment.getStaffName()));
            staffMeta.lore(Arrays.asList(
                TextUtil.parse(""),
                TextUtil.parse("<yellow>Click to view staff history")
            ));
            staffHead.setItemMeta(staffMeta);
            setItem(16, staffHead, click -> {
                viewer.closeInventory();
                viewer.performCommand("staffhistory " + punishment.getStaffName());
            });

            // Revoke button (slot 22) if applicable
            if (punishment.isActive() && !punishment.isExpired() && canRevoke(viewer, punishment)) {
                ItemStack revoke = createItem(Material.BARRIER, "<red>Revoke Punishment",
                    "<gray>Click to revoke this punishment",
                    "<gray>Shift-click to revoke without confirmation");
                setItem(22, revoke, click -> {
                    viewer.closeInventory();
                    if (click == org.bukkit.event.inventory.ClickType.SHIFT_LEFT ||
                        click == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT) {
                        viewer.performCommand(getRevokeCmdForType(punishment).trim() + "Revoked via GUI");
                    } else {
                        viewer.sendMessage(TextUtil.parse("<yellow>Type the revoke command to confirm:"));
                        viewer.sendMessage(TextUtil.parse("<gray>" + getRevokeCmdForType(punishment) + "<reason>"));
                    }
                });
            }
        }

        private Material getMaterialForType(com.blockforge.moderex.punishment.PunishmentType type) {
            return switch (type) {
                case BAN -> Material.BARRIER;
                case IPBAN -> Material.IRON_BARS;
                case MUTE -> Material.PAPER;
                case IPMUTE -> Material.CHAIN;
                case KICK -> Material.LEATHER_BOOTS;
                case WARN -> Material.BOOK;
            };
        }

        private String getColorForType(com.blockforge.moderex.punishment.PunishmentType type) {
            return switch (type) {
                case BAN, IPBAN -> "<red>";
                case MUTE, IPMUTE -> "<gold>";
                case KICK -> "<yellow>";
                case WARN -> "<aqua>";
            };
        }
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
        } else if (args.length == 2) {
            return filterCompletions(List.of("--gui"), args[1]);
        }
        return Collections.emptyList();
    }
}
