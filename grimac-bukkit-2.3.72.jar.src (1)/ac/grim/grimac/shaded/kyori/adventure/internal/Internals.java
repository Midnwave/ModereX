/*    */ package ac.grim.grimac.shaded.kyori.adventure.internal;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examiner;
/*    */ import ac.grim.grimac.shaded.kyori.examination.string.StringExaminer;
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
/*    */ public final class Internals
/*    */ {
/*    */   @NotNull
/*    */   public static String toString(@NotNull Examinable examinable) {
/* 47 */     return (String)examinable.examine((Examiner)StringExaminer.simpleEscaping());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\internal\Internals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */