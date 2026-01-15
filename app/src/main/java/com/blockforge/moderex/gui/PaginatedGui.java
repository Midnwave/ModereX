package com.blockforge.moderex.gui;

import com.blockforge.moderex.ModereX;
import org.bukkit.Material;

import java.util.List;

public abstract class PaginatedGui<T> extends BaseGui {

    protected int currentPage = 1;
    protected int itemsPerPage;
    protected List<T> items;

    // Slot configuration for paginated items
    protected int startSlot = 10;
    protected int endSlot = 43;
    protected int[] contentSlots;

    public PaginatedGui(ModereX plugin, String title, int rows) {
        super(plugin, title, rows);
        this.itemsPerPage = calculateItemsPerPage(rows);
        initContentSlots();
    }

    private int calculateItemsPerPage(int rows) {
        // Standard layout: border around edges, leaving interior slots
        return (rows - 2) * 7; // 7 columns per row (excluding borders)
    }

    private void initContentSlots() {
        // Calculate interior slots (excluding borders)
        int[] slots = new int[(rows - 2) * 7];
        int index = 0;
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < 8; col++) {
                slots[index++] = row * 9 + col;
            }
        }
        this.contentSlots = slots;
        this.itemsPerPage = slots.length;
    }

    protected abstract List<T> getItems();

    protected abstract void renderItem(int slot, T item);

    @Override
    protected void populate() {
        items = getItems();
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);

        // Place items for current page
        int totalPages = getTotalPages();
        int startIndex = (currentPage - 1) * itemsPerPage;

        for (int i = 0; i < contentSlots.length && startIndex + i < items.size(); i++) {
            T item = items.get(startIndex + i);
            renderItem(contentSlots[i], item);
        }

        // Navigation buttons
        addNavigationButtons();
    }

    protected void addNavigationButtons() {
        int totalPages = getTotalPages();
        int lastRow = rows * 9 - 9;

        // Previous page button (bottom left)
        if (currentPage > 1) {
            setItem(lastRow + 3, createPreviousButton(currentPage), () -> {
                currentPage--;
                refresh();
            });
        }

        // Page indicator (center)
        setItem(lastRow + 4, createItem(Material.PAPER,
                "<yellow>Page " + currentPage + "/" + Math.max(1, totalPages),
                "<gray>Total: " + items.size() + " items"));

        // Next page button (bottom right)
        if (currentPage < totalPages) {
            setItem(lastRow + 5, createNextButton(currentPage, totalPages), () -> {
                currentPage++;
                refresh();
            });
        }

        // Close button
        setItem(lastRow + 8, createCloseButton(), this::close);
    }

    protected int getTotalPages() {
        if (items == null || items.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) items.size() / itemsPerPage);
    }

    public void setPage(int page) {
        this.currentPage = Math.max(1, Math.min(page, getTotalPages()));
        refresh();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setContentSlots(int[] slots) {
        this.contentSlots = slots;
        this.itemsPerPage = slots.length;
    }
}
