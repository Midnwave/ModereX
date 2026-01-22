/*    */ package ac.grim.grimac.shaded.kyori.adventure.permission;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointer;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*    */ import java.util.Objects;
/*    */ import java.util.function.Predicate;
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
/*    */ public interface PermissionChecker
/*    */   extends Predicate<String>
/*    */ {
/* 46 */   public static final Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key("adventure", "permission"));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static PermissionChecker always(@NotNull TriState state) {
/* 56 */     Objects.requireNonNull(state);
/* 57 */     if (state == TriState.TRUE) return PermissionCheckers.TRUE; 
/* 58 */     if (state == TriState.FALSE) return PermissionCheckers.FALSE; 
/* 59 */     return PermissionCheckers.NOT_SET;
/*    */   }
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
/*    */   default boolean test(@NotNull String permission) {
/* 73 */     return (value(permission) == TriState.TRUE);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   TriState value(@NotNull String paramString);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\permission\PermissionChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */