package com.blockforge.moderex.permissions;

import java.util.*;

/**
 * Represents a rank/group in the ModereX permission system.
 */
public class Rank {

    private int id;
    private String name;
    private String displayName;
    private String prefix;
    private String suffix;
    private int weight;
    private boolean isDefault;
    private long createdAt;
    private long updatedAt;

    // Permissions: permission -> granted (true=allow, false=deny)
    private final Map<String, Boolean> permissions = new LinkedHashMap<>();

    // Parent rank IDs for inheritance
    private final Set<Integer> parentRankIds = new LinkedHashSet<>();

    public Rank() {}

    public Rank(int id, String name, String displayName, String prefix, String suffix, int weight, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.weight = weight;
        this.isDefault = isDefault;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Boolean> getPermissions() { return permissions; }

    public void setPermission(String permission, boolean granted) {
        permissions.put(permission, granted);
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    public Set<Integer> getParentRankIds() { return parentRankIds; }

    public void addParentRank(int parentId) {
        parentRankIds.add(parentId);
    }

    public void removeParentRank(int parentId) {
        parentRankIds.remove(parentId);
    }

    public void clearParentRanks() {
        parentRankIds.clear();
    }
}
