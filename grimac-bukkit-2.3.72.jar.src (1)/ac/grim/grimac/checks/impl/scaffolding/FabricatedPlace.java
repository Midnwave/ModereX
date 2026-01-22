/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.nmsutil.Materials;
/*    */ 
/*    */ @CheckData(name = "FabricatedPlace", description = "Sent out of bounds cursor position")
/*    */ public class FabricatedPlace extends BlockPlaceCheck {
/*    */   public FabricatedPlace(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 19 */     Vector3f cursor = place.cursor;
/* 20 */     if (cursor == null)
/*    */       return; 
/* 22 */     double allowed = (Materials.isShapeExceedsCube(place.getPlacedAgainstMaterial()) || place.getPlacedAgainstMaterial() == StateTypes.LECTERN) ? 1.5D : 1.0D;
/* 23 */     double minAllowed = 1.0D - allowed;
/*    */     
/* 25 */     if ((cursor.getX() < minAllowed || cursor.getY() < minAllowed || cursor.getZ() < minAllowed || cursor.getX() > allowed || cursor.getY() > allowed || cursor.getZ() > allowed) && 
/* 26 */       flagAndAlert() && shouldModifyPackets() && shouldCancel())
/* 27 */       place.resync(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\FabricatedPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */