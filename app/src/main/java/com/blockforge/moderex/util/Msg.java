package com.blockforge.moderex.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Cross-platform message utility.
 * Routes Paper-specific method calls through the correct API depending on server type.
 * On Paper: uses native methods (full Component fidelity with hover/click events).
 * On Spigot: serializes Components to legacy strings or uses BukkitAudiences.
 */
public final class Msg {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static BukkitAudiences audiences;
    private static boolean paper;

    private Msg() {
    }

    public static void init(Plugin plugin) {
        paper = VersionUtil.isPaper();
        if (!paper) {
            audiences = BukkitAudiences.create(plugin);
        }
    }

    public static void close() {
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
    }

    public static boolean isPaper() {
        return paper;
    }

    // --- Message Sending ---

    public static void send(CommandSender sender, Component message) {
        if (paper) {
            sender.sendMessage(message);
        } else {
            audiences.sender(sender).sendMessage(message);
        }
    }

    // --- Player Kick ---

    public static void kick(Player player, Component reason) {
        if (paper) {
            player.kick(reason);
        } else {
            player.kickPlayer(LEGACY.serialize(reason));
        }
    }

    // --- ItemMeta ---

    public static void displayName(ItemMeta meta, Component name) {
        if (paper) {
            meta.displayName(name);
        } else {
            meta.setDisplayName(LEGACY.serialize(name));
        }
    }

    public static void lore(ItemMeta meta, List<Component> lore) {
        if (paper) {
            meta.lore(lore);
        } else {
            List<String> legacy = new ArrayList<>();
            for (Component c : lore) {
                legacy.add(LEGACY.serialize(c));
            }
            meta.setLore(legacy);
        }
    }

    // --- Player Info ---

    public static String getDisplayName(Player player) {
        if (paper) {
            return TextUtil.toPlainText(player.displayName());
        } else {
            return player.getDisplayName();
        }
    }

    public static String getClientBrand(Player player) {
        if (paper) {
            return player.getClientBrandName();
        }
        return null;
    }

    // --- Inventory Creation ---

    public static Inventory createInventory(InventoryHolder owner, int size, Component title) {
        if (paper) {
            return Bukkit.createInventory(owner, size, title);
        } else {
            // On Spigot, use reflection to call the String-based method
            try {
                Method method = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, String.class);
                return (Inventory) method.invoke(null, owner, size, LEGACY.serialize(title));
            } catch (Exception e) {
                throw new RuntimeException("Failed to create inventory on Spigot", e);
            }
        }
    }

    // --- Join/Quit Messages ---

    public static void setJoinMessage(PlayerJoinEvent event, Component message) {
        if (paper) {
            event.joinMessage(message);
        } else {
            event.setJoinMessage(message == null ? null : LEGACY.serialize(message));
        }
    }

    public static void setQuitMessage(PlayerQuitEvent event, Component message) {
        if (paper) {
            event.quitMessage(message);
        } else {
            event.setQuitMessage(message == null ? null : LEGACY.serialize(message));
        }
    }
}
