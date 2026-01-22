package ac.grim.grimac.platform.api;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.grim.grimac.platform.api.manager.CommandAdapter;
import ac.grim.grimac.platform.api.manager.ItemResetHandler;
import ac.grim.grimac.platform.api.manager.MessagePlaceHolderManager;
import ac.grim.grimac.platform.api.manager.PermissionRegistrationManager;
import ac.grim.grimac.platform.api.manager.PlatformPluginManager;
import ac.grim.grimac.platform.api.player.PlatformPlayerFactory;
import ac.grim.grimac.platform.api.scheduler.PlatformScheduler;
import ac.grim.grimac.platform.api.sender.Sender;
import ac.grim.grimac.platform.api.sender.SenderFactory;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
import ac.grim.grimac.shaded.incendo.cloud.CommandManager;

public interface PlatformLoader {
  PlatformScheduler getScheduler();
  
  PlatformPlayerFactory getPlatformPlayerFactory();
  
  CommandAdapter getCommandAdapter();
  
  PacketEventsAPI<?> getPacketEvents();
  
  CommandManager<Sender> getCommandManager();
  
  ItemResetHandler getItemResetHandler();
  
  SenderFactory<?> getSenderFactory();
  
  GrimPlugin getPlugin();
  
  PlatformPluginManager getPluginManager();
  
  PlatformServer getPlatformServer();
  
  void registerAPIService();
  
  MessagePlaceHolderManager getMessagePlaceHolderManager();
  
  PermissionRegistrationManager getPermissionManager();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\PlatformLoader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */