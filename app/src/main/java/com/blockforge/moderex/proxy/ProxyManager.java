package com.blockforge.moderex.proxy;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ProxyManager implements PluginMessageListener {

    private static final String CHANNEL_BUNGEECORD = "BungeeCord";
    private static final String CHANNEL_MODEREX = "moderex:main";

    private final ModereX plugin;
    private boolean isVelocity;

    public ProxyManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String proxyType = plugin.getConfigManager().getSettings().getProxyType();
        isVelocity = proxyType.equalsIgnoreCase("velocity");

        // Register channels
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_BUNGEECORD);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL_BUNGEECORD, this);

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_MODEREX);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL_MODEREX, this);

        plugin.getLogger().info("Proxy messaging initialized (" + (isVelocity ? "Velocity" : "BungeeCord") + ")");
    }

    public void broadcastPunishment(Punishment punishment) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PUNISHMENT");
        out.writeUTF(punishment.getCaseId());
        out.writeUTF(punishment.getPlayerUuid().toString());
        out.writeUTF(punishment.getPlayerName());
        out.writeUTF(punishment.getType().name());
        out.writeUTF(punishment.getReason() != null ? punishment.getReason() : "");
        out.writeUTF(punishment.getStaffName());
        out.writeLong(punishment.getExpiresAt());
        out.writeBoolean(punishment.isActive());

        sendToAll(out.toByteArray());
    }

    public void broadcastPardon(String playerUuid, String type, String staffName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PARDON");
        out.writeUTF(playerUuid);
        out.writeUTF(type);
        out.writeUTF(staffName);

        sendToAll(out.toByteArray());
    }

    public void broadcastStaffChat(String playerName, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("STAFFCHAT");
        out.writeUTF(playerName);
        out.writeUTF(message);

        sendToAll(out.toByteArray());
    }

    public void broadcastVanish(String playerName, boolean vanished) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("VANISH");
        out.writeUTF(playerName);
        out.writeBoolean(vanished);

        sendToAll(out.toByteArray());
    }

    private void sendToAll(byte[] data) {
        // Use BungeeCord forward channel to send to all servers
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CHANNEL_MODEREX);
        out.writeShort(data.length);
        out.write(data);

        // Send through any online player
        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender != null) {
            sender.sendPluginMessage(plugin, CHANNEL_BUNGEECORD, out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL_MODEREX)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        switch (subchannel) {
            case "PUNISHMENT" -> handlePunishmentMessage(in);
            case "PARDON" -> handlePardonMessage(in);
            case "STAFFCHAT" -> handleStaffChatMessage(in);
            case "VANISH" -> handleVanishMessage(in);
        }
    }

    private void handlePunishmentMessage(ByteArrayDataInput in) {
        // Parse punishment data
        String caseId = in.readUTF();
        String playerUuid = in.readUTF();
        String playerName = in.readUTF();
        String type = in.readUTF();
        String reason = in.readUTF();
        String staffName = in.readUTF();
        long expiresAt = in.readLong();
        boolean active = in.readBoolean();

        // Reload punishment cache for this player if online
        Player target = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
        if (target != null) {
            plugin.getPunishmentManager().loadPlayerPunishments(target.getUniqueId());
        }

        plugin.logDebug("Received punishment sync: " + caseId + " for " + playerName);
    }

    private void handlePardonMessage(ByteArrayDataInput in) {
        String playerUuid = in.readUTF();
        String type = in.readUTF();
        String staffName = in.readUTF();

        // Clear punishment cache for this player
        Player target = Bukkit.getPlayer(java.util.UUID.fromString(playerUuid));
        if (target != null) {
            plugin.getPunishmentManager().loadPlayerPunishments(target.getUniqueId());
        }

        plugin.logDebug("Received pardon sync: " + type + " for " + playerUuid);
    }

    private void handleStaffChatMessage(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        String message = in.readUTF();

        // Don't re-broadcast, just display locally
        // The StaffChatManager handles the display
        plugin.logDebug("Received staff chat from " + playerName + ": " + message);
    }

    private void handleVanishMessage(ByteArrayDataInput in) {
        String playerName = in.readUTF();
        boolean vanished = in.readBoolean();

        plugin.logDebug("Received vanish update: " + playerName + " = " + vanished);
    }

    public boolean isVelocity() {
        return isVelocity;
    }

    public void shutdown() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin);
    }
}
