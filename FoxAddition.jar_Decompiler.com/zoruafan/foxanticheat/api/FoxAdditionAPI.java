package zoruafan.foxanticheat.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface FoxAdditionAPI {
   void flag(Player var1, String var2, int var3, String var4, String var5, String var6);

   int getVLS(Player var1, String var2);

   void addVLS(Player var1, String var2, int var3);

   void setVLS(Player var1, String var2, int var3);

   boolean getVerbose(CommandSender var1);

   void toggleVerbose(CommandSender var1);

   void verboseNotify(String var1);
}
