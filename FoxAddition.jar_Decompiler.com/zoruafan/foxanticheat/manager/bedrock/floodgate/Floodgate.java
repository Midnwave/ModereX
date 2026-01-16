package zoruafan.foxanticheat.manager.bedrock.floodgate;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class Floodgate {
   public static boolean isBedrockUser(Player player) {
      try {
         FloodgateApi api = FloodgateApi.getInstance();
         return api.isFloodgatePlayer(player.getUniqueId());
      } catch (Exception var2) {
         return false;
      }
   }
}
