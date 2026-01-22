/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ public interface TokenEmitter
/*    */ {
/*    */   @NotNull
/*    */   TokenEmitter tag(@NotNull String paramString);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter selfClosingTag(@NotNull String paramString);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter arguments(@NotNull String... args) {
/* 65 */     for (String arg : args) {
/* 66 */       argument(arg);
/*    */     }
/* 68 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter argument(@NotNull String paramString);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter argument(@NotNull String paramString, @NotNull QuotingOverride paramQuotingOverride);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter argument(@NotNull Component paramComponent);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter text(@NotNull String paramString);
/*    */   
/*    */   @NotNull
/*    */   TokenEmitter pop();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\serializer\TokenEmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */