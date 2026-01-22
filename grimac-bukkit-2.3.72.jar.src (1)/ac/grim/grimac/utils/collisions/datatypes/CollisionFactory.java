package ac.grim.grimac.utils.collisions.datatypes;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface CollisionFactory {
  CollisionBox fetch(GrimPlayer paramGrimPlayer, ClientVersion paramClientVersion, WrappedBlockState paramWrappedBlockState, int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\CollisionFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */