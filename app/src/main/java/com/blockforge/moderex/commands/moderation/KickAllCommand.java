package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// kicks all players - not recorded as a punishment
public class KickAllCommand extends BaseCommand {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    public KickAllCommand(ModereX plugin) {
        super(plugin, "moderex.command.kickall", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        String reason = args.length > 0 ? String.join(" ", args) : "Server maintenance";
        String staffName = sender instanceof Player player ? player.getName() : "Console";

        List<Player> playersToKick = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            // don't kick the sender
            if (sender instanceof Player senderPlayer && player.getUniqueId().equals(senderPlayer.getUniqueId())) {
                continue;
            }
            playersToKick.add(player);
        }

        if (playersToKick.isEmpty()) {
            sender.sendMessage(MM.deserialize("<yellow>No players to kick."));
            return;
        }

        Component kickMessage = buildKickMessage(reason, staffName);
        int kickedCount = 0;
        for (Player player : playersToKick) {
            player.kick(kickMessage);
            kickedCount++;
        }

        sender.sendMessage(MM.deserialize("<green>Kicked <white>" + kickedCount + "</white> player(s) from the server."));
        sender.sendMessage(MM.deserialize("<gray>Reason: <white>" + reason));

        Component staffNotification = MM.deserialize(
                "<dark_gray>[<red><bold>KICKALL</bold></red>]</dark_gray> " +
                        "<gold>" + staffName + "</gold> <gray>kicked</gray> <red>" + kickedCount + " player(s)</red> " +
                        "<dark_gray>»</dark_gray> <white>" + reason + "</white>"
        );

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("moderex.notify.punishments")) {
                staff.sendMessage(staffNotification);
            }
        }

        plugin.getLogger().info(staffName + " kicked " + kickedCount + " player(s) - Reason: " + reason);

        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastKickAll(staffName, kickedCount, reason);
        }
    }

    private Component buildKickMessage(String reason, String staffName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(centerText("§c§lKICKED FROM SERVER"));
        sb.append("\n\n");
        sb.append(centerText("§7Reason: §f" + reason));
        sb.append("\n\n");
        sb.append(centerText("§8Staff: §e" + staffName));
        sb.append("\n");

        return Component.text(sb.toString());
    }

    private String centerText(String text) {
        final int CENTER_PX = 154;
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : text.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + text;
    }

    // font info for message centering
    private enum DefaultFontInfo {
        A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5),
        E('E', 5), e('e', 5), F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5),
        I('I', 3), i('i', 1), J('J', 5), j('j', 5), K('K', 5), k('k', 4), L('L', 5), l('l', 1),
        M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5), P('P', 5), p('p', 5),
        Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4),
        U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5),
        Y('Y', 5), y('y', 5), Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5),
        NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5),
        NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6), NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5),
        ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4), RIGHT_PARENTHESIS(')', 4),
        MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5),
        SLASH('/', 5), BACK_SLASH('\\', 5), LINE('|', 1), TILDE('~', 5), TICK('`', 2),
        PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }
}
