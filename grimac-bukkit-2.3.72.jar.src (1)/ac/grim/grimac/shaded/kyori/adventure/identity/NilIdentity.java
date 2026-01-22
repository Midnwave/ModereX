/*    */ package ac.grim.grimac.shaded.kyori.adventure.identity;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.UUID;
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
/*    */ final class NilIdentity
/*    */   implements Identity
/*    */ {
/* 31 */   static final UUID NIL_UUID = new UUID(0L, 0L);
/* 32 */   static final Identity INSTANCE = new NilIdentity();
/*    */   
/*    */   @NotNull
/*    */   public UUID uuid() {
/* 36 */     return NIL_UUID;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "Identity.nil()";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object that) {
/* 46 */     return (this == that);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 51 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\identity\NilIdentity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */