/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Optional;
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
/*    */ public class MessageSignature
/*    */ {
/*    */   private byte[] bytes;
/*    */   
/*    */   public MessageSignature(byte[] bytes) {
/* 29 */     this.bytes = bytes;
/*    */   }
/*    */   
/*    */   public byte[] getBytes() {
/* 33 */     return this.bytes;
/*    */   }
/*    */   
/*    */   public void setBytes(byte[] bytes) {
/* 37 */     this.bytes = bytes;
/*    */   }
/*    */   
/*    */   public static class Packed
/*    */   {
/*    */     private int id;
/*    */     
/*    */     public Packed(@Nullable MessageSignature fullSignature) {
/* 45 */       this.id = -1;
/* 46 */       this.fullSignature = fullSignature;
/*    */     } @Nullable
/*    */     private MessageSignature fullSignature;
/*    */     public Packed(int id) {
/* 50 */       this.id = id;
/* 51 */       this.fullSignature = null;
/*    */     }
/*    */     
/*    */     public int getId() {
/* 55 */       return this.id;
/*    */     }
/*    */     
/*    */     public void setId(int id) {
/* 59 */       this.id = id;
/*    */     }
/*    */     
/*    */     public Optional<MessageSignature> getFullSignature() {
/* 63 */       return Optional.ofNullable(this.fullSignature);
/*    */     }
/*    */     
/*    */     public void setFullSignature(@Nullable MessageSignature fullSignature) {
/* 67 */       this.fullSignature = fullSignature;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\MessageSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */