/**
 * Under very heavy maintenance
 */

package com.blockforge.moderex.disguise;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.packet.PacketDisguiseInjector;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player disguises at the packet level.
 * Allows staff to completely impersonate other players or fake names.
 */
public class DisguiseManager {

    private static final String DISGUISE_METADATA = "moderex_disguised";

    private final ModereX plugin;
    private final PacketDisguiseInjector packetInjector;
    private final Map<UUID, DisguiseProfile> disguisedPlayers = new ConcurrentHashMap<>();

    public DisguiseManager(ModereX plugin) {
        this.plugin = plugin;
        this.packetInjector = new PacketDisguiseInjector(plugin);
    }

    /**
     * Disguise a player with a profile.
     *
     * @param player The player to disguise
     * @param profile The disguise profile (name, skin, etc.)
     */
    public void disguise(Player player, DisguiseProfile profile) {
        UUID uuid = player.getUniqueId();

        if (disguisedPlayers.containsKey(uuid)) {
            undisguise(player);
        }

        disguisedPlayers.put(uuid, profile);
        player.setMetadata(DISGUISE_METADATA, new FixedMetadataValue(plugin, true));

        // Apply packet-level disguise
        packetInjector.addDisguisedPlayer(uuid, profile);

        // Update tab list name
        player.setPlayerListName(profile.getDisplayName());

        // Update player's display name
        player.setDisplayName(profile.getDisplayName());
        player.setPlayerListName(profile.getDisplayName());
        player.setCustomName(profile.getDisplayName());
        player.setCustomNameVisible(false);

        // Refresh player for all online players with skin change
        refreshPlayerForAll(player, profile);

        // Send confirmation message
        Msg.send(player, Component.text("§aYou are now disguised as §f" + profile.getDisplayName()));
        if (profile.getSkinName() != null) {
            Msg.send(player, Component.text("§7Skin: §f" + profile.getSkinName()));
        }

        plugin.logDebug("[Disguise] " + player.getName() + " disguised as " + profile.getDisplayName());

        // Web panel debug
        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) {
            debugger.disguiseActivated(player.getName(), profile.getDisplayName());
        }
    }

    /**
     * Remove disguise from a player.
     *
     * @param player The player to undisguise
     */
    public void undisguise(Player player) {
        UUID uuid = player.getUniqueId();

        DisguiseProfile profile = disguisedPlayers.remove(uuid);
        if (profile == null) {
            return;
        }

        player.removeMetadata(DISGUISE_METADATA, plugin);

        // Remove packet-level disguise
        packetInjector.removeDisguisedPlayer(uuid);

        // Reset tab list name
        player.setPlayerListName(null);

        // Refresh player for all online players
        refreshPlayerForAll(player);

        // Send confirmation message
        Msg.send(player, Component.text("§aDisguise removed. You are now §f" + player.getName()));

        plugin.logDebug("[Disguise] " + player.getName() + " undisguised");

        // Web panel debug
        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) {
            debugger.disguiseDeactivated(player.getName());
        }
    }

    /**
     * Toggle disguise for a player using a profile.
     *
     * @param player The player
     * @param profile The disguise profile
     */
    public void toggleDisguise(Player player, DisguiseProfile profile) {
        if (isDisguised(player)) {
            undisguise(player);
        } else {
            disguise(player, profile);
        }
    }

    /**
     * Check if a player is disguised.
     *
     * @param player The player to check
     * @return true if disguised
     */
    public boolean isDisguised(Player player) {
        return disguisedPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Get the disguise profile for a player.
     *
     * @param player The player
     * @return The profile, or null if not disguised
     */
    public DisguiseProfile getDisguiseProfile(Player player) {
        return disguisedPlayers.get(player.getUniqueId());
    }

    /**
     * Get the disguise profile by UUID.
     *
     * @param uuid The player UUID
     * @return The profile, or null if not disguised
     */
    public DisguiseProfile getDisguiseProfile(UUID uuid) {
        return disguisedPlayers.get(uuid);
    }

    /**
     * Get the display name for a player (disguised name or real name).
     *
     * @param player The player
     * @return The display name
     */
    public String getDisplayName(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        return profile != null ? profile.getDisplayName() : player.getName();
    }

    /**
     * Find a player by their real or disguised name.
     *
     * @param name The name to search for
     * @return The player, or null if not found
     */
    public Player getPlayerByName(String name) {
        // Check real names first
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player;
        }

        // Check disguised names
        for (Map.Entry<UUID, DisguiseProfile> entry : disguisedPlayers.entrySet()) {
            if (entry.getValue().getDisplayName().equalsIgnoreCase(name)) {
                return Bukkit.getPlayer(entry.getKey());
            }
        }

        return null;
    }

    /**
     * Get all possible name completions (real + disguised names).
     *
     * @param prefix The prefix to match
     * @return List of matching names
     */
    public List<String> getNameCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String displayName = getDisplayName(player);
            if (displayName.toLowerCase().startsWith(lowerPrefix)) {
                completions.add(displayName);
            }
        }

        return completions;
    }

    /**
     * Refresh a player's entity for all online players.
     * This makes the disguise take effect immediately.
     *
     * @param player The player to refresh
     * @param profile The disguise profile
     */
    private void refreshPlayerForAll(Player player, DisguiseProfile profile) {
        // Apply skin change using reflection
        applySkinChange(player, profile);

        // Remove and re-add the player entity for all other players
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                // Hide and show to force entity refresh
                other.hidePlayer(plugin, player);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    other.showPlayer(plugin, player);
                }, 2L);
            }
        }
    }

    /**
     * Refresh a player's entity for all online players (without profile).
     * This resets the player back to their original skin.
     *
     * @param player The player to refresh
     */
    private void refreshPlayerForAll(Player player) {
        // Reset to original skin
        resetSkin(player);

        // Remove and re-add the player entity for all other players
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) {
                // Hide and show to force entity refresh
                other.hidePlayer(plugin, player);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    other.showPlayer(plugin, player);
                }, 2L);
            }
        }
    }

    /**
     * Reset player skin to their original.
     *
     * @param player The player
     */
    private void resetSkin(Player player) {
        try {
            // Get the player's original profile using Paper's API
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            com.destroystokyo.paper.profile.PlayerProfile originalProfile = offlinePlayer.getPlayerProfile();

            // Complete the profile to fetch original skin data
            originalProfile.complete(true);

            // Get current profile and reset properties
            com.destroystokyo.paper.profile.PlayerProfile playerProfile = player.getPlayerProfile();
            playerProfile.getProperties().clear();
            playerProfile.getProperties().addAll(originalProfile.getProperties());

            // Update player profile
            player.setPlayerProfile(playerProfile);

            plugin.logDebug("[Disguise] Reset skin for " + player.getName());
        } catch (Exception e) {
            plugin.logError("Failed to reset skin for " + player.getName(), e);
        }
    }

    /**
     * Apply skin change to player using Paper's PlayerProfile API.
     *
     * @param player The player
     * @param profile The disguise profile
     */
    private void applySkinChange(Player player, DisguiseProfile profile) {
        try {
            // Use Paper's PlayerProfile API to change skin
            com.destroystokyo.paper.profile.PlayerProfile playerProfile = player.getPlayerProfile();

            // Get skin from another player's profile
            if (profile.getSkinName() != null) {
                org.bukkit.OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(profile.getSkinName());
                com.destroystokyo.paper.profile.PlayerProfile targetProfile = targetPlayer.getPlayerProfile();

                // Complete the profile to fetch skin data
                targetProfile.complete(true);

                // Copy skin properties
                playerProfile.getProperties().clear();
                playerProfile.getProperties().addAll(targetProfile.getProperties());

                // Update player profile
                player.setPlayerProfile(playerProfile);

                plugin.logDebug("[Disguise] Applied skin from " + profile.getSkinName() + " to " + player.getName());
            }
        } catch (Exception e) {
            plugin.logError("Failed to apply skin change for " + player.getName(), e);
        }
    }

    /**
     * Handle player join.
     *
     * @param player The joining player
     */
    public void onPlayerJoin(Player player) {
        // Inject packet filter for this player
        packetInjector.injectPlayer(player);

        // Restore disguise if enabled
        restoreDisguise(player);

        plugin.logDebug("[Disguise] Injected packet filter for " + player.getName());
    }

    /**
     * Handle player quit.
     *
     * @param player The quitting player
     */
    public void onPlayerQuit(Player player) {
        // Save disguise state
        saveDisguise(player);

        // Remove packet filter
        packetInjector.removePlayer(player);

        plugin.logDebug("[Disguise] Cleaned up disguise data for " + player.getName());
    }

    /**
     * Clean up all disguises.
     */
    public void cleanup() {
        for (UUID uuid : new HashSet<>(disguisedPlayers.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                undisguise(player);
            }
        }

        packetInjector.removeAll();
        disguisedPlayers.clear();
    }

    /**
     * Get the packet injector.
     *
     * @return The packet injector
     */
    public PacketDisguiseInjector getPacketInjector() {
        return packetInjector;
    }

    /**
     * Get all disguised players.
     *
     * @return Set of disguised player UUIDs
     */
    public Set<UUID> getDisguisedPlayers() {
        return new HashSet<>(disguisedPlayers.keySet());
    }

    /**
     * Save disguise state to database.
     *
     * @param player The player
     */
    private void saveDisguise(Player player) {
        UUID uuid = player.getUniqueId();
        DisguiseProfile profile = disguisedPlayers.get(uuid);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (profile == null) {
                    // Remove saved disguise
                    plugin.getDatabaseManager().update("""
                            DELETE FROM moderex_disguise_state WHERE uuid = ?
                            """,
                            uuid.toString()
                    );
                } else {
                    // Save disguise
                    plugin.getDatabaseManager().update("""
                            INSERT INTO moderex_disguise_state (uuid, display_name, skin_name, rank, created_at)
                            VALUES (?, ?, ?, ?, ?)
                            ON CONFLICT(uuid) DO UPDATE SET
                                display_name = excluded.display_name,
                                skin_name = excluded.skin_name,
                                rank = excluded.rank,
                                created_at = excluded.created_at
                            """,
                            uuid.toString(),
                            profile.getDisplayName(),
                            profile.getSkinName(),
                            profile.getRank(),
                            System.currentTimeMillis()
                    );
                }
                plugin.logDebug("[Disguise] Saved disguise state for " + player.getName());
            } catch (Exception e) {
                plugin.logError("Failed to save disguise state for " + player.getName(), e);
            }
        });
    }

    /**
     * Restore disguise state from database.
     *
     * @param player The player
     */
    private void restoreDisguise(Player player) {
        UUID uuid = player.getUniqueId();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                DisguiseProfile profile = plugin.getDatabaseManager().query("""
                        SELECT display_name, skin_name, rank FROM moderex_disguise_state
                        WHERE uuid = ?
                        """,
                        rs -> {
                            if (rs.next()) {
                                return new DisguiseProfile(
                                        rs.getString("display_name"),
                                        rs.getString("skin_name"),
                                        rs.getString("rank")
                                );
                            }
                            return null;
                        },
                        uuid.toString()
                );

                if (profile != null) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        disguise(player, profile);
                        plugin.logDebug("[Disguise] Restored disguise for " + player.getName());
                    });
                }
            } catch (Exception e) {
                plugin.logError("Failed to restore disguise state for " + player.getName(), e);
            }
        });
    }

    /**
     * Get the join message for a disguised player.
     *
     * @param player The player
     * @return The join message, or null if not disguised
     */
    public Component getDisguisedJoinMessage(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        if (profile == null) return null;

        String format = plugin.getConfig().getString("disguise.join-message-format", "&e{player} joined the game");
        String message = format.replace("{player}", profile.getDisplayName());

        return Component.text(message.replace("&", "§"));
    }

    /**
     * Get the quit message for a disguised player.
     *
     * @param player The player
     * @return The quit message, or null if not disguised
     */
    public Component getDisguisedQuitMessage(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        if (profile == null) return null;

        String format = plugin.getConfig().getString("disguise.quit-message-format", "&e{player} left the game");
        String message = format.replace("{player}", profile.getDisplayName());

        return Component.text(message.replace("&", "§"));
    }
}
