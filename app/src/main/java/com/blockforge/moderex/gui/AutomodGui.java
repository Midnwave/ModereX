package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutomodGui extends BaseGui {

    public AutomodGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Automod Settings</gradient>", 6);
    }

    @Override
    protected void populate() {
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        AutomodManager automod = plugin.getAutomodManager();

        // Header
        setItem(4, createItem(Material.COMPARATOR, "<gold><bold>Automod Configuration</bold>",
                "<gray>Configure chat filtering and moderation",
                "",
                "<yellow>Manage spam, caps, and word filters"));

        // Spam Prevention Toggle
        boolean spamEnabled = automod.isSpamPreventionEnabled();
        setItem(19, createToggle("Spam Prevention",
                spamEnabled,
                "Block rapid message sending and duplicate messages",
                Material.REPEATER), () -> {
            automod.setSpamPreventionEnabled(!spamEnabled);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        });

        // Caps Filter Toggle
        boolean capsEnabled = automod.isCapsFilterEnabled();
        setItem(20, createToggle("Caps Filter",
                capsEnabled,
                "Convert excessive caps to lowercase",
                Material.NAME_TAG), () -> {
            automod.setCapsFilterEnabled(!capsEnabled);
            viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            refresh();
        });

        // Word Filter Rules Section
        setItem(22, createItem(Material.BOOK, "<aqua><bold>Word Filter Rules</bold>",
                "<gray>Custom word/phrase filters",
                "",
                "<white>" + countWordFilters() + " <gray>active rules"));

        // Display word filter rules
        int slot = 28;
        for (AutomodRule rule : automod.getRules()) {
            if (rule.getType() != AutomodRule.RuleType.WORD_FILTER) continue;
            if (slot > 34) break;

            setItem(slot, createRuleItem(rule), () -> openRuleEditor(rule));
            slot++;
        }

        // Add new rule button
        if (slot <= 34) {
            setItem(slot, createItem(Material.LIME_DYE, "<green>+ Create New Rule",
                    "<gray>Add a new word filter rule",
                    "",
                    "<yellow>Click to create"), this::promptCreateRule);
        }

        // Anticheat Rules Section
        setItem(24, createItem(Material.IRON_SWORD, "<red><bold>Anticheat Rules</bold>",
                "<gray>Auto-punishment for anticheat alerts",
                "",
                "<gray>Configure in anticheat settings"));

        // Back button
        setItem(49, createBackButton(), () -> openGui(new MainMenuGui(plugin)));

        // Close button
        setItem(53, createCloseButton(), this::close);
    }

    private int countWordFilters() {
        int count = 0;
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getType() == AutomodRule.RuleType.WORD_FILTER && rule.isEnabled()) {
                count++;
            }
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
                lore.add("<dark_gray>Preview: " + words.get(0) + (words.size() > 1 ? "..." : ""));
            }
        }

        lore.add("");
        if (rule.isBuiltIn()) {
            lore.add("<red>Built-in rule (cannot delete)");
        } else {
            lore.add("<yellow>Click to edit");
            lore.add("<red>Shift-click to delete");
        }

        return createItem(
                rule.isEnabled() ? Material.WRITABLE_BOOK : Material.BOOK,
                (rule.isEnabled() ? "<green>" : "<gray>") + rule.getName(),
                lore
        );
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

    public static class RuleEditorGui extends BaseGui {

        private final AutomodRule rule;
        private final AutomodGui parent;

        public RuleEditorGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>Edit Rule: " + rule.getName() + "</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            // Enabled toggle
            setItem(10, createItem(
                    rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                    rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                    "<gray>Toggle rule on/off",
                    "",
                    "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Blacklisted words
            setItem(12, createItem(Material.PAPER, "<gold>Blacklisted Words",
                    "<gray>Words/phrases to filter",
                    "",
                    "<white>" + rule.getBlacklistedWords().size() + " <gray>words",
                    "",
                    "<yellow>Click to edit"), this::promptEditWords);

            // Exact match toggle
            setItem(14, createItem(
                    rule.isExactMatch() ? Material.TARGET : Material.GLASS,
                    rule.isExactMatch() ? "<green>Exact Match: ON" : "<yellow>Exact Match: OFF",
                    "<gray>Match whole words only vs anywhere in message",
                    "",
                    "<yellow>Click to toggle"), () -> {
                rule.setExactMatch(!rule.isExactMatch());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Exclusions
            setItem(16, createItem(Material.EMERALD, "<aqua>Exclusions",
                    "<gray>Words that bypass this filter",
                    "",
                    "<white>" + rule.getExclusionWords().size() + " <gray>exclusions",
                    "",
                    "<yellow>Click to edit"), this::promptEditExclusions);

            // Delete button (if not built-in)
            if (!rule.isBuiltIn()) {
                setItem(31, createItem(Material.BARRIER, "<red>Delete Rule",
                        "<gray>Permanently delete this rule",
                        "",
                        "<red>This cannot be undone!",
                        "<yellow>Shift-click to confirm"), clickType -> {
                    if (clickType.isShiftClick()) {
                        plugin.getAutomodManager().deleteRule(rule.getId());
                        viewer.sendMessage(TextUtil.parse("<red>Rule deleted."));
                        openGui(parent);
                    }
                });
            }

            // Back button
            setItem(27, createBackButton(), () -> openGui(parent));
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
                                context.getForWhom().sendRawMessage(toLegacy("<green>Blacklist updated with " + words.size() + " words."));
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
                                context.getForWhom().sendRawMessage(toLegacy("<green>Exclusions updated with " + words.size() + " words."));
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
    }

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }
}
