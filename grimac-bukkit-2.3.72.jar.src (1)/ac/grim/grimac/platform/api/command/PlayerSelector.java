package ac.grim.grimac.platform.api.command;

import ac.grim.grimac.platform.api.sender.Sender;
import java.util.Collection;

public interface PlayerSelector {
  boolean isSingle();
  
  Sender getSinglePlayer();
  
  Collection<Sender> getPlayers();
  
  String inputString();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\command\PlayerSelector.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */