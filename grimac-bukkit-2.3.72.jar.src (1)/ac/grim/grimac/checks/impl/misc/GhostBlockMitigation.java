/*    */ package ac.grim.grimac.checks.impl.misc;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ public class GhostBlockMitigation
/*    */   extends BlockPlaceCheck {
/*    */   private boolean allow;
/*    */   private int distance;
/*    */   
/*    */   public GhostBlockMitigation(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 22 */     if (this.allow || this.player.platformPlayer == null)
/*    */       return; 
/* 24 */     PlatformWorld world = this.player.platformPlayer.getWorld();
/* 25 */     Vector3i pos = place.getPlacedBlockPos();
/* 26 */     Vector3i posAgainst = place.position;
/*    */     
/* 28 */     int x = pos.getX();
/* 29 */     int y = pos.getY();
/* 30 */     int z = pos.getZ();
/*    */     
/* 32 */     int xAgainst = posAgainst.getX();
/* 33 */     int yAgainst = posAgainst.getY();
/* 34 */     int zAgainst = posAgainst.getZ();
/*    */     
/*    */     try {
/* 37 */       for (int i = x - this.distance; i <= x + this.distance; i++) {
/* 38 */         for (int j = y - this.distance; j <= y + this.distance; j++) {
/* 39 */           for (int k = z - this.distance; k <= z + this.distance; k++) {
/* 40 */             if (i != x || j != y || k != z)
/*    */             {
/*    */ 
/*    */               
/* 44 */               if (i != xAgainst || j != yAgainst || k != zAgainst)
/*    */               {
/*    */ 
/*    */                 
/* 48 */                 if (world.isChunkLoaded(i >> 4, k >> 4)) {
/*    */ 
/*    */ 
/*    */                   
/* 52 */                   WrappedBlockState type = world.getBlockAt(i, j, k);
/*    */                   
/* 54 */                   if (!type.getType().isAir())
/*    */                     return; 
/*    */                 }  } 
/*    */             }
/*    */           } 
/*    */         } 
/*    */       } 
/* 61 */       place.resync();
/* 62 */     } catch (Exception exception) {}
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 68 */     this.allow = config.getBooleanElse("exploit.allow-building-on-ghostblocks", true);
/* 69 */     this.distance = config.getIntElse("exploit.distance-to-check-for-ghostblocks", 2);
/*    */     
/* 71 */     if (this.distance < 2 || this.distance > 4) this.distance = 2; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\misc\GhostBlockMitigation.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */