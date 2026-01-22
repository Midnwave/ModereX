package ac.grim.grimac.platform.api;

import ac.grim.grimac.platform.api.sender.Sender;

public interface PlatformServer {
  String getPlatformImplementationString();
  
  void dispatchCommand(Sender paramSender, String paramString);
  
  Sender getConsoleSender();
  
  void registerOutgoingPluginChannel(String paramString);
  
  double getTPS();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\PlatformServer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */