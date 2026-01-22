/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.lang.reflect.GenericArrayType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class GenericArrayTypeImpl
/*    */   implements GenericArrayType
/*    */ {
/*    */   private final Type componentType;
/*    */   
/*    */   GenericArrayTypeImpl(Type componentType) {
/* 18 */     this.componentType = componentType;
/*    */   }
/*    */ 
/*    */   
/*    */   static Class<?> createArrayType(Class<?> componentType) {
/* 23 */     return Array.newInstance(componentType, 0).getClass();
/*    */   }
/*    */   
/*    */   static Type createArrayType(Type componentType) {
/* 27 */     if (componentType instanceof Class) {
/* 28 */       return createArrayType((Class)componentType);
/*    */     }
/* 30 */     return new GenericArrayTypeImpl(componentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public Type getGenericComponentType() {
/* 35 */     return this.componentType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (other instanceof GenericArrayType && 
/* 41 */       Objects.equals(this.componentType, ((GenericArrayType)other).getGenericComponentType()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 46 */     return Objects.hashCode(this.componentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return this.componentType + "[]";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */