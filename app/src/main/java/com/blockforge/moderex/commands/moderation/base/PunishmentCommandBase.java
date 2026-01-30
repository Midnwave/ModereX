package com.blockforge.moderex.commands.moderation.base;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.FlagParser;
import org.bukkit.command.CommandSender;

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
        if (checkDelete && flags.isDelete() && !sender.hasPermission("moderex.punish.delete")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isGlobal() && !sender.hasPermission("moderex.flag.global")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (checkIp && flags.isIpBased() && !hasIpPermission(sender)) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (checkModify && flags.isModify() && !sender.hasPermission("moderex.punish.modify")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isPublic() && !sender.hasPermission("moderex.flag.public")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isSilent() && !sender.hasPermission("moderex.flag.silent")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isExtraSilent() && !sender.hasPermission("moderex.flag.extrasilent")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isHidden() && !sender.hasPermission("moderex.flag.hidden")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.isSkip() && !sender.hasPermission("moderex.flag.skip")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return false;
        }
        if (flags.getServerOrigin() != null && !sender.hasPermission("moderex.flag.global")) {
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
        return sender.hasPermission("moderex.ipban");
    }
}
