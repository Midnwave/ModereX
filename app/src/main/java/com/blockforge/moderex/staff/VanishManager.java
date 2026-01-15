package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private static final String VANISH_METADATA = "moderex_vanished";

    private final ModereX plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(ModereX plugin) {
        this.plugin = plugin;
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

        // Hide from other players
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player) && !other.hasPermission("moderex.command.vanish")) {
                other.hidePlayer(plugin, player);
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

        // Send message
        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.VANISH_ENABLED));

        // Notify staff
        notifyStaff(player.getName() + " is now vanished.");

        // Notify web panel
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastVanishUpdate(player.getName(), true);
        }

        plugin.logDebug(player.getName() + " vanished");
    }

    public void unvanish(Player player) {
        UUID uuid = player.getUniqueId();

        if (!vanishedPlayers.contains(uuid)) {
            return;
        }

        vanishedPlayers.remove(uuid);
        player.removeMetadata(VANISH_METADATA, plugin);

        // Show to other players
        for (Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }

        // Restore tab list name
        player.setPlayerListName(null);

        // Remove night vision
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        // Send message
        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.VANISH_DISABLED));

        // Notify staff
        notifyStaff(player.getName() + " is no longer vanished.");

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
        // Hide vanished players from this player
        for (UUID vanishedUuid : vanishedPlayers) {
            Player vanished = Bukkit.getPlayer(vanishedUuid);
            if (vanished != null && !player.hasPermission("moderex.command.vanish")) {
                player.hidePlayer(plugin, vanished);
            }
        }

        // If this player was vanished before (re-logging), re-vanish them
        // This would need to be stored in a database for persistence
    }

    public void onPlayerQuit(Player player) {
        // Optionally keep vanish state in database for persistence
        // For now, just clean up
        vanishedPlayers.remove(player.getUniqueId());
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
}
