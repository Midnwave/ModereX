package zoruafan.foxanticheat.checks.phase;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.manager.FilesManager;

public class PhaseData extends FilesManager implements Listener {
   private final FilesManager file;
   public List<String> eBC;

   public PhaseData(JavaPlugin plugin, FilesManager file) {
      super(plugin);
      this.file = file;
      this.eBC = this.lEB();
   }

   private String getServerVersion() {
      try {
         String bukkitVersion = Bukkit.getBukkitVersion();
         String[] versionParts = bukkitVersion.split("\\.");
         if (versionParts.length < 3) {
            return "1.20";
         } else {
            String major = versionParts[0];
            String minor = versionParts[1];
            String minor_1;
            if (minor.contains("-")) {
               String[] minorParts = minor.split("-");
               minor_1 = minorParts[0];
            } else {
               minor_1 = minor;
            }

            if (major.equals("1") && minor_1.equals("21")) {
               return "1.20";
            } else if (major.equals("1") && minor_1.equals("14")) {
               return "1.13";
            } else if (!major.equals("1") || !minor_1.equals("9") && !minor_1.equals("10") && !minor_1.equals("11")) {
               if (major.equals("1") && Integer.parseInt(minor_1) > 20) {
                  Bukkit.getLogger().warning("[PHASE] You're using a version newer than 1.20, using 1.20 excluded blocks.");
                  return "1.20";
               } else {
                  return String.valueOf(major) + "." + minor_1;
               }
            } else {
               return "1.8";
            }
         }
      } catch (Exception var7) {
         Bukkit.getLogger().severe("[PHASE] Your version can't be detected. Using 1.20...");
         return "1.20";
      }
   }

   public boolean getEB(String m) {
      return this.eBC.contains(m);
   }

   public List<String> getEBL() {
      return this.eBC;
   }

   public List<String> resetEBL() {
      this.eBC.clear();
      this.eBC = new ArrayList();
      return this.eBC = this.lEB();
   }

   public List<String> lEB() {
      String serverVersion = this.file.getChecks().getString("phase.eb.version", "auto").toLowerCase().replace("1.", "");
      List<String> eB = new ArrayList();
      List<String> cEB = this.file.getChecks().getStringList("phase.excluded-blocks");
      Iterator var5 = cEB.iterator();

      String dEBFP;
      while(var5.hasNext()) {
         dEBFP = (String)var5.next();
         Material material = Material.matchMaterial(dEBFP);
         if (material != null) {
            eB.add(material.name());
         } else {
            this.plugin().getLogger().warning("[PHASE] Invalid excluded block type: " + dEBFP);
         }
      }

      if (serverVersion.equals("auto")) {
         this.plugin().getLogger().info("[PHASE] Detecting automatically your version...");
         serverVersion = this.getServerVersion();
      } else {
         this.plugin().getLogger().info("[PHASE] Using a defined version to exclude blocks in the configuration.");
         serverVersion = "1." + this.file.getChecks().getString("phase.eb.version").toLowerCase();
      }

      dEBFP = "versions/phase/" + serverVersion + ".yml";
      File dEBF = new File(this.plugin().getDataFolder(), dEBFP);
      if (!dEBF.exists()) {
         this.plugin().getLogger().warning("[PHASE] Default excluded blocks file (" + dEBF + ") not found.");
         return this.eBC = eB;
      } else if (!this.getChecks().getBoolean("phase.eb.enable", true)) {
         this.plugin().getLogger().warning("[PHASE] Default excluded blocks are disabled in config. Skipping!");
         return this.eBC = eB;
      } else {
         FileConfiguration dEBC = YamlConfiguration.loadConfiguration(dEBF);
         List<String> dEB = dEBC.getStringList("excluded-blocks");
         Iterator var9 = dEB.iterator();

         while(var9.hasNext()) {
            String block = (String)var9.next();
            Material material = Material.matchMaterial(block);
            if (material != null) {
               eB.add(material.name());
            } else {
               this.plugin().getLogger().warning("[PHASE] Invalid default excluded block type in " + dEBFP + ": " + block);
            }
         }

         this.eBC = eB;
         return eB;
      }
   }
}
