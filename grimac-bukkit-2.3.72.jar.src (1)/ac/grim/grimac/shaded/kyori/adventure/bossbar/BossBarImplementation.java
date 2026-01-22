/*    */ package ac.grim.grimac.shaded.kyori.adventure.bossbar;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Collections;
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
/*    */ @Internal
/*    */ public interface BossBarImplementation
/*    */ {
/*    */   @Internal
/*    */   @NotNull
/*    */   static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
/* 48 */     return BossBarImpl.ImplementationAccessor.get(bar, type);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   @NotNull
/*    */   default Iterable<? extends BossBarViewer> viewers() {
/* 59 */     return Collections.emptyList();
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static interface Provider {
/*    */     @Internal
/*    */     @NotNull
/*    */     BossBarImplementation create(@NotNull BossBar param1BossBar);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\bossbar\BossBarImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */