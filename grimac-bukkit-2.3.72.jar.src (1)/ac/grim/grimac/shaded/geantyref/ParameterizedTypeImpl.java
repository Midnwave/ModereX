/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Arrays;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ParameterizedTypeImpl
/*    */   implements ParameterizedType
/*    */ {
/*    */   private final Class<?> rawType;
/*    */   private final Type[] actualTypeArguments;
/*    */   private final Type ownerType;
/*    */   
/*    */   ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
/* 19 */     this.rawType = rawType;
/* 20 */     this.actualTypeArguments = actualTypeArguments;
/* 21 */     this.ownerType = ownerType;
/*    */   }
/*    */   
/*    */   public Type getRawType() {
/* 25 */     return this.rawType;
/*    */   }
/*    */   
/*    */   public Type[] getActualTypeArguments() {
/* 29 */     return this.actualTypeArguments;
/*    */   }
/*    */   
/*    */   public Type getOwnerType() {
/* 33 */     return this.ownerType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 38 */     if (!(other instanceof ParameterizedType)) return false;
/*    */     
/* 40 */     ParameterizedType that = (ParameterizedType)other;
/* 41 */     return (this == that || (
/* 42 */       Objects.equals(this.ownerType, that.getOwnerType()) && 
/* 43 */       Objects.equals(this.rawType, that.getRawType()) && 
/* 44 */       Arrays.equals((Object[])this.actualTypeArguments, (Object[])that.getActualTypeArguments())));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     return Arrays.hashCode((Object[])this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     StringBuilder sb = new StringBuilder();
/*    */     
/* 56 */     String clazz = this.rawType.getName();
/*    */     
/* 58 */     if (this.ownerType != null) {
/* 59 */       sb.append(GenericTypeReflector.getTypeName(this.ownerType)).append('$');
/*    */ 
/*    */       
/* 62 */       String prefix = (this.ownerType instanceof ParameterizedType) ? (((Class)((ParameterizedType)this.ownerType).getRawType()).getName() + '$') : (((Class)this.ownerType).getName() + '$');
/* 63 */       if (clazz.startsWith(prefix))
/* 64 */         clazz = clazz.substring(prefix.length()); 
/*    */     } 
/* 66 */     sb.append(clazz);
/*    */     
/* 68 */     if (this.actualTypeArguments.length != 0) {
/* 69 */       sb.append('<');
/* 70 */       for (int i = 0; i < this.actualTypeArguments.length; i++) {
/* 71 */         Type arg = this.actualTypeArguments[i];
/* 72 */         if (i != 0)
/* 73 */           sb.append(", "); 
/* 74 */         sb.append(GenericTypeReflector.getTypeName(arg));
/*    */       } 
/* 76 */       sb.append('>');
/*    */     } 
/*    */     
/* 79 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */