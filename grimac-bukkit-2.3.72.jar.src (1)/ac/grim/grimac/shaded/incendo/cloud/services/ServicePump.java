/*    */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
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
/*    */ public final class ServicePump<Context>
/*    */ {
/*    */   private final ServicePipeline servicePipeline;
/*    */   private final Context context;
/*    */   
/*    */   ServicePump(ServicePipeline servicePipeline, Context context) {
/* 44 */     this.servicePipeline = servicePipeline;
/* 45 */     this.context = context;
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
/*    */   public <Result> ServiceSpigot<Context, Result> through(TypeToken<? extends Service<Context, Result>> type) {
/* 58 */     return new ServiceSpigot<>(this.servicePipeline, this.context, type);
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
/*    */   public <Result> ServiceSpigot<Context, Result> through(Class<? extends Service<Context, Result>> clazz) {
/* 71 */     return through(TypeToken.get(clazz));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServicePump.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */