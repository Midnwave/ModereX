package com.blockforge.moderex.commands;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().get(MessageKey.PLAYER_ONLY));
            return true;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
            return true;
        }

        try {
            execute(sender, args);
        } catch (Exception e) {
            plugin.logError("Error executing command: " + command.getName(), e);
            sender.sendMessage(TextUtil.parse("<red>An error occurred while executing this command."));
        }

        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            return Collections.emptyList();
        }

        return tabComplete(sender, args);
    }

    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    protected void sendMessage(CommandSender sender, MessageKey key, String... replacements) {
        sender.sendMessage(plugin.getLanguageManager().getPrefixed(key, replacements));
    }

    protected void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(plugin.getLanguageManager().getPrefix().append(TextUtil.parse(message)));
    }

    protected List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !plugin.getVanishManager().isVanished(p))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    // includes vanished players for staff
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
