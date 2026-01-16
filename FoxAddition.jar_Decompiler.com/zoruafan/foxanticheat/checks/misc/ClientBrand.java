package zoruafan.foxanticheat.checks.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.FoliaCompat;

public class ClientBrand implements Listener, PluginMessageListener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   String brand = "vanilla";
   boolean hasBrand = false;

   public ClientBrand(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
      this.registerPluginChannelListener();
   }

   private void registerPluginChannelListener() {
      try {
         Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "minecraft:brand", this);
      } catch (Exception var3) {
      }

      try {
         Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "MC|Brand", this);
      } catch (Exception var2) {
      }

   }

   public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (this.file.getConfig().getBoolean("brandchecker.enable", true)) {
         if ("minecraft:brand".equals(channel) || "MC|Brand".equals(channel)) {
            String originalMessage = (new String(message, StandardCharsets.UTF_8)).replace(" (Velocity)", "");
            this.brand = originalMessage.trim();
            if (!this.isAllowed(this.brand)) {
               List<String> commands = this.file.getConfig().getStringList("brandchecker.commands");

               String cmd_1;
               for(Iterator var7 = commands.iterator(); var7.hasNext(); FoliaCompat.runTask(this.plugin, (FA) -> {
                  this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), cmd_1);
               })) {
                  String command = (String)var7.next();
                  cmd_1 = command.replace("{player}", player.getName()).replace("{brand}", this.brand);
                  if (player != null) {
                     Plugin placeholderAPI = this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
                     if (placeholderAPI != null && placeholderAPI.isEnabled()) {
                        cmd_1 = this.applyPlaceholderAPI(player, cmd_1);
                     }
                  }
               }
            }
         }

      }
   }

   private boolean isAllowed(String brand) {
      List<String> list = this.file.getConfig().getStringList("brandchecker.list");
      String type = this.file.getConfig().getString("brandchecker.type", "whitelist");
      if (type.equalsIgnoreCase("whitelist")) {
         return list.contains(brand.toLowerCase());
      } else if (type.equalsIgnoreCase("blacklist")) {
         return !list.contains(brand.toLowerCase());
      } else {
         this.plugin.getLogger().severe("[BRANDCHECKER] Invalid option in 'type'. Use 'whitelist'/'blacklist' for this option.");
         this.plugin.getLogger().severe("[BRANDCHECKER] The brand checker don't works correctly.");
         return true;
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
