package com.blockforge.moderex.disguise;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * GUI for selecting disguise ranks and names.
 */
public class DisguiseGui {

    private final ModereX plugin;
    private final Player player;
    private final List<DisguiseRank> availableRanks;

    /**
     * Create a new disguise GUI.
     *
     * @param plugin The plugin instance
     * @param player The player viewing the GUI
     */
    public DisguiseGui(ModereX plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.availableRanks = loadRanksFromConfig();
    }

    /**
     * Open the disguise GUI.
     */
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("Select Disguise Rank"));

        // Add rank options
        int slot = 10;
        for (DisguiseRank rank : availableRanks) {
            if (slot >= 44) break; // Don't overflow inventory

            ItemStack item = createRankItem(rank);
            inv.setItem(slot, item);

            slot++;
            if ((slot + 1) % 9 == 8) { // Skip last column
                slot += 2;
            }
        }

        // Add "Remove Disguise" button
        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeItem.getItemMeta();
        if (removeMeta != null) {
            Msg.displayName(removeMeta, Component.text("Remove Disguise")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false));
            Msg.lore(removeMeta, List.of(
                    Component.text("Click to remove your current disguise")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            removeItem.setItemMeta(removeMeta);
        }
        inv.setItem(49, removeItem);

        player.openInventory(inv);
    }

    /**
     * Create an item stack for a rank.
     *
     * @param rank The rank
     * @return The item stack
     */
    private ItemStack createRankItem(DisguiseRank rank) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (meta != null) {
            Msg.displayName(meta, Component.text(rank.getDisplayName())
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Rank: " + rank.getRankName())
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));

            if (rank.getDescription() != null) {
                lore.add(Component.empty());
                lore.add(Component.text(rank.getDescription())
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false));
            }

            lore.add(Component.empty());
            lore.add(Component.text("Click to disguise as this rank")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false));

            Msg.lore(meta, lore);

            // Set player head texture if available
            if (rank.getSkullTexture() != null) {
                // Attempt to set texture (requires reflection or SkullMeta.setOwningPlayer())
                // For now, we'll just use a random UUID
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.randomUUID()));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Load ranks from config.
     *
     * @return List of disguise ranks
     */
    private List<DisguiseRank> loadRanksFromConfig() {
        List<DisguiseRank> ranks = new ArrayList<>();

        // Check if LuckPerms is available
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();

                // Get all groups from LuckPerms
                for (net.luckperms.api.model.group.Group group : luckPerms.getGroupManager().getLoadedGroups()) {
                    String groupName = group.getName();

                    // Check if player has permission to disguise as this rank
                    if (player.hasPermission("moderex.disguise.rank." + groupName.toLowerCase())) {
                        String displayName = group.getDisplayName() != null ? group.getDisplayName() : groupName;

                        // Get prefix from group
                        String prefix = group.getCachedData().getMetaData().getPrefix();
                        prefix = prefix != null ? prefix : "";

                        DisguiseRank rank = new DisguiseRank(
                                groupName,
                                displayName,
                                "Disguise as a " + displayName,
                                prefix,
                                null
                        );

                        ranks.add(rank);
                    }
                }

            } catch (Exception e) {
                plugin.logError("Failed to load LuckPerms groups for disguise GUI", e);
            }
        }

        // If no ranks from LuckPerms, load from config
        if (ranks.isEmpty()) {
            ranks.addAll(loadRanksFromPluginConfig());
        }

        return ranks;
    }

    /**
     * Load ranks from plugin config (fallback).
     *
     * @return List of disguise ranks
     */
    private List<DisguiseRank> loadRanksFromPluginConfig() {
        List<DisguiseRank> ranks = new ArrayList<>();

        // Load from config.yml under disguise.ranks
        var config = plugin.getConfig();
        if (config.contains("disguise.ranks")) {
            var ranksSection = config.getConfigurationSection("disguise.ranks");
            if (ranksSection != null) {
                for (String key : ranksSection.getKeys(false)) {
                    String displayName = ranksSection.getString(key + ".display-name", key);
                    String description = ranksSection.getString(key + ".description");
                    String prefix = ranksSection.getString(key + ".prefix", "");
                    String skullTexture = ranksSection.getString(key + ".skull-texture");

                    if (player.hasPermission("moderex.disguise.rank." + key.toLowerCase())) {
                        DisguiseRank rank = new DisguiseRank(key, displayName, description, prefix, skullTexture);
                        ranks.add(rank);
                    }
                }
            }
        }

        return ranks;
    }

    /**
     * Handle GUI click.
     *
     * @param slot The clicked slot
     */
    public void handleClick(int slot) {
        if (slot == 49) {
            // Remove disguise
            plugin.getDisguiseManager().undisguise(player);
            player.closeInventory();
            return;
        }

        // Calculate rank index from slot
        int index = calculateRankIndex(slot);

        if (index >= 0 && index < availableRanks.size()) {
            DisguiseRank rank = availableRanks.get(index);

            // Generate a random disguise name
            String disguiseName = generateRandomName();

            // Create disguise profile
            DisguiseProfile profile = new DisguiseProfile(disguiseName, disguiseName, rank.getRankName());

            // Apply disguise
            plugin.getDisguiseManager().disguise(player, profile);

            player.closeInventory();
        }
    }

    /**
     * Calculate rank index from inventory slot.
     *
     * @param slot The inventory slot
     * @return The rank index, or -1 if invalid
     */
    private int calculateRankIndex(int slot) {
        // Slots start at 10 and skip every 8th column
        if (slot < 10 || slot >= 44) return -1;

        int row = slot / 9;
        int col = slot % 9;

        if (col == 8 || col == 0) return -1; // Skip first and last columns

        int index = (row - 1) * 7 + (col - 1);
        return index;
    }

    /**
     * Generate a random disguise name.
     *
     * @return A random name
     */
    private String generateRandomName() {
        // Simple random name generator
        String[] prefixes = {"Dark", "Light", "Shadow", "Bright", "Swift", "Silent", "Mystic", "Ancient"};
        String[] suffixes = {"Wolf", "Eagle", "Bear", "Fox", "Hawk", "Lion", "Tiger", "Panther"};

        String prefix = prefixes[(int) (Math.random() * prefixes.length)];
        String suffix = suffixes[(int) (Math.random() * suffixes.length)];

        return prefix + suffix + (int) (Math.random() * 1000);
    }

    /**
     * Get the available ranks.
     *
     * @return List of ranks
     */
    public List<DisguiseRank> getAvailableRanks() {
        return availableRanks;
    }
}
