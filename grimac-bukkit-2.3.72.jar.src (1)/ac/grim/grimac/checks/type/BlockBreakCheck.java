package ac.grim.grimac.checks.type;

import ac.grim.grimac.utils.anticheat.update.BlockBreak;

public interface BlockBreakCheck extends PostPredictionCheck {
  default void onBlockBreak(BlockBreak blockBreak) {}
  
  default void onPostFlyingBlockBreak(BlockBreak blockBreak) {}
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\type\BlockBreakCheck.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */