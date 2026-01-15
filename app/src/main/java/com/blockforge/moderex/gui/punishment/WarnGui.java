package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class WarnGui extends BaseGui {

    private final OfflinePlayer target;
    private String selectedReason = "No reason specified";

    private static final String[] REASONS = {
            "Minor rule violation", "Spam warning", "Language warning", "Behavior warning",
            "First offense", "Final warning", "Custom..."
    };

    public WarnGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<aqua>Warn: <white>" + target.getName(), 4);
        this.target = target;
    }

    @Override
    protected void populate() {
        fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        // Header
        setItem(4, createItem(Material.BOOK, "<aqua>Warn " + target.getName(),
                "<gray>Select a reason for the warning"));

        // Reason options (row 2)
        int slot = 10;
        for (String reason : REASONS) {
            if (slot == 17) break;

            final String r = reason;
            boolean selected = r.equals(selectedReason);
            boolean isCustom = reason.equals("Custom...");

            Material mat = isCustom ? Material.NAME_TAG : (selected ? Material.LIME_DYE : Material.GRAY_DYE);

            setItem(slot, createItem(mat,
                    (selected ? "<green>" : "<yellow>") + reason,
                    isCustom ? "<gray>Click to enter custom reason" : (selected ? "<green>Selected" : "<gray>Click to select")),
                    () -> {
                        if (isCustom) {
                            close();
                            promptInput("<yellow>Enter the warning reason:", input -> {
                                selectedReason = input;
                                plugin.getGuiManager().open(viewer, this);
                            });
                        } else {
                            selectedReason = r;
                            refresh();
                        }
                    });
            slot++;
        }

        // Confirm button
        setItem(22, createConfirmButton(), this::executeWarn);

        // Back button
        setItem(27, createBackButton(), () -> openGui(new PunishPlayerGui(plugin, target)));

        // Close button
        setItem(35, createCloseButton(), this::close);
    }

    private void executeWarn() {
        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String command = String.format("warn %s %s",
                    target.getName(),
                    selectedReason);

            plugin.getServer().dispatchCommand(viewer, command);
        });
    }
}
