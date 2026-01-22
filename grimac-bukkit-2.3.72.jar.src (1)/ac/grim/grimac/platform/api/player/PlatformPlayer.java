package ac.grim.grimac.platform.api.player;

import ac.grim.grimac.platform.api.entity.GrimEntity;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
import ac.grim.grimac.shaded.kyori.adventure.text.Component;

public interface PlatformPlayer extends GrimEntity, OfflinePlatformPlayer {
  void kickPlayer(String paramString);
  
  boolean isSneaking();
  
  void setSneaking(boolean paramBoolean);
  
  boolean hasPermission(String paramString);
  
  boolean hasPermission(String paramString, boolean paramBoolean);
  
  void sendMessage(String paramString);
  
  void sendMessage(Component paramComponent);
  
  void updateInventory();
  
  Vector3d getPosition();
  
  PlatformInventory getInventory();
  
  GrimEntity getVehicle();
  
  GameMode getGameMode();
  
  void setGameMode(GameMode paramGameMode);
  
  boolean isExternalPlayer();
  
  void sendPluginMessage(String paramString, byte[] paramArrayOfbyte);
  
  Sender getSender();
  
  default void replaceNativePlayer(Object nativePlayerObject) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\player\PlatformPlayer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */