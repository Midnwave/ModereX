/*    */ package ac.grim.grimac.shaded.kyori.adventure.chat;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.security.SecureRandom;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SignedMessageImpl
/*    */   implements SignedMessage
/*    */ {
/* 35 */   static final SecureRandom RANDOM = new SecureRandom();
/*    */   
/*    */   private final Instant instant;
/*    */   private final long salt;
/*    */   private final String message;
/*    */   private final Component unsignedContent;
/*    */   
/*    */   SignedMessageImpl(String message, Component unsignedContent) {
/* 43 */     this.instant = Instant.now();
/* 44 */     this.salt = RANDOM.nextLong();
/* 45 */     this.message = message;
/* 46 */     this.unsignedContent = unsignedContent;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Instant timestamp() {
/* 51 */     return this.instant;
/*    */   }
/*    */ 
/*    */   
/*    */   public long salt() {
/* 56 */     return this.salt;
/*    */   }
/*    */ 
/*    */   
/*    */   public SignedMessage.Signature signature() {
/* 61 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Component unsignedContent() {
/* 66 */     return this.unsignedContent;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String message() {
/* 71 */     return this.message;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public Identity identity() {
/* 76 */     return Identity.nil();
/*    */   }
/*    */   
/*    */   static final class SignatureImpl
/*    */     implements SignedMessage.Signature {
/*    */     final byte[] signature;
/*    */     
/*    */     SignatureImpl(byte[] signature) {
/* 84 */       this.signature = signature;
/*    */     }
/*    */ 
/*    */     
/*    */     public byte[] bytes() {
/* 89 */       return this.signature;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\chat\SignedMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */