/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ import java.lang.reflect.WildcardType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
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
/*    */ class CaptureTypeImpl
/*    */   implements CaptureType
/*    */ {
/*    */   private final WildcardType wildcard;
/*    */   private final TypeVariable<?> variable;
/*    */   private final Type[] lowerBounds;
/*    */   private Type[] upperBounds;
/*    */   
/*    */   CaptureTypeImpl(WildcardType wildcard, TypeVariable<?> variable) {
/* 29 */     this.wildcard = wildcard;
/* 30 */     this.variable = variable;
/* 31 */     this.lowerBounds = wildcard.getLowerBounds();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void init(VarMap varMap) {
/* 39 */     ArrayList<Type> upperBoundsList = new ArrayList<>(Arrays.asList(varMap.map(this.variable.getBounds())));
/*    */     
/* 41 */     List<Type> wildcardUpperBounds = Arrays.asList(this.wildcard.getUpperBounds());
/* 42 */     if (wildcardUpperBounds.size() > 0 && wildcardUpperBounds.get(0) == Object.class) {
/*    */       
/* 44 */       upperBoundsList.addAll(wildcardUpperBounds.subList(1, wildcardUpperBounds.size()));
/*    */     } else {
/* 46 */       upperBoundsList.addAll(wildcardUpperBounds);
/*    */     } 
/* 48 */     this.upperBounds = new Type[upperBoundsList.size()];
/* 49 */     upperBoundsList.toArray(this.upperBounds);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Type[] getLowerBounds() {
/* 56 */     return (Type[])this.lowerBounds.clone();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Type[] getUpperBounds() {
/* 63 */     assert this.upperBounds != null;
/* 64 */     return (Type[])this.upperBounds.clone();
/*    */   }
/*    */   
/*    */   public void setUpperBounds(Type[] upperBounds) {
/* 68 */     this.upperBounds = upperBounds;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeVariable<?> getTypeVariable() {
/* 73 */     return this.variable;
/*    */   }
/*    */ 
/*    */   
/*    */   public WildcardType getWildcardType() {
/* 78 */     return this.wildcard;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 83 */     return "capture of " + this.wildcard;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\CaptureTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */