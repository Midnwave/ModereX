/*    */ package ac.grim.grimac.utils.anticheat.update;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.collisions.HitboxData;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class BlockBreak
/*    */ {
/*    */   public final Vector3i position;
/*    */   public final BlockFace face;
/*    */   public final int faceId;
/*    */   public final DiggingAction action;
/*    */   
/*    */   @Generated
/*    */   public boolean isCancelled() {
/* 24 */     return this.cancelled;
/*    */   }
/*    */   public final int sequence; public final WrappedBlockState block; private final GrimPlayer player; private boolean cancelled;
/*    */   public BlockBreak(GrimPlayer player, Vector3i position, BlockFace face, int faceId, DiggingAction action, int sequence, WrappedBlockState block) {
/* 28 */     this.player = player;
/* 29 */     this.position = position;
/* 30 */     this.face = face;
/* 31 */     this.faceId = faceId;
/* 32 */     this.action = action;
/* 33 */     this.sequence = sequence;
/* 34 */     this.block = block;
/*    */   }
/*    */   
/*    */   public void cancel() {
/* 38 */     this.cancelled = true;
/*    */   }
/*    */   
/*    */   public SimpleCollisionBox getCombinedBox() {
/* 42 */     CollisionBox placedOn = HitboxData.getBlockHitbox(this.player, this.player.inventory.getHeldItem().getType().getPlacedType(), this.player.getClientVersion(), this.block, true, this.position.x, this.position.y, this.position.z);
/*    */     
/* 44 */     List<SimpleCollisionBox> boxes = new ArrayList<>();
/* 45 */     placedOn.downCast(boxes);
/*    */     
/* 47 */     SimpleCollisionBox combined = new SimpleCollisionBox(this.position.x, this.position.y, this.position.z);
/* 48 */     for (SimpleCollisionBox box : boxes) {
/* 49 */       double minX = Math.max(box.minX, combined.minX);
/* 50 */       double minY = Math.max(box.minY, combined.minY);
/* 51 */       double minZ = Math.max(box.minZ, combined.minZ);
/* 52 */       double maxX = Math.min(box.maxX, combined.maxX);
/* 53 */       double maxY = Math.min(box.maxY, combined.maxY);
/* 54 */       double maxZ = Math.min(box.maxZ, combined.maxZ);
/* 55 */       combined = new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
/*    */     } 
/*    */     
/* 58 */     return combined;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\antichea\\update\BlockBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */