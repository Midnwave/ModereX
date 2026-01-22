/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.TimeStampMode;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class PacketEvent
/*    */   implements CallableEvent
/*    */ {
/*    */   private final long timestamp;
/*    */   
/*    */   public PacketEvent() {
/* 36 */     TimeStampMode timeStampMode = PacketEvents.getAPI().getSettings().getTimeStampMode();
/* 37 */     switch (timeStampMode) {
/*    */       case MILLIS:
/* 39 */         this.timestamp = System.currentTimeMillis();
/*    */         return;
/*    */       case NANO:
/* 42 */         this.timestamp = System.nanoTime();
/*    */         return;
/*    */     } 
/*    */     
/* 46 */     this.timestamp = 0L;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getTimestamp() {
/* 57 */     return this.timestamp;
/*    */   }
/*    */   
/*    */   public void callPacketEventExternal(PacketListenerCommon listener) {
/* 61 */     listener.onPacketEventExternal(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\PacketEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */