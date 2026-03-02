package com.blockforge.moderex.ai;

/**
 * Content types that AI moderation can analyze.
 * Each type can be toggled per automod rule.
 */
public enum ContentType {
    CHAT_MESSAGE("Chat Message", "Regular chat messages"),
    PRIVATE_MESSAGE("Private Message", "Private messages between players"),
    COMMAND_ARGUMENT("Command Argument", "Arguments passed to commands"),
    SIGN_TEXT("Sign Text", "Text written on signs"),
    BOOK_TEXT("Book Text", "Text written in books"),
    NICKNAME("Nickname", "Player nicknames"),
    ANVIL_RENAME("Anvil Rename", "Items renamed in anvils");

    private final String displayName;
    private final String description;

    ContentType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
