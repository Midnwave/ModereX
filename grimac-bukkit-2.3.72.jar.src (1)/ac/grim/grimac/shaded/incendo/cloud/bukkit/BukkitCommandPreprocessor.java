/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessor;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import java.util.concurrent.Executor;
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
/*    */ final class BukkitCommandPreprocessor<C>
/*    */   implements CommandPreprocessor<C>
/*    */ {
/*    */   private final BukkitCommandManager<C> commandManager;
/*    */   private final BukkitBackwardsBrigadierSenderMapper<C, ?> mapper;
/*    */   
/*    */   BukkitCommandPreprocessor(BukkitCommandManager<C> commandManager) {
/* 52 */     this.commandManager = commandManager;
/*    */     
/* 54 */     if (this.commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
/* 55 */       this.mapper = new BukkitBackwardsBrigadierSenderMapper(this.commandManager.senderMapper());
/*    */     } else {
/* 57 */       this.mapper = null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(CommandPreprocessingContext<C> context) {
/* 63 */     if (this.mapper != null)
/*    */     {
/*    */       
/* 66 */       if (!context.commandContext().contains("_cloud_brigadier_native_sender")) {
/* 67 */         context.commandContext().store("_cloud_brigadier_native_sender", this.mapper
/*    */             
/* 69 */             .apply(context.commandContext().sender()));
/*    */       }
/*    */     }
/*    */     
/* 73 */     context.commandContext().store(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER, this.commandManager
/*    */         
/* 75 */         .senderMapper().reverse(context.commandContext().sender()));
/*    */ 
/*    */ 
/*    */     
/* 79 */     context.commandContext().computeIfAbsent(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR, $ -> BukkitHelper.mainThreadExecutor(this.commandManager));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitCommandPreprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */