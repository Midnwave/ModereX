package ac.grim.grimac.api.config;

import ac.grim.grimac.api.common.BasicReloadable;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;

public interface ConfigManager extends BasicReloadable {
  String getStringElse(String paramString1, String paramString2);
  
  @Nullable
  String getString(String paramString);
  
  @Nullable
  List<String> getStringList(String paramString);
  
  List<String> getStringListElse(String paramString, List<String> paramList);
  
  int getIntElse(String paramString, int paramInt);
  
  long getLongElse(String paramString, long paramLong);
  
  double getDoubleElse(String paramString, double paramDouble);
  
  boolean getBooleanElse(String paramString, boolean paramBoolean);
  
  @Nullable
  <T> T get(String paramString);
  
  @Nullable
  <T> T getElse(String paramString, T paramT);
  
  @Nullable
  <K, V> Map<K, V> getMap(String paramString);
  
  @Nullable
  <K, V> Map<K, V> getMapElse(String paramString, Map<K, V> paramMap);
  
  @Nullable
  <T> List<T> getList(String paramString);
  
  @Nullable
  <T> List<T> getListElse(String paramString, List<T> paramList);
  
  boolean hasLoaded();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\config\ConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */