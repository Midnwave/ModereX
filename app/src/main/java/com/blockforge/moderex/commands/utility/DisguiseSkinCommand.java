package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.disguise.DisguiseProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to change disguise skin only (keep current name/rank).
 * Usage: /disguiseskin <playerName>
 */
public class DisguiseSkinCommand extends BaseCommand {

    public DisguiseSkinCommand(ModereX plugin) {
        super(plugin, "moderex.disguise", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /disguiseskin <playerName>").color(NamedTextColor.RED));
            player.sendMessage(Component.text("This will copy the skin of the specified player.").color(NamedTextColor.GRAY));
            return;
        }

        String skinName = args[0];

        // Validate name
        if (!skinName.matches("[a-zA-Z0-9_]{3,16}")) {
            player.sendMessage(Component.text("Invalid player name! Must be 3-16 characters.")
                    .color(NamedTextColor.RED));
            return;
        }

        // Get current disguise or create with player's current name
        DisguiseProfile currentDisguise = plugin.getDisguiseManager().getDisguiseProfile(player);

        String displayName = currentDisguise != null ? currentDisguise.getDisplayName() : player.getName();
        String rank = currentDisguise != null ? currentDisguise.getRank() : "default";

        // Create new disguise with updated skin
        DisguiseProfile profile = new DisguiseProfile(displayName, skinName, rank);
        plugin.getDisguiseManager().disguise(player, profile);

        player.sendMessage(Component.text("§aDisguise skin changed to: §f" + skinName));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            // Suggest online player names
            List<String> suggestions = new ArrayList<>();
            String prefix = args[0].toLowerCase();

            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    suggestions.add(p.getName());
                }
            }

            return suggestions;
        }
        return new ArrayList<>();
    }
}
