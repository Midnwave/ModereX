/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto;
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
/*    */ public class SignatureData
/*    */ {
/*    */   private final Instant timestamp;
/*    */   private final PublicKey publicKey;
/*    */   private final byte[] signature;
/*    */   
/*    */   public SignatureData(Instant timestamp, PublicKey publicKey, byte[] signature) {
/* 30 */     this.timestamp = timestamp;
/* 31 */     this.publicKey = publicKey;
/* 32 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public SignatureData(Instant timestamp, byte[] encodedPublicKey, byte[] signature) {
/* 36 */     this.timestamp = timestamp;
/* 37 */     this.publicKey = MinecraftEncryptionUtil.publicKey(encodedPublicKey);
/* 38 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public Instant getTimestamp() {
/* 42 */     return this.timestamp;
/*    */   }
/*    */   
/*    */   public PublicKey getPublicKey() {
/* 46 */     return this.publicKey;
/*    */   }
/*    */   
/*    */   public byte[] getSignature() {
/* 50 */     return this.signature;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\crypto\SignatureData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */