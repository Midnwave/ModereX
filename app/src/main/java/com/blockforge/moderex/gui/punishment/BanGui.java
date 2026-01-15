package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class BanGui extends BaseGui {

    private final OfflinePlayer target;
    private String selectedDuration = null;
    private String selectedReason = "No reason specified";

    private static final String[] DURATIONS = {
            "1d", "3d", "7d", "14d", "30d", "90d", "180d", "365d", "permanent"
    };

    private static final String[] REASONS = {
            "Cheating/Hacking", "Exploiting", "Ban evasion", "Extreme toxicity",
            "Threats", "Doxxing", "Chargebacks", "Custom..."
    };

    public BanGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<dark_red>Ban: <red>" + target.getName(), 5);
        this.target = target;
    }

    @Override
    protected void populate() {
        fillBorder(Material.RED_STAINED_GLASS_PANE);

        // Duration selection header
        setItem(3, createItem(Material.CLOCK, "<red>Select Duration",
                "<gray>Choose how long to ban"));

        // Duration options (row 2)
        int slot = 10;
        for (String duration : DURATIONS) {
            if (slot == 17) slot = 19;
            if (slot > 25) break;

            final String dur = duration;
            boolean selected = dur.equals(selectedDuration);
            Material mat = selected ? Material.LIME_DYE : Material.GRAY_DYE;
            String displayDuration = duration.equals("permanent") ? "Permanent" : formatDurationDisplay(duration);

            setItem(slot, createItem(mat,
                    (selected ? "<green>" : "<yellow>") + displayDuration,
                    selected ? "<green>Selected" : "<gray>Click to select"),
                    () -> {
                        selectedDuration = dur;
                        refresh();
                    });
            slot++;
        }

        // Reason selection header
        setItem(30, createItem(Material.PAPER, "<red>Select Reason",
                "<gray>Choose a reason for the ban"));

        // Reason options (row 4)
        slot = 28;
        for (int i = 0; i < REASONS.length && slot < 35; i++) {
            if (slot == 27 || slot == 35) {
                slot++;
                continue;
            }

            final String reason = REASONS[i];
            boolean selected = reason.equals(selectedReason);
            boolean isCustom = reason.equals("Custom...");

            Material mat = isCustom ? Material.NAME_TAG : (selected ? Material.LIME_DYE : Material.GRAY_DYE);

            setItem(slot, createItem(mat,
                    (selected ? "<green>" : "<yellow>") + reason,
                    isCustom ? "<gray>Click to enter custom reason" : (selected ? "<green>Selected" : "<gray>Click to select")),
                    () -> {
                        if (isCustom) {
                            close();
                            promptInput("<yellow>Enter the ban reason:", input -> {
                                selectedReason = input;
                                plugin.getGuiManager().open(viewer, this);
                            });
                        } else {
                            selectedReason = reason;
                            refresh();
                        }
                    });
            slot++;
        }

        // Confirm button
        if (selectedDuration != null) {
            setItem(40, createConfirmButton(), this::executeBan);
        } else {
            setItem(40, createItem(Material.GRAY_CONCRETE, "<gray>Select a duration",
                    "<gray>You must select a duration to proceed"));
        }

        // Back button
        setItem(36, createBackButton(), () -> openGui(new PunishPlayerGui(plugin, target)));

        // Close button
        setItem(44, createCloseButton(), this::close);
    }

    private void executeBan() {
        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String command = String.format("ban %s %s %s",
                    target.getName(),
                    selectedDuration,
                    selectedReason);

            plugin.getServer().dispatchCommand(viewer, command);
        });
    }

    private String formatDurationDisplay(String duration) {
        if (duration == null || duration.isEmpty()) return "Unknown";

        StringBuilder result = new StringBuilder();
        StringBuilder number = new StringBuilder();

        for (char c : duration.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    int value = Integer.parseInt(number.toString());
                    String unit = switch (Character.toLowerCase(c)) {
                        case 's' -> value == 1 ? "Second" : "Seconds";
                        case 'm' -> value == 1 ? "Minute" : "Minutes";
                        case 'h' -> value == 1 ? "Hour" : "Hours";
                        case 'd' -> value == 1 ? "Day" : "Days";
                        case 'w' -> value == 1 ? "Week" : "Weeks";
                        default -> "";
                    };
                    if (!unit.isEmpty()) {
                        if (result.length() > 0) result.append(" ");
                        result.append(value).append(" ").append(unit);
                    }
                    number.setLength(0);
                }
            }
        }
        return result.length() > 0 ? result.toString() : duration;
    }
}
