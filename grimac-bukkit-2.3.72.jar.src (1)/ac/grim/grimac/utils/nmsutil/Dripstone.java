/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Thickness;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.VerticalDirection;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class Dripstone {
/*    */   @Generated
/*    */   private Dripstone() {
/* 12 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static void update(@NotNull GrimPlayer player, @NotNull WrappedBlockState toPlace, int x, int y, int z, boolean secondaryUse) {
/* 14 */     VerticalDirection primaryDirection = toPlace.getVerticalDirection();
/* 15 */     VerticalDirection opposite = (toPlace.getVerticalDirection() == VerticalDirection.UP) ? VerticalDirection.DOWN : VerticalDirection.UP;
/*    */     
/* 17 */     WrappedBlockState typePlacingOn = player.compensatedWorld.getBlock(x, y + ((primaryDirection == VerticalDirection.UP) ? 1 : -1), z);
/*    */     
/* 19 */     if (isPointedDripstoneWithDirection(typePlacingOn, opposite)) {
/*    */ 
/*    */       
/* 22 */       Thickness thick = (secondaryUse && typePlacingOn.getThickness() != Thickness.TIP_MERGE) ? Thickness.TIP : Thickness.TIP_MERGE;
/*    */       
/* 24 */       toPlace.setThickness(thick);
/*    */     
/*    */     }
/* 27 */     else if (!isPointedDripstoneWithDirection(typePlacingOn, primaryDirection)) {
/* 28 */       toPlace.setThickness(Thickness.TIP);
/*    */     } else {
/* 30 */       Thickness dripThick = typePlacingOn.getThickness();
/* 31 */       if (dripThick != Thickness.TIP && dripThick != Thickness.TIP_MERGE) {
/*    */         
/* 33 */         WrappedBlockState oppositeData = player.compensatedWorld.getBlock(x, y + ((opposite == VerticalDirection.UP) ? 1 : -1), z);
/*    */         
/* 35 */         Thickness toSetThick = !isPointedDripstoneWithDirection(oppositeData, primaryDirection) ? Thickness.BASE : Thickness.MIDDLE;
/* 36 */         toPlace.setThickness(toSetThick);
/*    */       } else {
/* 38 */         toPlace.setThickness(Thickness.FRUSTUM);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static boolean isPointedDripstoneWithDirection(@NotNull WrappedBlockState unknown, VerticalDirection direction) {
/* 45 */     return (unknown.getType() == StateTypes.POINTED_DRIPSTONE && unknown.getVerticalDirection() == direction);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\Dripstone.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */