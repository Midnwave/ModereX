/*   */ package ac.grim.grimac.utils.collisions.datatypes;
/*   */ 
/*   */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*   */ 
/*   */ public class HexOffsetCollisionBox
/*   */   extends OffsetCollisionBox {
/*   */   public HexOffsetCollisionBox(StateType block, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
/* 8 */     super(block, minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
/*   */   }
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\HexOffsetCollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */