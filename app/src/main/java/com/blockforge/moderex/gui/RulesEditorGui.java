package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.rules.Rule;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * In-game rules editor GUI.
 * Lists all server rules with options to add, edit, delete, and reorder.
 */
public class RulesEditorGui extends BaseGui {

    private int page = 0;
    private static final int RULES_PER_PAGE = 14; // 2 rows of 7

    public RulesEditorGui(ModereX plugin) {
        super(plugin, "<gradient:#3b82f6:#8b5cf6>Rules Editor</gradient>", 6);
    }

    @Override
    protected void populate() {
        fillBorder(Material.BLUE_STAINED_GLASS_PANE);

        List<Rule> rules = new ArrayList<>(plugin.getRulesManager().getRules());
        int totalPages = Math.max(1, (int) Math.ceil((double) rules.size() / RULES_PER_PAGE));
        if (page >= totalPages) page = totalPages - 1;

        int start = page * RULES_PER_PAGE;
        int end = Math.min(start + RULES_PER_PAGE, rules.size());

        // Place rules in slots 10-16, 19-25 (rows 2-3)
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < slots.length && (start + i) < end; i++) {
            Rule rule = rules.get(start + i);

            List<String> lore = new ArrayList<>();
            lore.add("<gray>" + (rule.getDescription() != null ? rule.getDescription() : "No description"));
            lore.add("");
            lore.add("<gray>Category: <yellow>" + (rule.getCategory() != null ? rule.getCategory() : "General"));
            lore.add("<gray>Order: <yellow>#" + rule.getOrder());
            lore.add(rule.isEnabled() ? "<green>Enabled" : "<red>Disabled");
            lore.add("");
            lore.add("<yellow>Left-click to edit");
            lore.add("<red>Right-click to toggle");
            lore.add("<gray>Shift-click to delete");

            final Rule finalRule = rule;
            setItem(slots[i], createItem(
                    rule.isEnabled() ? Material.WRITABLE_BOOK : Material.BOOK,
                    (rule.isEnabled() ? "<green>" : "<red>") + "#" + rule.getOrder() + " " + rule.getTitle(),
                    lore
            ), clickType -> {
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    // Delete
                    plugin.getRulesManager().deleteRule(finalRule.getId());
                    Msg.send(viewer, TextUtil.parse("<red>Deleted rule: " + finalRule.getTitle()));
                    refresh();
                } else if (clickType == ClickType.RIGHT) {
                    // Toggle
                    finalRule.setEnabled(!finalRule.isEnabled());
                    plugin.getRulesManager().saveRule(finalRule);
                    refresh();
                } else {
                    // Open edit GUI
                    openGui(new RuleEditGui(plugin, finalRule, RulesEditorGui.this));
                }
            });
        }

        // Row 4-5: Info and actions
        // Add new rule button
        setItem(28, createItem(
                Material.LIME_CONCRETE,
                "<green>+ Add New Rule",
                "<gray>Create a new server rule.",
                "",
                "<yellow>Click to create"
        ), () -> {
            Rule newRule = new Rule();
            newRule.setTitle("New Rule");
            newRule.setDescription("Description here");
            newRule.setCategory("General");
            newRule.setOrder(rules.size() + 1);
            newRule.setEnabled(true);
            plugin.getRulesManager().saveRule(newRule);
            Msg.send(viewer, TextUtil.parse("<green>Created new rule. Click to edit it."));
            refresh();
        });

        // Refresh rules acceptance (force re-present to all online players)
        setItem(30, createItem(
                Material.GOLDEN_APPLE,
                "<gold>Re-present Rules",
                "<gray>Force all online players to",
                "<gray>re-accept the updated rules.",
                "",
                "<yellow>Click to re-present"
        ), () -> {
            if (plugin.getCodeOfConductManager() != null) {
                for (var p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getCodeOfConductManager().presentRules(p);
                }
                Msg.send(viewer, TextUtil.parse("<green>Rules re-presented to all online players."));
            } else {
                Msg.send(viewer, TextUtil.parse("<red>Code of conduct manager is not available."));
            }
        });

        // Rule stats
        long enabledCount = rules.stream().filter(Rule::isEnabled).count();
        long disabledCount = rules.stream().filter(r -> !r.isEnabled()).count();
        setItem(32, createItem(
                Material.PAPER,
                "<aqua>Rules Statistics",
                "<gray>Total rules: <white>" + rules.size(),
                "<gray>Enabled: <green>" + enabledCount,
                "<gray>Disabled: <red>" + disabledCount
        ));

        // Generate AI descriptions
        setItem(34, createItem(
                Material.BRAIN_CORAL,
                "<light_purple>AI Descriptions",
                "<gray>Use AI to generate detailed",
                "<gray>descriptions for all rules.",
                "",
                "<yellow>Click to generate"
        ), () -> {
            var aiClient = plugin.getAIClient();
            if (aiClient == null || !aiClient.isAvailable()) {
                Msg.send(viewer, TextUtil.parse("<red>AI is not available."));
                return;
            }
            Msg.send(viewer, TextUtil.parse("<yellow>Generating AI descriptions for all rules..."));
            for (Rule rule : rules) {
                aiClient.generateRuleDescription(rule.getTitle(), rule.getDescription())
                        .thenAccept(desc -> {
                            if (desc != null) {
                                rule.setAiDescription(desc);
                                plugin.getRulesManager().saveRule(rule);
                            }
                        });
            }
        });

        // Pagination
        if (page > 0) {
            setItem(48, createPreviousButton(page + 1), () -> {
                page--;
                refresh();
            });
        }

        setItem(49, createItem(Material.PAPER, "<white>Page " + (page + 1) + "/" + totalPages));

        if (page < totalPages - 1) {
            setItem(50, createNextButton(page + 1, totalPages), () -> {
                page++;
                refresh();
            });
        }

        // Back button
        setItem(45, createBackButton(), () -> {
            openGui(new MainMenuGui(plugin));
        });

        // Close
        setItem(53, createCloseButton(), this::close);

        fillEmpty(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Sub-GUI for editing a single rule.
     */
    static class RuleEditGui extends BaseGui {

        private final Rule rule;
        private final RulesEditorGui parent;

        public RuleEditGui(ModereX plugin, Rule rule, RulesEditorGui parent) {
            super(plugin, "<gradient:#3b82f6:#8b5cf6>Edit Rule: " + rule.getTitle() + "</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillBorder(Material.LIGHT_BLUE_STAINED_GLASS_PANE);

            // Title edit
            setItem(10, createItem(
                    Material.NAME_TAG,
                    "<gold>Title: <white>" + rule.getTitle(),
                    "<gray>Click to change the rule title.",
                    "",
                    "<yellow>Click to edit"
            ), () -> {
                viewer.closeInventory();
                promptInput("<yellow>Type the new rule title (or 'cancel' to go back):", input -> {
                    if (!"cancel".equalsIgnoreCase(input.trim())) {
                        rule.setTitle(input.trim());
                        plugin.getRulesManager().saveRule(rule);
                        Msg.send(viewer, TextUtil.parse("<green>Title updated to: " + input.trim()));
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getGuiManager().open(viewer, new RuleEditGui(plugin, rule, parent));
                    });
                });
            });

            // Description edit
            setItem(12, createItem(
                    Material.WRITABLE_BOOK,
                    "<aqua>Description",
                    "<gray>" + (rule.getDescription() != null ? rule.getDescription() : "None"),
                    "",
                    "<yellow>Click to edit"
            ), () -> {
                viewer.closeInventory();
                promptInput("<yellow>Type the new description (or 'cancel' to go back):", input -> {
                    if (!"cancel".equalsIgnoreCase(input.trim())) {
                        rule.setDescription(input.trim());
                        plugin.getRulesManager().saveRule(rule);
                        Msg.send(viewer, TextUtil.parse("<green>Description updated."));
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        plugin.getGuiManager().open(viewer, new RuleEditGui(plugin, rule, parent));
                    });
                });
            });

            // Category
            String[] categories = {"General", "Chat", "Gameplay", "PvP", "Building", "Community"};
            int catIdx = 0;
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(rule.getCategory())) {
                    catIdx = i;
                    break;
                }
            }
            final int currentCatIdx = catIdx;
            setItem(14, createItem(
                    Material.CHEST,
                    "<yellow>Category: <white>" + (rule.getCategory() != null ? rule.getCategory() : "General"),
                    "<gray>Click to cycle categories."
            ), () -> {
                int next = (currentCatIdx + 1) % categories.length;
                rule.setCategory(categories[next]);
                plugin.getRulesManager().saveRule(rule);
                refresh();
            });

            // Toggle enabled
            setItem(16, createItem(
                    rule.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE,
                    rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                    "<gray>Click to toggle."
            ), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getRulesManager().saveRule(rule);
                refresh();
            });

            // AI description
            setItem(22, createItem(
                    Material.BRAIN_CORAL,
                    "<light_purple>AI Description",
                    rule.getAiDescription() != null
                            ? "<gray>" + rule.getAiDescription()
                            : "<gray>No AI description yet.",
                    "",
                    "<yellow>Click to generate"
            ), () -> {
                var aiClient = plugin.getAIClient();
                if (aiClient == null || !aiClient.isAvailable()) {
                    Msg.send(viewer, TextUtil.parse("<red>AI is not available."));
                    return;
                }
                Msg.send(viewer, TextUtil.parse("<yellow>Generating AI description..."));
                aiClient.generateRuleDescription(rule.getTitle(), rule.getDescription())
                        .thenAccept(desc -> {
                            if (desc != null) {
                                rule.setAiDescription(desc);
                                plugin.getRulesManager().saveRule(rule);
                                plugin.getServer().getScheduler().runTask(plugin, () -> refresh());
                                Msg.send(viewer, TextUtil.parse("<green>AI description generated."));
                            } else {
                                Msg.send(viewer, TextUtil.parse("<red>AI failed to generate description."));
                            }
                        });
            });

            // Back
            setItem(31, createItem(Material.ARROW, "<yellow>Go Back", "<gray>Return to rules list"), () -> {
                openGui(parent);
            });

            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);
        }
    }
}
