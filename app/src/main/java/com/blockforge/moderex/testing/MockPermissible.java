package com.blockforge.moderex.testing;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A mock Permissible implementation for permission testing.
 * Does NOT require a real Bukkit Player - works purely with permission sets.
 * Used by PermissionTestEngine to simulate players with specific permission configurations.
 */
public class MockPermissible implements Permissible {

    private final String name;
    private final Set<String> permissions;
    private boolean isOp;

    public MockPermissible(String name, Set<String> permissions, boolean isOp) {
        this.name = name;
        this.permissions = new HashSet<>(permissions);
        this.isOp = isOp;
    }

    public String getName() {
        return name;
    }

    public Set<String> getGrantedPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    public void clearPermissions() {
        permissions.clear();
    }

    // --- Permissible interface ---

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return permissions.contains(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return permissions.contains(perm.getName());
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return permissions.contains(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return permissions.contains(perm.getName());
    }

    @Override
    public boolean isOp() {
        return isOp;
    }

    @Override
    public void setOp(boolean value) {
        this.isOp = value;
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        throw new UnsupportedOperationException("MockPermissible does not support attachments");
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        throw new UnsupportedOperationException("MockPermissible does not support attachments");
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        throw new UnsupportedOperationException("MockPermissible does not support attachments");
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        throw new UnsupportedOperationException("MockPermissible does not support attachments");
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        // No-op
    }

    @Override
    public void recalculatePermissions() {
        // No-op
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Set<PermissionAttachmentInfo> result = new HashSet<>();
        for (String perm : permissions) {
            result.add(new PermissionAttachmentInfo(this, perm, null, true));
        }
        return result;
    }
}
