package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.staff.hooks.VanishPluginHookManager;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.vanish.packet.PacketVanishInjector;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class VanishManager {

    private static final String VANISH_METADATA = "moderex_vanished";

    private final ModereX plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final PacketVanishInjector packetInjector;
    private final VanishLevel vanishLevel;
    private final VanishPluginHookManager hookManager;

    public VanishManager(ModereX plugin) {
        this.plugin = plugin;
        this.packetInjector = new PacketVanishInjector(plugin);
        this.vanishLevel = new VanishLevel(plugin);
        this.hookManager = new VanishPluginHookManager(plugin);
        this.hookManager.enableHooks();

        // Warn if LuckPerms features are enabled without LuckPerms
        if (plugin.getConfigManager().getSettings().isVanishLuckPermsEnabled() &&
            !vanishLevel.isLuckPermsAvailable()) {
            plugin.getLogger().warning("=".repeat(60));
            plugin.getLogger().warning("LuckPerms vanish prefix/suffix is enabled but LuckPerms is not installed!");
            plugin.getLogger().warning("Prefix/suffix features will not work.");
            plugin.getLogger().warning("Either install LuckPerms or disable vanish.luckperms.enabled in config.yml");
            plugin.getLogger().warning("=".repeat(60));
        }
    }

    public void toggleVanish(Player player) {
        if (isVanished(player)) {
            unvanish(player);
        } else {
            vanish(player);
        }
    }

    public void vanish(Player player) {
        UUID uuid = player.getUniqueId();

        if (vanishedPlayers.contains(uuid)) {
            return;
        }

        vanishedPlayers.add(uuid);
        player.setMetadata(VANISH_METADATA, new FixedMetadataValue(plugin, true));

        int level = vanishLevel.getVanishLevel(player);
        vanishLevel.storeLevel(uuid, level);

        if (plugin.getConfigManager().getSettings().isVanishFakeMessagesEnabled()) {
            sendFakeLeaveMessage(player);
        }

        hookManager.onVanish(player);

        applyLuckPermsPrefix(player);

        boolean usePacketLevel = plugin.getConfigManager().getSettings().isVanishUsePacketLevel();
        int vanishedEntityId = player.getEntityId();

        if (usePacketLevel) {
            packetInjector.addVanishedEntity(vanishedEntityId);

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player)) {
                    boolean injected = packetInjector.injectPlayer(other);

                    if (!injected && !canSeeVanished(other, player)) {
                        other.hidePlayer(plugin, player);
                        plugin.logDebug("[Vanish] Fallback to hidePlayer for " + other.getName());
                    }
                }
            }

            plugin.logDebug("[Vanish] Packet-level vanishing enabled for " + player.getName() + " (entity ID: " + vanishedEntityId + ", level: " + level + ")");
        } else {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(player) && !canSeeVanished(other, player)) {
                    other.hidePlayer(plugin, player);
                }
            }
        }

        // Remove from tab list if configured
        if (plugin.getConfigManager().getSettings().isVanishHideFromTablist()) {
            player.setPlayerListName("");
        }

        // Silent containers - handled in listener
        // No footsteps - handled in listener

        // Give night vision to see in dark
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));

        if (plugin.getConfigManager().getSettings().isVanishSaveVanishState()) {
            updateVanishStateInDatabase(uuid, true);
        }

        // Send message
        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.VANISH_ENABLED));

        notifyStaffOfVanish(player, true);

        // Notify web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastVanishUpdate(player.getName(), true);
        }

        plugin.logDebug(player.getName() + " vanished (level: " + level + ")");
    }

    public void unvanish(Player player) {
        UUID uuid = player.getUniqueId();

        if (!vanishedPlayers.contains(uuid)) {
            return;
        }

        vanishedPlayers.remove(uuid);
        player.removeMetadata(VANISH_METADATA, plugin);

        if (plugin.getConfigManager().getSettings().isVanishFakeMessagesEnabled()) {
            sendFakeJoinMessage(player);
        }

        hookManager.onUnvanish(player);

        removeLuckPermsPrefix(player);

        int vanishedEntityId = player.getEntityId();
        packetInjector.removeVanishedEntity(vanishedEntityId);

        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }

        plugin.logDebug("[Vanish] Removed vanishing for " + player.getName() + " (entity ID: " + vanishedEntityId + ")");

        player.setPlayerListName(null);

        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        vanishLevel.removeLevel(uuid);

        if (plugin.getConfigManager().getSettings().isVanishSaveVanishState()) {
            updateVanishStateInDatabase(uuid, false);
        }

        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.VANISH_DISABLED));

        notifyStaffOfVanish(player, false);

        // Notify web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastVanishUpdate(player.getName(), false);
        }

        plugin.logDebug(player.getName() + " unvanished");
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }

    public void onPlayerJoin(Player player) {
        boolean usePacketLevel = plugin.getConfigManager().getSettings().isVanishUsePacketLevel();

        if (usePacketLevel) {
            boolean injected = packetInjector.injectPlayer(player);

            if (!injected) {
                plugin.logDebug("[Vanish] Failed to inject packet filter for joining player " + player.getName() + ", using hidePlayer fallback");
            } else {
                plugin.logDebug("[Vanish] Injected packet filter for joining player " + player.getName());
            }

            if (!injected) {
                for (UUID vanishedUuid : vanishedPlayers) {
                    Player vanished = Bukkit.getPlayer(vanishedUuid);
                    if (vanished != null && !canSeeVanished(player, vanished)) {
                        player.hidePlayer(plugin, vanished);
                    }
                }
            }
        } else {
            for (UUID vanishedUuid : vanishedPlayers) {
                Player vanished = Bukkit.getPlayer(vanishedUuid);
                if (vanished != null && !canSeeVanished(player, vanished)) {
                    player.hidePlayer(plugin, vanished);
                }
            }
        }

        if (plugin.getConfigManager().getSettings().isVanishSaveVanishState()) {
            restoreVanishState(player);
        }
    }

    public void onPlayerQuit(Player player) {
        packetInjector.removePlayer(player);

        if (plugin.getConfigManager().getSettings().isVanishSaveVanishState()) {
            saveVanishState(player);
        }

        if (!plugin.getConfigManager().getSettings().isVanishSaveVanishState()) {
            vanishedPlayers.remove(player.getUniqueId());
            vanishLevel.removeLevel(player.getUniqueId());
        }

        plugin.logDebug("[Vanish] Cleaned up vanish data for " + player.getName());
    }

    public boolean shouldHideFromCount(Player player) {
        return isVanished(player);
    }

    public int getVisiblePlayerCount() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isVanished(player)) {
                count++;
            }
        }
        return count;
    }

    public boolean isSilentContainersEnabled() {
        return plugin.getConfigManager().getSettings().isVanishSilentContainers();
    }

    public boolean isNoFootstepsEnabled() {
        return plugin.getConfigManager().getSettings().isVanishNoFootsteps();
    }

    private void notifyStaff(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moderex.command.vanish")) {
                player.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(net.kyori.adventure.text.Component.text(message)));
            }
        }
    }

    /**
     * Notify staff about vanish state change with level-based visibility.
     */
    private void notifyStaffOfVanish(Player target, boolean vanishing) {
        String message = vanishing
            ? target.getName() + " is now vanished."
            : target.getName() + " is no longer vanished.";

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.equals(target)) continue;

            if (vanishing && canSeeVanished(staff, target)) {
                staff.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(Component.text(message)));
            } else if (!vanishing && staff.hasPermission("moderex.command.vanish")) {
                staff.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(Component.text(message)));
            }
        }
    }

    /**
     * Check if observer can see vanished target.
     */
    public boolean canSeeVanished(Player observer, Player target) {
        if (!isVanished(target)) {
            return true;
        }
        return vanishLevel.canSee(observer, target);
    }

    /**
     * Send fake join message to all players.
     */
    private void sendFakeJoinMessage(Player player) {
        String message = plugin.getConfigManager().getSettings().getVanishFakeJoinMessage()
                .replace("{player}", player.getName());
        Component component = TextUtil.parse(message);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("moderex.command.vanish")) {
                online.sendMessage(component);
            }
        }

        plugin.logDebug("[Vanish] Sent fake join message for " + player.getName());
    }

    /**
     * Send fake leave message to all players.
     */
    private void sendFakeLeaveMessage(Player player) {
        String message = plugin.getConfigManager().getSettings().getVanishFakeLeaveMessage()
                .replace("{player}", player.getName());
        Component component = TextUtil.parse(message);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("moderex.command.vanish")) {
                online.sendMessage(component);
            }
        }

        plugin.logDebug("[Vanish] Sent fake leave message for " + player.getName());
    }

    /**
     * Apply LuckPerms prefix/suffix to vanished player.
     */
    private void applyLuckPermsPrefix(Player player) {
        if (!plugin.getConfigManager().getSettings().isVanishLuckPermsEnabled()) {
            return;
        }

        try {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
                return;
            }

            net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(player.getUniqueId());

            if (user == null) return;

            String prefix = plugin.getConfigManager().getSettings().getVanishLuckPermsVanishPrefix();
            String suffix = plugin.getConfigManager().getSettings().getVanishLuckPermsVanishSuffix();

            String originalPrefix = user.getCachedData().getMetaData().getPrefix();
            String originalSuffix = user.getCachedData().getMetaData().getSuffix();

            // Set temporary prefix/suffix
            if (!prefix.isEmpty()) {
                user.data().add(net.luckperms.api.node.types.PrefixNode.builder(prefix, 100).build());
            }
            if (!suffix.isEmpty()) {
                user.data().add(net.luckperms.api.node.types.SuffixNode.builder(suffix, 100).build());
            }

            luckPerms.getUserManager().saveUser(user);

            plugin.logDebug("[Vanish] Applied LuckPerms prefix/suffix to " + player.getName());
        } catch (Exception e) {
            plugin.logDebug("[Vanish] Failed to apply LuckPerms prefix/suffix: " + e.getMessage());
        }
    }

    /**
     * Remove LuckPerms prefix/suffix from player.
     */
    private void removeLuckPermsPrefix(Player player) {
        if (!plugin.getConfigManager().getSettings().isVanishLuckPermsEnabled()) {
            return;
        }

        try {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
                return;
            }

            net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(player.getUniqueId());

            if (user == null) return;

            user.data().clear(node -> {
                if (node instanceof net.luckperms.api.node.types.PrefixNode) {
                    return ((net.luckperms.api.node.types.PrefixNode) node).getPriority() == 100;
                } else if (node instanceof net.luckperms.api.node.types.SuffixNode) {
                    return ((net.luckperms.api.node.types.SuffixNode) node).getPriority() == 100;
                }
                return false;
            });

            luckPerms.getUserManager().saveUser(user);

            plugin.logDebug("[Vanish] Removed LuckPerms prefix/suffix from " + player.getName());
        } catch (Exception e) {
            plugin.logDebug("[Vanish] Failed to remove LuckPerms prefix/suffix: " + e.getMessage());
        }
    }

    /**
     * Get list of vanished players visible to the observer.
     */
    public List<Player> getVisibleVanishedPlayers(Player observer) {
        return vanishedPlayers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(vanished -> canSeeVanished(observer, vanished))
                .collect(Collectors.toList());
    }

    /**
     * Get vanish level for a player.
     */
    public int getVanishLevel(Player player) {
        return vanishLevel.getVanishLevel(player);
    }

    /**
     * Get stored vanish level for a UUID.
     */
    public int getStoredVanishLevel(UUID uuid) {
        return vanishLevel.getStoredLevel(uuid);
    }

    /**
     * Get vanish duration for a player.
     */
    public String getVanishDuration(UUID uuid) {
        return vanishLevel.formatDuration(vanishLevel.getVanishDuration(uuid));
    }

    /**
     * Get the vanish level manager.
     */
    public VanishLevel getVanishLevelManager() {
        return vanishLevel;
    }

    /**
     * Get the plugin hook manager.
     */
    public VanishPluginHookManager getHookManager() {
        return hookManager;
    }

    /**
     * Get the packet vanish injector for debug/admin purposes.
     * @return The packet injector instance
     */
    public PacketVanishInjector getPacketInjector() {
        return packetInjector;
    }

    /**
     * Save vanish state to database.
     */
    private void saveVanishState(Player player) {
        UUID uuid = player.getUniqueId();
        boolean vanished = isVanished(player);
        int level = vanishLevel.getStoredLevel(uuid);
        long now = System.currentTimeMillis();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_vanish_state (uuid, vanished, vanish_level, vanish_time, updated_at)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET
                            vanished = excluded.vanished,
                            vanish_level = excluded.vanish_level,
                            updated_at = excluded.updated_at
                        """,
                        uuid.toString(),
                        vanished,
                        level,
                        now,
                        now
                );
                plugin.logDebug("[Vanish] Saved vanish state for " + player.getName() + " (vanished: " + vanished + ")");
            } catch (Exception e) {
                plugin.logError("Failed to save vanish state for " + player.getName(), e);
            }
        });
    }

    /**
     * Restore vanish state from database.
     */
    private void restoreVanishState(Player player) {
        UUID uuid = player.getUniqueId();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Boolean wasVanished = plugin.getDatabaseManager().query("""
                        SELECT vanished, vanish_level FROM moderex_vanish_state
                        WHERE uuid = ?
                        """,
                        rs -> {
                            if (rs.next()) {
                                return rs.getBoolean("vanished");
                            }
                            return null;
                        },
                        uuid.toString()
                );

                if (wasVanished != null && wasVanished) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        vanish(player);
                        plugin.logDebug("[Vanish] Restored vanish state for " + player.getName());
                    });
                }
            } catch (Exception e) {
                plugin.logError("Failed to restore vanish state for " + player.getName(), e);
            }
        });
    }

    /**
     * Update vanish state in database (for when player manually toggles vanish).
     */
    private void updateVanishStateInDatabase(UUID uuid, boolean vanished) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                long now = System.currentTimeMillis();
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_vanish_state (uuid, vanished, vanish_level, vanish_time, updated_at)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET
                            vanished = excluded.vanished,
                            updated_at = excluded.updated_at
                        """,
                        uuid.toString(),
                        vanished,
                        1,
                        now,
                        now
                );
            } catch (Exception e) {
                plugin.logError("Failed to update vanish state in database", e);
            }
        });
    }
}
