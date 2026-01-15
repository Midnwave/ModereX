package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

public class KickGui extends BaseGui {

    private final OfflinePlayer target;
    private String selectedReason = "No reason specified";

    private static final String[] REASONS = {
            "AFK", "Server maintenance", "Rule violation", "Spam",
            "Inappropriate behavior", "Connection issues", "Custom..."
    };

    public KickGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, "<yellow>Kick: <gold>" + target.getName(), 4);
        this.target = target;
    }

    @Override
    protected void populate() {
        fillBorder(Material.YELLOW_STAINED_GLASS_PANE);

        // Header
        setItem(4, createItem(Material.LEATHER_BOOTS, "<yellow>Kick " + target.getName(),
                "<gray>Select a reason for the kick"));

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
                            promptInput("<yellow>Enter the kick reason:", input -> {
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
        setItem(22, createConfirmButton(), this::executeKick);

        // Back button
        setItem(27, createBackButton(), () -> openGui(new PunishPlayerGui(plugin, target)));

        // Close button
        setItem(35, createCloseButton(), this::close);
    }

    private void executeKick() {
        if (!target.isOnline()) {
            viewer.sendMessage(TextUtil.parse(
                    "<red>Player " + target.getName() + " is not online!"));
            close();
            return;
        }

        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String command = String.format("kick %s %s",
                    target.getName(),
                    selectedReason);

            plugin.getServer().dispatchCommand(viewer, command);
        });
    }
}
