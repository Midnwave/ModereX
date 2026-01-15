package com.blockforge.moderex.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_BRACKET_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final int CENTER_PX = 154;
    private static final DefaultFontInfo[] FONT_INFO = DefaultFontInfo.values();

    private TextUtil() {
    }

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        String converted = convertToMiniMessage(text);
        return MINI_MESSAGE.deserialize(converted);
    }

    public static Component parse(String text, String... replacements) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        String result = text;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            result = result.replace(replacements[i], replacements[i + 1]);
        }

        return parse(result);
    }

    public static String convertToMiniMessage(String text) {
        if (text == null) return "";

        String result = text;
        Matcher hexMatcher = HEX_PATTERN.matcher(result);
        result = hexMatcher.replaceAll("<#$1>");

        result = result
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>");

        return result;
    }

    public static String toPlainText(Component component) {
        return PLAIN_SERIALIZER.serialize(component);
    }

    public static String toLegacy(Component component) {
        return SECTION_SERIALIZER.serialize(component);
    }

    public static TextColor hexColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return TextColor.fromHexString("#" + hex);
    }

    public static String centerMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        String stripped = stripColor(message);
        int messagePxSize = getStringWidth(stripped);

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }

    public static List<String> centerMessages(List<String> messages) {
        List<String> centered = new ArrayList<>();
        for (String message : messages) {
            centered.add(centerMessage(message));
        }
        return centered;
    }

    public static Component centerComponent(String message) {
        return parse(centerMessage(message));
    }

    public static String stripColor(String text) {
        if (text == null) return null;
        text = text.replaceAll("<[^>]+>", "");
        text = text.replaceAll("&[0-9a-fk-or]", "");
        text = text.replaceAll("&#[A-Fa-f0-9]{6}", "");
        text = text.replaceAll("\u00A7[0-9a-fk-or]", "");
        return text;
    }

    public static int getStringWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            DefaultFontInfo info = DefaultFontInfo.getDefaultFontInfo(c);
            width += info.getLength() + 1;
        }
        return width;
    }

    // wraps text to max width, preserves color codes across lines
    public static List<String> wordWrap(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String lastColor = "";
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int visibleLength = 0;

        for (String word : words) {
            String visibleWord = stripColor(word);
            int wordLength = visibleWord.length();

            if (visibleLength + wordLength + (visibleLength > 0 ? 1 : 0) > maxLength) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString().trim());
                    lastColor = extractLastColor(currentLine.toString());
                    currentLine = new StringBuilder();
                    if (!lastColor.isEmpty()) {
                        currentLine.append(lastColor);
                    }
                    visibleLength = 0;
                }
            }

            if (visibleLength > 0) {
                currentLine.append(" ");
                visibleLength++;
            }

            currentLine.append(word);
            visibleLength += wordLength;

            String wordColor = extractLastColor(word);
            if (!wordColor.isEmpty()) {
                lastColor = wordColor;
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    public static List<String> wrapLore(String text) {
        return wordWrap(text, 25);
    }

    public static List<String> wrapLore(List<String> lines) {
        List<String> wrapped = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty() || stripColor(line).isEmpty()) {
                wrapped.add(line);
            } else {
                wrapped.addAll(wordWrap(line, 25));
            }
        }
        return wrapped;
    }

    private static String extractLastColor(String text) {
        StringBuilder lastColor = new StringBuilder();

        Pattern miniTagPattern = Pattern.compile("<([a-zA-Z_#][a-zA-Z0-9_#]*)>");
        Matcher matcher = miniTagPattern.matcher(text);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();
            if (!tag.equals("reset") && !tag.startsWith("/")) {
                if (tag.equals("bold") || tag.equals("italic") || tag.equals("underlined") ||
                    tag.equals("strikethrough") || tag.equals("obfuscated")) {
                    lastColor.append("<").append(tag).append(">");
                } else if (tag.startsWith("#") || isColorTag(tag)) {
                    lastColor = new StringBuilder("<" + tag + ">");
                }
            } else if (tag.equals("reset")) {
                lastColor = new StringBuilder();
            }
        }

        Pattern legacyPattern = Pattern.compile("&([0-9a-fk-or])");
        Matcher legacyMatcher = legacyPattern.matcher(text);
        while (legacyMatcher.find()) {
            char code = legacyMatcher.group(1).charAt(0);
            if (code == 'r') {
                lastColor = new StringBuilder();
            } else if (Character.isDigit(code) || (code >= 'a' && code <= 'f')) {
                lastColor = new StringBuilder("&" + code);
            } else {
                lastColor.append("&").append(code);
            }
        }

        return lastColor.toString();
    }

    private static boolean isColorTag(String tag) {
        return tag.equals("black") || tag.equals("dark_blue") || tag.equals("dark_green") ||
               tag.equals("dark_aqua") || tag.equals("dark_red") || tag.equals("dark_purple") ||
               tag.equals("gold") || tag.equals("gray") || tag.equals("dark_gray") ||
               tag.equals("blue") || tag.equals("green") || tag.equals("aqua") ||
               tag.equals("red") || tag.equals("light_purple") || tag.equals("yellow") ||
               tag.equals("white");
    }

    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static Component gradient(String text, String startColor, String endColor) {
        return MINI_MESSAGE.deserialize("<gradient:" + startColor + ":" + endColor + ">" + text + "</gradient>");
    }

    public static Component rainbow(String text) {
        return MINI_MESSAGE.deserialize("<rainbow>" + text + "</rainbow>");
    }

    private enum DefaultFontInfo {
        A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5),
        D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F', 5), f('f', 4),
        G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1),
        J('J', 5), j('j', 5), K('K', 5), k('k', 4), L('L', 5), l('l', 1),
        M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5),
        P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5),
        S('S', 5), s('s', 5), T('T', 5), t('t', 4), U('U', 5), u('u', 5),
        V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5),
        Y('Y', 5), y('y', 5), Z('Z', 5), z('z', 5),
        NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5),
        NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5),
        NUM_9('9', 5), NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6), POUND('#', 5),
        DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5),
        AMPERSAND('&', 5), ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4),
        RIGHT_PARENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5),
        PLUS('+', 5), EQUALS('=', 5), LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3),
        COLON(':', 1), SEMICOLON(';', 1), DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5),
        LINE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1),
        COMMA(',', 1), SPACE(' ', 3), DEFAULT('?', 5);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return character;
        }

        public int getLength() {
            return length;
        }

        public int getBoldLength() {
            return length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo info : values()) {
                if (info.getCharacter() == c) {
                    return info;
                }
            }
            return DEFAULT;
        }
    }
}
