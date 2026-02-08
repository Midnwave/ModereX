package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishPlayerGui extends BaseGui {

    private final OfflinePlayer target;
    private final UUID targetUuid;
    private final String targetName;

    public PunishPlayerGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<dark_red>Punish: <red>" + target.getName(), 5);
        this.target = target;
        this.targetUuid = target.getUniqueId();
        this.targetName = target.getName() != null ? target.getName() : "Unknown";
    }

    @Override
    protected void populate() {
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Player head with info in center top
        setItem(4, createPlayerHead());

        // Punishment type options with permission checks
        // Row 2: Mute, Kick, Warn
        setPermissionGuardedItem(20, createMuteItem(), "moderex.mute", () -> openGui(new MuteGui(plugin, target)));
        setPermissionGuardedItem(22, createKickItem(), "moderex.kick", () -> openGui(new KickGui(plugin, target)));
        setPermissionGuardedItem(24, createWarnItem(), "moderex.warn", () -> openGui(new WarnGui(plugin, target)));

        // Row 3: Ban, IP Ban
        setPermissionGuardedItem(29, createBanItem(), "moderex.ban", () -> openGui(new BanGui(plugin, target)));
        setPermissionGuardedItem(33, createIpBanItem(), "moderex.ipban", () -> openGui(new IpBanGui(plugin, target)));

        // Player history button
        setItem(40, createHistoryItem(), () -> openGui(new PlayerHistoryGui(plugin, target)));

        // Close button
        setItem(44, createCloseButton(), this::close);
    }

    /**
     * Sets an item with a permission guard. If the player lacks the permission,
     * clicking shows a brief "No Permission" flash with an error sound.
     */
    private void setPermissionGuardedItem(int slot, ItemStack item, String permission, Runnable action) {
        setItem(slot, item, () -> {
            if (PermissionUtil.hasPermission(viewer, permission)) {
                action.run();
            } else {
                // Play error sound
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

                // Flash a "No Permission" barrier item
                ItemStack noPermItem = createItem(Material.BARRIER, "<red>No Permission",
                        "<gray>You lack <white>" + permission);
                inventory.setItem(slot, noPermItem);

                // Restore original item after 500ms (10 ticks)
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (viewer != null && plugin.getGuiManager().hasGuiOpen(viewer)) {
                        inventory.setItem(slot, item);
                    }
                }, 10L);
            }
        });
    }

    private ItemStack createPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        Msg.displayName(meta, TextUtil.parseLore("<yellow>" + targetName));

        List<String> lore = new ArrayList<>();
        lore.add("<gray>UUID: <white>" + targetUuid);
        lore.add("");

        // Add active punishment info
        Punishment activeMute = plugin.getPunishmentManager().getActivePunishment(targetUuid, PunishmentType.MUTE);
        Punishment activeBan = plugin.getPunishmentManager().getActivePunishment(targetUuid, PunishmentType.BAN);

        if (activeMute != null && !activeMute.isExpired()) {
            lore.add("<red>Currently Muted");
            lore.add("<gray>Reason: <white>" + activeMute.getReason());
        }
        if (activeBan != null && !activeBan.isExpired()) {
            lore.add("<red>Currently Banned");
            lore.add("<gray>Reason: <white>" + activeBan.getReason());
        }

        if (target.isOnline()) {
            lore.add("");
            lore.add("<green>Online");
        } else {
            lore.add("");
            lore.add("<gray>Offline");
        }

        Msg.lore(meta, lore.stream().map(TextUtil::parseLore).toList());
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createMuteItem() {
        return createItem(Material.PAPER, "<gold>Mute",
                "<gray>Prevent the player from chatting",
                "",
                "<yellow>Click to mute");
    }

    private ItemStack createKickItem() {
        return createItem(Material.LEATHER_BOOTS, "<yellow>Kick",
                "<gray>Remove player from the server",
                "",
                "<yellow>Click to kick");
    }

    private ItemStack createWarnItem() {
        return createItem(Material.BOOK, "<aqua>Warn",
                "<gray>Issue a warning to the player",
                "",
                "<yellow>Click to warn");
    }

    private ItemStack createBanItem() {
        return createItem(Material.BARRIER, "<red>Ban",
                "<gray>Ban the player from the server",
                "",
                "<yellow>Click to ban");
    }

    private ItemStack createIpBanItem() {
        return createItem(Material.IRON_BARS, "<dark_red>IP Ban",
                "<gray>Ban the player's IP address",
                "",
                "<yellow>Click to IP ban");
    }

    private ItemStack createHistoryItem() {
        return createItem(Material.WRITABLE_BOOK, "<light_purple>View History",
                "<gray>View punishment history",
                "",
                "<yellow>Click <gray>to view history");
    }
}
