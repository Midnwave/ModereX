package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.hooks.LuckPermsHook;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffChatManager {

    private final ModereX plugin;
    private final Set<UUID> staffChatToggled = new HashSet<>();

    public StaffChatManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(Player sender, String message) {
        String prefix = "";

        // Get LuckPerms prefix if available
        LuckPermsHook lpHook = plugin.getHookManager().getLuckPermsHook();
        if (lpHook != null) {
            prefix = lpHook.getPrefix(sender);
        }

        Component formattedMessage = plugin.getLanguageManager().get(MessageKey.STAFFCHAT_FORMAT,
                "player", prefix + sender.getName(),
                "message", message
        );

        // Send to all staff members
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moderex.notify.staffchat")) {
                player.sendMessage(formattedMessage);

                // Play sound if enabled
                if (plugin.getConfigManager().getSettings().isStaffChatSoundEnabled()) {
                    playStaffChatSound(player);
                }
            }
        }

        // Log to console
        plugin.getLogger().info("[StaffChat] " + sender.getName() + ": " + message);

        // Notify web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastStaffChat(sender.getName(), message);
        }
    }

    public void broadcastFromWebPanel(String senderName, String message) {
        Component formattedMessage = plugin.getLanguageManager().get(MessageKey.STAFFCHAT_FORMAT,
                "player", "[Web] " + senderName,
                "message", message
        );

        // Send to all staff members
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moderex.notify.staffchat")) {
                player.sendMessage(formattedMessage);

                // Play sound if enabled
                if (plugin.getConfigManager().getSettings().isStaffChatSoundEnabled()) {
                    playStaffChatSound(player);
                }
            }
        }

        // Log to console
        plugin.getLogger().info("[StaffChat] [Web] " + senderName + ": " + message);
    }

    public void sendHelpRequest(Player requester) {
        Component message = plugin.getLanguageManager().get(MessageKey.STAFFCHAT_HELP_REQUEST,
                "player", requester.getName()
        );

        // Send to all staff members
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moderex.notify.staffchat")) {
                player.sendMessage(message);

                // Play urgent sound
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            }
        }

        // Confirm to requester
        requester.sendMessage(TextUtil.parse("<green>Your help request has been sent to staff."));

        // Log
        plugin.getLogger().info("[StaffHelp] " + requester.getName() + " requested help");

        // Notify web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastAlert("Staff Help", requester.getName() + " needs assistance");
        }
    }

    public void toggleStaffChat(Player player) {
        UUID uuid = player.getUniqueId();
        if (staffChatToggled.contains(uuid)) {
            staffChatToggled.remove(uuid);
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.STAFFCHAT_DISABLED));
        } else {
            staffChatToggled.add(uuid);
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.STAFFCHAT_ENABLED));
        }
    }

    public void enableStaffChat(Player player) {
        staffChatToggled.add(player.getUniqueId());
        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.STAFFCHAT_ENABLED));
    }

    public void disableStaffChat(Player player) {
        staffChatToggled.remove(player.getUniqueId());
        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.STAFFCHAT_DISABLED));
    }

    public boolean hasStaffChatToggled(Player player) {
        return staffChatToggled.contains(player.getUniqueId());
    }

    private void playStaffChatSound(Player player) {
        String soundName = plugin.getConfigManager().getSettings().getStaffChatSound();
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Invalid sound, use default
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
        }
    }

    public void onPlayerQuit(Player player) {
        staffChatToggled.remove(player.getUniqueId());
    }
}
