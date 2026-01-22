/*     */ package ac.grim.grimac.shaded.kyori.adventure.chat;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identified;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.time.Instant;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NonExtendable
/*     */ public interface SignedMessage
/*     */   extends Identified, Examinable
/*     */ {
/*     */   @Contract(value = "_ -> new", pure = true)
/*     */   @NotNull
/*     */   static Signature signature(byte[] signature) {
/*  58 */     return new SignedMessageImpl.SignatureImpl(signature);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(value = "_, _ -> new", pure = true)
/*     */   @NotNull
/*     */   static SignedMessage system(@NotNull String message, @Nullable ComponentLike unsignedContent) {
/*  72 */     return new SignedMessageImpl(message, ComponentLike.unbox(unsignedContent));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   Instant timestamp();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   long salt();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @Nullable
/*     */   Signature signature();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @Nullable
/*     */   Component unsignedContent();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   String message();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   default boolean isSystem() {
/* 134 */     return (identity() == Identity.nil());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   default boolean canDelete() {
/* 146 */     return (signature() != null);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 151 */     return Stream.of(new ExaminableProperty[] {
/* 152 */           ExaminableProperty.of("timestamp", timestamp()), 
/* 153 */           ExaminableProperty.of("salt", salt()), 
/* 154 */           ExaminableProperty.of("signature", signature()), 
/* 155 */           ExaminableProperty.of("unsignedContent", unsignedContent()), 
/* 156 */           ExaminableProperty.of("message", message())
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NonExtendable
/*     */   public static interface Signature
/*     */     extends Examinable
/*     */   {
/*     */     @Contract(pure = true)
/*     */     byte[] bytes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default Stream<? extends ExaminableProperty> examinableProperties() {
/* 181 */       return Stream.of(ExaminableProperty.of("bytes", bytes()));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\chat\SignedMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */