package ac.grim.grimac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;

public interface PacketCheck extends AbstractCheck {
  default void onPacketReceive(PacketReceiveEvent event) {}
  
  default void onPacketSend(PacketSendEvent event) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\PacketCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */