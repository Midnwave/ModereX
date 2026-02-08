package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.rules.Rule;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command to view and manage server rules.
 * Usage: /rules [page|rule#|category]
 */
public class RulesCommand extends BaseCommand {

    private static final int RULES_PER_PAGE = 5;

    public RulesCommand(ModereX plugin) {
        super(plugin, "moderex.rules", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        List<Rule> rules = plugin.getRulesManager().getEnabledRules();

        if (rules.isEmpty()) {
            sendMessage(sender, "<yellow>No server rules have been configured.");
            return;
        }

        // Check for specific rule number
        if (args.length > 0) {
            String arg = args[0].toLowerCase();

            // Check if viewing by rule number
            if (arg.matches("\\d+")) {
                int ruleNum = Integer.parseInt(arg);
                showSpecificRule(sender, rules, ruleNum);
                return;
            }

            // Check if viewing by category
            Map<String, List<Rule>> byCategory = rules.stream()
                    .filter(r -> r.getCategory() != null)
                    .collect(Collectors.groupingBy(r -> r.getCategory().toLowerCase()));

            if (byCategory.containsKey(arg)) {
                showCategory(sender, arg, byCategory.get(arg));
                return;
            }

            // Check if it's a page number (page:N)
            if (arg.startsWith("page:") || arg.startsWith("p:")) {
                String pageStr = arg.contains(":") ? arg.split(":")[1] : "1";
                int page = Math.max(1, Integer.parseInt(pageStr));
                showRulesPage(sender, rules, page);
                return;
            }
        }

        // Default: show first page
        showRulesPage(sender, rules, 1);
    }

    private void showRulesPage(CommandSender sender, List<Rule> rules, int page) {
        int totalPages = (int) Math.ceil(rules.size() / (double) RULES_PER_PAGE);
        page = Math.min(page, totalPages);

        int start = (page - 1) * RULES_PER_PAGE;
        int end = Math.min(start + RULES_PER_PAGE, rules.size());

        // Header
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╔══════════════════════════════════════════════════════════════╗"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ <gradient:#ff6b6b:#ee5a5a>Server Rules</gradient> <dark_gray>│ <gray>Page " + page + "/" + totalPages
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));

        // Rules
        for (int i = start; i < end; i++) {
            Rule rule = rules.get(i);
            String categoryTag = rule.getCategory() != null ?
                    "<dark_gray>[<" + getCategoryColor(rule.getCategory()) + ">" + rule.getCategory() + "<dark_gray>] " : "";

            Component ruleComponent = TextUtil.parse(
                    "<dark_gray>║ <yellow>" + rule.getOrder() + ". <white>" + rule.getTitle() + " " + categoryTag
            );

            if (sender instanceof Player) {
                ruleComponent = ruleComponent.hoverEvent(HoverEvent.showText(
                        TextUtil.parse("<gray>" + rule.getDescription() + "\n\n<yellow>Click for details")
                )).clickEvent(ClickEvent.runCommand("/rules " + rule.getOrder()));
            }

            Msg.send(sender, ruleComponent);
        }

        // Footer with navigation
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));

        Component navComponent = TextUtil.parse("<dark_gray>║ ");
        if (page > 1) {
            navComponent = navComponent.append(TextUtil.parse("<green>[◄ Prev] ")
                    .clickEvent(ClickEvent.runCommand("/rules page:" + (page - 1)))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Previous page"))));
        }

        navComponent = navComponent.append(TextUtil.parse("<gray>Click a rule for details "));

        if (page < totalPages) {
            navComponent = navComponent.append(TextUtil.parse("<green>[Next ►]")
                    .clickEvent(ClickEvent.runCommand("/rules page:" + (page + 1)))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Next page"))));
        }

        Msg.send(sender, navComponent);
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╚══════════════════════════════════════════════════════════════╝"
        ));
    }

    private void showSpecificRule(CommandSender sender, List<Rule> rules, int ruleNum) {
        Rule rule = rules.stream()
                .filter(r -> r.getOrder() == ruleNum)
                .findFirst()
                .orElse(null);

        if (rule == null) {
            sendMessage(sender, "<red>Rule #" + ruleNum + " not found.");
            return;
        }

        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╔══════════════════════════════════════════════════════════════╗"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ <gradient:#ff6b6b:#ee5a5a>Rule #" + rule.getOrder() + "</gradient>"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ <yellow>Title: <white>" + rule.getTitle()
        ));
        if (rule.getCategory() != null) {
            Msg.send(sender, TextUtil.parse(
                    "<dark_gray>║ <yellow>Category: <" + getCategoryColor(rule.getCategory()) + ">" + rule.getCategory()
            ));
        }
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ "
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ <gray>" + rule.getDescription()
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));

        Component backBtn = TextUtil.parse("<dark_gray>║ <green>[◄ Back to Rules]")
                .clickEvent(ClickEvent.runCommand("/rules"))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Return to rules list")));
        Msg.send(sender, backBtn);

        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╚══════════════════════════════════════════════════════════════╝"
        ));
    }

    private void showCategory(CommandSender sender, String category, List<Rule> rules) {
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╔══════════════════════════════════════════════════════════════╗"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>║ <gradient:#ff6b6b:#ee5a5a>" + capitalize(category) + " Rules</gradient>"
        ));
        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));

        for (Rule rule : rules) {
            Component ruleComponent = TextUtil.parse(
                    "<dark_gray>║ <yellow>" + rule.getOrder() + ". <white>" + rule.getTitle()
            );

            if (sender instanceof Player) {
                ruleComponent = ruleComponent.hoverEvent(HoverEvent.showText(
                        TextUtil.parse("<gray>" + rule.getDescription())
                )).clickEvent(ClickEvent.runCommand("/rules " + rule.getOrder()));
            }

            Msg.send(sender, ruleComponent);
        }

        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╠══════════════════════════════════════════════════════════════╣"
        ));

        Component backBtn = TextUtil.parse("<dark_gray>║ <green>[◄ All Rules]")
                .clickEvent(ClickEvent.runCommand("/rules"))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Return to all rules")));
        Msg.send(sender, backBtn);

        Msg.send(sender, TextUtil.parse(
                "<dark_gray>╚══════════════════════════════════════════════════════════════╝"
        ));
    }

    private String getCategoryColor(String category) {
        return switch (category.toLowerCase()) {
            case "general" -> "aqua";
            case "chat" -> "yellow";
            case "gameplay" -> "green";
            case "pvp" -> "red";
            case "building" -> "gold";
            default -> "gray";
        };
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new java.util.ArrayList<>();

            // Add rule numbers
            List<Rule> rules = plugin.getRulesManager().getEnabledRules();
            for (Rule rule : rules) {
                completions.add(String.valueOf(rule.getOrder()));
            }

            // Add categories
            rules.stream()
                    .map(Rule::getCategory)
                    .filter(c -> c != null)
                    .distinct()
                    .forEach(c -> completions.add(c.toLowerCase()));

            return filterCompletions(completions, args[0]);
        }
        return List.of();
    }
}
