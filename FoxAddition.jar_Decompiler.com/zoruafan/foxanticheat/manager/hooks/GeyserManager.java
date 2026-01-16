package zoruafan.foxanticheat.manager.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.bedrock.floodgate.Floodgate;

public class GeyserManager extends Floodgate {
   private final FilesManager file;
   private Boolean floodgate = false;

   public GeyserManager(FilesManager files) {
      this.file = files;
      if (Bukkit.getPluginManager().isPluginEnabled("Floodgate")) {
         this.floodgate = true;
      }

   }

   public boolean isAllowDetect_Mode(Player e, String check) {
      boolean isBedrock = false;
      boolean isBedrock_1 = false;
      if (this.file.getConfig().getBoolean("bedrock.prefix.enable", false)) {
         isBedrock_1 = e.getName().startsWith(this.file.getConfig().getString("bedrock.prefix.prefix", "."));
         if (isBedrock_1) {
            isBedrock = true;
         }
      }

      if (this.file.getConfig().getBoolean("bedrock.uuid.enable", true)) {
         isBedrock_1 = e.getUniqueId().toString().startsWith("00000000-0000-0000-");
         if (isBedrock_1) {
            isBedrock = true;
         }
      }

      if (!isBedrock_1 && this.floodgate) {
         try {
            isBedrock_1 = Floodgate.isBedrockUser(e);
            if (isBedrock_1) {
               isBedrock = true;
            }
         } catch (Exception var7) {
         }
      }

      byte mode;
      if (!isBedrock) {
         mode = 1;
      } else {
         mode = 2;
      }

      return mode == 1 ? this.file.getChecks().getBoolean(check + ".java", true) : this.file.getChecks().getBoolean(check + ".bedrock", true);
   }

   public boolean isBedrock(Player e) {
      boolean isBedrock = false;
      if (this.file.getConfig().getBoolean("bedrock.prefix.enable", false)) {
         isBedrock = e.getName().startsWith(this.file.getConfig().getString("bedrock.prefix.prefix", "."));
         if (isBedrock) {
            return true;
         }
      }

      if (this.file.getConfig().getBoolean("bedrock.uuid.enable", true)) {
         isBedrock = e.getUniqueId().toString().startsWith("00000000-0000-0000-");
         if (isBedrock) {
            return true;
         }
      }

      if (this.floodgate && this.file.getConfig().getBoolean("bedrock.floodgate.enable", false)) {
         try {
            isBedrock = Floodgate.isBedrockUser(e);
            if (isBedrock) {
               return true;
            }
         } catch (Exception var4) {
         }
      }

      return isBedrock;
   }
}
