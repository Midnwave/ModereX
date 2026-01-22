/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PistonData
/*    */ {
/*    */   public final boolean isPush;
/*    */   public final boolean hasSlimeBlock;
/*    */   public final boolean hasHoneyBlock;
/*    */   public final BlockFace direction;
/*    */   public final int lastTransactionSent;
/* 16 */   public int ticksOfPistonBeingAlive = 0;
/*    */   
/*    */   public List<SimpleCollisionBox> boxes;
/*    */ 
/*    */   
/*    */   public PistonData(BlockFace direction, List<SimpleCollisionBox> pushedBlocks, int lastTransactionSent, boolean isPush, boolean hasSlimeBlock, boolean hasHoneyBlock) {
/* 22 */     this.direction = direction;
/* 23 */     this.boxes = pushedBlocks;
/* 24 */     this.lastTransactionSent = lastTransactionSent;
/* 25 */     this.isPush = isPush;
/* 26 */     this.hasSlimeBlock = hasSlimeBlock;
/* 27 */     this.hasHoneyBlock = hasHoneyBlock;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean tickIfGuaranteedFinished() {
/* 34 */     return (++this.ticksOfPistonBeingAlive >= 10);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\PistonData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */