package zoruafan.foxanticheat.vls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.FoliaCompat;

public class CommandExecutor implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   FoxAdditionAPI api = FoxAddition.getAPI();
   private Map<Player, Set<String>> EC_BP = new HashMap();
   private Map<Player, Set<String>> EC_BL = new HashMap();
   private Map<Player, Set<String>> EC_FB = new HashMap();
   private Map<Player, Set<String>> EC_PH = new HashMap();
   private Map<Player, Set<String>> EC_RH = new HashMap();

   public CommandExecutor(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
   }

   public void executeCommands(Player player, List<String> commands) {
      if (player.isOnline()) {
         Iterator var4 = commands.iterator();

         while(var4.hasNext()) {
            String originalCommand = (String)var4.next();
            String command = originalCommand;
            if (player != null) {
               Plugin placeholderAPI = this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
               if (placeholderAPI != null && placeholderAPI.isEnabled()) {
                  command = this.applyPlaceholderAPI(player, originalCommand);
               }
            }

            command = command.replace("{player}", player.getName());
            FoliaCompat.runTask(this.plugin, (FA) -> {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            });
         }

         commands.clear();
      }
   }

   public List<String> loadVLSCommands(Player player, String configSection, int vls) {
      List<String> commands = new ArrayList();
      String configValue = configSection.replace(".commands", "");
      Set<String> executedCommands = this.getExecutedCommands(player, configValue);
      ConfigurationSection vlsCommandsConfig = this.file.getChecks().getConfigurationSection(configSection);
      if (vlsCommandsConfig != null) {
         Iterator var9 = vlsCommandsConfig.getKeys(false).iterator();

         while(true) {
            String key;
            int threshold;
            String commandKey;
            do {
               do {
                  if (!var9.hasNext()) {
                     return commands;
                  }

                  key = (String)var9.next();
                  threshold = Integer.parseInt(key);
                  commandKey = configSection + "-" + threshold;
               } while(vls < threshold);
            } while(executedCommands.contains(commandKey));

            Object value = vlsCommandsConfig.get(key);
            if (value instanceof String) {
               commands.add((String)value);
            } else if (value instanceof List) {
               List<?> listValue = (List)value;
               Iterator var15 = listValue.iterator();

               while(var15.hasNext()) {
                  Object item = var15.next();
                  if (item instanceof String) {
                     commands.add((String)item);
                  }
               }
            }

            executedCommands.add(commandKey);
            if ("badpackets".equals(configValue)) {
               this.EC_BP.put(player, executedCommands);
            } else if ("blocks".equals(configValue)) {
               this.EC_BL.put(player, executedCommands);
            } else if ("fastbow".equals(configValue)) {
               this.EC_FB.put(player, executedCommands);
            } else if ("phase".equals(configValue)) {
               this.EC_PH.put(player, executedCommands);
            } else if ("reach".equals(configValue)) {
               this.EC_RH.put(player, executedCommands);
            }
         }
      } else {
         return commands;
      }
   }

   private Set<String> getExecutedCommands(Player player, String configSection) {
      Map<Player, Set<String>> targetMap = this.getCommands(configSection);
      return targetMap != null ? (Set)targetMap.computeIfAbsent(player, (k) -> {
         return new HashSet();
      }) : Collections.emptySet();
   }

   public Map<Player, Set<String>> getCommands(String configValue) {
      if ("badpackets".equals(configValue)) {
         return this.EC_BP;
      } else if ("blocks".equals(configValue)) {
         return this.EC_BL;
      } else if ("fastbow".equals(configValue)) {
         return this.EC_FB;
      } else if ("phase".equals(configValue)) {
         return this.EC_PH;
      } else {
         return "reach".equals(configValue) ? this.EC_RH : Collections.emptyMap();
      }
   }

   public String applyPlaceholderAPI(Player player, String input) {
      try {
         Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
         Method setPlaceholdersMethod = placeholderAPI.getMethod("setPlaceholders", Player.class, String.class);
         return (String)setPlaceholdersMethod.invoke((Object)null, player, input);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException | ClassNotFoundException var5) {
         return input;
      }
   }
}
