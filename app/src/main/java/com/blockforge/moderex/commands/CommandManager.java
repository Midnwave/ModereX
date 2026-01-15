package com.blockforge.moderex.commands;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.admin.MxCommand;
import com.blockforge.moderex.commands.moderation.*;
import com.blockforge.moderex.commands.utility.*;
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
        registerPaperCommand(commandMap, "mute", new MuteCommand(plugin), "Mute a player", List.of());
        registerPaperCommand(commandMap, "unmute", new UnmuteCommand(plugin), "Unmute a player", List.of());
        registerPaperCommand(commandMap, "ban", new BanCommand(plugin), "Ban a player", List.of());
        registerPaperCommand(commandMap, "unban", new UnbanCommand(plugin), "Unban a player", List.of());
        registerPaperCommand(commandMap, "ipban", new IPBanCommand(plugin), "IP ban a player", List.of());
        registerPaperCommand(commandMap, "kick", new KickCommand(plugin), "Kick a player", List.of());
        registerPaperCommand(commandMap, "kickall", new KickAllCommand(plugin), "Kick all players", List.of());
        registerPaperCommand(commandMap, "warn", new WarnCommand(plugin), "Warn a player", List.of());
        registerPaperCommand(commandMap, "clearwarnings", new ClearWarningsCommand(plugin), "Clear player warnings", List.of());
        registerPaperCommand(commandMap, "punish", new PunishCommand(plugin), "Open punishment GUI", List.of());
        registerPaperCommand(commandMap, "modlog", new ModLogCommand(plugin), "View moderation log", List.of());
        registerPaperCommand(commandMap, "staffchat", new StaffChatCommand(plugin), "Staff chat", List.of("sc"));
        registerPaperCommand(commandMap, "staffhelp", new StaffHelpCommand(plugin), "Request staff help", List.of());
        registerPaperCommand(commandMap, "vanish", new VanishCommand(plugin), "Toggle vanish", List.of("v"));
        registerPaperCommand(commandMap, "cmdblacklist", new CmdBlacklistCommand(plugin), "Blacklist command", List.of());
        registerPaperCommand(commandMap, "cmdunblacklist", new CmdUnblacklistCommand(plugin), "Remove command blacklist", List.of());
        registerPaperCommand(commandMap, "cmdhistory", new CmdHistoryCommand(plugin), "View command history", List.of());
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

        registerSpigotCommand("mute", new MuteCommand(plugin));
        registerSpigotCommand("unmute", new UnmuteCommand(plugin));
        registerSpigotCommand("ban", new BanCommand(plugin));
        registerSpigotCommand("unban", new UnbanCommand(plugin));
        registerSpigotCommand("ipban", new IPBanCommand(plugin));
        registerSpigotCommand("kick", new KickCommand(plugin));
        registerSpigotCommand("kickall", new KickAllCommand(plugin));
        registerSpigotCommand("warn", new WarnCommand(plugin));
        registerSpigotCommand("clearwarnings", new ClearWarningsCommand(plugin));
        registerSpigotCommand("punish", new PunishCommand(plugin));
        registerSpigotCommand("modlog", new ModLogCommand(plugin));

        registerSpigotCommand("staffchat", new StaffChatCommand(plugin));
        registerSpigotCommand("staffhelp", new StaffHelpCommand(plugin));
        registerSpigotCommand("vanish", new VanishCommand(plugin));
        registerSpigotCommand("cmdblacklist", new CmdBlacklistCommand(plugin));
        registerSpigotCommand("cmdunblacklist", new CmdUnblacklistCommand(plugin));
        registerSpigotCommand("cmdhistory", new CmdHistoryCommand(plugin));
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
