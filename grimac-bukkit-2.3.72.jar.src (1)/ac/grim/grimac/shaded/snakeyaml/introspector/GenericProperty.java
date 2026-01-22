/*    */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.lang.reflect.GenericArrayType;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
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
/*    */ public abstract class GenericProperty
/*    */   extends Property
/*    */ {
/*    */   private final Type genType;
/*    */   private boolean actualClassesChecked;
/*    */   private Class<?>[] actualClasses;
/*    */   
/*    */   public GenericProperty(String name, Class<?> aClass, Type aType) {
/* 29 */     super(name, aClass);
/* 30 */     this.genType = aType;
/* 31 */     this.actualClassesChecked = (aType == null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?>[] getActualTypeArguments() {
/* 38 */     if (!this.actualClassesChecked) {
/* 39 */       if (this.genType instanceof ParameterizedType) {
/* 40 */         ParameterizedType parameterizedType = (ParameterizedType)this.genType;
/* 41 */         Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
/* 42 */         if (actualTypeArguments.length > 0) {
/* 43 */           this.actualClasses = new Class[actualTypeArguments.length];
/* 44 */           for (int i = 0; i < actualTypeArguments.length; i++) {
/* 45 */             if (actualTypeArguments[i] instanceof Class) {
/* 46 */               this.actualClasses[i] = (Class)actualTypeArguments[i];
/* 47 */             } else if (actualTypeArguments[i] instanceof ParameterizedType) {
/* 48 */               this.actualClasses[i] = (Class)((ParameterizedType)actualTypeArguments[i])
/* 49 */                 .getRawType();
/* 50 */             } else if (actualTypeArguments[i] instanceof GenericArrayType) {
/*    */               
/* 52 */               Type componentType = ((GenericArrayType)actualTypeArguments[i]).getGenericComponentType();
/* 53 */               if (componentType instanceof Class) {
/* 54 */                 this.actualClasses[i] = Array.newInstance((Class)componentType, 0).getClass();
/*    */               } else {
/* 56 */                 this.actualClasses = null;
/*    */                 break;
/*    */               } 
/*    */             } else {
/* 60 */               this.actualClasses = null;
/*    */               break;
/*    */             } 
/*    */           } 
/*    */         } 
/* 65 */       } else if (this.genType instanceof GenericArrayType) {
/* 66 */         Type componentType = ((GenericArrayType)this.genType).getGenericComponentType();
/* 67 */         if (componentType instanceof Class) {
/* 68 */           this.actualClasses = new Class[] { (Class)componentType };
/*    */         }
/* 70 */       } else if (this.genType instanceof Class) {
/*    */         
/* 72 */         Class<?> classType = (Class)this.genType;
/* 73 */         if (classType.isArray()) {
/* 74 */           this.actualClasses = new Class[1];
/* 75 */           this.actualClasses[0] = getType().getComponentType();
/*    */         } 
/*    */       } 
/* 78 */       this.actualClassesChecked = true;
/*    */     } 
/* 80 */     return this.actualClasses;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\GenericProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */