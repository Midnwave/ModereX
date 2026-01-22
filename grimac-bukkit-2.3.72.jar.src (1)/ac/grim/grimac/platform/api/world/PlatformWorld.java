package ac.grim.grimac.platform.api.world;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
import java.util.UUID;

public interface PlatformWorld {
  boolean isChunkLoaded(int paramInt1, int paramInt2);
  
  WrappedBlockState getBlockAt(int paramInt1, int paramInt2, int paramInt3);
  
  String getName();
  
  @Nullable
  UUID getUID();
  
  PlatformChunk getChunkAt(int paramInt1, int paramInt2);
  
  boolean isLoaded();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\world\PlatformWorld.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */