/*    */ package ac.grim.grimac.shaded.incendo.cloud.state;
/*    */ 
/*    */ import org.apiguardian.api.API;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface Stateful<S extends State>
/*    */ {
/*    */   S state();
/*    */   
/*    */   boolean transitionIfPossible(S paramS1, S paramS2);
/*    */   
/*    */   default void requireState(S expected) {
/* 62 */     if (state().equals(expected)) {
/*    */       return;
/*    */     }
/* 65 */     throw new IllegalStateException(String.format("This operation requires the command manager to be in state '%s', but it is in '%s'", new Object[] { expected, 
/*    */ 
/*    */             
/* 68 */             state() }));
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
/*    */   default void transitionOrThrow(S in, S out) {
/* 81 */     if (transitionIfPossible(in, out)) {
/*    */       return;
/*    */     }
/* 84 */     throw new IllegalStateException(String.format("The current state is '%s' but we expected a state of '%s' or '%s'", new Object[] {
/*    */             
/* 86 */             state(), in, out
/*    */           }));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\state\Stateful.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */