package zoruafan.foxanticheat.api.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.LogManager;
import zoruafan.foxanticheat.manager.hooks.DiscordSRVManager;
import zoruafan.foxanticheat.vls.bl;
import zoruafan.foxanticheat.vls.bp;
import zoruafan.foxanticheat.vls.fb;
import zoruafan.foxanticheat.vls.ph;
import zoruafan.foxanticheat.vls.rh;

public class FoxFlagEventListener implements Listener {
   private final DiscordSRVManager discordSRV;
   private final FilesManager files;
   private final boolean useDiscordSRV;
   private final bp bp;
   private final bl bl;
   private final fb fb;
   private final ph ph;
   private final rh rh;
   private final LogManager logManager;

   public FoxFlagEventListener(JavaPlugin plugin, FilesManager files, LogManager logManager, boolean useDiscordSRV, bp bp, bl bl, fb fb, ph ph, rh rh) {
      this.useDiscordSRV = useDiscordSRV;
      this.files = files;
      this.bp = bp;
      this.bl = bl;
      this.fb = fb;
      this.ph = ph;
      this.rh = rh;
      this.logManager = logManager;
      if (useDiscordSRV) {
         this.discordSRV = new DiscordSRVManager(plugin, 1000L, files);
      } else {
         this.discordSRV = null;
      }

   }

   @EventHandler
   public void onFoxFlagEvent(FoxFlagEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         String checkType = event.getCheckType();
         String details = event.getDetails();
         String module = event.getModule();
         int vls = event.getVLS();
         int vls_added = vls;
         String log = event.getLog();
         FoxAdditionAPI api = FoxAddition.getAPI();
         this.logManager.log("[ALERT] " + player.getName() + " detected " + module + " " + log + " [vls:" + vls + "(+" + vls + ")]");
         if (checkType.equals("misc")) {
            api.verboseNotify(this.files.MessageNormal("command.verbose.format", player).replace("{player}", String.valueOf(player.getName())).replace("{vls}", "0").replace("{vls_added}", "0").replace("{module}", module).replace("info", details).replace("{details}", log.replaceAll(", ", "§8, §7").replace("[", "").replace("] [", "§8, §7").replace("]", "§7").replaceAll(":", ":§b").replaceAll("/", "§8/§a")));
         } else {
            api.addVLS(player, checkType, vls);
            vls = api.getVLS(player, checkType);
            if (this.useDiscordSRV) {
               this.discordSRV.sendMessageToDiscord(module, player, vls, details);
            }

            if (checkType.equals("badpackets")) {
               this.bp.flag(player, vls);
            } else if (checkType.equals("blocks")) {
               this.bl.flag(player, vls);
            } else if (checkType.equals("fastbow")) {
               this.fb.flag(player, vls);
            } else if (checkType.equals("phase")) {
               this.ph.flag(player, vls);
            } else if (checkType.equals("reach")) {
               this.rh.flag(player, vls);
            }

            api.verboseNotify(this.files.MessageNormal("command.verbose.format", player).replace("{player}", String.valueOf(player.getName())).replace("{vls}", String.valueOf(vls)).replace("{vls_added}", String.valueOf(vls_added)).replace("{module}", module).replace("info", details).replace("{details}", log.replaceAll(", ", "§8, §7").replace("[", "").replace("] [", "§8, §7").replace("]", "§7").replaceAll(":", ":§b").replaceAll("/", "§8/§a")));
         }
      }
   }
}
