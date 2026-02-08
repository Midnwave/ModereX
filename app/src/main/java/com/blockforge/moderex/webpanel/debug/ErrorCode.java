package com.blockforge.moderex.webpanel.debug;

/**
 * Centralized error codes for the ModereX WebPanel.
 * Each error code has a unique identifier and human-readable description.
 *
 * Error Code Format: CATEGORY_SPECIFIC_ERROR
 * - 1xxx: Authentication errors
 * - 2xxx: Request/validation errors
 * - 3xxx: Resource errors (not found, forbidden)
 * - 4xxx: Operation errors (create, update, delete failures)
 * - 5xxx: System/internal errors
 * - 6xxx: Player/entity errors
 * - 7xxx: Punishment errors
 * - 8xxx: Automod errors
 * - 9xxx: Anticheat errors
 */
public enum ErrorCode {

    // ==================== 1xxx: AUTHENTICATION ERRORS ====================
    AUTH_NOT_AUTHENTICATED(1001, "NOT_AUTHENTICATED",
        "Request requires authentication. Please provide a valid session token."),
    AUTH_INVALID_TOKEN(1002, "INVALID_TOKEN",
        "The provided authentication token is invalid or malformed."),
    AUTH_EXPIRED_SESSION(1003, "SESSION_EXPIRED",
        "Your session has expired. Please re-authenticate."),
    AUTH_UNAVAILABLE(1004, "AUTH_UNAVAILABLE",
        "Authentication system is not available. Check server configuration."),
    AUTH_MISSING_SESSION_ID(1005, "MISSING_SESSION_ID",
        "Session ID is required but was not provided in the request."),
    AUTH_ACCESS_DENIED(1006, "ACCESS_DENIED",
        "You do not have permission to perform this action."),
    AUTH_FAILED(1007, "AUTH_FAILED",
        "Authentication failed. Invalid credentials or token."),
    AUTH_INVALID_CODE(1008, "INVALID_CODE",
        "The provided connect code is invalid or expired."),
    AUTH_TOKEN_REQUIRED(1009, "TOKEN_REQUIRED",
        "Authentication token is required for this operation."),
    AUTH_RATE_LIMITED(1010, "RATE_LIMITED",
        "Too many authentication attempts. Please wait before trying again."),
    AUTH_SESSION_EXPIRED(1011, "SESSION_EXPIRED",
        "Your session has expired due to inactivity. Please reconnect."),

    // ==================== 2xxx: REQUEST/VALIDATION ERRORS ====================
    REQUEST_INVALID(2001, "INVALID_REQUEST",
        "The request is malformed or contains invalid data."),
    REQUEST_MISSING_DATA(2002, "MISSING_DATA",
        "Required data fields are missing from the request."),
    REQUEST_MISSING_PARAMETER(2003, "MISSING_PARAMETER",
        "A required parameter is missing from the request."),
    REQUEST_INVALID_UUID(2004, "INVALID_UUID",
        "The provided UUID is not in a valid format."),
    REQUEST_INVALID_TYPE(2005, "INVALID_TYPE",
        "The specified type is not recognized or supported."),
    REQUEST_UNKNOWN_TYPE(2006, "UNKNOWN_TYPE",
        "Unknown message type received. Check API documentation."),
    REQUEST_EMPTY_MESSAGE(2007, "EMPTY_MESSAGE",
        "Message content cannot be empty."),
    REQUEST_INVALID_FORMAT(2008, "INVALID_FORMAT",
        "Data format is invalid. Expected JSON or specific format."),
    REQUEST_INVALID_CHECK_KEY(2009, "INVALID_CHECK_KEY",
        "Invalid anticheat check key format. Expected: anticheat:checkname"),
    REQUEST_MISSING_FIELD(2010, "MISSING_FIELD",
        "A required field is missing from the request data."),
    REQUEST_PLAYER_NOT_FOUND(2011, "PLAYER_NOT_FOUND",
        "The specified player could not be found."),

    // ==================== 3xxx: RESOURCE ERRORS ====================
    RESOURCE_NOT_FOUND(3001, "NOT_FOUND",
        "The requested resource could not be found."),
    RESOURCE_FORBIDDEN(3002, "FORBIDDEN",
        "Access to this resource is forbidden. Cannot modify built-in items."),
    RESOURCE_PLAYER_NOT_FOUND(3003, "PLAYER_NOT_FOUND",
        "The specified player could not be found in the database."),
    RESOURCE_RULE_NOT_FOUND(3004, "RULE_NOT_FOUND",
        "The specified automod rule does not exist."),
    RESOURCE_TEMPLATE_NOT_FOUND(3005, "TEMPLATE_NOT_FOUND",
        "The specified punishment template does not exist."),
    RESOURCE_PUNISHMENT_NOT_FOUND(3006, "PUNISHMENT_NOT_FOUND",
        "The specified punishment record does not exist."),
    RESOURCE_REPLAY_NOT_FOUND(3007, "REPLAY_NOT_FOUND",
        "The specified replay data could not be found."),
    RESOURCE_NOT_AVAILABLE(3008, "NOT_AVAILABLE",
        "The requested service or resource is not currently available."),

    // ==================== 4xxx: OPERATION ERRORS ====================
    OPERATION_FAILED(4001, "FAILED",
        "The operation failed. Check server logs for details."),
    OPERATION_CREATE_ERROR(4002, "CREATE_ERROR",
        "Failed to create the resource. It may already exist or data is invalid."),
    OPERATION_UPDATE_ERROR(4003, "UPDATE_ERROR",
        "Failed to update the resource. It may not exist or data is invalid."),
    OPERATION_DELETE_ERROR(4004, "DELETE_ERROR",
        "Failed to delete the resource. It may be in use or protected."),
    OPERATION_UPDATE_FAILED(4005, "UPDATE_FAILED",
        "The update operation did not complete successfully."),
    OPERATION_SAVE_ERROR(4006, "SAVE_ERROR",
        "Failed to save changes to the database."),
    OPERATION_LOAD_ERROR(4007, "LOAD_ERROR",
        "Failed to load data from the database."),

    // ==================== 5xxx: SYSTEM ERRORS ====================
    SYSTEM_INTERNAL_ERROR(5001, "INTERNAL_ERROR",
        "An internal server error occurred. Check server logs."),
    SYSTEM_DATABASE_ERROR(5002, "DATABASE_ERROR",
        "A database error occurred. Connection may be lost."),
    SYSTEM_CONNECTION_ERROR(5003, "CONNECTION_ERROR",
        "Failed to establish or maintain connection."),
    SYSTEM_TIMEOUT(5004, "TIMEOUT",
        "The operation timed out. Server may be overloaded."),
    SYSTEM_NOT_INITIALIZED(5005, "NOT_INITIALIZED",
        "The required system component is not initialized."),
    SYSTEM_SHUTDOWN(5006, "SHUTDOWN",
        "The server is shutting down. Please reconnect later."),
    SERVER_BIND_FAILED(5007, "BIND_FAILED",
        "Failed to bind web panel server to the specified port."),
    DATABASE_QUERY_FAILED(5008, "DATABASE_QUERY_FAILED",
        "Failed to execute database query."),

    // ==================== 6xxx: PLAYER/ENTITY ERRORS ====================
    PLAYER_NOT_ONLINE(6001, "NOT_ONLINE",
        "The specified player is not currently online."),
    PLAYER_NO_IP(6002, "NO_IP",
        "Cannot perform IP-based action. No IP address stored for player."),
    PLAYER_IMMUNE(6003, "PLAYER_IMMUNE",
        "The player is immune to this action (staff or protected)."),
    PLAYER_ALREADY_PUNISHED(6004, "ALREADY_PUNISHED",
        "The player already has an active punishment of this type."),
    PLAYER_SELF_ACTION(6005, "SELF_ACTION",
        "You cannot perform this action on yourself."),

    // ==================== 7xxx: PUNISHMENT ERRORS ====================
    PUNISHMENT_ALREADY_REVOKED(7001, "ALREADY_REVOKED",
        "This punishment has already been revoked."),
    PUNISHMENT_INVALID_DURATION(7002, "INVALID_DURATION",
        "The specified punishment duration is invalid."),
    PUNISHMENT_INVALID_REASON(7003, "INVALID_REASON",
        "A valid reason must be provided for this punishment."),
    PUNISHMENT_TEMPLATE_ERROR(7004, "TEMPLATE_ERROR",
        "Error processing punishment template."),
    PUNISHMENT_CANNOT_REVOKE(7005, "CANNOT_REVOKE",
        "This punishment cannot be revoked (permanent or system-issued)."),

    // ==================== 8xxx: AUTOMOD ERRORS ====================
    AUTOMOD_RULE_EXISTS(8001, "RULE_EXISTS",
        "An automod rule with this name already exists."),
    AUTOMOD_INVALID_CONFIG(8002, "INVALID_CONFIG",
        "The automod rule configuration is invalid."),
    AUTOMOD_BUILTIN_PROTECTED(8003, "BUILTIN_PROTECTED",
        "Cannot delete or rename built-in automod rules."),
    AUTOMOD_THRESHOLD_INVALID(8004, "THRESHOLD_INVALID",
        "The specified threshold values are out of valid range."),
    AUTOMOD_RULE_NOT_FOUND(8005, "RULE_NOT_FOUND",
        "The specified automod rule does not exist."),
    AUTOMOD_RULE_UPDATE_FAILED(8006, "RULE_UPDATE_FAILED",
        "Failed to update the automod rule due to an internal error."),
    AUTOMOD_RULE_CREATE_FAILED(8007, "RULE_CREATE_FAILED",
        "Failed to create the automod rule due to an internal error."),
    AUTOMOD_RULE_DELETE_FAILED(8008, "RULE_DELETE_FAILED",
        "Failed to delete the automod rule due to an internal error."),

    // ==================== 9xxx: ANTICHEAT ERRORS ====================
    ANTICHEAT_NOT_ENABLED(9001, "ANTICHEAT_NOT_ENABLED",
        "No anticheat plugin is currently enabled or detected."),
    ANTICHEAT_CHECK_NOT_FOUND(9002, "CHECK_NOT_FOUND",
        "The specified anticheat check does not exist."),
    ANTICHEAT_INVALID_SETTING(9003, "INVALID_SETTING",
        "The anticheat setting value is invalid.");

    private final int numericCode;
    private final String code;
    private final String description;

    ErrorCode(int numericCode, String code, String description) {
        this.numericCode = numericCode;
        this.code = code;
        this.description = description;
    }

    /**
     * Get the numeric error code (e.g., 1001).
     */
    public int getNumericCode() {
        return numericCode;
    }

    /**
     * Get the string error code (e.g., "NOT_AUTHENTICATED").
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the human-readable description of this error.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the full error message with code and description.
     */
    public String getFullMessage() {
        return "[" + numericCode + "] " + code + ": " + description;
    }

    /**
     * Find an ErrorCode by its string code.
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode ec : values()) {
            if (ec.code.equals(code)) {
                return ec;
            }
        }
        return null;
    }

    /**
     * Find an ErrorCode by its numeric code.
     */
    public static ErrorCode fromNumericCode(int numericCode) {
        for (ErrorCode ec : values()) {
            if (ec.numericCode == numericCode) {
                return ec;
            }
        }
        return null;
    }
}
