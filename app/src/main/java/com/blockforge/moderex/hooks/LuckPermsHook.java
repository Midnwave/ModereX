package com.blockforge.moderex.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckPermsHook {

    private final LuckPerms luckPerms;

    public LuckPermsHook() {
        this.luckPerms = LuckPermsProvider.get();
    }

    public String getPrefix(Player player) {
        return getPrefix(player.getUniqueId());
    }

    public String getPrefix(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        return prefix != null ? prefix : "";
    }

    public String getSuffix(Player player) {
        return getSuffix(player.getUniqueId());
    }

    public String getSuffix(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String suffix = metaData.getSuffix();
        return suffix != null ? suffix : "";
    }

    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getUniqueId());
    }

    public String getPrimaryGroup(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }
        return user.getPrimaryGroup();
    }

    public String getMeta(Player player, String key) {
        return getMeta(player.getUniqueId(), key);
    }

    public String getMeta(UUID uuid, String key) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String value = metaData.getMetaValue(key);
        return value != null ? value : "";
    }

    public String getFormattedName(Player player) {
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        return prefix + player.getName() + suffix;
    }

    public boolean hasPermission(UUID uuid, String permission) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            // Try to load user data
            try {
                user = luckPerms.getUserManager().loadUser(uuid).join();
            } catch (Exception e) {
                return false;
            }
        }

        if (user == null) {
            return false;
        }

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
