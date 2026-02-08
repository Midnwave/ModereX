package com.blockforge.moderex.webpanel.debug;

/**
 * Categories for debug messages in the ModereX WebPanel.
 * Used to organize and filter debug output.
 */
public enum DebugCategory {

    // Connection & Authentication
    CONNECTION("CONNECTION", "WebSocket and HTTP connection events"),
    AUTH("AUTH", "Authentication and session management"),
    REQUEST("REQUEST", "API request handling and validation"),

    // Core Features
    PUNISHMENT("PUNISHMENT", "Punishment creation, modification, and revocation"),
    AUTOMOD("AUTOMOD", "Automod rule processing and configuration"),
    ANTICHEAT("ANTICHEAT", "Anticheat alerts and rule management"),

    // Staff Features
    STAFF("STAFF", "Staff mode, staff chat, and staff actions"),
    VANISH("VANISH", "Vanish state changes and visibility"),
    DISGUISE("DISGUISE", "Disguise activation and deactivation"),
    STAFFMODE("STAFFMODE", "Staff mode enter/exit and actions"),

    // Player Management
    PLAYER("PLAYER", "Player profile and data operations"),
    WATCHLIST("WATCHLIST", "Watchlist additions, removals, and alerts"),

    // Chat & Communication
    CHAT("CHAT", "Chat messages and filtering"),
    STAFFCHAT("STAFFCHAT", "Staff chat messages"),
    BROADCAST("BROADCAST", "Server-wide broadcasts"),

    // System Operations
    DATABASE("DATABASE", "Database queries and operations"),
    CONFIG("CONFIG", "Configuration loading and saving"),
    PLUGIN("PLUGIN", "Plugin hooks and integrations"),
    WEBPANEL("WEBPANEL", "Web panel server operations"),

    // Data Operations
    TEMPLATE("TEMPLATE", "Punishment template operations"),
    RULES("RULES", "Server rules management"),
    REPLAY("REPLAY", "Replay recording and playback"),

    // Resource & Performance
    RESOURCE("RESOURCE", "Resource pack serving"),
    GEOIP("GEOIP", "GeoIP lookups and location data"),
    API("API", "External API calls"),

    // General
    SUCCESS("SUCCESS", "Successful operations"),
    ERROR("ERROR", "Error events"),
    WARNING("WARNING", "Warning events"),
    INFO("INFO", "General information");

    private final String tag;
    private final String description;

    DebugCategory(String tag, String description) {
        this.tag = tag;
        this.description = description;
    }

    /**
     * Get the short tag for this category (e.g., "AUTH", "PUNISHMENT").
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the description of this category.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Format a message with this category's tag.
     */
    public String format(String message) {
        return "[" + tag + "] " + message;
    }
}
