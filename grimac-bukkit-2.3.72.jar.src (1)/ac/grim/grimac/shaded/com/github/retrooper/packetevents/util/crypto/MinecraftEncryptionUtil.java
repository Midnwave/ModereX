/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.crypto;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.KeyFactory;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.spec.EncodedKeySpec;
/*    */ import java.security.spec.X509EncodedKeySpec;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.IllegalBlockSizeException;
/*    */ import javax.crypto.NoSuchPaddingException;
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
/*    */ public class MinecraftEncryptionUtil
/*    */ {
/*    */   public static byte[] decryptRSA(PrivateKey privateKey, byte[] data) {
/* 40 */     return decrypt("RSA/ECB/PKCS1Padding", privateKey, data);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static byte[] encryptRSA(PublicKey publicKey, byte[] data) {
/* 51 */     return encrypt("RSA/ECB/PKCS1Padding", publicKey, data);
/*    */   }
/*    */   
/*    */   public static byte[] decrypt(Cipher cipher, byte[] data) {
/*    */     try {
/* 56 */       return cipher.doFinal(data);
/* 57 */     } catch (IllegalBlockSizeException|javax.crypto.BadPaddingException e) {
/* 58 */       e.printStackTrace();
/*    */       
/* 60 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static byte[] decrypt(String algorithm, PrivateKey privateKey, byte[] data) {
/*    */     try {
/* 66 */       Cipher cipher = Cipher.getInstance(algorithm);
/* 67 */       cipher.init(2, privateKey);
/* 68 */       return cipher.doFinal(data);
/* 69 */     } catch (NoSuchPaddingException|java.security.NoSuchAlgorithmException|java.security.InvalidKeyException|IllegalBlockSizeException|javax.crypto.BadPaddingException ex) {
/* 70 */       ex.printStackTrace();
/* 71 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static byte[] encrypt(String algorithm, PublicKey publicKey, byte[] data) {
/*    */     try {
/* 77 */       Cipher cipher = Cipher.getInstance(algorithm);
/* 78 */       cipher.init(1, publicKey);
/* 79 */       return cipher.doFinal(data);
/* 80 */     } catch (NoSuchPaddingException|java.security.NoSuchAlgorithmException|java.security.InvalidKeyException|IllegalBlockSizeException|javax.crypto.BadPaddingException ex) {
/* 81 */       ex.printStackTrace();
/* 82 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static byte[] encrypt(Cipher cipher, byte[] data) {
/* 88 */     return decrypt(cipher, data);
/*    */   }
/*    */   
/*    */   public static PublicKey publicKey(byte[] bytes) {
/*    */     try {
/* 93 */       EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bytes);
/* 94 */       KeyFactory keyFactory = KeyFactory.getInstance("RSA");
/* 95 */       return keyFactory.generatePublic(encodedKeySpec);
/* 96 */     } catch (Exception ex) {
/* 97 */       ex.printStackTrace();
/* 98 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\crypto\MinecraftEncryptionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */