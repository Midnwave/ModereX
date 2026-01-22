/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ @CheckData(name = "InvalidPlaceA", description = "Sent invalid cursor position")
/*    */ public class InvalidPlaceA extends BlockPlaceCheck {
/*    */   public InvalidPlaceA(GrimPlayer player) {
/* 12 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 17 */     Vector3f cursor = place.cursor;
/* 18 */     if (cursor == null)
/* 19 */       return;  if ((!Float.isFinite(cursor.x) || !Float.isFinite(cursor.y) || !Float.isFinite(cursor.z)) && 
/* 20 */       flagAndAlert() && shouldModifyPackets() && shouldCancel())
/* 21 */       place.resync(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\InvalidPlaceA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */