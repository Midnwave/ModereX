/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ 
/*    */ 
/*    */ public final class Dummy
/*    */ {
/* 25 */   public static final Dummy DUMMY = new Dummy();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Dummy dummyRead(PacketWrapper<?> wrapper) {
/* 31 */     return DUMMY;
/*    */   }
/*    */   
/*    */   public static Dummy dummyReadNbt(PacketWrapper<?> wrapper) {
/* 35 */     wrapper.readNBTRaw();
/* 36 */     return DUMMY;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void dummyWrite(PacketWrapper<?> wrapper, Dummy dummy) {}
/*    */ 
/*    */   
/*    */   public static void dummyWriteNbt(PacketWrapper<?> wrapper, Dummy dummy) {
/* 44 */     wrapper.writeByte(10);
/* 45 */     wrapper.writeByte(0);
/*    */   }
/*    */   
/*    */   public static Dummy dummy() {
/* 49 */     return DUMMY;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "Dummy{}";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Dummy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */