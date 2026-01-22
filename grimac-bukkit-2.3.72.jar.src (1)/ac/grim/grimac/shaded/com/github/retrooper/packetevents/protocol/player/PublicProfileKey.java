/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*    */ 
/*    */ import java.security.PublicKey;
/*    */ import java.time.Instant;
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
/*    */ public class PublicProfileKey
/*    */ {
/*    */   private final Instant expiresAt;
/*    */   private final PublicKey key;
/*    */   private final byte[] keySignature;
/*    */   
/*    */   public PublicProfileKey(Instant expiresAt, PublicKey key, byte[] keySignature) {
/* 30 */     this.expiresAt = expiresAt;
/* 31 */     this.key = key;
/* 32 */     this.keySignature = keySignature;
/*    */   }
/*    */   
/*    */   public Instant getExpiresAt() {
/* 36 */     return this.expiresAt;
/*    */   }
/*    */   
/*    */   public PublicKey getKey() {
/* 40 */     return this.key;
/*    */   }
/*    */   
/*    */   public byte[] getKeySignature() {
/* 44 */     return this.keySignature;
/*    */   }
/*    */   
/*    */   public boolean hasExpired() {
/* 48 */     return this.expiresAt.isBefore(Instant.now());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\PublicProfileKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */