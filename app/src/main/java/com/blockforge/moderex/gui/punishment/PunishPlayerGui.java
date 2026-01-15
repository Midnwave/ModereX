package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

        // Punishment type options
        // Row 2: Mute, Kick, Warn
        setItem(20, createMuteItem(), () -> openGui(new MuteGui(plugin, target)));
        setItem(22, createKickItem(), () -> openGui(new KickGui(plugin, target)));
        setItem(24, createWarnItem(), () -> openGui(new WarnGui(plugin, target)));

        // Row 3: Ban, IP Ban
        setItem(29, createBanItem(), () -> openGui(new BanGui(plugin, target)));
        setItem(33, createIpBanItem(), () -> openGui(new IpBanGui(plugin, target)));

        // Player history button
        setItem(40, createHistoryItem(), () -> openGui(new PlayerHistoryGui(plugin, target)));

        // Close button
        setItem(44, createCloseButton(), this::close);
    }

    private ItemStack createPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(TextUtil.parse("<yellow>" + targetName));

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

        meta.lore(lore.stream().map(TextUtil::parse).toList());
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createMuteItem() {
        return createItem(Material.PAPER, "<gold>Mute",
                "<gray>Prevent the player from chatting",
                "",
                "<yellow>Left-click <gray>for quick mute",
                "<yellow>Right-click <gray>for custom duration");
    }

    private ItemStack createKickItem() {
        return createItem(Material.LEATHER_BOOTS, "<yellow>Kick",
                "<gray>Remove player from the server",
                "",
                "<yellow>Click <gray>to kick player");
    }

    private ItemStack createWarnItem() {
        return createItem(Material.BOOK, "<aqua>Warn",
                "<gray>Issue a warning to the player",
                "",
                "<yellow>Click <gray>to warn player");
    }

    private ItemStack createBanItem() {
        return createItem(Material.BARRIER, "<red>Ban",
                "<gray>Ban the player from the server",
                "",
                "<yellow>Left-click <gray>for quick ban",
                "<yellow>Right-click <gray>for custom duration");
    }

    private ItemStack createIpBanItem() {
        return createItem(Material.IRON_BARS, "<dark_red>IP Ban",
                "<gray>Ban the player's IP address",
                "",
                "<red>Use with caution!",
                "",
                "<yellow>Click <gray>to IP ban player");
    }

    private ItemStack createHistoryItem() {
        return createItem(Material.WRITABLE_BOOK, "<light_purple>View History",
                "<gray>View punishment history",
                "",
                "<yellow>Click <gray>to view history");
    }
}
