package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.anticheat.AnticheatAlertManager;
import com.blockforge.moderex.hooks.anticheat.AnticheatAlertManager.AnticheatCheckRule;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.staff.StaffSettings.CheckAlertPreference;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Per-staff anticheat alert configuration GUI.
 * Shows all detected checks in a paginated list.
 * Staff can configure: alert level, threshold count, time window for each check.
 */
public class AnticheatRulesGui extends BaseGui {

    private int currentPage = 0;
    private static final int CHECKS_PER_PAGE = 28; // 4 rows of 7
    private StaffSettings staffSettings;
    private List<DetectedCheck> allChecks = new ArrayList<>();

    public AnticheatRulesGui(ModereX plugin) {
        super(plugin, "<gradient:#ff6b6b:#ee5a5a><bold>ANTICHEAT ALERTS</bold></gradient>", 6);
    }

    @Override
    protected void populate() {
        staffSettings = plugin.getStaffSettingsManager().getSettings(viewer);
        loadAllChecks();

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

        // Title row info
        int configured = 0;
        for (DetectedCheck check : allChecks) {
            CheckAlertPreference pref = staffSettings.getCheckAlertPreference(check.anticheat, check.checkName);
            if (pref.isConfigured()) configured++;
        }

        setItem(4, createItem(Material.IRON_SWORD, "<gold><bold>Your Alert Settings</bold>",
                "<gray>Configure which anticheat alerts",
                "<gray>you want to receive in chat.",
                "",
                "<white>" + allChecks.size() + " <gray>checks detected",
                "<green>" + configured + " <gray>configured",
                "",
                "<dark_gray>Unconfigured = No alerts"));

        // Render checks (slots 10-16, 19-25, 28-34, 37-43)
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };

        int totalPages = (int) Math.ceil(allChecks.size() / (double) CHECKS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int startIndex = currentPage * CHECKS_PER_PAGE;
        for (int i = 0; i < slots.length && (startIndex + i) < allChecks.size(); i++) {
            DetectedCheck check = allChecks.get(startIndex + i);
            setItem(slots[i], createCheckItem(check), clickType -> {
                openCheckEditor(check);
            });
        }

        if (allChecks.isEmpty()) {
            setItem(22, createItem(Material.GRAY_DYE, "<gray>No Checks Detected",
                    "<gray>Join a server with an anticheat",
                    "<gray>and trigger some alerts first"));
        }

        // Bottom navigation
        int lastRow = 45;

        // Back button
        setItem(lastRow, createItem(Material.ARROW, "<yellow>Back",
                "<gray>Return to staff settings"), () -> {
            plugin.getGuiManager().open(viewer, new StaffSettingsGui(plugin));
        });

        // Previous page
        if (currentPage > 0) {
            setItem(lastRow + 2, createItem(Material.ARROW, "<green>Previous",
                    "<gray>Page " + currentPage), () -> {
                currentPage--;
                refresh();
            });
        }

        // Page indicator
        setItem(lastRow + 4, createItem(Material.PAPER,
                "<gold>Page " + (currentPage + 1) + "/" + totalPages,
                "<gray>" + allChecks.size() + " total checks"));

        // Next page
        if (currentPage < totalPages - 1) {
            setItem(lastRow + 6, createItem(Material.ARROW, "<green>Next",
                    "<gray>Page " + (currentPage + 2)), () -> {
                currentPage++;
                refresh();
            });
        }

        // Close button
        setItem(lastRow + 8, createItem(Material.BARRIER, "<red>Close"), this::close);

        // Enable all button
        setItem(lastRow + 1, createItem(Material.LIME_DYE, "<green>Enable All",
                "<gray>Set all checks to Everyone",
                "<gray>with default thresholds"), () -> {
            for (DetectedCheck check : allChecks) {
                CheckAlertPreference pref = staffSettings.getCheckAlertPreference(check.anticheat, check.checkName);
                pref.setAlertLevel(AlertLevel.EVERYONE);
            }
            plugin.getStaffSettingsManager().saveSettings(staffSettings);
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            refresh();
        });

        // Disable all button
        setItem(lastRow + 7, createItem(Material.RED_DYE, "<red>Disable All",
                "<gray>Turn off all alerts"), () -> {
            for (DetectedCheck check : allChecks) {
                CheckAlertPreference pref = staffSettings.getCheckAlertPreference(check.anticheat, check.checkName);
                pref.setAlertLevel(AlertLevel.OFF);
            }
            plugin.getStaffSettingsManager().saveSettings(staffSettings);
            viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            refresh();
        });
    }

    private void loadAllChecks() {
        allChecks.clear();

        // Get all rules from alert manager (these are detected checks)
        AnticheatAlertManager alertManager = plugin.getAnticheatManager().getAlertManager();
        for (AnticheatCheckRule rule : alertManager.getRules()) {
            allChecks.add(new DetectedCheck(rule.getAnticheat(), rule.getCheckName()));
        }

        // Sort by anticheat then check name
        allChecks.sort((a, b) -> {
            int acCompare = a.anticheat.compareToIgnoreCase(b.anticheat);
            if (acCompare != 0) return acCompare;
            return a.checkName.compareToIgnoreCase(b.checkName);
        });
    }

    private ItemStack createCheckItem(DetectedCheck check) {
        CheckAlertPreference pref = staffSettings.getCheckAlertPreference(check.anticheat, check.checkName);
        boolean configured = pref.isConfigured();

        List<String> lore = new ArrayList<>();
        lore.add("<dark_gray>" + check.anticheat);
        lore.add("");

        if (configured) {
            lore.add("<gray>Alert Level: " + pref.getAlertLevel().getColor() + pref.getAlertLevel().getDisplayName());
            lore.add("<gray>After: <white>" + pref.getThresholdCount() + " alerts");
            lore.add("<gray>Within: <white>" + pref.getTimeWindowSeconds() + " seconds");
            lore.add("");
            lore.add("<green>Configured");
        } else {
            lore.add("<red>Not configured");
            lore.add("<dark_gray>You won't receive alerts");
        }

        lore.add("");
        lore.add("<yellow>Click to configure");

        Material material;
        String color;
        if (!configured) {
            material = Material.GRAY_DYE;
            color = "<gray>";
        } else {
            switch (pref.getAlertLevel()) {
                case EVERYONE -> {
                    material = Material.LIME_DYE;
                    color = "<green>";
                }
                case WATCHLIST_ONLY -> {
                    material = Material.YELLOW_DYE;
                    color = "<yellow>";
                }
                default -> {
                    material = Material.RED_DYE;
                    color = "<red>";
                }
            }
        }

        return createItem(material, color + check.checkName, lore);
    }

    private void openCheckEditor(DetectedCheck check) {
        plugin.getGuiManager().open(viewer, new CheckEditorGui(plugin, check.anticheat, check.checkName, this));
    }

    // Simple holder for detected check info
    private static class DetectedCheck {
        final String anticheat;
        final String checkName;

        DetectedCheck(String anticheat, String checkName) {
            this.anticheat = anticheat;
            this.checkName = checkName;
        }
    }

    // ========== Check Editor GUI ==========

    public static class CheckEditorGui extends BaseGui {

        private final String anticheat;
        private final String checkName;
        private final AnticheatRulesGui parent;
        private StaffSettings staffSettings;
        private CheckAlertPreference pref;

        public CheckEditorGui(ModereX plugin, String anticheat, String checkName, AnticheatRulesGui parent) {
            super(plugin, "<gradient:#ff6b6b:#ee5a5a>Configure: " + checkName + "</gradient>", 4);
            this.anticheat = anticheat;
            this.checkName = checkName;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            staffSettings = plugin.getStaffSettingsManager().getSettings(viewer);
            pref = staffSettings.getCheckAlertPreference(anticheat, checkName);

            fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

            // Title
            setItem(4, createItem(Material.IRON_SWORD, "<gold>" + checkName,
                    "<dark_gray>" + anticheat,
                    "",
                    "<gray>Configure when you receive",
                    "<gray>alerts for this check"));

            // Alert Level (slot 11)
            setItem(11, createAlertLevelItem(), () -> {
                pref.setAlertLevel(pref.getAlertLevel().next());
                saveAndRefresh();
            });

            // Threshold Count (slot 13)
            setItem(13, createItem(Material.REDSTONE,
                    "<gold>Alert After: <white>" + pref.getThresholdCount() + " triggers",
                    "<gray>How many times the check must",
                    "<gray>trigger before you get an alert",
                    "",
                    "<yellow>Left: +1 | Right: -1",
                    "<yellow>Shift: +/-5"), clickType -> {
                int delta = clickType.isShiftClick() ? 5 : 1;
                if (clickType.isLeftClick()) {
                    pref.setThresholdCount(Math.min(50, pref.getThresholdCount() + delta));
                } else if (clickType.isRightClick()) {
                    pref.setThresholdCount(Math.max(1, pref.getThresholdCount() - delta));
                }
                saveAndRefresh();
            });

            // Time Window (slot 15)
            setItem(15, createItem(Material.CLOCK,
                    "<gold>Within: <white>" + pref.getTimeWindowSeconds() + " seconds",
                    "<gray>Time window for counting triggers",
                    "",
                    "<yellow>Click to enter custom time",
                    "<yellow>Left: +10s | Right: -10s"), clickType -> {
                if (clickType == org.bukkit.event.inventory.ClickType.MIDDLE || clickType == org.bukkit.event.inventory.ClickType.DROP) {
                    promptTimeWindow();
                } else if (clickType.isLeftClick()) {
                    pref.setTimeWindowSeconds(Math.min(600, pref.getTimeWindowSeconds() + 10));
                    saveAndRefresh();
                } else if (clickType.isRightClick()) {
                    pref.setTimeWindowSeconds(Math.max(5, pref.getTimeWindowSeconds() - 10));
                    saveAndRefresh();
                }
            });

            // Enter custom time button
            setItem(22, createItem(Material.NAME_TAG, "<aqua>Enter Custom Time",
                    "<gray>Click to type a custom",
                    "<gray>time window in seconds"), this::promptTimeWindow);

            // Quick presets row
            setItem(19, createPresetItem("Quick", AlertLevel.EVERYONE, 3, 30), () -> {
                pref.setAlertLevel(AlertLevel.EVERYONE);
                pref.setThresholdCount(3);
                pref.setTimeWindowSeconds(30);
                saveAndRefresh();
            });

            setItem(20, createPresetItem("Normal", AlertLevel.EVERYONE, 5, 60), () -> {
                pref.setAlertLevel(AlertLevel.EVERYONE);
                pref.setThresholdCount(5);
                pref.setTimeWindowSeconds(60);
                saveAndRefresh();
            });

            setItem(21, createPresetItem("Relaxed", AlertLevel.EVERYONE, 10, 120), () -> {
                pref.setAlertLevel(AlertLevel.EVERYONE);
                pref.setThresholdCount(10);
                pref.setTimeWindowSeconds(120);
                saveAndRefresh();
            });

            setItem(23, createPresetItem("Watchlist", AlertLevel.WATCHLIST_ONLY, 3, 60), () -> {
                pref.setAlertLevel(AlertLevel.WATCHLIST_ONLY);
                pref.setThresholdCount(3);
                pref.setTimeWindowSeconds(60);
                saveAndRefresh();
            });

            setItem(24, createPresetItem("Off", AlertLevel.OFF, 5, 60), () -> {
                pref.setAlertLevel(AlertLevel.OFF);
                saveAndRefresh();
            });

            // Bottom navigation
            setItem(27, createItem(Material.ARROW, "<yellow>Back",
                    "<gray>Return to check list"), () -> {
                plugin.getGuiManager().open(viewer, parent);
            });

            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close",
                    "<gray>Save and return"), () -> {
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                viewer.sendMessage(TextUtil.parse("<green>Settings saved for " + checkName + "!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                plugin.getGuiManager().open(viewer, parent);
            });
        }

