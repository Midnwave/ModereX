package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.DisguiseGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryView;

/**
 * Listener for disguise-related events.
 */
public class DisguiseListener implements Listener {

    private final ModereX plugin;

    public DisguiseListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryView view = event.getView();
        String title = view.getTitle();

        if (title.equals("Select Disguise Rank")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            if (slot < 0 || slot >= view.getTopInventory().getSize()) {
                return; // Clicked outside inventory
            }

            // Create GUI instance and handle click
            DisguiseGui gui = new DisguiseGui(plugin, player);
            gui.handleClick(slot);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Replace join message if disguised
        Component disguisedMessage = plugin.getDisguiseManager().getDisguisedJoinMessage(player);
        if (disguisedMessage != null && plugin.getConfig().getBoolean("disguise.spoof-join-quit-messages", true)) {
            event.joinMessage(disguisedMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Replace quit message if disguised
        Component disguisedMessage = plugin.getDisguiseManager().getDisguisedQuitMessage(player);
        if (disguisedMessage != null && plugin.getConfig().getBoolean("disguise.spoof-join-quit-messages", true)) {
            event.quitMessage(disguisedMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDisguiseManager().isDisguised(player)) {
            return;
        }

        String disguisedName = plugin.getDisguiseManager().getDisplayName(player);

        // Cancel original event and send custom message
        event.setCancelled(true);

        String format = event.getFormat();
        String message = event.getMessage();

        // Replace player name and display name
        format = format.replace("%1$s", disguisedName);
        format = format.replace("%2$s", message);
        format = format.replace(player.getName(), disguisedName);
        format = format.replace(player.getDisplayName(), disguisedName);

        String finalMessage = format;

        // Broadcast to all players on main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                recipient.sendMessage(finalMessage);
            }
            plugin.getLogger().info(finalMessage);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // Log command with disguised name if applicable
        if (plugin.getDisguiseManager().isDisguised(player)) {
            String disguisedName = plugin.getDisguiseManager().getDisplayName(player);
            plugin.logDebug("[Disguise] " + disguisedName + " (" + player.getName() + ") executed: " + event.getMessage());
        }
    }
}
