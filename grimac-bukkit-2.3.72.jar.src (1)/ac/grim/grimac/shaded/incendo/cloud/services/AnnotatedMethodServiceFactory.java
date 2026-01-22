/*    */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.annotation.ServiceImplementation;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ enum AnnotatedMethodServiceFactory
/*    */ {
/* 35 */   INSTANCE;
/*    */ 
/*    */ 
/*    */   
/*    */   Map<? extends Service<?, ?>, TypeToken<? extends Service<?, ?>>> lookupServices(Object instance) throws Exception {
/* 40 */     Map<Service<?, ?>, TypeToken<? extends Service<?, ?>>> map = new HashMap<>();
/* 41 */     Class<?> clazz = instance.getClass();
/* 42 */     for (Method method : clazz.getDeclaredMethods()) {
/*    */       
/* 44 */       ServiceImplementation serviceImplementation = method.<ServiceImplementation>getAnnotation(ServiceImplementation.class);
/* 45 */       if (serviceImplementation != null) {
/*    */ 
/*    */         
/* 48 */         if (method.getParameterCount() != 1) {
/* 49 */           throw new IllegalArgumentException(String.format("Method '%s' in class '%s' has wrong parameter count. Expected 1, got %d", new Object[] { method
/*    */                   
/* 51 */                   .getName(), instance.getClass().getCanonicalName(), 
/* 52 */                   Integer.valueOf(method.getParameterCount()) }));
/*    */         }
/*    */         
/* 55 */         map.put(new AnnotatedMethodService<>(instance, method), 
/*    */             
/* 57 */             TypeToken.get(serviceImplementation.value()));
/*    */       } 
/*    */     } 
/* 60 */     return map;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\AnnotatedMethodServiceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */