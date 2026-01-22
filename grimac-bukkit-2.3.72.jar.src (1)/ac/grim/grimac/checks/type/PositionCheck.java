package ac.grim.grimac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.utils.anticheat.update.PositionUpdate;

public interface PositionCheck extends AbstractCheck {
  default void onPositionUpdate(PositionUpdate positionUpdate) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\PositionCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */