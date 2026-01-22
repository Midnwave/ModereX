/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto;
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
/*    */ public class SaltSignature
/*    */ {
/*    */   private long salt;
/*    */   private byte[] signature;
/*    */   
/*    */   public SaltSignature(long salt, byte[] signature) {
/* 26 */     this.salt = salt;
/* 27 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public long getSalt() {
/* 31 */     return this.salt;
/*    */   }
/*    */   
/*    */   public void setSalt(long salt) {
/* 35 */     this.salt = salt;
/*    */   }
/*    */   
/*    */   public byte[] getSignature() {
/* 39 */     return this.signature;
/*    */   }
/*    */   
/*    */   public void setSignature(byte[] signature) {
/* 43 */     this.signature = signature;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\crypto\SaltSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */