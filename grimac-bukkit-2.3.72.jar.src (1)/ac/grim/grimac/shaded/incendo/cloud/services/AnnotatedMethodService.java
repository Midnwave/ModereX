/*    */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.annotation.Order;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*    */ import java.lang.invoke.MethodHandle;
/*    */ import java.lang.invoke.MethodHandles;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Objects;
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
/*    */ class AnnotatedMethodService<Context, Result>
/*    */   implements Service<Context, Result>
/*    */ {
/*    */   private final ExecutionOrder executionOrder;
/*    */   private final MethodHandle methodHandle;
/*    */   private final Method method;
/*    */   private final Object instance;
/*    */   
/*    */   AnnotatedMethodService(Object instance, Method method) throws Exception {
/* 47 */     ExecutionOrder executionOrder = ExecutionOrder.SOON;
/*    */     try {
/* 49 */       Order order = method.<Order>getAnnotation(Order.class);
/* 50 */       if (order != null) {
/* 51 */         executionOrder = order.value();
/*    */       }
/* 53 */     } catch (Exception exception) {}
/*    */ 
/*    */     
/* 56 */     this.instance = instance;
/* 57 */     this.executionOrder = executionOrder;
/* 58 */     method.setAccessible(true);
/* 59 */     this.methodHandle = MethodHandles.lookup().unreflect(method);
/* 60 */     this.method = method;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result handle(Context context) {
/*    */     try {
/* 67 */       return (Result)this.methodHandle.invoke(this.instance, context);
/* 68 */     } catch (Throwable throwable) {
/* 69 */       (new IllegalStateException(
/* 70 */           String.format("Failed to call method service implementation '%s' in class '%s'", new Object[] {
/* 71 */               this.method.getName(), this.instance.getClass().getCanonicalName()
/*    */             
/* 73 */             }), throwable)).printStackTrace();
/*    */       
/* 75 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public ExecutionOrder order() {
/* 80 */     return this.executionOrder;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 85 */     if (this == o) {
/* 86 */       return true;
/*    */     }
/* 88 */     if (o == null || getClass() != o.getClass()) {
/* 89 */       return false;
/*    */     }
/* 91 */     AnnotatedMethodService<?, ?> that = (AnnotatedMethodService<?, ?>)o;
/* 92 */     return Objects.equals(this.methodHandle, that.methodHandle);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 97 */     return Objects.hash(new Object[] { this.methodHandle });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\AnnotatedMethodService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */