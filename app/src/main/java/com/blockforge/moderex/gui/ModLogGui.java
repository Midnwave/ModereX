package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModLogGui extends PaginatedGui<Punishment> {

    private PunishmentType filterType = null;
    private boolean activeOnly = false;
    private List<Punishment> cachedPunishments = new ArrayList<>();
    private UUID filterPlayerUuid = null;
    private String filterPlayerName = null;

    public ModLogGui(ModereX plugin) {
        super(plugin, "<gradient:#9b59b6:#8e44ad><bold>MODERATION LOG</bold></gradient>", 6);
        loadPunishments();
    }

    public ModLogGui(ModereX plugin, UUID playerUuid, String playerName) {
        super(plugin, "<gradient:#9b59b6:#8e44ad><bold>HISTORY</bold></gradient> <gray>-</gray> <white>" + playerName, 6);
        this.filterPlayerUuid = playerUuid;
        this.filterPlayerName = playerName;
        loadPunishments();
    }

    private void loadPunishments() {
        plugin.getPunishmentManager().getRecentPunishments(200)
                .thenAccept(punishments -> {
                    var stream = punishments.stream();

                    if (filterPlayerUuid != null) {
                        stream = stream.filter(p -> p.getPlayerUuid().equals(filterPlayerUuid));
                    }

                    if (filterType != null) {
                        stream = stream.filter(p -> p.getType() == filterType);
                    }

                    if (activeOnly) {
                        stream = stream.filter(p -> p.isActive() && !p.isExpired());
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
        setItem(slot, item, clickType -> handlePunishmentClick(punishment, clickType));
    }

    private void handlePunishmentClick(Punishment punishment, ClickType clickType) {
        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            // Shift-click to revoke
            if (punishment.isActive() && !punishment.isExpired()) {
                if (viewer.hasPermission("moderex.command.unban") ||
                    viewer.hasPermission("moderex.command.unmute")) {
                    revokePunishment(punishment);
                } else {
                    viewer.sendMessage(TextUtil.parse("<red>You don't have permission to revoke punishments."));
                    viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                }
            } else {
                viewer.sendMessage(TextUtil.parse("<yellow>This punishment is already inactive."));
            }
        } else {
            showPunishmentDetails(punishment);
        }
    }

    private void revokePunishment(Punishment punishment) {
        String caseId = punishment.getCaseId();
        UUID staffUuid = viewer.getUniqueId();
        String staffName = viewer.getName();

        viewer.sendMessage(TextUtil.parse("<yellow>Revoking punishment " + caseId + "..."));

        plugin.getPunishmentManager().removePunishmentByCaseId(caseId, staffUuid, staffName, "Revoked via GUI")
                .thenAccept(success -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (success) {
                            viewer.sendMessage(TextUtil.parse("<green>Successfully revoked punishment <white>" + caseId));
                            viewer.playSound(viewer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                            loadPunishments();
                        } else {
                            viewer.sendMessage(TextUtil.parse("<red>Failed to revoke punishment."));
                            viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }
                    });
                });
    }

    @Override
    protected void populate() {
        // Fill with dark glass for cleaner look
        for (int i = 0; i < inventory.getSize(); i++) {
            setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        // Render paginated content
        items = getItems();
        int startIndex = (currentPage - 1) * itemsPerPage;
        for (int i = 0; i < contentSlots.length && startIndex + i < items.size(); i++) {
            Punishment item = items.get(startIndex + i);
            renderItem(contentSlots[i], item);
        }

        // Top row - centered filter buttons
        // Back button (slot 0)
        setItem(0, createItem(Material.ARROW, "<yellow>Back", "<gray>Return to main menu"), () -> {
            plugin.getGuiManager().open(viewer, new StaffSettingsGui(plugin));
        });

        // Filter buttons centered (slots 2-6)
        setItem(2, createFilterButton(null, "All", Material.BOOK), () -> {
            filterType = null;
            loadPunishments();
        });

        setItem(3, createFilterButton(PunishmentType.BAN, "Bans", Material.BARRIER), () -> {
            filterType = PunishmentType.BAN;
            loadPunishments();
        });

        setItem(4, createFilterButton(PunishmentType.MUTE, "Mutes", Material.PAPER), () -> {
            filterType = PunishmentType.MUTE;
            loadPunishments();
        });

        setItem(5, createFilterButton(PunishmentType.KICK, "Kicks", Material.LEATHER_BOOTS), () -> {
            filterType = PunishmentType.KICK;
            loadPunishments();
        });

        setItem(6, createFilterButton(PunishmentType.WARN, "Warns", Material.WRITABLE_BOOK), () -> {
            filterType = PunishmentType.WARN;
            loadPunishments();
        });

        // Active only toggle (slot 8)
        setItem(8, createActiveToggle(), () -> {
            activeOnly = !activeOnly;
            loadPunishments();
        });

        // Navigation in bottom row
        addNavigationButtons();

        // Info item
        int lastRow = rows * 9 - 9;
        List<String> infoLore = new ArrayList<>();
        infoLore.add("<gray>Filter: " + (filterType == null ? "<white>All" : getColorForType(filterType) + filterType.name()));
        infoLore.add("<gray>Active Only: " + (activeOnly ? "<green>Yes" : "<red>No"));
        infoLore.add("<gray>Total: <white>" + cachedPunishments.size() + " records");
        infoLore.add("");
        infoLore.add("<dark_gray>Tip: <italic>Shift-click to revoke</italic>");
        setItem(lastRow, createItem(Material.OAK_SIGN, "<yellow>Filter Info", infoLore));
    }

    @Override
    protected void addNavigationButtons() {
        int totalPages = getTotalPages();
        int lastRow = rows * 9 - 9;

        // Previous page (slot 3)
        if (currentPage > 1) {
            setItem(lastRow + 3, createItem(Material.ARROW,
                    "<green>Previous Page",
                    "<gray>Go to page " + (currentPage - 1)), () -> {
                currentPage--;
                refresh();
            });
        } else {
            setItem(lastRow + 3, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // Page indicator (center, slot 4)
        setItem(lastRow + 4, createItem(Material.PAPER,
                "<gold>Page " + currentPage + "/" + Math.max(1, totalPages),
                "<gray>" + items.size() + " total records"));

        // Next page (slot 5)
        if (currentPage < totalPages) {
            setItem(lastRow + 5, createItem(Material.ARROW,
                    "<green>Next Page",
                    "<gray>Go to page " + (currentPage + 1)), () -> {
                currentPage++;
                refresh();
            });
        } else {
            setItem(lastRow + 5, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // Refresh button (slot 7)
        setItem(lastRow + 7, createItem(Material.SUNFLOWER, "<aqua>Refresh", "<gray>Reload data"), () -> {
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            loadPunishments();
        });

        // Close button (slot 8)
        setItem(lastRow + 8, createItem(Material.BARRIER, "<red>Close", "<gray>Close this menu"), this::close);
    }

    private ItemStack createFilterButton(PunishmentType type, String label, Material defaultMaterial) {
        boolean selected = (type == filterType);

        Material material;
        String color;

        if (type == null) {
            material = selected ? Material.ENCHANTED_BOOK : defaultMaterial;
            color = selected ? "<green>" : "<gray>";
        } else {
            color = getColorForType(type);
            material = selected ? getGlowingMaterial(type) : defaultMaterial;
        }

        List<String> lore = new ArrayList<>();
        if (selected) {
            lore.add("<green>Currently selected");
        } else {
            lore.add("<yellow>Click to filter");
        }

        return createItem(material, color + label, lore);
    }

    private Material getGlowingMaterial(PunishmentType type) {
        return switch (type) {
            case BAN, IPBAN -> Material.RED_STAINED_GLASS;
            case MUTE -> Material.ORANGE_STAINED_GLASS;
            case KICK -> Material.YELLOW_STAINED_GLASS;
            case WARN -> Material.LIGHT_BLUE_STAINED_GLASS;
        };
    }

    private ItemStack createActiveToggle() {
        Material material = activeOnly ? Material.LIME_DYE : Material.GRAY_DYE;
        String title = activeOnly ? "<green>Active Only: ON" : "<red>Active Only: OFF";

        List<String> lore = new ArrayList<>();
        lore.add(activeOnly ? "<gray>Showing only active punishments" : "<gray>Showing all punishments");
        lore.add("");
        lore.add("<yellow>Click to toggle");

        return createItem(material, title, lore);
    }

    private ItemStack createPunishmentItem(Punishment punishment) {
        Material material = getMaterialForType(punishment.getType());
        String color = getColorForType(punishment.getType());

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("<dark_gray>Case ID: <white>" + punishment.getCaseId());
        lore.add("<dark_gray>Type: " + color + punishment.getType().name());
        lore.add("");
        lore.add("<gray>Player: <white>" + punishment.getPlayerName());
        lore.add("<gray>Reason: <white>" + truncate(punishment.getReason(), 28));
        lore.add("<gray>Staff: <white>" + punishment.getStaffName());
        lore.add("<gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt()));

        if (punishment.getType() != PunishmentType.KICK && punishment.getType() != PunishmentType.WARN) {
            if (punishment.getExpiresAt() == -1) {
                lore.add("<gray>Duration: <dark_red>Permanent");
            } else {
                lore.add("<gray>Duration: <white>" +
                        DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt()));
            }
        }

        lore.add("");
        if (punishment.isActive() && !punishment.isExpired()) {
            lore.add("<green><bold>ACTIVE</bold>");
            lore.add("");
            lore.add("<dark_gray>Shift-click to revoke");
        } else if (punishment.isExpired()) {
            lore.add("<yellow>Expired");
        } else {
            lore.add("<red>Revoked");
            if (punishment.getRemovedByName() != null) {
                lore.add("<gray>By: " + punishment.getRemovedByName());
            }
        }

        String title = color + punishment.getPlayerName();
        if (punishment.isActive() && !punishment.isExpired()) {
            title = "<bold>" + title + "</bold>";
        }

        return createItem(material, title, lore);
    }

    private Material getMaterialForType(PunishmentType type) {
        return switch (type) {
            case MUTE -> Material.PAPER;
            case BAN -> Material.BARRIER;
            case KICK -> Material.LEATHER_BOOTS;
            case WARN -> Material.WRITABLE_BOOK;
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
        viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
        viewer.sendMessage(Component.empty());
        viewer.sendMessage(TextUtil.parse("<dark_purple><strikethrough>                                              </strikethrough>"));
        viewer.sendMessage(TextUtil.parse("   <light_purple>Case: <white>" + punishment.getCaseId()));
        viewer.sendMessage(TextUtil.parse("<dark_purple><strikethrough>                                              </strikethrough>"));
        viewer.sendMessage(TextUtil.parse("   <gray>Type: " + getColorForType(punishment.getType()) + punishment.getType().name()));
        viewer.sendMessage(TextUtil.parse("   <gray>Player: <white>" + punishment.getPlayerName()));
        viewer.sendMessage(TextUtil.parse("   <gray>Reason: <white>" + punishment.getReason()));
        viewer.sendMessage(TextUtil.parse("   <gray>Staff: <white>" + punishment.getStaffName()));
        viewer.sendMessage(TextUtil.parse("   <gray>Date: <white>" + TimeUtil.formatDateTime(punishment.getCreatedAt())));

        if (punishment.getType() != PunishmentType.KICK && punishment.getType() != PunishmentType.WARN) {
            if (punishment.getExpiresAt() == -1) {
                viewer.sendMessage(TextUtil.parse("   <gray>Duration: <red>Permanent"));
            } else {
                viewer.sendMessage(TextUtil.parse("   <gray>Duration: <white>" +
                        DurationParser.format(punishment.getExpiresAt() - punishment.getCreatedAt())));
            }
        }

        String status;
        if (punishment.isActive() && !punishment.isExpired()) {
            status = "<green>Active";
        } else if (punishment.isExpired()) {
            status = "<yellow>Expired";
        } else {
            status = "<red>Revoked";
            if (punishment.getRemovedByName() != null) {
                status += " <gray>by " + punishment.getRemovedByName();
            }
        }
        viewer.sendMessage(TextUtil.parse("   <gray>Status: " + status));
        viewer.sendMessage(TextUtil.parse("<dark_purple><strikethrough>                                              </strikethrough>"));
    }
}
