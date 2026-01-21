package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.disguise.DisguiseFlags;
import com.blockforge.moderex.disguise.DisguiseGui;
import com.blockforge.moderex.disguise.DisguiseProfile;
import com.blockforge.moderex.util.FlagParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command to manage player disguises.
 * Usage: /disguise [name] [rank]
 *        /disguise gui
 *        /disguise remove
 */
public class DisguiseCommand extends BaseCommand {

    public DisguiseCommand(ModereX plugin) {
        super(plugin, "moderex.disguise", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
            return;
        }

        if (!player.hasPermission("moderex.command.disguise")) {
            player.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return;
        }

        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            openGui(player);
            return;
        }

        String subCommand = regularArgs.get(0).toLowerCase();

        switch (subCommand) {
            case "gui" -> {
                openGui(player);
            }
            case "remove", "off", "disable" -> {
                plugin.getDisguiseManager().undisguise(player);
            }
            case "help" -> {
                sendHelp(player);
            }
            default -> {
                String disguiseName = regularArgs.get(0);
                String skinName = disguiseName;
                String rank = regularArgs.size() > 1 ? regularArgs.get(1) : "default";

                if (flagParser.getSetDisplayName() != null) {
                    disguiseName = flagParser.getSetDisplayName();
                }

                if (flagParser.getSetSkin() != null) {
                    skinName = flagParser.getSetSkin();
                } else {
                    Player targetPlayer = Bukkit.getPlayer(disguiseName);
                    if (targetPlayer != null) {
                        skinName = targetPlayer.getName();
                        if (regularArgs.size() == 1 && flagParser.getSetDisplayName() == null) {
                            disguiseName = targetPlayer.getName();
                        }
                    }
                }

                if (flagParser.getFakeRank() != null) {
                    rank = flagParser.getFakeRank();
                }

                if (!player.hasPermission("moderex.disguise.rank." + rank.toLowerCase())) {
                    player.sendMessage(Component.text("You don't have permission to disguise as rank: " + rank)
                            .color(NamedTextColor.RED));
                    return;
                }

                DisguiseFlags flags = new DisguiseFlags(
                        flagParser.isHideRank(),
                        flagParser.getFakeRank(),
                        !flagParser.hasFlag("change-tab") || flagParser.isChangeTab(),
                        !flagParser.hasFlag("change-tab-complete") || flagParser.isChangeTabComplete()
                );

                DisguiseProfile profile = new DisguiseProfile(disguiseName, skinName, rank, flags);
                plugin.getDisguiseManager().disguise(player, profile);
            }
        }
    }

    /**
     * Open the disguise GUI for a player.
     *
     * @param player The player
     */
    private void openGui(Player player) {
        DisguiseGui gui = new DisguiseGui(plugin, player);
        gui.open();
    }

    /**
     * Send help message.
     *
     * @param player The player
     */
    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== Disguise Command Help ===").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("/disguise").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Open disguise GUI").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/disguise gui").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Open disguise GUI").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/disguise <name> [rank]").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Disguise with specific name").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/disguise remove").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Remove disguise").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("").color(NamedTextColor.GRAY));
        player.sendMessage(Component.text("Available Flags:").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("  --hide-rank").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Hide your rank").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  --fake-rank <rank>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Shows a fake rank").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  --set-skin <player>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Set skin to copy").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  --set-display-name <name>").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Set display name").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  --change-tab").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Change tab list name").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  --change-tab-complete").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Change tab completion").color(NamedTextColor.GRAY)));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        String lastArg = args.length > 0 ? args[args.length - 1] : "";

        if (lastArg.startsWith("--")) {
            completions.addAll(Arrays.asList(
                    "--hide-rank",
                    "--fake-rank",
                    "--set-skin",
                    "--set-display-name",
                    "--change-tab",
                    "--change-tab-complete"
            ));
            return filterByPrefix(completions, lastArg);
        }

        if (args.length >= 2) {
            String prevArg = args[args.length - 2];

            if (prevArg.equals("--fake-rank")) {
                if (sender instanceof Player player) {
                    if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                        try {
                            net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
                            for (net.luckperms.api.model.group.Group group : luckPerms.getGroupManager().getLoadedGroups()) {
                                if (player.hasPermission("moderex.disguise.rank." + group.getName().toLowerCase())) {
                                    completions.add(group.getName());
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
                if (completions.isEmpty()) {
                    completions.add("default");
                }
                return completions;
            } else if (prevArg.equals("--set-skin")) {
                return getOnlinePlayerNamesForCompletion();
            } else if (prevArg.equals("--set-display-name")) {
                return completions;
            }
        }

        if (regularArgs.isEmpty()) {
            completions.add("gui");
            completions.add("remove");
            completions.add("help");
            completions.addAll(getOnlinePlayerNamesForCompletion());
        } else if (regularArgs.size() == 1) {
            String firstArg = regularArgs.get(0).toLowerCase();
            if (!Arrays.asList("gui", "remove", "help").contains(firstArg)) {
                if (sender instanceof Player player) {
                    if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                        try {
                            net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
                            for (net.luckperms.api.model.group.Group group : luckPerms.getGroupManager().getLoadedGroups()) {
                                if (player.hasPermission("moderex.disguise.rank." + group.getName().toLowerCase())) {
                                    completions.add(group.getName());
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
                if (completions.isEmpty()) {
                    completions.add("default");
                }
            }
        }

        return completions;
    }

    private List<String> filterByPrefix(List<String> completions, String prefix) {
        String lowerPrefix = prefix.toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .toList();
    }

    protected List<String> getOnlinePlayerNamesForCompletion() {
        List<String> names = new ArrayList<>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            names.add(p.getName());
        }
        return names;
    }

}
