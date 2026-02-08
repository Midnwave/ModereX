package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.automod.AutomodRule;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.staff.StaffSettings.AlertLevel;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Redesigned Automod GUI - Clean, user-friendly interface
 *
 * Layout:
 * - Top row: Navigation and info
 * - Built-in filters section (toggleable)
 * - Custom word filter rules
 * - Anticheat rules (at the end)
 * - Bottom: Create new rule, navigation
 */
public class AutomodGui extends BaseGui {

    private int currentPage = 0;
    private static final int RULES_PER_PAGE = 21; // 3 rows of 7

    public AutomodGui(ModereX plugin) {
        super(plugin, "<gradient:#a855f7:#ec4899>Automod Manager</gradient>", 6);
    }

    @Override
    protected void populate() {
        fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

        AutomodManager automod = plugin.getAutomodManager();

        // === TOP ROW: Header and Stats ===
        // Decorative corners
        setItem(0, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));
        setItem(8, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        // Title/Info
        setItem(4, createItem(Material.COMMAND_BLOCK, "<gradient:#a855f7:#ec4899>Automod Manager</gradient>",
                "<gray>Configure automatic moderation",
                "",
                "<white>Active Rules: <green>" + countActiveRules(),
                "<white>Total Rules: <yellow>" + automod.getRules().size(),
                "",
                "<dark_gray>Rules are checked in order"));

        // === ROW 2: Built-in Filters (Quick Toggles) ===
        setItem(9, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        // Spam Protection
        AutomodRule spamRule = automod.getRule("spam_protection");
        boolean spamEnabled = spamRule != null && spamRule.isEnabled();
        setItem(10, createBuiltInToggle("Spam Filter", spamEnabled, Material.REPEATER,
                "Block rapid & duplicate messages",
                spamRule != null ? "Messages: " + spamRule.getSpamMessageCount() + " in " + spamRule.getSpamTimeWindowSeconds() + "s" : null),
                clickType -> {
                    if (clickType.isShiftClick() && spamRule != null) {
                        openSpamSettings(spamRule);
                    } else if (spamRule != null) {
                        spamRule.setEnabled(!spamRule.isEnabled());
                        automod.saveRule(spamRule);
                        playToggleSound(spamRule.isEnabled());
                        refresh();
                    }
                });

        // Caps Filter
        AutomodRule capsRule = automod.getRule("caps_filter");
        boolean capsEnabled = capsRule != null && capsRule.isEnabled();
        setItem(11, createBuiltInToggle("Caps Filter", capsEnabled, Material.NAME_TAG,
                "Convert excessive CAPS to lowercase",
                capsRule != null ? "Max: " + capsRule.getCapsMaxPercentage() + "% caps" : null),
                clickType -> {
                    if (clickType.isShiftClick() && capsRule != null) {
                        openCapsSettings(capsRule);
                    } else if (capsRule != null) {
                        capsRule.setEnabled(!capsRule.isEnabled());
                        automod.saveRule(capsRule);
                        playToggleSound(capsRule.isEnabled());
                        refresh();
                    }
                });

        // Link Filter
        AutomodRule linkRule = automod.getRule("link_filter");
        boolean linkEnabled = linkRule != null && linkRule.isEnabled();
        setItem(12, createBuiltInToggle("Link Filter", linkEnabled, Material.CHAIN,
                "Block URLs and IP addresses",
                null),
                () -> {
                    if (linkRule != null) {
                        linkRule.setEnabled(!linkRule.isEnabled());
                        automod.saveRule(linkRule);
                        playToggleSound(linkRule.isEnabled());
                        refresh();
                    }
                });

        // AFK Kick
        AutomodRule afkRule = automod.getRule("afk_kick");
        boolean afkEnabled = afkRule != null && afkRule.isEnabled();
        setItem(13, createBuiltInToggle("AFK Auto-Kick", afkEnabled, Material.CLOCK,
                "Kick inactive players",
                afkRule != null && afkRule.isAfkKickEnabled() ? "After: " + afkRule.getAfkTimeoutMinutes() + " min" : null),
                clickType -> {
                    if (clickType.isShiftClick() && afkRule != null) {
                        openAfkSettings(afkRule);
                    } else if (afkRule != null) {
                        afkRule.setEnabled(!afkRule.isEnabled());
                        automod.saveRule(afkRule);
                        playToggleSound(afkRule.isEnabled());
                        refresh();
                    }
                });

        // Info button
        setItem(16, createItem(Material.BOOK, "<aqua>How It Works",
                "<gray>Built-in filters process messages",
                "<gray>automatically when enabled.",
                "",
                "<yellow>Click <white>to toggle on/off",
                "<yellow>Shift+Click <white>to configure"));

        setItem(17, createItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        // === SEPARATOR ===
        for (int i = 18; i < 27; i++) {
            setItem(i, createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }
        setItem(22, createItem(Material.PAPER, "<white>Custom & Anticheat Rules",
                "<gray>Word filters and anticheat rules below"));

        // === RULES LIST (Rows 4-6, slots 27-53) ===
        List<AutomodRule> displayRules = getSortedRules();
        int totalPages = Math.max(1, (int) Math.ceil(displayRules.size() / (double) RULES_PER_PAGE));

        // Display rules in grid (slots 28-34, 37-43, 46-52)
        int[] ruleSlots = {
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43,
            46, 47, 48, 49, 50, 51, 52
        };

        int startIndex = currentPage * RULES_PER_PAGE;
        for (int i = 0; i < ruleSlots.length; i++) {
            int ruleIndex = startIndex + i;
            if (ruleIndex < displayRules.size()) {
                AutomodRule rule = displayRules.get(ruleIndex);
                setItem(ruleSlots[i], createRuleItem(rule), clickType -> {
                    if (clickType.isShiftClick() && !rule.isBuiltIn()) {
                        // For anticheat rules, open alert settings instead of deleting
                        if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                            openAnticheatAlertSettings(rule);
                        } else {
                            // Delete non-anticheat rules
                            Msg.send(viewer, TextUtil.parse("<red>Deleted rule: " + rule.getName()));
                            automod.deleteRule(rule.getId());
                            viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                            refresh();
                        }
                    } else {
                        openRuleEditor(rule);
                    }
                });
            } else {
                setItem(ruleSlots[i], createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        // === BOTTOM ROW: Navigation ===
        // Back button
        setItem(45, createItem(Material.ARROW, "<yellow>← Back", "<gray>Return to main menu"),
                () -> openGui(new MainMenuGui(plugin)));

        // Previous page
        if (currentPage > 0) {
            setItem(27, createItem(Material.ARROW, "<yellow>Previous Page",
                    "<gray>Page " + currentPage + "/" + totalPages), () -> {
                currentPage--;
                refresh();
            });
        } else {
            setItem(27, createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        // Create new word filter rule
        setItem(35, createItem(Material.LIME_DYE, "<green>+ Word Filter",
                "<gray>Create a custom word filter rule",
                "<gray>for blocking words in chat",
                "",
                "<yellow>Click to create"), this::promptCreateWordFilter);

        // Create new nickname rule
        setItem(44, createItem(Material.MAGENTA_DYE, "<light_purple>+ Nickname Filter",
                "<gray>Create a nickname filter rule",
                "<gray>for blocking inappropriate names",
                "",
                "<yellow>Click to create"), this::promptCreateNicknameRule);

        // Page indicator
        setItem(36, createItem(Material.PAPER, "<white>Page " + (currentPage + 1) + "/" + totalPages,
                "<gray>" + displayRules.size() + " rules total",
                "<gray>" + countAnticheatRules() + " anticheat rules",
                "<gray>" + countNicknameRules() + " nickname rules"));

        // Next page (move to slot 43 since 44 is nickname button)
        if (currentPage < totalPages - 1) {
            setItem(43, createItem(Material.ARROW, "<yellow>Next →",
                    "<gray>Page " + (currentPage + 2) + "/" + totalPages), () -> {
                currentPage++;
                refresh();
            });
        }

        // Close button
        setItem(53, createCloseButton(), this::close);
    }

    // ==================== HELPER METHODS ====================

    private List<AutomodRule> getSortedRules() {
        List<AutomodRule> rules = new ArrayList<>();

        // Add custom word filter rules first
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (!rule.isBuiltIn() && rule.getType() == AutomodRule.RuleType.WORD_FILTER) {
                rules.add(rule);
            }
        }

        // Add nickname rules
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (!rule.isBuiltIn() && rule.getType() == AutomodRule.RuleType.NICKNAME) {
                rules.add(rule);
            }
        }

        // Add anticheat rules at the end (sorted alphabetically)
        List<AutomodRule> acRules = new ArrayList<>();
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                acRules.add(rule);
            }
        }
        acRules.sort(Comparator.comparing(AutomodRule::getName));
        rules.addAll(acRules);

        return rules;
    }

    private int countActiveRules() {
        int count = 0;
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.isEnabled()) count++;
        }
        return count;
    }

    private int countAnticheatRules() {
        int count = 0;
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) count++;
        }
        return count;
    }

    private int countNicknameRules() {
        int count = 0;
        for (AutomodRule rule : plugin.getAutomodManager().getRules()) {
            if (rule.getType() == AutomodRule.RuleType.NICKNAME) count++;
        }
        return count;
    }

    private ItemStack createBuiltInToggle(String name, boolean enabled, Material icon, String description, String extra) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + description);
        if (extra != null) {
            lore.add("<dark_gray>" + extra);
        }
        lore.add("");
        lore.add(enabled ? "<green>✓ Enabled" : "<red>✗ Disabled");
        lore.add("");
        lore.add("<yellow>Click <gray>to toggle");
        lore.add("<yellow>Shift+Click <gray>to configure");

        return createItem(
            enabled ? Material.LIME_DYE : Material.RED_DYE,
            (enabled ? "<green>" : "<red>") + name,
            lore
        );
    }

    private ItemStack createRuleItem(AutomodRule rule) {
        List<String> lore = new ArrayList<>();

        // Type indicator
        String typeDisplay = switch (rule.getType()) {
            case WORD_FILTER -> "<blue>Word Filter";
            case NICKNAME -> "<light_purple>Nickname Filter";
            case ANTICHEAT -> "<red>Anticheat: " + (rule.getAnticheatName() != null ? rule.getAnticheatName() : "Unknown");
            default -> "<gray>" + rule.getType().name();
        };
        lore.add(typeDisplay);

        // Status
        lore.add(rule.isEnabled() ? "<green>✓ Enabled" : "<red>✗ Disabled");

        // Type-specific info
        if (rule.getType() == AutomodRule.RuleType.WORD_FILTER || rule.getType() == AutomodRule.RuleType.NICKNAME) {
            List<String> words = rule.getBlacklistedPhrases();
            lore.add("<gray>Phrases: <white>" + words.size());
            if (!words.isEmpty()) {
                String preview = words.get(0);
                if (preview.length() > 15) preview = preview.substring(0, 15) + "...";
                lore.add("<dark_gray>→ " + preview + (words.size() > 1 ? " +" + (words.size() - 1) + " more" : ""));
            }
        } else if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
            lore.add("<gray>Check: <white>" + (rule.getCheckName() != null ? rule.getCheckName() : "Unknown"));
            lore.add("<gray>Threshold: <white>" + rule.getAnticheatAlertThreshold() + " alerts in " + rule.getAnticheatTimeWindowSeconds() + "s");
        }

        // Auto-punishment indicator
        if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
            String punishType = rule.getAutoPunishment().getType().name();
            lore.add("<gold>⚠ Auto: " + punishType + " after " + rule.getAutoPunishment().getTriggerCount() + " flags");
        }

        lore.add("");
        lore.add("<yellow>Click <gray>to edit");
        if (!rule.isBuiltIn()) {
            if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                lore.add("<aqua>Shift+Click <gray>to configure alerts");
            } else {
                lore.add("<red>Shift+Click <gray>to delete");
            }
        }

        // Icon based on type and status
        Material icon;
        if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
            icon = rule.isEnabled() ? Material.DIAMOND_SWORD : Material.IRON_SWORD;
        } else if (rule.getAutoPunishment() != null && rule.getAutoPunishment().isEnabled()) {
            icon = Material.ORANGE_DYE;
        } else {
            icon = rule.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE;
        }

        return createItem(icon, (rule.isEnabled() ? "<white>" : "<gray>") + rule.getName(), lore);
    }

    private void playToggleSound(boolean enabled) {
        viewer.playSound(viewer.getLocation(),
            enabled ? Sound.BLOCK_NOTE_BLOCK_PLING : Sound.BLOCK_NOTE_BLOCK_BASS,
            1f, enabled ? 1.2f : 0.8f);
    }

    // ==================== BUILT-IN RULE SETTINGS ====================

    private void openSpamSettings(AutomodRule rule) {
        openGui(new SpamSettingsGui(plugin, rule, this));
    }

    private void openCapsSettings(AutomodRule rule) {
        openGui(new CapsSettingsGui(plugin, rule, this));
    }

    private void openAfkSettings(AutomodRule rule) {
        openGui(new AfkSettingsGui(plugin, rule, this));
    }

    private void openAnticheatAlertSettings(AutomodRule rule) {
        openGui(new AnticheatAlertSettingsGui(plugin, rule, this));
    }

    private void openRuleEditor(AutomodRule rule) {
        openGui(new RuleEditorGui(plugin, rule, this));
    }

    private void promptCreateWordFilter() {
        close();
        Msg.send(viewer, TextUtil.parse("<aqua>Enter a name for your new word filter rule:"));
        Msg.send(viewer, TextUtil.parse("<gray>Type 'cancel' to cancel"));

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
                        AutomodRule newRule = plugin.getAutomodManager().createRule(input, AutomodRule.RuleType.WORD_FILTER);
                        context.getForWhom().sendRawMessage(toLegacy("<green>Created word filter rule: " + input));

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            openGui(new RuleEditorGui(plugin, newRule, AutomodGui.this));
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

    private void promptCreateNicknameRule() {
        close();
        Msg.send(viewer, TextUtil.parse("<light_purple>Enter a name for your new nickname filter rule:"));
        Msg.send(viewer, TextUtil.parse("<gray>This rule will block players from using inappropriate nicknames"));
        Msg.send(viewer, TextUtil.parse("<gray>Type 'cancel' to cancel"));

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
                        AutomodRule newRule = plugin.getAutomodManager().createRule(input, AutomodRule.RuleType.NICKNAME);
                        context.getForWhom().sendRawMessage(toLegacy("<light_purple>Created nickname filter rule: " + input));

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            openGui(new RuleEditorGui(plugin, newRule, AutomodGui.this));
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

    // ==================== SPAM SETTINGS GUI ====================

    public static class SpamSettingsGui extends BaseGui {
        private final AutomodRule rule;
        private final AutomodGui parent;

        public SpamSettingsGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>Spam Protection Settings</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            // Enable/Disable toggle
            setItem(10, createItem(
                rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                "<gray>Toggle spam protection on/off",
                "",
                "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Message count
            setItem(12, createItem(Material.PAPER,
                "<gold>Message Limit: <white>" + rule.getSpamMessageCount(),
                "<gray>Messages allowed before flagging",
                "",
                "<yellow>Left-click: +1",
                "<yellow>Right-click: -1"), clickType -> {
                if (clickType.isLeftClick()) {
                    rule.setSpamMessageCount(Math.min(20, rule.getSpamMessageCount() + 1));
                } else if (clickType.isRightClick()) {
                    rule.setSpamMessageCount(Math.max(2, rule.getSpamMessageCount() - 1));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Time window
            setItem(13, createItem(Material.CLOCK,
                "<gold>Time Window: <white>" + rule.getSpamTimeWindowSeconds() + "s",
                "<gray>Time window for counting messages",
                "",
                "<yellow>Left-click: +1s",
                "<yellow>Right-click: -1s",
                "<yellow>Shift: ±5s"), clickType -> {
                int change = clickType.isShiftClick() ? 5 : 1;
                if (clickType.isLeftClick()) {
                    rule.setSpamTimeWindowSeconds(Math.min(60, rule.getSpamTimeWindowSeconds() + change));
                } else if (clickType.isRightClick()) {
                    rule.setSpamTimeWindowSeconds(Math.max(1, rule.getSpamTimeWindowSeconds() - change));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Similar message detection
            setItem(14, createItem(
                rule.isSpamDetectSimilar() ? Material.LIME_DYE : Material.RED_DYE,
                "<gold>Detect Similar: " + (rule.isSpamDetectSimilar() ? "<green>ON" : "<red>OFF"),
                "<gray>Block messages that are similar",
                "<gray>to previously sent messages",
                "",
                "<yellow>Click to toggle"), () -> {
                rule.setSpamDetectSimilar(!rule.isSpamDetectSimilar());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Similarity threshold
            int similarityPercent = (int) (rule.getSpamSimilarityThreshold() * 100);
            setItem(16, createItem(Material.COMPARATOR,
                "<gold>Similarity: <white>" + similarityPercent + "%",
                "<gray>How similar messages must be",
                "<gray>to be considered duplicates",
                "",
                "<yellow>Left-click: +5%",
                "<yellow>Right-click: -5%"), clickType -> {
                double change = 0.05;
                if (clickType.isLeftClick()) {
                    rule.setSpamSimilarityThreshold(Math.min(1.0, rule.getSpamSimilarityThreshold() + change));
                } else if (clickType.isRightClick()) {
                    rule.setSpamSimilarityThreshold(Math.max(0.5, rule.getSpamSimilarityThreshold() - change));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Auto-punishment section
            setItem(22, createPunishmentButtonItem(rule), clickType -> openGui(new PunishmentConfigGui(plugin, rule, this)));

            // Navigation
            setItem(27, createBackButton(), () -> openGui(parent));
            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }

        private ItemStack createPunishmentButtonItem(AutomodRule rule) {
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            boolean hasPunishment = punishment != null && punishment.isEnabled();

            List<String> lore = new ArrayList<>();
            if (hasPunishment) {
                lore.add("<green>✓ Enabled");
                lore.add("<gray>Type: <white>" + punishment.getType().name());
                if (punishment.getDuration() != 0) {
                    String duration = punishment.getDuration() == -1 ? "Permanent" : DurationParser.format(punishment.getDuration());
                    lore.add("<gray>Duration: <white>" + duration);
                }
                lore.add("<gray>After: <white>" + punishment.getTriggerCount() + " violations");
            } else {
                lore.add("<red>✗ Disabled");
                lore.add("<gray>No auto-punishment configured");
            }
            lore.add("");
            lore.add("<yellow>Click to configure");

            return createItem(
                hasPunishment ? Material.DIAMOND_SWORD : Material.WOODEN_SWORD,
                "<gold>Auto-Punishment",
                lore
            );
        }
    }

    // ==================== CAPS SETTINGS GUI ====================

    public static class CapsSettingsGui extends BaseGui {
        private final AutomodRule rule;
        private final AutomodGui parent;

        public CapsSettingsGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>Caps Filter Settings</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            // Enable/Disable toggle
            setItem(10, createItem(
                rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                "<gray>Toggle caps filter on/off",
                "",
                "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Max caps percentage
            setItem(12, createItem(Material.NAME_TAG,
                "<gold>Max Caps: <white>" + rule.getCapsMaxPercentage() + "%",
                "<gray>Maximum percentage of capital",
                "<gray>letters allowed in a message",
                "",
                "<yellow>Left-click: +5%",
                "<yellow>Right-click: -5%"), clickType -> {
                if (clickType.isLeftClick()) {
                    rule.setCapsMaxPercentage(Math.min(100, rule.getCapsMaxPercentage() + 5));
                } else if (clickType.isRightClick()) {
                    rule.setCapsMaxPercentage(Math.max(10, rule.getCapsMaxPercentage() - 5));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Minimum length
            setItem(14, createItem(Material.PAPER,
                "<gold>Min Length: <white>" + rule.getCapsMinLength() + " chars",
                "<gray>Minimum message length to check",
                "<gray>Short messages are ignored",
                "",
                "<yellow>Left-click: +1",
                "<yellow>Right-click: -1",
                "<yellow>Shift: ±5"), clickType -> {
                int change = clickType.isShiftClick() ? 5 : 1;
                if (clickType.isLeftClick()) {
                    rule.setCapsMinLength(Math.min(50, rule.getCapsMinLength() + change));
                } else if (clickType.isRightClick()) {
                    rule.setCapsMinLength(Math.max(3, rule.getCapsMinLength() - change));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Preview
            setItem(16, createItem(Material.BOOK, "<aqua>How It Works",
                "<gray>When a message exceeds " + rule.getCapsMaxPercentage() + "% caps",
                "<gray>and is longer than " + rule.getCapsMinLength() + " characters,",
                "<gray>it will be converted to lowercase.",
                "",
                "<white>Example:",
                "<red>HELLO WORLD → <green>hello world"));

            // Navigation
            setItem(27, createBackButton(), () -> openGui(parent));
            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }
    }

    // ==================== AFK SETTINGS GUI ====================

    public static class AfkSettingsGui extends BaseGui {
        private final AutomodRule rule;
        private final AutomodGui parent;

        public AfkSettingsGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>AFK Auto-Kick Settings</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            // Enable/Disable toggle
            setItem(11, createItem(
                rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                rule.isEnabled() ? "<green>Enabled" : "<red>Disabled",
                "<gray>Toggle AFK auto-kick on/off",
                "",
                "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Timeout
            setItem(13, createItem(Material.CLOCK,
                "<gold>Timeout: <white>" + rule.getAfkTimeoutMinutes() + " minutes",
                "<gray>Time before kicking AFK player",
                "",
                "<yellow>Left-click: +1 min",
                "<yellow>Right-click: -1 min",
                "<yellow>Shift: ±5 min"), clickType -> {
                int change = clickType.isShiftClick() ? 5 : 1;
                if (clickType.isLeftClick()) {
                    rule.setAfkTimeoutMinutes(Math.min(120, rule.getAfkTimeoutMinutes() + change));
                } else if (clickType.isRightClick()) {
                    rule.setAfkTimeoutMinutes(Math.max(1, rule.getAfkTimeoutMinutes() - change));
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            // Info
            setItem(15, createItem(Material.BOOK, "<aqua>How It Works",
                "<gray>Players who are inactive for",
                "<gray>" + rule.getAfkTimeoutMinutes() + " minutes will be kicked.",
                "",
                "<gray>Activity includes:",
                "<white>• Moving",
                "<white>• Chatting",
                "<white>• Breaking/placing blocks",
                "<white>• Interacting"));

            // Navigation
            setItem(27, createBackButton(), () -> openGui(parent));
            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }
    }

    // ==================== ANTICHEAT ALERT SETTINGS GUI ====================

    public static class AnticheatAlertSettingsGui extends BaseGui {
        private final AutomodRule rule;
        private final AutomodGui parent;
        private final StaffSettings staffSettings;

        public AnticheatAlertSettingsGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#ff6b6b:#ee5a5a>Alert Settings: " + rule.getName() + "</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
            this.staffSettings = plugin.getStaffSettingsManager().getSettings(parent.viewer);
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            String checkKey = (rule.getAnticheatName() != null ? rule.getAnticheatName() : "unknown") + ":"
                + (rule.getCheckName() != null ? rule.getCheckName() : "unknown");
            StaffSettings.CheckAlertPreference pref = staffSettings.getCheckAlertPreference(
                rule.getAnticheatName() != null ? rule.getAnticheatName() : "unknown",
                rule.getCheckName() != null ? rule.getCheckName() : "unknown"
            );

            // Info header
            setItem(4, createItem(Material.DIAMOND_SWORD,
                "<gold>Alert Configuration",
                "<gray>Configure how you receive alerts",
                "<gray>for this anticheat check.",
                "",
                "<white>Anticheat: <aqua>" + (rule.getAnticheatName() != null ? rule.getAnticheatName() : "Unknown"),
                "<white>Check: <aqua>" + (rule.getCheckName() != null ? rule.getCheckName() : "Unknown")));

            // Alert Level toggle
            AlertLevel currentLevel = pref.getAlertLevel();
            String levelColor = currentLevel.getColor();
            setItem(10, createItem(
                currentLevel == AlertLevel.EVERYONE ? Material.LIME_DYE :
                    currentLevel == AlertLevel.WATCHLIST_ONLY ? Material.YELLOW_DYE : Material.RED_DYE,
                "<gold>Alert Level: " + levelColor + currentLevel.getDisplayName(),
                "<gray>" + currentLevel.getDescription(),
                "",
                "<dark_gray>Options:",
                "<green>Everyone <gray>- All players",
                "<yellow>Watchlist Only <gray>- Monitored players",
                "<red>Off <gray>- No alerts",
                "",
                "<yellow>Click to cycle"), () -> {
                pref.setAlertLevel(currentLevel.next());
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                refresh();
            });

            // Threshold count
            setItem(12, createItem(Material.COMPARATOR,
                "<gold>Alert Threshold: <white>" + pref.getThresholdCount(),
                "<gray>Number of alerts required before",
                "<gray>you receive a notification.",
                "",
                "<dark_gray>1 = Every alert",
                "<dark_gray>5 = After 5 alerts",
                "",
                "<yellow>Left-click: +1",
                "<yellow>Right-click: -1",
                "<yellow>Shift: ±5"), clickType -> {
                int change = clickType.isShiftClick() ? 5 : 1;
                if (clickType.isLeftClick()) {
                    pref.setThresholdCount(Math.min(50, pref.getThresholdCount() + change));
                } else if (clickType.isRightClick()) {
                    pref.setThresholdCount(Math.max(1, pref.getThresholdCount() - change));
                }
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                refresh();
            });

            // Time window
            setItem(14, createItem(Material.CLOCK,
                "<gold>Time Window: <white>" + pref.getTimeWindowSeconds() + "s",
                "<gray>Alerts reset after this time.",
                "<gray>Threshold count within this window.",
                "",
                "<yellow>Left-click: +10s",
                "<yellow>Right-click: -10s",
                "<yellow>Shift: ±60s"), clickType -> {
                int change = clickType.isShiftClick() ? 60 : 10;
                if (clickType.isLeftClick()) {
                    pref.setTimeWindowSeconds(Math.min(600, pref.getTimeWindowSeconds() + change));
                } else if (clickType.isRightClick()) {
                    pref.setTimeWindowSeconds(Math.max(10, pref.getTimeWindowSeconds() - change));
                }
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                refresh();
            });

            // Quick presets
            setItem(16, createItem(Material.BOOK,
                "<aqua>Quick Presets",
                "<gray>Apply common configurations:",
                "",
                "<yellow>Left-click: <white>High Priority",
                "<dark_gray>  Every alert, 60s window",
                "<yellow>Right-click: <white>Medium",
                "<dark_gray>  3 alerts in 30s",
                "<yellow>Shift+click: <white>Low Priority",
                "<dark_gray>  10 alerts in 120s"), clickType -> {
                if (clickType.isShiftClick()) {
                    // Low priority
                    pref.setAlertLevel(AlertLevel.WATCHLIST_ONLY);
                    pref.setThresholdCount(10);
                    pref.setTimeWindowSeconds(120);
                } else if (clickType.isRightClick()) {
                    // Medium
                    pref.setAlertLevel(AlertLevel.EVERYONE);
                    pref.setThresholdCount(3);
                    pref.setTimeWindowSeconds(30);
                } else {
                    // High priority
                    pref.setAlertLevel(AlertLevel.EVERYONE);
                    pref.setThresholdCount(1);
                    pref.setTimeWindowSeconds(60);
                }
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                refresh();
            });

            // Current summary
            String summary;
            if (pref.getAlertLevel() == AlertLevel.OFF) {
                summary = "<red>Alerts are disabled";
            } else {
                summary = pref.getAlertLevel().getColor() + pref.getAlertLevel().getDisplayName() +
                    "<gray>, after <white>" + pref.getThresholdCount() +
                    " <gray>alerts in <white>" + pref.getTimeWindowSeconds() + "s";
            }
            setItem(22, createItem(Material.PAPER,
                "<white>Current Configuration",
                summary,
                "",
                "<gray>These are YOUR personal settings.",
                "<gray>Other staff have their own preferences."));

            // Navigation
            setItem(27, createBackButton(), () -> openGui(parent));
            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close"), () -> {
                plugin.getStaffSettingsManager().saveSettings(staffSettings);
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                Msg.send(viewer, TextUtil.parse("<green>Alert settings saved for <white>" + rule.getName()));
                openGui(parent);
            });
        }
    }

    // ==================== RULE EDITOR GUI (Single Page) ====================

    public static class RuleEditorGui extends BaseGui {
        private final AutomodRule rule;
        private final AutomodGui parent;

        public RuleEditorGui(ModereX plugin, AutomodRule rule, AutomodGui parent) {
            super(plugin, "<gradient:#a855f7:#ec4899>Edit: " + rule.getName() + "</gradient>", 5);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            // === TOP ROW: Basic Settings ===
            // Enable/Disable
            setItem(10, createItem(
                rule.isEnabled() ? Material.LIME_DYE : Material.RED_DYE,
                rule.isEnabled() ? "<green>✓ Enabled" : "<red>✗ Disabled",
                "<gray>Toggle this rule on/off",
                "",
                "<yellow>Click to toggle"), () -> {
                rule.setEnabled(!rule.isEnabled());
                plugin.getAutomodManager().saveRule(rule);
                viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                refresh();
            });

            // Rule type info
            String typeDisplay = switch (rule.getType()) {
                case WORD_FILTER -> "<blue>Word Filter";
                case NICKNAME -> "<light_purple>Nickname Filter";
                case ANTICHEAT -> "<red>Anticheat Rule";
                default -> "<gray>" + rule.getType().name();
            };
            setItem(11, createItem(Material.BOOK,
                "<gold>Type: " + typeDisplay,
                rule.getDescription() != null ? "<gray>" + rule.getDescription() : "<gray>No description"));

            // === WORD FILTER SETTINGS ===
            if (rule.getType() == AutomodRule.RuleType.WORD_FILTER || rule.getType() == AutomodRule.RuleType.NICKNAME) {
                // Applies To selector
                String appliesDisplay;
                Material appliesToIcon;
                if (rule.isNicknameOnly()) {
                    appliesDisplay = "<light_purple>Nicknames Only";
                    appliesToIcon = Material.NAME_TAG;
                } else if (rule.isApplyToNicknames()) {
                    appliesDisplay = "<gold>Chat & Nicknames";
                    appliesToIcon = Material.WRITABLE_BOOK;
                } else {
                    appliesDisplay = "<aqua>Chat Only";
                    appliesToIcon = Material.PAPER;
                }
                setItem(12, createItem(appliesToIcon,
                    "<gold>Applies To: " + appliesDisplay,
                    "<gray>Where this rule is enforced",
                    "",
                    "<white>Options:",
                    "<aqua>• Chat Only <gray>- Only chat messages",
                    "<gold>• Chat & Nicknames <gray>- Both",
                    "<light_purple>• Nicknames Only <gray>- Only nicknames",
                    "",
                    "<yellow>Click to cycle"), () -> {
                    if (rule.isNicknameOnly()) {
                        // Nicknames Only -> Chat Only
                        rule.setNicknameOnly(false);
                        rule.setApplyToNicknames(false);
                    } else if (rule.isApplyToNicknames()) {
                        // Chat & Nicknames -> Nicknames Only
                        rule.setNicknameOnly(true);
                    } else {
                        // Chat Only -> Chat & Nicknames
                        rule.setApplyToNicknames(true);
                    }
                    plugin.getAutomodManager().saveRule(rule);
                    viewer.playSound(viewer.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    refresh();
                });

                // Filter mode
                setItem(14, createItem(
                    rule.isExactMatch() ? Material.TARGET : Material.GLASS,
                    "<gold>Mode: " + (rule.isExactMatch() ? "<yellow>Exact Match" : "<aqua>Contains"),
                    "<gray>How to match phrases",
                    "",
                    rule.isExactMatch()
                        ? "<white>Matches whole message only"
                        : "<white>Matches anywhere in message",
                    "",
                    "<yellow>Click to toggle"), () -> {
                    rule.setExactMatch(!rule.isExactMatch());
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });

                // Blacklisted words
                List<String> words = rule.getBlacklistedPhrases();
                List<String> wordLore = new ArrayList<>();
                wordLore.add("<gray>Phrases that trigger this rule");
                wordLore.add("");
                wordLore.add("<white>" + words.size() + " phrases configured");
                if (!words.isEmpty()) {
                    wordLore.add("");
                    for (int i = 0; i < Math.min(5, words.size()); i++) {
                        String word = words.get(i);
                        if (word.length() > 20) word = word.substring(0, 20) + "...";
                        wordLore.add("<gray>• " + word);
                    }
                    if (words.size() > 5) {
                        wordLore.add("<dark_gray>... and " + (words.size() - 5) + " more");
                    }
                }
                wordLore.add("");
                wordLore.add("<yellow>Click to edit");

                setItem(15, createItem(Material.PAPER, "<gold>Blacklisted Phrases", wordLore),
                    this::promptEditWords);

                // Exclusions
                List<String> exclusions = rule.getExclusionPhrases();
                setItem(16, createItem(Material.EMERALD,
                    "<aqua>Exclusions: <white>" + exclusions.size(),
                    "<gray>Phrases that bypass this filter",
                    "",
                    "<yellow>Click to edit"),
                    this::promptEditExclusions);
            }

            // === ANTICHEAT SETTINGS ===
            if (rule.getType() == AutomodRule.RuleType.ANTICHEAT) {
                // Anticheat name
                setItem(13, createItem(Material.DIAMOND_SWORD,
                    "<gold>Anticheat: <white>" + (rule.getAnticheatName() != null ? rule.getAnticheatName() : "Unknown"),
                    "<gold>Check: <white>" + (rule.getCheckName() != null ? rule.getCheckName() : "Unknown"),
                    "",
                    "<gray>This rule triggers when players",
                    "<gray>flag this anticheat check"));

                // Alert threshold
                setItem(15, createItem(Material.COMPARATOR,
                    "<gold>Threshold: <white>" + rule.getAnticheatAlertThreshold() + " alerts",
                    "<gray>Alerts needed to trigger action",
                    "",
                    "<yellow>Left: +1 | Right: -1",
                    "<yellow>Shift: ±5"), clickType -> {
                    int change = clickType.isShiftClick() ? 5 : 1;
                    if (clickType.isLeftClick()) {
                        rule.setAnticheatAlertThreshold(Math.min(100, rule.getAnticheatAlertThreshold() + change));
                    } else if (clickType.isRightClick()) {
                        rule.setAnticheatAlertThreshold(Math.max(1, rule.getAnticheatAlertThreshold() - change));
                    }
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });

                // Time window
                setItem(16, createItem(Material.CLOCK,
                    "<gold>Time Window: <white>" + rule.getAnticheatTimeWindowSeconds() + "s",
                    "<gray>Reset violations after this time",
                    "",
                    "<yellow>Left: +5s | Right: -5s",
                    "<yellow>Shift: ±30s"), clickType -> {
                    int change = clickType.isShiftClick() ? 30 : 5;
                    if (clickType.isLeftClick()) {
                        rule.setAnticheatTimeWindowSeconds(Math.min(600, rule.getAnticheatTimeWindowSeconds() + change));
                    } else if (clickType.isRightClick()) {
                        rule.setAnticheatTimeWindowSeconds(Math.max(10, rule.getAnticheatTimeWindowSeconds() - change));
                    }
                    plugin.getAutomodManager().saveRule(rule);
                    refresh();
                });
            }

            // === AUTO PUNISHMENT (Middle Row) ===
            setItem(22, createPunishmentButtonItem(rule), clickType -> openGui(new PunishmentConfigGui(plugin, rule, this)));

            // === BOTTOM ROW ===
            // Back button
            setItem(36, createBackButton(), () -> openGui(parent));

            // Delete button (if not built-in)
            if (!rule.isBuiltIn()) {
                setItem(40, createItem(Material.BARRIER, "<red>Delete Rule",
                    "<gray>Permanently delete this rule",
                    "",
                    "<red>Shift+click to confirm"), clickType -> {
                    if (clickType.isShiftClick()) {
                        plugin.getAutomodManager().deleteRule(rule.getId());
                        Msg.send(viewer, TextUtil.parse("<red>Rule deleted."));
                        viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                        openGui(parent);
                    }
                });
            }

            // Save & Close
            setItem(44, createItem(Material.LIME_CONCRETE, "<green>Save & Close",
                "<gray>Save changes and return"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                Msg.send(viewer, TextUtil.parse("<green>Rule saved!"));
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }

        private void promptEditWords() {
            close();
            Msg.send(viewer, TextUtil.parse("<aqua>Enter phrases to filter (comma-separated):"));
            Msg.send(viewer, TextUtil.parse("<gray>Current: " + String.join(", ", rule.getBlacklistedPhrases())));
            Msg.send(viewer, TextUtil.parse("<gray>Type 'cancel' to cancel, 'clear' to remove all"));

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
                            rule.setBlacklistedPhrases(new ArrayList<>());
                            plugin.getAutomodManager().saveRule(rule);
                            context.getForWhom().sendRawMessage(toLegacy("<yellow>Phrases cleared."));
                        } else if (!input.equalsIgnoreCase("cancel")) {
                            List<String> words = new ArrayList<>();
                            for (String word : input.split(",")) {
                                word = word.trim();
                                if (!word.isEmpty()) {
                                    words.add(word);
                                }
                            }
                            rule.setBlacklistedPhrases(words);
                            plugin.getAutomodManager().saveRule(rule);
                            context.getForWhom().sendRawMessage(toLegacy("<green>Updated with " + words.size() + " phrases."));
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
            Msg.send(viewer, TextUtil.parse("<aqua>Enter exclusion phrases (comma-separated):"));
            Msg.send(viewer, TextUtil.parse("<gray>Current: " + String.join(", ", rule.getExclusionPhrases())));
            Msg.send(viewer, TextUtil.parse("<gray>Type 'cancel' to cancel, 'clear' to remove all"));

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
                            rule.setExclusionPhrases(new ArrayList<>());
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
                            rule.setExclusionPhrases(words);
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

        private ItemStack createPunishmentButtonItem(AutomodRule rule) {
            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            boolean hasPunishment = punishment != null && punishment.isEnabled();

            List<String> lore = new ArrayList<>();
            if (hasPunishment) {
                lore.add("<green>✓ Enabled");
                lore.add("<gray>Type: <white>" + punishment.getType().name());
                if (punishment.getDuration() != 0) {
                    String duration = punishment.getDuration() == -1 ? "Permanent" : DurationParser.format(punishment.getDuration());
                    lore.add("<gray>Duration: <white>" + duration);
                }
                lore.add("<gray>After: <white>" + punishment.getTriggerCount() + " violations");
            } else {
                lore.add("<red>✗ Disabled");
                lore.add("<gray>No auto-punishment configured");
            }
            lore.add("");
            lore.add("<yellow>Click to configure");

            return createItem(
                hasPunishment ? Material.DIAMOND_SWORD : Material.WOODEN_SWORD,
                "<gold>Auto-Punishment",
                lore
            );
        }
    }

    // ==================== PUNISHMENT CONFIG GUI ====================

    public static class PunishmentConfigGui extends BaseGui {
        private final AutomodRule rule;
        private final BaseGui parent;

        public PunishmentConfigGui(ModereX plugin, AutomodRule rule, BaseGui parent) {
            super(plugin, "<gradient:#ff6b6b:#ee5a5a>Auto-Punishment Config</gradient>", 4);
            this.rule = rule;
            this.parent = parent;
        }

        @Override
        protected void populate() {
            fillEmpty(Material.GRAY_STAINED_GLASS_PANE);

            AutomodRule.AutoPunishment punishment = rule.getAutoPunishment();
            boolean hasPunishment = punishment != null && punishment.isEnabled();

            // Enable toggle
            setItem(10, createItem(
                hasPunishment ? Material.LIME_DYE : Material.RED_DYE,
                hasPunishment ? "<green>Auto-Punish: ON" : "<red>Auto-Punish: OFF",
                "<gray>Automatically punish when rule triggers",
                "",
                "<yellow>Click to toggle"), () -> {
                if (punishment == null) {
                    AutomodRule.AutoPunishment newPunishment = new AutomodRule.AutoPunishment();
                    newPunishment.setEnabled(true);
                    newPunishment.setType(PunishmentType.WARN);
                    newPunishment.setDuration(0);
                    newPunishment.setTriggerCount(3);
                    newPunishment.setTimeWindow(300000);
                    rule.setAutoPunishment(newPunishment);
                } else {
                    punishment.setEnabled(!punishment.isEnabled());
                }
                plugin.getAutomodManager().saveRule(rule);
                refresh();
            });

            if (hasPunishment) {
                // Punishment type
                setItem(12, createItem(Material.IRON_SWORD,
                    "<gold>Type: <white>" + punishment.getType().name(),
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
                    String duration = punishment.getDuration() == -1 ? "<red>Permanent" :
                        punishment.getDuration() == 0 ? "<gray>Not set" :
                        "<white>" + DurationParser.format(punishment.getDuration());
                    setItem(13, createItem(Material.CLOCK,
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
                setItem(15, createItem(Material.REDSTONE,
                    "<gold>Trigger After: <white>" + punishment.getTriggerCount() + " violations",
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
                setItem(16, createItem(Material.HOPPER,
                    "<gold>Time Window: <white>" + window,
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

            // Navigation
            setItem(27, createBackButton(), () -> openGui(parent));
            setItem(35, createItem(Material.LIME_CONCRETE, "<green>Save & Close"), () -> {
                plugin.getAutomodManager().saveRule(rule);
                viewer.playSound(viewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                openGui(parent);
            });
        }
    }

    // ==================== SHARED METHODS ====================

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }
}
