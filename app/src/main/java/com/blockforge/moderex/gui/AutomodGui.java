package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutomodGui extends BaseGui {

    private Tab currentTab = Tab.FILTERS;
    private int rulePage = 0;
    private static final int RULES_PER_PAGE = 14;

    public AutomodGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Automod Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

        AutomodManager automod = plugin.getAutomodManager();

        // Tab buttons (row 1)
        setItem(10, createTabButton(Tab.FILTERS, Material.COMPARATOR), () -> {
            currentTab = Tab.FILTERS;
            rulePage = 0;
            refresh();
        });

        setItem(11, createTabButton(Tab.RULES, Material.BOOK), () -> {
            currentTab = Tab.RULES;
            rulePage = 0;
            refresh();
        });

        setItem(12, createTabButton(Tab.PUNISHMENTS, Material.BARRIER), () -> {
            currentTab = Tab.PUNISHMENTS;
            refresh();
        });

        setItem(14, createItem(Material.PAPER, "<aqua>Statistics",
                "<gray>Active rules: <white>" + countActiveRules(),
                "<gray>Total rules: <white>" + automod.getRules().size(),
                "<gray>Spam filter: " + (automod.isSpamPreventionEnabled() ? "<green>ON" : "<red>OFF"),
                "<gray>Caps filter: " + (automod.isCapsFilterEnabled() ? "<green>ON" : "<red>OFF")));

        // Render current tab content
        switch (currentTab) {
            case FILTERS -> renderFiltersTab();
            case RULES -> renderRulesTab();
            case PUNISHMENTS -> renderPunishmentsTab();
        }

        // Bottom navigation
        setItem(45, createItem(Material.ARROW, "<red>Back", "<gray>Return to main menu"),
                () -> openGui(new MainMenuGui(plugin)));

        setItem(49, createItem(Material.LIME_DYE, "<green>+ New Rule",
                "<gray>Create a new automod rule",
                "",
                "<yellow>Click to create"), this::promptCreateRule);

        setItem(53, createCloseButton(), this::close);
    }

    private ItemStack createTabButton(Tab tab, Material icon) {
        boolean selected = (currentTab == tab);

        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + tab.description);
        lore.add("");
        lore.add(selected ? "<green>▶ Currently viewing" : "<yellow>Click to view");

        return createItem(
                selected ? Material.LIME_STAINED_GLASS_PANE : icon,
                (selected ? "<green>" : "<white>") + tab.displayName,
                lore
        );
    }

    // ========== Filters Tab ==========

    private void renderFiltersTab() {
        AutomodManager automod = plugin.getAutomodManager();

        // Centered content - row 2 (slots 20-24)
        boolean spamEnabled = automod.isSpamPreventionEnabled();
        setItem(20, createToggle("Spam Prevention", spamEnabled,
                "Block rapid messages and duplicates", Material.REPEATER), () -> {
            automod.setSpamPreventionEnabled(!spamEnabled);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        });

        boolean capsEnabled = automod.isCapsFilterEnabled();
        setItem(22, createToggle("Caps Filter", capsEnabled,
                "Convert excessive caps to lowercase", Material.NAME_TAG), () -> {
            automod.setCapsFilterEnabled(!capsEnabled);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        });

        setItem(24, createItem(Material.CLOCK, "<gold>Spam Settings",
                "<gray>Fine-tune spam detection",
                "",
                "<white>Message delay: <yellow>2s",
                "<white>Similar threshold: <yellow>80%",
                "",
                "<dark_gray>Edit via config.yml"));

        // Row 3 - Additional info
        setItem(31, createItem(Material.BOOK, "<aqua>How Filters Work",
                "<gray>Spam: Blocks messages sent",
                "<gray>too quickly or duplicates.",
                "",
                "<gray>Caps: Automatically converts",
                "<gray>messages with >50% caps."));
    }

    // ========== Rules Tab ==========

    private void renderRulesTab() {
        List<AutomodRule> rules = new ArrayList<>(plugin.getAutomodManager().getRules());
        int totalPages = (int) Math.ceil(rules.size() / (double) RULES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // Rules display area - centered (slots 19-25, 28-34)
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int startIndex = rulePage * RULES_PER_PAGE;

        for (int i = 0; i < slots.length && (startIndex + i) < rules.size(); i++) {
            AutomodRule rule = rules.get(startIndex + i);
            setItem(slots[i], createRuleItem(rule), clickType -> {
                if (clickType.isShiftClick() && !rule.isBuiltIn()) {
                    plugin.getAutomodManager().deleteRule(rule.getId());
                    viewer.sendMessage(TextUtil.parse("<red>Rule deleted."));
                    viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                    refresh();
                } else {
                    openRuleEditor(rule);
                }
            });
        }

        if (rules.isEmpty()) {
            setItem(22, createItem(Material.GRAY_DYE, "<gray>No Rules",
                    "<gray>Create your first rule",
                    "<gray>with the button below"));
        }

        // Pagination - row 4
        if (rulePage > 0) {
            setItem(37, createPreviousButton(rulePage + 1), () -> {
                rulePage--;
                refresh();
            });
        }

        setItem(40, createItem(Material.PAPER, "<white>Page " + (rulePage + 1) + "/" + totalPages,
                "<gray>" + rules.size() + " total rules"));

        if (rulePage < totalPages - 1) {
            setItem(43, createNextButton(rulePage + 1, totalPages), () -> {
                rulePage++;
                refresh();
            });
        }
    }

    // ========== Punishments Tab ==========

    private void renderPunishmentsTab() {
        // Display rules that have auto-punishments configured
        List<AutomodRule> rulesWithPunishments = new ArrayList<>();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
                rulesWithPunishments.add(rule);
            }
        }

        setItem(20, createItem(Material.BOOK, "<gold>Auto-Punishments",
                "<gray>Configure automatic actions",
                "<gray>when rules are triggered.",
                "",
                "<white>" + rulesWithPunishments.size() + " <gray>rules with punishments"));

        // Show rules with punishments
        int[] slots = {28, 29, 30, 31, 32, 33, 34};
        int slotIndex = 0;

        for (AutomodRule rule : rulesWithPunishments) {
            if (slotIndex >= slots.length) break;

            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            String duration = punishment.getDuration() == -1 ? "permanent" :
                    DurationParser.format(punishment.getDuration());

            setItem(slots[slotIndex], createItem(Material.BARRIER,
                    "<red>" + rule.getName(),
                    "<gray>Action: <white>" + punishment.getType().name(),
                    "<gray>Duration: <white>" + duration,
                    "<gray>After: <white>" + punishment.getTriggerCount() + " violations",
                    "",
                    "<yellow>Click to edit"), () -> openRuleEditor(rule));
            slotIndex++;
        }

        if (rulesWithPunishments.isEmpty()) {
            setItem(31, createItem(Material.GRAY_DYE, "<gray>No Auto-Punishments",
                    "<gray>Configure punishments in",
                    "<gray>rule settings"));
        }

        // Info
        setItem(22, createItem(Material.IRON_SWORD, "<aqua>Tip",
                "<gray>Click on any rule in the",
                "<gray>Rules tab to configure",
                "<gray>auto-punishments for it"));
    }

    // ========== Helper Methods ==========

    private int countActiveRules() {
        int count = 0;
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.isEnabled()) count++;
        }
        return count;
    }

    private ItemStack createToggle(String name, boolean enabled, String description, Material icon) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + description);
        lore.add("");
        lore.add(enabled ? "<green>✓ Enabled" : "<red>✗ Disabled");
        lore.add("");
        lore.add("<yellow>Click to toggle");

        return createItem(
                enabled ? Material.LIME_DYE : Material.RED_DYE,
                (enabled ? "<green>" : "<red>") + name,
                lore
        );
    }

    private ItemStack createRuleItem(AutomodRule rule) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>Type: <white>" + rule.getType().name());
        lore.add("<gray>Status: " + (rule.isEnabled() ? "<green>Enabled" : "<red>Disabled"));

        if (rule.getType() == AutomodRule.RuleType.WORD_FILTER) {
            List<String> words = rule.getBlacklistedWords();
            lore.add("<gray>Words: <white>" + words.size());
            if (!words.isEmpty()) {
                String preview = words.get(0);
                if (preview.length() > 10) preview = preview.substring(0, 10) + "...";
                lore.add("<dark_gray>Preview: " + preview + (words.size() > 1 ? " +" + (words.size() - 1) : ""));
            }
        }

        if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
            lore.add("<gold>⚠ Auto-punishment: " + rule.getAutoPunishment().getType().name());
        }

        lore.add("");
        if (rule.isBuiltIn()) {
            lore.add("<red>Built-in rule");
        } else {
            lore.add("<yellow>Click to edit");
            lore.add("<red>Shift+click to delete");
        }

        Material icon = rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE;
        if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
            icon = Material.ORANGE_DYE;
        }

        return createItem(icon, (rule.isEnabled() ? "<green>" : "<gray>") + rule.getName(), lore);
    }

    private void openRuleEditor(AutomodRule rule) {
        plugin.getGuiManager().open(viewer, new RuleEditorGui(plugin, rule, this));
    }

    private void promptCreateRule() {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter a name for the new rule:"));
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
                            AutomodRule newRule = plugin.getAutomodManager().createRule(input);
                            context.getForWhom().sendRawMessage(toLegacy("<green>Rule '" + input + "' created!"));

                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                plugin.getGuiManager().open(viewer, new RuleEditorGui(plugin, newRule, AutomodGui.this));
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            plugin.getGuiManager().open(viewer, AutomodGui.this);
                        });
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    private enum Tab {
        FILTERS("Filters", "Global chat filters"),
        RULES("Rules", "Word filter rules"),
        PUNISHMENTS("Punishments", "Auto-punishment config");

        final String displayName;
        final String description;

        Tab(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }

    // ========== Rule Editor GUI ==========

    public static class RuleEditorGui extends BaseGui {

        private final AutomodRule rule;
        private final AutomodGui parent;
        private EditorTab editorTab = EditorTab.GENERAL;

        public RuleEditorGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>Edit: " + rule.getName() + "</gradient>", 5);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.BLACK_STAINED_GLASS_PANE);

            // Tab buttons
            setItem(10, createEditorTabButton(EditorTab.GENERAL, Material.COMPARATOR), () -> {
                editorTab = EditorTab.GENERAL;
                refresh();
            });

            setItem(11, createEditorTabButton(EditorTab.WORDS, Material.PAPER), () -> {
                editorTab = EditorTab.WORDS;
                refresh();
            });

            setItem(12, createEditorTabButton(EditorTab.PUNISHMENT, Material.BARRIER), () -> {
                editorTab = EditorTab.PUNISHMENT;
                refresh();
            });

            switch (editorTab) {
                case GENERAL -> renderGeneralTab();
                case WORDS -> renderWordsTab();
                case PUNISHMENT -> renderPunishmentTab();
            }

            // Bottom navigation
            setItem(36, createBackButton(), () -> openGui(parent));

            if (!rule.isBuiltIn()) {
                setItem(40, createItem(Material.BARRIER, "<red>Delete Rule",
                        "<gray>Permanently delete this rule",
                        "",
                        "<red>Shift+click to confirm"), clickType -> {
                    if (clickType.isShiftClick()) {
                        plugin.getAutomodManager().deleteRule(rule.getId());
                        viewer.sendMessage(TextUtil.parse("<red>Rule deleted."));
                        openGui(parent);
                    }
                });
            }

            setItem(44, createItem(Material.LIME_CONCRETE, "<green>Save & Close",
                    "<gray>Save changes and return"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                viewer.sendMessage(TextUtil.parse("<green>Rule saved!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }

        private ItemStack createEditorTabButton(EditorTab tab, Material icon) {
            boolean selected = (editorTab == tab);
            return createItem(
                    selected ? Material.LIME_STAINED_GLASS_PANE : icon,
                    (selected ? "<green>" : "<white>") + tab.displayName,
                    "<gray>" + tab.description,
                    "",
                    selected ? "<green>▶ Viewing" : "<yellow>Click to view"
            );
        }

        private void renderGeneralTab() {
            // Enabled toggle
            setItem(20, createItem(
                    rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                    rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                    "<gray>Toggle rule on/off",
                    "",
                    "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Exact match toggle
            setItem(22, createItem(
                    rule.isExactMatch() ? Material.TARGET : Material.GLASS,
                    rule.isExactMatch() ? "<green>Exact Match: ON" : "<yellow>Exact Match: OFF",
                    "<gray>Match whole words only",
                    "<gray>vs anywhere in message",
                    "",
                    "<yellow>Click to toggle"), () -> {
                rule.setExactMatch(!rule.isExactMatch());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Rule type info
            setItem(24, createItem(Material.BOOK, "<aqua>Rule Type",
                    "<white>" + rule.getType().name(),
                    "",
                    "<gray>Type cannot be changed"));
        }

        private void renderWordsTab() {
            // Blacklisted words
            setItem(20, createItem(Material.PAPER, "<gold>Blacklisted Words",
                    "<gray>Words to filter",
                    "",
                    "<white>" + rule.getBlacklistedWords().size() + " words",
                    "",
                    "<yellow>Click to edit"), this::promptEditWords);

            // Preview some words
            List<String> words = rule.getBlacklistedWords();
            if (!words.isEmpty()) {
                StringBuilder preview = new StringBuilder();
                for (int i = 0; i < Math.min(5, words.size()); i++) {
                    if (i > 0) preview.append(", ");
                    preview.append(words.get(i));
                }
                if (words.size() > 5) preview.append("...");

                setItem(29, createItem(Material.WRITABLE_BOOK, "<gray>Preview",
                        "<white>" + preview));
            }

            // Exclusions
            setItem(24, createItem(Material.EMERALD, "<aqua>Exclusions",
                    "<gray>Words that bypass filter",
                    "",
                    "<white>" + rule.getExclusionWords().size() + " exclusions",
                    "",
                    "<yellow>Click to edit"), this::promptEditExclusions);
        }

        private void renderPunishmentTab() {
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            boolean hasPunishment = punishment != null && punishment.isEnabled();

            // Enable toggle
            setItem(20, createItem(
                    hasPunishment ? Material.LIME_DYE : Material.RED_DYE,
                    hasPunishment ? "<green>Auto-Punish: ON" : "<red>Auto-Punish: OFF",
                    "<gray>Automatically punish",
                    "<gray>when rule is triggered",
                    "",
                    "<yellow>Click to toggle"), () -> {
                if (punishment == null) {
                    AutomodRule.AutoPunishment newPunishment = new AutomodRule.AutoPunishment();
                    newPunishment.setEnabled(true);
                    newPunishment.setType(PunishmentType.WARN);
                    newPunishment.setDuration(0);
                    newPunishment.setTriggerCount(3);
                    newPunishment.setTimeWindow(300000); // 5 minutes
                    rule.setAutoPunishment(newPunishment);
                } else {
                    punishment.setEnabled(!punishment.isEnabled());
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            if (hasPunishment) {
                // Punishment type
                setItem(21, createItem(Material.IRON_SWORD,
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
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });

                // Duration (for mute/ban)
                if (punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.BAN) {
                    String duration = punishment.getDuration() == -1 ? "Permanent" :
                            punishment.getDuration() == 0 ? "Not set" : DurationParser.format(punishment.getDuration());
                    setItem(22, createItem(Material.CLOCK,
                            "<gold>Duration: " + duration,
                            "<gray>How long the punishment lasts",
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
                        plugin.getAutomodManager().saveRule(rule);
                        refresh();
                    });
                }

                // Trigger count
                setItem(23, createItem(Material.REDSTONE,
                        "<gold>Trigger After: " + punishment.getTriggerCount() + " violations",
                        "<gray>How many violations before punishing",
                        "",
                        "<yellow>Left: +1 | Right: -1"), clickType -> {
                    if (clickType.isLeftClick()) {
                        punishment.setTriggerCount(Math.min(20, punishment.getTriggerCount() + 1));
                    } else if (clickType.isRightClick()) {
                        punishment.setTriggerCount(Math.max(1, punishment.getTriggerCount() - 1));
                    }
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });

                // Time window
                String window = DurationParser.format(punishment.getTimeWindow());
                setItem(24, createItem(Material.HOPPER,
                        "<gold>Time Window: " + window,
                        "<gray>Reset violations after this time",
                        "",
                        "<yellow>Left: +1m | Right: -1m"), clickType -> {
                    if (clickType.isLeftClick()) {
                        punishment.setTimeWindow(Math.min(3600000, punishment.getTimeWindow() + 60000));
                    } else if (clickType.isRightClick()) {
                        punishment.setTimeWindow(Math.max(60000, punishment.getTimeWindow() - 60000));
                    }
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });
            }
        }

        private void promptEditWords() {
            close();
            viewer.sendMessage(TextUtil.parse("<aqua>Enter blacklisted words (comma-separated):"));
            viewer.sendMessage(TextUtil.parse("<gray>Current: " + String.join(", ", rule.getBlacklistedWords())));
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
                            if (!input.equalsIgnoreCase("cancel")) {
                                List<String> words = new ArrayList<>();
                                for (String word : input.split(",")) {
                                    word = word.trim();
                                    if (!word.isEmpty()) {
                                        words.add(word);
                                    }
                                }
                                rule.setBlacklistedWords(words);
                                plugin.getAutomodManager().saveRule(rule);
                                context.getForWhom().sendRawMessage(toLegacy("<green>Updated with " + words.size() + " words."));
                            }

                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                plugin.getGuiManager().open(viewer, RuleEditorGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }
                    })
                    .withLocalEcho(false)
                    .withTimeout(120)
                    .buildConversation(viewer)
                    .begin();
        }

        private void promptEditExclusions() {
            close();
            viewer.sendMessage(TextUtil.parse("<aqua>Enter exclusion words (comma-separated):"));
            viewer.sendMessage(TextUtil.parse("<gray>Current: " + String.join(", ", rule.getExclusionWords())));
            viewer.sendMessage(TextUtil.parse("<gray>Type 'cancel' to cancel, 'clear' to remove all"));

            new ConversationFactory(plugin)
                    .withModality(true)
                    .withFirstPrompt(new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext context) {
                            return "";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext context, String input) {
                            if (input.equalsIgnoreCase("clear")) {
                                rule.setExclusionWords(new ArrayList<>());
                                plugin.getAutomodManager().saveRule(rule);
                                context.getForWhom().sendRawMessage(toLegacy("<yellow>Exclusions cleared."));
                            } else if (!input.equalsIgnoreCase("cancel")) {
                                List<String> words = new ArrayList<>();
                                for (String word : input.split(",")) {
                                    word = word.trim();
                                    if (!word.isEmpty()) {
                                        words.add(word);
                                    }
                                }
                                rule.setExclusionWords(words);
                                plugin.getAutomodManager().saveRule(rule);
                                context.getForWhom().sendRawMessage(toLegacy("<green>Updated with " + words.size() + " exclusions."));
                            }

                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                plugin.getGuiManager().open(viewer, RuleEditorGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }
                    })
                    .withLocalEcho(false)
                    .withTimeout(120)
                    .buildConversation(viewer)
                    .begin();
        }

        private enum EditorTab {
            GENERAL("General", "Basic rule settings"),
            WORDS("Words", "Blacklist and exclusions"),
            PUNISHMENT("Punishment", "Auto-punishment settings");

            final String displayName;
            final String description;

            EditorTab(String displayName, String description) {
                this.displayName = displayName;
                this.description = description;
            }
        }
    }

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }
}
