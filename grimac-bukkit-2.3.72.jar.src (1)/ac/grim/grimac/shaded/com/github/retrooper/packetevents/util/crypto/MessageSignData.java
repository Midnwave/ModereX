/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto;
/*    */ 
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
/*    */ public class MessageSignData
/*    */ {
/*    */   private final SaltSignature saltSignature;
/*    */   private final Instant timestamp;
/*    */   private boolean signedPreview;
/*    */   
/*    */   public MessageSignData(SaltSignature saltSignature, Instant timestamp) {
/* 29 */     this.saltSignature = saltSignature;
/* 30 */     this.timestamp = timestamp;
/*    */   }
/*    */   
/*    */   public MessageSignData(SaltSignature saltSignature, Instant timestamp, boolean signedPreview) {
/* 34 */     this.saltSignature = saltSignature;
/* 35 */     this.timestamp = timestamp;
/* 36 */     this.signedPreview = signedPreview;
/*    */   }
/*    */   public SaltSignature getSaltSignature() {
/* 39 */     return this.saltSignature;
/*    */   }
/*    */   
/*    */   public Instant getTimestamp() {
/* 43 */     return this.timestamp;
/*    */   }
/*    */   
/*    */   public boolean isSignedPreview() {
/* 47 */     return this.signedPreview;
/*    */   }
/*    */   
/*    */   public void setSignedPreview(boolean signedPreview) {
/* 51 */     this.signedPreview = signedPreview;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\crypto\MessageSignData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */