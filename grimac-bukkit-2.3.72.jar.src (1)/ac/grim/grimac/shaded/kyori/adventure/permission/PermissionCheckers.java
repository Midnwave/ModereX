/*    */ package ac.grim.grimac.shaded.kyori.adventure.permission;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
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
/*    */ final class PermissionCheckers
/*    */ {
/* 31 */   static final PermissionChecker NOT_SET = new Always(TriState.NOT_SET);
/* 32 */   static final PermissionChecker FALSE = new Always(TriState.FALSE);
/* 33 */   static final PermissionChecker TRUE = new Always(TriState.TRUE);
/*    */ 
/*    */   
/*    */   private static final class Always
/*    */     implements PermissionChecker
/*    */   {
/*    */     private final TriState value;
/*    */     
/*    */     private Always(TriState value) {
/* 42 */       this.value = value;
/*    */     }
/*    */     
/*    */     @NotNull
/*    */     public TriState value(@NotNull String permission) {
/* 47 */       return this.value;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 52 */       return PermissionChecker.class.getSimpleName() + ".always(" + this.value + ")";
/*    */     }
/*    */ 
/*    */     
/*    */     public boolean equals(@Nullable Object other) {
/* 57 */       if (this == other) return true; 
/* 58 */       if (other == null || getClass() != other.getClass()) return false; 
/* 59 */       Always always = (Always)other;
/* 60 */       return (this.value == always.value);
/*    */     }
/*    */ 
/*    */     
/*    */     public int hashCode() {
/* 65 */       return this.value.hashCode();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\permission\PermissionCheckers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */