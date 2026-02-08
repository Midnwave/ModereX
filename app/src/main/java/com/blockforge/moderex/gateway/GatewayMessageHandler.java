package com.blockforge.moderex.gateway;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.UUID;

/**
 * Interface for handling messages received from the gateway.
 * Implemented by HybridPanelServer to process panel requests.
 */
public interface GatewayMessageHandler {

    /**
     * Handle an incoming message from the gateway.
     *
     * @param type    The message type (e.g., "GET_PLAYERS", "AUTH_CODE", etc.)
     * @param message The full JSON message including clientId and payload
     */
    void handleMessage(String type, JsonObject message);

    /**
     * Broadcast a message to all connected web panel clients.
     * Used for admin announcements and other global notifications.
     *
     * @param message The JSON message to broadcast
     */
    void broadcastToAllClients(JsonObject message);

    /**
     * Handle a global pre-auth request from the gateway.
     * Creates a session for a user authenticated via global token,
     * allowing them to access this server's panel without a local token.
     *
     * @param clientId    The gateway client ID for routing the response
     * @param uuid        The player's UUID
     * @param username    The player's username
     * @param permissions The player's moderex permissions for this server
     */
    void handleGlobalPreAuth(String clientId, UUID uuid, String username, List<String> permissions);
}
