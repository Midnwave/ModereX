/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
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
/*    */ public class TypeArgumentNotInBoundException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private final Type argument;
/*    */   private final TypeVariable<?> parameter;
/*    */   private final Type bound;
/*    */   
/*    */   TypeArgumentNotInBoundException(Type argument, TypeVariable<?> parameter, Type bound) {
/* 23 */     super("Given argument [" + argument + "] for type parameter [" + parameter
/* 24 */         .getName() + "] is not within the bound [" + bound + "]");
/* 25 */     this.argument = argument;
/* 26 */     this.parameter = parameter;
/* 27 */     this.bound = bound;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Type getArgument() {
/* 34 */     return this.argument;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TypeVariable<?> getParameter() {
/* 41 */     return this.parameter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Type getBound() {
/* 49 */     return this.bound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\TypeArgumentNotInBoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */