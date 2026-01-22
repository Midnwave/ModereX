/*    */ package ac.grim.grimac.shaded.kyori.adventure.key;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ public final class InvalidKeyException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -5413304087321449434L;
/*    */   private final String keyNamespace;
/*    */   private final String keyValue;
/*    */   
/*    */   InvalidKeyException(@NotNull String keyNamespace, @NotNull String keyValue, @Nullable String message) {
/* 40 */     super(message);
/* 41 */     this.keyNamespace = keyNamespace;
/* 42 */     this.keyValue = keyValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public final String keyNamespace() {
/* 52 */     return this.keyNamespace;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public final String keyValue() {
/* 62 */     return this.keyValue;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\key\InvalidKeyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */