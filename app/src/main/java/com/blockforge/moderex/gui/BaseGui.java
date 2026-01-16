package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseGui {

    protected final ModereX plugin;
    protected Player viewer;
    protected Inventory inventory;
    protected final Map<Integer, ClickHandler> clickHandlers = new HashMap<>();

    protected String title;
    protected int rows;

    public BaseGui(ModereX plugin, String title, int rows) {
        this.plugin = plugin;
        this.title = title;
        this.rows = Math.min(6, Math.max(1, rows));
    }

    public void build() {
        inventory = Bukkit.createInventory(null, rows * 9, TextUtil.parse(title));
        clickHandlers.clear();
        populate();
        plugin.logDebug("[GUI] Built " + getClass().getSimpleName() +
                       " with " + clickHandlers.size() + " click handlers");
    }

    protected abstract void populate();

    public void handleClick(int slot, ClickType clickType) {
        ClickHandler handler = clickHandlers.get(slot);
        if (handler != null) {
            plugin.logDebug("[GUI] " + getClass().getSimpleName() +
                           " - executing handler for slot " + slot);
            try {
                handler.handle(clickType);
            } catch (Exception e) {
                plugin.logError("[GUI] Error in click handler for " + getClass().getSimpleName() +
                               " slot " + slot, e);
            }
        } else {
            plugin.logDebug("[GUI] " + getClass().getSimpleName() +
                           " - no handler for slot " + slot +
                           " (registered slots: " + clickHandlers.keySet() + ")");
        }
    }

    public void onClose() {
        plugin.logDebug("[GUI] " + getClass().getSimpleName() + " onClose called");
    }

    public boolean supportsDialog() {
        return false;
    }

    protected void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }

    protected void setItem(int slot, ItemStack item, ClickHandler handler) {
        setItem(slot, item);
        if (handler != null) {
            clickHandlers.put(slot, handler);
        }
    }

    protected void setItem(int slot, ItemStack item, Runnable onClick) {
        setItem(slot, item, clickType -> onClick.run());
    }

    // auto-wraps lore to 25 chars
    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(TextUtil.parse(name));
        }

        if (lore != null && lore.length > 0) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                List<String> wrappedLines = TextUtil.wrapLore(line);
                for (String wrappedLine : wrappedLines) {
                    loreComponents.add(TextUtil.parse(wrappedLine));
                }
            }
            meta.lore(loreComponents);
        }

        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createItem(Material material, String name, List<String> lore) {
        List<String> wrappedLore = TextUtil.wrapLore(lore);
        return createItemNoWrap(material, name, wrappedLore);
    }

    private ItemStack createItemNoWrap(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.displayName(TextUtil.parse(name));
        }

        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(TextUtil.parse(line));
            }
            meta.lore(loreComponents);
        }

        item.setItemMeta(meta);
        return item;
    }

    protected void fillEmpty(Material material) {
        ItemStack filler = createItem(material, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                setItem(i, filler);
            }
        }
    }

    protected void fillBorder(Material material) {
        ItemStack border = createItem(material, " ");
        int size = inventory.getSize();

        for (int i = 0; i < 9; i++) {
            setItem(i, border);
        }

        for (int i = size - 9; i < size; i++) {
            setItem(i, border);
        }

        for (int i = 9; i < size - 9; i += 9) {
            setItem(i, border);
            setItem(i + 8, border);
        }
    }

    protected ItemStack createBackButton() {
        return createItem(Material.ARROW, "<red>Back", "<gray>Click to go back");
    }

    protected ItemStack createCloseButton() {
        return createItem(Material.BARRIER, "<red>Close", "<gray>Click to close");
    }

    protected ItemStack createConfirmButton() {
        return createItem(Material.LIME_CONCRETE, "<green>Confirm", "<gray>Click to confirm");
    }

    protected ItemStack createCancelButton() {
        return createItem(Material.RED_CONCRETE, "<red>Cancel", "<gray>Click to cancel");
    }

    protected ItemStack createNextButton(int currentPage, int totalPages) {
        if (currentPage >= totalPages) {
            return createItem(Material.GRAY_DYE, "<gray>No More Pages");
        }
        return createItem(Material.LIME_DYE, "<green>Next Page",
                "<gray>Page " + (currentPage + 1) + "/" + totalPages);
    }

    protected ItemStack createPreviousButton(int currentPage) {
        if (currentPage <= 1) {
            return createItem(Material.GRAY_DYE, "<gray>No Previous Pages");
        }
        return createItem(Material.LIME_DYE, "<green>Previous Page",
                "<gray>Page " + (currentPage - 1));
    }

    protected void refresh() {
        // Clear existing handlers and repopulate without creating new inventory
        clickHandlers.clear();
        inventory.clear();
        populate();
        plugin.logDebug("[GUI] Refreshed " + getClass().getSimpleName() +
                       " with " + clickHandlers.size() + " click handlers");
    }

    /**
     * Reopen the GUI with a fresh inventory (use when title changes or major rebuild needed)
     */
    protected void reopen() {
        plugin.getGuiManager().open(viewer, this);
    }

    protected void openGui(BaseGui gui) {
        plugin.getGuiManager().open(viewer, gui);
    }

    protected void close() {
        plugin.getGuiManager().closeCurrentGui(viewer);
    }

    protected void promptInput(String prompt, Consumer<String> onInput) {
        viewer.sendMessage(TextUtil.parse(prompt));
        plugin.getGuiManager().registerInputHandler(viewer, new GuiManager.GuiInputHandler() {
            @Override
            public void onInput(String input) {
                onInput.accept(input);
            }

            @Override
            public void onCancel() {
                plugin.getGuiManager().open(viewer, BaseGui.this);
            }
        });
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setViewer(Player viewer) {
        this.viewer = viewer;
    }

    public Player getViewer() {
        return viewer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @FunctionalInterface
    public interface ClickHandler {
        void handle(ClickType clickType);
    }
}
