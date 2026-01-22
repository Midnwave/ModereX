package ac.grim.grimac.shaded.zaxxer.hikari.metrics;

public interface IMetricsTracker extends AutoCloseable {
  default void recordConnectionCreatedMillis(long connectionCreatedMillis) {}
  
  default void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {}
  
  default void recordConnectionUsageMillis(long elapsedBorrowedMillis) {}
  
  default void recordConnectionTimeout() {}
  
  default void close() {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\zaxxer\hikari\metrics\IMetricsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */