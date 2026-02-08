package com.blockforge.moderex.commands;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    protected final ModereX plugin;
    protected final String permission;
    protected final boolean playerOnly;

    public BaseCommand(ModereX plugin, String permission, boolean playerOnly) {
        this.plugin = plugin;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    /**
     * Get the permission required for this command.
     * Used by CommandManager to set permission on PluginCommand for tab complete hiding.
     */
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            Msg.send(sender, plugin.getLanguageManager().get(MessageKey.PLAYER_ONLY));
            return true;
        }

        if (permission != null && !PermissionUtil.hasPermission(sender, permission)) {
            Msg.send(sender, plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
            // Debug mode: show required permission
            if (plugin.getConfigManager().getSettings().isDebugMode() && sender instanceof Player) {
                Msg.send(sender, TextUtil.parse("<dark_gray>[<gray>Debug<dark_gray>] <gray>Required permission: <yellow>" + permission));
            }
            return true;
        }

        try {
            execute(sender, args);
        } catch (Exception e) {
            plugin.logError("Error executing command: " + command.getName(), e);
            Msg.send(sender, TextUtil.parse("<red>An error occurred while executing this command."));
        }

        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (permission != null && !PermissionUtil.hasPermission(sender, permission)) {
            return Collections.emptyList();
        }

        return tabComplete(sender, args);
    }

    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    protected void sendMessage(CommandSender sender, MessageKey key, String... replacements) {
        Msg.send(sender, plugin.getLanguageManager().getPrefixed(key, replacements));
    }

    protected void sendMessage(CommandSender sender, Component message) {
        Msg.send(sender, message);
    }

    protected void sendMessage(CommandSender sender, String message) {
        Msg.send(sender, plugin.getLanguageManager().getPrefix().append(TextUtil.parse(message)));
    }

    protected List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !plugin.getVanishManager().isVanished(p))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    protected List<String> getOnlinePlayerNames(CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !plugin.getVanishManager().isVanished(p) ||
                        sender.hasPermission("moderex.command.vanish"))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    protected List<String> filterCompletions(List<String> completions, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return completions;
        }
        String lowerPrefix = prefix.toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .collect(Collectors.toList());
    }

    /**
     * Check if the sender has sufficient staff weight to act on the target.
     * Sends an error message if blocked.
     * @return true if the sender can act on the target
     */
    protected boolean checkWeight(CommandSender sender, Player target) {
        if (target == null) return true;
        if (!PermissionUtil.canActOn(sender, target)) {
            int senderWeight = PermissionUtil.getWeight(sender);
            int targetWeight = PermissionUtil.getWeight(target);
            sendMessage(sender, "<red>You cannot perform this action on " + target.getName() + ".");
            sendMessage(sender, "<gray>Their staff rank is higher than yours.");
            if (plugin.getConfigManager().getSettings().isDebugMode() && sender instanceof Player) {
                Msg.send(sender, TextUtil.parse("<dark_gray>[<gray>Debug<dark_gray>] <gray>Your weight: <yellow>" + senderWeight +
                        " <gray>| Target weight: <yellow>" + targetWeight));
            }
            return false;
        }
        return true;
    }

    /**
     * Check if the sender has sufficient staff weight to act on a target by UUID.
     */
    protected boolean checkWeight(CommandSender sender, java.util.UUID targetUuid) {
        Player target = Bukkit.getPlayer(targetUuid);
        if (target != null) {
            return checkWeight(sender, target);
        }
        // Offline player - allow by default (can't check weight without LuckPerms)
        return true;
    }

    protected Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    protected String joinArgs(String[] args, int fromIndex) {
        if (fromIndex >= args.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = fromIndex; i < args.length; i++) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }
}
