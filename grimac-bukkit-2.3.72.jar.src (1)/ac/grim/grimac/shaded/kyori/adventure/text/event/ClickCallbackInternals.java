/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.permission.PermissionChecker;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*    */ import java.util.function.Supplier;
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
/*    */ final class ClickCallbackInternals
/*    */ {
/* 36 */   static final PermissionChecker ALWAYS_FALSE = PermissionChecker.always(TriState.FALSE);
/*    */   
/* 38 */   static final ClickCallback.Provider PROVIDER = Services.service(ClickCallback.Provider.class)
/* 39 */     .orElseGet(Fallback::new);
/*    */   
/*    */   static final class Fallback implements ClickCallback.Provider {
/*    */     @NotNull
/*    */     public ClickEvent create(@NotNull ClickCallback<Audience> callback, ClickCallback.Options options) {
/* 44 */       return ClickEvent.suggestCommand("Callbacks are not supported on this platform!");
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\ClickCallbackInternals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */