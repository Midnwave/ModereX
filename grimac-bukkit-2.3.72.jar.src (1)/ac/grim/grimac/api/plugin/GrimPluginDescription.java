package ac.grim.grimac.api.plugin;

import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
import java.util.Collection;

public interface GrimPluginDescription {
  String getVersion();
  
  String getDescription();
  
  @NotNull
  Collection<String> getAuthors();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\plugin\GrimPluginDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */