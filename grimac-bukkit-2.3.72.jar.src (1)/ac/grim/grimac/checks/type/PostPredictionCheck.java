package ac.grim.grimac.checks.type;

import ac.grim.grimac.utils.anticheat.update.PredictionComplete;

public interface PostPredictionCheck extends PacketCheck {
  default void onPredictionComplete(PredictionComplete predictionComplete) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\PostPredictionCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */