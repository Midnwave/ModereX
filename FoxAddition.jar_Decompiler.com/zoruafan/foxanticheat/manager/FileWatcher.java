package zoruafan.foxanticheat.manager;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import org.bukkit.plugin.java.JavaPlugin;

public class FileWatcher implements Runnable {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private long lastChangeTime = 0L;
   private final long cooldownTime = 500L;

   public FileWatcher(JavaPlugin plugin, FilesManager files) {
      this.plugin = plugin;
      this.file = files;
   }

   public void run() {
      try {
         WatchService watchService = FileSystems.getDefault().newWatchService();
         Path pluginFolder = this.plugin.getDataFolder().toPath();
         Path configPath = pluginFolder.resolve("config.yml");
         Path checksPath = pluginFolder.resolve("checks.yml");
         Path languagePath = pluginFolder.resolve("language.yml");
         configPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
         checksPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
         languagePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

         WatchKey key;
         for(; (key = watchService.take()) != null; key.reset()) {
            Iterator var8 = key.pollEvents().iterator();
            if (var8.hasNext()) {
               WatchEvent<?> event = (WatchEvent)var8.next();
               Path changedFile = (Path)event.context();
               String fileName = changedFile.toString();
               if (!fileName.equals("config.yml") && !fileName.equals("checks.yml") && !fileName.equals("language.yml")) {
                  return;
               }

               long currentTime = System.currentTimeMillis();
               if (currentTime - this.lastChangeTime >= 500L) {
                  this.plugin.getLogger().info("[FILES] Changes in " + fileName + " has been found and applied automatically.");
                  this.lastChangeTime = currentTime;
               }

               if (fileName.equals("config.yml")) {
                  this.file.reload("config");
               } else if (fileName.equals("checks.yml")) {
                  this.file.reload("checks");
               } else if (fileName.equals("language.yml")) {
                  this.file.reload("language");
               }

               key.reset();
            }
         }
      } catch (Exception var13) {
      }

   }
}
