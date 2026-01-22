package ac.grim.grimac.api.event;

@FunctionalInterface
public interface GrimEventListener<T extends GrimEvent> {
  void handle(T paramT) throws Exception;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\GrimEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */