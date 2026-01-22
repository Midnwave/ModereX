package ac.grim.grimac.shaded.snakeyaml.parser;

import ac.grim.grimac.shaded.snakeyaml.events.Event;

public interface Parser {
  boolean checkEvent(Event.ID paramID);
  
  Event peekEvent();
  
  Event getEvent();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\parser\Parser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */