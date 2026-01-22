/*    */ package ac.grim.grimac.shaded.incendo.cloud.util.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ final class MultiDelegateAnnotationAccessor
/*    */   implements AnnotationAccessor
/*    */ {
/*    */   private final AnnotationAccessor[] accessors;
/*    */   
/*    */   MultiDelegateAnnotationAccessor(AnnotationAccessor... accessors) {
/* 41 */     this.accessors = accessors;
/*    */   }
/*    */ 
/*    */   
/*    */   public <A extends Annotation> A annotation(Class<A> clazz) {
/* 46 */     A instance = null;
/* 47 */     for (AnnotationAccessor annotationAccessor : this.accessors) {
/* 48 */       instance = annotationAccessor.annotation(clazz);
/* 49 */       if (instance != null) {
/*    */         break;
/*    */       }
/*    */     } 
/* 53 */     return instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Annotation> annotations() {
/* 58 */     List<Annotation> annotationList = new LinkedList<>();
/* 59 */     for (AnnotationAccessor annotationAccessor : this.accessors) {
/* 60 */       annotationList.addAll(annotationAccessor.annotations());
/*    */     }
/* 62 */     return Collections.unmodifiableCollection(annotationList);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\annotation\MultiDelegateAnnotationAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */