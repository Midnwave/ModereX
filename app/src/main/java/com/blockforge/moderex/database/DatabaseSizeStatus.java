package com.blockforge.moderex.database;

/**
 * Status of the database size relative to the limit.
 * Used for free tier users to track database usage.
 */
public enum DatabaseSizeStatus {
    /**
     * Under 80% of limit - normal operation
     */
    OK,

    /**
     * 80-90% of limit - yellow warning, encourage cleanup
     */
    WARNING,

    /**
     * 90-100% of limit - red alert, approaching limit
     */
    CRITICAL,

    /**
     * At or over 100% - writes blocked until data deleted or premium purchased
     */
    LIMIT_REACHED
}
