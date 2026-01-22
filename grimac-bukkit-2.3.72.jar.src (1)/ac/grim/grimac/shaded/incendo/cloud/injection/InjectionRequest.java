/*    */ package ac.grim.grimac.shaded.incendo.cloud.injection;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Derived;
/*    */ import org.immutables.value.Value.Immutable;
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
/*    */ @Immutable
/*    */ public interface InjectionRequest<C>
/*    */ {
/*    */   static <C> InjectionRequest<C> of(CommandContext<C> context, TypeToken<?> injectedType, AnnotationAccessor annotationAccessor) {
/* 54 */     return InjectionRequestImpl.of(context, injectedType, annotationAccessor);
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
/*    */ 
/*    */   
/*    */   static <C> InjectionRequest<C> of(CommandContext<C> context, TypeToken<?> injectedType) {
/* 69 */     return InjectionRequestImpl.of(context, injectedType, AnnotationAccessor.empty());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   CommandContext<C> commandContext();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   TypeToken<?> injectedType();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Derived
/*    */   default Class<?> injectedClass() {
/* 93 */     return GenericTypeReflector.erase(injectedType().getType());
/*    */   }
/*    */   
/*    */   AnnotationAccessor annotationAccessor();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\injection\InjectionRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */