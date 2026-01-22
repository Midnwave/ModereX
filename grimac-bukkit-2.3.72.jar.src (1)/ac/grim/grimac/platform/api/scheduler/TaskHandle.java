package ac.grim.grimac.platform.api.scheduler;

public interface TaskHandle {
  boolean isSync();
  
  boolean isCancelled();
  
  void cancel();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\scheduler\TaskHandle.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */