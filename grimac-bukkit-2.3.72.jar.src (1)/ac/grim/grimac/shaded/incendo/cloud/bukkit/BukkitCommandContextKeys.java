/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import java.util.concurrent.Executor;
/*    */ import org.apiguardian.api.API;
/*    */ import org.bukkit.command.CommandSender;
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
/*    */ public final class BukkitCommandContextKeys
/*    */ {
/* 45 */   public static final CloudKey<CommandSender> BUKKIT_COMMAND_SENDER = CloudKey.of("BukkitCommandSender", 
/*    */       
/* 47 */       TypeToken.get(CommandSender.class));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/* 56 */   public static final CloudKey<Executor> SENDER_SCHEDULER_EXECUTOR = CloudKey.of("SenderSchedulerExecutor", Executor.class);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitCommandContextKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */