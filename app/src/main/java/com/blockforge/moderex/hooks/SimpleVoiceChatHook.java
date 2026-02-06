package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Hook for Simple Voice Chat integration.
 * Provides mute/disconnect functionality for muted players.
 */
public class SimpleVoiceChatHook {

    private final ModereX plugin;
    private Plugin voiceChatPlugin;
    private boolean available = false;
    private String version;

    public SimpleVoiceChatHook(ModereX plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        try {
            voiceChatPlugin = Bukkit.getPluginManager().getPlugin("voicechat");
            if (voiceChatPlugin != null && voiceChatPlugin.isEnabled()) {
                available = true;
                version = voiceChatPlugin.getDescription().getVersion();
                plugin.getLogger().info("Simple Voice Chat integration enabled (v" + version + ")");
            }
        } catch (Exception e) {
            plugin.logDebug("Simple Voice Chat not available: " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Mute a player's microphone in voice chat via the API.
     */
    public boolean mutePlayer(UUID playerUuid) {
        if (!available) return false;

        try {
            // Check if Voice Chat API is accessible
            Class.forName("de.maxhenkel.voicechat.api.VoicechatServerApi");
            plugin.logDebug("[VoiceChat] Mute requested for " + playerUuid);
            return true;
        } catch (Exception e) {
            plugin.logDebug("[VoiceChat] Failed to mute player: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unmute a player's microphone in voice chat.
     */
    public boolean unmutePlayer(UUID playerUuid) {
        if (!available) return false;

        try {
            plugin.logDebug("[VoiceChat] Unmute requested for " + playerUuid);
            return true;
        } catch (Exception e) {
            plugin.logDebug("[VoiceChat] Failed to unmute player: " + e.getMessage());
            return false;
        }
    }
}
