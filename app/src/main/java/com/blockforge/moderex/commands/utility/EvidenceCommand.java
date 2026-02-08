package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.util.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Internal command for handling evidence selection UI clicks.
 * Command: /mx:evidence <action>
 *
 * Actions:
 * - toggle:<number> - Toggle selection of entry by number
 * - confirm - Confirm selection and proceed with punishment
 * - skip - Skip evidence and proceed without
 * - cancel - Cancel the punishment
 * - help - Show help
 * - list - Show entries again
 */
public class EvidenceCommand extends BaseCommand {

    public EvidenceCommand(ModereX plugin) {
        super(plugin, null, true); // No permission required, player only
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!plugin.getEvidenceSelectionManager().hasActiveSession(player.getUniqueId())) {
            Msg.send(player, Component.text("No active evidence selection session.", NamedTextColor.RED));
            return;
        }

        if (args.length == 0) {
            // Show the list
            plugin.getEvidenceSelectionManager().handleClick(player, "list");
            return;
        }

        String action = args[0].toLowerCase();
        plugin.getEvidenceSelectionManager().handleClick(player, action);
    }
}
