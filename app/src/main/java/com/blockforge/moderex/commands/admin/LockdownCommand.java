package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Activate or deactivate lockdown mode.
 * Prevents users from joining unless they have moderex.lockdown.bypass permission.
 * Useful as a temporary whitelist function.
 */
public class LockdownCommand extends BaseCommand {

    public LockdownCommand(ModereX plugin) {
        super(plugin, "moderex.lockdown", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        boolean isGlobal = false;
        boolean isEnd = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("server:global")) {
                isGlobal = true;
            } else if (arg.equalsIgnoreCase("server:local")) {
                isGlobal = false;
            } else if (arg.equalsIgnoreCase("end")) {
                isEnd = true;
            }
        }

        if (isEnd) {
            if (isGlobal) {
                plugin.setGlobalLockdown(false);
                sendMessage(sender, MessageKey.LOCKDOWN_DISABLED);
                plugin.broadcastToPermission(MessageKey.LOCKDOWN_BROADCAST_DISABLED,
                        "moderex.notify.lockdown",
                        "staff", sender.getName());
            } else {
                plugin.setLocalLockdown(false);
                sendMessage(sender, MessageKey.LOCKDOWN_DISABLED);
                plugin.broadcastToPermission(MessageKey.LOCKDOWN_BROADCAST_DISABLED,
                        "moderex.notify.lockdown",
                        "staff", sender.getName());
            }
        } else {
            if (isGlobal) {
                plugin.setGlobalLockdown(true);
                sendMessage(sender, MessageKey.LOCKDOWN_ENABLED);

                kickNonAllowedPlayers();

                plugin.broadcastToPermission(MessageKey.LOCKDOWN_BROADCAST_ENABLED,
                        "moderex.notify.lockdown",
                        "staff", sender.getName());
            } else {
                plugin.setLocalLockdown(true);
                sendMessage(sender, MessageKey.LOCKDOWN_ENABLED);

                kickNonAllowedPlayers();

                plugin.broadcastToPermission(MessageKey.LOCKDOWN_BROADCAST_ENABLED,
                        "moderex.notify.lockdown",
                        "staff", sender.getName());
            }
        }
    }

    private void kickNonAllowedPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("moderex.lockdown.bypass")) {
                player.kickPlayer("Server is currently in lockdown mode. Please try again later.");
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            return filterCompletions(Arrays.asList("server:local", "server:global", "end"), args[args.length - 1]);
        }
        return super.tabComplete(sender, args);
    }
}
