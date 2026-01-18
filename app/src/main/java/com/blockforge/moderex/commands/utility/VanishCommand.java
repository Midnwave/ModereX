package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VanishCommand extends BaseCommand {

    public VanishCommand(ModereX plugin) {
        super(plugin, "moderex.command.vanish", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        // /vanish - toggle for self (player only)
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.PLAYER_ONLY));
                return;
            }
            Player player = (Player) sender;
            plugin.getVanishManager().toggleVanish(player);
            return;
        }

        String subcommand = args[0].toLowerCase();

        // /vanish list - show vanished players
        if (subcommand.equals("list")) {
            handleList(sender);
            return;
        }

        // /vanish toggle <player> or /vanish player <player>
        if (subcommand.equals("toggle") || subcommand.equals("player")) {
            if (args.length < 2) {
                sendMessage(sender, "<red>Usage: /vanish " + subcommand + " <player>");
                return;
            }

            if (!sender.hasPermission("moderex.command.vanish.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[1]);
                return;
            }

            plugin.getVanishManager().toggleVanish(target);

            if (plugin.getVanishManager().isVanished(target)) {
                sendMessage(sender, "<green>Vanished " + target.getName());
            } else {
                sendMessage(sender, "<green>Unvanished " + target.getName());
            }
            return;
        }

        // /vanish enable <player>
        if (subcommand.equals("enable") || subcommand.equals("on")) {
            if (args.length < 2) {
                sendMessage(sender, "<red>Usage: /vanish enable <player>");
                return;
            }

            if (!sender.hasPermission("moderex.command.vanish.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[1]);
                return;
            }

            if (plugin.getVanishManager().isVanished(target)) {
                sendMessage(sender, "<yellow>" + target.getName() + " is already vanished");
                return;
            }

            plugin.getVanishManager().vanish(target);
            sendMessage(sender, "<green>Vanished " + target.getName());
            return;
        }

        // /vanish disable <player>
        if (subcommand.equals("disable") || subcommand.equals("off")) {
            if (args.length < 2) {
                sendMessage(sender, "<red>Usage: /vanish disable <player>");
                return;
            }

            if (!sender.hasPermission("moderex.command.vanish.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[1]);
                return;
            }

            if (!plugin.getVanishManager().isVanished(target)) {
                sendMessage(sender, "<yellow>" + target.getName() + " is not vanished");
                return;
            }

            plugin.getVanishManager().unvanish(target);
            sendMessage(sender, "<green>Unvanished " + target.getName());
            return;
        }

        // If no valid subcommand, treat first arg as player name (for /vanish <player>)
        if (args.length == 1) {
            if (!sender.hasPermission("moderex.command.vanish.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[0]);
                return;
            }

            plugin.getVanishManager().toggleVanish(target);

            if (plugin.getVanishManager().isVanished(target)) {
                sendMessage(sender, "<green>Vanished " + target.getName());
            } else {
                sendMessage(sender, "<green>Unvanished " + target.getName());
            }
            return;
        }

        sendMessage(sender, "<red>Usage: /vanish [toggle|enable|disable|player|list] [player]");
    }

    private void handleList(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // Console sees all vanished players
            List<UUID> vanished = new ArrayList<>(plugin.getVanishManager().getVanishedPlayers());
            if (vanished.isEmpty()) {
                sendMessage(sender, "<yellow>No players are currently vanished");
                return;
            }

            sendMessage(sender, "<gold>Vanished Players (" + vanished.size() + "):");
            for (UUID uuid : vanished) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    int level = plugin.getVanishManager().getStoredVanishLevel(uuid);
                    String duration = plugin.getVanishManager().getVanishDuration(uuid);
                    sendMessage(sender, "  <gray>- <white>" + player.getName() + " <dark_gray>(Level " + level + ", " + duration + ")");
                }
            }
            return;
        }

        Player viewer = (Player) sender;
        boolean respectVisibility = plugin.getConfigManager().getSettings().isVanishListRespectVisibility();
        boolean showLevels = plugin.getConfigManager().getSettings().isVanishListShowLevels();

        List<Player> visibleVanished;
        if (respectVisibility) {
            visibleVanished = plugin.getVanishManager().getVisibleVanishedPlayers(viewer);
        } else {
            visibleVanished = plugin.getVanishManager().getVanishedPlayers().stream()
                    .map(Bukkit::getPlayer)
                    .filter(p -> p != null)
                    .collect(Collectors.toList());
        }

        if (visibleVanished.isEmpty()) {
            sendMessage(sender, "<yellow>No vanished players visible to you");
            return;
        }

        sendMessage(sender, "<gold>Vanished Players (" + visibleVanished.size() + "):");

        for (Player vanished : visibleVanished) {
            String format = plugin.getConfigManager().getSettings().getVanishListFormat();
            int level = plugin.getVanishManager().getStoredVanishLevel(vanished.getUniqueId());
            String duration = plugin.getVanishManager().getVanishDuration(vanished.getUniqueId());

            String message = format
                    .replace("{player}", vanished.getName())
                    .replace("{level}", String.valueOf(level))
                    .replace("{time}", duration);

            sender.sendMessage(TextUtil.parse(message));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("toggle", "enable", "disable", "player", "list"));

            // Add online player names if they have permission
            if (sender.hasPermission("moderex.command.vanish.others")) {
                completions.addAll(getOnlinePlayerNames(sender));
            }

            return filterCompletions(completions, args[0]);
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("toggle") || subcommand.equals("enable") ||
                subcommand.equals("disable") || subcommand.equals("player") ||
                subcommand.equals("on") || subcommand.equals("off")) {
                if (sender.hasPermission("moderex.command.vanish.others")) {
                    return filterCompletions(getOnlinePlayerNames(sender), args[1]);
                }
            }
        }

        return super.tabComplete(sender, args);
    }
}
