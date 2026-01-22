package ac.grim.grimac.shaded.zaxxer.hikari;

public interface HikariPoolMXBean {
  int getIdleConnections();
  
  int getActiveConnections();
  
  int getTotalConnections();
  
  int getThreadsAwaitingConnection();
  
  void softEvictConnections();
  
  void suspendPool();
  
  void resumePool();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\HikariPoolMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */