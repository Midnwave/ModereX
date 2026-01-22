package ac.grim.grimac.platform.api.manager;

import ac.grim.grimac.platform.api.player.PlatformPlayer;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;

public interface ItemResetHandler {
  void resetItemUsage(@Nullable PlatformPlayer paramPlatformPlayer);
  
  @Contract("null -> null")
  @Nullable
  InteractionHand getItemUsageHand(@Nullable PlatformPlayer paramPlatformPlayer);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\manager\ItemResetHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */