package com.blockforge.moderex.commands.moderation.base;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Base class for punishment commands providing common flag permission checking.
 */
public abstract class PunishmentCommandBase extends BaseCommand {

    public PunishmentCommandBase(ModereX plugin, String permission, boolean playerOnly) {
        super(plugin, permission, playerOnly);
    }

    /**
     * Checks sender has permissions for all flags they've specified.
     * Returns true if all permissions are granted, false otherwise.
     */
    protected boolean checkFlagPermissions(CommandSender sender, FlagParser flags) {
        return checkFlagPermissions(sender, flags, true, true, true);
    }

    /**
     * Checks flag permissions with options to skip certain checks.
     * Uses moderex.flag.* permissions for punishment flags.
     *
     * @param sender The command sender
     * @param flags The parsed flags
     * @param checkIp Whether to check IP-based permissions
     * @param checkModify Whether to check modify permission
     * @param checkDelete Whether to check delete permission
     * @return true if all permissions are granted
     */
    protected boolean checkFlagPermissions(CommandSender sender, FlagParser flags,
                                           boolean checkIp, boolean checkModify, boolean checkDelete) {
        if (checkDelete && flags.isDelete() && !PermissionUtil.hasPermission(sender, "moderex.punish.delete")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isGlobal() && !PermissionUtil.hasPermission(sender, "moderex.flag.global")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (checkIp && flags.isIpBased() && !hasIpPermission(sender)) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (checkModify && flags.isModify() && !PermissionUtil.hasPermission(sender, "moderex.punish.modify")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isPublic() && !PermissionUtil.hasPermission(sender, "moderex.flag.public")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isSilent() && !PermissionUtil.hasPermission(sender, "moderex.flag.silent")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isExtraSilent() && !PermissionUtil.hasPermission(sender, "moderex.flag.extrasilent")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isHidden() && !PermissionUtil.hasPermission(sender, "moderex.flag.hidden")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isSkip() && !PermissionUtil.hasPermission(sender, "moderex.flag.skip")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.getServerOrigin() != null && !PermissionUtil.hasPermission(sender, "moderex.flag.global")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }

        return true;
    }

    /**
     * Get the appropriate IP permission for this command type.
     * Override in subclasses to specify different permissions (ipban, ipmute, ipwarn).
     */
    protected boolean hasIpPermission(CommandSender sender) {
        return PermissionUtil.hasPermission(sender, "moderex.ipban");
    }

    /**
     * Check staff weight against a resolved target.
     * Returns false (and sends error message) if sender has lower weight than target.
     */
    protected boolean checkTargetWeight(CommandSender sender, TargetResolver target) {
        if (target == null || target.getUuid() == null) return true;
        Player onlineTarget = Bukkit.getPlayer(target.getUuid());
        if (onlineTarget != null) {
            return checkWeight(sender, onlineTarget);
        }
        return true; // Offline players - allow by default
    }
}
