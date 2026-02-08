package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.PaginatedGui;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerHistoryGui extends PaginatedGui<Punishment> {

    private final OfflinePlayer target;
    private final UUID targetUuid;
    private List<Punishment> cachedPunishments = new ArrayList<>();
    private boolean loaded = false;

    public PlayerHistoryGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<light_purple>History: <white>" + target.getName(), 6);
        this.target = target;
        this.targetUuid = target.getUniqueId();
    }

    private void loadPunishments() {
        // Load punishments asynchronously and cache them
        plugin.getPunishmentManager().getPunishments(targetUuid)
                .thenAccept(punishments -> {
                    this.cachedPunishments = punishments != null ? punishments : new ArrayList<>();
                    this.loaded = true;
                    // Refresh GUI on main thread if viewer is still viewing
                    if (viewer != null) {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
                                refresh();
                            }
                        });
                    }
                });
    }

    @Override
    protected List<Punishment> getItems() {
        return cachedPunishments;
    }

    @Override
    protected void renderItem(int slot, Punishment punishment) {
        ItemStack item = createPunishmentItem(punishment);
        setItem(slot, item, clickType -> {
            // Show detailed info on click
            showPunishmentDetails(punishment);
        });
    }

    @Override
    protected void populate() {
        super.populate();

        // Trigger async load on first populate (viewer is now set)
        if (!loaded) {
            loadPunishments();

            // Show loading indicator while data is being fetched
            setItem(22, createItem(Material.CLOCK,
                    "<yellow>Loading...",
                    "<gray>Fetching punishment history"));
        }

        // Back button in bottom row
        setItem(45, createBackButton(), () -> openGui(new PunishPlayerGui(plugin, target)));
    }

    private ItemStack createPunishmentItem(Punishment punishment) {
        Material material = getMaterialForType(punishment.getType());
        String color = getColorForType(punishment.getType());

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Case: <white>" + punishment.getCaseId());
        lore.add("<gray>Type: " + color + punishment.getType().name());
        lore.add("<gray>Reason: <white>" + punishment.getReason());
        lore.add("<gray>Staff: <white>" + punishment.getStaffName());
        lore.add("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt()));

        if (punishment.getExpiresAt() == -1) {
            lore.add("<gray>Duration: <red>Permanent");
        } else {
            lore.add("<gray>Duration: <white>" + DurationParser.format(
                    punishment.getExpiresAt() - punishment.getCreatedAt()));
        }

        lore.add("");
        if (punishment.isActive() && !punishment.isExpired()) {
            lore.add("<green>Status: Active");
            if (punishment.getExpiresAt() != -1) {
                lore.add("<gray>Remaining: <white>" + DurationParser.formatRemaining(punishment.getExpiresAt()));
            }
        } else if (punishment.isExpired()) {
            lore.add("<yellow>Status: Expired");
        } else {
            lore.add("<red>Status: Revoked");
            if (punishment.getRemovedByName() != null) {
                lore.add("<gray>Revoked by: <white>" + punishment.getRemovedByName());
            }
        }

        lore.add("");
        lore.add("<yellow>Click for details");

        return createItem(material,
                color + punishment.getType().name() + " <gray>- <white>" + punishment.getCaseId(),
                lore);
    }

    private Material getMaterialForType(PunishmentType type) {
        return switch (type) {
            case MUTE -> Material.PAPER;
            case BAN -> Material.BARRIER;
            case KICK -> Material.LEATHER_BOOTS;
            case WARN -> Material.BOOK;
            case IPBAN -> Material.IRON_BARS;
            case IPMUTE -> Material.CHAIN;
        };
    }

    private String getColorForType(PunishmentType type) {
        return switch (type) {
            case MUTE -> "<gold>";
            case BAN -> "<red>";
            case KICK -> "<yellow>";
            case WARN -> "<aqua>";
            case IPBAN -> "<dark_red>";
            case IPMUTE -> "<dark_purple>";
        };
    }

    private void showPunishmentDetails(Punishment punishment) {
        Msg.send(viewer, TextUtil.parse(""));
        Msg.send(viewer, TextUtil.parse("<gray>═══════════════════════════════════"));
        Msg.send(viewer, TextUtil.parse("<yellow>Punishment Details: <white>" + punishment.getCaseId()));
        Msg.send(viewer, TextUtil.parse("<gray>═══════════════════════════════════"));
        Msg.send(viewer, TextUtil.parse("<gray>Type: <white>" + punishment.getType().name()));
        Msg.send(viewer, TextUtil.parse("<gray>Player: <white>" + punishment.getPlayerName()));
        Msg.send(viewer, TextUtil.parse("<gray>Reason: <white>" + punishment.getReason()));
        Msg.send(viewer, TextUtil.parse("<gray>Staff: <white>" + punishment.getStaffName()));
        Msg.send(viewer, TextUtil.parse("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt())));

        if (punishment.getExpiresAt() == -1) {
            Msg.send(viewer, TextUtil.parse("<gray>Duration: <red>Permanent"));
        } else {
            Msg.send(viewer, TextUtil.parse("<gray>Duration: <white>" +
                    DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt())));
        }

        if (punishment.isActive() && !punishment.isExpired()) {
            Msg.send(viewer, TextUtil.parse("<gray>Status: <green>Active"));
        } else if (punishment.isExpired()) {
            Msg.send(viewer, TextUtil.parse("<gray>Status: <yellow>Expired"));
        } else {
            Msg.send(viewer, TextUtil.parse("<gray>Status: <red>Revoked"));
            if (punishment.getRemovedByName() != null) {
                Msg.send(viewer, TextUtil.parse("<gray>Revoked by: <white>" + punishment.getRemovedByName()));
            }
        }
        Msg.send(viewer, TextUtil.parse("<gray>═══════════════════════════════════"));
        Msg.send(viewer, TextUtil.parse(""));
    }
}
