package com.blockforge.moderex.webpanel;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebPanelServer extends WebSocketServer {

    private static final Gson GSON = new Gson();
    private static final long CONNECT_CODE_EXPIRY = 5 * 60 * 1000; // 5 minutes

    private final ModereX plugin;
    private final Set<WebSocket> authenticatedClients = ConcurrentHashMap.newKeySet();
    private final Map<WebSocket, WebPanelSession> sessions = new ConcurrentHashMap<>();

    // Connect code system: code -> PendingConnection
    private final Map<String, PendingConnection> pendingConnections = new ConcurrentHashMap<>();

    // Per-user settings storage
    private final Map<UUID, UserPanelSettings> userSettings = new ConcurrentHashMap<>();

    public WebPanelServer(ModereX plugin) {
        this(plugin, plugin.getConfigManager().getSettings().getWebPanelPort());
    }

    public WebPanelServer(ModereX plugin, int port) {
        super(new InetSocketAddress(port));
        this.plugin = plugin;
        setReuseAddr(true);
        loadUserSettings();
    }

    // ==================== Connect Code System ====================

    public String generateConnectCode(Player player) {
        // Clean expired codes first
        cleanExpiredCodes();

        // Remove any existing code for this player
        pendingConnections.entrySet().removeIf(e ->
            e.getValue().playerUuid.equals(player.getUniqueId()));

        // Generate new code (6 alphanumeric characters)
        String code = generateRandomCode();

        PendingConnection pending = new PendingConnection();
        pending.playerUuid = player.getUniqueId();
        pending.playerName = player.getName();
        pending.createdAt = System.currentTimeMillis();
        pending.hasPermission = player.hasPermission("moderex.webpanel");

        // Get LuckPerms prefix/suffix if available
        if (plugin.getHookManager().isLuckPermsEnabled()) {
            pending.prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(player);
            pending.suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(player);
        }

        pendingConnections.put(code.toUpperCase(), pending);
        return code.toUpperCase();
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Removed confusing chars: I,O,0,1
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void cleanExpiredCodes() {
        long now = System.currentTimeMillis();
        pendingConnections.entrySet().removeIf(e ->
            now - e.getValue().createdAt > CONNECT_CODE_EXPIRY);
    }

    // ==================== WebSocket Events ====================

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        plugin.logDebug("WebSocket connection from: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        authenticatedClients.remove(conn);
        WebPanelSession session = sessions.remove(conn);
        if (session != null) {
            plugin.logDebug("WebSocket disconnected: " + session.playerName);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = GSON.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();

            // Authentication messages
            if (type.equals("AUTH_CONNECT_CODE")) {
                handleConnectCodeAuth(conn, json);
                return;
            }
            if (type.equals("AUTH_CONSOLE")) {
                handleConsoleAuth(conn, json);
                return;
            }

            // All other messages require authentication
            if (!authenticatedClients.contains(conn)) {
                sendError(conn, "NOT_AUTHENTICATED", "You must authenticate first");
                return;
            }

            // Check if user has permission
            WebPanelSession session = sessions.get(conn);
            if (session == null || !session.hasPermission) {
                sendAccessDenied(conn);
                return;
            }

            handleMessage(conn, type, json);
        } catch (Exception e) {
            plugin.logError("Error processing WebSocket message", e);
            sendError(conn, "INVALID_MESSAGE", "Invalid message format");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        plugin.logError("WebSocket error", ex);
    }

    @Override
    public void onStart() {
        plugin.getLogger().info("WebSocket server started on port " +
                plugin.getConfigManager().getSettings().getWebPanelPort());
    }

    // ==================== Authentication Handlers ====================

    private void handleConnectCodeAuth(WebSocket conn, JsonObject json) {
        String code = json.has("code") ? json.get("code").getAsString().toUpperCase().trim() : "";

        cleanExpiredCodes();

        PendingConnection pending = pendingConnections.remove(code);
        if (pending == null) {
            sendError(conn, "INVALID_CODE", "Invalid or expired connect code. Use /mx connect in-game to get a new code.");
            return;
        }

        if (!pending.hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        // Create session
        WebPanelSession session = new WebPanelSession();
        session.playerUuid = pending.playerUuid;
        session.playerName = pending.playerName;
        session.authMethod = "MINECRAFT";
        session.hasPermission = true;
        session.prefix = pending.prefix;
        session.suffix = pending.suffix;
        session.connectedAt = System.currentTimeMillis();

        authenticatedClients.add(conn);
        sessions.put(conn, session);

        // Load user settings
        UserPanelSettings settings = getUserSettings(pending.playerUuid);

        sendAuthSuccess(conn, session, settings);
        plugin.getLogger().info("Web panel authenticated: " + pending.playerName + " (Minecraft)");
    }

    private void handleConsoleAuth(WebSocket conn, JsonObject json) {
        String username = json.has("username") ? json.get("username").getAsString().trim() : "";

        if (username.isEmpty()) {
            sendError(conn, "INVALID_USERNAME", "Username is required");
            return;
        }

        // Look up the player
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            sendError(conn, "UNKNOWN_PLAYER", "Player '" + username + "' not found on this server");
            return;
        }

        // Check permission via LuckPerms if available
        boolean hasPermission = false;
        String prefix = "";
        String suffix = "";

        if (offlinePlayer.isOnline()) {
            Player onlinePlayer = offlinePlayer.getPlayer();
            hasPermission = onlinePlayer.hasPermission("moderex.webpanel");
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(onlinePlayer);
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(onlinePlayer);
            }
        } else {
            // Check via LuckPerms for offline players
            if (plugin.getHookManager().isLuckPermsEnabled()) {
                hasPermission = plugin.getHookManager().getLuckPermsHook()
                    .hasPermission(offlinePlayer.getUniqueId(), "moderex.webpanel");
                prefix = plugin.getHookManager().getLuckPermsHook().getPrefix(offlinePlayer.getUniqueId());
                suffix = plugin.getHookManager().getLuckPermsHook().getSuffix(offlinePlayer.getUniqueId());
            }
        }

        if (!hasPermission) {
            sendAccessDenied(conn);
            return;
        }

        // Create session
        WebPanelSession session = new WebPanelSession();
        session.playerUuid = offlinePlayer.getUniqueId();
        session.playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : username;
        session.authMethod = "CONSOLE";
        session.hasPermission = true;
        session.prefix = prefix;
        session.suffix = suffix;
        session.connectedAt = System.currentTimeMillis();

        authenticatedClients.add(conn);
        sessions.put(conn, session);

        // Load user settings
        UserPanelSettings settings = getUserSettings(offlinePlayer.getUniqueId());

        sendAuthSuccess(conn, session, settings);
        plugin.getLogger().info("Web panel authenticated: " + session.playerName + " (Console)");
    }

    private void sendAuthSuccess(WebSocket conn, WebPanelSession session, UserPanelSettings settings) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTH_SUCCESS");
        response.addProperty("playerUuid", session.playerUuid.toString());
        response.addProperty("playerName", session.playerName);
        response.addProperty("authMethod", session.authMethod);
        response.addProperty("prefix", session.prefix != null ? session.prefix : "");
        response.addProperty("suffix", session.suffix != null ? session.suffix : "");
        response.addProperty("serverName", plugin.getServer().getName());
        response.addProperty("serverVersion", plugin.getServer().getVersion());
        response.addProperty("pluginVersion", plugin.getDescription().getVersion());
        response.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        response.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());

        // Include user settings
        response.add("settings", settings.toJson());

        conn.send(GSON.toJson(response));
    }

    private void sendAccessDenied(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ACCESS_DENIED");
        response.addProperty("message", "You do not have permission to access the ModereX Web Panel.");
        response.addProperty("requiredPermission", "moderex.webpanel");
        conn.send(GSON.toJson(response));
    }

    // ==================== Message Handlers ====================

    private void handleMessage(WebSocket conn, String type, JsonObject json) {
        JsonObject data = json.has("data") ? json.getAsJsonObject("data") : new JsonObject();
        WebPanelSession session = sessions.get(conn);

        switch (type) {
            // Data fetching
            case "GET_PLAYERS" -> sendPlayerList(conn);
            case "GET_PLAYER_DETAILS" -> sendPlayerDetails(conn, data);
            case "GET_PUNISHMENTS" -> sendPunishments(conn, data);
            case "GET_MODLOG" -> sendModlog(conn, data);
            case "GET_AUTOMOD_RULES" -> sendAutomodRules(conn);
            case "GET_WATCHLIST" -> sendWatchlist(conn);
            case "GET_SETTINGS" -> sendSettings(conn);
            case "GET_MESSAGES" -> sendMessages(conn);
            case "GET_TEMPLATES" -> sendTemplates(conn);
            case "GET_LOGS" -> sendLogs(conn, data);
            case "GET_STATS" -> sendStats(conn);
            case "GET_ANTICHEAT_CONFIG" -> sendAnticheatConfig(conn);
            case "GET_ANTICHEAT_INFO" -> sendAnticheatInfo(conn);
            case "GET_LUCKPERMS_STATUS" -> sendLuckPermsStatus(conn);
            case "GET_MODERATION_PLUGINS" -> sendModerationPlugins(conn);

            // Actions
            case "CREATE_PUNISHMENT" -> createPunishment(conn, data, session);
            case "REVOKE_PUNISHMENT" -> revokePunishment(conn, data, session);
            case "ADD_WATCHLIST" -> addToWatchlist(conn, data, session);
            case "REMOVE_WATCHLIST" -> removeFromWatchlist(conn, data);
            case "UPDATE_AUTOMOD_RULE" -> updateAutomodRule(conn, data);
            case "CREATE_AUTOMOD_RULE" -> createAutomodRule(conn, data);
            case "DELETE_AUTOMOD_RULE" -> deleteAutomodRule(conn, data);
            case "SYNC_AUTOMOD_RULES" -> syncAutomodRules(conn, data);
            case "UPDATE_SETTINGS" -> updateSettings(conn, data);
            case "UPDATE_USER_SETTINGS" -> updateUserSettings(conn, data, session);
            case "SEND_STAFFCHAT" -> sendStaffChatFromPanel(conn, data, session);
            case "KICK_PLAYER" -> kickPlayer(conn, data, session);
            case "EXECUTE_COMMAND" -> executeCommand(conn, data, session);
            case "CLEAR_CHAT" -> clearChat(conn, session);
            case "TOGGLE_CHAT_LOCKDOWN" -> toggleChatLockdown(conn, data, session);
            case "SET_SLOWMODE" -> setSlowmode(conn, data, session);

            default -> sendError(conn, "UNKNOWN_TYPE", "Unknown message type: " + type);
        }
    }

    // ==================== Data Endpoints ====================

    private void sendPlayerList(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "PLAYERS");

        JsonArray players = new JsonArray();

        // Online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            JsonObject p = new JsonObject();
            p.addProperty("uuid", player.getUniqueId().toString());
            p.addProperty("name", player.getName());
            p.addProperty("online", true);
            p.addProperty("vanished", plugin.getVanishManager().isVanished(player));
            p.addProperty("platform", "Java"); // Could detect Bedrock with Geyser
            p.addProperty("watched", plugin.getWatchlistManager().isWatched(player.getUniqueId()));

            // Add lastJoin timestamp (when they're online, it's their current session start)
            p.addProperty("lastJoin", player.getLastLogin());
            p.addProperty("firstJoin", player.getFirstPlayed());

            // Get active punishments
            Punishment activeMute = plugin.getPunishmentManager().getActivePunishment(
                player.getUniqueId(), PunishmentType.MUTE);
            Punishment activeBan = plugin.getPunishmentManager().getActivePunishment(
                player.getUniqueId(), PunishmentType.BAN);

            p.addProperty("muted", activeMute != null && !activeMute.isExpired());
            p.addProperty("banned", activeBan != null && !activeBan.isExpired());

            // Get warning count
            p.addProperty("warnings", 0); // Would need to query DB

            players.add(p);
        }

        response.add("data", players);
        conn.send(GSON.toJson(response));
    }

    private void sendPlayerDetails(WebSocket conn, JsonObject data) {
        String uuid = data.has("uuid") ? data.get("uuid").getAsString() : "";

        JsonObject response = new JsonObject();
        response.addProperty("type", "PLAYER_DETAILS");

        try {
            UUID playerUuid = UUID.fromString(uuid);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);

            JsonObject details = new JsonObject();
            details.addProperty("uuid", uuid);
            details.addProperty("name", offlinePlayer.getName());
            details.addProperty("online", offlinePlayer.isOnline());
            details.addProperty("firstPlayed", offlinePlayer.getFirstPlayed());
            details.addProperty("lastPlayed", offlinePlayer.getLastPlayed());
            details.addProperty("watched", plugin.getWatchlistManager().isWatched(playerUuid));

            // Get punishments
            plugin.getPunishmentManager().getPunishments(playerUuid).thenAccept(punishments -> {
                JsonArray punsArray = new JsonArray();
                for (Punishment p : punishments) {
                    punsArray.add(punishmentToJson(p));
                }
                details.add("punishments", punsArray);

                response.add("data", details);
                conn.send(GSON.toJson(response));
            });
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid player UUID");
        }
    }

    private void sendPunishments(WebSocket conn, JsonObject data) {
        String playerUuid = data.has("playerUuid") ? data.get("playerUuid").getAsString() : null;
        String filterType = data.has("type") ? data.get("type").getAsString() : null;
        int limit = data.has("limit") ? data.get("limit").getAsInt() : 100;

        plugin.getPunishmentManager().getRecentPunishments(limit).thenAccept(punishments -> {
            JsonObject response = new JsonObject();
            response.addProperty("type", "PUNISHMENTS");

            JsonArray array = new JsonArray();
            for (Punishment p : punishments) {
                if (playerUuid != null && !p.getPlayerUuid().toString().equals(playerUuid)) continue;
                if (filterType != null && !p.getType().name().equals(filterType)) continue;
                array.add(punishmentToJson(p));
            }
            response.add("data", array);
            conn.send(GSON.toJson(response));
        });
    }

    private void sendModlog(WebSocket conn, JsonObject data) {
        sendPunishments(conn, data);
    }

    private void sendAutomodRules(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "AUTOMOD_RULES_DATA");

        JsonArray rules = new JsonArray();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            rules.add(automodRuleToWebPanelFormat(rule));
        }

        JsonObject data = new JsonObject();
        data.add("rules", rules);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private JsonObject automodRuleToWebPanelFormat(AutomodRule rule) {
        JsonObject r = new JsonObject();
        r.addProperty("id", rule.getId());
        r.addProperty("name", rule.getName());
        r.addProperty("enabled", rule.isEnabled());
        r.addProperty("locked", rule.isBuiltIn());
        r.addProperty("block", true); // Default to blocking

        // Build conditions based on rule type
        JsonArray conditions = new JsonArray();
        switch (rule.getType()) {
            case WORD_FILTER -> {
                if (!rule.getBlacklistedWords().isEmpty()) {
                    JsonObject cond = new JsonObject();
                    cond.addProperty("kind", "contains");
                    cond.addProperty("value", String.join(",", rule.getBlacklistedWords()));
                    cond.addProperty("match", rule.isExactMatch() ? "exact" : "contains");
                    conditions.add(cond);
                }
            }
            case SPAM -> {
                JsonObject cond = new JsonObject();
                cond.addProperty("kind", "repeat");
                cond.addProperty("value", "3");
                cond.addProperty("similar", true);
                conditions.add(cond);
            }
            case CAPS -> {
                JsonObject cond = new JsonObject();
                cond.addProperty("kind", "caps");
                cond.addProperty("value", "65");
                conditions.add(cond);
            }
            case ANTICHEAT -> {
                JsonObject cond = new JsonObject();
                cond.addProperty("kind", "anticheat");
                cond.addProperty("value", "");
                conditions.add(cond);
            }
        }
        r.add("conditions", conditions);

        // Build action
        JsonObject action = new JsonObject();
        AutomodRule.AutoPunishment autoPunish = rule.getAutoPunishment();
        if (autoPunish != null && autoPunish.isEnabled()) {
            action.addProperty("kind", autoPunish.getType().name().toLowerCase());
            action.addProperty("extra", rule.getName());
        } else {
            action.addProperty("kind", "none");
            action.addProperty("extra", "");
        }
        r.add("action", action);

        // Threshold
        JsonObject threshold = new JsonObject();
        if (autoPunish != null) {
            threshold.addProperty("hits", autoPunish.getTriggerCount());
            threshold.addProperty("windowMins", (int) (autoPunish.getTimeWindow() / 60000));
        } else {
            threshold.addProperty("hits", 1);
            threshold.addProperty("windowMins", 10);
        }
        r.add("threshold", threshold);

        r.addProperty("notes", "");

        return r;
    }

    private void updateAutomodRuleFromWebPanel(AutomodRule rule, JsonObject data) {
        if (data.has("name")) {
            rule.setName(data.get("name").getAsString());
        }
        if (data.has("enabled")) {
            rule.setEnabled(data.get("enabled").getAsBoolean());
        }

        // Parse conditions
        if (data.has("conditions") && data.get("conditions").isJsonArray()) {
            JsonArray conditions = data.getAsJsonArray("conditions");
            List<String> words = new ArrayList<>();
            boolean exactMatch = false;

            for (int i = 0; i < conditions.size(); i++) {
                JsonObject cond = conditions.get(i).getAsJsonObject();
                String kind = cond.has("kind") ? cond.get("kind").getAsString() : "";
                String value = cond.has("value") ? cond.get("value").getAsString() : "";

                if (kind.equals("contains") || kind.equals("regex")) {
                    // Split by comma for word lists
                    for (String word : value.split(",")) {
                        word = word.trim();
                        if (!word.isEmpty()) {
                            words.add(word);
                        }
                    }
                    if (cond.has("match")) {
                        exactMatch = cond.get("match").getAsString().equals("exact");
                    }
                }
            }

            if (!words.isEmpty()) {
                rule.setBlacklistedWords(words);
                rule.setExactMatch(exactMatch);
            }
        }

        // Parse action
        if (data.has("action") && data.get("action").isJsonObject()) {
            JsonObject action = data.getAsJsonObject("action");
            String kind = action.has("kind") ? action.get("kind").getAsString() : "none";

            if (!kind.equals("none")) {
                AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
                if (punishment == null) {
                    punishment = new AutomodRule.AutoPunishment();
                    rule.setAutoPunishment(punishment);
                }
                punishment.setEnabled(true);

                try {
                    punishment.setType(PunishmentType.valueOf(kind.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    punishment.setType(PunishmentType.WARN);
                }
            } else if (rule.getAutoPunishment() != null) {
                rule.getAutoPunishment().setEnabled(false);
            }
        }

        // Parse threshold
        if (data.has("threshold") && data.get("threshold").isJsonObject()) {
            JsonObject threshold = data.getAsJsonObject("threshold");
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            if (punishment == null) {
                punishment = new AutomodRule.AutoPunishment();
                rule.setAutoPunishment(punishment);
            }

            if (threshold.has("hits")) {
                punishment.setTriggerCount(threshold.get("hits").getAsInt());
            }
            if (threshold.has("windowMins")) {
                punishment.setTimeWindow(threshold.get("windowMins").getAsLong() * 60000);
            }
        }
    }

    private void sendWatchlist(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "WATCHLIST_DATA");

        JsonArray watchlist = new JsonArray();
        for (UUID uuid : plugin.getWatchlistManager().getWatchedPlayers()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            JsonObject w = new JsonObject();
            w.addProperty("playerUuid", uuid.toString()); // Match expected format
            w.addProperty("uuid", uuid.toString());
            w.addProperty("name", player.getName());
            w.addProperty("playerName", player.getName()); // Match expected format
            w.addProperty("online", player.isOnline());
            watchlist.add(w);
        }

        JsonObject data = new JsonObject();
        data.add("watchlist", watchlist); // Match expected format
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendSettings(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SETTINGS");

        JsonObject settings = new JsonObject();
        var config = plugin.getConfigManager().getSettings();
        settings.addProperty("language", config.getLanguage());
        settings.addProperty("timezone", config.getTimezone());
        settings.addProperty("debugMode", config.isDebugMode());
        settings.addProperty("dialogsEnabled", config.isDialogsEnabled());
        settings.addProperty("staffChatSoundEnabled", config.isStaffChatSoundEnabled());
        settings.addProperty("chatEnabled", config.isChatEnabled());
        settings.addProperty("defaultSlowmode", config.getDefaultSlowmodeSeconds());
        settings.addProperty("vanishHideFromTablist", config.isVanishHideFromTablist());
        settings.addProperty("mutedPlayersCanUseCommands", config.isMutedPlayersCanUseCommands());
        settings.addProperty("updateCheckerEnabled", config.isUpdateCheckerEnabled());

        response.add("data", settings);
        conn.send(GSON.toJson(response));
    }

    private void sendMessages(WebSocket conn) {
        // Send all message keys and their values
        JsonObject response = new JsonObject();
        response.addProperty("type", "MESSAGES");

        JsonObject messages = new JsonObject();
        // Would need to iterate through LanguageManager messages
        response.add("data", messages);
        conn.send(GSON.toJson(response));
    }

    private void sendTemplates(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "TEMPLATES");

        // Get templates from config or database
        JsonArray templates = new JsonArray();
        // Add default templates
        addTemplate(templates, "Spam", "MUTE", "1h", "Excessive chat spam");
        addTemplate(templates, "Advertising", "BAN", "7d", "Advertising other servers");
        addTemplate(templates, "Hacking", "BAN", "permanent", "Using hacked client");
        addTemplate(templates, "Toxicity", "MUTE", "3d", "Toxic behavior");
        addTemplate(templates, "First Warning", "WARN", "", "Please follow the rules");
        addTemplate(templates, "Final Warning", "WARN", "", "This is your final warning");

        response.add("data", templates);
        conn.send(GSON.toJson(response));
    }

    private void addTemplate(JsonArray arr, String name, String type, String duration, String reason) {
        JsonObject t = new JsonObject();
        t.addProperty("name", name);
        t.addProperty("type", type);
        t.addProperty("duration", duration);
        t.addProperty("reason", reason);
        arr.add(t);
    }

    private void sendLogs(WebSocket conn, JsonObject data) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "LOGS");

        // Would fetch from a log storage system
        JsonArray logs = new JsonArray();
        response.add("data", logs);
        conn.send(GSON.toJson(response));
    }

    private void sendStats(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "STATS");

        JsonObject stats = new JsonObject();
        stats.addProperty("onlinePlayers", plugin.getServer().getOnlinePlayers().size());
        stats.addProperty("maxPlayers", plugin.getServer().getMaxPlayers());
        stats.addProperty("vanishedPlayers", plugin.getVanishManager().getVanishedPlayers().size());
        stats.addProperty("watchlistSize", plugin.getWatchlistManager().getWatchedPlayers().size());

        response.add("data", stats);
        conn.send(GSON.toJson(response));
    }

    private void sendAnticheatConfig(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ANTICHEAT_CONFIG");

        JsonObject config = new JsonObject();
        config.addProperty("enabled", plugin.getConfigManager().getSettings().isAnticheatAlertsEnabled());

        // List detected anticheats
        JsonArray detected = new JsonArray();
        if (plugin.getHookManager().getAnticheatManager() != null) {
            for (String ac : plugin.getHookManager().getAnticheatManager().getEnabledAnticheats()) {
                detected.add(ac);
            }
        }
        config.add("detectedAnticheats", detected);

        response.add("data", config);
        conn.send(GSON.toJson(response));
    }

    private void sendAnticheatInfo(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ANTICHEAT_INFO");

        JsonObject data = new JsonObject();
        JsonArray plugins = new JsonArray();

        // Get detected anticheats with their alert status
        if (plugin.getHookManager().getAnticheatManager() != null) {
            boolean alertsEnabled = plugin.getConfigManager().getSettings().isAnticheatAlertsEnabled();
            for (String acName : plugin.getHookManager().getAnticheatManager().getEnabledAnticheats()) {
                JsonObject ac = new JsonObject();
                ac.addProperty("name", acName);
                ac.addProperty("alertsEnabled", alertsEnabled);
                plugins.add(ac);
            }
        }

        data.add("plugins", plugins);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendLuckPermsStatus(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "LUCKPERMS_STATUS");

        JsonObject data = new JsonObject();
        boolean luckPermsAvailable = plugin.getHookManager().hasLuckPerms();
        data.addProperty("available", luckPermsAvailable);

        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    private void sendModerationPlugins(WebSocket conn) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "MODERATION_PLUGINS");

        JsonObject data = new JsonObject();
        JsonArray plugins = new JsonArray();

        // Check for common moderation plugins
        String[] moderationPlugins = {"LiteBans", "AdvancedBan", "BanManager", "Essentials", "CMI", "Liberty's Bans"};
        for (String pluginName : moderationPlugins) {
            if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                JsonObject mp = new JsonObject();
                mp.addProperty("name", pluginName);
                mp.addProperty("punishmentCount", 0); // Would need to query the plugin's database
                plugins.add(mp);
            }
        }

        data.add("plugins", plugins);
        response.add("data", data);
        conn.send(GSON.toJson(response));
    }

    // ==================== Action Endpoints ====================

    private void createPunishment(WebSocket conn, JsonObject data, WebPanelSession session) {
        String targetName = data.has("playerName") ? data.get("playerName").getAsString() : "";
        String typeStr = data.has("type") ? data.get("type").getAsString() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "No reason specified";
        String durationStr = data.has("duration") ? data.get("duration").getAsString() : "";

        if (targetName.isEmpty() || typeStr.isEmpty()) {
            sendError(conn, "MISSING_DATA", "Player name and punishment type are required");
            return;
        }

        // Parse the duration
        long durationMs = DurationParser.parse(durationStr);

        // Get the target player's UUID
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String resolvedName = target.getName() != null ? target.getName() : targetName;

        // Use the session's staff info (actual connected player) instead of "Console"
        UUID staffUuid = session.playerUuid;
        String staffName = session.playerName;

        // Execute punishment via PunishmentManager directly
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PunishmentType type = PunishmentType.valueOf(typeStr.toUpperCase());

                switch (type) {
                    case BAN -> plugin.getPunishmentManager().ban(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case MUTE -> plugin.getPunishmentManager().mute(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case KICK -> plugin.getPunishmentManager().kick(targetUuid, resolvedName, staffUuid, staffName, reason);
                    case WARN -> plugin.getPunishmentManager().warn(targetUuid, resolvedName, staffUuid, staffName, durationMs, reason);
                    case IPBAN -> {
                        // For IP ban, get the player's IP if online, otherwise use stored IP
                        Player onlineTarget = Bukkit.getPlayer(targetUuid);
                        String ip = onlineTarget != null && onlineTarget.getAddress() != null
                                ? onlineTarget.getAddress().getAddress().getHostAddress()
                                : null;
                        if (ip != null) {
                            plugin.getPunishmentManager().ipBan(targetUuid, resolvedName, ip, staffUuid, staffName, durationMs, reason);
                        } else {
                            sendError(conn, "NO_IP", "Cannot IP ban - player has no stored IP address");
                            return;
                        }
                    }
                }

                sendSuccess(conn, "Punishment issued for " + resolvedName);
                plugin.getLogger().info("[WebPanel] " + staffName + " issued " + typeStr + " to " + resolvedName + ": " + reason);

            } catch (IllegalArgumentException e) {
                sendError(conn, "INVALID_TYPE", "Unknown punishment type: " + typeStr);
            }
        });
    }

    private void revokePunishment(WebSocket conn, JsonObject data, WebPanelSession session) {
        String caseId = data.has("caseId") ? data.get("caseId").getAsString() : "";

        if (caseId.isEmpty()) {
            sendError(conn, "MISSING_DATA", "Case ID is required");
            return;
        }

        plugin.getPunishmentManager().getPunishmentByCaseId(caseId).thenAccept(punishment -> {
            if (punishment == null) {
                sendError(conn, "NOT_FOUND", "Punishment not found: " + caseId);
                return;
            }

            plugin.getPunishmentManager().removePunishment(
                punishment.getPlayerUuid(),
                punishment.getType(),
                session.playerUuid,
                session.playerName,
                "Revoked via Web Panel"
            );
            sendSuccess(conn, "Punishment revoked: " + caseId);

            plugin.getLogger().info("[WebPanel] " + session.playerName + " revoked punishment " + caseId);
        });
    }

    private void addToWatchlist(WebSocket conn, JsonObject data, WebPanelSession session) {
        String uuid = data.has("playerUuid") ? data.get("playerUuid").getAsString() : "";
        String playerName = data.has("playerName") ? data.get("playerName").getAsString() : "Unknown";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Added via Web Panel";

        try {
            UUID playerUuid = UUID.fromString(uuid);
            plugin.getWatchlistManager().addToWatchlist(
                playerUuid,
                playerName,
                session.playerUuid,
                session.playerName,
                reason
            );
            sendSuccess(conn, "Added to watchlist");
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid UUID");
        }
    }

    private void removeFromWatchlist(WebSocket conn, JsonObject data) {
        String uuid = data.has("uuid") ? data.get("uuid").getAsString() : "";

        try {
            UUID playerUuid = UUID.fromString(uuid);
            plugin.getWatchlistManager().removeFromWatchlist(playerUuid);
            sendSuccess(conn, "Removed from watchlist");
        } catch (Exception e) {
            sendError(conn, "INVALID_UUID", "Invalid UUID");
        }
    }

    private void updateAutomodRule(WebSocket conn, JsonObject data) {
        String ruleId = data.has("id") ? data.get("id").getAsString() : "";

        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule != null) {
            // Update all properties from web panel format
            updateAutomodRuleFromWebPanel(rule, data);
            plugin.getAutomodManager().saveRule(rule);

            // Broadcast update to all connected clients
            broadcastAutomodRuleUpdate(rule);
            sendSuccess(conn, "Rule updated");
        } else {
            sendError(conn, "NOT_FOUND", "Rule not found");
        }
    }

    private void createAutomodRule(WebSocket conn, JsonObject data) {
        String name = data.has("name") ? data.get("name").getAsString() : "New Rule";

        AutomodRule rule = plugin.getAutomodManager().createRule(name);
        if (rule != null) {
            // Apply additional settings from web panel
            updateAutomodRuleFromWebPanel(rule, data);
            plugin.getAutomodManager().saveRule(rule);

            // Send back the created rule
            JsonObject response = new JsonObject();
            response.addProperty("type", "AUTOMOD_RULE_CREATED");
            response.add("data", automodRuleToWebPanelFormat(rule));
            conn.send(GSON.toJson(response));

            // Broadcast to all clients
            broadcastAutomodRuleUpdate(rule);
            plugin.getLogger().info("[WebPanel] Created automod rule: " + name);
        } else {
            sendError(conn, "CREATE_FAILED", "Failed to create rule");
        }
    }

    private void deleteAutomodRule(WebSocket conn, JsonObject data) {
        String ruleId = data.has("id") ? data.get("id").getAsString() : "";

        AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
        if (rule == null) {
            sendError(conn, "NOT_FOUND", "Rule not found");
            return;
        }

        if (rule.isBuiltIn()) {
            sendError(conn, "FORBIDDEN", "Cannot delete built-in rules");
            return;
        }

        plugin.getAutomodManager().deleteRule(ruleId);

        // Broadcast deletion to all clients
        JsonObject broadcast = new JsonObject();
        broadcast.addProperty("type", "AUTOMOD_RULE_DELETED");
        JsonObject broadcastData = new JsonObject();
        broadcastData.addProperty("id", ruleId);
        broadcast.add("data", broadcastData);
        broadcastToAuthenticated(GSON.toJson(broadcast));

        sendSuccess(conn, "Rule deleted");
        plugin.getLogger().info("[WebPanel] Deleted automod rule: " + ruleId);
    }

    private void syncAutomodRules(WebSocket conn, JsonObject data) {
        // Batch update all rules from web panel
        if (!data.has("rules") || !data.get("rules").isJsonArray()) {
            sendError(conn, "INVALID_DATA", "Rules array required");
            return;
        }

        JsonArray rulesArray = data.getAsJsonArray("rules");
        int updated = 0;

        for (int i = 0; i < rulesArray.size(); i++) {
            JsonObject ruleData = rulesArray.get(i).getAsJsonObject();
            String ruleId = ruleData.has("id") ? ruleData.get("id").getAsString() : "";

            AutomodRule rule = plugin.getAutomodManager().getRule(ruleId);
            if (rule != null && !rule.isBuiltIn()) {
                updateAutomodRuleFromWebPanel(rule, ruleData);
                plugin.getAutomodManager().saveRule(rule);
                updated++;
            }
        }

        // Broadcast full rules list to all clients
        broadcastAutomodRulesRefresh();
        sendSuccess(conn, "Synced " + updated + " rules");
        plugin.getLogger().info("[WebPanel] Synced " + updated + " automod rules");
    }

    public void broadcastAutomodRuleUpdate(AutomodRule rule) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_RULE_UPDATED");
        json.add("data", automodRuleToWebPanelFormat(rule));
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastAutomodRulesRefresh() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_RULES_DATA");

        JsonArray rules = new JsonArray();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            rules.add(automodRuleToWebPanelFormat(rule));
        }

        JsonObject data = new JsonObject();
        data.add("rules", rules);
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    private void updateSettings(WebSocket conn, JsonObject data) {
        // Update global settings
        var settings = plugin.getConfigManager().getSettings();

        if (data.has("chatEnabled")) {
            settings.setChatEnabled(data.get("chatEnabled").getAsBoolean());
        }
        if (data.has("defaultSlowmode")) {
            settings.setDefaultSlowmodeSeconds(data.get("defaultSlowmode").getAsInt());
        }

        plugin.getConfigManager().saveConfig();
        sendSuccess(conn, "Settings updated");
    }

    private void updateUserSettings(WebSocket conn, JsonObject data, WebPanelSession session) {
        UserPanelSettings settings = getUserSettings(session.playerUuid);

        if (data.has("chatAlerts")) settings.chatAlerts = data.get("chatAlerts").getAsBoolean();
        if (data.has("soundEnabled")) settings.soundEnabled = data.get("soundEnabled").getAsBoolean();
        if (data.has("watchlistToasts")) settings.watchlistToasts = data.get("watchlistToasts").getAsBoolean();
        if (data.has("staffChatNotifications")) settings.staffChatNotifications = data.get("staffChatNotifications").getAsBoolean();
        if (data.has("punishmentAlerts")) settings.punishmentAlerts = data.get("punishmentAlerts").getAsBoolean();
        if (data.has("automodAlerts")) settings.automodAlerts = data.get("automodAlerts").getAsBoolean();
        if (data.has("anticheatAlerts")) settings.anticheatAlerts = data.get("anticheatAlerts").getAsBoolean();
        if (data.has("compactMode")) settings.compactMode = data.get("compactMode").getAsBoolean();

        saveUserSettings();
        sendSuccess(conn, "User settings saved");
    }

    private void sendStaffChatFromPanel(WebSocket conn, JsonObject data, WebPanelSession session) {
        String message = data.has("message") ? data.get("message").getAsString() : "";

        if (message.isEmpty()) {
            sendError(conn, "EMPTY_MESSAGE", "Message cannot be empty");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getStaffChatManager().broadcastFromWebPanel(session.playerName, message);
        });

        sendSuccess(conn, "Message sent");
    }

    private void kickPlayer(WebSocket conn, JsonObject data, WebPanelSession session) {
        String playerName = data.has("playerName") ? data.get("playerName").getAsString() : "";
        String reason = data.has("reason") ? data.get("reason").getAsString() : "Kicked via Web Panel";

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                player.kick(TextUtil.parse("<red>" + reason));
                sendSuccess(conn, "Player kicked: " + playerName);
            } else {
                sendError(conn, "NOT_ONLINE", "Player is not online");
            }
        });
    }

    private void executeCommand(WebSocket conn, JsonObject data, WebPanelSession session) {
        // Security: Only allow specific commands
        String command = data.has("command") ? data.get("command").getAsString() : "";

        List<String> allowedCommands = Arrays.asList("mx", "mute", "unmute", "ban", "unban", "kick", "warn");
        String cmdBase = command.split(" ")[0].toLowerCase();

        if (!allowedCommands.contains(cmdBase)) {
            sendError(conn, "FORBIDDEN", "Command not allowed from web panel");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
            sendSuccess(conn, "Command executed");
            plugin.getLogger().info("[WebPanel] " + session.playerName + " executed: /" + command);
        });
    }

    private void clearChat(WebSocket conn, WebPanelSession session) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!player.hasPermission("moderex.bypass.clearchat")) {
                    for (int i = 0; i < 100; i++) {
                        player.sendMessage("");
                    }
                }
            }
            sendSuccess(conn, "Chat cleared");
            plugin.getLogger().info("[WebPanel] " + session.playerName + " cleared chat");
        });
    }

    private void toggleChatLockdown(WebSocket conn, JsonObject data, WebPanelSession session) {
        boolean enabled = data.has("enabled") && data.get("enabled").getAsBoolean();
        plugin.getConfigManager().getSettings().setChatEnabled(!enabled);
        sendSuccess(conn, enabled ? "Chat locked" : "Chat unlocked");
    }

    private void setSlowmode(WebSocket conn, JsonObject data, WebPanelSession session) {
        int seconds = data.has("seconds") ? data.get("seconds").getAsInt() : 0;
        plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(seconds);
        sendSuccess(conn, seconds > 0 ? "Slowmode set to " + seconds + "s" : "Slowmode disabled");
    }

    // ==================== Helper Methods ====================

    private JsonObject punishmentToJson(Punishment p) {
        JsonObject json = new JsonObject();
        json.addProperty("caseId", p.getCaseId());
        json.addProperty("playerUuid", p.getPlayerUuid().toString());
        json.addProperty("playerName", p.getPlayerName());
        json.addProperty("type", p.getType().name());
        json.addProperty("reason", p.getReason());
        json.addProperty("staffUuid", p.getStaffUuid() != null ? p.getStaffUuid().toString() : "");
        json.addProperty("staffName", p.getStaffName());
        json.addProperty("createdAt", p.getCreatedAt());
        json.addProperty("expiresAt", p.getExpiresAt());
        json.addProperty("active", p.isActive());
        json.addProperty("expired", p.isExpired());

        // Add formatted duration
        if (p.getExpiresAt() == -1) {
            json.addProperty("duration", "Permanent");
        } else if (p.getExpiresAt() == 0 || p.getType() == PunishmentType.KICK) {
            json.addProperty("duration", "Instant");
        } else {
            long durationMs = p.getExpiresAt() - p.getCreatedAt();
            json.addProperty("duration", DurationParser.format(durationMs, true));
        }

        return json;
    }

    private void sendError(WebSocket conn, String code, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "ERROR");
        response.addProperty("code", code);
        response.addProperty("message", message);
        conn.send(GSON.toJson(response));
    }

    private void sendSuccess(WebSocket conn, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "SUCCESS");
        response.addProperty("message", message);
        conn.send(GSON.toJson(response));
    }

    // ==================== User Settings ====================

    private UserPanelSettings getUserSettings(UUID uuid) {
        return userSettings.computeIfAbsent(uuid, k -> new UserPanelSettings());
    }

    private void loadUserSettings() {
        // Load from database
        try {
            plugin.getDatabaseManager().query(
                "SELECT * FROM moderex_webpanel_settings",
                rs -> {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                        UserPanelSettings settings = new UserPanelSettings();
                        settings.chatAlerts = rs.getBoolean("chat_alerts");
                        settings.soundEnabled = rs.getBoolean("sound_enabled");
                        settings.watchlistToasts = rs.getBoolean("watchlist_toasts");
                        settings.staffChatNotifications = rs.getBoolean("staffchat_notifications");
                        settings.punishmentAlerts = rs.getBoolean("punishment_alerts");
                        settings.automodAlerts = rs.getBoolean("automod_alerts");
                        settings.anticheatAlerts = rs.getBoolean("anticheat_alerts");
                        settings.compactMode = rs.getBoolean("compact_mode");
                        userSettings.put(uuid, settings);
                    }
                    return null;
                }
            );
        } catch (SQLException ignored) {
            // Table might not exist yet
        }
    }

    private void saveUserSettings() {
        for (Map.Entry<UUID, UserPanelSettings> entry : userSettings.entrySet()) {
            try {
                plugin.getDatabaseManager().update(
                    """
                    INSERT OR REPLACE INTO moderex_webpanel_settings
                    (player_uuid, chat_alerts, sound_enabled, watchlist_toasts,
                     staffchat_notifications, punishment_alerts, automod_alerts,
                     anticheat_alerts, compact_mode)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    entry.getKey().toString(),
                    entry.getValue().chatAlerts,
                    entry.getValue().soundEnabled,
                    entry.getValue().watchlistToasts,
                    entry.getValue().staffChatNotifications,
                    entry.getValue().punishmentAlerts,
                    entry.getValue().automodAlerts,
                    entry.getValue().anticheatAlerts,
                    entry.getValue().compactMode
                );
            } catch (SQLException ignored) {}
        }
    }

    // ==================== Broadcast Methods ====================

    public void broadcastStaffChat(String playerName, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "STAFFCHAT_MESSAGE");
        JsonObject data = new JsonObject();
        data.addProperty("player", playerName);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastPunishment(Punishment punishment) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PUNISHMENT_CREATED");
        json.add("data", punishmentToJson(punishment));
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastPlayerJoin(Player player) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_JOIN");
        JsonObject data = new JsonObject();
        data.addProperty("uuid", player.getUniqueId().toString());
        data.addProperty("name", player.getName());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastPlayerQuit(Player player) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "PLAYER_QUIT");
        JsonObject data = new JsonObject();
        data.addProperty("uuid", player.getUniqueId().toString());
        data.addProperty("name", player.getName());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastWatchlistAlert(String type, String playerName, String details) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "WATCHLIST_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("alertType", type);
        data.addProperty("playerName", playerName);
        data.addProperty("details", details);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastAutomodTrigger(String playerName, String rule, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "AUTOMOD_TRIGGER");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("rule", rule);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastAnticheatAlert(String playerName, String check, double vl) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ANTICHEAT_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("check", check);
        data.addProperty("vl", vl);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastAnticheatAlert(String anticheat, UUID playerUuid, String playerName,
                                         String checkName, String checkType, int violations, double vlLevel) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ANTICHEAT_ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("anticheat", anticheat);
        data.addProperty("playerUuid", playerUuid.toString());
        data.addProperty("playerName", playerName);
        data.addProperty("check", checkName);
        data.addProperty("checkType", checkType);
        data.addProperty("violations", violations);
        data.addProperty("vl", vlLevel);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastLogEvent(String severity, String type, String title, String detail) {
        broadcastLogEvent(severity, type, title, detail, null, null);
    }

    public void broadcastLogEvent(String severity, String type, String title, String detail,
                                   UUID playerUuid, String caseId) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOG_EVENT");
        JsonObject data = new JsonObject();
        data.addProperty("severity", severity); // INFO, WARN, ERROR
        data.addProperty("logType", type); // PUNISHMENT, CHAT, AUTOMOD, SYSTEM, etc.
        data.addProperty("title", title);
        data.addProperty("detail", detail);
        data.addProperty("timestamp", System.currentTimeMillis());
        if (playerUuid != null) {
            data.addProperty("playerUuid", playerUuid.toString());
        }
        if (caseId != null) {
            data.addProperty("caseId", caseId);
        }
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastAlert(String title, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ALERT");
        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("message", message);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    public void broadcastVanishUpdate(String playerName, boolean vanished) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "VANISH_UPDATE");
        JsonObject data = new JsonObject();
        data.addProperty("playerName", playerName);
        data.addProperty("vanished", vanished);
        data.addProperty("timestamp", System.currentTimeMillis());
        json.add("data", data);
        broadcastToAuthenticated(GSON.toJson(json));
    }

    private void broadcastToAuthenticated(String message) {
        for (WebSocket client : authenticatedClients) {
            if (client.isOpen()) {
                client.send(message);
            }
        }
    }

    // ==================== Inner Classes ====================

    private static class PendingConnection {
        UUID playerUuid;
        String playerName;
        long createdAt;
        boolean hasPermission;
        String prefix;
        String suffix;
    }

    private static class WebPanelSession {
        UUID playerUuid;
        String playerName;
        String authMethod; // "MINECRAFT" or "CONSOLE"
        boolean hasPermission;
        String prefix;
        String suffix;
        long connectedAt;
    }

    public static class UserPanelSettings {
        boolean chatAlerts = true;
        boolean soundEnabled = true;
        boolean watchlistToasts = true;
        boolean staffChatNotifications = true;
        boolean punishmentAlerts = true;
        boolean automodAlerts = true;
        boolean anticheatAlerts = true;
        boolean compactMode = false;

        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("chatAlerts", chatAlerts);
            json.addProperty("soundEnabled", soundEnabled);
            json.addProperty("watchlistToasts", watchlistToasts);
            json.addProperty("staffChatNotifications", staffChatNotifications);
            json.addProperty("punishmentAlerts", punishmentAlerts);
            json.addProperty("automodAlerts", automodAlerts);
            json.addProperty("anticheatAlerts", anticheatAlerts);
            json.addProperty("compactMode", compactMode);
            return json;
        }
    }
}
