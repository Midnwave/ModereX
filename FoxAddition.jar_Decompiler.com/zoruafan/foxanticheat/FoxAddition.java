package zoruafan.foxanticheat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.api.FoxAdditionAPIImpl;
import zoruafan.foxanticheat.api.listeners.FoxFlagEventListener;
import zoruafan.foxanticheat.checks.badpackets.FastSwitch;
import zoruafan.foxanticheat.checks.badpackets.FastUse;
import zoruafan.foxanticheat.checks.badpackets.Regen;
import zoruafan.foxanticheat.checks.badpackets.Sneak;
import zoruafan.foxanticheat.checks.badpackets.impossible.AirPlacement;
import zoruafan.foxanticheat.checks.badpackets.impossible.LiquidInteract;
import zoruafan.foxanticheat.checks.badpackets.impossible.Protocols;
import zoruafan.foxanticheat.checks.badpackets.impossible.SkinBlinker;
import zoruafan.foxanticheat.checks.blocks.FastPlace;
import zoruafan.foxanticheat.checks.blocks.nuker.Nuker;
import zoruafan.foxanticheat.checks.blocks.nuker.NukerP;
import zoruafan.foxanticheat.checks.fastbow.FBLenience;
import zoruafan.foxanticheat.checks.fastbow.FBLimit;
import zoruafan.foxanticheat.checks.misc.ClientBrand;
import zoruafan.foxanticheat.checks.misc.IllegalPitch;
import zoruafan.foxanticheat.checks.misc.exploits.BowBomb;
import zoruafan.foxanticheat.checks.misc.exploits.NullAddress;
import zoruafan.foxanticheat.checks.misc.exploits.Packets;
import zoruafan.foxanticheat.checks.phase.Phase;
import zoruafan.foxanticheat.checks.phase.PhaseData;
import zoruafan.foxanticheat.checks.reach.ReachBlock;
import zoruafan.foxanticheat.checks.reach.ReachHit;
import zoruafan.foxanticheat.manager.CommandManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.FoliaCompat;
import zoruafan.foxanticheat.manager.LogManager;
import zoruafan.foxanticheat.manager.Metrics;
import zoruafan.foxanticheat.vls.bl;
import zoruafan.foxanticheat.vls.bp;
import zoruafan.foxanticheat.vls.fb;
import zoruafan.foxanticheat.vls.ph;
import zoruafan.foxanticheat.vls.rh;

public class FoxAddition extends JavaPlugin {
   private static FoxAdditionAPI foxAdditionAPI;
   private FilesManager files;
   private LogManager logManager;
   private bp bpVLS;
   private fb fbVLS;
   private rh rhVLS;
   private ph phVLS;
   private bl blVLS;
   private PhaseData pd;
   private boolean configUpdated = false;
   private boolean useDiscordSRV = false;
   private boolean useVeinMiner = false;
   private boolean disableFoxAddition = false;
   private boolean useFloodgate = false;
   private boolean useExecutableItems = false;
   private boolean useWeaponMechanics = false;
   private boolean usemcMMO = false;

   public void onEnable() {
      this.header();
      this.validVersion();
   }

   public void onDisable() {
      this.closeFileWatcher();

      try {
         this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "minecraft:brand");
      } catch (Exception var3) {
      }

      try {
         this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "MC|Brand");
      } catch (Exception var2) {
      }

   }

   private void validVersion() {
      try {
         String bukkitVersion = Bukkit.getBukkitVersion();
         String[] versionParts = bukkitVersion.split("\\.");
         String minor = versionParts[1];
         String minor_1;
         if (minor.contains("-")) {
            String[] minorParts = minor.split("-");
            minor_1 = minorParts[0];
         } else {
            minor_1 = minor;
         }

         if (Integer.parseInt(minor_1) < 7) {
            this.disableFoxAddition = true;
         }

         if (this.disableFoxAddition) {
            try {
               this.getLogger().severe("[CHECK] You're using a old version than 1.7.");
               this.getLogger().severe("[CHECK] The plugin doesn't been tested in older versions than 1.7.");
               this.getLogger().severe("[CHECK] To avoid problems, the plugin has been disabled.");
               this.getLogger().severe("[CHECK] Update your server version for a better experience.");
            } catch (Exception var6) {
            }

            Bukkit.getPluginManager().disablePlugin(this);
            return;
         }
      } catch (Exception var7) {
         this.getLogger().severe("[CHECK] Your server don't return a version.");
         this.getLogger().severe("[CHECK] The plugin probably can't work correctly because");
         this.getLogger().severe("[CHECK] this can't check if this is supported, continue");
         this.getLogger().severe("[CHECK] with your risk!");
      }

      this.initialize();
      this.checkForUpdates();
   }

   private void initialize() {
      this.files = new FilesManager(this);
      this.files.setup("config");
      this.files.setup("language");
      this.files.setup("checks");
      if (!this.isOldConfig("config") && this.files.exists("config")) {
         this.updateConfig("config");
      }

      if (!this.isOldConfig("language") && this.files.exists("language")) {
         this.updateConfig("language");
      }

      if (!this.isOldConfig("checks") && this.files.exists("checks")) {
         this.updateConfig("checks");
      }

      if (this.configUpdated) {
         this.getLogger().warning("[UPDATER] (!) You need to move old values in the new config manually.");
         this.files.reload("config");
         this.files.reload("language");
         this.files.reload("checks");
      }

      foxAdditionAPI = new FoxAdditionAPIImpl();
      boolean logEnabled = this.files.getConfig().getBoolean("logs.enable", true);
      String logTimezone = this.files.getConfig().getString("logs.timezone", "auto");
      String logTimeFormat = this.files.getConfig().getString("logs.timeformat", "yyyy-MM-dd HH:mm:ss");
      this.logManager = new LogManager(this, this.files, logEnabled, logTimezone, logTimeFormat);
      this.bpVLS = new bp(this, this.files);
      this.fbVLS = new fb(this, this.files);
      this.rhVLS = new rh(this, this.files);
      this.phVLS = new ph(this, this.files);
      this.blVLS = new bl(this, this.files);
      this.pd = new PhaseData(this, this.files);
      if (this.isPresent("DiscordSRV")) {
         this.getLogger().info("[HOOK] DiscordSRV has been hooked.");
         this.useDiscordSRV = true;
      }

      if (this.isPresent("ExecutableItems")) {
         this.getLogger().info("[HOOK] ExecutableItems has been hooked.");
         this.useExecutableItems = true;
      }

      if (this.isPresent("floodgate")) {
         this.getLogger().info("[HOOK] Geyser (with Floodgate) has been hooked.");
         this.useFloodgate = true;
      }

      if (this.files.getConfig().getBoolean("bedrock.prefix.enable", false) || this.files.getConfig().getBoolean("bedrock.uuid.enable", true)) {
         this.getLogger().info("[HOOK] Bedrock: Adding support for bedrock players with prefix or UUID.");
         this.useFloodgate = true;
      }

      if (this.isPresent("PlaceholderAPI")) {
         this.getLogger().info("[HOOK] PlaceholderAPI has been hooked.");
      }

      if (this.isPresent("mcMMO")) {
         this.getLogger().info("[HOOK] mcMMO has been hooked.");
         this.usemcMMO = true;
      }

      if (this.isPresent("ProtocolLib")) {
         Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");
         boolean useProtocolLib = false;
         if (protocolLib != null) {
            try {
               PluginDescriptionFile description = protocolLib.getDescription();
               String version = description.getVersion();
               String[] versionParts = version.split("\\.");
               String minor = versionParts[0];
               String minor_1;
               if (minor.contains("-")) {
                  String[] minorParts = minor.split("-");
                  minor_1 = minorParts[0];
               } else {
                  minor_1 = minor;
               }

               if (Integer.parseInt(minor_1) < 5) {
                  this.getLogger().severe(" [HOOK] ");
                  this.getLogger().severe(" [HOOK] ProtocolLib has been found, but it's older than 5.X.X.");
                  this.getLogger().severe(" [HOOK] Update your version of ProtocolLib to allow hook with");
                  this.getLogger().severe(" [HOOK] this and use some features. This is used to prevent");
                  this.getLogger().severe(" [HOOK] errors in the console.");
                  this.getLogger().severe(" [HOOK] https://ci.dmulloy2.net/job/ProtocolLib//lastBuild/");
                  this.getLogger().severe(" [HOOK] ");
               } else {
                  useProtocolLib = true;
               }
            } catch (Exception var13) {
               useProtocolLib = true;
               this.getLogger().severe(" [HOOK] ");
               this.getLogger().severe(" [HOOK] ProtocolLib has been found, but isn't possible to");
               this.getLogger().severe(" [HOOK] catch the version, FoxAddition go to hook with this,");
               this.getLogger().severe(" [HOOK] but be sure you're using ProtocolLib version 5.X.X");
               this.getLogger().severe(" [HOOK] or higher for this.");
               this.getLogger().severe(" [HOOK] https://ci.dmulloy2.net/job/ProtocolLib//lastBuild/");
               this.getLogger().severe(" [HOOK] ");
            }
         }

         if (useProtocolLib) {
            this.getLogger().info("[HOOK] ProtocolLib has been hooked.");
            Object[] checks;
            Object obj;
            int var20;
            int var21;
            Object[] var23;
            if (FoliaCompat.isFolia()) {
               this.getLogger().warning(" [FILES] ");
               this.getLogger().warning(" [FILES] Nuker (Target) has been disabled automatically because this");
               this.getLogger().warning(" [FILES] has errors in Folia servers.");
               this.getLogger().warning(" [FILES] ");
               checks = new Object[]{new IllegalPitch(this, this.files), new Packets(this, this.files), new Protocols(this, this.files), new SkinBlinker(this, this.files)};
               var23 = checks;
               var21 = checks.length;

               for(var20 = 0; var20 < var21; ++var20) {
                  obj = var23[var20];
                  if (obj instanceof Listener) {
                     this.getServer().getPluginManager().registerEvents((Listener)obj, this);
                  }
               }
            } else {
               checks = new Object[]{new IllegalPitch(this, this.files), new Packets(this, this.files), new NukerP(this, this.files, this.useVeinMiner, this.useFloodgate), new Protocols(this, this.files), new SkinBlinker(this, this.files)};
               var23 = checks;
               var21 = checks.length;

               for(var20 = 0; var20 < var21; ++var20) {
                  obj = var23[var20];
                  if (obj instanceof Listener) {
                     this.getServer().getPluginManager().registerEvents((Listener)obj, this);
                  }
               }
            }
         }
      }

      if (this.isPresent("VeinMiner")) {
         this.getLogger().info("[HOOK] VeinMiner has been hooked.");
         this.useVeinMiner = true;
      }

      if (this.isPresent("WeaponMechanics")) {
         this.getLogger().info("[HOOK] WeaponMechanics has been hooked.");
         this.useWeaponMechanics = true;
      }

      Object[] checks = new Object[]{new BowBomb(this.files), new ClientBrand(this, this.files), new FBLenience(this, this.files, this.useFloodgate), new FBLimit(this.files, this.useFloodgate, this.useExecutableItems), new FastPlace(this.files, this.useFloodgate), new FastSwitch(this.files, this.useFloodgate), new FastUse(this.files, this.useFloodgate, this.usemcMMO), new LiquidInteract(this.files, this.useFloodgate), new AirPlacement(this.files, this.useFloodgate), new Nuker(this, this.files, this.useVeinMiner, this.useFloodgate, this.usemcMMO), new NullAddress(this.files), new Phase(this.files, this.useFloodgate, this.pd), new ReachBlock(this, this.files, this.useVeinMiner, this.useFloodgate, this.usemcMMO), new ReachHit(this.files, this.useExecutableItems, this.useFloodgate, this.useWeaponMechanics), new Regen(this.files, this.useFloodgate), new Sneak(this.files, this.useFloodgate), new FoxFlagEventListener(this, this.files, this.logManager, this.useDiscordSRV, this.bpVLS, this.blVLS, this.fbVLS, this.phVLS, this.rhVLS)};
      Object[] var22 = checks;
      int var19 = checks.length;

      for(int var17 = 0; var17 < var19; ++var17) {
         Object obj = var22[var17];
         if (obj instanceof Listener) {
            this.getServer().getPluginManager().registerEvents((Listener)obj, this);
         }
      }

      this.getCommand("foxaddition").setExecutor(new CommandManager(this, this.files, this.getDescription().getVersion(), this.useDiscordSRV, this.pd));
      this.files.loadFileWatcher();

      try {
         if (this.files.getConfig().getBoolean("metrics.enabled", true)) {
            new Metrics(this, 20795);
         } else {
            this.getLogger().info("Metrics disabled in config, ignoring it...");
         }
      } catch (Exception var12) {
      }

      this.logManager.log("");
      this.logManager.log("[STARTUP] Starting server with " + Bukkit.getVersion() + " [" + Bukkit.getBukkitVersion() + "] in FoxAddition v" + this.getDescription().getVersion());
   }

   public void reloadPlugin() {
      this.files.loadFileWatcher();
   }

   private void closeFileWatcher() {
      try {
         if (this.files.isWatchRunning) {
            this.files.isWatchRunning = false;
            this.files.stopFileWatcher();
            this.getLogger().info("[FILES] FileWatcher is now disabled.");
         }
      } catch (Exception var2) {
      }

   }

   public static FoxAdditionAPI getAPI() {
      return foxAdditionAPI;
   }

   private void checkForUpdates() {
      try {
         if (!this.files.getConfig().getBoolean("updates.update", true)) {
            return;
         }

         URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=111260");
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("GET");
         connection.setConnectTimeout(3000);
         connection.setReadTimeout(3000);
         connection.setUseCaches(false);
         int responseCode = connection.getResponseCode();
         if (responseCode == 200) {
            String currentVersion = this.getDescription().getVersion();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String latestVersion = reader.readLine();
            if (!latestVersion.equals(currentVersion)) {
               this.getLogger().warning("[UPDATER] A new version of this plugin has been found.");
               this.getLogger().warning("[UPDATER] Your version is: " + currentVersion);
               this.getLogger().warning("[UPDATER] Latest version is: " + latestVersion);
               this.getLogger().warning("[UPDATER] Download it in: https://www.spigotmc.org/resources/111260/");
            } else {
               this.getLogger().info("[UPDATER] You are currently using the lastest version.");
            }

            reader.close();
            connection.disconnect();
         } else {
            this.getLogger().warning("[UPDATER] Failed to check for new updates!");
         }

         connection.disconnect();
      } catch (IOException var7) {
         this.getLogger().warning("[UPDATER] Failed to check for new updates!");
      }

   }

   private boolean isPresent(String pluginName) {
      boolean enable = false;
      if (pluginName.toLowerCase() != "floodgate") {
         String configPath = "hooks." + pluginName.toLowerCase() + ".enable";
         enable = this.files.getConfig().getBoolean(configPath, false);
      } else {
         enable = this.files.getConfig().getBoolean("bedrock.floodgate.enable", false);
      }

      Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);
      return plugin != null && plugin.isEnabled() && enable;
   }

   private boolean isOldConfig(String fileName) {
      FileConfiguration config;
      byte max_version;
      if (fileName.equals("config")) {
         config = this.files.getConfig();
         max_version = 5;
      } else if (fileName.equals("language")) {
         config = this.files.getLangA();
         max_version = 3;
      } else {
         if (!fileName.equals("checks")) {
            int max_version = false;
            return false;
         }

         config = this.files.getChecks();
         max_version = 5;
      }

      try {
         if (config.contains("version")) {
            int configVersion = config.getInt("version", 0);
            if (configVersion == max_version) {
               return true;
            }

            return false;
         }
      } catch (NullPointerException var5) {
         this.getLogger().severe("[UPDATER] (!) The file '" + fileName + ".yml' is damaged/corrupted.");
         this.getLogger().severe("[UPDATER] (!) The file has been re-created again with default values.");
         this.getLogger().severe("[UPDATER] (!) You need to move old values in the new config manually.");
         this.files.reload("config");
         this.files.reload("language");
         this.files.reload("checks");
      }

      return false;
   }

   private void updateConfig(String fileName) {
      String configFile;
      if (fileName.equals("config")) {
         configFile = "config.yml";
      } else if (fileName.equals("language")) {
         configFile = "language.yml";
      } else if (fileName.equals("checks")) {
         configFile = "checks.yml";
      } else {
         configFile = null;
      }

      if (configFile != null) {
         File file = new File(this.getDataFolder(), configFile);
         File outdatedFolder = new File(this.getDataFolder(), "outdated");
         if (!outdatedFolder.exists()) {
            outdatedFolder.mkdir();
         }

         File renamedConfigFile = new File(outdatedFolder, "old_" + configFile);
         if (file.exists()) {
            try {
               Files.copy(file.toPath(), renamedConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
               file.delete();
            } catch (IOException var7) {
               this.getLogger().warning("[UPDATER] Failed to rename the " + fileName + ".yml file.");
               return;
            }
         }

         this.getLogger().info("[UPDATER] A new " + fileName + ".yml has been created.");
         this.configUpdated = true;
      }
   }

   private void header() {
      this.logCentered("");
      this.logCentered("______                         _      _  _  _    _                 ");
      this.logCentered("|  ____|             /\\       | |    | |(_)| |  (_)              ");
      this.logCentered("| |__  ___ __  __   /  \\    __| |  __| | _ | |_  _   ___   _ __  ");
      this.logCentered("|  __|/ _ \\\\ \\/ /  / /\\ \\  / _` | / _` || || __|| | / _ \\ | '_ \\ ");
      this.logCentered("| |  | (_) |>  <  / ____ \\| (_| || (_| || || |_ | || (_) || | | |");
      this.logCentered("|_|   \\___//_/\\_\\/_/    \\_\\\\__,_| \\__,_||_| \\__||_| \\___/ |_| |_|");
      this.logCentered("Powered by https://www.idcteam.xyz/");
      this.logCentered("");
      this.logCentered("    Created by NovaCraft254");
      this.logCentered("    Running on " + Bukkit.getVersion());
      this.logCentered("");
   }

   private void logCentered(String message) {
      int lineWidth = 53;
      int messageWidth = message.length();
      int padding = (lineWidth - messageWidth) / 2;
      StringBuilder paddedMessage = new StringBuilder();

      for(int i = 0; i < padding; ++i) {
         paddedMessage.append(" ");
      }

      paddedMessage.append(message);
      this.getLogger().info(paddedMessage.toString());
   }
}
