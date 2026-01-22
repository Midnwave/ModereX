/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntityShulker;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShulkerData
/*    */ {
/*    */   public final int lastTransactionSent;
/*    */   public final boolean isClosing;
/* 15 */   public PacketEntity entity = null;
/* 16 */   public Vector3i blockPos = null;
/*    */ 
/*    */   
/* 19 */   private int ticksOfOpeningClosing = 0;
/*    */   
/*    */   public ShulkerData(Vector3i position, int lastTransactionSent, boolean isClosing) {
/* 22 */     this.lastTransactionSent = lastTransactionSent;
/* 23 */     this.isClosing = isClosing;
/* 24 */     this.blockPos = position;
/*    */   }
/*    */   
/*    */   public ShulkerData(PacketEntityShulker entity, int lastTransactionSent, boolean isClosing) {
/* 28 */     this.lastTransactionSent = lastTransactionSent;
/* 29 */     this.isClosing = isClosing;
/* 30 */     this.entity = (PacketEntity)entity;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean tickIfGuaranteedFinished() {
/* 37 */     return (this.isClosing && ++this.ticksOfOpeningClosing >= 25);
/*    */   }
/*    */   
/*    */   public SimpleCollisionBox getCollision() {
/* 41 */     if (this.blockPos != null) {
/* 42 */       return new SimpleCollisionBox(this.blockPos);
/*    */     }
/* 44 */     return this.entity.getPossibleCollisionBoxes();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 49 */     if (this == o) return true; 
/* 50 */     if (o == null || getClass() != o.getClass()) return false; 
/* 51 */     ShulkerData that = (ShulkerData)o;
/* 52 */     return (Objects.equals(this.entity, that.entity) && Objects.equals(this.blockPos, that.blockPos));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return Objects.hash(new Object[] { this.entity, this.blockPos });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\ShulkerData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */