/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*    */ import io.papermc.paper.plugin.configuration.PluginMeta;
/*    */ import java.util.Objects;
/*    */ import org.apiguardian.api.API;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
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
/*    */ 
/*    */ public interface PluginMetaHolder
/*    */   extends PluginHolder
/*    */ {
/*    */   default Plugin owningPlugin() {
/* 53 */     return Objects.<Plugin>requireNonNull(
/* 54 */         Bukkit.getPluginManager().getPlugin(owningPluginMeta().getName()), () -> owningPluginMeta().getName() + " Plugin instance");
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
/*    */   @API(status = API.Status.INTERNAL)
/*    */   static PluginMetaHolder fromPluginHolder(final PluginHolder pluginHolder) {
/* 67 */     return new PluginMetaHolder()
/*    */       {
/*    */         public PluginMeta owningPluginMeta() {
/* 70 */           return pluginHolder.owningPlugin().getPluginMeta();
/*    */         }
/*    */ 
/*    */         
/*    */         public Plugin owningPlugin() {
/* 75 */           return pluginHolder.owningPlugin();
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   PluginMeta owningPluginMeta();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\PluginMetaHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */