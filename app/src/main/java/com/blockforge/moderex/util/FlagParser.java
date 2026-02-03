package com.blockforge.moderex.util;

import java.util.*;

/**
 * Utility class for parsing command flags from arguments.
 * Supports both short flags (-s, -I, -g) and long flags (--delete, --sender=value)
 */
public class FlagParser {

    private final Map<String, String> flags = new HashMap<>();
    private final List<String> regularArgs = new ArrayList<>();
    private boolean ignoreFlags = false;

    public FlagParser(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // Check for -- flag (ignore all flags after this)
            if (arg.equals("--") && !ignoreFlags) {
                ignoreFlags = true;
                continue;
            }

            if (ignoreFlags) {
                regularArgs.add(arg);
                continue;
            }

            // Long flags with = (e.g., --sender=Custom)
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                flags.put(parts[0].toLowerCase(), parts.length > 1 ? parts[1] : "true");
                continue;
            }

            // Long flags without = (e.g., --delete, --confirm)
            if (arg.startsWith("--")) {
                flags.put(arg.substring(2).toLowerCase(), "true");
                continue;
            }

            // Short flags (e.g., -s, -I, -g)
            if (arg.startsWith("-") && arg.length() > 1 && !arg.matches("-\\d+.*")) {
                String flagChars = arg.substring(1);

                // Handle flags with colons (e.g., -s:true, -s:$silent)
                if (flagChars.contains(":")) {
                    String[] parts = flagChars.split(":", 2);
                    flags.put(parts[0].toLowerCase(), parts.length > 1 ? parts[1] : "true");
                } else {
                    // Multiple single-character flags (e.g., -sgI)
                    for (char c : flagChars.toCharArray()) {
                        flags.put(String.valueOf(c).toLowerCase(), "true");
                    }
                }
                continue;
            }

            // Regular argument
            regularArgs.add(arg);
        }
    }

    public boolean hasFlag(String flag) {
        return flags.containsKey(flag.toLowerCase());
    }

    public String getFlagValue(String flag) {
        return flags.get(flag.toLowerCase());
    }

    public String getFlagValue(String flag, String defaultValue) {
        return flags.getOrDefault(flag.toLowerCase(), defaultValue);
    }

    public List<String> getRegularArgs() {
        return Collections.unmodifiableList(regularArgs);
    }

    public Map<String, String> getAllFlags() {
        return Collections.unmodifiableMap(flags);
    }

    // Convenience methods for common flags
    public boolean isDelete() {
        return hasFlag("d") || hasFlag("delete");
    }

    public boolean isGlobal() {
        return hasFlag("g");
    }

    public boolean isIpBased() {
        return hasFlag("i");
    }

    public boolean isModify() {
        return hasFlag("m") || hasFlag("modify");
    }

    public boolean isNoOverride() {
        return hasFlag("n");
    }

    public boolean isPublic() {
        return hasFlag("p");
    }

    public boolean isSilent() {
        return hasFlag("s");
    }

    public boolean isExtraSilent() {
        return hasFlag("s") && "extra".equalsIgnoreCase(getFlagValue("s", ""));
    }

    public boolean isHidden() {
        return hasFlag("hide");
    }

    public boolean isSkip() {
        return hasFlag("skip");
    }

    public boolean isNoQueue() {
        return hasFlag("no-queue");
    }

    public boolean isConfirm() {
        return hasFlag("confirm");
    }

    public boolean isEvidence() {
        return hasFlag("e") || hasFlag("evidence");
    }

    public String getSender() {
        return getFlagValue("sender");
    }

    public String getSenderUuid() {
        return getFlagValue("sender-uuid");
    }

    public String getServerOrigin() {
        return getFlagValue("server-origin");
    }

    /**
     * Get the silence level based on flags
     * @return 0 = normal, 1 = silent, 2 = extra silent, 3 = hidden
     */
    public int getSilenceLevel() {
        if (isHidden()) return 3;

        String silentValue = getFlagValue("s", "");
        if (hasFlag("s")) {
            if ("extra".equalsIgnoreCase(silentValue) || "true".equals(silentValue)) {
                return hasFlag("s") && silentValue.isEmpty() ? 1 : 2;
            }
            return 1;
        }

        return 0;
    }

    /**
     * Joins the regular arguments into a single string starting from the given index
     */
    public String joinArgs(int fromIndex) {
        if (fromIndex >= regularArgs.size()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = fromIndex; i < regularArgs.size(); i++) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(regularArgs.get(i));
        }
        return sb.toString();
    }

    /**
     * Gets a regular argument at the specified index, or null if out of bounds
     */
    public String getArg(int index) {
        return index >= 0 && index < regularArgs.size() ? regularArgs.get(index) : null;
    }

    /**
     * Gets the number of regular arguments (excluding flags)
     */
    public int getArgCount() {
        return regularArgs.size();
    }

    // Disguise-specific convenience methods

    public boolean isHideRank() {
        return hasFlag("hide-rank");
    }

    public String getFakeRank() {
        return getFlagValue("fake-rank");
    }

    public String getSetSkin() {
        return getFlagValue("set-skin");
    }

    public String getSetDisplayName() {
        return getFlagValue("set-display-name");
    }

    public boolean isChangeTab() {
        return hasFlag("change-tab");
    }

    public boolean isChangeTabComplete() {
        return hasFlag("change-tab-complete");
    }
}
