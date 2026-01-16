package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.hooks.anticheat.AnticheatAlertManager;
import com.blockforge.moderex.hooks.anticheat.AnticheatAlertManager.AnticheatCheckRule;
import com.blockforge.moderex.hooks.anticheat.AnticheatAlertManager.CheckWithInfo;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks.Category;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks.CheckInfo;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AnticheatRulesGui extends BaseGui {

    private String selectedAnticheat = null;
    private Category selectedCategory = null;
    private int checkPage = 0;
    private static final int CHECKS_PER_PAGE = 21;

    public AnticheatRulesGui(ModereX plugin) {
        super(plugin, "<gradient:#ff6b6b:#ee5a5a><bold>ANTICHEAT RULES</bold></gradient>", 6);
    }

    @Override
    protected void populate() {
        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

        AnticheatAlertManager alertManager = plugin.getAnticheatManager().getAlertManager();
        List<String> enabledAnticheats = plugin.getAnticheatManager().getEnabledAnticheats();

        if (selectedAnticheat == null) {
            // Show anticheat selection
            renderAnticheatSelection(enabledAnticheats);
        } else if (selectedCategory == null) {
            // Show category selection
            renderCategorySelection();
        } else {
            // Show checks for selected category
            renderChecks();
        }

        // Bottom navigation
        int lastRow = rows * 9 - 9;

        if (selectedCategory != null) {
            setItem(lastRow, createItem(Material.ARROW, "<yellow>Back to Categories",
                    "<gray>Return to category selection"), () -> {
                selectedCategory = null;
                checkPage = 0;
                refresh();
            });
        } else if (selectedAnticheat != null) {
            setItem(lastRow, createItem(Material.ARROW, "<yellow>Back to Anticheats",
                    "<gray>Return to anticheat selection"), () -> {
                selectedAnticheat = null;
                refresh();
            });
        } else {
            setItem(lastRow, createItem(Material.ARROW, "<yellow>Back",
                    "<gray>Return to main menu"), () -> {
                plugin.getGuiManager().open(viewer, new StaffSettingsGui(plugin));
            });
        }

        setItem(lastRow + 8, createItem(Material.BARRIER, "<red>Close", "<gray>Close this menu"), this::close);
    }

    private void renderAnticheatSelection(List<String> enabledAnticheats) {
        // Title
        setItem(4, createItem(Material.IRON_SWORD, "<gold><bold>Select Anticheat</bold>",
                "<gray>Choose an anticheat to configure",
                "",
                "<white>" + enabledAnticheats.size() + " anticheat(s) detected"));

        if (enabledAnticheats.isEmpty()) {
            setItem(22, createItem(Material.GRAY_DYE, "<red>No Anticheats Detected",
                    "<gray>Install an anticheat plugin",
                    "<gray>to configure check rules",
                    "",
                    "<dark_gray>Supported: Grim, Vulcan, Matrix,",
                    "<dark_gray>Spartan, NCP, Themis, FoxAddition, LightAC"));
            return;
        }

        // Display enabled anticheats in center
        int[] slots = getCenteredSlots(enabledAnticheats.size(), 3);
        for (int i = 0; i < enabledAnticheats.size() && i < slots.length; i++) {
            String anticheat = enabledAnticheats.get(i);
            int ruleCount = alertManager().getRulesForAnticheat(anticheat).size();

            setItem(slots[i], createItem(getMaterialForAnticheat(anticheat),
                    "<green>" + anticheat,
                    "<gray>Click to configure",
                    "",
                    "<white>" + ruleCount + " <gray>rules configured",
                    "",
                    "<yellow>Click to select"), () -> {
                selectedAnticheat = anticheat;
                refresh();
            });
        }

        // Show supported but not detected
        List<String> notDetected = new ArrayList<>();
        for (String supported : AnticheatChecks.getSupportedAnticheats()) {
            if (!enabledAnticheats.stream().anyMatch(e -> e.equalsIgnoreCase(supported))) {
                notDetected.add(supported);
            }
        }

        if (!notDetected.isEmpty()) {
            String notDetectedStr = String.join(", ", notDetected);
            setItem(49, createItem(Material.GRAY_DYE, "<gray>Not Detected",
                    "<dark_gray>" + notDetectedStr));
        }
    }

    private void renderCategorySelection() {
        // Title
        setItem(4, createItem(getMaterialForAnticheat(selectedAnticheat),
                "<gold><bold>" + selectedAnticheat.toUpperCase() + "</bold>",
                "<gray>Select a category to configure"));

        Map<Category, List<CheckWithInfo>> checksByCategory =
                alertManager().getChecksByCategory(selectedAnticheat);

        // Display categories centered
        int slot = 19;
        for (Category category : Category.values()) {
            List<CheckWithInfo> checks = checksByCategory.getOrDefault(category, Collections.emptyList());
            if (checks.isEmpty()) continue;

            long enabledCount = checks.stream().filter(CheckWithInfo::isEnabled).count();
            long detectedCount = checks.stream().filter(CheckWithInfo::wasDetected).count();

            setItem(slot, createItem(getMaterialForCategory(category),
                    getColorForCategory(category) + category.getDisplayName(),
                    "<gray>" + category.getDescription(),
                    "",
                    "<white>" + checks.size() + " <gray>checks",
                    "<green>" + enabledCount + " enabled <dark_gray>| <yellow>" + detectedCount + " detected",
                    "",
                    "<yellow>Click to configure"), () -> {
                selectedCategory = category;
                checkPage = 0;
                refresh();
            });

            slot++;
            if (slot == 26) slot = 28; // Skip to next row
        }

        // Quick stats
        Collection<AnticheatCheckRule> allRules = alertManager().getRulesForAnticheat(selectedAnticheat);
        long withPunishment = allRules.stream()
                .filter(r -> r.getAutoPunishment() != null && r.getAutoPunishment().isEnabled())
                .count();

        setItem(40, createItem(Material.PAPER, "<aqua>Statistics",
                "<gray>Total rules: <white>" + allRules.size(),
                "<gray>With auto-punishment: <white>" + withPunishment));
    }

    private void renderChecks() {
        // Title
        setItem(4, createItem(getMaterialForCategory(selectedCategory),
                getColorForCategory(selectedCategory) + "<bold>" + selectedCategory.getDisplayName().toUpperCase() + "</bold>",
                "<gray>" + selectedAnticheat + " - " + selectedCategory.getDescription()));

        List<CheckWithInfo> checks = alertManager().getAllChecksForAnticheat(selectedAnticheat)
                .stream()
                .filter(c -> c.getInfo().getCategory() == selectedCategory)
                .toList();

        int totalPages = (int) Math.ceil(checks.size() / (double) CHECKS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Display checks
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        };

        int startIndex = checkPage * CHECKS_PER_PAGE;
        for (int i = 0; i < slots.length && (startIndex + i) < checks.size(); i++) {
            CheckWithInfo check = checks.get(startIndex + i);
            setItem(slots[i], createCheckItem(check), clickType -> {
                if (clickType.isShiftClick()) {
                    // Toggle enabled
                    toggleCheck(check);
                } else {
                    // Open editor
                    openCheckEditor(check);
                }
            });
        }

        if (checks.isEmpty()) {
            setItem(22, createItem(Material.GRAY_DYE, "<gray>No Checks",
                    "<gray>No checks in this category"));
        }

        // Pagination
        int lastRow = rows * 9 - 9;

        if (checkPage > 0) {
            setItem(lastRow + 3, createItem(Material.ARROW, "<green>Previous Page",
                    "<gray>Go to page " + checkPage), () -> {
                checkPage--;
                refresh();
            });
        }

        setItem(lastRow + 4, createItem(Material.PAPER,
                "<gold>Page " + (checkPage + 1) + "/" + totalPages,
                "<gray>" + checks.size() + " checks"));

        if (checkPage < totalPages - 1) {
            setItem(lastRow + 5, createItem(Material.ARROW, "<green>Next Page",
                    "<gray>Go to page " + (checkPage + 2)), () -> {
                checkPage++;
                refresh();
            });
        }

        // Bulk actions
        setItem(lastRow + 6, createItem(Material.LIME_DYE, "<green>Enable All",
                "<gray>Enable all checks in category"), () -> {
            for (CheckWithInfo check : checks) {
                if (check.hasRule()) {
                    check.getRule().setEnabled(true);
                    alertManager().saveRule(check.getRule());
                }
            }
            viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            refresh();
        });

        setItem(lastRow + 7, createItem(Material.RED_DYE, "<red>Disable All",
                "<gray>Disable all checks in category"), () -> {
            for (CheckWithInfo check : checks) {
                AnticheatCheckRule rule = alertManager()
                        .getOrCreateRule(selectedAnticheat, check.getInfo().getName());
                rule.setEnabled(false);
                alertManager().saveRule(rule);
            }
            viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            refresh();
        });
    }

    private ItemStack createCheckItem(CheckWithInfo check) {
        CheckInfo info = check.getInfo();
        AnticheatCheckRule rule = check.getRule();
        boolean enabled = check.isEnabled();
        boolean detected = check.wasDetected();

        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + info.getDescription());
        lore.add("");

        if (detected) {
            lore.add("<green>Detected");
            if (rule != null) {
                lore.add("<gray>Min VL: <white>" + (rule.getMinVL() > 0 ? rule.getMinVL() : "default"));
                lore.add("<gray>Threshold: <white>" + (rule.getThresholdCount() > 0 ?
                        rule.getThresholdCount() + " alerts" : "default"));

                if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
                    AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
                    String duration = punishment.getDuration() == -1 ? "permanent" :
                            DurationParser.format(punishment.getDuration());
                    lore.add("");
                    lore.add("<gold>Auto-Punishment:");
                    lore.add("<gray>  Type: <white>" + punishment.getType().name());
                    if (punishment.getType() != PunishmentType.KICK &&
                        punishment.getType() != PunishmentType.WARN) {
                        lore.add("<gray>  Duration: <white>" + duration);
                    }
                    lore.add("<gray>  After: <white>" + punishment.getTriggerCount() + " violations");
                }
            }
        } else {
            lore.add("<yellow>Not detected yet");
            lore.add("<dark_gray>Will be configured when first alert received");
        }

        lore.add("");
        lore.add(enabled ? "<green>Enabled" : "<red>Disabled");
        lore.add("");
        lore.add("<yellow>Click to edit");
        lore.add("<yellow>Shift+click to toggle");

        Material material;
        if (!enabled) {
            material = Material.RED_DYE;
        } else if (rule != null && rule.getAutoPunishment() != null &&
                   rule.getAutoPunishment().isEnabled()) {
            material = Material.ORANGE_DYE;
        } else if (detected) {
            material = Material.LIME_DYE;
        } else {
            material = Material.GRAY_DYE;
        }

        String title = (enabled ? "<green>" : "<red>") + info.getDisplayName();

        return createItem(material, title, lore);
    }

    private void toggleCheck(CheckWithInfo check) {
        AnticheatCheckRule rule = alertManager()
                .getOrCreateRule(selectedAnticheat, check.getInfo().getName());
        rule.setEnabled(!rule.isEnabled());
        alertManager().saveRule(rule);
        viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        refresh();
    }

    private void openCheckEditor(CheckWithInfo check) {
        AnticheatCheckRule rule = alertManager()
                .getOrCreateRule(selectedAnticheat, check.getInfo().getName());
        plugin.getGuiManager().open(viewer, new CheckEditorGui(plugin, rule, check.getInfo(), this));
    }

    private AnticheatAlertManager alertManager() {
        return plugin.getAnticheatManager().getAlertManager();
    }

    private int[] getCenteredSlots(int count, int row) {
        int startSlot = row * 9;
        return switch (count) {
            case 1 -> new int[]{startSlot + 4};
            case 2 -> new int[]{startSlot + 3, startSlot + 5};
            case 3 -> new int[]{startSlot + 2, startSlot + 4, startSlot + 6};
            case 4 -> new int[]{startSlot + 1, startSlot + 3, startSlot + 5, startSlot + 7};
            case 5 -> new int[]{startSlot + 2, startSlot + 3, startSlot + 4, startSlot + 5, startSlot + 6};
            case 6 -> new int[]{startSlot + 1, startSlot + 2, startSlot + 3, startSlot + 5, startSlot + 6, startSlot + 7};
            case 7 -> new int[]{startSlot + 1, startSlot + 2, startSlot + 3, startSlot + 4, startSlot + 5, startSlot + 6, startSlot + 7};
            default -> new int[]{startSlot + 1, startSlot + 2, startSlot + 3, startSlot + 4, startSlot + 5, startSlot + 6, startSlot + 7};
        };
    }

    private Material getMaterialForAnticheat(String anticheat) {
        return switch (anticheat.toLowerCase()) {
            case "grim" -> Material.NETHERITE_SWORD;
            case "vulcan" -> Material.DIAMOND_SWORD;
            case "matrix" -> Material.IRON_SWORD;
            case "spartan" -> Material.GOLDEN_SWORD;
            case "ncp", "nocheatplus" -> Material.STONE_SWORD;
            case "themis" -> Material.TRIDENT;
            case "foxaddition", "fox" -> Material.CROSSBOW;
            case "lightac", "lightanticheat", "lac" -> Material.BOW;
            default -> Material.IRON_SWORD;
        };
    }

    private Material getMaterialForCategory(Category category) {
        return switch (category) {
            case COMBAT -> Material.DIAMOND_SWORD;
            case MOVEMENT -> Material.FEATHER;
            case PLAYER -> Material.PLAYER_HEAD;
            case WORLD -> Material.GRASS_BLOCK;
            case MISC -> Material.COMPASS;
        };
    }

    private String getColorForCategory(Category category) {
        return switch (category) {
            case COMBAT -> "<red>";
            case MOVEMENT -> "<aqua>";
            case PLAYER -> "<yellow>";
            case WORLD -> "<green>";
            case MISC -> "<gray>";
        };
    }

    // ========== Check Editor GUI ==========

    public static class CheckEditorGui extends BaseGui {

        private final AnticheatCheckRule rule;
        private final CheckInfo checkInfo;
        private final AnticheatRulesGui parent;

        public CheckEditorGui(ModereX plugin, AnticheatCheckRule rule, CheckInfo checkInfo, AnticheatRulesGui parent) {
            super(plugin, "<gradient:#ff6b6b:#ee5a5a>Edit: " + checkInfo.getDisplayName() + "</gradient>", 5);
            this.rule = rule;
            this.checkInfo = checkInfo;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

            // Title / Info
            setItem(4, createItem(Material.IRON_SWORD, "<gold>" + checkInfo.getDisplayName(),
                    "<gray>" + checkInfo.getDescription(),
                    "",
                    "<dark_gray>Anticheat: " + rule.getAnticheat(),
                    "<dark_gray>Category: " + checkInfo.getCategory().getDisplayName()));

            // Enabled toggle
            setItem(19, createItem(
                    rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                    rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                    "<gray>Toggle check notifications",
                    "",
                    "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                saveAndRefresh();
            });

            // Min VL
            setItem(21, createItem(Material.EXPERIENCE_BOTTLE,
                    "<gold>Minimum VL: " + (rule.getMinVL() > 0 ? rule.getMinVL() : "default"),
                    "<gray>Only show alerts above this VL",
                    "",
                    "<yellow>Left: +5 | Right: -5",
                    "<yellow>Shift+click: Reset to default"), clickType -> {
                if (clickType.isShiftClick()) {
                    rule.setMinVL(0);
                } else if (clickType.isLeftClick()) {
                    rule.setMinVL(Math.min(100, rule.getMinVL() + 5));
                } else if (clickType.isRightClick()) {
                    rule.setMinVL(Math.max(0, rule.getMinVL() - 5));
                }
                saveAndRefresh();
            });

            // Threshold count
            setItem(23, createItem(Material.REDSTONE,
                    "<gold>Threshold: " + (rule.getThresholdCount() > 0 ? rule.getThresholdCount() + " alerts" : "default"),
                    "<gray>Alerts required before showing",
                    "",
                    "<yellow>Left: +1 | Right: -1",
                    "<yellow>Shift+click: Reset to default"), clickType -> {
                if (clickType.isShiftClick()) {
                    rule.setThresholdCount(0);
                } else if (clickType.isLeftClick()) {
                    rule.setThresholdCount(Math.min(20, rule.getThresholdCount() + 1));
                } else if (clickType.isRightClick()) {
                    rule.setThresholdCount(Math.max(0, rule.getThresholdCount() - 1));
                }
                saveAndRefresh();
            });

            // Threshold duration
            String durationStr = rule.getThresholdDuration() > 0 ?
                    DurationParser.format(rule.getThresholdDuration()) : "default";
            setItem(25, createItem(Material.CLOCK,
                    "<gold>Time Window: " + durationStr,
                    "<gray>Time window for threshold",
                    "",
                    "<yellow>Left: +30s | Right: -30s",
                    "<yellow>Shift+click: Reset to default"), clickType -> {
                if (clickType.isShiftClick()) {
                    rule.setThresholdDuration(0);
                } else if (clickType.isLeftClick()) {
                    rule.setThresholdDuration(Math.min(600000, rule.getThresholdDuration() + 30000));
                } else if (clickType.isRightClick()) {
                    rule.setThresholdDuration(Math.max(0, rule.getThresholdDuration() - 30000));
                }
                saveAndRefresh();
            });

            // ========== Auto-Punishment Section ==========
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            boolean hasPunishment = punishment != null && punishment.isEnabled();

            setItem(31, createItem(
                    hasPunishment ? Material.LIME_DYE : Material.RED_DYE,
                    hasPunishment ? "<green>Auto-Punish: ON" : "<red>Auto-Punish: OFF",
                    "<gray>Automatically punish when",
                    "<gray>this check is triggered",
                    "",
                    "<yellow>Click to toggle"), () -> {
                if (punishment == null) {
                    AutomodRule.AutoPunishment newPunishment = new AutomodRule.AutoPunishment();
                    newPunishment.setEnabled(true);
                    newPunishment.setType(PunishmentType.WARN);
                    newPunishment.setDuration(0);
                    newPunishment.setTriggerCount(5);
                    newPunishment.setTimeWindow(300000);
                    rule.setAutoPunishment(newPunishment);
                } else {
                    punishment.setEnabled(!punishment.isEnabled());
                }
                saveAndRefresh();
            });

            if (hasPunishment) {
                // Punishment type
                setItem(29, createItem(Material.IRON_SWORD,
                        "<gold>Type: " + punishment.getType().name(),
                        "<gray>What punishment to apply",
                        "",
                        "<yellow>Click to cycle"), () -> {
                    PunishmentType[] types = {PunishmentType.WARN, PunishmentType.MUTE,
                            PunishmentType.KICK, PunishmentType.BAN};
                    int current = 0;
                    for (int i = 0; i < types.length; i++) {
                        if (types[i] == punishment.getType()) {
                            current = i;
                            break;
                        }
                    }
                    punishment.setType(types[(current + 1) % types.length]);
                    saveAndRefresh();
                });

                // Duration (for mute/ban)
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.BAN) {
                    String pDuration = punishment.getDuration() == -1 ? "Permanent" :
                            punishment.getDuration() == 0 ? "Not set" : DurationParser.format(punishment.getDuration());
                    setItem(30, createItem(Material.HOPPER,
                            "<gold>Duration: " + pDuration,
                            "<gray>Punishment duration",
                            "",
                            "<yellow>Left: +1h | Right: -1h",
                            "<yellow>Shift+click: Toggle permanent"), clickType -> {
                        if (clickType.isShiftClick()) {
                            punishment.setDuration(punishment.getDuration() == -1 ? 3600000 : -1);
                        } else if (clickType.isLeftClick()) {
                            if (punishment.getDuration() != -1) {
                                punishment.setDuration(punishment.getDuration() + 3600000);
                            }
                        } else if (clickType.isRightClick()) {
                            if (punishment.getDuration() > 3600000) {
                                punishment.setDuration(punishment.getDuration() - 3600000);
                            } else if (punishment.getDuration() != -1) {
                                punishment.setDuration(3600000);
                            }
                        }
                        saveAndRefresh();
                    });
                }

                // Trigger count
                setItem(32, createItem(Material.TNT,
                        "<gold>After: " + punishment.getTriggerCount() + " violations",
                        "<gray>Violations before punishing",
                        "",
                        "<yellow>Left: +1 | Right: -1"), clickType -> {
                    if (clickType.isLeftClick()) {
                        punishment.setTriggerCount(Math.min(50, punishment.getTriggerCount() + 1));
                    } else if (clickType.isRightClick()) {
                        punishment.setTriggerCount(Math.max(1, punishment.getTriggerCount() - 1));
                    }
                    saveAndRefresh();
                });

                // Time window for violations
                String vWindow = DurationParser.format(punishment.getTimeWindow());
                setItem(33, createItem(Material.REPEATER,
                        "<gold>Window: " + vWindow,
                        "<gray>Reset violations after this",
                        "",
                        "<yellow>Left: +1m | Right: -1m"), clickType -> {
                    if (clickType.isLeftClick()) {
                        punishment.setTimeWindow(Math.min(3600000, punishment.getTimeWindow() + 60000));
                    } else if (clickType.isRightClick()) {
                        punishment.setTimeWindow(Math.max(60000, punishment.getTimeWindow() - 60000));
                    }
                    saveAndRefresh();
                });
            }

            // Navigation
            setItem(36, createItem(Material.ARROW, "<yellow>Back",
                    "<gray>Return to check list"), () -> {
                plugin.getGuiManager().open(viewer, parent);
            });

            setItem(44, createItem(Material.LIME_CONCRETE, "<green>Save & Close",
                    "<gray>Save changes and return"), () -> {
                plugin.getAnticheatManager().getAlertManager().saveRule(rule);
                viewer.sendMessage(TextUtil.parse("<green>Check rule saved!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                plugin.getGuiManager().open(viewer, parent);
            });
        }

        private void saveAndRefresh() {
            plugin.getAnticheatManager().getAlertManager().saveRule(rule);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        }
    }
}
