/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.PublicKey;
/*    */ import java.security.Signature;
/*    */ import java.util.Base64;
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
/*    */ public class TextureProperty
/*    */ {
/*    */   private final String name;
/*    */   private final String value;
/*    */   private final String signature;
/*    */   
/*    */   public TextureProperty(String name, String value, @Nullable String signature) {
/* 32 */     this.name = name;
/* 33 */     this.value = value;
/* 34 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 38 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 42 */     return this.value;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getSignature() {
/* 47 */     return this.signature;
/*    */   }
/*    */   
/*    */   public boolean isSignatureValid(PublicKey publicKey) {
/* 51 */     if (getSignature() == null) {
/* 52 */       return false;
/*    */     }
/*    */     try {
/* 55 */       Signature signature = Signature.getInstance("SHA1withRSA");
/* 56 */       signature.initVerify(publicKey);
/* 57 */       signature.update(this.value.getBytes());
/* 58 */       return signature.verify(Base64.getDecoder().decode(this.signature));
/* 59 */     } catch (NoSuchAlgorithmException|java.security.InvalidKeyException|java.security.SignatureException ex) {
/* 60 */       ex.printStackTrace();
/*    */       
/* 62 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\TextureProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */