/*    */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*    */ import ac.grim.grimac.shaded.snakeyaml.util.ArrayUtils;
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.Field;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldProperty
/*    */   extends GenericProperty
/*    */ {
/*    */   private final Field field;
/*    */   
/*    */   public FieldProperty(Field field) {
/* 34 */     super(field.getName(), field.getType(), field.getGenericType());
/* 35 */     this.field = field;
/* 36 */     field.setAccessible(true);
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(Object object, Object value) throws Exception {
/* 41 */     this.field.set(object, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(Object object) {
/*    */     try {
/* 47 */       return this.field.get(object);
/* 48 */     } catch (Exception e) {
/* 49 */       throw new YAMLException("Unable to access field " + this.field
/* 50 */           .getName() + " on object " + object + " : " + e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Annotation> getAnnotations() {
/* 56 */     return ArrayUtils.toUnmodifiableList((Object[])this.field.getAnnotations());
/*    */   }
/*    */ 
/*    */   
/*    */   public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
/* 61 */     return this.field.getAnnotation(annotationType);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\FieldProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */