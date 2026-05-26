package com.blockforge.moderex.disguise;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.packet.NmsProfileHelper;
import com.blockforge.moderex.disguise.packet.PacketDisguiseInjector;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DisguiseManager {

    private static final String DISGUISE_METADATA = "moderex_disguised";

    private final ModereX plugin;
    private final PacketDisguiseInjector packetInjector;
    private final NmsProfileHelper nmsHelper;
    private final Map<UUID, DisguiseProfile> disguisedPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerProfile> originalProfiles = new ConcurrentHashMap<>();
    // NMS-level GameProfile saved before disguise so we can restore it exactly
    private final Map<UUID, GameProfile> nmsOriginalProfiles = new ConcurrentHashMap<>();

    public DisguiseManager(ModereX plugin) {
        this.plugin = plugin;
        this.packetInjector = new PacketDisguiseInjector(plugin);
        this.nmsHelper = new NmsProfileHelper(plugin);
    }

    public void disguise(Player player, DisguiseProfile profile) {
        UUID uuid = player.getUniqueId();

        if (disguisedPlayers.containsKey(uuid)) {
            undisguise(player);
        }

        // Capture originals BEFORE anything is changed
        String realName = player.getName();
        originalProfiles.put(uuid, player.getPlayerProfile());
        GameProfile originalNms = nmsHelper.get(player);
        if (originalNms != null) nmsOriginalProfiles.put(uuid, originalNms);

        disguisedPlayers.put(uuid, profile);
        player.setMetadata(DISGUISE_METADATA, new FixedMetadataValue(plugin, true));
        packetInjector.addDisguisedPlayer(uuid, profile);

        // Apply profile at Bukkit level (skin, PlayerProfile wrapper)
        applyProfile(player, profile);

        // Swap NMS-level gameProfile so player.getName() returns the disguised name.
        // This runs after setPlayerProfile() so it is not overwritten by Bukkit.
        if (nmsHelper.isReady()) {
            GameProfile disguisedNms = NmsProfileHelper.buildDisguised(uuid, profile.getDisplayName(), originalNms);
            nmsHelper.set(player, disguisedNms);
        }

        // Keep display name in sync for chat plugins that read it
        player.setDisplayName(profile.getDisplayName());

        // Force a respawn for all observers so they pick up the new profile
        respawnForAll(player);

        Msg.send(player, Component.text("§aYou are now disguised as §f" + profile.getDisplayName()));
        if (profile.getSkinName() != null) {
            Msg.send(player, Component.text("§7Skin: §f" + profile.getSkinName()));
        }

        plugin.logDebug("[Disguise] " + profile.getDisplayName() + " (real: " + realName + ") disguised");

        plugin.getActivityLogManager().logDisguiseStart(uuid, realName, profile.getDisplayName(), profile.getRank());

        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) debugger.disguiseActivated(realName, profile.getDisplayName());
    }

    public void undisguise(Player player) {
        UUID uuid = player.getUniqueId();

        DisguiseProfile profile = disguisedPlayers.remove(uuid);
        if (profile == null) return;

        player.removeMetadata(DISGUISE_METADATA, plugin);
        packetInjector.removeDisguisedPlayer(uuid);

        // Restore original profile (name + skin)
        restoreProfile(player);

        // Reset display name
        player.setDisplayName(player.getName());
        player.setPlayerListName(null);

        // Force respawn so observers see the real name/skin
        respawnForAll(player);

        Msg.send(player, Component.text("§aDisguise removed. You are now §f" + player.getName()));

        plugin.logDebug("[Disguise] " + player.getName() + " undisguised");

        plugin.getActivityLogManager().logDisguiseEnd(uuid, player.getName(), profile.getDisplayName());

        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) debugger.disguiseDeactivated(player.getName());
    }

    public void toggleDisguise(Player player, DisguiseProfile profile) {
        if (isDisguised(player)) undisguise(player);
        else disguise(player, profile);
    }

    public boolean isDisguised(Player player) {
        return disguisedPlayers.containsKey(player.getUniqueId());
    }

    public DisguiseProfile getDisguiseProfile(Player player) {
        return disguisedPlayers.get(player.getUniqueId());
    }

    public DisguiseProfile getDisguiseProfile(UUID uuid) {
        return disguisedPlayers.get(uuid);
    }

    public String getDisplayName(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        return profile != null ? profile.getDisplayName() : player.getName();
    }

    public String getRealName(Player player) {
        PlayerProfile original = originalProfiles.get(player.getUniqueId());
        return original != null ? original.getName() : player.getName();
    }

    public Player getPlayerByName(String name) {
        Player direct = Bukkit.getPlayerExact(name);
        if (direct != null) return direct;
        for (Map.Entry<UUID, DisguiseProfile> entry : disguisedPlayers.entrySet()) {
            if (entry.getValue().getDisplayName().equalsIgnoreCase(name)) {
                return Bukkit.getPlayer(entry.getKey());
            }
        }
        return null;
    }

    public List<String> getNameCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        String lower = prefix.toLowerCase();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = getDisplayName(player);
            if (name.toLowerCase().startsWith(lower)) completions.add(name);
        }
        return completions;
    }

    // ── Profile manipulation ────────────────────────────────────────────────

    /**
     * Build a new PlayerProfile with the disguised name.
     * Copies the skin from the target player if skinName is set, otherwise keeps original skin.
     * Called synchronously on the main thread — skin fetch is done via complete(false)
     * (uses cached data) to avoid blocking the main thread.
     */
    private void applyProfile(Player player, DisguiseProfile profile) {
        try {
            // New profile: keep same UUID, use disguised name
            PlayerProfile newProfile = Bukkit.createProfile(player.getUniqueId(), profile.getDisplayName());

            if (profile.getSkinName() != null) {
                // Copy skin from another player (uses Mojang cache if available)
                org.bukkit.OfflinePlayer skinSource = Bukkit.getOfflinePlayer(profile.getSkinName());
                PlayerProfile skinProfile = skinSource.getPlayerProfile();
                skinProfile.complete(false); // false = don't block if not cached
                if (!skinProfile.getProperties().isEmpty()) {
                    newProfile.getProperties().addAll(skinProfile.getProperties());
                } else {
                    // Fall back to original skin while skin loads async
                    PlayerProfile original = originalProfiles.get(player.getUniqueId());
                    if (original != null) newProfile.getProperties().addAll(original.getProperties());

                    // Fetch skin async and re-apply once ready
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        skinProfile.complete(true);
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (isDisguised(player)) {
                                PlayerProfile updated = Bukkit.createProfile(player.getUniqueId(), profile.getDisplayName());
                                updated.getProperties().addAll(skinProfile.getProperties());
                                player.setPlayerProfile(updated);
                                respawnForAll(player);
                            }
                        });
                    });
                }
            } else {
                // No skin change — keep original skin but use new name
                PlayerProfile original = originalProfiles.get(player.getUniqueId());
                if (original != null) newProfile.getProperties().addAll(original.getProperties());
            }

            // Applying the profile changes player.getName() in Paper 1.19+
            player.setPlayerProfile(newProfile);
            player.setPlayerListName(profile.getDisplayName());

            plugin.logDebug("[Disguise] Profile applied: name=" + profile.getDisplayName()
                    + " skin=" + profile.getSkinName());
        } catch (Exception e) {
            plugin.logError("Failed to apply profile for " + player.getName(), e);
        }
    }

    private void restoreProfile(Player player) {
        UUID uuid = player.getUniqueId();

        // Restore NMS-level profile first so player.getName() is correct immediately
        GameProfile originalNms = nmsOriginalProfiles.remove(uuid);
        if (originalNms != null && nmsHelper.isReady()) {
            nmsHelper.set(player, originalNms);
        }

        PlayerProfile original = originalProfiles.remove(uuid);
        if (original == null) return;
        try {
            player.setPlayerProfile(original);
            plugin.logDebug("[Disguise] Restored profile for " + player.getName());
        } catch (Exception e) {
            plugin.logError("Failed to restore profile for " + player.getName(), e);
        }
    }

    /**
     * Hide then show the player for all observers so they receive fresh
     * PlayerInfo + AddEntity packets that reflect the updated profile.
     */
    private void respawnForAll(Player player) {
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer.equals(player)) continue;
            observer.hidePlayer(plugin, player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> observer.showPlayer(plugin, player), 2L);
        }
    }

    // ── Session lifecycle ───────────────────────────────────────────────────

    public void onPlayerJoin(Player player) {
        packetInjector.injectPlayer(player);
        if (plugin.getConfig().getBoolean("disguise.restore-on-join", true)) {
            restoreDisguise(player);
        }
        plugin.logDebug("[Disguise] Injected packet filter for " + player.getName());
    }

    public void onPlayerQuit(Player player) {
        saveDisguise(player);
        // Restore real profile before the session ends so the player reconnects normally
        if (isDisguised(player)) {
            restoreProfile(player);
            disguisedPlayers.remove(player.getUniqueId());
            player.removeMetadata(DISGUISE_METADATA, plugin);
            packetInjector.removeDisguisedPlayer(player.getUniqueId());
        }
        packetInjector.removePlayer(player);
        plugin.logDebug("[Disguise] Cleaned up for " + player.getName());
    }

    public void cleanup() {
        for (UUID uuid : new HashSet<>(disguisedPlayers.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) undisguise(player);
        }
        packetInjector.removeAll();
        disguisedPlayers.clear();
        originalProfiles.clear();
        nmsOriginalProfiles.clear();
    }

    public Set<UUID> getDisguisedPlayers() {
        return new HashSet<>(disguisedPlayers.keySet());
    }

    public PacketDisguiseInjector getPacketInjector() {
        return packetInjector;
    }

    // ── Database persistence ────────────────────────────────────────────────

    private void saveDisguise(Player player) {
        UUID uuid = player.getUniqueId();
        DisguiseProfile profile = disguisedPlayers.get(uuid);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (profile == null) {
                    plugin.getDatabaseManager().update(
                            "DELETE FROM moderex_disguise_state WHERE uuid = ?",
                            uuid.toString());
                } else {
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
                            System.currentTimeMillis());
                }
                plugin.logDebug("[Disguise] Saved state for " + player.getName());
            } catch (Exception e) {
                plugin.logError("Failed to save disguise state for " + player.getName(), e);
            }
        });
    }

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
                plugin.logError("Failed to restore disguise for " + player.getName(), e);
            }
        });
    }

    // ── Message helpers ─────────────────────────────────────────────────────

    public Component getDisguisedJoinMessage(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        if (profile == null) return null;
        String format = plugin.getConfig().getString("disguise.join-message-format", "&e{player} joined the game");
        return Component.text(format.replace("{player}", profile.getDisplayName()).replace("&", "§"));
    }

    public Component getDisguisedQuitMessage(Player player) {
        DisguiseProfile profile = getDisguiseProfile(player);
        if (profile == null) return null;
        String format = plugin.getConfig().getString("disguise.quit-message-format", "&e{player} left the game");
        return Component.text(format.replace("{player}", profile.getDisplayName()).replace("&", "§"));
    }
}
