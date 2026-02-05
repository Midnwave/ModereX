package com.blockforge.moderex.commands;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.admin.*;
import com.blockforge.moderex.commands.moderation.punishment.*;
import com.blockforge.moderex.commands.moderation.check.*;
import com.blockforge.moderex.commands.moderation.list.*;
import com.blockforge.moderex.commands.moderation.history.*;
import com.blockforge.moderex.commands.utility.*;
import com.blockforge.moderex.commands.utility.ReplayCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final ModereX plugin;
    private final boolean isPaper;

    public CommandManager(ModereX plugin) {
        this.plugin = plugin;
        this.isPaper = checkIfPaper();
    }

    private boolean checkIfPaper() {
        try {
            Class.forName("io.papermc.paper.plugin.configuration.PluginMeta");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void registerAll() {
        if (isPaper) {
            plugin.getLogger().info("Detected Paper - using programmatic command registration");
            registerPaperCommands();
        } else {
            plugin.getLogger().info("Detected Spigot - using plugin.yml command registration");
            registerSpigotCommands();
        }

        plugin.getLogger().info("Registered all commands.");
    }

    private void registerPaperCommands() {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            plugin.getLogger().severe("Failed to get CommandMap!");
            return;
        }

        registerPaperCommand(commandMap, "moderex", new MxCommand(plugin), "Main ModereX command", List.of("mx"));

        registerPaperCommand(commandMap, "ban", new BanCommand(plugin), "Ban a player", List.of());
        registerPaperCommand(commandMap, "mute", new MuteCommand(plugin), "Mute a player", List.of());
        registerPaperCommand(commandMap, "warn", new WarnCommand(plugin), "Warn a player", List.of());
        registerPaperCommand(commandMap, "kick", new KickCommand(plugin), "Kick a player", List.of());
        registerPaperCommand(commandMap, "tempban", new TempBanCommand(plugin), "Temporarily ban a player", List.of());
        registerPaperCommand(commandMap, "tempmute", new TempMuteCommand(plugin), "Temporarily mute a player", List.of());
        registerPaperCommand(commandMap, "ipban", new IPBanCommand(plugin), "IP ban a player", List.of("banip", "ban-ip"));
        registerPaperCommand(commandMap, "ipmute", new IPMuteCommand(plugin), "IP mute a player", List.of("muteip"));

        registerPaperCommand(commandMap, "unban", new UnbanCommand(plugin), "Unban a player", List.of());
        registerPaperCommand(commandMap, "unmute", new UnmuteCommand(plugin), "Unmute a player", List.of());
        registerPaperCommand(commandMap, "unwarn", new UnwarnCommand(plugin), "Remove a warning", List.of());

        registerPaperCommand(commandMap, "check", new CheckCommand(plugin), "Checks comprehensive player info", List.of());
        registerPaperCommand(commandMap, "checkban", new CheckBanCommand(plugin), "Checks player is banned", List.of());
        registerPaperCommand(commandMap, "checkmute", new CheckMuteCommand(plugin), "Checks player is muted", List.of());
        registerPaperCommand(commandMap, "checkwarn", new CheckWarnCommand(plugin), "Checks player warnings", List.of());

        registerPaperCommand(commandMap, "history", new HistoryCommand(plugin), "View punishment history", List.of("hist", "punishments", "phistory"));
        registerPaperCommand(commandMap, "staffhistory", new StaffHistoryCommand(plugin), "View staff action history", List.of("staffhist"));
        registerPaperCommand(commandMap, "warnings", new WarningsCommand(plugin), "View active warnings", List.of());
        registerPaperCommand(commandMap, "banlist", new BanListCommand(plugin), "View list of bans", List.of());
        registerPaperCommand(commandMap, "mutelist", new MuteListCommand(plugin), "View list of mutes", List.of());
        registerPaperCommand(commandMap, "warnlist", new WarnListCommand(plugin), "View list of warnings", List.of());
        registerPaperCommand(commandMap, "viewpunishment", new ViewPunishmentCommand(plugin), "View punishment details by case ID", List.of("vp", "case", "punishinfo"));

        registerPaperCommand(commandMap, "dupeip", new DupeIPCommand(plugin), "Checks for alt accounts", List.of("alts", "checkalts"));
        registerPaperCommand(commandMap, "iphistory", new IPHistoryCommand(plugin), "View IP history", List.of());
        registerPaperCommand(commandMap, "ipreport", new IPReportCommand(plugin), "Show duplicate IPs", List.of());
        registerPaperCommand(commandMap, "geoip", new GeoIPCommand(plugin), "Show player's country", List.of());
        registerPaperCommand(commandMap, "lastuuid", new LastUUIDCommand(plugin), "Show player's UUID", List.of());
        registerPaperCommand(commandMap, "namehistory", new NameHistoryCommand(plugin), "Show player's name history", List.of());
        registerPaperCommand(commandMap, "automodhistory", new AutomodHistoryCommand(plugin), "View player's automod triggers", List.of("amhistory", "automodlog"));
        registerPaperCommand(commandMap, "nickhistory", new NickHistoryCommand(plugin), "View player's nickname history", List.of("nicknamehistory", "nicklog"));

        registerPaperCommand(commandMap, "lockdown", new LockdownCommand(plugin), "Enable/disable lockdown mode", List.of());
        registerPaperCommand(commandMap, "prunehistory", new PruneHistoryCommand(plugin), "Prune old punishments", List.of());
        registerPaperCommand(commandMap, "staffrollback", new StaffRollbackCommand(plugin), "Rollback staff actions", List.of());

        registerPaperCommand(commandMap, "clearwarnings", new ClearWarningsCommand(plugin), "Clear player warnings", List.of());
        registerPaperCommand(commandMap, "kickall", new KickAllCommand(plugin), "Kick all players", List.of());
        registerPaperCommand(commandMap, "punish", new PunishCommand(plugin), "Open punishment GUI", List.of());
        registerPaperCommand(commandMap, "modlog", new ModLogCommand(plugin), "View moderation log", List.of());
        registerPaperCommand(commandMap, "staffchat", new StaffChatCommand(plugin), "Staff chat", List.of("sc"));
        registerPaperCommand(commandMap, "watchlist", new WatchlistCommand(plugin), "Manage watchlist", List.of("wl"));
        registerPaperCommand(commandMap, "staffhelp", new StaffHelpCommand(plugin), "Request staff help", List.of());
        registerPaperCommand(commandMap, "staffmode", new StaffModeCommand(plugin), "Toggle staff mode", List.of("sm"));
        registerPaperCommand(commandMap, "vanish", new VanishCommand(plugin), "Toggle vanish", List.of("v"));
        registerPaperCommand(commandMap, "fly", new FlyCommand(plugin), "Toggle flight mode", List.of("flight"));
        registerPaperCommand(commandMap, "broadcast", new BroadcastCommand(plugin), "Broadcast a message", List.of("bc", "announce"));
        registerPaperCommand(commandMap, "invsee", new InvseeCommand(plugin), "View player inventory", List.of("openinv"));
        registerPaperCommand(commandMap, "echest", new EchestCommand(plugin), "View player ender chest", List.of("enderchest", "endersee"));
        registerPaperCommand(commandMap, "cmdblacklist", new CmdBlacklistCommand(plugin), "Blacklist command", List.of());
        registerPaperCommand(commandMap, "cmdunblacklist", new CmdUnblacklistCommand(plugin), "Remove command blacklist", List.of());
        registerPaperCommand(commandMap, "cmdhistory", new CmdHistoryCommand(plugin), "View command history", List.of());
        registerPaperCommand(commandMap, "log", new LogCommand(plugin), "View player activity log", List.of("activitylog", "playerlog"));
        registerPaperCommand(commandMap, "seen", new SeenCommand(plugin), "View player last seen info", List.of("playerinfo", "lastseen"));
        registerPaperCommand(commandMap, "disguise", new DisguiseCommand(plugin), "Disguise as another player", List.of("d"));
        registerPaperCommand(commandMap, "undisguise", new UndisguiseCommand(plugin), "Remove disguise", List.of("ud"));
        registerPaperCommand(commandMap, "disguisename", new DisguiseNameCommand(plugin), "Change disguise name", List.of("dname"));
        registerPaperCommand(commandMap, "disguiseskin", new DisguiseSkinCommand(plugin), "Change disguise skin", List.of("dskin"));
        registerPaperCommand(commandMap, "rules", new RulesCommand(plugin), "View server rules", List.of("serverrules"));

        registerPaperCommand(commandMap, "replay", new ReplayCommand(plugin), "Replay system commands", List.of("replays"));

        registerPaperCommand(commandMap, "mban", new BanCommand(plugin), "Ban a player", List.of());
        registerPaperCommand(commandMap, "munban", new UnbanCommand(plugin), "Unban a player", List.of());
        registerPaperCommand(commandMap, "mmute", new MuteCommand(plugin), "Mute a player", List.of());
        registerPaperCommand(commandMap, "munmute", new UnmuteCommand(plugin), "Unmute a player", List.of());
        registerPaperCommand(commandMap, "mwarn", new WarnCommand(plugin), "Warn a player", List.of());
        registerPaperCommand(commandMap, "munwarn", new UnwarnCommand(plugin), "Remove a warning", List.of());
        registerPaperCommand(commandMap, "mkick", new KickCommand(plugin), "Kick a player", List.of());

        // Mass punishment commands
        registerPaperCommand(commandMap, "masswarn", new MassWarnCommand(plugin), "Mass warn players", List.of());
        registerPaperCommand(commandMap, "massmute", new MassMuteCommand(plugin), "Mass mute players", List.of());
        registerPaperCommand(commandMap, "masskick", new MassKickCommand(plugin), "Mass kick players", List.of());
        registerPaperCommand(commandMap, "massban", new MassBanCommand(plugin), "Mass ban players", List.of());
        registerPaperCommand(commandMap, "massunwarn", new MassUnwarnCommand(plugin), "Mass unwarn by batch ID", List.of());
        registerPaperCommand(commandMap, "massunmute", new MassUnmuteCommand(plugin), "Mass unmute by batch ID", List.of());
        registerPaperCommand(commandMap, "massunban", new MassUnbanCommand(plugin), "Mass unban by batch ID", List.of());

        // Internal commands (for click handlers)
        registerPaperCommand(commandMap, "mx:evidence", new EvidenceCommand(plugin), "Internal evidence selection", List.of());
    }

    private void registerPaperCommand(CommandMap commandMap, String name, BaseCommand executor, String description, List<String> aliases) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(name, plugin);

            command.setDescription(description);
            command.setAliases(aliases);
            command.setExecutor(executor);
            command.setTabCompleter(executor);

            commandMap.register(plugin.getName().toLowerCase(), command);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register command: " + name);
            e.printStackTrace();
        }
    }

    private void registerSpigotCommands() {
        registerSpigotCommand("moderex", new MxCommand(plugin));

        registerSpigotCommand("ban", new BanCommand(plugin));
        registerSpigotCommand("mute", new MuteCommand(plugin));
        registerSpigotCommand("warn", new WarnCommand(plugin));
        registerSpigotCommand("kick", new KickCommand(plugin));
        registerSpigotCommand("tempban", new TempBanCommand(plugin));
        registerSpigotCommand("tempmute", new TempMuteCommand(plugin));
        registerSpigotCommand("ipban", new IPBanCommand(plugin));
        registerSpigotCommand("ipmute", new IPMuteCommand(plugin));

        registerSpigotCommand("unban", new UnbanCommand(plugin));
        registerSpigotCommand("unmute", new UnmuteCommand(plugin));
        registerSpigotCommand("unwarn", new UnwarnCommand(plugin));

        registerSpigotCommand("check", new CheckCommand(plugin));
        registerSpigotCommand("checkban", new CheckBanCommand(plugin));
        registerSpigotCommand("checkmute", new CheckMuteCommand(plugin));
        registerSpigotCommand("checkwarn", new CheckWarnCommand(plugin));

        registerSpigotCommand("history", new HistoryCommand(plugin));
        registerSpigotCommand("staffhistory", new StaffHistoryCommand(plugin));
        registerSpigotCommand("warnings", new WarningsCommand(plugin));
        registerSpigotCommand("banlist", new BanListCommand(plugin));
        registerSpigotCommand("mutelist", new MuteListCommand(plugin));
        registerSpigotCommand("warnlist", new WarnListCommand(plugin));
        registerSpigotCommand("viewpunishment", new ViewPunishmentCommand(plugin));

        registerSpigotCommand("dupeip", new DupeIPCommand(plugin));
        registerSpigotCommand("iphistory", new IPHistoryCommand(plugin));
        registerSpigotCommand("ipreport", new IPReportCommand(plugin));
        registerSpigotCommand("geoip", new GeoIPCommand(plugin));
        registerSpigotCommand("lastuuid", new LastUUIDCommand(plugin));
        registerSpigotCommand("namehistory", new NameHistoryCommand(plugin));
        registerSpigotCommand("automodhistory", new AutomodHistoryCommand(plugin));
        registerSpigotCommand("nickhistory", new NickHistoryCommand(plugin));

        registerSpigotCommand("lockdown", new LockdownCommand(plugin));
        registerSpigotCommand("prunehistory", new PruneHistoryCommand(plugin));
        registerSpigotCommand("staffrollback", new StaffRollbackCommand(plugin));

        registerSpigotCommand("clearwarnings", new ClearWarningsCommand(plugin));
        registerSpigotCommand("kickall", new KickAllCommand(plugin));
        registerSpigotCommand("punish", new PunishCommand(plugin));
        registerSpigotCommand("modlog", new ModLogCommand(plugin));
        registerSpigotCommand("staffchat", new StaffChatCommand(plugin));
        registerSpigotCommand("watchlist", new WatchlistCommand(plugin));
        registerSpigotCommand("staffhelp", new StaffHelpCommand(plugin));
        registerSpigotCommand("staffmode", new StaffModeCommand(plugin));
        registerSpigotCommand("vanish", new VanishCommand(plugin));
        registerSpigotCommand("cmdblacklist", new CmdBlacklistCommand(plugin));
        registerSpigotCommand("cmdunblacklist", new CmdUnblacklistCommand(plugin));
        registerSpigotCommand("cmdhistory", new CmdHistoryCommand(plugin));
        registerSpigotCommand("log", new LogCommand(plugin));
        registerSpigotCommand("seen", new SeenCommand(plugin));
        registerSpigotCommand("disguise", new DisguiseCommand(plugin));
        registerSpigotCommand("undisguise", new UndisguiseCommand(plugin));
        registerSpigotCommand("disguisename", new DisguiseNameCommand(plugin));
        registerSpigotCommand("disguiseskin", new DisguiseSkinCommand(plugin));
        registerSpigotCommand("rules", new RulesCommand(plugin));

        registerSpigotCommand("replay", new ReplayCommand(plugin));

        registerSpigotCommand("mban", new BanCommand(plugin));
        registerSpigotCommand("munban", new UnbanCommand(plugin));
        registerSpigotCommand("mmute", new MuteCommand(plugin));
        registerSpigotCommand("munmute", new UnmuteCommand(plugin));
        registerSpigotCommand("mwarn", new WarnCommand(plugin));
        registerSpigotCommand("munwarn", new UnwarnCommand(plugin));
        registerSpigotCommand("mkick", new KickCommand(plugin));

        // Mass punishment commands
        registerSpigotCommand("masswarn", new MassWarnCommand(plugin));
        registerSpigotCommand("massmute", new MassMuteCommand(plugin));
        registerSpigotCommand("masskick", new MassKickCommand(plugin));
        registerSpigotCommand("massban", new MassBanCommand(plugin));
        registerSpigotCommand("massunwarn", new MassUnwarnCommand(plugin));
        registerSpigotCommand("massunmute", new MassUnmuteCommand(plugin));
        registerSpigotCommand("massunban", new MassUnbanCommand(plugin));

        // Internal commands - need to register via command map for Spigot too
        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            registerPaperCommand(commandMap, "mx:evidence", new EvidenceCommand(plugin), "Internal evidence selection", List.of());
        }
    }

    private void registerSpigotCommand(String name, BaseCommand executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            plugin.getLogger().warning("Could not register command: " + name);
        }
    }

    private CommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
