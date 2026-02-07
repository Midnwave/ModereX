package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.evidence.EvidenceSelectionSession;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.log.ActivityLogEntry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GUI for Bedrock/Geyser players to select activity log entries as evidence.
 * Works like the chat-based evidence selection but in a chest inventory.
 */
public class EvidenceSelectionGui extends BaseGui {

    private final EvidenceSelectionSession session;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public EvidenceSelectionGui(ModereX plugin, EvidenceSelectionSession session) {
        super(plugin, "<dark_gray>Select Evidence - " + session.getTarget().getDisplayName(), 6);
        this.session = session;
    }

    @Override
    protected void populate() {
        List<ActivityLogEntry> entries = session.getAvailableEntries();

        // Fill border with gray glass
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Info item in top center
        ItemStack infoItem = createItem(Material.BOOK, "<gold><bold>Evidence Selection",
                "<gray>Target: <white>" + session.getTarget().getDisplayName(),
                "<gray>Action: <red>" + session.getPunishmentType(),
                "",
                "<yellow>Selected: <white>" + session.getSelectionCount() + "/" + EvidenceSelectionSession.MAX_SELECTIONS,
                "",
                "<gray>Click entries to select/deselect",
                "<gray>Selected items glow");
        setItem(4, infoItem);

        // Display entries in slots 10-34 (middle rows)
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int maxDisplay = Math.min(entries.size(), slots.length);

        for (int i = 0; i < maxDisplay; i++) {
            ActivityLogEntry entry = entries.get(i);
            int slot = slots[i];
            int displayNum = i + 1;
            boolean selected = session.isSelected(entry.getId());

            ItemStack item = createEntryItem(entry, displayNum, selected);
            final int entryIndex = i;
            setItem(slot, item, () -> {
                toggleEntry(entryIndex);
            });
        }

        // Bottom row controls

        // Cancel button (slot 45 - bottom left)
        ItemStack cancelBtn = createItem(Material.RED_WOOL, "<red><bold>Cancel",
                "<gray>Abort the punishment",
                "<gray>No action will be taken");
        setItem(45, cancelBtn, () -> {
            close();
            session.cancel();
        });

        // Skip button (slot 49 - bottom center)
        ItemStack skipBtn = createItem(Material.YELLOW_WOOL, "<yellow><bold>Skip Evidence",
                "<gray>Proceed without evidence",
                "<gray>Punishment will be issued",
                "<gray>with no attached evidence");
        setItem(49, skipBtn, () -> {
            close();
            session.skip();
        });

        // Confirm button (slot 53 - bottom right)
        if (session.hasSelections()) {
            ItemStack confirmBtn = createItem(Material.LIME_WOOL, "<green><bold>Confirm",
                    "<gray>Attach " + session.getSelectionCount() + " entries as evidence",
                    "<gray>and proceed with punishment");
            setItem(53, confirmBtn, () -> {
                close();
                session.confirm();
            });
        } else {
            ItemStack confirmBtn = createItem(Material.GRAY_WOOL, "<gray><bold>Confirm",
                    "<red>Select at least one entry",
                    "<gray>or click Skip to proceed",
                    "<gray>without evidence");
            setItem(53, confirmBtn);
        }
    }

    private ItemStack createEntryItem(ActivityLogEntry entry, int displayNum, boolean selected) {
        Material material = getMaterialForType(entry.getType());
        String typeColor = getTypeColor(entry.getType());

        // Truncate content if too long
        String content = entry.getContent();
        if (content.length() > 35) {
            content = content.substring(0, 32) + "...";
        }

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Type: " + typeColor + entry.getTypeDisplayName());
        lore.add("<gray>Time: <white>" + timeFormat.format(new Date(entry.getTimestamp())));
        lore.add("");
        lore.add("<white>" + content);
        lore.add("");
        if (selected) {
            lore.add("<green>✓ Selected");
            lore.add("<gray>Click to deselect");
        } else {
            lore.add("<yellow>Click to select");
        }

        String nameColor = selected ? "<green>" : typeColor;
        ItemStack item = createItem(material, nameColor + "#" + displayNum + " " + entry.getTypeDisplayName(), lore);

        // Add glow effect if selected
        if (selected) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }

        return item;
    }

    private void toggleEntry(int index) {
        List<ActivityLogEntry> entries = session.getAvailableEntries();
        if (index < 0 || index >= entries.size()) return;

        ActivityLogEntry entry = entries.get(index);

        if (session.isSelected(entry.getId())) {
            session.deselectEntry(entry.getId());
        } else {
            if (!session.canSelectMore()) {
                viewer.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(net.kyori.adventure.text.Component.text(
                                "Maximum " + EvidenceSelectionSession.MAX_SELECTIONS + " entries can be selected",
                                net.kyori.adventure.text.format.NamedTextColor.RED)));
                return;
            }
            session.selectEntry(entry.getId());
        }

        refresh();
    }

    private Material getMaterialForType(ActivityLogEntry.ActivityType type) {
        return switch (type) {
            case CHAT -> Material.PAPER;
            case COMMAND -> Material.COMMAND_BLOCK;
            case AUTOMOD_TRIGGER -> Material.BARRIER;
            case ANTICHEAT_ALERT -> Material.IRON_SWORD;
            case SIGN -> Material.OAK_SIGN;
            case ANVIL_RENAME -> Material.ANVIL;
            default -> Material.BOOK;
        };
    }

    private String getTypeColor(ActivityLogEntry.ActivityType type) {
        return switch (type) {
            case CHAT -> "<aqua>";
            case COMMAND -> "<yellow>";
            case AUTOMOD_TRIGGER -> "<gold>";
            case ANTICHEAT_ALERT -> "<red>";
            case SIGN, ANVIL_RENAME -> "<light_purple>";
            default -> "<gray>";
        };
    }

    @Override
    public void onClose() {
        super.onClose();
        // If closed without confirming, treat as cancel
        if (session.isActive()) {
            session.cancel();
        }
    }
}
