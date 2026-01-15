package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModLogGui extends PaginatedGui<Punishment> {

    private PunishmentType filterType = null; // null means all types
    private List<Punishment> cachedPunishments = new ArrayList<>();
    private UUID filterPlayerUuid = null;
    private String filterPlayerName = null;

    public ModLogGui(ModereX plugin) {
        super(plugin, "<dark_purple>Moderation Log", 6);
        loadPunishments();
    }

    public ModLogGui(ModereX plugin, UUID playerUuid, String playerName) {
        super(plugin, "<dark_purple>History: " + playerName, 6);
        this.filterPlayerUuid = playerUuid;
        this.filterPlayerName = playerName;
        loadPunishments();
    }

    private void loadPunishments() {
        plugin.getPunishmentManager().getRecentPunishments(100)
                .thenAccept(punishments -> {
                    var stream = punishments.stream();

                    // Filter by player if specified
                    if (filterPlayerUuid != null) {
                        stream = stream.filter(p -> p.getPlayerUuid().equals(filterPlayerUuid));
                    }

                    // Filter by type if specified
                    if (filterType != null) {
                        stream = stream.filter(p -> p.getType() == filterType);
                    }

                    this.cachedPunishments = stream.toList();

                    if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
                        plugin.getServer().getScheduler().runTask(plugin, this::refresh);
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
        setItem(slot, item, clickType -> showPunishmentDetails(punishment));
    }

    @Override
    protected void populate() {
        super.populate();

        // Filter buttons in top row
        setItem(1, createFilterButton(null, "All"), () -> {
            filterType = null;
            loadPunishments();
        });
        setItem(2, createFilterButton(PunishmentType.MUTE, "Mutes"), () -> {
            filterType = PunishmentType.MUTE;
            loadPunishments();
        });
        setItem(3, createFilterButton(PunishmentType.BAN, "Bans"), () -> {
            filterType = PunishmentType.BAN;
            loadPunishments();
        });
        setItem(5, createFilterButton(PunishmentType.KICK, "Kicks"), () -> {
            filterType = PunishmentType.KICK;
            loadPunishments();
        });
        setItem(6, createFilterButton(PunishmentType.WARN, "Warns"), () -> {
            filterType = PunishmentType.WARN;
            loadPunishments();
        });
        setItem(7, createFilterButton(PunishmentType.IPBAN, "IP Bans"), () -> {
            filterType = PunishmentType.IPBAN;
            loadPunishments();
        });
    }

    private ItemStack createFilterButton(PunishmentType type, String label) {
        boolean selected = (type == filterType);
        Material material = selected ? Material.LIME_DYE : Material.GRAY_DYE;

        if (type != null) {
            material = switch (type) {
                case MUTE -> selected ? Material.ORANGE_DYE : Material.PAPER;
                case BAN -> selected ? Material.RED_DYE : Material.BARRIER;
                case KICK -> selected ? Material.YELLOW_DYE : Material.LEATHER_BOOTS;
                case WARN -> selected ? Material.LIGHT_BLUE_DYE : Material.BOOK;
                case IPBAN -> selected ? Material.BLACK_DYE : Material.IRON_BARS;
            };
        }

        return createItem(material,
                (selected ? "<green>" : "<gray>") + label,
                selected ? "<green>Currently viewing" : "<yellow>Click to filter");
    }

    private ItemStack createPunishmentItem(Punishment punishment) {
        Material material = getMaterialForType(punishment.getType());
        String color = getColorForType(punishment.getType());

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Case: <white>" + punishment.getCaseId());
        lore.add("<gray>Player: <white>" + punishment.getPlayerName());
        lore.add("<gray>Type: " + color + punishment.getType().name());
        lore.add("<gray>Reason: <white>" + truncate(punishment.getReason(), 30));
        lore.add("<gray>Staff: <white>" + punishment.getStaffName());
        lore.add("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt()));

        if (punishment.isActive() && !punishment.isExpired()) {
            lore.add("<green>Active");
        } else if (punishment.isExpired()) {
            lore.add("<yellow>Expired");
        } else {
            lore.add("<red>Revoked");
        }

        return createItem(material,
                color + punishment.getPlayerName() + " <gray>- " + punishment.getType().name(),
                lore);
    }

    private Material getMaterialForType(PunishmentType type) {
        return switch (type) {
            case MUTE -> Material.PAPER;
            case BAN -> Material.BARRIER;
            case KICK -> Material.LEATHER_BOOTS;
            case WARN -> Material.BOOK;
            case IPBAN -> Material.IRON_BARS;
        };
    }

    private String getColorForType(PunishmentType type) {
        return switch (type) {
            case MUTE -> "<gold>";
            case BAN -> "<red>";
            case KICK -> "<yellow>";
            case WARN -> "<aqua>";
            case IPBAN -> "<dark_red>";
        };
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private void showPunishmentDetails(Punishment punishment) {
        viewer.sendMessage(TextUtil.parse(""));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse("<yellow>Case: <white>" + punishment.getCaseId()));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse("<gray>Type: <white>" + punishment.getType().name()));
        viewer.sendMessage(TextUtil.parse("<gray>Player: <white>" + punishment.getPlayerName()));
        viewer.sendMessage(TextUtil.parse("<gray>Reason: <white>" + punishment.getReason()));
        viewer.sendMessage(TextUtil.parse("<gray>Staff: <white>" + punishment.getStaffName()));
        viewer.sendMessage(TextUtil.parse("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt())));

        if (punishment.getExpiresAt() == -1) {
            viewer.sendMessage(TextUtil.parse("<gray>Duration: <red>Permanent"));
        } else {
            viewer.sendMessage(TextUtil.parse("<gray>Duration: <white>" +
                    DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt())));
        }

        if (punishment.isActive() && !punishment.isExpired()) {
            viewer.sendMessage(TextUtil.parse("<gray>Status: <green>Active"));
        } else if (punishment.isExpired()) {
            viewer.sendMessage(TextUtil.parse("<gray>Status: <yellow>Expired"));
        } else {
            viewer.sendMessage(TextUtil.parse("<gray>Status: <red>Revoked"));
        }
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
    }
}
