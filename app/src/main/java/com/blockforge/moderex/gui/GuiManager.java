package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.VersionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GuiManager {

    private final ModereX plugin;
    private final Map<UUID, BaseGui> openGuis = new ConcurrentHashMap<>();
    private final Map<UUID, GuiInputHandler> inputHandlers = new ConcurrentHashMap<>();

    public GuiManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, BaseGui gui) {
        if (player == null || gui == null) {
            plugin.logDebug("[GUI] Cannot open GUI - player or gui is null");
            return;
        }

        plugin.logDebug("[GUI] Opening " + gui.getClass().getSimpleName() + " for " + player.getName());

        if (shouldUseDialogs() && gui.supportsDialog()) {
            openDialog(player, gui);
            return;
        }

        closeCurrentGui(player);
        gui.setViewer(player);
        gui.build();
        openGuis.put(player.getUniqueId(), gui);
        plugin.logDebug("[GUI] Registered " + gui.getClass().getSimpleName() +
                       " for " + player.getName() +
                       " (total open GUIs: " + openGuis.size() + ")");

        player.openInventory(gui.getInventory());
        plugin.logDebug("[GUI] Inventory opened for " + player.getName() +
                       " - inv hash: " + System.identityHashCode(gui.getInventory()));
    }

    private void openDialog(Player player, BaseGui gui) {
        plugin.logDebug("[GUI] Dialog fallback - opening chest GUI for " + player.getName());

        closeCurrentGui(player);
        gui.setViewer(player);
        gui.build();
        openGuis.put(player.getUniqueId(), gui);
        player.openInventory(gui.getInventory());
    }

    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        BaseGui gui = openGuis.get(player.getUniqueId());
        if (gui == null) {
            plugin.logDebug("[GUI] handleClick called but no GUI for " + player.getName());
            return;
        }

        event.setCancelled(true);
        event.setResult(org.bukkit.event.Event.Result.DENY);

        // Check if click is in the top inventory (the GUI)
        int slot = event.getRawSlot();
        Inventory topInv = event.getView().getTopInventory();
        Inventory guiInv = gui.getInventory();

        // Use identity check or size check as fallback
        boolean isGuiInventory = (topInv == guiInv) ||
                                 (topInv.getSize() == guiInv.getSize() && slot < topInv.getSize());

        if (!isGuiInventory || slot < 0 || slot >= guiInv.getSize()) {
            // Click is in player inventory, ignore
            return;
        }

        plugin.logDebug("[GUI] handleClick processing slot " + slot + " for " + player.getName());
        gui.handleClick(slot, event.getClick());
    }

    public void handleClose(Player player) {
        BaseGui gui = openGuis.remove(player.getUniqueId());
        if (gui != null) {
            plugin.logDebug("[GUI] Handling close for " + player.getName() +
                           " - " + gui.getClass().getSimpleName() +
                           " (remaining open GUIs: " + openGuis.size() + ")");
            try {
                gui.onClose();
            } catch (Exception e) {
                plugin.logError("[GUI] Error in onClose for " + gui.getClass().getSimpleName(), e);
            }
        }
    }

    public void closeCurrentGui(Player player) {
        BaseGui gui = openGuis.remove(player.getUniqueId());
        if (gui != null) {
            plugin.logDebug("[GUI] Force closing " + gui.getClass().getSimpleName() +
                           " for " + player.getName());
            try {
                gui.onClose();
            } catch (Exception e) {
                plugin.logError("[GUI] Error in onClose for " + gui.getClass().getSimpleName(), e);
            }
            player.closeInventory();
        }
    }

    public BaseGui getOpenGui(Player player) {
        if (player == null) return null;
        return openGuis.get(player.getUniqueId());
    }

    public boolean hasGuiOpen(Player player) {
        if (player == null) return false;
        return openGuis.containsKey(player.getUniqueId());
    }

    public int getOpenGuiCount() {
        return openGuis.size();
    }

    public void registerInputHandler(Player player, GuiInputHandler handler) {
        inputHandlers.put(player.getUniqueId(), handler);
        player.closeInventory();
    }

    public boolean handleChatInput(Player player, String message) {
        GuiInputHandler handler = inputHandlers.remove(player.getUniqueId());
        if (handler != null) {
            handler.onInput(message);
            return true;
        }
        return false;
    }

    public void cancelInput(Player player) {
        GuiInputHandler handler = inputHandlers.remove(player.getUniqueId());
        if (handler != null) {
            handler.onCancel();
        }
    }

    public boolean hasPendingInput(Player player) {
        return inputHandlers.containsKey(player.getUniqueId());
    }

    public boolean shouldUseDialogs() {
        return VersionUtil.supportsDialogs() &&
                plugin.getConfigManager().getSettings().isDialogsEnabled();
    }

    public interface GuiInputHandler {
        void onInput(String input);

        default void onCancel() {
        }
    }
}