        private ItemStack createAlertLevelItem() {
            AlertLevel level = pref.getAlertLevel();
            Material material = switch (level) {
                case EVERYONE -> Material.LIME_DYE;
                case WATCHLIST_ONLY -> Material.YELLOW_DYE;
                case OFF -> Material.RED_DYE;
            };

            List<String> lore = new ArrayList<>();
            lore.add("<gray>" + level.getDescription());
            lore.add("");
            for (AlertLevel l : AlertLevel.values()) {
                String prefix = l == level ? "<white>▶ " : "<dark_gray>  ";
                lore.add(prefix + l.getColor() + l.getDisplayName());
            }
            lore.add("");
            lore.add("<yellow>Click to cycle");

            return createItem(material, level.getColor() + "Alert Level: " + level.getDisplayName(), lore);
        }

        private ItemStack createPresetItem(String name, AlertLevel level, int threshold, int window) {
            String color = switch (level) {
                case EVERYONE -> "<green>";
                case WATCHLIST_ONLY -> "<yellow>";
                case OFF -> "<red>";
            };

            List<String> lore = new ArrayList<>();
            lore.add("<gray>Level: " + color + level.getDisplayName());
            if (level != AlertLevel.OFF) {
                lore.add("<gray>After: <white>" + threshold + " triggers");
                lore.add("<gray>Within: <white>" + window + "s");
            }
            lore.add("");
            lore.add("<yellow>Click to apply");

            return createItem(Material.PAPER, color + name + " Preset", lore);
        }

        private void promptTimeWindow() {
            close();
            viewer.sendMessage(TextUtil.parse("<aqua>Enter time window in seconds (5-600):"));
            viewer.sendMessage(TextUtil.parse("<gray>Type 'cancel' to cancel"));

            new ConversationFactory(plugin)
                    .withModality(true)
                    .withFirstPrompt(new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return "";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (input.equalsIgnoreCase("cancel")) {
                                context.getForWhom().sendRawMessage(toLegacy("<red>Cancelled."));
                            } else {
                                try {
                                    int seconds = Integer.parseInt(input.trim());
                                    if (seconds >= 5 && seconds <= 600) {
                                        pref.setTimeWindowSeconds(seconds);
                                        plugin.getStaffSettingsManager().saveSettings(staffSettings);
                                        context.getForWhom().sendRawMessage(toLegacy("<green>Time window set to " + seconds + " seconds."));
                                    } else {
                                        context.getForWhom().sendRawMessage(toLegacy("<red>Must be between 5 and 600 seconds."));
                                    }
                                } catch (NumberFormatException e) {
                                    context.getForWhom().sendRawMessage(toLegacy("<red>Invalid number."));
                                }
                            }

                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                plugin.getGuiManager().open(viewer, CheckEditorGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }
                    })
                    .withLocalEcho(false)
                    .withTimeout(60)
                    .buildConversation(viewer)
                    .begin();
        }

        private void saveAndRefresh() {
            plugin.getStaffSettingsManager().saveSettings(staffSettings);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        }

        private static String toLegacy(String miniMessage) {
            return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
        }
    }
}
