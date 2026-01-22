package ac.grim.grimac.api.feature;

import ac.grim.grimac.api.common.BasicReloadable;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
import java.util.Collection;

public interface FeatureManager extends BasicReloadable {
  Collection<String> getFeatureKeys();
  
  @Nullable
  FeatureState getFeatureState(String paramString);
  
  boolean isFeatureEnabled(String paramString);
  
  boolean setFeatureState(String paramString, FeatureState paramFeatureState);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\feature\FeatureManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */