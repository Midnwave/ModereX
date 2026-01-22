/*    */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*    */ import java.util.function.Predicate;
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
/*    */ enum ServiceFilterHandler
/*    */ {
/* 32 */   INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   <Context> boolean passes(ServiceRepository<Context, ?>.ServiceWrapper<? extends Service<Context, ?>> service, Context context) {
/* 38 */     if (!service.isDefaultImplementation()) {
/* 39 */       for (Predicate<Context> predicate : service.filters()) {
/*    */         try {
/* 41 */           if (!predicate.test(context)) {
/* 42 */             return false;
/*    */           }
/* 44 */         } catch (Exception e) {
/* 45 */           throw new PipelineException(
/* 46 */               String.format("Failed to evaluate filter '%s' for '%s'", new Object[] {
/* 47 */                   TypeToken.get(predicate.getClass()).getType().getTypeName(), service
/*    */                 }), e);
/*    */         } 
/*    */       } 
/*    */     }
/* 52 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServiceFilterHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */