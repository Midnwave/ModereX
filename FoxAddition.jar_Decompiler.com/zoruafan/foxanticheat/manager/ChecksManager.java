package zoruafan.foxanticheat.manager;

import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChecksManager {
   private final FilesManager file;

   protected ChecksManager(FilesManager file) {
      this.file = file;
   }

   public boolean iAD(Player e, String t) {
      if (e.hasPermission("foxac.bypass." + t)) {
         return true;
      } else if (this.iIC(e)) {
         return true;
      } else {
         List<String> disabledWorlds = this.file.getChecks().getStringList(t.toLowerCase() + ".disabled-worlds");
         return disabledWorlds != null && disabledWorlds.contains(e.getWorld().getName());
      }
   }

   private boolean iIC(Player player) {
      Location location = player.getLocation();
      int chunkX = location.getBlockX() >> 4;
      int chunkZ = location.getBlockZ() >> 4;
      return !player.getWorld().isChunkLoaded(chunkX, chunkZ);
   }

   public int gP(Player player) {
      try {
         Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
         Field pingField = entityPlayer.getClass().getDeclaredField("ping");
         pingField.setAccessible(true);
         return pingField.getInt(entityPlayer);
      } catch (Exception var4) {
         return -1;
      }
   }

   public static double[] gT() {
      try {
         Object minecraftServer = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
         double[] recentTps = (double[])minecraftServer.getClass().getField("recentTps").get(minecraftServer);

         for(int i = 0; i < recentTps.length; ++i) {
            recentTps[i] = Math.min(recentTps[i], 20.0D);
         }

         return recentTps;
      } catch (Exception var3) {
         return new double[]{-1.0D, -1.0D, -1.0D};
      }
   }
}
