package com.blockforge.moderex.rules;

/**
 * Represents a server rule that players must follow.
 */
public class Rule {

    private int id;
    private int order;
    private String title;
    private String description;
    private String category;
    private boolean enabled;
    private long createdAt;
    private long updatedAt;

    public Rule() {
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Rule(int order, String title, String description) {
        this();
        this.order = order;
        this.title = title;
        this.description = description;
    }

    public Rule(int order, String title, String description, String category) {
        this(order, title, description);
        this.category = category;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", order=" + order +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
