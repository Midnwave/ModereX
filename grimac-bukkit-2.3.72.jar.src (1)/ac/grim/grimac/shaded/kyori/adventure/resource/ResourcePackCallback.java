/*    */ package ac.grim.grimac.shaded.kyori.adventure.resource;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*    */ import java.util.UUID;
/*    */ import java.util.function.BiConsumer;
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
/*    */ @FunctionalInterface
/*    */ public interface ResourcePackCallback
/*    */ {
/*    */   @NotNull
/*    */   static ResourcePackCallback noOp() {
/* 47 */     return ResourcePackCallbacks.NO_OP;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static ResourcePackCallback onTerminal(@NotNull BiConsumer<UUID, Audience> success, @NotNull BiConsumer<UUID, Audience> failure) {
/* 59 */     return (uuid, status, audience) -> {
/*    */         if (status == ResourcePackStatus.SUCCESSFULLY_LOADED) {
/*    */           success.accept(uuid, audience);
/*    */         } else if (!status.intermediate()) {
/*    */           failure.accept(uuid, audience);
/*    */         } 
/*    */       };
/*    */   }
/*    */   
/*    */   void packEventReceived(@NotNull UUID paramUUID, @NotNull ResourcePackStatus paramResourcePackStatus, @NotNull Audience paramAudience);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\resource\ResourcePackCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */