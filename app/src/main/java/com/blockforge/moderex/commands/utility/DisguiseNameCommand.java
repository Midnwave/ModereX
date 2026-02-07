package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.disguise.DisguiseProfile;
import com.blockforge.moderex.util.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to change disguise name only (keep current skin/rank).
 * Usage: /disguisename <name>
 */
public class DisguiseNameCommand extends BaseCommand {

    public DisguiseNameCommand(ModereX plugin) {
        super(plugin, "moderex.disguise", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Msg.send(sender, Component.text("This command can only be used by players.").color(NamedTextColor.RED));
            return;
        }

        if (args.length < 1) {
            Msg.send(player, Component.text("Usage: /disguisename <name>").color(NamedTextColor.RED));
            return;
        }

        String newName = args[0];

        if (!newName.matches("[a-zA-Z0-9_]{3,16}")) {
            Msg.send(player, Component.text("Invalid name! Must be 3-16 characters (letters, numbers, underscore only)")
                    .color(NamedTextColor.RED));
            return;
        }

        DisguiseProfile currentDisguise = plugin.getDisguiseManager().getDisguiseProfile(player);

        String rank = currentDisguise != null ? currentDisguise.getRank() : "default";
        String skinName = currentDisguise != null ? currentDisguise.getSkinName() : player.getName();

        if (!player.hasPermission("moderex.disguise.rank." + rank.toLowerCase())) {
            Msg.send(player, Component.text("You don't have permission to use rank: " + rank)
                    .color(NamedTextColor.RED));
            return;
        }

        DisguiseProfile profile = new DisguiseProfile(newName, skinName, rank);
        plugin.getDisguiseManager().disguise(player, profile);

        Msg.send(player, Component.text("§aDisguise name changed to: §f" + newName));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
