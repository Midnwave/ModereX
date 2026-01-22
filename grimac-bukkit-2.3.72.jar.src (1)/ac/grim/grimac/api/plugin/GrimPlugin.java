package ac.grim.grimac.api.plugin;

import java.io.File;
import java.util.logging.Logger;

public interface GrimPlugin {
  GrimPluginDescription getDescription();
  
  Logger getLogger();
  
  File getDataFolder();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\plugin\GrimPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */