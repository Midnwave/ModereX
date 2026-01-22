package ac.grim.grimac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {
  default void process(RotationUpdate rotationUpdate) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\RotationCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */