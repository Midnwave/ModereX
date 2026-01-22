/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PacketTypeData
/*    */ {
/*    */   @Nullable
/*    */   private PacketTypeCommon packetType;
/*    */   private int nativePacketId;
/*    */   
/*    */   public PacketTypeData(@Nullable PacketTypeCommon packetType, int nativePacketId) {
/* 29 */     this.packetType = packetType;
/* 30 */     this.nativePacketId = nativePacketId;
/*    */   }
/*    */   @Nullable
/*    */   public PacketTypeCommon getPacketType() {
/* 34 */     return this.packetType;
/*    */   }
/*    */   
/*    */   public int getNativePacketId() {
/* 38 */     return this.nativePacketId;
/*    */   }
/*    */   
/*    */   public void setPacketType(@Nullable PacketTypeCommon packetType) {
/* 42 */     this.packetType = packetType;
/*    */   }
/*    */   
/*    */   public void setNativePacketId(int nativePacketId) {
/* 46 */     this.nativePacketId = nativePacketId;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\PacketTypeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */