package zoruafan.foxanticheat.manager;

import java.util.Iterator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.checks.phase.PhaseData;
import zoruafan.foxanticheat.manager.hooks.DiscordSRVManager;

public class CommandManager extends ChecksManager implements CommandExecutor {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private final String version;
   private final PhaseData pd;
   private boolean useDiscordSRV;
   FoxAdditionAPI api = FoxAddition.getAPI();

   public CommandManager(JavaPlugin plugin, FilesManager files, String version, boolean useDiscordSRV, PhaseData pd) {
      super(files);
      this.plugin = plugin;
      this.pd = pd;
      this.file = files;
      this.version = version;
      this.useDiscordSRV = useDiscordSRV;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      int length = args.length;
      String message;
      if (!sender.hasPermission("foxac.command")) {
         message = "/" + label + " " + String.join(" ", args);
         Bukkit.dispatchCommand(sender, message);
         return true;
      } else {
         if (length >= 1 && !args[0].equalsIgnoreCase("help")) {
            if (args[0].equalsIgnoreCase("reload")) {
               this.file.reload("checks");
               this.file.reload("config");
               this.file.reload("language");
               this.pd.resetEBL();
               message = sender instanceof Player ? this.file.MessageNormal("command.reload.message", (Player)sender) : this.file.MessageNormal("command.reload.message", (Player)null);
               sender.sendMessage(message);
               return true;
            }

            String message;
            if (args[0].equalsIgnoreCase("notify")) {
               if (length < 2) {
                  message = sender instanceof Player ? this.file.MessageNormal("command.notify.noargs", (Player)sender) : this.file.MessageNormal("command.notify.noargs", (Player)null);
                  sender.sendMessage(message);
                  return true;
               }

               message = "{prefix} " + String.join(" ", args).substring(7);
               message = message.replace("{text}", message);
               String prefix = this.file.getPrefix();
               String message2 = message.replace("{prefix}", prefix);
               String colorMessage = ChatColor.translateAlternateColorCodes('&', message2);
               if (this.file.getConfig().getBoolean("notifies.console", true)) {
                  Bukkit.getLogger().info(ChatColor.stripColor(colorMessage));
               }

               Iterator var12 = Bukkit.getOnlinePlayers().iterator();

               while(var12.hasNext()) {
                  Player notifyPlayer = (Player)var12.next();
                  if (notifyPlayer.hasPermission(this.file.getConfig().getString("notifies.permission", "foxac.notifications"))) {
                     notifyPlayer.sendMessage(colorMessage);
                  }
               }

               return true;
            }

            if (args[0].equalsIgnoreCase("discordtest")) {
               if (this.useDiscordSRV) {
                  DiscordSRVManager discordSRV = new DiscordSRVManager(this.plugin, 100L, this.file);
                  if (!(sender instanceof Player)) {
                     message = sender instanceof Player ? this.file.MessageNormal("command.discordtest.ingame", (Player)sender) : this.file.MessageNormal("command.discordtest.ingame", (Player)null);
                     sender.sendMessage(message);
                     return true;
                  }

                  discordSRV.sendMessageToDiscord("Test (DiscordSRV).", (Player)sender, 1, "This is only a test of Discord Integration in FoxAddition.");
                  message = sender instanceof Player ? this.file.MessageNormal("command.discordtest.success", (Player)sender) : this.file.MessageNormal("command.discordtest.success", (Player)null);
                  sender.sendMessage(message);
                  return true;
               }

               message = sender instanceof Player ? this.file.MessageNormal("command.discordtest.nohook", (Player)sender) : this.file.MessageNormal("command.discordtest.nohook", (Player)null);
               sender.sendMessage(message);
               return true;
            }

            if (args[0].equalsIgnoreCase("verbose")) {
               if (!this.api.getVerbose(sender)) {
                  message = sender instanceof Player ? this.file.MessageNormal("command.verbose.enable", (Player)sender) : this.file.MessageNormal("command.verbose.enable", (Player)null);
                  sender.sendMessage(message);
               } else {
                  message = sender instanceof Player ? this.file.MessageNormal("command.verbose.disable", (Player)sender) : this.file.MessageNormal("command.verbose.disable", (Player)null);
                  sender.sendMessage(message);
               }

               this.api.toggleVerbose(sender);
               return true;
            }

            this.sendHelpMessage(sender);
         } else {
            this.sendHelpMessage(sender);
         }

         return true;
      }
   }

   private void sendHelpMessage(CommandSender sender) {
      sender.sendMessage(" ");
      sender.sendMessage(" §e§lFoxAddition §8[v" + this.version + "]");
      sender.sendMessage(" §bwww.spigotmc.org/resources/111260/");
      sender.sendMessage(" ");
      if (sender instanceof Player) {
         String[] p1 = new String[]{"§7/foxaddition §rreload", "§7/foxaddition §rnotify", "§7/foxaddition §rdiscordtest", "§7/foxaddition §rverbose"};
         String[] p2 = new String[]{this.file.MessageNormal("help.reload", (Player)sender), this.file.MessageNormal("help.notify", (Player)sender), this.file.MessageNormal("help.discordtest", (Player)sender), this.file.MessageNormal("help.verbose", (Player)sender)};
         if (p1.length == p2.length) {
            for(int i = 0; i < p1.length; ++i) {
               String command = p1[i];
               String hoverText = p2[i];
               String clickableText = " §8▪ §r" + command;
               ComponentBuilder message = new ComponentBuilder(clickableText);
               message.event(new ClickEvent(Action.SUGGEST_COMMAND, command.replaceAll("§7", "").replaceAll("§r", "")));
               message.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(hoverText)).create()));
               ((Player)sender).spigot().sendMessage(message.create());
            }
         }
      } else {
         sender.sendMessage(" §8▪ §7/foxaddition §rreload");
         sender.sendMessage(" §8▪ §7/foxaddition §rnotify");
         sender.sendMessage(" §8▪ §7/foxaddition §rdiscordtest");
         sender.sendMessage(" §8▪ §7/foxaddition §rverbose");
      }

      sender.sendMessage(" ");
   }
}
