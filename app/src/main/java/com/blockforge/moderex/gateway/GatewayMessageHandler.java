package com.blockforge.moderex.gateway;

import com.google.gson.JsonObject;

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
}
