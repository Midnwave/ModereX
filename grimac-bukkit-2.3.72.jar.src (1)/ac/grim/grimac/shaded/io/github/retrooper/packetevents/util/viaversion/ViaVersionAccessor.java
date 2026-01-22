package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.entity.Player;

public interface ViaVersionAccessor {
  int getProtocolVersion(Player paramPlayer);
  
  int getProtocolVersion(User paramUser);
  
  Class<?> getUserConnectionClass();
  
  Class<?> getBukkitDecodeHandlerClass();
  
  Class<?> getBukkitEncodeHandlerClass();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\viaversion\ViaVersionAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */