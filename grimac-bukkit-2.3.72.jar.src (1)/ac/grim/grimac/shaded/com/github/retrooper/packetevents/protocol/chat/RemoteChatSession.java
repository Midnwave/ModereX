/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.PublicProfileKey;
/*    */ import java.util.UUID;
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
/*    */ public class RemoteChatSession
/*    */ {
/*    */   private final UUID sessionId;
/*    */   private final PublicProfileKey publicProfileKey;
/*    */   
/*    */   public RemoteChatSession(UUID sessionId, PublicProfileKey publicProfileKey) {
/* 30 */     this.sessionId = sessionId;
/* 31 */     this.publicProfileKey = publicProfileKey;
/*    */   }
/*    */   
/*    */   public UUID getSessionId() {
/* 35 */     return this.sessionId;
/*    */   }
/*    */   
/*    */   public PublicProfileKey getPublicProfileKey() {
/* 39 */     return this.publicProfileKey;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\RemoteChatSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */