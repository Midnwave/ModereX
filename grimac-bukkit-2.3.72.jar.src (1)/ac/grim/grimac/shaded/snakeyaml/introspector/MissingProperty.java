/*    */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.util.Collections;
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
/*    */ public class MissingProperty
/*    */   extends Property
/*    */ {
/*    */   public MissingProperty(String name) {
/* 27 */     super(name, Object.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?>[] getActualTypeArguments() {
/* 32 */     return new Class[0];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void set(Object object, Object value) throws Exception {}
/*    */ 
/*    */ 
/*    */   
/*    */   public Object get(Object object) {
/* 43 */     return object;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Annotation> getAnnotations() {
/* 48 */     return Collections.emptyList();
/*    */   }
/*    */ 
/*    */   
/*    */   public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
/* 53 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\MissingProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */