package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import com.blockforge.moderex.gui.TemplateGui;
import com.blockforge.moderex.punishment.PunishmentTemplate;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Base class for all punishment GUIs (Ban, Mute, Warn, Kick, IpBan).
 * New layout:
 * - Rows 0-2 (slots 0-26): Templates (26 slots + "View More" at slot 26)
 * - Row 3 (slots 27-35): Duration modifiers (+1h, +1d, +1w, +1mo | INFO | -1mo, -1w, -1d, -1h)
 * - Row 4 (slots 36-44): Custom actions (Reason, Duration, Add Players, Execute)
 * - Row 5 (slots 45-53): Navigation (Back, Close)
 */
public abstract class BasePunishmentGui extends BaseGui {

    protected final OfflinePlayer target;
    protected final Set<UUID> additionalTargets = new HashSet<>();

    protected String duration = "";
    protected String reason = "No reason specified";
    protected boolean isPermanent = false;

    // Duration components for +/- buttons
    protected int hours = 0;
    protected int days = 0;
    protected int weeks = 0;
    protected int months = 0;

    private static final int TEMPLATE_START_SLOT = 0;
    private static final int TEMPLATE_END_SLOT = 25;
    private static final int VIEW_MORE_SLOT = 26;

    private static final int CUSTOM_REASON_SLOT = 37;
    private static final int CUSTOM_DURATION_SLOT = 38;
    private static final int ADD_PLAYERS_SLOT = 39;
    private static final int EXECUTE_SLOT = 43;

    private static final int BACK_SLOT = 45;
    private static final int CLOSE_SLOT = 53;
    private static final int INFO_SLOT = 31;

    public BasePunishmentGui(ModereX plugin, OfflinePlayer target, String title) {
        super(plugin, title, 6);
        this.target = target;
    }

    protected abstract PunishmentType getPunishmentType();
    protected abstract void executePunishment();
    protected abstract Material getInfoMaterial();
    protected abstract String getInfoTitle();
    protected abstract boolean supportsDuration();

    @Override
    protected void populate() {
        // No glass border - clean look

        // Row 0-2: Templates
        populateTemplates();

        // Row 3: Duration modifiers (only if punishment type supports duration)
        if (supportsDuration()) {
            populateDurationModifiers();
        }

        // Info item in center of row 3
        populateInfoItem();

        // Row 4: Custom action buttons
        populateActionButtons();

        // Row 5: Navigation
        populateNavigation();
    }

    private void populateTemplates() {
        List<PunishmentTemplate> templates = plugin.getTemplateManager()
            .getTemplatesByType(getPunishmentType());

        int displayCount = Math.min(templates.size(), TEMPLATE_END_SLOT - TEMPLATE_START_SLOT + 1);
        boolean hasMore = templates.size() > displayCount;

        for (int i = 0; i < displayCount; i++) {
            PunishmentTemplate template = templates.get(i);
            int slot = TEMPLATE_START_SLOT + i;

            setItem(slot, createTemplateItem(template), () -> applyTemplate(template));
        }

        // View More button if there are more templates
        if (hasMore) {
            setItem(VIEW_MORE_SLOT, createItem(Material.CHEST,
                    "<gold>View More Templates",
                    "<gray>Click to see all " + templates.size() + " templates",
                    "<gray>for " + getPunishmentType().name().toLowerCase() + "s"),
                    () -> openGui(new TemplateGui(plugin, target, this::applyTemplate)));
        } else if (templates.isEmpty()) {
            setItem(13, createItem(Material.GRAY_DYE,
                    "<gray>No Templates",
                    "<gray>No templates available for",
                    "<gray>" + getPunishmentType().name().toLowerCase() + "s"));
        }
    }

    private ItemStack createTemplateItem(PunishmentTemplate template) {
        Material mat = switch (getPunishmentType()) {
            case WARN -> Material.BOOK;
            case MUTE -> Material.PAPER;
            case KICK -> Material.LEATHER_BOOTS;
            case BAN -> Material.BARRIER;
            case IPBAN -> Material.IRON_BARS;
            default -> Material.MAP;
        };

        List<String> lore = new ArrayList<>();
        lore.add("<gray>Duration: <white>" + (template.getDuration().isEmpty() ? "instant" : template.getDuration()));
        lore.add("<gray>Reason: <white>" + truncate(template.getReason(), 30));
        lore.add("");
        lore.add("<yellow>Click to apply this template");

        return createItem(mat, "<gold>" + template.getName(), lore);
    }

    private void applyTemplate(PunishmentTemplate template) {
        this.reason = template.getReason();
        if (!template.getDuration().isEmpty()) {
            parseDuration(template.getDuration());
        }
        refresh();
    }

    private void populateDurationModifiers() {
        // +1h, +1d, +1w, +1mo, [INFO], -1mo, -1w, -1d, -1h
        setItem(27, createDurationButton("+1h", Material.LIME_DYE), () -> modifyDuration(1, 0, 0, 0));
        setItem(28, createDurationButton("+1d", Material.LIME_DYE), () -> modifyDuration(0, 1, 0, 0));
        setItem(29, createDurationButton("+1w", Material.LIME_DYE), () -> modifyDuration(0, 0, 1, 0));
        setItem(30, createDurationButton("+1mo", Material.LIME_DYE), () -> modifyDuration(0, 0, 0, 1));

        // Slot 31 is INFO (handled separately)

        setItem(32, createDurationButton("-1mo", Material.RED_DYE), () -> modifyDuration(0, 0, 0, -1));
        setItem(33, createDurationButton("-1w", Material.RED_DYE), () -> modifyDuration(0, 0, -1, 0));
        setItem(34, createDurationButton("-1d", Material.RED_DYE), () -> modifyDuration(0, -1, 0, 0));
        setItem(35, createDurationButton("-1h", Material.RED_DYE), () -> modifyDuration(-1, 0, 0, 0));
    }

    private ItemStack createDurationButton(String label, Material material) {
        return createItem(material, "<yellow>" + label, "<gray>Click to adjust duration");
    }

    private void modifyDuration(int h, int d, int w, int mo) {
        hours = Math.max(0, hours + h);
        days = Math.max(0, days + d);
        weeks = Math.max(0, weeks + w);
        months = Math.max(0, months + mo);
        isPermanent = false;
        updateDurationString();
        refresh();
    }

    private void updateDurationString() {
        StringBuilder sb = new StringBuilder();
        if (months > 0) sb.append(months).append("mo");
        if (weeks > 0) sb.append(weeks).append("w");
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        duration = sb.toString();
    }

    private void parseDuration(String dur) {
        hours = 0;
        days = 0;
        weeks = 0;
        months = 0;
        isPermanent = false;

        if (dur == null || dur.isEmpty()) return;

        if (dur.equalsIgnoreCase("perm") || dur.equalsIgnoreCase("permanent")) {
            isPermanent = true;
            duration = "permanent";
            return;
        }

        StringBuilder num = new StringBuilder();
        for (char c : dur.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
            } else if (num.length() > 0) {
                int value = Integer.parseInt(num.toString());
                switch (Character.toLowerCase(c)) {
                    case 'h' -> hours = value;
                    case 'd' -> days = value;
                    case 'w' -> weeks = value;
                    case 'm' -> {
                        if (dur.toLowerCase().contains("mo")) months = value;
                    }
                }
                num.setLength(0);
            }
        }
        updateDurationString();
    }

    private void populateInfoItem() {
        List<String> lore = new ArrayList<>();
        lore.add("");

        // Target info
        lore.add("<gray>Target: <white>" + target.getName());

        // Show UUID if has permission
        if (viewer.hasPermission("moderex.info.uuid")) {
            lore.add("<gray>UUID: <dark_gray>" + target.getUniqueId().toString().substring(0, 8) + "...");
        }

        // Show IP if has permission and player is online
        if (viewer.hasPermission("moderex.info.ip") && target.isOnline()) {
            String ip = target.getPlayer().getAddress().getAddress().getHostAddress();
            lore.add("<gray>IP: <dark_gray>" + ip);
        }

        lore.add("");
        lore.add("<gray>Duration: <white>" + (isPermanent ? "Permanent" : (duration.isEmpty() ? "Not set" : formatDuration())));
        lore.add("<gray>Reason: <white>" + truncate(reason, 30));

        // Show additional targets if any (mass punishment)
        if (!additionalTargets.isEmpty()) {
            lore.add("");
            lore.add("<gold>+ " + additionalTargets.size() + " additional player(s)");
        }

        // Create player head item
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        Msg.displayName(meta, TextUtil.parseLore(getInfoTitle()));

        List<net.kyori.adventure.text.Component> loreComponents = new ArrayList<>();
        for (String line : lore) {
            for (String wrapped : TextUtil.wrapLore(line)) {
                loreComponents.add(TextUtil.parseLore(wrapped));
            }
        }
        Msg.lore(meta, loreComponents);
        head.setItemMeta(meta);

        setItem(INFO_SLOT, head);
    }

    private void populateActionButtons() {
        // Custom Reason
        setItem(CUSTOM_REASON_SLOT, createItem(Material.WRITABLE_BOOK,
                "<yellow>Custom Reason",
                "<gray>Current: <white>" + truncate(reason, 25),
                "",
                "<yellow>Click to set custom reason",
                "<gray>Max 100 characters"),
                this::promptCustomReason);

        // Custom Duration (only if supports duration)
        if (supportsDuration()) {
            setItem(CUSTOM_DURATION_SLOT, createItem(Material.CLOCK,
                    "<yellow>Custom Duration",
                    "<gray>Current: <white>" + (isPermanent ? "Permanent" : (duration.isEmpty() ? "Not set" : formatDuration())),
                    "",
                    "<yellow>Click to set custom duration",
                    "<gray>Format: 1w1d1h, perm, etc."),
                    this::promptCustomDuration);
        }

        // Add Players (for mass punishment)
        if (viewer.hasPermission(getMassPermission())) {
            setItem(ADD_PLAYERS_SLOT, createItem(Material.PLAYER_HEAD,
                    "<gold>Add Players",
                    "<gray>Current targets: <white>" + (1 + additionalTargets.size()),
                    "",
                    "<yellow>Click to add more players",
                    "<gray>For mass " + getPunishmentType().name().toLowerCase()),
                    this::promptAddPlayer);
        }

        // Execute button
        if (canExecute()) {
            setItem(EXECUTE_SLOT, createItem(Material.LIME_CONCRETE,
                    "<green>Execute " + getPunishmentType().name(),
                    "<gray>Target: <white>" + target.getName(),
                    supportsDuration() ? "<gray>Duration: <white>" + (isPermanent ? "Permanent" : (duration.isEmpty() ? "N/A" : formatDuration())) : "",
                    "<gray>Reason: <white>" + truncate(reason, 25),
                    "",
                    "<yellow>Click to execute"),
                    this::executePunishment);
        } else {
            List<String> missingLore = new ArrayList<>();
            missingLore.add("<red>Cannot execute yet:");
            if (supportsDuration() && duration.isEmpty() && !isPermanent) {
                missingLore.add("<gray>- Set a duration");
            }
            missingLore.add("");
            missingLore.add("<gray>Configure above to continue");

            setItem(EXECUTE_SLOT, createItem(Material.GRAY_CONCRETE,
                    "<gray>Execute " + getPunishmentType().name(),
                    missingLore));
        }
    }

    protected boolean canExecute() {
        if (supportsDuration()) {
            return !duration.isEmpty() || isPermanent;
        }
        return true;
    }

    private void populateNavigation() {
        setItem(BACK_SLOT, createBackButton(), () -> openGui(new PunishPlayerGui(plugin, target)));
        setItem(CLOSE_SLOT, createCloseButton(), this::close);
    }

    private void promptCustomReason() {
        close();
        promptInput("<yellow>Enter the " + getPunishmentType().name().toLowerCase() + " reason (max 100 chars):", input -> {
            if (input.length() > 100) {
                input = input.substring(0, 100);
            }
            reason = input;
            plugin.getGuiManager().open(viewer, this);
        });
    }

    private void promptCustomDuration() {
        close();
        promptInput("<yellow>Enter duration (e.g. 1w1h, 1d, perm):", input -> {
            String lower = input.toLowerCase().trim();
            if (lower.equals("perm") || lower.equals("permanent")) {
                if (viewer.hasPermission(getPermPermission())) {
                    isPermanent = true;
                    duration = "permanent";
                } else {
                    Msg.send(viewer, TextUtil.parse("<red>You don't have permission to issue permanent punishments."));
                }
            } else {
                parseDuration(input);
            }
            plugin.getGuiManager().open(viewer, this);
        });
    }

    private void promptAddPlayer() {
        close();
        promptInput("<yellow>Enter player name to add:", input -> {
            OfflinePlayer added = plugin.getServer().getOfflinePlayer(input);
            if (added.hasPlayedBefore() || added.isOnline()) {
                additionalTargets.add(added.getUniqueId());
                Msg.send(viewer, TextUtil.parse("<green>Added " + input + " to the punishment."));
            } else {
                Msg.send(viewer, TextUtil.parse("<red>Player not found: " + input));
            }
            plugin.getGuiManager().open(viewer, this);
        });
    }

    protected String formatDuration() {
        if (isPermanent) return "Permanent";
        if (duration.isEmpty()) return "Not set";

        StringBuilder sb = new StringBuilder();
        if (months > 0) sb.append(months).append(" month").append(months > 1 ? "s " : " ");
        if (weeks > 0) sb.append(weeks).append(" week").append(weeks > 1 ? "s " : " ");
        if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s " : " ");
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        return sb.toString().trim();
    }

    protected String getDurationForCommand() {
        if (isPermanent) return "permanent";
        return duration;
    }

    protected String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }

    protected String getMassPermission() {
        return "moderex.mass" + getPunishmentType().name().toLowerCase();
    }

    protected String getPermPermission() {
        return "moderex.perm" + getPunishmentType().name().toLowerCase();
    }

    public Set<UUID> getAdditionalTargets() {
        return additionalTargets;
    }

    public String getReason() {
        return reason;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPermanent() {
        return isPermanent;
    }
}
