package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentTemplate;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class TemplateGui extends PaginatedGui<PunishmentTemplate> {

    private PunishmentType filterType = null;
    private String filterCategory = null;
    private final Consumer<PunishmentTemplate> onSelect;
    private final OfflinePlayer target;

    public TemplateGui(ModereX plugin, OfflinePlayer target, Consumer<PunishmentTemplate> onSelect) {
        super(plugin, "<gradient:#a855f7:#ec4899>Punishment Templates</gradient>", 6);
        this.target = target;
        this.onSelect = onSelect;
    }

    public TemplateGui(ModereX plugin) {
        this(plugin, null, null);
    }

    @Override
    protected List<PunishmentTemplate> getItems() {
        var templates = plugin.getTemplateManager().getAllTemplates();
        var stream = templates.stream();

        if (filterType != null) {
            stream = stream.filter(t -> t.getType() == filterType);
        }
        if (filterCategory != null) {
            stream = stream.filter(t -> filterCategory.equals(t.getCategory()));
        }

        return stream.toList();
    }

    @Override
    protected void renderItem(int slot, PunishmentTemplate template) {
        ItemStack item = createTemplateItem(template);
        setItem(slot, item, clickType -> {
            if (clickType.isRightClick() && viewer.hasPermission("moderex.template.edit")) {
                // Edit template
                openGui(new TemplateEditorGui(plugin, template, () -> openGui(this)));
            } else if (onSelect != null) {
                // Select template for punishment
                close();
                onSelect.accept(template);
            } else {
                // Show details
                showTemplateDetails(template);
            }
        });
    }

    @Override
    protected void populate() {
        super.populate();

        // Filter buttons in top row
        setItem(0, createFilterButton(null, "All Types", Material.CHEST), () -> {
            filterType = null;
            refresh();
        });

        setItem(1, createFilterButton(PunishmentType.WARN, "Warnings", Material.BOOK), () -> {
            filterType = PunishmentType.WARN;
            refresh();
        });

        setItem(2, createFilterButton(PunishmentType.MUTE, "Mutes", Material.PAPER), () -> {
            filterType = PunishmentType.MUTE;
            refresh();
        });

        setItem(3, createFilterButton(PunishmentType.KICK, "Kicks", Material.LEATHER_BOOTS), () -> {
            filterType = PunishmentType.KICK;
            refresh();
        });

        setItem(5, createFilterButton(PunishmentType.BAN, "Bans", Material.BARRIER), () -> {
            filterType = PunishmentType.BAN;
            refresh();
        });

        setItem(6, createFilterButton(PunishmentType.IPBAN, "IP Bans", Material.IRON_BARS), () -> {
            filterType = PunishmentType.IPBAN;
            refresh();
        });

        // Category filter
        setItem(7, createItem(Material.NAME_TAG, "<gold>Filter by Category",
                "<gray>Click to select a category",
                "",
                "<white>Current: " + (filterCategory != null ? "<yellow>" + filterCategory : "<gray>All"),
                "",
                "<yellow>Left-click: Select category",
                "<red>Right-click: Clear filter"), clickType -> {
            if (clickType.isRightClick()) {
                filterCategory = null;
                refresh();
            } else {
                openGui(new CategorySelectorGui(plugin, category -> {
                    filterCategory = category;
                    openGui(this);
                }));
            }
        });

        // Create new template button (admin only)
        if (viewer.hasPermission("moderex.template.create")) {
            setItem(8, createItem(Material.EMERALD, "<green>Create Template",
                    "<gray>Create a new punishment template",
                    "",
                    "<yellow>Click to create"), () -> {
                openGui(new TemplateEditorGui(plugin, null, () -> openGui(this)));
            });
        }
    }

    private ItemStack createFilterButton(PunishmentType type, String label, Material defaultIcon) {
        boolean selected = (type == filterType);
        Material material = selected ? Material.LIME_DYE : defaultIcon;

        return createItem(material,
                (selected ? "<green>" : "<gray>") + label,
                selected ? "<green>Currently viewing" : "<yellow>Click to filter");
    }

    private ItemStack createTemplateItem(PunishmentTemplate template) {
        Material material = getMaterialForType(template.getType());
        String typeColor = template.getTypeColor();

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Type: " + typeColor + template.getType().name());

        if (template.getDuration() != null && !template.getDuration().isEmpty()) {
            lore.add("<gray>Duration: <white>" + template.getDuration());
        } else {
            lore.add("<gray>Duration: <white>None (instant)");
        }

        if (template.getReason() != null && !template.getReason().isEmpty()) {
            lore.add("<gray>Reason: <white>" + truncate(template.getReason(), 30));
        }

        if (template.getCategory() != null) {
            lore.add("<gray>Category: <white>" + template.getCategory());
        }

        lore.add("");

        if (onSelect != null) {
            if (target != null) {
                lore.add("<yellow>Click to punish " + target.getName());
            } else {
                lore.add("<yellow>Click to select");
            }
        } else {
            lore.add("<yellow>Click to view details");
        }

        if (viewer.hasPermission("moderex.template.edit")) {
            lore.add("<aqua>Right-click to edit");
        }

        return createItem(material,
                typeColor + template.getName(),
                lore);
    }

    private Material getMaterialForType(PunishmentType type) {
        return switch (type) {
            case WARN -> Material.BOOK;
            case MUTE -> Material.PAPER;
            case KICK -> Material.LEATHER_BOOTS;
            case BAN -> Material.BARRIER;
            case IPBAN -> Material.IRON_BARS;
        };
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private void showTemplateDetails(PunishmentTemplate template) {
        viewer.sendMessage(TextUtil.parse(""));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse(template.getTypeColor() + "<bold>" + template.getName()));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
        viewer.sendMessage(TextUtil.parse("<gray>Type: <white>" + template.getType().name()));
        viewer.sendMessage(TextUtil.parse("<gray>Duration: <white>" + (template.getDuration() != null ? template.getDuration() : "None")));
        viewer.sendMessage(TextUtil.parse("<gray>Reason: <white>" + (template.getReason() != null ? template.getReason() : "None")));
        viewer.sendMessage(TextUtil.parse("<gray>Category: <white>" + (template.getCategory() != null ? template.getCategory() : "None")));
        viewer.sendMessage(TextUtil.parse("<gray>Priority: <white>" + template.getPriority()));
        viewer.sendMessage(TextUtil.parse("<gray>═══════════════════════════════════"));
    }

    @Override
    protected void addNavigationButtons() {
        super.addNavigationButtons();
        // Back button
        setItem(45, createBackButton(), () -> {
            if (target != null) {
                // Go back to punish GUI (would need to recreate it)
                close();
            } else {
                openGui(new MainMenuGui(plugin));
            }
        });
    }

    public static class CategorySelectorGui extends BaseGui {
        private final Consumer<String> onSelect;

        public CategorySelectorGui(ModereX plugin, Consumer<String> onSelect) {
            super(plugin, "<gold>Select Category", 3);
            this.onSelect = onSelect;
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            Set<String> categories = plugin.getTemplateManager().getCategories();
            int slot = 10;

            for (String category : categories) {
                if (slot > 16) break;

                setItem(slot, createItem(Material.NAME_TAG, "<yellow>" + category,
                        "<gray>Click to filter by this category"), () -> {
                    close();
                    onSelect.accept(category);
                });
                slot++;
            }

            // Back button
            setItem(22, createBackButton(), this::close);
        }
    }

    public static class TemplateEditorGui extends BaseGui {
        private final PunishmentTemplate template;
        private final Runnable onComplete;

        // Editable values
        private String name;
        private PunishmentType type;
        private String duration;
        private String reason;
        private String category;
        private int priority;

        public TemplateEditorGui(ModereX plugin, PunishmentTemplate existing, Runnable onComplete) {
            super(plugin, existing != null ? "<yellow>Edit Template" : "<green>Create Template", 5);
            this.onComplete = onComplete;

            if (existing != null) {
                this.template = existing;
                this.name = existing.getName();
                this.type = existing.getType();
                this.duration = existing.getDuration();
                this.reason = existing.getReason();
                this.category = existing.getCategory();
                this.priority = existing.getPriority();
            } else {
                this.template = null;
                this.name = "New Template";
                this.type = PunishmentType.WARN;
                this.duration = "";
                this.reason = "";
                this.category = "General";
                this.priority = 0;
            }
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            // Name
            setItem(10, createItem(Material.NAME_TAG, "<gold>Name: <white>" + name,
                    "<gray>The template name",
                    "",
                    "<yellow>Click to change"), () -> promptTextInput("Enter template name:", input -> {
                this.name = input;
                refresh();
            }));

            // Type
            setItem(12, createItem(getMaterialForType(type), "<aqua>Type: <white>" + type.name(),
                    "<gray>Punishment type",
                    "",
                    "<yellow>Click to cycle"), () -> {
                // Cycle through types
                PunishmentType[] types = PunishmentType.values();
                int nextIndex = (type.ordinal() + 1) % types.length;
                type = types[nextIndex];
                refresh();
            });

            // Duration
            setItem(14, createItem(Material.CLOCK, "<yellow>Duration: <white>" + (duration.isEmpty() ? "None" : duration),
                    "<gray>Punishment duration",
                    "<gray>Examples: 30m, 1h, 7d, 1mo, permanent",
                    "",
                    "<yellow>Click to change"), () -> promptTextInput("Enter duration (e.g., 30m, 1h, 7d, permanent):", input -> {
                this.duration = input;
                refresh();
            }));

            // Reason
            setItem(16, createItem(Material.PAPER, "<red>Reason:",
                    "<white>" + truncate(reason, 40),
                    "",
                    "<yellow>Click to change"), () -> promptTextInput("Enter reason:", input -> {
                this.reason = input;
                refresh();
            }));

            // Category
            setItem(20, createItem(Material.CHEST, "<light_purple>Category: <white>" + category,
                    "<gray>Template category for organization",
                    "",
                    "<yellow>Click to change"), () -> promptTextInput("Enter category:", input -> {
                this.category = input;
                refresh();
            }));

            // Priority
            setItem(22, createItem(Material.LADDER, "<blue>Priority: <white>" + priority,
                    "<gray>Lower numbers appear first",
                    "",
                    "<yellow>Left-click: +1",
                    "<yellow>Right-click: -1"), clickType -> {
                if (clickType.isLeftClick()) {
                    priority = Math.min(100, priority + 1);
                } else if (clickType.isRightClick()) {
                    priority = Math.max(0, priority - 1);
                }
                refresh();
            });

            // Preview
            setItem(24, createItem(getMaterialForType(type), "<white>Preview",
                    "<gray>Type: <white>" + type.name(),
                    "<gray>Duration: <white>" + (duration.isEmpty() ? "None" : duration),
                    "<gray>Reason: <white>" + truncate(reason, 25),
                    "<gray>Category: <white>" + category,
                    "<gray>Priority: <white>" + priority));

            // Save button
            setItem(30, createItem(Material.LIME_CONCRETE, "<green>Save Template",
                    "<gray>Save changes to this template",
                    "",
                    "<yellow>Click to save"), this::saveTemplate);

            // Cancel button
            setItem(32, createItem(Material.RED_CONCRETE, "<red>Cancel",
                    "<gray>Discard changes",
                    "",
                    "<yellow>Click to cancel"), () -> {
                close();
                onComplete.run();
            });

            // Delete button (only for existing templates)
            if (template != null && viewer.hasPermission("moderex.template.delete")) {
                setItem(40, createItem(Material.TNT, "<dark_red>Delete Template",
                        "<gray>Permanently delete this template",
                        "",
                        "<red>Click to delete"), () -> {
                    plugin.getTemplateManager().deleteTemplate(template.getId());
                    viewer.sendMessage(TextUtil.parse("<red>Template deleted!"));
                    close();
                    onComplete.run();
                });
            }
        }

        private Material getMaterialForType(PunishmentType type) {
            return switch (type) {
                case WARN -> Material.BOOK;
                case MUTE -> Material.PAPER;
                case KICK -> Material.LEATHER_BOOTS;
                case BAN -> Material.BARRIER;
                case IPBAN -> Material.IRON_BARS;
            };
        }

        private String truncate(String text, int maxLength) {
            if (text == null || text.isEmpty()) return "(none)";
            if (text.length() <= maxLength) return text;
            return text.substring(0, maxLength - 3) + "...";
        }

        private void saveTemplate() {
            PunishmentTemplate toSave;

            if (template != null) {
                toSave = template;
            } else {
                toSave = new PunishmentTemplate(name, type, duration, reason);
                toSave.setCreatedBy(viewer.getUniqueId());
                toSave.setCreatedByName(viewer.getName());
            }

            toSave.setName(name);
            toSave.setType(type);
            toSave.setDuration(duration);
            toSave.setReason(reason);
            toSave.setCategory(category);
            toSave.setPriority(priority);
            toSave.setUpdatedAt(System.currentTimeMillis());

            plugin.getTemplateManager().saveTemplate(toSave);

            viewer.sendMessage(TextUtil.parse("<green>Template saved successfully!"));
            close();
            onComplete.run();
        }

        private void promptTextInput(String prompt, Consumer<String> callback) {
            close();
            viewer.sendMessage(TextUtil.parse("<aqua>" + prompt));
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
                                callback.accept(input);
                            }
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                plugin.getGuiManager().open(viewer, TemplateEditorGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }
                    })
                    .withLocalEcho(false)
                    .withTimeout(60)
                    .buildConversation(viewer)
                    .begin();
        }
    }
}
