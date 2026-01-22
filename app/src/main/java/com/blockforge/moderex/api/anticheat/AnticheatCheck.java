package com.blockforge.moderex.api.anticheat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single check/detection provided by an anticheat.
 *
 * <p>Each check has a name, display name, category, and optional description.
 * Checks can also have aliases for matching different naming conventions.
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // Simple check
 * new AnticheatCheck("speed", "Speed", Category.MOVEMENT, "Detects speed modifications");
 *
 * // Check with aliases
 * AnticheatCheck.builder("killaura")
 *     .displayName("KillAura")
 *     .category(Category.COMBAT)
 *     .description("Detects automatic hitting")
 *     .aliases("aura", "ka", "aimbot")
 *     .build();
 * }</pre>
 *
 * @see Category
 */
public class AnticheatCheck {

    private final String name;
    private final String displayName;
    private final Category category;
    private final String description;
    private final List<String> aliases;

    /**
     * Create a new anticheat check.
     *
     * @param name        internal name (lowercase, no spaces)
     * @param displayName display name shown in GUIs
     * @param category    check category
     * @param description brief description of what this check detects
     */
    public AnticheatCheck(String name, String displayName, Category category, String description) {
        this.name = name.toLowerCase();
        this.displayName = displayName;
        this.category = category;
        this.description = description;
        this.aliases = Collections.emptyList();
    }

    private AnticheatCheck(Builder builder) {
        this.name = builder.name.toLowerCase();
        this.displayName = builder.displayName;
        this.category = builder.category;
        this.description = builder.description;
        this.aliases = builder.aliases;
    }

    /**
     * Create a builder for this check.
     *
     * @param name the internal name of the check
     * @return a new builder
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Get the internal name of this check.
     *
     * @return the check name (lowercase)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the display name of this check.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the category of this check.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Get the description of this check.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the aliases for this check.
     *
     * @return list of aliases, or empty list if none
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Check if a given name matches this check (by name or alias).
     *
     * @param checkName the name to match
     * @return true if it matches
     */
    public boolean matches(String checkName) {
        if (checkName == null) return false;
        String lower = checkName.toLowerCase().replace(" ", "").replace("_", "");
        if (name.replace("_", "").equals(lower)) return true;
        for (String alias : aliases) {
            if (alias.toLowerCase().replace("_", "").equals(lower)) return true;
        }
        return false;
    }

    /**
     * Categories for anticheat checks.
     */
    public enum Category {
        /** Combat-related checks (reach, killaura, velocity, criticals, etc.) */
        COMBAT("Combat", "fa-solid fa-swords"),

        /** Movement-related checks (speed, fly, phase, elytra, etc.) */
        MOVEMENT("Movement", "fa-solid fa-person-running"),

        /** Player-related checks (inventory, scaffold, autoclicker, etc.) */
        PLAYER("Player", "fa-solid fa-user"),

        /** World interaction checks (block place/break, interactions) */
        WORLD("World", "fa-solid fa-globe"),

        /** Miscellaneous checks that don't fit other categories */
        MISC("Misc", "fa-solid fa-question");

        private final String displayName;
        private final String icon;

        Category(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getIcon() {
            return icon;
        }
    }

    /**
     * Builder for creating AnticheatCheck instances.
     */
    public static class Builder {
        private final String name;
        private String displayName;
        private Category category = Category.MISC;
        private String description = "";
        private List<String> aliases = Collections.emptyList();

        private Builder(String name) {
            this.name = name;
            this.displayName = name;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder aliases(String... aliases) {
            this.aliases = Arrays.asList(aliases);
            return this;
        }

        public Builder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public AnticheatCheck build() {
            return new AnticheatCheck(this);
        }
    }
}
