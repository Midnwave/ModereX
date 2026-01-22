/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.AnnotatedParameterizedType;
/*    */ import java.lang.reflect.AnnotatedType;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TypeToken<T>
/*    */ {
/*    */   private final AnnotatedType type;
/*    */   private volatile AnnotatedType canonical;
/*    */   
/*    */   protected TypeToken() {
/* 33 */     this.type = extractType();
/*    */   }
/*    */   
/*    */   private TypeToken(AnnotatedType type) {
/* 37 */     this.type = type;
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
/*    */   public static <T> TypeToken<T> get(Class<T> type) {
/* 49 */     return new TypeToken<T>(GenericTypeReflector.annotate(type))
/*    */       {
/*    */       
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TypeToken<?> get(Type type) {
/* 60 */     return new TypeToken(GenericTypeReflector.annotate(type)) {  }
/*    */       ;
/*    */   }
/*    */   public Type getType() {
/* 64 */     return this.type.getType();
/*    */   }
/*    */   
/*    */   public AnnotatedType getAnnotatedType() {
/* 68 */     return this.type;
/*    */   }
/*    */   
/*    */   public AnnotatedType getCanonicalType() {
/* 72 */     if (this.canonical == null) {
/* 73 */       this.canonical = GenericTypeReflector.toCanonical(this.type);
/*    */     }
/* 75 */     return this.canonical;
/*    */   }
/*    */   
/*    */   private AnnotatedType extractType() {
/* 79 */     AnnotatedType t = getClass().getAnnotatedSuperclass();
/* 80 */     if (!(t instanceof AnnotatedParameterizedType)) {
/* 81 */       throw new RuntimeException("Invalid TypeToken; must specify type parameters");
/*    */     }
/* 83 */     AnnotatedParameterizedType pt = (AnnotatedParameterizedType)t;
/* 84 */     if (((ParameterizedType)pt.getType()).getRawType() != TypeToken.class) {
/* 85 */       throw new RuntimeException("Invalid TypeToken; must directly extend TypeToken");
/*    */     }
/* 87 */     return pt.getAnnotatedActualTypeArguments()[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 92 */     if (this == obj) return true; 
/* 93 */     return (obj instanceof TypeToken && getCanonicalType().equals(((TypeToken)obj).type));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     return getType().hashCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\TypeToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */