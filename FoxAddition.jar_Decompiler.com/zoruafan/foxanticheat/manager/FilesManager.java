package zoruafan.foxanticheat.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FilesManager {
   protected final JavaPlugin plugin;
   private FileConfiguration config;
   private FileConfiguration lang;
   private FileConfiguration checks;
   public boolean isWatchRunning = false;
   public Thread watcherThread;

   public FilesManager(JavaPlugin plugin) {
      this.plugin = plugin;
      this.setup("config");
      this.setup("checks");
      this.setup("language");
      this.setupPhase();
      this.watcherThread = new Thread(new FileWatcher(plugin, this));
   }

   public void setup(String file) {
      if (!this.plugin.getDataFolder().exists()) {
         this.plugin.getDataFolder().mkdir();
      }

      File filec = new File(this.plugin.getDataFolder(), file + ".yml");
      if (!filec.exists()) {
         try {
            InputStream inputStream = this.plugin.getResource(file + ".yml");
            if (inputStream != null) {
               Files.copy(inputStream, filec.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
               inputStream.close();
            }
         } catch (IOException var4) {
            this.plugin.getLogger().warning("[FILES] Failed to get " + file + ".yml file");
         }
      }

      if (file.equals("config")) {
         this.config = YamlConfiguration.loadConfiguration(filec);
      } else if (file.equals("language")) {
         this.lang = YamlConfiguration.loadConfiguration(filec);
      } else if (file.equals("checks")) {
         this.checks = YamlConfiguration.loadConfiguration(filec);
      }

   }

   public boolean exists(String file) {
      File configFile = new File(this.plugin.getDataFolder(), file + ".yml");
      return configFile.exists();
   }

   public JavaPlugin plugin() {
      return this.plugin;
   }

   public void reload(String file) {
      if (!this.plugin.getDataFolder().exists()) {
         this.plugin.getDataFolder().mkdir();
      }

      File filec = new File(this.plugin.getDataFolder(), file + ".yml");
      if (!filec.exists()) {
         try {
            InputStream inputStream = this.plugin.getResource(file + ".yml");

            try {
               Files.copy(inputStream, filec.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            } catch (Exception var5) {
               Files.copy(inputStream, filec.toPath(), new CopyOption[0]);
            }

            inputStream.close();
         } catch (IOException var6) {
            this.plugin.getLogger().warning("[FILES] Failed to get " + file + ".yml file");
         }
      }

      if (file.equals("config")) {
         this.config = YamlConfiguration.loadConfiguration(filec);
      } else if (file.equals("language")) {
         this.lang = YamlConfiguration.loadConfiguration(filec);
      } else {
         if (!file.equals("checks")) {
            return;
         }

         this.checks = YamlConfiguration.loadConfiguration(filec);
      }

   }

   public void setupPhase() {
      Object[] PhaseVersions = new Object[]{"1.7", "1.8", "1.12", "1.13", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20"};
      Object[] var5 = PhaseVersions;
      int var4 = PhaseVersions.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Object obj = var5[var3];
         this.GeneratePhase(obj);
      }

   }

   private void GeneratePhase(Object version) {
      File versionsFolder = new File(this.plugin.getDataFolder(), "versions");
      if (!versionsFolder.exists()) {
         versionsFolder.mkdir();
      }

      File phaseFolder = new File(this.plugin.getDataFolder(), "versions/phase");
      if (!phaseFolder.exists()) {
         phaseFolder.mkdir();
      }

      File phaseV = new File(this.plugin.getDataFolder(), "versions/phase/" + version + ".yml");
      if (!phaseV.exists()) {
         try {
            InputStream inputStream = this.plugin.getResource("versions/phase/" + version + ".yml");
            if (inputStream != null) {
               Files.copy(inputStream, phaseV.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
               inputStream.close();
            }
         } catch (Exception var6) {
            this.plugin.getLogger().warning("[FILES] Failed to get " + version + " phase file.");
         }
      }

   }

   public FileConfiguration getChecks() {
      return this.checks;
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public FileConfiguration getLangA() {
      return this.lang;
   }

   public String getLang(String key, Player player) {
      if (this.lang == null) {
         return "";
      } else {
         String message = this.lang.getString("messages." + key, "");
         if (player != null) {
            Plugin placeholderAPI = this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
            if (placeholderAPI != null && placeholderAPI.isEnabled()) {
               message = this.applyPlaceholderAPI(player, message);
            }
         }

         message = ChatColor.translateAlternateColorCodes('&', message).replace("\\n", "\n").replace("%nl%", "\n").replace("\\nl", "\n");
         return message;
      }
   }

   private String applyPlaceholderAPI(Player player, String input) {
      try {
         Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
         Method setPlaceholdersMethod = placeholderAPI.getMethod("setPlaceholders", Player.class, String.class);
         return (String)setPlaceholdersMethod.invoke((Object)null, player, input);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException | ClassNotFoundException var5) {
         return input;
      }
   }

   public String getPrefix() {
      if (this.lang == null) {
         return "";
      } else {
         try {
            return ChatColor.translateAlternateColorCodes('&', this.lang.getString("prefix"));
         } catch (Exception var2) {
            Bukkit.getLogger().severe("[FILES] Hey! The language.yml is broken, please, fix it or delete and restart.");
            return "FAILED!";
         }
      }
   }

   public String MessageNormal(String text, Player player) {
      String message = this.getLang(text, player);
      String prefix = this.getPrefix();
      String sending = message.replace("{prefix}", prefix);
      if (player != null) {
         Plugin placeholderAPI = this.plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
         if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            this.applyPlaceholderAPI(player, message);
         }
      }

      message = ChatColor.translateAlternateColorCodes('&', sending).replace("\\n", "\n").replace("%nl%", "\n").replace("\\nl", "\n");
      return message;
   }

   public void loadFileWatcher() {
      if (!this.isWatchRunning) {
         if (this.getConfig().getBoolean("updates.filewatcher")) {
            try {
               this.isWatchRunning = true;
               if (!this.watcherThread.isAlive()) {
                  this.watcherThread.start();
                  this.plugin.getLogger().info("[FILES] FileWatcher is now enabled and ready to apply changes.");
               }
            } catch (Exception var2) {
               Bukkit.getLogger().severe("[FILES] Hey! Failed to resume FileWatcher: " + var2);
            }
         }

      }
   }

   public void stopFileWatcher() {
      if (this.watcherThread != null) {
         try {
            this.watcherThread.interrupt();
         } catch (Exception var3) {
         }

         try {
            this.watcherThread.join();
            this.plugin.getLogger().info("[FILES] FileWatcher is now enabled and ready to apply changes.");
         } catch (Exception var2) {
         }

         this.isWatchRunning = false;
      }

   }
}
