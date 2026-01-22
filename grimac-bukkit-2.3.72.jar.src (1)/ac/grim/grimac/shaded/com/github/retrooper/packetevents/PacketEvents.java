/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ public final class PacketEvents
/*    */ {
/*    */   private static PacketEventsAPI<?> API;
/*    */   @Internal
/*    */   public static String IDENTIFIER;
/*    */   @Internal
/*    */   public static String ENCODER_NAME;
/*    */   @Internal
/*    */   public static String DECODER_NAME;
/*    */   @Internal
/*    */   public static String CONNECTION_HANDLER_NAME;
/*    */   @Internal
/*    */   public static String SERVER_CHANNEL_HANDLER_NAME;
/*    */   @Internal
/*    */   public static String TIMEOUT_HANDLER_NAME;
/*    */   
/*    */   public static PacketEventsAPI<?> getAPI() {
/* 32 */     return API;
/*    */   }
/*    */   
/*    */   public static void setAPI(PacketEventsAPI<?> api) {
/* 36 */     API = api;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\PacketEvents.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */