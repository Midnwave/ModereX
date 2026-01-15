package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PunishSelectGui extends BaseGui {

    private int page = 0;
    private static final int PLAYERS_PER_PAGE = 28; // 7 cols x 4 rows

    public PunishSelectGui(ModereX plugin) {
        super(plugin, "<dark_red>Select Player to Punish", 6);
    }

    public PunishSelectGui(ModereX plugin, int page) {
        super(plugin, "<dark_red>Select Player to Punish", 6);
        this.page = page;
    }

    @Override
    protected void populate() {
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Get online players sorted by name
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.sort(Comparator.comparing(Player::getName, String.CASE_INSENSITIVE_ORDER));

        // Filter out the viewer if they're a player
        if (viewer != null) {
            players.removeIf(p -> p.getUniqueId().equals(viewer.getUniqueId()));
        }

        // Calculate pagination
        int totalPages = (int) Math.ceil((double) players.size() / PLAYERS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int startIndex = page * PLAYERS_PER_PAGE;
        int endIndex = Math.min(startIndex + PLAYERS_PER_PAGE, players.size());

        // Fill player heads in the middle area (slots 10-16, 19-25, 28-34, 37-43)
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };

        int slotIndex = 0;
        for (int i = startIndex; i < endIndex && slotIndex < slots.length; i++) {
            Player target = players.get(i);
            setItem(slots[slotIndex], createPlayerHead(target), () -> {
                openGui(new PunishPlayerGui(plugin, target));
            });
            slotIndex++;
        }

        // Show empty message if no players
        if (players.isEmpty()) {
            setItem(22, createItem(Material.BARRIER, "<gray>No players online",
                    "<dark_gray>Wait for players to join"));
        }

        // Page indicator
        setItem(4, createItem(Material.PAPER, "<yellow>Page " + (page + 1) + "/" + totalPages,
                "<gray>" + players.size() + " players online"));

        // Previous page button
        if (page > 0) {
            setItem(45, createPreviousButton(page + 1), () -> openGui(new PunishSelectGui(plugin, page - 1)));
        }

        // Next page button
        if (page < totalPages - 1) {
            setItem(53, createNextButton(page + 1, totalPages), () -> openGui(new PunishSelectGui(plugin, page + 1)));
        }

        // Close button
        setItem(49, createCloseButton(), this::close);

        // Refresh button
        setItem(48, createItem(Material.COMPASS, "<green>Refresh",
                "<gray>Click to refresh player list"), this::refresh);
    }

    private ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.displayName(TextUtil.parse("<yellow>" + player.getName()));

        List<String> lore = new ArrayList<>();
        lore.add("<gray>UUID: <white>" + player.getUniqueId().toString().substring(0, 8) + "...");

        // Check if player has active punishments
        var activeMute = plugin.getPunishmentManager().getActivePunishment(
                player.getUniqueId(),
                com.blockforge.moderex.punishment.PunishmentType.MUTE
        );
        if (activeMute != null && !activeMute.isExpired()) {
            lore.add("");
            lore.add("<gold>Currently Muted");
        }

        // Check if player is on watchlist
        if (plugin.getWatchlistManager().isWatched(player.getUniqueId())) {
            lore.add("");
            lore.add("<light_purple>On Watchlist");
        }

        lore.add("");
        lore.add("<yellow>Click <gray>to punish");

        meta.lore(lore.stream().map(TextUtil::parse).toList());
        head.setItemMeta(meta);
        return head;
    }
}
