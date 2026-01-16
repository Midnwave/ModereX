package zoruafan.foxanticheat.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;

public class FoxAdditionAPIImpl implements FoxAdditionAPI {
   private Map<Player, Map<String, Integer>> fVLS = new HashMap();
   private List<CommandSender> verboseL = new ArrayList();
   private static final Set<String> checkTypes = new HashSet(Arrays.asList("badpackets", "blocks", "fastbow", "misc", "phase", "reach"));

   public void flag(Player player, String checkType, int vls, String details, String module, String log) {
      if (!checkTypes.contains(checkType)) {
         Bukkit.getLogger().severe("[FA] [API] Value for checkType '" + checkType + "' is invalid. Event ignored.");
      } else {
         FoxFlagEvent foxFlagEvent = new FoxFlagEvent(player, checkType, vls, details, module, log);
         Bukkit.getServer().getPluginManager().callEvent(foxFlagEvent);
      }
   }

   public int getVLS(Player player, String checkType) {
      if (!checkTypes.contains(checkType)) {
         Bukkit.getLogger().severe("[FA] [API] Value for checkType '" + checkType + "' is invalid. Event ignored.");
         return 0;
      } else {
         Map<String, Integer> playerVLS = (Map)this.fVLS.get(player);
         return playerVLS == null ? 0 : (Integer)playerVLS.getOrDefault(checkType, 0);
      }
   }

   public void addVLS(Player player, String checkType, int vls) {
      if (!checkTypes.contains(checkType)) {
         Bukkit.getLogger().severe("[FA] [API] Value for checkType '" + checkType + "' is invalid. Event ignored.");
      } else {
         Map<String, Integer> playerVLS = (Map)this.fVLS.computeIfAbsent(player, (k) -> {
            return new HashMap();
         });
         playerVLS.put(checkType, (Integer)playerVLS.getOrDefault(checkType, 0) + vls);
      }
   }

   public void setVLS(Player player, String checkType, int vls) {
      if (!checkTypes.contains(checkType)) {
         Bukkit.getLogger().severe("[FA] [API] Value for checkType '" + checkType + "' is invalid. Event ignored.");
      } else {
         Map<String, Integer> playerVLS = (Map)this.fVLS.computeIfAbsent(player, (k) -> {
            return new HashMap();
         });
         playerVLS.put(checkType, Math.max(0, vls));
      }
   }

   public boolean getVerbose(CommandSender sender) {
      return this.verboseL.contains(sender);
   }

   public void toggleVerbose(CommandSender sender) {
      if (this.getVerbose(sender)) {
         this.verboseL.remove(sender);
      } else {
         this.verboseL.add(sender);
      }

   }

   public void verboseNotify(String message) {
      Iterator var3 = this.verboseL.iterator();

      while(var3.hasNext()) {
         CommandSender sender = (CommandSender)var3.next();
         sender.sendMessage(message);
      }

   }
}
