/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.function.Function;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ public final class BukkitBackwardsBrigadierSenderMapper<C, S>
/*    */   implements Function<C, S>
/*    */ {
/* 40 */   private static final Class<?> VANILLA_COMMAND_WRAPPER_CLASS = CraftBukkitReflection.needOBCClass("command.VanillaCommandWrapper");
/*    */   
/* 42 */   private static final Method GET_LISTENER_METHOD = CraftBukkitReflection.needMethod(VANILLA_COMMAND_WRAPPER_CLASS, "getListener", new Class[] { CommandSender.class });
/*    */   
/*    */   private final SenderMapper<?, C> senderMapper;
/*    */   
/*    */   public BukkitBackwardsBrigadierSenderMapper(SenderMapper<?, C> senderMapper) {
/* 47 */     this.senderMapper = senderMapper;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public S apply(C cloud) {
/*    */     try {
/* 54 */       return (S)GET_LISTENER_METHOD.invoke(null, new Object[] { this.senderMapper.reverse(cloud) });
/* 55 */     } catch (ReflectiveOperationException e) {
/* 56 */       throw new RuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\BukkitBackwardsBrigadierSenderMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */