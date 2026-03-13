package com.blockforge.moderex.ai;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains a sliding context window of recent messages per player.
 * The AI can use this context to detect patterns across multiple messages.
 */
public class MessageContextWindow {

    private static final int DEFAULT_MAX_MESSAGES = 500;
    private static final long DEFAULT_WINDOW_MS = 3600000L; // 1 hour

    private final int maxMessages;
    private final long windowMs;

    // Player UUID -> deque of recent messages
    private final Map<UUID, Deque<ContextMessage>> playerContext = new ConcurrentHashMap<>();

    public MessageContextWindow() {
        this(DEFAULT_MAX_MESSAGES, DEFAULT_WINDOW_MS);
    }

    public MessageContextWindow(int maxMessages, long windowMs) {
        this.maxMessages = maxMessages;
        this.windowMs = windowMs;
    }

    /**
     * Add a message to the player's context.
     */
    public void addMessage(UUID playerUuid, String message, String contentType) {
        Deque<ContextMessage> deque = playerContext.computeIfAbsent(playerUuid,
                k -> new ArrayDeque<>());

        synchronized (deque) {
            deque.addLast(new ContextMessage(message, contentType, System.currentTimeMillis()));

            // Trim to max size
            while (deque.size() > maxMessages) {
                deque.removeFirst();
            }

            // Remove expired
            long cutoff = System.currentTimeMillis() - windowMs;
            while (!deque.isEmpty() && deque.peekFirst().timestamp < cutoff) {
                deque.removeFirst();
            }
        }
    }

    /**
     * Get recent context for a player as a formatted string for AI prompt.
     */
    public String getContextString(UUID playerUuid, int lastN) {
        Deque<ContextMessage> deque = playerContext.get(playerUuid);
        if (deque == null || deque.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Recent message history for this player:\n");

        List<ContextMessage> recent;
        synchronized (deque) {
            recent = new ArrayList<>(deque);
        }

        // Take last N
        int start = Math.max(0, recent.size() - lastN);
        for (int i = start; i < recent.size(); i++) {
            ContextMessage msg = recent.get(i);
            sb.append("- [").append(msg.contentType).append("] ").append(msg.message).append("\n");
        }

        return sb.toString();
    }

    /**
     * Get the count of messages in a player's context.
     */
    public int getMessageCount(UUID playerUuid) {
        Deque<ContextMessage> deque = playerContext.get(playerUuid);
        return deque != null ? deque.size() : 0;
    }

    public void clearPlayer(UUID playerUuid) {
        playerContext.remove(playerUuid);
    }

    public void clearAll() {
        playerContext.clear();
    }

    private record ContextMessage(String message, String contentType, long timestamp) {}
}
