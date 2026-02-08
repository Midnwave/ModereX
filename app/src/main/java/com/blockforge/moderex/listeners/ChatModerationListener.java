package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Handles chat-adjacent moderation events that work on both Paper and Spigot:
 * book editing, anvil renaming, and sign changes for muted players.
 */
public class ChatModerationListener implements Listener {

    private final ModereX plugin;

    public ChatModerationListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBookEdit(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("moderex.bypass.mute")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
            if (mute != null && !mute.isExpired()) {
                event.setCancelled(true);
                String duration = DurationParser.formatRemaining(mute.getExpiresAt());
                Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                        "duration", duration,
                        "reason", mute.getReason()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnvilRename(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (event.getSlot() != 2) {
            return;
        }

        if (!player.hasPermission("moderex.bypass.mute")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
            if (mute != null && !mute.isExpired()) {
                ItemStack result = event.getCurrentItem();
                if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                    event.setCancelled(true);
                    String duration = DurationParser.formatRemaining(mute.getExpiresAt());
                    Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                            "duration", duration,
                            "reason", mute.getReason()));
                }
            }
        }
    }
}
