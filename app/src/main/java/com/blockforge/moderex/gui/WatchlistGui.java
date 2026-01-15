package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.punishment.PunishPlayerGui;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class WatchlistGui extends PaginatedGui<UUID> {

    public WatchlistGui(ModereX plugin) {
        super(plugin, "<aqua>Watchlist", 6);
    }

    @Override
    protected List<UUID> getItems() {
        return new ArrayList<>(plugin.getWatchlistManager().getWatchedPlayers());
    }

    @Override
    protected void renderItem(int slot, UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);

        List<String> lore = new ArrayList<>();

        // Online status
        if (player.isOnline()) {
            meta.displayName(TextUtil.parse("<green>" + name + " <gray>(Online)"));
            lore.add("<green>Currently Online");
        } else {
            meta.displayName(TextUtil.parse("<yellow>" + name + " <gray>(Offline)"));
            long lastPlayed = player.getLastPlayed();
            if (lastPlayed > 0) {
                lore.add("<gray>Last seen: <white>" + formatTimeAgo(lastPlayed));
            }
        }

        lore.add("");
        lore.add("<gray>UUID: <white>" + uuid.toString().substring(0, 8) + "...");
        lore.add("");
        lore.add("<yellow>Left-click: <white>View player");
        lore.add("<red>Right-click: <white>Remove from watchlist");
        lore.add("<aqua>Shift-click: <white>Add note");

        meta.lore(lore.stream().map(TextUtil::parse).toList());
        head.setItemMeta(meta);

        setItem(slot, head, clickType -> {
            if (clickType.isShiftClick()) {
                // Add note
                addNoteToWatchlist(uuid, name);
            } else if (clickType.isRightClick()) {
                // Remove from watchlist
                removeFromWatchlist(uuid, name);
            } else {
                // View player
                if (player.isOnline()) {
                    openGui(new PunishPlayerGui(plugin, player.getPlayer()));
                } else {
                    openGui(new OfflinePlayerGui(plugin, uuid, name));
                }
            }
        });
    }

    @Override
    protected void addNavigationButtons() {
        super.addNavigationButtons();

        // Back to main menu
        setItem(45, createBackButton(), () -> openGui(new MainMenuGui(plugin)));

        // Add player to watchlist
        setItem(49, createItem(Material.EMERALD, "<green>Add Player",
                "<gray>Add a player to the watchlist",
                "",
                "<yellow>Click to add"), this::promptAddPlayer);
    }

    private void promptAddPlayer() {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter the player name to add to watchlist:"));
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
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                openGui(WatchlistGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        @SuppressWarnings("deprecation")
                        OfflinePlayer target = Bukkit.getOfflinePlayer(input);

                        if (!target.hasPlayedBefore() && !target.isOnline()) {
                            context.getForWhom().sendRawMessage(toLegacy("<red>Player not found: " + input));
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                openGui(WatchlistGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        if (plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
                            context.getForWhom().sendRawMessage(toLegacy("<yellow>" + target.getName() + " is already on the watchlist."));
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                openGui(WatchlistGui.this);
                            });
                            return Prompt.END_OF_CONVERSATION;
                        }

                        context.setSessionData("target", target);
                        return new ReasonPrompt();
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    private class ReasonPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return toLegacy("<aqua>Enter a reason for watching this player (or 'skip' to skip):");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            OfflinePlayer target = (OfflinePlayer) context.getSessionData("target");
            String reason = input.equalsIgnoreCase("skip") ? "Added via GUI" : input;

            plugin.getWatchlistManager().addToWatchlist(
                    target.getUniqueId(),
                    target.getName(),
                    viewer.getUniqueId(),
                    viewer.getName(),
                    reason
            );

            context.getForWhom().sendRawMessage(toLegacy("<green>Added " + target.getName() + " to the watchlist."));

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                openGui(new WatchlistGui(plugin));
            });

            return Prompt.END_OF_CONVERSATION;
        }
    }

    private void removeFromWatchlist(UUID uuid, String name) {
        plugin.getWatchlistManager().removeFromWatchlist(uuid);
        viewer.sendMessage(TextUtil.parse("<yellow>Removed <white>" + name + "<yellow> from the watchlist."));
        refresh();
    }

    private void addNoteToWatchlist(UUID uuid, String name) {
        close();
        viewer.sendMessage(TextUtil.parse("<aqua>Enter a note for " + name + ":"));
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
                            // Store note in database
                            plugin.getWatchlistManager().updateNote(uuid, input).thenAccept(success -> {
                                if (success) {
                                    context.getForWhom().sendRawMessage(toLegacy("<green>Note updated for " + name + "."));
                                } else {
                                    context.getForWhom().sendRawMessage(toLegacy("<red>Failed to update note."));
                                }
                            });
                        }

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            openGui(new WatchlistGui(plugin));
                        });
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(viewer)
                .begin();
    }

    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            return "Just now";
        }
    }

    private static String toLegacy(String miniMessage) {
        return LegacyComponentSerializer.legacySection().serialize(TextUtil.parse(miniMessage));
    }

    public static class OfflinePlayerGui extends BaseGui {

        private final UUID targetUuid;
        private final String targetName;

        public OfflinePlayerGui(ModereX plugin, UUID uuid, String name) {
            super(plugin, "<yellow>" + name, 3);
            this.targetUuid = uuid;
            this.targetName = name;
        }

        @Override
        protected void populate() {
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);

            // Player head
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(targetUuid));
            meta.displayName(TextUtil.parse("<yellow>" + targetName));

            List<String> lore = new ArrayList<>();
            lore.add("<gray>UUID: <white>" + targetUuid.toString());
            lore.add("");
            lore.add("<red>Player is offline");

            meta.lore(lore.stream().map(TextUtil::parse).toList());
            head.setItemMeta(meta);
            setItem(4, head);

            // Punishment history
            setItem(11, createItem(Material.BOOK, "<gold>Punishment History",
                    "<gray>View this player's history",
                    "",
                    "<yellow>Click to view"), () -> {
                openGui(new ModLogGui(plugin, targetUuid, targetName));
            });

            // Ban player
            setItem(13, createItem(Material.BARRIER, "<red>Ban Player",
                    "<gray>Issue a ban",
                    "",
                    "<yellow>Click to ban"), () -> {
                close();
                viewer.performCommand("ban " + targetName);
            });

            // Remove from watchlist
            if (plugin.getWatchlistManager().isWatched(targetUuid)) {
                setItem(15, createItem(Material.SPYGLASS, "<yellow>Remove from Watchlist",
                        "<gray>Remove from monitoring",
                        "",
                        "<yellow>Click to remove"), () -> {
                    plugin.getWatchlistManager().removeFromWatchlist(targetUuid);
                    viewer.sendMessage(TextUtil.parse("<yellow>Removed " + targetName + " from watchlist."));
                    refresh();
                });
            } else {
                setItem(15, createItem(Material.SPYGLASS, "<aqua>Add to Watchlist",
                        "<gray>Add to monitoring",
                        "",
                        "<yellow>Click to add"), () -> {
                    plugin.getWatchlistManager().addToWatchlist(
                            targetUuid, targetName,
                            viewer.getUniqueId(), viewer.getName(),
                            "Added via player profile"
                    );
                    viewer.sendMessage(TextUtil.parse("<green>Added " + targetName + " to watchlist."));
                    refresh();
                });
            }

            // Back button
            setItem(22, createBackButton(), () -> openGui(new WatchlistGui(plugin)));
        }
    }
}
