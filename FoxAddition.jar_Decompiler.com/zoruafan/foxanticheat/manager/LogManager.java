package zoruafan.foxanticheat.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.bukkit.plugin.java.JavaPlugin;

public class LogManager {
   private JavaPlugin plugin;
   private File logDirectory;
   private DateTimeFormatter dateFormat;
   private DateTimeFormatter timestampFormatter;
   private File currentLogFile;
   private FilesManager file;
   private int cleanupDays;

   public LogManager(JavaPlugin plugin, FilesManager files, boolean enableLogging, String timezone, String timeFormat) {
      this.plugin = plugin;
      this.file = files;
      if (files.getConfig().getBoolean("logs.enable")) {
         this.logDirectory = new File(plugin.getDataFolder(), "logs");
         if (!this.logDirectory.exists()) {
            this.logDirectory.mkdirs();
         }

         this.dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         this.currentLogFile = this.getCurrentLogFile();
         TimeZone defaultTimezone = TimeZone.getDefault();
         this.timestampFormatter = DateTimeFormatter.ofPattern(timeFormat).withZone(defaultTimezone.toZoneId());
         if (enableLogging) {
            TimeZone systemTimezone;
            if (timezone.equalsIgnoreCase("auto")) {
               systemTimezone = TimeZone.getDefault();
               this.timestampFormatter = DateTimeFormatter.ofPattern(timeFormat).withZone(systemTimezone.toZoneId());
            } else {
               systemTimezone = TimeZone.getTimeZone(timezone);
               this.timestampFormatter = DateTimeFormatter.ofPattern(timeFormat).withZone(systemTimezone.toZoneId());
            }

            this.cleanupDays = this.file.getConfig().getInt("logs.cleanup.days", 7);
            if (this.file.getConfig().getBoolean("logs.cleanup.enable")) {
               plugin.getLogger().info("[LOGS] The cleanup of old logs started...");
               this.cleanOldLogFiles();
               plugin.getLogger().info("[LOGS] The cleanup of old logs finished.");
            }
         }

      }
   }

   public void log(String message) {
      if (this.file.getConfig().getBoolean("logs.enable")) {
         File logFile = this.getCurrentLogFile();

         try {
            Throwable var3 = null;
            Object var4 = null;

            try {
               FileWriter writer = new FileWriter(logFile, true);

               try {
                  String timestamp = this.timestampFormatter.format(LocalDateTime.now());
                  writer.write("[" + timestamp + "] " + message + "\n");
               } finally {
                  if (writer != null) {
                     writer.close();
                  }

               }
            } catch (Throwable var14) {
               if (var3 == null) {
                  var3 = var14;
               } else if (var3 != var14) {
                  var3.addSuppressed(var14);
               }

               throw var3;
            }
         } catch (IOException var15) {
         }

      }
   }

   private File getCurrentLogFile() {
      String currentDateString = this.dateFormat.format(LocalDateTime.now());
      File logFile = new File(this.logDirectory, String.valueOf(currentDateString) + ".txt");
      if (!logFile.exists()) {
         try {
            logFile.createNewFile();
         } catch (IOException var4) {
         }
      }

      return logFile;
   }

   private boolean isLogFile(Path path) {
      return Files.isRegularFile(path, new LinkOption[0]) && path.getFileName().toString().endsWith(".txt");
   }

   private void cleanOldLogFiles() {
      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            Stream logFiles = Files.list(this.logDirectory.toPath());

            try {
               logFiles.filter(this::isLogFile).forEach(this::deleteOldLogFile);
            } finally {
               if (logFiles != null) {
                  logFiles.close();
               }

            }
         } catch (Throwable var11) {
            if (var1 == null) {
               var1 = var11;
            } else if (var1 != var11) {
               var1.addSuppressed(var11);
            }

            throw var1;
         }
      } catch (IOException var12) {
      }

   }

   private void deleteOldLogFile(Path path) {
      try {
         BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
         FileTime creationTime = attributes.creationTime();
         LocalDateTime logFileCreationTime = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
         if (logFileCreationTime.isBefore(LocalDateTime.now().minusDays((long)this.cleanupDays))) {
            Files.delete(path);
         }
      } catch (IOException var5) {
      }

   }
}
